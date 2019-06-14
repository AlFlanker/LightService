/*---------------Mouse events---------------*/
function marker_mouseover() {
    let curGroup;
    let lampByGroup = [];
    let cur_lamp;
    lamps.forEach((value, key) => {
        if (value.marker === this) {
            cur_lamp = value;
        }
    });
    curGroup = cur_lamp.group;
    if (curGroup !== "" && curGroup !== null) {

        lamps.forEach((value, key) => {
            if ((value.group === curGroup)) {
                // value.marker.setIcon(icon_Lamp_in_group);
                lampByGroup.push(value);
            }
        });

        if (lampByGroup.length) {
            let arr = [];
            lampByGroup.forEach((value, index) => {
                arr[index] = new L.LatLng(value.location.latitude, value.location.longitude);
            });
            polyline = new L.polyline(arr, {
                color: 'green',
                weight: 3,
                opacity: 1,
                smoothFactor: 1,
                className: ''
            });
            polyline.bindTooltip(curGroup);
            polyline.addTo(map);
        }
    }
}


function marker_mouseout() {
    let cur_lamp;
    console.log("marker_mouseout");
    lamps.forEach((value, key) => {
        if (value.marker === this) {
            cur_lamp = value;
        }
    });
    curGroup = cur_lamp.group;
    if (curGroup !== "" && curGroup !== null) {
        lamps.forEach((value, key) => {
            if ((value.group === curGroup)) {
                value.marker.setIcon(icon);
            }
        });

    }
}

function marker_onClick(event) {

    let cur_lamp;
    if (polyline) {
        polyline.removeFrom(map);
        delete polyline;
    }
    lamps.forEach((value, key) => {
        if (value.marker === this) {
            cur_lamp = value;
        }
    });

    if ((cntrlIsPressed === false) && cur_lamp !== null) {
        let card;
        if(user_role === "[USER]"){
            card = updateLampPopun(cur_lamp);
        }else {
            card = lampPopunWithEditFunc(cur_lamp);
        }
        cur_lamp.marker.bindPopup(card,{maxWidth: "auto"});
        info_dialog_open = true;

    }
    /*Добавление в группу выделенных*/

    else if (cntrlIsPressed && cur_lamp !== null) {
        //Если этого не делать, то событие (даже при запрете проброски) открывает диалог!
        cur_lamp.marker.unbindPopup();

        if (selectedObj === null) {
            selectedObj = new Set();
        }
        if (selectedObj.has(cur_lamp)) {
            selectedObj.delete(cur_lamp);
            cur_lamp.marker.setIcon(icon);
        } else {
            selectedObj.add(cur_lamp);
            cur_lamp.marker.setIcon(icon_Lamp_selected);
            selectedObj.add(cur_lamp);
        }
    }
}

$(document).keydown(function (event) {

    if (event.which === 16) {
        shift_key_pressed = true;
        map.dragging.disable();

    }
    if (event.which === 17) {
        cntrlIsPressed = true;
    }

});

$(document).keyup(function (event) {
    let selectAll = $('#cb1');
    let selectSeq = $('#cb2');
    let btn = $("#edit_obj");
    if (event.which === 16) {
        if (selectAll.prop('checked') && selectAll.parent().parent().hasClass('inner-shadow')) {
            selectAll.parent().parent().removeClass('inner-shadow');
            selectAll.prop('checked', false);
            btn.css('background-image', "url('/icon/menu_black.png')");
        }
        shift_key_pressed = false;
        map.dragging.enable();

        if (rectangle != null) {
            getSelectedMarkerByFrame(rectangle.getBounds());
            rectangle.removeFrom(map);
            rectangle = null;
        }

    }
    if (event.which === 17) {
        if (selectSeq.prop('checked') && selectSeq.parent().parent().hasClass('inner-shadow')) {
            selectSeq.parent().parent().removeClass('inner-shadow');
            selectSeq.prop('checked', false);
            btn.css('background-image', "url('/icon/menu_black.png')");
        }
        cntrlIsPressed = false;

    }
});


function test() {
    alert(selectedObj);
}

// export {marker_onClick,marker_mouseout,marker_mouseover};
/*------------------------------------------*/