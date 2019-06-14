(function (L, setTimeout) {

    let _controlContainer,
        _map,
        _zoomThreshold,
        _hideClass = 'leaflet-control-group-hidden',
        _anchorClass = 'leaflet-control-group-in-osm-toggle',

        _Widgets =  {
            MultiButton: function (config) {
                //
                let className = 'leaflet-control-group-in-osm btn-group btn-group-sm',
                    helpText = "Операции над группами",
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
            GROUP:function (config) {
                let displayName ='<input class="btn btn-secondary active" hidden type="checkbox" name="options"  autocomplete="off" checked><img  data-toggle="tooltip" src="/icon/group.png" title="Добавить в группу">';
                return {
                    displayName:  displayName,
                    reason:'AddToGroup'
                };
            },
            UNGROUP:function (config) {
                let displayName = '<input class="btn btn-secondary" hidden type="checkbox" name="options"  autocomplete="off""><img data-toggle="tooltip" src="/icon/ungroup.png" title="Удалить из группы">';

                return {
                    displayName:  displayName,
                    reason:'DeleteFromGroup'
                };
            }
        };
      /*Обработчик нажатия на элемент
      входные данные элемент _Editors[]:
                {

                    displayName:  displayName,
                    title:'test 2'
                }
                checked="checked"
      * */
    function openRemote (editor) {
        let arr=[];
        if(selectedObj) {
            selectedObj.forEach(elem => {
                arr.push(elem.name);
            });
        }

        if(editor.reason === 'AddToGroup'){
            jQuery.noConflict();
            $('#add_2group_modal').modal('show');
        }
        else if(editor.reason === 'DeleteFromGroup'){
            removeSelectedObjFromGroup(arr);
        }

    }


    function addEditorToWidget(widgetContainer, editor, text) {

        let editorWidget = L.DomUtil.create('a', "osm-editor btn btn-secondary ", widgetContainer);
        editorWidget.setAttribute("style","background-color:#f4f4f4");
        editorWidget.href = "#";
        editorWidget.innerHTML = text || editor.displayName;
        L.DomEvent.on(editorWidget, "click", function (e) {
            openRemote(editor);
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

    L.Control.GroupMenu = L.Control.extend({

        options: {
            position: "topright",
            zoomThreshold: 0,
            widget: "multiButton",
            editors: ["group", "ungroup"]
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

                if (editorSmallName === "group") {
                    newEditors.push(new _Editors.GROUP());
                } else if (editorSmallName === "ungroup") {
                    newEditors.push(new _Editors.UNGROUP());
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


            _controlContainer.title = this.options.widget.helpText || '';

            L.DomUtil.create('a', _anchorClass, _controlContainer);

            this.options.widget.addEditors(_controlContainer, this.options.editors);
            return _controlContainer;
        },

        onRemove: function (map) {
            map.off('zoomend', this._onZoomEnd);
        }

    });

    L.Control.GroupMenu.Widgets = _Widgets;
    L.Control.GroupMenu.Editors = _Editors;

    L.Map.addInitHook(function () {
        if (this.options.GroupMenuControlOptions) {
            var options = this.options.GroupMenuControlOptions || {};
            this.GroupMenuControl = (new L.Control.GroupMenu(options)).addTo(this);
        }
    });

})(L, setTimeout);
