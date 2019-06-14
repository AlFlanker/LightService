function lampAddEvent(jdata) {
    if (lamps.has(jdata.object.name)) {
        let val = lamps.get(jdata.object.name).marker;
        lamps.set(jdata.object.name, new Lamp(
            jdata.object.name, jdata.object.alias,
            jdata.object.location, jdata.object.objStates,
            jdata.object.cp_owner, jdata.object.lastUpdate,
            jdata.object.group, val));
    } else {
        lamps.set(jdata.object.name, new Lamp(
            jdata.object.name, jdata.object.alias,
            jdata.object.location, jdata.object.objStates,
            jdata.object.cp_owner, jdata.object.lastUpdate,
            jdata.object.group, null
        ));
    }
    fire_to_Update_Lamp();
}


function AddCPEvent(jdata) {

    if (control_points.has(jdata.object.objectName)) {
        let val = control_points.get(jdata.object.objectName);
        if (((val.latitude === jdata.object.latitude)
            && (val.longitude === jdata.object.longitude))
            && (val.marker !== null)) {
        } else {
            control_points.set(jdata.object.objectName, new CP(jdata.object.objectName, jdata.object.latitude,
                jdata.object.longitude, jdata.object.lastUpdate, null));
        }
    } else {
        control_points.set(jdata.object.objectName, new CP(jdata.object.objectName, jdata.object.latitude,
            jdata.object.longitude, jdata.object.lastUpdate, null));
    }
    fire_to_Update_CP();
}

function editLampEui() {
    let re = new RegExp("\^(([0-9a-fA-F]{2})-){7}([0-9a-fA-F]{2})\$");
    let eui_input =  $('#head_eui_id');
    let old_eui = eui_input.text();
    if(eui_input.hasClass("is-invalid")) eui_input.removeClass("is-invalid");
    eui_input.html('<input class="thVal" title="Введите новое название и нажмите enter" type="text" value="' + old_eui + '" />');
    $(".thVal").keyup(function (event) {
        if (event.keyCode == 13) {
            eui_input.html($(".thVal").val().trim());
            if (re.test(eui_input.text())) {
                //сохраняю старый eui
                old_lamp_eui =old_eui;
            } else {
                eui_input.text(old_eui);
                alert("Некорректный EUI" + '\n' + "Формат: XX-XX-XX-XX-XX-XX-XX-XX");
            }
        }
    });
}

function editLampAlias() {
    let alias = $('#popun_alias');
    let old_alias = alias.text();
    alias.html('<input class="thVal" title="Введите новое название и нажмите enter" type="text" value="' + old_alias + '" />');
    $(".thVal").keyup(function (event) {
        if (event.keyCode == 13) {
            alias.html($(".thVal").val().trim());
            if (alias.text().length >= 5 && alias.text().length <= 20) {
            } else {
                alias.text(old_alias);
                alert("Некорректный Alias"+ '\n' + "Длинна строки от 5 до 20 символов");
            }
        }
    });

}

function getNewlampPropierties(old_eui) {
    let lamp = lamps.get(old_eui);
    let eui = old_eui;
    let lat = lamp.marker.getLatLng().lat;
    let lon = lamp.marker.getLatLng().lng;
    let new_eui = $('#head_eui_id').length > 0 ? $('#head_eui_id').text() : old_eui;
    let alias = $('#popun_alias').length > 0 ? $('#popun_alias').text() : lamp.alias;
    let req = {
        "eui": eui,
        "new_eui": new_eui,
        "alias": alias,
        "lat": lat,
        "lon": lon
    };
    return req;
}

function addObjToGroup(obj, id) {
    let arr = [];
    arr.push(obj);
    let json = {
        "eui": arr,
        "groupsName": $('#group_sel_id').val()
    };

    let req = {
        "type": "add2Group",
        "obj_type": "add_lamp_to_group",
        "payload": json
    };

    sendLamp(req);
}

function addSelectedObjToGroup() {
    let arr = [];
    if(selectedObj) {
        selectedObj.forEach(elem => {
            arr.push(elem.name);
        });
    }else{
        alert("Нет выделенных объектов!");
        $('#add_2group_modal').modal('hide');
        return;
    }
    let json = {
        "eui": arr,
        "groupsName": mapOfGroups.get($('#groups_id').val())
    };

    let req = {
        "type": "add2Group",
        "obj_type": "add_lamp_to_group",
        "payload": json
    };
    $('#add_2group_modal').modal('hide');
    sendLamp(req);
}

