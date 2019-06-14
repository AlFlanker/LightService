function allLampEvent(jdata) {

    if (jdata.object !== null && jdata.object !== undefined && jdata.object !== '') {
        for (let i = 0; i < jdata.object.length; i++) {
            // debugger;
            if (!groups.has(jdata.object[i].group)) {
                groups.add(jdata.object[i].group);
            }
            if (lamps.has(jdata.object[i].name)) {

                let val = lamps.get(jdata.object[i].name).marker;
                let cur_lamp = lamps.get(jdata.object[i].name);

                if (cur_lamp.objStates.length !== 0 && (jdata.object[0].objStates[0].data.brightness !== cur_lamp.objStates[0].data.brightness)) {
                    if (lampsState.has(cur_lamp.name)) {
                        let arr = lampsState.get(cur_lamp.name);
                        if (arr.length > 50) {
                            arr = arr.slice(40, 50);
                        }

                        arr.push(jdata.object[i].objStates[0]);
                    } else {
                        let states = [];
                        states.push(jdata.object[i].objStates[0]);
                        lampsState.set(cur_lamp.name, states);
                    }

                }
                lamps.set(jdata.object[i].name, new Lamp(
                    jdata.object[i].name, jdata.object[i].alias,
                    jdata.object[i].location, jdata.object[i].objStates,
                    jdata.object[i].cp_owner, jdata.object[i].lastUpdate,
                    jdata.object[i].group, val, jdata.object[i].workGroup, jdata.object[i].organization));

            } else {
                lamps.set(jdata.object[i].name, new Lamp(
                    jdata.object[i].name, jdata.object[i].alias,
                    jdata.object[i].location, jdata.object[i].objStates,
                    jdata.object[i].cp_owner, jdata.object[i].lastUpdate,
                    jdata.object[i].group, null, jdata.object[i].workGroup, jdata.object[i].organization
                ));
            }

        }

        fire_to_Update_Menu();
        let payload = [];
        jdata.object.forEach(function (item, i, arr) {
            let buf = [];
            if (item.objStates !== null) {
                buf = item.objStates.filter((obj) => {
                    return obj.passed === false;
                }).map((obj) => {
                    return obj.id;
                });
            }

        });

        fire_to_Update_Lamp();
        jdata = null;
    }

}

function allCPEvent(jdata) {
    for (let i = 0; i < jdata.object.length; i++) {
        if (control_points.has(jdata.object[i].objectName)) {
            let val = control_points.get(jdata.object[i].objectName);
            if (((val.latitude === jdata.object[i].latitude)
                && (val.longitude === jdata.object[i].longitude)) && (val.marker !== null)) {

            } else {
                control_points.set(jdata.object[i].objectName, new CP(jdata.object[i].objectName, jdata.object[i].latitude,
                    jdata.object[i].longitude, jdata.object[i].lastUpdate, null, jdata.object[i].id, jdata.object[i].workGroup, jdata.object[i].organization));
            }
        } else {
            control_points.set(jdata.object[i].objectName, new CP(jdata.object[i].objectName, jdata.object[i].latitude,
                jdata.object[i].longitude, jdata.object[i].lastUpdate, null, jdata.object[i].id, jdata.object[i].workGroup, jdata.object[i].organization));
        }

    }
    fire_to_Update_CP();

}

function popupRuntimeEvent() {
    $('#lamp_popup_alias').dblclick((e) => {
        e.stopPropagation();
        var currentEle = $(this);
        var value = $(this).html();
    });
}

function showLampInfo() {
    $('#main_info_popup_body').hide();
    $('#lamp_info_body').show();
}

function showMainInfo() {
    $('#main_info_popup_body').show();
    $('#lamp_info_body').hide();
}

