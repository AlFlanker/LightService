'use strict';
let token = $("meta[name='_csrf']").attr("content");
let header = $("meta[name='_csrf_header']").attr("content");

$(document).ready(getStates());

function getStates() {
    let requestString = "?datefrom=2016-01-01T00:00:00Z&dateto=2017-12-31T23:59:59Z&objectsid=1,2,3&tags=v_ac,i_ac,temperature,brightness,state,date_of_changed&limit=2000";
    //showProgressBar();
    $.ajax({
        url: "/states/allstates" + requestString,
        type: 'GET',
        contentType: "application/json",
        dataType: 'json',
        success: function (response) {
            $('#states_table').DataTable(
                {
                    "language": {
                        "url": "//cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/Russian.json"
                    },


                    // данные с сервера будут поступать по всем установленным параметрам (без выбора определённого), но с выбором периода отчёта
                    // данные с сервера придётся пересобирать, так как возвращается упорядоченный массив значений, а не массив объектов с именами
                    // параметр columns заполнять из списка выбранных параметров
                    //


                    data: response.data,
                    columns:
                        [
                            {data: "objAlias"},
                            {data: "currentDate"},
                            {data: "vac"},
                            {data: "iac"},
                            {data: "temperature"},
                            {data: "vdcboard"},
                            {data: "brightness"},
                            {data: "state"}
                        ],
                    initComplete: function () {
                        this.api().columns([0]).every(function () {
                            let column = this;
                            let div2Filter = $('<div class="form-group" id="filter_div"></div>').appendTo('#states_table_length');
                            let label = $('<div><label for="selector_states">Выберите параметры фильтра</label></div>').appendTo(div2Filter);
                            let select = $('<select id="selector_states" class="custom-select custom-select-sm form-control form-control-sm" style="width: auto"><option value="">Показать все</option></select>')
                                .appendTo('#filter_div')
                                .on('change', function () {
                                    let val = $.fn.dataTable.util.escapeRegex(
                                        $(this).val()
                                    );
                                    column
                                        .search(val ? '^' + val + '$' : '', true, false)
                                        .draw();
                                });
                            column.data().unique().sort().each( function ( d, j ) {
                                select.append( '<option value="'+d+'">'+d+'</option>' )
                            } );
                        });
                    }
                }
            )
            ;
            //stopProgressBar();
        },
        error: function (e) {
        }
    })
    ;
}

function parse(arr) {
    let result;
    for (let i = 0; i < arr.length; i++) {
        document.getElementById('name_id').value = answer.object.name;
        document.getElementById('alias_id').value = answer.object.alias;
        if (answer.object.objStates[0] !== 'null') {
            document.getElementById('vAC_id').value = answer.object.objStates[0]["data"]["vAc"];
            document.getElementById('iAC_id').value = answer.object.objStates[0]["data"]["iAc"];
            document.getElementById('temperature_id').value = answer.object.objStates[0]["data"]["temperature"];
            document.getElementById('vDCBoard_id').value = answer.object.objStates[0]["data"]["vDCBoard"];
            document.getElementById('state_id').value = answer.object.objStates[0]["data"]["state"];
            document.getElementById('brightness_id').value = answer.object.objStates[0]["data"]["brightness"];
            document.getElementById('date_id').value = answer.object.objStates[0].currentDate;
        }
    }
}

function stopProgressBar() {
    $("#dignissim-animunculus").addClass('pre-loader-awaiter');
    $("#main-body").removeClass('pre-loader-awaiter');
}

function showProgressBar() {
    $("#dignissim-animunculus").removeClass('pre-loader-awaiter');
    $("#main-body").addClass('pre-loader-awaiter');

}
