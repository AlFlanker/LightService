/*---------------Base Objects---------------*/
function Lamp(name, alias, location, objStates, cp_owner, lastUpdate, group, marker, workGroup, organization) {
    this.name = name;
    this.alias = alias;
    this.location = location;
    this.objStates = objStates;
    this.cp_owner = cp_owner;
    this.lastUpdate = lastUpdate;
    this.group = group;
    this.marker = marker;
    this.workGroup = workGroup;
    this.organization = organization;
}

function UpdateLamp(eui,old_eui,alias,cp,organization,workGroup,group,lat,lon){
    this.eui = eui;
    this.old_eui = old_eui;
    this.alias = alias;
    this.cp= cp;
    this.organization= organization;
    this.workGroup= workGroup;
    this.group=group;
    this.lat = lat;
    this.lon = lon;
}

function CP(objectName, latitude, longitude, lastUpdate, marker, id, workGroup, organization) {
    this.objectName = objectName;
    this.latitude = latitude;
    this.longitude = longitude;
    this.lastUpdate = lastUpdate;
    this.marker = marker;
    this.id = id;
    this.workGroup = workGroup;
    this.organization = organization;
}

function Passed(name, passedID) {
    this.name = name;
    this.passedID = passedID;
}

function Organization(id, name, workGroups) {
    this.id = id;
    this.name = name;
    this.workGroups = workGroups;
}

/*------------------------------------------*/
let CheckLamp, CheckCP,selectAll,selectSeq;


function EditInit() {
    map.on('mousedown', function (event) {
        if (rectangle != null) {
            rectangle.removeFrom(map);
            rectangle = null;
        }
        if ((event.originalEvent.button === 0) && (shift_key_pressed) && (!cntrlIsPressed)) {
            left_key_pressed = true;
            def_pos = event.latlng;
            rectangle = L.rectangle([[event.latlng.lat, event.latlng.lng], [event.latlng.lat, event.latlng.lng]], {
                color: "#717171",
                weight: 1
            });
            rectangle.addTo(map);
        }
    });

    map.on('mouseup', function (event) {
        if (rectangle != null && shift_key_pressed) {
            getSelectedMarkerByFrame(rectangle.getBounds());
            rectangle.removeFrom(map);
            rectangle.removeFrom(Testlayer);
            rectangle = null;
            left_key_pressed = false;
        }

        if ((event.originalEvent.button === 0)
            && shift_key_pressed
            && left_key_pressed
            && (!cntrlIsPressed)
            && (selectedObj == null)) {
            if (left_key_pressed) {
                left_key_pressed = false;
            }


        }


    });
    map.on('mousemove', function (event) {
        if (rectangle != null && left_key_pressed && shift_key_pressed) {
            rectangle.setBounds(L.latLngBounds(event.latlng, def_pos));

        }

    });
    map.on('contextmenu', function (event) {
    });
    map.on('click', function (event) {
        if (polyline) {
            polyline.removeFrom(map);
            delete polyline;
        }
        if (setNewLtnLon) {
            setNewLtnLon = false;
            reDrawMark(current_eui, event.latlng);
            current_eui = "";
        }


        if ((event.originalEvent.button === 0) && (!cntrlIsPressed
            && !shift_key_pressed
            && !CheckLamp.prop("checked")
            && !CheckCP.prop("checked"))) {
            if (selectedObj != null) {
                selectedObj.forEach(value => {
                    if (lampsState.has(value.name)) {
                        value.marker.setIcon(icon_ring);
                    }else value.marker.setIcon(icon);
                });
                selectAll.prop("checked",false);
                selectSeq.prop("checked",false);
                let btn = $("#edit_obj");
                btn.css('background-image', "url('/icon/menu_black.png')");
                selectedObj = null;

            }
            if (rectangle != null) {
                rectangle.removeFrom(map);
                rectangle = null;
            }
        } else if (event.originalEvent.button === 0) {
            if (rectangle != null) {
                rectangle.removeFrom(map);
                rectangle = null;
            }
        }

        if (isNumber(event.latlng.lat) &&
            isNumber(event.latlng.lng)) {
            lat = event.latlng.lat;
            lon = event.latlng.lng;
        }

        if (CheckLamp.prop("checked") && (!cntrlIsPressed) && (!shift_key_pressed)) {
            let elem = createRegistrationDialog();
            addPopupDialog = L.popup({maxWidth: "auto"}).setLatLng(event.latlng).setContent(elem).openOn(map);
            elem = $('<option></option>', {
                value: null,
                label: 'Без Кп'
            });
            let cp_sel = $("#cp_selector_id").append(elem);
            if (user_role !== '[SuperUser]') {
                //текущая выбранная рабочая группа
                let cur_wg = $("#wg_sel_id").val();
                control_points.forEach((value, key) => {
                    if (value.workGroup.id == cur_wg) {
                        elem = $('<option></option>', {
                            value: value.objectName,
                            label: value.objectName
                        });
                        cp_sel.append(elem);
                    }
                });
            } else {
                control_points.forEach((value, key) => {
                    elem = $('<option></option>', {
                        value: value.objectName,
                        label: value.objectName
                    });
                    cp_sel.append(elem);
                });
            }

            //ОТработка щелчка по списку организации
            $('#orgs_sel_id').on("change", (e) => reloadWGSelector());
            $("#wg_sel_id").on("change", (e) => reloadLocalGroupSelector());

        } else if (CheckCP.prop("checked") && (!cntrlIsPressed) && (!shift_key_pressed)) {
            // let elem = CreateCPDialog();
            let elem = createRegistrationControlPointDialog();
            addPopupDialog = L.popup().setLatLng(event.latlng).setContent(elem);
            addPopupDialog.openOn(map);
            //ОТработка щелчка по списку организации
            $('#orgs_sel_id').on("change", (e) => reloadWGSelector());
        }

    });
}


