function changeData(lamp_id) {
    //id ControlPoint!
    let cp_id = $('#cp_sel_id').val();

    let data = {
        eui: lamp_id,
        cp: cp_id
    };

    $.ajax({
        url: '/map/change_cp',
        type: "POST",
        contentType: "application/json",
        dataType: 'json',
        data: JSON.stringify(data),
        cache: false,
        timeout: 600000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {

            if (lamps.has(response.name)) {
                let lamp = lamps.get(response.name);

                let marker = lamp.marker;
                // заменяем данные у лампы
                lamps.set(response.name, new Lamp(
                    response.name, response.alias,
                    response.location, lamp.objStates,
                    response.cp_owner, response.lastUpdate,
                    response.group, marker));
            }
            $('#cp_sel_id').val(response.cp_owner);
            $('#cp_sel_id').addClass("is-valid");
            $('#cp_sel_id').change(() => {
                $('#cp_sel_id').removeClass("is-valid");
            });
        },
        error: function (response) {


        }
    });
}

function updateLamp(data) {

    $.ajax({
        url: '/map/update_lamp',
        type: "POST",
        contentType: "application/json",
        dataType: 'json',
        data: JSON.stringify(data),
        cache: false,
        timeout: 600000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            let marker;

            if (lamps.has(response.old_eui) && response.old_eui !== response.new_eui) {
                let lamp = lamps.get(response.old_eui);

                marker = lamp.marker;
                // заменяем данные у лампы
                lamp.location.latitude = response.data.location.latitude;
                lamp.location.longitude = response.data.location.longitude;
                lamps.set(response.new_eui, new Lamp(
                    response.new_eui, response.data.alias,
                    lamp.location, lamp.objStates,
                    response.data.cp_owner, response.data.lastUpdate,
                    response.data.group, marker));
                lamps.delete(response.old_eui);
            } else if (lamps.has(response.old_eui) && response.old_eui === response.new_eui) {
                let lamp = lamps.get(response.old_eui);
                lamp.alias = response.data.alias;
                lamp.location.latitude = response.data.location.latitude;
                lamp.location.longitude = response.data.location.longitude;
                lamp.group = response.data.group;
                lamp.lastUpdate = response.data.lastUpdate;
            }
            let lamp = lamps.get(response.new_eui);
            if (lamp.objStates.length > 0) {
                $('#popup_bri').text(lamp.objStates[0].data.brightness);
                $('#popup_last_upd').text(getCurrentDate(lamp.objStates[0].currentDate));
                $('#popup_last_upd_prop').text(getCurrentDate(lamp.objStates[0].currentDate));

            }
            $('#popun_alias').text(lamp.alias);
            $('#head_eui_id').text(lamp.name);
            $('#popup_ac').text(lamp.objStates.length > 0 ? lamp.objStates[0].data.v_ac : 'нет данных');
            $('#popup_current_ac').text(lamp.objStates.length > 0 ? lamp.objStates[0].data.i_ac + ' мА' : 'нет данных');
            $('#popup_dc').text(lamp.objStates.length > 0 ? lamp.objStates[0].data.vdcboard + ' мВ' : 'нет данных');
            $('#popup_temp').text(lamp.objStates.length > 0 ? lamp.objStates[0].data.temperature + ' °C' : 'нет данных');

            fire_to_Update_Lamp();


        },
        error: function (response) {


        }
    });
}

