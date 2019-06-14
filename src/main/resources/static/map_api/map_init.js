

/*---------------Base Objects---------------*/
function Lamp(name, alias, location, objStates, cp_owner, lastUpdate, group, marker,workGroup,organization) {
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

function CP(objectName, latitude, longitude, lastUpdate, marker, id,workGroup,organization) {
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
/*------------------------------------------*/
let CheckLamp,CheckCP ;



function InitMap() {
    map = L.map('mapid',{zoomControl: false, layers:[objects,controlPointsLayer]}).setView([45.071089, 38.997525], 18);
    mLayer = L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoiYWxleGZsYW5rZXIiLCJhIjoiY2puNGttMnlxMDA5bjNxczNhMjg3eDlocSJ9.PAZdQJ9c51fmNreVN_UKFg', {
        attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
        maxZoom: 16,
        id: 'mapbox.streets'
    });
    L.control.zoom({position:'bottomleft'}).addTo(map);
    mLayer.addTo(map);



    CheckLamp = $("#CheckLamp");
    CheckCP = $("#CheckCP");

    let mlayer = {
        "map": mLayer
    };
    let overlay_objects = {
        "Светильники": objects,
        "КП": controlPointsLayer
    };

    let controlLayers = L.control.layers(null, overlay_objects, {position: 'topleft', collapsed: true});
    controlLayers.addTo(map);

    $("#header_id").click(()=>{
        let cb_id = $("#card_body_id");
        let head = $("#header_id");
        let info_pane = $("#info-pane");
        if(cb_id.css("display") !=="none"){
            cb_id.prop("style","display:none");
            head.text("+");
            head.css({"border-radius":"50%","background": "dimgray"});
            info_pane.css({"border-radius":"50%"});
            // head.prop("style","background: dimgray");

        }
        else{
            cb_id.prop("style","display:block");
            head.text("Меню:");
            head.prop("style","background-color: dimgray");
            info_pane.css({"border-radius":".25rem"});
        }
    })
}