function getSelectedMarkerByFrame(frame) {
    if (selectedObj == null) {
        selectedObj = new Set();
    }
    lamps.forEach((value, key, map) => {
        let loc = L.latLng(value.location.latitude, value.location.longitude);
        if (frame.contains(loc)) {
            value.marker.setIcon(icon_Lamp_selected);
            selectedObj.add(value);
        }
    });
}

function InitMap() {
    Testlayer = L.layerGroup();
    testEdit = L.Editable.RectangleEditor;

    map = L.map('mapid', {
        dragging: true,
        boxZoom: false,
        zoomControl: false,
        editable: true,
        editOptions: {editLayer: Testlayer, rectangleEditorClass: testEdit},
        editInOSMControlOptions: {position: 'topleft'},
        SwitchMenuControlOptions: {position: 'topleft'},
        GroupMenuControlOptions: {position: 'topleft'},
        layers: [objects, controlPointsLayer]
    }).setView([45.071089, 38.997525], 18);


    mLayer = L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoiYWxleGZsYW5rZXIiLCJhIjoiY2puNGttMnlxMDA5bjNxczNhMjg3eDlocSJ9.PAZdQJ9c51fmNreVN_UKFg', {
        attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
        maxZoom: 16,
        id: 'mapbox.streets'
    });
    Testlayer.addTo(map);
    L.control.scale({updateWhenIdle: true, metric: true}).addTo(map);
    L.control.zoom({position: 'bottomleft'}).addTo(map);
    mLayer.addTo(map);


    let overlay_objects = {
        "Светильники": objects,
        "КП": controlPointsLayer
    };

    let controlLayers = L.control.layers(null, overlay_objects, {position: 'topleft', collapsed: true});
    controlLayers.addTo(map);

    CheckLamp = $("#CheckLamp");
    CheckCP = $("#CheckCP");
    selectAll= $('#cb1') ;
    selectSeq= $('#cb2') ;
    EditInit();
    let mlayer = {
        "map": mLayer
    };

    $("#header_id").click(() => {
        let cb_id = $("#card_body_id");
        let head = $("#header_id");
        let info_pane = $("#info-pane");
        if (cb_id.css("display") !== "none") {
            cb_id.prop("style", "display:none");
            head.text("+");
            head.css({"border-radius": "50%", "background": "dimgray"});
            info_pane.css({"border-radius": "50%"});
            // head.prop("style","background: dimgray");

        } else {
            cb_id.prop("style", "display:block");
            head.text("Меню:");
            head.prop("style", "background-color: dimgray");
            info_pane.css({"border-radius": ".25rem"});
            // info_pane.addClass("card form_ui_style");
        }
    })
}

