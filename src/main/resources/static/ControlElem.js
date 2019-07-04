let ControlElem = L.Control.extend({
    options: {
        initPalette: 'hsv'
    },
    initialize: function(options) {
        L.setOptions(this, options);
        // this._fractalLayers = fractalLayers;
    },

    onAdd: function (map) {
        // create the control container with a particular class name
        let container = L.DomUtil.create('div', 'leaflet-control-layers leaflet-control-layers-expanded');
        let _this = this;

        let title = document.createElement('span');
        title.innerHTML = '<h5>Рабочий слой</h5>';

        let content = document.createElement('div');
        content.innerHTML='<div class="btn-group btn-group-toggle" data-toggle="buttons">'+
            '<label class="btn btn-secondary active">'+
            '<input type="radio" name="options" id="lamp_control_selector" autocomplete="off" checked> Лампа'+
            '</label>'+
            '<label class="btn btn-secondary">'+
            '<input type="radio" name="options" id="cp_control_selector" autocomplete="off"> КП'+
            '</label>'+
            '<label class="btn btn-secondary">'+
            '<input type="radio" name="options" id="option3" autocomplete="off"> ОТКЗ'+
            '</label>'+
            '</div>';
        container.appendChild(title);
        container.appendChild(content);
        return container;
    },

});