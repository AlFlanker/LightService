(function (L, setTimeout) {
    let buttons = [];
    let _controlContainer,
        _map,
        _zoomThreshold,
        _shadow_class = 'inner-shadow',
        _hideClass = 'leaflet-control-switch-hidden',
        _anchorClass = 'leaflet-control-switch-in-osm-toggle',

        _Widgets =  {
            MultiButton: function (config) {
                //
                let className = 'leaflet-control-switch-in-osm btn-group btn-group-sm',
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
            LAMP:function (config) {
                let displayName ='<input class="btn btn-secondary" id="CheckLamp" hidden type="checkbox" name="options"  autocomplete="off"><img  data-toggle="tooltip" src="/icon/smallLamp.png" title="Добавить Лампу">';
                // let displayName ='<label class="edit_label" for="cb3"><input type="checkbox" id="cb3" /><img class="edit_img" data-toggle="tooltip" src="/icon/smallLamp.png" title="Добавить Лампу"></label>';

                return {
                    displayName:  displayName,
                    title:'CheckLamp'
                };
            },
            CP:function (config) {
                let displayName = '<input class="btn btn-secondary" id="CheckCP" hidden type="checkbox" name="options"  autocomplete="off"><img data-toggle="tooltip" src="/icon/smallBuild.png" title="Добавить КП">';
                // let displayName = ' <label class="edit_label" for="cb4"><input type="checkbox" id="cb4" /><img class="edit_img" data-toggle="tooltip" src="/icon/smallBuild.png" title="Добавить КП"></label>';
                return {
                    displayName:  displayName,
                    title:'CheckCP'
                };
            },
            CANCEL_BTN:function (config) {
                let displayName = '<input class="btn btn-secondary" id="Cancel_btn" hidden type="checkbox" name="options"  autocomplete="off"><img data-toggle="tooltip" src="/icon/devices.png" title="Отмена действий">';
                // let displayName = ' <label class="edit_label" for="cb4"><input type="checkbox" id="cb4" /><img class="edit_img" data-toggle="tooltip" src="/icon/smallBuild.png" title="Добавить КП"></label>';
                return {
                    displayName:  displayName,
                    title:'Cancel_btn'
                };
            }
        };


    function cancelAnotherSel() {
        let selectAll = $('#cb1');
        let selectSeq = $('#cb2');
        let cancel_btn = $('#cb3');
        let btn = $("#edit_obj");
        selectAll.parent().parent().removeClass('inner-shadow');
        selectSeq.parent().parent().removeClass('inner-shadow');
        cancel_btn.parent().parent().removeClass('inner-shadow');
        selectAll.prop("checked",false);
        selectSeq.prop("checked",false);
        cancel_btn.prop("checked",false);
        btn.css('background-image', "url('/icon/menu_black.png')");
        cntrlIsPressed = false;
        shift_key_pressed = false;
        map.dragging.enable();
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

                    displayName:  displayName,
                    title:'test 2'
                }
                checked="checked"
      * */
    function openRemote (editor,widget) {
        let checkLamp = $("#CheckLamp");
        let checkCP = $("#CheckCP");
        let btn = $("#switch_obj");
        cancelAnotherSel();
        if(editor.title === 'CheckLamp') {

            checkLamp.prop("checked",!checkLamp.prop("checked"));
            checkCP.prop('checked', false);
        } else if(editor.title === 'CheckCP'){
            checkCP.prop("checked",!checkCP.prop("checked"));
            checkLamp.prop('checked', false);
        } else if(editor.title === 'Cancel_btn'){
            checkCP.prop('checked',false);
            checkLamp.prop('checked', false);
        }

        if(checkLamp.prop("checked")){
            btn.css('background-image', "url('/icon/smallLamp.png')");
        }
        else if(checkCP.prop("checked")){
            btn.css('background-image', "url('/icon/smallBuild.png')");
        }
        else  btn.css('background-image', "url('/icon/devices.png')");

        if(addPopupDialog != null) {
            addPopupDialog.removeFrom(map);
            addPopupDialog = null;
        }
        buttons.forEach(button => {
            if ($(button).hasClass(_shadow_class) && ($(button).is($(widget)))) {
                $(button).removeClass(_shadow_class);
            }
            else if($(button).is($(widget)) && !($(button).hasClass(_shadow_class))) {
                let c = $(button).children("input");
                if (c.attr('id') !== 'Cancel_btn') {
                    $(button).addClass(_shadow_class);
                }
            } else $(button).removeClass(_shadow_class);
        });
    }





    function addEditorToWidget(widgetContainer, editor, text) {
        let editorWidget = L.DomUtil.create('a', "osm-editor btn btn-secondary ", widgetContainer);
        editorWidget.setAttribute("style","background-color:#f4f4f4");
        editorWidget.href = "#";
        editorWidget.innerHTML = text || editor.displayName;
        buttons.push(editorWidget);
        L.DomEvent.on(editorWidget, "click", function (e) {
            openRemote(editor,editorWidget);
            L.DomEvent.stop(e);
        });

    }

    function showOrHideUI() {
        var zoom = _map.getZoom();
        if (zoom < _zoomThreshold) {
            L.DomUtil.addClass(_controlContainer, _hideClass);
        } else {
            L.DomUtil.removeClass(_controlContainer, _hideClass);
        }
    }

    L.Control.SwitchMenu = L.Control.extend({

        options: {
            position: "topright",
            zoomThreshold: 0,
            widget: "multiButton",
            editors: ["cancel_btn","lamp", "cp"]
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
            widgetSmallName = typeof(widget) === 'string' ? widget.toLowerCase() : '';

            // setup widget from string or object
            if (widgetSmallName === "multibutton") {
                this.options.widget = new _Widgets.MultiButton();
            } else if (widgetSmallName === "attributionbox") {
                this.options.widget = new _Widgets.AttributionBox();
            }

            // setup editors from strings or objects
            for (var i in this.options.editors) {
                editor = this.options.editors[i],
                    editorSmallName = typeof(editor) === "string" ? editor.toLowerCase() : null;

                if (editorSmallName === "lamp") {
                    newEditors.push(new _Editors.LAMP());
                } else if (editorSmallName === "cp") {
                    newEditors.push(new _Editors.CP());
                }else if (editorSmallName === "cancel_btn") {
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
            _controlContainer.appendChild(L.DomUtil.create('div', "btn-group btn-group-toggle"));

            // _controlContainer = L.DomUtil.create('div', this.options.widget.className);

            _controlContainer.title = this.options.widget.helpText || '';

            // let a = L.DomUtil.create('a', _anchorClass, _controlContainer);
            let a = L.DomUtil.create('a', _anchorClass);
            a.setAttribute("id","switch_obj");
            _controlContainer.appendChild(a);
            this.options.widget.addEditors(_controlContainer, this.options.editors);
            return _controlContainer;
        },

        onRemove: function (map) {
            map.off('zoomend', this._onZoomEnd);
        }

    });

    L.Control.SwitchMenu.Widgets = _Widgets;
    L.Control.SwitchMenu.Editors = _Editors;

    L.Map.addInitHook(function () {
        if (this.options.SwitchMenuControlOptions) {
            var options = this.options.SwitchMenuControlOptions || {};
            this.SwitchMenuControl = (new L.Control.SwitchMenu(options)).addTo(this);
        }
    });

})(L, setTimeout);