function updateLampPopun(cur_lamp) {

    let card = $('<div></div>', {
        class: "card",
        id: 'main_info_popup_card'
    });
    card.attr("width", "100%");
    card.attr("display", "inline-block");
    let card_body = $('<div></div>', {
        class: "card-body",
        id: 'main_info_popup_body'
    });
    let card_body_info = drawLampProperties(cur_lamp);

    let table = $('<div></div>', {
        class: "container"
    });

    let breadcrumb = $('<nav></nav>');
    breadcrumb.attr('aria-label', "breadcrumb");
    breadcrumb.attr("style", 'background:#ffffffff')
    let ol = $('<ol></ol>', {
        class: "breadcrumb"
    });
    ol.attr("display", "flex");
    let li_1 = $('<li></li>', {
        class: "breadcrumb-item"
    });
    let a_1 = $('<a></a>', {
        href: '#',
        text: 'Основаная информация',
        onclick: 'showMainInfo()'
    });
    li_1.attr("display", "inline-block")
    li_1.append(a_1);
    let li_2 = $('<li></li>', {
        class: "breadcrumb-item"
    });
    li_2.append($('<a></a>', {
        href: '#',
        text: 'Измерения',
        onclick: 'showLampInfo()'
    }));
    li_2.attr("display", "block");
    ol.append(li_1).append(li_2);
    breadcrumb.append(ol);
    let table_row;
    let cell_1;
    let cell_2;
    //*********************************************
//*****************Организация**************
    if (user_role === "[ADMIN]") {
        table_row = $('<div></div>', {class: "row justify-content-between"});
        table_row.attr("display", "inline-block");
        cell_1 = $('<div></div>', {
            class: "col-lg-2 col-md-3",
            text: "организация: "
        });
        cell_2 = $('<div></div>', {
            class: "col-lg-8 col-md-7",
            text: cur_lamp.organization.name
        });


        table_row.append(cell_1).append(cell_2);
        table.append(table_row);
        table.append($('<div></div>', {
            class: 'dropdown-divider'
        }));
    }
//*********************************************
//*****************Рабочая группа**************
    if (user_role === "[SuperUserOwner]" || user_role === "[ADMIN]") {
        table_row = $('<div></div>', {class: "row justify-content-between"});
        table_row.attr("display", "inline-block");
        cell_1 = $('<div></div>', {
            class: "col-lg-2 col-md-3",
            text: "Рабочая группа: "
        });
        cell_2 = $('<div></div>', {
            class: "col-lg-8 col-md-7",
            text: cur_lamp.workGroup.name
        });


        table_row.append(cell_1).append(cell_2);
        table.append(table_row);
        table.append($('<div></div>', {
            class: 'dropdown-divider'
        }));
    }
//*********************************************
//********************EUI********************[SuperUserOwner]
    table_row = $('<div></div>', {class: "row justify-content-between"});
    table_row.attr("display", "inline-block");
    cell_1 = $('<div></div>', {
        class: "col-lg-2 col-md-3",
        text: "EUI: "
    });
    cell_2 = $('<div></div>', {
        class: "col-lg-8 col-md-7",
    });

    inp = $('<div></div>', {

        id: "head_eui_id",
        text: cur_lamp.name
    });
    inp.attr("align", "center");
    let invCheck = $('<label></label>', {
        class: "invalid-feedback",
        id: "check_eui",
        text: 'некорретный eui'
    });
    cell_2.append(inp).append(invCheck);


    let cell_3 = $('<div></div>', {
        class: "col-lg-2 col-md-2",
    });
    let edit_btn = $('<input/>', {
        type: 'image',
        src: 'map_api/icon/edit.png',
        onclick: 'editLampEui()',
        title: "Изменить EUI"
    });
    cell_3.append(edit_btn);

    table_row.append(cell_1).append(cell_2).append(invCheck).append(cell_3);
    table.append(table_row);

    table.append($('<div></div>', {
        class: 'dropdown-divider'
    }));
//*********************************************
//**********Alias*********
    table_row = $('<div></div>', {class: "row justify-content-start"});
    cell_1 = $('<div></div>', {
        class: "col-lg-2 col-md-3",
        text: "Alias: "
    });


    cell_2 = $('<div></div>', {
        class: "col-lg-8 col-md-7",
        id: "popun_alias",
        text: cur_lamp.alias
    });
    cell_2.attr("align", "center");

    cell_3 = $('<div></div>', {
        class: "col-lg-2 col-md-2",
    });
    edit_btn = $('<input/>', {
        type: 'image',
        src: 'map_api/icon/edit.png',
        onclick: 'editLampAlias()',
        title: "Изменить Alias"
    });
    cell_3.append(edit_btn);

    table_row.append(cell_1).append(cell_2).append(cell_3);
    table.append(table_row);
    table.append($('<div></div>', {
        class: 'dropdown-divider'
    }));

//*********************************************
//**********дата последнего обновления*********
    table_row = $('<div></div>', {class: "row justify-content-start"});
    cell_1 = $('<div></div>', {
        class: "col-lg-4 col-md-4",
        text: "дата обновления: "
    });
    let last_update;
    if (cur_lamp.objStates != null) {
        if (cur_lamp.objStates.length > 0) {
            last_update = cur_lamp.objStates[0].currentDate;
        }
    }


    cell_2 = $('<div></div>', {
        id: "popup_last_upd",
        class: "col-lg-8 col-md-8",
        text: (last_update === undefined) ? "обновлений не было" : last_update
    });
    cell_2.attr("align", "left");
    table_row.append(cell_1).append(cell_2);
    table.append(table_row);
    table.append($('<div></div>', {
        class: 'dropdown-divider'
    }));
//*********************************************
//****************Яркость**********************
    table_row = $('<div></div>', {class: "row justify-content-between"});
    cell_1 = $('<div></div>', {
        class: "col-lg-3 col-md-4",
        text: "яркость: "
    });
    let br;
    if (cur_lamp.objStates != null) {
        if (cur_lamp.objStates.length > 0) {
            br = cur_lamp.objStates[0].data.brightness;
        }
    }
    cell_2 = $('<div></div>', {
        id: "popup_bri",
        class: "col-lg-6 col-md-5 ",
        text: br === undefined ? "нет данных" : br + " %"
    });
    cell_2.attr("align", "center");
    let button = $('<input/>', {
        type: 'image',
        src: 'map_api/icon/brightness.png',
        id: 'expandMenu',
        title: "Изменить яркость лампы"

    });
    button.attr('data-toggle', 'dropdown');
    button.attr('aria-haspopup', 'true');
    button.attr('aria-expanded', 'false');
    let menu = $('<div></div>', {
        class: 'dropdown-menu'
    });

    let elem = $('<a></a>', {
        class: "dropdown-item",
        href: '#',
        onclick: 'sendCommand(0)',
        text: 'Off'
    });
    menu.append(elem);
    menu.append($('<div></div>', {
        class: 'dropdown-divider'
    }));

    for (let i = 1; i <= 10; i++) {
        elem = $('<a></a>', {
            class: "dropdown-item",
            href: '#',
            onclick: 'sendCommand(' + i * 10 + ')',
            text: i * 10 + ' %'
        });
        menu.append(elem);
    }

    cell_3 = $('<div></div>', {
        class: "col-lg-2 col-md-3",
    });
    cell_3.append(menu);
    cell_3.append(button);

    table_row.append(cell_1).append(cell_2).append(cell_3);
    table.append(table_row);
    table.append($('<div></div>', {
        class: 'dropdown-divider'
    }));

    //*********************************************
//********************группа*******************
    table_row = $('<div></div>', {class: "row justify-content-between"});
    cell_1 = $('<div></div>', {
        class: "col-lg-3 col-md-3",
        text: "группа: "
    });

    cell_2 = $('<div></div>', {
        class: "col-lg-7 col-md-7"
    });


    let in_group = $('<div></div>', {
        class: 'input-group'
    });

    let group_selector = $('<select></select>', {
        class: 'custom-select custom-select-sm',
        id: 'group_sel_id'
    });


    let def = $('<option></option>', {
        value: 0,
        label: 'без группы'
    });
    group_selector.append(def);
    mapOfGroups.forEach((value, key) => {
        let elem = $('<option></option>', {
            value: value,
            label: key,
        });
        if (key === cur_lamp.group) {
            elem.attr("selected", "selected");
        }

        group_selector.append(elem);
    });

    group_selector.change(() => {
        group_selector.removeClass("is-valid");
    });
    in_group.append(group_selector);
    let input_gr_addon = $('<div></div>', {
        class: 'input-group-addon input-group-button'
    });
    let sub_btn = $('<button></button>', {
        class: "btn btn-secondary btn-sm",
        text: '>',
        onclick: 'addObjToGroup( \'' + cur_lamp.name + '\')',
        title: "Изменить группу на выбранную"
    });
    sub_btn.prop('type', 'button');

    input_gr_addon.append(sub_btn);
    in_group.append(input_gr_addon);
    cell_2.append(in_group);


    /* кнопка*/
    let gr_button = $('<input/>', {
        type: 'image',
        src: 'map_api/icon/brightness.png',
        id: 'expandMenu',
        title: "Изменить яркость группы"
    });
    gr_button.attr('data-toggle', 'dropdown');
    gr_button.attr('aria-haspopup', 'true');
    gr_button.attr('aria-expanded', 'false');
    let gr_menu = $('<div></div>', {
        class: 'dropdown-menu'
    });

    let command = 'sendGroupCommand(' + "\'" + cur_lamp.group + "\'" + ', 0)';
    elem = $('<a></a>', {
        class: "dropdown-item",
        href: '#',
        onclick: command,
        text: 'Off'
    });
    gr_menu.append(elem);
    gr_menu.append($('<div></div>', {
        class: 'dropdown-divider'
    }));

    for (let i = 1; i <= 10; i++) {
        let command = 'sendGroupCommand(' + "\'" + cur_lamp.group + "\'" + ', ' + i * 10 + ')';
        elem = $('<a></a>', {
            class: "dropdown-item",
            href: '#',
            onclick: command,
            text: i * 10 + ' %'
        });
        gr_menu.append(elem);
    }
    /* кнопка*/
    cell_3 = $('<div></div>', {
        class: "col-lg-2 col-md-2",
    });
    cell_3.append(gr_menu);
    cell_3.append(gr_button);
    table_row.append(cell_1).append(cell_2).append(cell_3);
    table.append(table_row);
    table.append($('<div></div>', {
        class: 'dropdown-divider'
    }));
//*********************************************
//******************Выбор  КП******************
    table_row = $('<div></div>', {class: "row justify-content-start"});
    cell_1 = $('<div></div>', {
        class: "col-lg-3 col-md-3",
        text: "КП: "
    });
    cell_2 = $('<div></div>', {
        class: "col-lg-7 col-md-7"
    });


    let group = $('<div></div>', {
        class: 'input-group'
    });

    let cp_selector = $('<select></select>', {
        class: 'custom-select custom-select-sm',
        id: 'cp_sel_id'
    });


    def = $('<option></option>', {
        value: 0,
        label: 'Без Кп'
    });
    cp_selector.append(def);
    control_points.forEach((value, key) => {
        let elem = $('<option></option>', {
            value: value.id,
            label: value.objectName,
            // onclick:'chageCP('+1 + ', '+2 +')'
        });
        if (value.id === cur_lamp.cp_owner) {
            elem.attr("selected", "selected");
        }
        cp_selector.append(elem);
    });

    cp_selector.change(() => {
        cp_selector.removeClass("is-valid");
    });
    group.append(cp_selector);
    input_gr_addon = $('<div></div>', {
        class: 'input-group-addon input-group-button'
    });
    sub_btn = $('<button></button>', {
        class: "btn btn-secondary btn-sm",
        text: '>',
        onclick: 'changeData( \'' + cur_lamp.name + '\')',
        title: "Изменить прязку к КП"
    });
    sub_btn.prop('type', 'button');
    // пока не использую
    //ToDo: подобравть иконку!
    // sub_btn.append($('<image></image>',{
    //     src:'/icon/attachment.png'
    // }));
    input_gr_addon.append(sub_btn);
    group.append(input_gr_addon);
    cell_2.append(group);
    table_row.append(cell_1).append(cell_2);
    table.append(table_row);
    table.append($('<div></div>', {
        class: 'dropdown-divider'
    }));
    table_row = $('<div></div>', {class: "row justify-content-between"});
    cell_1 = $('<div></div>', {
        class: "col-4"
    });
    //acknowledgeGroup

    cell_2 = $('<div></div>', {
        class: "col-6"
    });
    cell_3 = $('<div></div>', {
        class: "col-2"
    });
    let aknowledge = $('<a></a>', {
        href: '#',
        onclick: 'acknowledge(\'' + cur_lamp.name + '\')',
        text: 'квитировать'
    });
    let aknowledgeAll = $('<a></a>', {
        href: '#',
        onclick: 'acknowledgeGroup(\'' + cur_lamp.name + '\')',
        text: 'квитировать группу'
    });
    let setCoord = $('<input/>', {
        type: 'image',
        src: 'map_api/icon/location.png',
        title: "Задать координаты на карте",
        onclick: 'setMarkerLatLng(\'' + cur_lamp.name + '\')'
    });
    cell_1.append(aknowledge);
    cell_2.append(aknowledgeAll);
    cell_3.append(setCoord);
    table_row.append(cell_1).append(cell_2).append(cell_3);
    table.append(table_row);

    card_body.append(table);
    card.append(breadcrumb);
    card.append(card_body);
    card.append(card_body_info);

    return card.html();
}


