/*----------DOM Objects----------*/



const latitude_in = $("#latitude_in");
const longitude_in = $("#longitude_in");
const name_id = $('#name_id');
const alias_id = $('#alias_id');
const vAC_id = $('#vAC_id');
const iAC_id = $('#iAC_id');
const temperature_id = $('#temperature_id');
const vDCBoard_id = $('#vDCBoard_id');
const state_id = $('#state_id');
const brightness_id = $('#brightness_id');
const date_id = $('#date_id');
const group_id = $('#group_id');
const cp_id = $('#cp_id');
const group_selector = $('#groups_id');
let old_lamp_eui;
const token = $("meta[name='_csrf']").attr("content");
const header = $("meta[name='_csrf_header']").attr("content");
const user_role = $("meta[name='user_role']").attr("content");
let setNewLtnLon = false;
let current_eui;
/*----------END----------*/
// import {latitude_in,longitude_in,name_id,alias_id,vAC_id,iAC_id,temperature_id,vDCBoard_id,state_id,brightness_id,date_id} from "./map_DOM_elem";
let map;
let lampsState = new Map()
let lamps = new Map();
let control_points = new Map();
let mapOfGroups = new Map();
let groups = new Set();
let organizations = new Set();
let lat;
let lon;
let polyline;
let objects = L.layerGroup();
let controlPointsLayer = L.layerGroup();
let mLayer;
let url_img_cp = '/baseline_build_black_32х32.png';
let url_img_lamp = '/lamp.png';
let lines_cp2L = [];
let activeMarker;
let info_dialog_open = false;
let addPopupDialog=null;
let LayersButton;
let ActionState = 'lamp';
let rigthMB_is_down;
let Testlayer;


let icon_Lamp_selected = L.divIcon({
    iconSize: [10, 10],
    iconAnchor: [0, 0],
    popupAnchor: [0, 0],
    shadowSize: [0, 0],
    className: ' my-icon',
    html:  '<div class="animated-icon-selected"></div>'
});


let icon_Lamp_in_group = L.divIcon({
    iconSize: [10, 10],
    iconAnchor: [0, 0],
    popupAnchor: [0, 0],
    shadowSize: [0, 0],
    className: ' my-icon',
    html:  '<div class="animated-icon-in-group"></div>'
});

let icon = L.divIcon({
    iconSize: [10, 10],
    iconAnchor: [0, 0],
    popupAnchor: [0, 0],
    shadowSize: [0, 0],
    className: 'my-icon',
    html: '<div class="animated-icon"></div>'
});
let icon_ring = L.divIcon({
    iconSize: [10, 10],
    iconAnchor: [0, 0],
    popupAnchor: [0, 0],
    shadowSize: [0, 0],
    className: 'my-icon',
    html: '<div class="animated-icon gps_ring"></div>'
});
let icon_cp = L.divIcon({
    iconSize: [10, 10],
    iconAnchor: [0, 0],
    popupAnchor: [0, 0],
    shadowSize: [0, 0],
    className: 'my-icon',
    html: '<div class="animated-icon-cp"></div>'
});
let rectangle =null;
let def_pos;
let selectedObj = new Set();
let left_key_pressed = false;
let shift_key_pressed = false;
let cntrlIsPressed = false;
let startSelected = false;
const group_dialog = $('#add_group_modal');
const findInMap = (object, value) => {
    for (let [k, v] of object) {
        if (v == value) {
            return k;
        }
    }
    return '';
}
const findCPid = (object, value) => {
    for (let [k, v] of object) {
        if (v.id === value) {
            return k;
        }
    }
    return '';
}


function createActionmenu() {
return '<div class="leaflet-control-zoom leaflet-bar leaflet-control" style="margin-left: 0px" data-toggle="buttons">'+
    '<div class="btn-group btn-group-sm">'+
    '<div class="btn-group-vertical btn-group-toggle" data-toggle="buttons">'+
    '<label class="btn btn-secondary active" style="background-color: #f4f4f4">'+
    '<input type="radio" name="options" id="lamp_control_selector" autocomplete="off" checked > <img src="/smallLamp.png">'+
    '</label>'+
    '<label class="btn btn-secondary"  style="background-color: #f4f4f4" >'+
    '<input type="radio" name="options" id="cp_control_selector" autocomplete="off" > <img src="/smallBuild.png">'+
    '</label>'+
    '</div>'+
    '</div>'+
    '</div>';
        // classes : 'btn-group btn-group-sm',
}