function updateLampPopun(cur_lamp) {
    let card = $('<div></div>',{
        class:"card",
        id:'main_info_popup_card'
    });
    card.attr("width","100%");
    card.attr("display","inline-block");
    let card_body = $('<div></div>',{
        class:"card-body",
        id:'main_info_popup_body'
    });
    let card_body_info = drawLampProperties(cur_lamp);

    let table = $('<div></div>',{
        class:"container"
    });

    let breadcrumb = $('<nav></nav>');
    breadcrumb.attr('aria-label',"breadcrumb");
    breadcrumb.attr("style",'background:#ffffffff')
    let ol = $('<ol></ol>',{
        class:"breadcrumb"
    });
    ol.attr("display","flex");
    let li_1 = $('<li></li>',{
        class:"breadcrumb-item"
    });
    let a_1 =$('<a></a>',{
        href:'#',
        text:'Основаная информация',
        onclick:'showMainInfo()'
    });
    li_1.attr("display","inline-block")
    li_1.append(a_1);
    let li_2 = $('<li></li>',{
        class:"breadcrumb-item"
    });
    li_2.append($('<a></a>',{
        href:'#',
        text:'Измерения',
        onclick:'showLampInfo()'
    }));
    li_2.attr("display","block");
    ol.append(li_1).append(li_2);
    breadcrumb.append(ol);



//*********************************************
//********************EUI********************
    let table_row = $('<div></div>',{class:"row justify-content-between"});
    table_row.attr("display","inline-block");
    let cell_1 = $('<div></div>',{
        class:"col-lg-2 col-md-3",
        text:"EUI: "
    });
    let cell_2 = $('<div></div>',{
        class:"col-lg-8 col-md-7",
        text: cur_lamp.name,
        id:"head_eui_id"
    });

    cell_2.attr("align","left");

    table_row.append(cell_1).append(cell_2)
    table.append(table_row);

    table.append( $('<div></div>',{
        class:'dropdown-divider'
    }));
//*********************************************
//**********Alias*********
    table_row = $('<div></div>',{class:"row justify-content-start"});
    cell_1 = $('<div></div>',{
        class:"col-lg-2 col-md-3",
        text:"Alias: "
    });



    cell_2 =  $('<div></div>',{
        class:"col-lg-8 col-md-7",
        id:"popun_alias",
        text: cur_lamp.alias
    });
    cell_2.attr("align","center");

    table_row.append(cell_1).append(cell_2)
    table.append(table_row);
    table.append( $('<div></div>',{
        class:'dropdown-divider'
    }));

//*********************************************
//**********дата последнего обновления*********
    table_row = $('<div></div>',{class:"row justify-content-start"});
    cell_1 = $('<div></div>',{
        class:"col-lg-4 col-md-4",
        text:"дата обновления: "
    });
    let last_update;
    if (cur_lamp.objStates != null) {
        if (cur_lamp.objStates.length > 0) {
            last_update = cur_lamp.objStates[0].currentDate;
        }
    }


    cell_2 =  $('<div></div>',{
        id:"popup_last_upd",
        class:"col-lg-8 col-md-8",
        text: (last_update=== undefined)?"обновлений не было":last_update
    });
    cell_2.attr("align","left");
    table_row.append(cell_1).append(cell_2);
    table.append(table_row);
    table.append( $('<div></div>',{
        class:'dropdown-divider'
    }));
//*********************************************
//****************Яркость**********************
    table_row = $('<div></div>',{class:"row justify-content-between"});
    cell_1 =  $('<div></div>',{
        class:"col-lg-3 col-md-4",
        text:"яркость: "
    });
    let br;
    if (cur_lamp.objStates != null) {
        if (cur_lamp.objStates.length > 0) {
            br = cur_lamp.objStates[0].data.brightness;
        }
    }
    cell_2 = $('<div></div>',{
        id:"popup_bri",
        class:"col-lg-6 col-md-5 ",
        text: br === undefined ? "нет данных" : br + " %"
    });
    cell_2.attr("align","center");
    let button = $('<input/>',{
        type:'image',
        src:'map_api/icon/brightness.png',
        id:'expandMenu',
        title:"Изменить яркость лампы"

    });
    button.attr('data-toggle','dropdown');
    button.attr('aria-haspopup','true');
    button.attr('aria-expanded','false');
    let menu = $('<div></div>',{
        class:'dropdown-menu'
    });

    let elem = $('<a></a>',{
        class:"dropdown-item",
        href:'#',
        onclick:'sendCommand(0)',
        text:'Off'
    });
    menu.append(elem);
    menu.append( $('<div></div>',{
        class:'dropdown-divider'
    }));

    for(let i=1; i<=10;i++){
        elem = $('<a></a>',{
            class:"dropdown-item",
            href:'#',
            onclick:'sendCommand(' + i*10+')',
            text:i*10 + ' %'
        });
        menu.append(elem);
    }

    let cell_3 =$('<div></div>',{
        class:"col-lg-2 col-md-3",
    });
    cell_3.append(menu);
    cell_3.append(button);

    table_row.append(cell_1).append(cell_2).append(cell_3);
    table.append(table_row);
    table.append( $('<div></div>',{
        class:'dropdown-divider'
    }));

    //*********************************************
//********************группа*******************
    table_row =$('<div></div>',{class:"row justify-content-between"});
    cell_1 =  $('<div></div>',{
        class:"col-lg-3 col-md-3",
        text:"группа: "
    });

    cell_2 = $('<div></div>',{
        class:"col-lg-7 col-md-7",
        text:cur_lamp.group
    });
    cell_2.attr("align","center");

    /* кнопка*/
    let gr_button = $('<input/>',{
        type:'image',
        src:'map_api/icon/brightness.png',
        id:'expandMenu',
        title:"Изменить яркость группы"
    });
    gr_button.attr('data-toggle','dropdown');
    gr_button.attr('aria-haspopup','true');
    gr_button.attr('aria-expanded','false');
    let gr_menu = $('<div></div>',{
        class:'dropdown-menu'
    });

    let command ='sendGroupCommand(' +"\'"+ cur_lamp.group +"\'"+', 0)';
    elem = $('<a></a>',{
        class:"dropdown-item",
        href:'#',
        onclick:command,
        text:'Off'
    });
    gr_menu.append(elem);
    gr_menu.append( $('<div></div>',{
        class:'dropdown-divider'
    }));

    for(let i=1; i<=10;i++){
        let command = 'sendGroupCommand(' +"\'"+ cur_lamp.group +"\'"+', '+ i*10 +')';
        elem = $('<a></a>',{
            class:"dropdown-item",
            href:'#',
            onclick:command,
            text:i*10 + ' %'
        });
        gr_menu.append(elem);
    }
    /* кнопка*/
    cell_3 =$('<div></div>',{
        class:"col-lg-2 col-md-2",
    });
    cell_3.append(gr_menu);
    cell_3.append(gr_button);
    table_row.append(cell_1).append(cell_2).append(cell_3);
    table.append(table_row);
    table.append( $('<div></div>',{
        class:'dropdown-divider'
    }));
//*********************************************
//******************Выбор  КП******************
    table_row =  $('<div></div>',{class:"row justify-content-start"});
    cell_1 =  $('<div></div>',{
        class:"col-lg-3 col-md-3",
        text:"КП: "
    });
    cell_2 = $('<div></div>',{
        class:"col-lg-7 col-md-7",
        text:findCPid(control_points,cur_lamp.cp_owner)
    });
    cell_2.attr("align","center");
    table_row.append(cell_1).append(cell_2);
    table.append(table_row);
    table.append( $('<div></div>',{
        class:'dropdown-divider'
    }));
    table_row = $('<div></div>', {class: "row justify-content-between"});
    cell_1 = $('<div></div>', {
        class: "col-4"
    });
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

    cell_1.append(aknowledge);
    cell_2.append(aknowledgeAll);
    table_row.append(cell_1).append(cell_2).append(cell_3);
    table.append(table_row);
    card_body.append(table);
    card.append(breadcrumb);
    card.append(card_body);
    card.append(card_body_info);

    return card.html();
}


