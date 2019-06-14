const getAllRequest = {"type": "get_all"};
const getAllWGRequest = {"type": "get_all_wg"};
const getAllOrgRequest = {"type": "get_all_org"};
let stompClient = null;
const handlers = [];
const group_name = $('#new_group_name_input');
let attempt = 1;
let watchDog;

function connect() {
    const socket = new SockJS("/maps_events");
    stompClient = Stomp.over(socket);
    stompClient.debug = () => {};
    stompClient.connect({"X-CSRF-TOKEN": token}, function (frame) {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/user/topic/activity', (mapData) => {
                console.log(mapData);
            });
            /*Subscribe on Diffrent LAMP EVENT*/
            stompClient.subscribe('/user/lamp/events', (mapData) => {
                let jdata = JSON.parse(mapData.body);
                if (jdata.type === "allLamps") {
                    allLampEvent(jdata);
                    jdata = null;
                } else if (jdata.type === "lamp_add") {
                    lampAddEvent(jdata);
                    jdata = null;

                } else if (jdata.type === "add_lamp_to_group") {
                    let group_sel = $('#group_sel_id')
                    if (jdata.status === "success") {
                        let lamp;

                        jdata.payload.eui.forEach((item, i, arr) => {
                            lamp = lamps.get(item);
                            lamp.group = findInMap(mapOfGroups, jdata.payload.groupsName);
                            if (lamp.workGroup.groups && lamp.workGroup.groups.length > 0) {
                                lamp.workGroup.groups[0].id = Number(jdata.payload.groupsName);
                                lamp.workGroup.groups[0].name = findInMap(mapOfGroups, jdata.payload.groupsName);
                            }

                        });

                        if (($("#add_2group_modal").data('bs.modal') || {})._isShown) {
                            $('#add_2group_modal').modal('hide');
                        }

                        group_sel.val(jdata.payload.groupsName);
                        group_sel.addClass("is-valid");
                        group_sel.change(() => {
                            group_sel.removeClass("is-valid");
                        });

                        fire_to_Update_Lamp();
                    }
                } else if (jdata.type === "changeBrightness") {

                    if (jdata.status === "success") {
                        if (jdata.payload !== null) {
                            console.log(jdata.payload);

                        }
                    }
                }
            });

            stompClient.subscribe('/user/cp/events', (mapData) => {
                var jdata = JSON.parse(mapData.body);
                if (jdata.type === "allCP") {
                    allCPEvent(jdata);
                    jdata = null;
                } else if (jdata.type === "cp_add") {
                    AddCPEvent(jdata);
                    jdata = null;
                }
                jdata = null;

            });
            stompClient.subscribe('/user/groups/events', (mapData) => {
                let data = JSON.parse(mapData.body);

                if (data.type === "allGroups") {
                    mapOfGroups = new Map();
                    for (let i = 0; i < data.object.length; i++) {
                        if (data.object[i] && data.object[i] !== null && data.object[i] !== 'undefined') {
                            Object.keys(data.object[i]).map(function (key) {
                                mapOfGroups.set(data.object[i][key], key);
                                groups.add(data.object[i][key]);
                            });
                        }
                    }
                    fire_to_Update_Menu();

                } else if (data.type === "Map_Group_Add") {
                    if (data.status === "success") {
                        groups.add(data.groupsName);
                        mapOfGroups.set(data.groupsName, data.id);
                        if (group_name.hasClass("is-invalid")) {
                            group_name.removeClass("is-invalid");
                        }
                        let opt = $('<option></option>', {
                            value: data.groupsName,
                            label: data.groupsName
                        });
                        group_selector.append(opt);
                        group_selector.val(data.groupsName);
                    } else {
                        group_name.addClass("is-invalid");
                    }
                }

            });
            stompClient.subscribe('/user/wg/events', (mapData) => {
                let data = JSON.parse(mapData.body);
                if (data.type === "ALL_WORK_GROUP") {
                    if (data.status === "success") {
                        if (data.object != null && data.object !== undefined) {
                            for (let i = 0; i < data.object.length; i++) {
                                if (data.object[i] && data.object[i] !== null && data.object[i] !== undefined) {
                                    organizations.add(new Organization(data.object[i].id, data.object[i].name, data.object[i].workGroup4MapList));

                                }
                            }
                        }

                    }
                }
                console.log(mapData);
            });
            stompClient.subscribe('/user/org/events', (mapData) => {
                let data = JSON.parse(mapData.body);
                if (data.type === "ALL_ORGANIZATION") {
                    if (data.status === "success") {
                        if (data.object != null && data.object !== undefined) {
                            for (let i = 0; i < data.object.length; i++) {
                                if (data.object[i] && data.object[i] !== null && data.object[i] !== undefined) {
                                    organizations.add(new Organization(data.object[i].id, data.object[i].name, data.object[i].workGroup4MapList));
                                }
                            }
                        }

                    }
                }
                console.log(mapData);
            });

            sendLamp(getAllRequest);
            sendCP(getAllRequest);
            sendGroups(getAllRequest);
            if (user_role === "[ADMIN]") {
                sendOrgs(getAllOrgRequest);
            }
            if (user_role === "[SuperUserOwner]") {
                sendWorkGroups(getAllWGRequest);
            }

        },
        stompFailureCallback
    )
    ;
}

function addHandler(handler) {
    handlers.push(handler);
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendLamp(lamp) {
    stompClient.send("/app/lamp", {}, JSON.stringify(lamp));
}

function sendCP(cp) {
    stompClient.send("/app/cp", {}, JSON.stringify(cp));
}

function sendGroups(group) {
    stompClient.send("/app/groups", {}, JSON.stringify(group));
}

function sendWorkGroups(wg) {
    stompClient.send("/app/wg", {}, JSON.stringify(wg));
}

function sendOrgs(org) {
    stompClient.send("/app/org", {}, JSON.stringify(org));
}

function sendWSDATA(mapData) {
    stompClient.send("/app/changeMessage", {}, JSON.stringify(mapData));
}


function sendCommand(code) {

    let json = {
        "eui": $('#head_eui_id').text(),
        "command": code
    };
    let sendData = {
        "type": "changeBrightness",
        "obj_type": "changeBrightness",
        "payload": json
    };

    sendLamp(sendData);
}

function sendGroupCommand(name, value) {
    let names = [];
    if (selectedObj != null && selectedObj.size > 1) {
        selectedObj.forEach(value => {
            names.push(value.name);
        });
    }

    let group_id = mapOfGroups.get(name);

    let payload = {
        "group": group_id,
        "lamps": names,
        "command": value
    };
    let send = {
        "type": "changeGroupBrightness",
        "obj_type": "changeBrightness",
        "payload": payload
    };

    sendLamp(send);

}

function sleep(ms) {
    ms += new Date().getTime();
    while (new Date() < ms) {
    }
}

var stompFailureCallback = function (error) {
    console.log('STOMP connect is : ' + stompClient.connected);
    console.log('STOMP: Reconecting in ' + (attempt * 500) / 1000 + ' seconds');
    sleep(200);
    if (attempt <= 10) {
        setTimeout(connect, (attempt * 500));
        attempt++;
    }
};

var reconnect = function(){
    console.log("watchDog: Stomp connect is " + stompClient.connected);
    if(!stompClient.connected){
        connect();
    }
}
/* end WebSocket section*/
$(document).ready(function () {
    InitMap();
    connect();
    watchDog = setInterval(reconnect,5000);
});

$(document).on("change", '#orgs_sel_id', (e) => {
    console.log("test");
    reloadWGSelector();
});
$(document).on("change", '#wg_sel_id', (e) => reloadLocalGroupSelector());