/*ДОБАВИТЬ в таблицу БД состояний поле квитированно, если уже отправлялся объект!!!*/
function fire_to_Update_Lamp() {
    let marker;
    lamps.forEach((value, key) => {
        if (value.marker !== null) {
            if (!((value.marker.getLatLng().lat === value.location.latitude)
                && (value.marker.getLatLng().lng === value.location.longitude)))
                value.marker.setLatLng(new L.LatLng(value.location.latitude, value.location.longitude));

        } else {
            /*draggable: true*/
            marker = L.marker([value.location.latitude, value.location.longitude], {icon: icon, title: value.name});
            marker.addTo(objects);
            marker.on('click', marker_onClick);
            marker.on("mouseover", marker_mouseover);
            marker.on("mouseout", (e) => {
                if (polyline) {
                    polyline.removeFrom(map);
                    delete polyline;
                }
            });
            // marker.on("mouseout", marker_mouseout);
            value.marker = marker;
        }
        if (value.marker.isPopupOpen()) {
            if (value.objStates.length > 0) {
                $('#popup_bri').text(value.objStates[0].data.brightness + ' %');
                $('#popup_last_upd').text(value.lastUpdate);
                $('#popup_last_upd_prop').text(value.lastUpdate);
            }
            $('#popup_ac').text(value.objStates.length > 0 ? value.objStates[0].data.v_ac + ' В' : 'нет данных');
            $('#popup_current_ac').text(value.objStates.length > 0 ? value.objStates[0].data.i_ac + ' мА' : 'нет данных');
            $('#popup_dc').text(value.objStates.length > 0 ? value.objStates[0].data.vdcboard + ' мВ' : 'нет данных');
            $('#popup_temp').text(value.objStates.length > 0 ? value.objStates[0].data.temperature + ' °C' : 'нет данных');
        }
        if (lampsState.has(key)) {
            value.marker.setIcon(icon_ring);
        }
    });
}

function fire_to_Update_CP() {
    control_points.forEach((value, key) => {
        let marker;
        if (value.marker === null) {
            marker = L.marker([value.latitude, value.longitude], {title: value.objectName});
            marker.addTo(controlPointsLayer);
            marker.bindTooltip(value.objectName);
            marker.setIcon(icon_cp);
            marker.on('mouseover', function (event) {
                    let arr = [];
                    let curCP = null;
                    lines_cp2L = [];
                    control_points.forEach((value, key) => {
                        if (value.marker === this) {
                            curCP = value;
                        }
                    });

                    lamps.forEach((value, index) => {
                        if (value.cp_owner === curCP.id) {
                            arr.push(new L.LatLng(value.location.latitude, value.location.longitude));
                        }

                    });
                    arr.forEach((value, index) => {
                        lines_cp2L.push(new L.Polyline([[event.latlng.lat, event.latlng.lng], arr[index]], {color: 'red'}));
                    });
                    // console.log(arr);
                    lines_cp2L.forEach(cline => cline.addTo(map));
                }
            );

            marker.on('mouseout', (e) => {
                lines_cp2L.forEach(value => value.removeFrom(map));
                lines_cp2L = [];
            });
            value.marker = marker;
        }
    });
}

function fire_to_Update_Menu() {
    let select = $('#groups_id');
    let values = $("#groups_id>option").map(function () {
        return $(this).val();
    });
    let i = 0;
    if (values.length === 0) {
        groups.forEach((val) => {
            if (val.trim() !== '') {
                let opt = $('<option></option>', {
                    value: val,
                    label: val
                });
                select.append(opt);
            }
        });
    } else {
        groups.forEach((val) => {
            if (findInArray(values, val) < 0 && val.trim() !== '') {
                let opt = $('<option></option>', {
                    value: val,
                    label: val
                });
                select.append(opt);
            }
        });
    }

}

function acknowledge(cur_lamp) {

    if (lamps.has(cur_lamp)) {
        let lamp = lamps.get(cur_lamp);
        lamp.marker.setIcon(icon);
        lampsState.delete(cur_lamp);
    }
}

function acknowledgeGroup(cur_lamp) {
    if (selectedObj != null && selectedObj.size > 0) {
        selectedObj.forEach(value => {
            lampsState.delete(value.name);
        });
    } else {
        if (lamps.has(cur_lamp)) {
            let lamp = lamps.get(cur_lamp);
            lamps.forEach((value, key) => {
                if (value.group === lamp.group) {
                    value.marker.setIcon(icon);
                    lampsState.delete(value.name);
                }
            });
        }
    }
}





