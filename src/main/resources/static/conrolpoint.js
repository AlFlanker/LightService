'use strict';
const token = $("meta[name='_csrf']").attr("content");
const header = $("meta[name='_csrf_header']").attr("content");
$(document).ready(function() {
    showProgressBar();
    $.ajax({
        url: "/objects/getCP",
        type: 'GET',
        contentType: "application/json",
        dataType: 'json',
        success: function (responce) {
            console.log("success : ", JSON.stringify(responce));
            $('#control_points_table').DataTable(
                {
                    "language": {
                        "url": "//cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/Russian.json"
                    },
                    data: responce,
                    columns: [
                        { data: "objectName" },
                        { data: "latitude" },
                        { data: "longitude" },
                        { data: "lastUpdate" }
                    ]
                }
            );
            stopProgressBar();
        },
        error: function (e) {

        }
    });
});


function stopProgressBar() {
    $("#load_cp").hide();
    $("#control_point").show();
}
function showProgressBar() {
    var imgObj = $("#load_cp");
    imgObj.show();

    var centerY = $(window).scrollTop() + ($(window).height() + imgObj.height())/2;
    var centerX = $(window).scrollLeft() + ($(window).width() + imgObj.width())/2;

    imgObj.offset({top:centerY, left:centerX});

}