function drawLampProperties(cur_lamp) {

    let card_body_info = $('<div></div>',{
        class:"card-body",
        style:'display:none',
        id:'lamp_info_body'
    });

    let table_info = $('<div></div>',{
        class:"container"
    });

//*********************************************
//********************Напряжение питания********************
     let table_row = $('<div></div>',{class:"row justify-content-around"});
     let cell_1 = $('<div></div>',{
        class:"col-lg-5 col-md-5",
        text:"Напряжение питания: "
    });
     let cell_2 = $('<div></div>',{
        class:"col-lg-5 col-md-5",
         id:"popup_ac",
        text: cur_lamp.objStates.length>0?cur_lamp.objStates[0].data.v_ac + ' В':'нет данных'
    });
    table_row.append(cell_1).append(cell_2);
    table_info.append(table_row);
    table_info.append( $('<div></div>',{
        class:'dropdown-divider'
    }));
//*********************************************
//********************Ток питания*******************
    table_row =$('<div></div>',{class:"row justify-content-around"});
    cell_1 =  $('<div></div>',{
        class:"col-lg-5 col-md-5",
        text:"Ток: "
    });

    cell_2 = $('<div></div>',{
        class:"col-lg-5 col-md-5",
        id:"popup_current_ac",
        text:cur_lamp.objStates .length>0?cur_lamp.objStates[0].data.i_ac + ' мА': 'нет данных'
    });

    table_row.append(cell_1).append(cell_2);
    table_info.append(table_row);
    table_info.append( $('<div></div>',{
        class:'dropdown-divider'
    }));

//*********************************************
//****************Температура**********************
    table_row = $('<div></div>',{class:"row justify-content-around"});
    cell_1 =  $('<div></div>',{
        class:"col-lg-5 col-md-5",
        text:"Температура: "
    });

    cell_2 = $('<div></div>',{
        class:"col-lg-5 col-md-5",
        id:"popup_temp",
        text: cur_lamp.objStates.length>0?cur_lamp.objStates[0].data.temperature + ' °C': 'нет данных'
    });
    table_row.append(cell_1).append(cell_2);
    table_info.append(table_row);
    table_info.append( $('<div></div>',{
        class:'dropdown-divider'
    }));

//*********************************************
//****************DC**********************
    table_row = $('<div></div>',{class:"row justify-content-around"});
    cell_1 =  $('<div></div>',{
        class:"col-lg-5 col-md-5",
        text:"Напряжение DC: "
    });

    cell_2 = $('<div></div>',{
        class:"col-lg-5 col-md-5",
        id:"popup_dc",
        text: cur_lamp.objStates.length>0?cur_lamp.objStates[0].data.vdcboard+ ' мВ': 'нет данных'
    });
    table_row.append(cell_1).append(cell_2);
    table_info.append(table_row);
    table_info.append( $('<div></div>',{
        class:'dropdown-divider'
    }));


//*********************************************
//**********дата последнего обновления*********
    table_row = $('<div></div>',{class:"row justify-content-around"});
    cell_1 = $('<div></div>',{
        class:"col-lg-5 col-md-5",
        text:"дата последнего обновления: "
    });
    let last_update;
    if (cur_lamp.objStates != null) {
        if (cur_lamp.objStates.length > 0) {
            last_update = cur_lamp.objStates[0].currentDate;
        }
    }
    let last_update_date;
    if(last_update === undefined) {
        last_update_date = "обновлений не было";
    }
    else{
        let str = new Date(Date.parse(last_update));
        last_update_date = str.getFullYear().toString()+"-"+((str.getMonth()+1).toString().length==2?(str.getMonth()+1).toString():"0"+(str.getMonth()+1).toString())+"-"+(str.getDate().toString().length==2?str.getDate().toString():"0"+str.getDate().toString())+" "+(str.getHours().toString().length==2?str.getHours().toString():"0"+str.getHours().toString())+":"+((parseInt(str.getMinutes()/5)*5).toString().length==2?(parseInt(str.getMinutes()/5)*5).toString():"0"+(parseInt(str.getMinutes()/5)*5).toString())+":00";

    }

    cell_2 =  $('<div></div>',{
        class:"col-lg-5 col-md-5",
        id:"popup_last_upd_prop",
        text: last_update_date
    });
    table_row.append(cell_1).append(cell_2);
    table_info.append(table_row);
    table_info.append( $('<div></div>',{
        class:'dropdown-divider'
    }));
    card_body_info.append(table_info);
    return card_body_info;
}