// $('#reg_new_lamp_id').on('click',()=>registrateLamp());
function registrateLamp() {
    let popup_eui = $('#new_reg_eui_id');
    let popup_alias = $('#new_reg_alias_id');
    let wg_sel = $('#wg_sel_id');
    let org_sel = $('#orgs_sel_id');
    let local_group_sel = $('#reg_group_sel_id');
    let eui_regex = /^(([0-9a-fA-F]{2})-){7}([0-9a-fA-F]{2}$)/;
    let eui = popup_eui.val();
    let alias = popup_alias.val();
    let organization = null;
    let workGroup = null;
    let group = null;
    let cp;

    if (user_role === "[ADMIN]") {
        organization = org_sel.val();
        workGroup = wg_sel.val();
        group = local_group_sel.val();
        if (organization == null || organization === undefined || organization === '') {
            org_sel.addClass("is-invalid");
            return;
        } else {
            if (org_sel.hasClass("is-invalid")) {
                org_sel.removeClass("is-invalid");
            }
        }
        if (workGroup == null || workGroup === undefined || workGroup === '') {
            wg_sel.addClass("is-invalid");
            return;
        } else {
            if (wg_sel.hasClass("is-invalid")) {
                wg_sel.removeClass("is-invalid");
            }
        }
    } else if (user_role === "[SuperUserOwner]") {
        workGroup = wg_sel.val();
        group = local_group_sel.val();
        if (workGroup == null || workGroup === undefined || workGroup === '') {
            wg_sel.addClass("is-invalid");
            return;
        } else {
            if (wg_sel.hasClass("is-invalid")) {
                wg_sel.removeClass("is-invalid");
            }
        }
    } else if (user_role === "[SuperUser]") {
        group = local_group_sel.val();
    }
    if (!eui_regex.test(eui)) {
        popup_eui.addClass("is-invalid");
        return;
    } else {
        if (popup_eui.hasClass("is-invalid")) {
            popup_eui.removeClass("is-invalid");
        }
    }
    let trim_alias = alias.trim();
    if (trim_alias.length >= 5 && trim_alias.length <= 20) {
        if (popup_alias.hasClass("is-invalid")) {
            popup_alias.removeClass("is-invalid");
        }
    } else {
        popup_alias.addClass("is-invalid");
        return;
    }

    if (control_points.has($('#cp_selector_id').val())) {
        let entry = control_points.get($('#cp_selector_id').val());
        cp = entry.id;
    } else {
        cp = null;
    }

    let data = {
        eui: eui,
        alias: alias,
        cp: cp,
        organization: organization,
        workGroup: workGroup,
        group: group,
        lat: lat,
        lon: lon
    };

    $.ajax({
        url: '/map/lamp',
        type: "POST",
        contentType: "application/json",
        dataType: 'json',
        data: JSON.stringify(data),
        cache: false,
        timeout: 600000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            if (lamps.has(response.name)) {
                let val = lamps.get(response.name).marker;
                let cur_lamp = lamps.get(response.name);
                lamps.set(response.name, new Lamp(
                    response.name, response.alias,
                    response.location, response.objStates,
                    response.cp_owner, response.lastUpdate,
                    response.group, val, response.workGroup, response.organization));
            } else {
                lamps.set(response.name, new Lamp(
                    response.name, response.alias,
                    response.location, response.objStates,
                    response.cp_owner, response.lastUpdate,
                    response.group, null, response.workGroup, response.organization
                ));
            }
            popup_eui.removeClass("is-invalid");
            addPopupDialog.removeFrom(map);
            fire_to_Update_Lamp();
            console.log(response);
        },
        error: function (response) {
            Object.getOwnPropertyNames(response.responseJSON).forEach((prop) => {
                switch (prop) {
                    case "eui":
                        popup_eui.addClass("is-invalid");
                        break;

                }
            });
            console.log(response);
        }
    });

}

function registrateControlPoint() {
    let popup_name = $('#new_reg_cp_id');
    let wg_sel = $('#wg_sel_id');
    let org_sel = $('#orgs_sel_id');
    let name = popup_name.val();
    let organization = null;
    let workGroup = null;
    let group = null;

    if (user_role === "[ADMIN]") {
        organization = org_sel.val();
        workGroup = wg_sel.val();

        if (organization == null || organization === undefined || organization === '') {
            org_sel.addClass("is-invalid");
            return;
        } else {
            if (org_sel.hasClass("is-invalid")) {
                org_sel.removeClass("is-invalid");
            }
        }
        if (workGroup == null || workGroup === undefined || workGroup === '') {
            wg_sel.addClass("is-invalid");
            return;
        } else {
            if (wg_sel.hasClass("is-invalid")) {
                wg_sel.removeClass("is-invalid");
            }
        }
    } else if (user_role === "[SuperUserOwner]") {
        workGroup = wg_sel.val();
        if (workGroup == null || workGroup === undefined || workGroup === '') {
            wg_sel.addClass("is-invalid");
            return;
        } else {
            if (wg_sel.hasClass("is-invalid")) {
                wg_sel.removeClass("is-invalid");
            }
        }
    }

    let trim_name = name.trim();
    if (trim_name.length >= 5 && trim_name.length <= 20) {
        if (popup_name.hasClass("is-invalid")) {
            popup_name.removeClass("is-invalid");
        }
    } else {
        popup_name.addClass("is-invalid");
        return;
    }
    let data = {
        name: name,
        organization: organization,
        workGroup: workGroup,
        lat: lat,
        lon: lon
    };

    $.ajax({
        url: '/map/controlpoint',
        type: "POST",
        contentType: "application/json",
        dataType: 'json',
        data: JSON.stringify(data),
        cache: false,
        timeout: 600000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            if (control_points.has(response.objectName)) {
                let val = control_points.get(response.objectName);
                if (((val.latitude === response.latitude)
                    && (val.longitude === response.longitude)) && (val.marker !== null)) {

                } else {
                    control_points.set(response.objectName, new CP(response.objectName, response.latitude,
                        response.longitude, response.lastUpdate, null, response.id, response.workGroup, response.organization));
                }
            } else {
                control_points.set(response.objectName, new CP(response.objectName, response.latitude,
                    response.longitude, response.lastUpdate, null, response.id, response.workGroup, response.organization));
            }
            fire_to_Update_CP();
            console.log(response);
        },
        error: function (response) {
            Object.getOwnPropertyNames(response.responseJSON).forEach((prop) => {
                switch (prop) {
                    case "cp":
                        popup_name.addClass("is-invalid");
                        break;

                }
            });
            console.log(response);
        }
    });

}

