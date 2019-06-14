
var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
var map;
var arr = [];
var marker = [];
var lat;
var lon;
var first = true;
var icons = {
    controllPoint:{
        name: "controllPoint",
        icon:'images/mind.png'
    },
    Lamp:{
        name: "Lamp",
        icon:'images/lightbulb.png'
    },
};
var legend = document.getElementById('legend');



$(document).ready(function () {
    for (var key in icons) {
        var type = icons[key];
        var name = type.name;
        var icon = type.icon;
        var div = document.createElement('div');
        div.innerHTML = '<img src="' + icon + '"> ' + name;
        legend.appendChild(div);
    }
    fire_ajax_get();
});

function sendData() {
    var sdata = getData() ;
    if(checkInput()) {
        send(sdata);
    }
}


function getData() {

    var json = {
        "latitude" : lat,
        "longitude" : lon,
        "eui" : $('#EUI_id').val(),
        "alias" :  $('#aliasID').val()
    };
    console.log("check" , JSON.stringify(json));
    return json;
}
function checkInput() {
if(checkAlias() && checkEUI()) {
    console.log("check" , true);
    return true;
}
else if (!checkEUI()) {
    console.log("check" , 'checkEUI() false' );
}
else if (! checkAlias()){
    console.log("check" , 'checkAlias() false');
    console.log("check" ,document.getElementById("aliasID").value.toString());
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


function fire_ajax_get() {
    $.ajax({
        url: "/search",
        success: function (data){
            var json = JSON.stringify(data);
            console.log("SUCCESS : ", json);
            var jdata = JSON.parse(json);
            for (var i = 0; i < jdata.objectList.length; i++) {
                arr[i] = jdata.objectList[i];
            }
            delete jdata;
            delete json;
            console.log("arr: ", arr);
            initMap();
        }
    });

}

function initMap() {
    console.log("initMap" , 'initMap' );
    map = new google.maps.Map(document.getElementById("map"), {
        center: {lat: 45.071044, lng:39.000394 },
        zoom: 18
    });
    map.addListener('click', function(event) {
        if (isNumber(event.latLng.lat()) &&
            isNumber(event.latLng.lng())) {
            lat = event.latLng.lat();
            lon = event.latLng.lng();
        }
        console.log("ltln",lat +' ' + lon );
        showAddDialog();
    });


        map.controls[google.maps.ControlPosition.TOP_RIGHT].push(legend);


    for (var i = 0; i < arr.length; i++) {
       marker[i] =  new google.maps.Marker({
           position: new google.maps.LatLng( arr[i].location.latitude,arr[i].location.longitude),              // Координаты расположения маркера. В данном случае координаты нашего маркера совпадают с центром карты, но разумеется нам никто не мешает создать отдельную переменную и туда поместить другие координаты.
           map: map,
           animation: google.maps.Animation.DROP,
           title: arr[i].name


       });
        marker[i].addListener('click', function(event) {
            // showPropertiesDialog(event, this.title);
            if (this.getAnimation() !== null) {
                this.setAnimation(null);
            } else {
                this.setAnimation(google.maps.Animation.BOUNCE);
                uniqAnimation(this);

            }
            var json = { "eui" : this.title};
            $.ajax({
                url: "/search/get",
                type: "POST",
                contentType: "application/json",
                dataType: 'json',
                data: JSON.stringify(json),
                cache: false,
                timeout: 600000,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(header, token);
                },
                success: function (data){
                    var answer= JSON.stringify(data);
                    console.log("SUCCESS : ", answer);
                    showPropertiesActualDialog(event,answer);
                }
            });


        });
    }
    google.maps.event.addDomListener(window, "load", initMap);

}
function clearMarkers() {
    for (var i = 0; i < markers.length; i++) {
        markers[i].setMap(null);
    }
    markers = [];
}
function uniqAnimation(curMarker) {
    marker.forEach(function (value) { if(value!=curMarker){
        value.setAnimation(null);
    }});
}

function showAddDialog() {
    // alert("click");
    jQuery.noConflict();
    $('#AddDevModalDialog').modal('show');
}

function showPropertiesActualDialog(location,obj) {
    if (isNumber(location.latLng.lat()) &&
        isNumber(location.latLng.lng())) {

        // console.log("json",  answer);
        var answer = JSON.parse(obj);
        document.getElementById("latitude_in").value = location.latLng.lat();
        document.getElementById("longitude_in").value = location.latLng.lng();
        document.getElementById('name_id').value = answer.object.name;
        document.getElementById('alias_id').value = answer.object.alias;
        if (answer.object.objStates[0] !== 'null' ) {
            document.getElementById('vAC_id').value = answer.object.objStates[0]["data"]["vAc"];
            document.getElementById('iAC_id').value = answer.object.objStates[0]["data"]["iAc"];
            document.getElementById('temperature_id').value = answer.object.objStates[0]["data"]["temperature"];
            document.getElementById('vDCBoard_id').value = answer.object.objStates[0]["data"]["vDCBoard"];
            document.getElementById('state_id').value = answer.object.objStates[0]["data"]["state"];
            document.getElementById('brightness_id').value = answer.object.objStates[0]["data"]["brightness"];
            document.getElementById('date_id').value = answer.object.objStates[0].currentDate;
        }
        else{
            document.getElementById('vAC_id').value = "no data";
            document.getElementById('iAC_id').value = "no data";
            document.getElementById('temperature_id').value = "no data";
            document.getElementById('vDCBoard_id').value = "no data";
            document.getElementById('state_id').value = "no data";
            document.getElementById('brightness_id').value = "no data";
            document.getElementById('date_id').value = "no data";
        }
                jQuery.noConflict();
                $('#modalDialog').modal('show');
        }

    else{
        alert("some kind of problem");
    }
}

function isNumber(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
}
// function getForm() {
//     var json = [];
//     json["latitude"] = document.getElementById("latitude_in").value;
//     json["longitude"] = document.getElementById("longitude_in").value;
//     json["name"] = document.getElementById("name_id").value;
//     json["type"] = document.getElementById("inlineFormCustomSelect").value;
//     SendModalFormToServer(json);
//
// }
// function SendModalFormToServer(data) {
//     $.ajax({
//         type: "GET",
//         contentType: "application/json",
//         url: "/main",
//         data: JSON.stringify(search),
//         dataType: 'json',
//         cache: false,
//         timeout: 600000,
//         beforeSend: function (xhr) {
//             xhr.setRequestHeader(header, token);
//         },
//         success: function (data) {
//
//         },
//         error: function (e) {
//
//         }
//     });
// }

function send(sdata) {
    console.log("sendData : ", sdata);
    // jQuery.noConflict();
    $.ajax({
        url: "/newdev",
        type: "POST",
        contentType: "application/json",
        dataType: 'json',
        data: JSON.stringify(sdata),
        cache: false,
        timeout: 600000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            console.log("success : ", data);
            if(data.msg  == "Ok"){
                console.log("msg: ",data.msg );
                jQuery.noConflict();
                $('#AddDevModalDialog').modal('hide');
                fire_ajax_get();
            }
            else{
                console.log("msg: ",data.msg  );
            }
        },
        error: function (e) {
            console.log("ERROR : ", e);
        }
    });

}

function sendCommand(code) {
    var sendData = {
        "eui" : document.getElementById("name_id").value,
        "command": code
    };
    $.ajax({
        url: "/sendcmd",
        type: "POST",
        contentType: "application/json",
        dataType: 'json',
        data: JSON.stringify(sendData),
        cache: false,
        timeout: 600000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            console.log("success send: ", data);

        },
        error: function (e) {
            console.log("ERROR : ", e);
        }
    });


}





