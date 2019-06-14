(function (L, setTimeout) {
    let buttons = [];
    let _controlContainer,
        _map,
        _zoomThreshold,
        _hideClass = 'leaflet-control-edit-hidden',
        _anchorClass = 'leaflet-control-edit-in-osm-toggle',

        _Widgets = {
            MultiButton: function (config) {
                let className = 'leaflet-control-edit-in-osm',
                    helpText = "Меню выделения объектов",
                    addEditors = function (container, editors) {
                        for (let i in editors) {
                            addEditorToWidget(container, editors[i]);
                        }
                    };

                return {
                    className: (config && config.className) || className,
                    helpText: (config && config.helpText) || helpText,
                    addEditors: (config && config.addEditors) || addEditors
                };
            },
            AttributionBox: function (config) {
                let className = 'leaflet-control-attribution',
                    helpText = "Edit in OSM",
                    addEditors = function (container, editors) {
                        addEditorToWidget(container, editors[0], helpText);
                    };

                return {
                    className: (config && config.className) || className,
                    helpText: (config && config.helpText) || helpText,
                    addEditors: (config && config.addEditors) || addEditors
                };
            }
        },

        _Editors = {
            SELECT_ALL: function (config) {
                let displayName = ' <label class="edit_label" for="cb1"><input type="checkbox" id="cb1" /><img class="edit_img" data-toggle="tooltip" src="/icon/border_style.png" title="Рамка"></label>';
                return {
                    displayName: displayName,
                    title: 'select_all'
                };
            },
            SEQUENTIAL_SELECTION: function (config) {
                let displayName = '<label class="edit_label" for="cb2"><input type="checkbox" id="cb2" /><img class="edit_img" data-toggle="tooltip" src="/icon/share_black.png" title="Последовательное выделение"></label>';
                return {
                    displayName: displayName,
                    title: 'sequential_selection'
                };
            },
            CANCEL_BTN: function (config) {
                let displayName = '<label class="edit_label" for="cb3"><input type="checkbox" id="cb3" /><img class="edit_img" data-toggle="tooltip" src="/icon/menu_black.png" title="Отмена действия"></label>';
                return {
                    displayName: displayName,
                    title: 'cancel_btn'
                };
            }
        };


    function cancelAnotherSel() {
        let checkLamp = $("#CheckLamp");
        let checkCP = $("#CheckCP");
        let btn = $("#switch_obj");
        checkLamp.parent().removeClass('inner-shadow');
        checkCP.parent().removeClass('inner-shadow');
        checkLamp.prop("checked",false);
        checkCP.prop("checked",false);
        btn.css('background-image', "url('/icon/devices.png')");


    }

    function unselected() {
        cntrlIsPressed = false;
        shift_key_pressed = false;
        if(selectedObj) {
            if (selectedObj.size > 0) {
                selectedObj.forEach(value => {
                    value.marker.setIcon(icon);
                });
                selectedObj = null;
            }
        }
    }
    /*Обработчик нажатия на элемент
    входные данные элемент _Editors[]:
              {
                  url: '',
                  displayName:  displayName,
                  buildUrl: '',
                  title:'test 2'
              }
    * */
    function openRemote(editor, widget) {
        if(addPopupDialog != null) {
            addPopupDialog.removeFrom(map);
            addPopupDialog = null;
        }
        let selectAll = $('#cb1');
        let selectSeq = $('#cb2');
        let cancel_btn = $('#cb3');
        let btn = $("#edit_obj");
        cancelAnotherSel();
        if (editor.title === 'select_all') {
            selectAll.prop('checked', !selectAll.prop('checked'));
            selectSeq.prop('checked',false);
            cancel_btn.prop('checked',false);
            selectSeq.parent().parent().removeClass('inner-shadow');
            cancel_btn.parent().parent().removeClass('inner-shadow');
            if (map.dragging._enabled && selectAll.prop('checked')) {
                map.dragging.disable();
                cntrlIsPressed=false;
                shift_key_pressed = true;
            } else {
                map.dragging.enable();
                shift_key_pressed = false;
                unselected();
            }

        } else if (editor.title === 'sequential_selection') {
            selectAll.prop('checked',false);
            cancel_btn.prop('checked',false);
            selectAll.parent().parent().removeClass('inner-shadow');
            cancel_btn.parent().parent().removeClass('inner-shadow');
            if(selectSeq.prop("checked")){
                if(selectedObj) {
                    if (selectedObj.size > 0) {
                        selectedObj.forEach(value => {
                            value.marker.setIcon(icon);
                        });
                        selectedObj = null;
                    }
                }
            }
            selectSeq.prop('checked', !selectSeq.prop('checked'));
            map.dragging.enable();
            if (cntrlIsPressed) {
                cntrlIsPressed = false;
                shift_key_pressed = false;
            } else {
                cntrlIsPressed = true;
                shift_key_pressed = false;
            }
        } else if (editor.title === 'cancel_btn') {
            map.dragging.enable();
            cntrlIsPressed = false;
            shift_key_pressed = false;
            selectAll.prop('checked',false);
            selectSeq.prop('checked',false);
            cancel_btn.prop('checked',false);
            selectAll.parent().parent().removeClass('inner-shadow');
            selectSeq.parent().parent().removeClass('inner-shadow');
            cancel_btn.parent().parent().removeClass('inner-shadow');
        }


        if (selectAll.prop("checked")) {
            btn.css('background-image', "url('/icon/border_style.png')");
        } else if (selectSeq.prop("checked")) {
            btn.css('background-image', "url('/icon/share_black.png')");
        } else btn.css('background-image', "url('/icon/menu_black.png')");

        buttons.forEach(button => {
            if ($(button).hasClass("inner-shadow") && ($(button).is($(widget)))) {
                $(button).removeClass("inner-shadow");
            } else if ($(button).is($(widget)) && !($(button).hasClass("inner-shadow")) ) {
                let c = $(button).children().children("input");
                if(c.attr('id') !== 'cb3'){
                    $(button).addClass("inner-shadow");
                }
            } else $(button).removeClass("inner-shadow");

        });
    }


    function addEditorToWidget(widgetContainer, editor, text) {
        let editorWidget = L.DomUtil.create('a', "osm-editor", widgetContainer);
        editorWidget.href = "#";
        editorWidget.innerHTML = text || editor.displayName;
        buttons.push(editorWidget);
        L.DomEvent.on(editorWidget, "click", function (e) {
                openRemote(editor, editorWidget);
                L.DomEvent.stop(e);
            }
        );
    }

    function showOrHideUI() {
        var zoom = _map.getZoom();
        if (zoom < _zoomThreshold) {
            L.DomUtil.addClass(_controlContainer, _hideClass);
        } else {
            L.DomUtil.removeClass(_controlContainer, _hideClass);
        }
    }

    L.Control.EditInOSM = L.Control.extend({

        options: {
            position: "topright",
            zoomThreshold: 0,
            widget: "multiButton",
            editors: ['cancel_btn',"select", "seq_select"]
        },

        initialize: function (options) {
            var newEditors = [],
                widget,
                widgetSmallName,
                editor,
                editorSmallName;

            L.setOptions(this, options);

            _zoomThreshold = this.options.zoomThreshold;

            widget = this.options.widget;
            widgetSmallName = typeof (widget) === 'string' ? widget.toLowerCase() : '';

            // setup widget from string or object
            if (widgetSmallName === "multibutton") {
                this.options.widget = new _Widgets.MultiButton();
            } else if (widgetSmallName === "attributionbox") {
                this.options.widget = new _Widgets.AttributionBox();
            }

            // setup editors from strings or objects
            for (var i in this.options.editors) {
                editor = this.options.editors[i],
                    editorSmallName = typeof (editor) === "string" ? editor.toLowerCase() : null;

                if (editorSmallName === "select") {
                    newEditors.push(new _Editors.SELECT_ALL());
                } else if (editorSmallName === "seq_select") {
                    newEditors.push(new _Editors.SEQUENTIAL_SELECTION());
                } else if (editorSmallName === "cancel_btn") {
                    newEditors.push(new _Editors.CANCEL_BTN());
                } else {
                    newEditors.push(editor);
                }
            }
            this.options.editors = newEditors;

        },

        onAdd: function (map) {
            _map = map;
            map.on('zoomend', showOrHideUI);

            _controlContainer = L.DomUtil.create('div', this.options.widget.className);

            _controlContainer.title = this.options.widget.helpText || '';

            // L.DomUtil.create('a', _anchorClass, _controlContainer);

            let a = L.DomUtil.create('a', _anchorClass);
            a.setAttribute("id", "edit_obj");
            _controlContainer.appendChild(a);

            this.options.widget.addEditors(_controlContainer, this.options.editors);
            return _controlContainer;
        },

        onRemove: function (map) {
            map.off('zoomend', this._onZoomEnd);
        }

    });

    L.Control.EditInOSM.Widgets = _Widgets;
    L.Control.EditInOSM.Editors = _Editors;

    L.Map.addInitHook(function () {
        if (this.options.editInOSMControlOptions) {
            var options = this.options.editInOSMControlOptions || {};
            this.editInOSMControl = (new L.Control.EditInOSM(options)).addTo(this);
        }
    });

})
(L, setTimeout);