//Обновление данных лампы(изменения из Popup lamp)
function updateLampData() {
    let multiEdit = (selectedObj != null && selectedObj.size > 1);

    let popup_eui = $('#head_eui_id');
    let popup_alias = $('#popun_alias');
    let wg_sel = $('#wg_sel_id');
    let org_sel = $('#orgs_sel_id');
    let local_group_sel = $('#reg_group_sel_id');
    let cp_selector = $('#cp_selector_id');
    let eui = popup_eui.text();
    let alias = popup_alias.text();
    let organization = null;
    let workGroup = null;
    let group = null;
    let cp;

    if (user_role === "[ADMIN]") {
        organization = org_sel.val();
        workGroup = wg_sel.val();
        group = local_group_sel.val();
        if (organization == null || organization === undefined || organization === '') {
            org_sel.addClass("is-invalid");
            return;
        } else {
            if (org_sel.hasClass("is-invalid")) {
                org_sel.removeClass("is-invalid");
            }
        }
        if (workGroup == null || workGroup === undefined || workGroup === '') {
            wg_sel.addClass("is-invalid");
            return;
        } else {
            if (wg_sel.hasClass("is-invalid")) {
                wg_sel.removeClass("is-invalid");
            }
        }
    } else if (user_role === "[SuperUserOwner]") {
        workGroup = wg_sel.val();
        group = local_group_sel.val();
        if (workGroup == null || workGroup === undefined || workGroup === '') {
            wg_sel.addClass("is-invalid");
            return;
        } else {
            if (wg_sel.hasClass("is-invalid")) {
                wg_sel.removeClass("is-invalid");
            }
        }
    } else if (user_role === "[SuperUser]") {
        group = local_group_sel.val();
    }
    if (control_points.has(cp_selector.val())) {
        cp = control_points.get(cp_selector.val()).id;
    } else cp = null;


    data = {
        eui: eui,
        old_eui: old_lamp_eui === undefined ? '' : old_lamp_eui,
        alias: alias,
        cp: cp,
        organization: organization,
        workGroup: workGroup,
        group: group,
        lat: lat,
        lon: lon
    };

    let arr_data = [];
    if (!multiEdit) {
        arr_data.push(new UpdateLamp(eui,old_lamp_eui === undefined ? '' : old_lamp_eui,alias,cp,organization,workGroup,group,lat,lon));}
    else {
        selectedObj.forEach(value => {
            arr_data.push(new UpdateLamp(value.name,'',value.alias,cp,organization,workGroup,group,value.location.latitude,value.location.longitude));
        });
    }

     $.ajax({
         url: '/map/update/lamp',
         type: "POST",
         contentType: "application/json",
         dataType: 'json',
         data: JSON.stringify(arr_data),
         cache: false,
         timeout: 600000,
         beforeSend: function (xhr) {
             xhr.setRequestHeader(header, token);
         },
         success: function (responses) {

             responses.forEach(response=>{
                 console.log(response);
                 if (lamps.has(response.name)) {
                     let val = lamps.get(response.name).marker;
                     let cur_lamp = lamps.get(response.name);
                     lamps.set(response.name, new Lamp(
                         response.name, response.alias,
                         response.location, response.objStates,
                         response.cp_owner, response.lastUpdate,
                         response.group, val, response.workGroup, response.organization));
                 } else {
                     lamps.set(response.name, new Lamp(
                         response.name, response.alias,
                         response.location, response.objStates,
                         response.cp_owner, response.lastUpdate,
                         response.group, null, response.workGroup, response.organization
                     ));
                 }
             });

             fire_to_Update_Lamp();
             alert("Успешно сохранено!");

         },
         error: function (response) {
             Object.getOwnPropertyNames(response.responseJSON).forEach((prop) => {
                 switch (prop) {
                     case "eui":
                         alert('Лампа с таким eui уже зарегистрирована в системе!')
                         break;

                 }
             });
             console.log(response);
         }
     });
}

function deleteLamp() {
    let popup_eui = $('#head_eui_id');
    let eui = popup_eui.text();
    if (lamps.has(eui)) {
        let result = confirm("Действительно хотите удалить?");
        if (result) {
            $.ajax({
                url: '/map/delete/lamp/' + eui,
                type: 'POST',
                contentType: "application/json",
                dataType: 'json',
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(header, token);
                },
                success: function (data) {
                    console.log(data);

                    if (lamps.has(data.eui)) {
                        let m = lamps.get(data.eui);
                        lamps.delete(data.eui);
                        m.marker.closePopup();
                        m.marker.removeFrom(map);
                        fire_to_Update_Lamp();
                    }

                },
                error: function (data) {
                    console.log(data);

                }
            });
        }
    }

}