function sendLampRegFormData() {

    if (CheckLamp.prop("checked")) {
        let sdata = getData("lamp_add", "LAMP");
        if (checkInput()) {
            sendLamp(sdata);
        }
    } else if (CheckCP.prop("checked")) {
        let sdata = getData("cp_add", "KP");
        sendCP(sdata);
    }
    addPopupDialog.removeFrom(map);
    addPopupDialog = null;

}

/**
 * @return {string}
 */
function createAddGroupDialogPopup(groupName) {
    return '<form class="was-validated">' +
        '<div class="mt-2"><label><h5>Добавить устройство в группу ' + groupName + '?</h5></label></div>' +
        '<div>' +
        '<button type="button" class="btn btn-primary mt-2" onclick="addToGroup()">Добавить</button>' +
        '<button type="button" class="btn btn-primary mt-2 ml-4" onclick="removeFromGroup()">Убрать из группы</button>' +
        '</div>' +
        '</form>';
}


/**
 * @return {string}
 */
function CreateCPDialog() {
    return '<form>' +
        '<div class="mt-2"><label><h5>Регистрационные данные:</h5></label></div>' +
        '<label for="cp_alias_id">Наазвание КП:</label>' +
        '<input type="text" id="cp_alias_id" class="form-control " placeholder="название" required>' +
        '<button type="button" class="btn btn-primary mt-2" onclick="sendLampRegFormData()">Добавить</button>' +
        '</div>' +
        '</form>';
}