function reDrawMark(eui, latlng) {
    let lamp = lamps.get(eui);
    lamp.marker.setLatLng(latlng);
    objects.addTo(map);
    controlPointsLayer.addTo(map);
    updateLamp(getNewlampPropierties(eui));

}

function setMarkerLatLng(eui) {

    let lamp = lamps.get(eui);
    let current_marker = lamp.marker;
    if (current_marker.isPopupOpen() && info_dialog_open) {
        current_marker.closePopup();
        setNewLtnLon = true;
        map.removeLayer(objects);
        map.removeLayer(controlPointsLayer);
        current_eui = eui;
        info_dialog_open = false;
    }

    // marker = L.marker([value.location.latitude, value.location.longitude], {icon: icon, title: value.name,draggable: true})

}

//Перерисовка полей в форме регистрации при щелчке на выбор организацтй!
function reloadWGSelector() {
    console.log("onchangeOrg");
    let current_id = $('#orgs_sel_id').val();
    let wg_selector = $("#wg_sel_id");
    let local_group_selector = $('#reg_group_sel_id');
    let current_wg;
    let current_org = undefined;
    organizations.forEach(value => {
        if (value.id == current_id) {
            current_org = value;
            return;
        }
    });
    if (current_org != null && current_org != undefined && current_org != '') {
        reloadWorkGroup(current_org);
        let current_wg_id = wg_selector.val();
        current_org.workGroups.forEach((value) => {
            if (value.id == current_wg_id) {
                current_wg = value;
                return;
            }
        });
        reloadLocalGroup(current_wg);
        reloadCP(current_wg_id);
    }
}

//Перерисовка полей формы регистрации при щелчке на рабочие группы
function reloadLocalGroupSelector() {
    let current_id;
    let current_wg;
    let current_org;
    let wg_selector = $("#wg_sel_id");
    if (user_role === "[SuperUserOwner]") {
        let it = organizations.values();
        current_org = it.next().value;
    }else {
        current_id = $('#orgs_sel_id').val();
        current_org = undefined;
        organizations.forEach(value => {
            if (value.id == current_id) {
                current_org = value;
                return;
            }
        });
    }

    if (current_org != null && current_org != undefined && current_org != '') {
        let current_wg_id = wg_selector.val();
        current_org.workGroups.forEach((value) => {
            if (value.id == current_wg_id) {
                current_wg = value;
                return;
            }
        });
        reloadLocalGroup(current_wg);
        reloadCP(current_wg_id);
    }
}

function reloadCP(current_wg_id) {
    let cp_sel = $("#cp_selector_id");
    cp_sel.find('option').remove();
    let elem = $('<option></option>', {
        value: null,
        label: 'Без Кп',
        text: 'Без Кп'
    });
    cp_sel.append(elem);

    control_points.forEach((value, key) => {
        if (value.workGroup.id == current_wg_id) {
            elem = $('<option></option>', {
                value: value.objectName,
                label: value.objectName,
                text: value.objectName
            });
            cp_sel.append(elem);
        }
    });
}

function reloadLocalGroup(current_wg) {
    let local_group_selector = $('#reg_group_sel_id');
    local_group_selector.find('option').remove(); //удаление старых данных
    let def = $('<option></option>', {
        value: 0,
        label: 'без группы',
        text: 'без группы'
    });
    if(current_wg!=null &&current_wg != undefined) {
        current_wg.groups.forEach((value) => {
            let elem = $('<option></option>', {
                value: value.id,
                label: value.name,
                text: value.name
            });
            local_group_selector.append(elem);
        });
        local_group_selector.append(def);
    }
}

function reloadWorkGroup(current_org) {
    let wg_selector = $("#wg_sel_id");
    let current_id = $('#orgs_sel_id').val();
    wg_selector.find('option').remove(); //удаление старых данных
    current_org.workGroups.forEach((value) => {
        let elem = $('<option></option>', {
            value: value.id,
            label: value.name,
            text: value.name

        });
        wg_selector.append(elem);
    });

}