function drawButtonRow(cur_lamp) {
    //*********************************************
//********************Ряд кнопок***************
    let btn_row = $('<div></div>',{
        class:'row justify-content-center'
    });

//*********************************************
//********************кнопка комманд***********
    let command_but = $('<div></div>');
    let btn_group = $('<div></div>',{
        class:'btn-group',
        role:'group'
    });
    let button = $('<button></button>',{
        type:'button',
        id:'expandMenu',
        class:'btn btn-info dropdown-toggle mt-3',
        text:'Лампа'
    });
    button.attr('data-toggle','dropdown');
    button.attr('aria-haspopup','true');
    button.attr('aria-expanded','false');
    let menu = $('<div></div>',{
        class:'dropdown-menu'
    });
    let elem;
    let divived = $('<div></div>',{
        class:'dropdown-divider'
    });
    elem = $('<a></a>',{
        class:"dropdown-item",
        href:'#',
        onclick:'sendCommand(0)',
        text:'Off'
    });
    menu.append(elem);
    menu.append(divived);

    for(let i=1; i<=10;i++){
        elem = $('<a></a>',{
            class:"dropdown-item",
            href:'#',
            onclick:'sendCommand(' + i*10+')',
            text:i*10 + ' %'
        });
        menu.append(elem);
    }


//*********************************************
//*************кнопка комманд группы***********
    let _group_command_but = $('<div></div>');
    let gr_btn_group = $('<div></div>',{
        class:'btn-group',
        role:'group'
    });
    let gr_button = $('<button></button>',{
        type:'button',
        id:'expandMenu',
        class:'btn btn-info dropdown-toggle mt-3',
        text:'Группа'
    });
    gr_button.attr('data-toggle','dropdown');
    gr_button.attr('aria-haspopup','true');
    gr_button.attr('aria-expanded','false');
    let gr_menu = $('<div></div>',{
        class:'dropdown-menu'
    });

    divived = $('<div></div>',{
        class:'dropdown-divider'
    });
    let command ='sendGroupCommand(' +"\'"+ cur_lamp.group +"\'"+', 0)';
    elem = $('<a></a>',{
        class:"dropdown-item",
        href:'#',
        onclick:command,
        text:'Off'
    });
    gr_menu.append(elem);
    gr_menu.append(divived);

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
//*********************************************
//********************-----********************

    let col_btn_sc = $('<div></div>',{
        class:'col-lg-6 col-md-6'
    });
    let col_btn_gc = $('<div></div>',{
        class:'col-lg-6 col-md-6'
    });
    gr_btn_group.append(gr_menu);
    gr_btn_group.append(gr_button);
    col_btn_gc.append(gr_btn_group);
    btn_group.append(menu);
    btn_group.append(button);
    col_btn_sc.append(btn_group);
    btn_row.append(col_btn_sc).append(col_btn_gc);
    return btn_row;
}

function getCurrentDate(last_update) {
    let last_update_date;
    if(last_update === undefined) {
        last_update_date = "обновлений не было";
    }
    else{
        let str = new Date(Date.parse(last_update));
        last_update_date = str.getFullYear().toString()+"-"+((str.getMonth()+1).toString().length==2?(str.getMonth()+1).toString():"0"+(str.getMonth()+1).toString())+"-"+(str.getDate().toString().length==2?str.getDate().toString():"0"+str.getDate().toString())+" "+(str.getHours().toString().length==2?str.getHours().toString():"0"+str.getHours().toString())+":"+((parseInt(str.getMinutes()/5)*5).toString().length==2?(parseInt(str.getMinutes()/5)*5).toString():"0"+(parseInt(str.getMinutes()/5)*5).toString())+":00";
    }
    return last_update_date;
}












