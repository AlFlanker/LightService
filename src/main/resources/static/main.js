'use strict';
var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
var arr =[];
var Location = {
    latitude:0.0,
    longitude:0.0,
    address:'',

    toString:function () {
        return 'latitude: ' + this.latitude + ', ' + 'longitude: '+  this.longitude
            + ', '+'address: ' + this.address;
    },
    newObject:function (config) {
        var buf = Object.create(this);
        for(var key in config){
            if(config.hasOwnProperty(key)){
                buf[key] = config[key];
            }
        }
        return buf;
    }
};
var BaseObject = {
    id: 0,
    name:'',
    objType:'',
    Location:{},
    toString:function () {
        return 'id: ' + this.id + ', ' + 'name: '+  this.name +' /n' + this.Location.toString();

    },
    newObject:function (config) {
        var buf = Object.create(this);
        for(var key in config){
            if(config.hasOwnProperty(key)){
                buf[key] = config[key];
            }
        }
        return buf;
    }
};

var BaseObject_1 = {
    BaseObject:{},
    toString:function () {
        return 'id: ' + this.BaseObject.id + ', ' + 'name: '+  this.BaseObject.name +' /n'
            + this.BaseObject.location.latitude
            + this.BaseObject.location.longitude
            + this.BaseObject.location.address;

    },
    newObject:function (config) {
        var buf = Object.create(this);
        for(var key in config){
            if(config.hasOwnProperty(key)){
                buf[key] = config[key];
            }
        }
        return buf;
    }
};
$(document).ready(function () {


    $("#search-form").submit(function (event) {

        //stop submit the form, we will post it manually.
        event.preventDefault();
        fire_ajax_get();

    });

});


function fire_ajax_submit() {

    var search = {}
    search["username"] = $("#uname").val();

    $("#btn-search").prop("disabled", true);

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "http://localhost:8080/search",
        data: JSON.stringify(search),
        dataType: 'json',
        cache: false,
        timeout: 600000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            var json = "<h4>Ajax Response</h4><pre>"
                + JSON.stringify(data, null, 1) + "</pre>";
            $('#feedback').html(json);

            console.log("SUCCESS : ", data);
            $("#btn-search").prop("disabled", false);

        },
        error: function (e) {

            var json = "<h4>Ajax Response</h4><pre>"
                + e.responseText + "</pre>";
            $('#feedback').html(json);

            console.log("ERROR : ", e);
            $("#btn-search").prop("disabled", false);

        }
    });
}
function fire_ajax_get() {
        $.ajax({
            url: "http://localhost:8080/search"
        }).then(function(data) {
            var json = JSON.stringify(data);
            // $('#feedback').html(json);
            console.log("SUCCESS : ", json);
            var jdata = JSON.parse(json);
            console.log("arr: ", jdata);
            for (var i = 0; i < jdata.objectList.length; i++) {
                arr[i] = BaseObject_1.newObject({
                    BaseObject:jdata.objectList[i]

                });
            }
            jdata = null;
            console.log("arr: ", arr);
        });
    }

// for (var i = 0; i < jdata.objectList.length; i++) {
//     arr[i] = BaseObject.newObject({
//         // foo:jdata.objectList[i]
//         id: jdata.objectList[i].id,
//         name: jdata.objectList[i].name,
//         objType: jdata.objectList[i].objectsType,
//         Location: Location.newObject({
//             latitude: jdata.objectList[i].location.latitude,
//             longitude: jdata.objectList[i].location.longitude,
//             address: jdata.objectList[i].location.address
//         })
//
//     });
// }