function sendData() {

    if (CheckLamp.prop("checked")) {
        let sdata = getData("lamp_add", "LAMP");
        if (checkInput()) {
            sendLamp(sdata);
        }
    }
    else if(CheckCP.prop("checked")){
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
    return '<form class="was-validated">'+
        '<div class="mt-2"><label><h5>Добавить устройство в группу '+groupName+'?</h5></label></div>'+
        '<div>'+
        '<button type="button" class="btn btn-primary mt-2" onclick="addToGroup()">Добавить</button>'+
        '<button type="button" class="btn btn-primary mt-2 ml-4" onclick="removeFromGroup()">Убрать из группы</button>'+
        '</div>'+
        '</form>';
}

/**
 * @return {string}
 */
function CreateAddDialog() {
    return '<form class="was-validated">' +
        '<div class="mt-2"><label><h5>Регистрационные данные:</h5></label></div>' +
        '<label for="EUI_id">EUI:</label>' +
        '<input type="text" id="EUI_id" class="form-control " placeholder="eui" required pattern="(([0-9a-fA-F]{2})-){7}([0-9a-fA-F]{2})">' +
        '<label for="aliasID">Алиас:</label>' +
        '<input type="text" id="aliasID" class="form-control " placeholder="алиас" required>' +
        '<label for="cp_selector_id">Выберите КП: </label>' +
        '<select class="custom-select custom-select-sm" id="cp_selector_id" required>' +
        '</select>' +
        '<div>' +
        '<button type="button" class="btn btn-primary mt-2" onclick="sendLampRegFormData()">Добавить</button>' +
        '</div>' +
        '</form>';


}

/**
 * @return {string}
 */
function CreateCPDialog() {
    return '<form>' +
        '<div class="mt-2"><label><h5>Регистрационные данные:</h5></label></div>' +
        '<label for="cp_alias_id">Название КП:</label>' +
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
            "groupsName":type === "GROUP" ? $('#group_name_id').val() : ""
        }
    };
    console.log("check", JSON.stringify(json));
    return json;
}
function checkInput() {
    if (checkAlias() && checkEUI()) {
        console.log("check", true);
        return true;
    }
    else if (!checkEUI()) {
        console.log("check", 'checkEUI() false');
    }
    else if (!checkAlias()) {
        console.log("check", 'checkAlias() false');
        console.log("check", document.getElementById("aliasID").value.toString());
    }
    return false;
}

function checkAlias() {
    // var alias = document.getElementById("alias_id").value;
    // return alias.trim() != '';
    return true;

}

function checkEUI() {
    var re = /(([0-9a-fA-F]{2})-){7}([0-9a-fA-F]{2})/;
    var eui = document.getElementById("EUI_id").value;
    return re.test(eui);
}
function addNewGroup() {
    let sdata = getData("group_add", "GROUP");
    sendGroups(sdata);
}
function removeFromGroup(){
    let json = {
        "eui": activeMarker.name,
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
function addToGroup() {
    let json = {
        "eui": activeMarker.name,
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