function getData(cmd, type) {
    let json = {
        "type": "addObject",
        "obj_type": cmd,
        "payload": {
            "latitude": lat,
            "longitude": lon,
            "eui": $('#EUI_id').val(),
            "alias": type === "KP" ? $('#cp_alias_id').val() : $('#aliasID').val(),
            "cp": $('#cp_selector_id').val(),
            "type": type,
            "groupsName": type === "GROUP" ? $('#new_group_name_input').val() : ""
        }
    };
    console.log("check", JSON.stringify(json));
    return json;
}

function checkInput() {
    if (checkAlias() && checkEUI()) {
        console.log("check", true);
        return true;
    } else if (!checkEUI()) {
        console.log("check", 'checkEUI() false');
    } else if (!checkAlias()) {
        console.log("check", 'checkAlias() false');
        console.log("check", document.getElementById("aliasID").value.toString());
    }
    return false;
}

// ToDo:выкинуть
function checkAlias() {
    // var alias = document.getElementById("aljava ias_id").value;
    // return alias.trim() != '';
    return true;

}

function checkEUI() {
    var re = /(([0-9a-fA-F]{2})-){7}([0-9a-fA-F]{2})/;
    var eui = document.getElementById("EUI_id").value;
    return re.test(eui);
}


/**
 * Добавление новой группы
 */
function addNewGroup() {
    let sdata = getData("group_add", "GROUP");
    sendGroups(sdata);
}

function removeFromGroup() {
    let json = {
        "eui": [activeMarker.name],
        "groupsName": null
    };

    let req = {
        "type": "add2Group",
        "obj_type": "add_lamp_to_group",
        "payload": json
    };
    sendLamp(req);
    activeMarker.marker.closePopup();
    activeMarker = null;
}

function removeSelectedObjFromGroup(selectedObj) {
    let json = {
        "eui": selectedObj,
        "groupsName": null
    };

    let req = {
        "type": "add2Group",
        "obj_type": "add_lamp_to_group",
        "payload": json
    };
    sendLamp(req);
}
// вычисляем позицию диалога
function getPopupPosition(selectedObj) {

}

//Показать Popup с выбором и возможностью создать локальную группу
function showGroupPopup(selectedObj) {
    console.log("showGroupPopup");

}


function addToGroup() {
    let json = {
        "eui": [activeMarker.name],
        "groupsName": $('#groups_id').val()
    };

    let req = {
        "type": "add2Group",
        "obj_type": "add_lamp_to_group",
        "payload": json
    };
    sendLamp(req);
    activeMarker.marker.closePopup();
    activeMarker = null;
}






