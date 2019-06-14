
var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
$(document).ready(function() {
    showProgressBar();
    $.ajax({
        url: "/groups/get",
        type: 'GET',
        contentType: "application/json",
        dataType: 'json',
        success: function (responce) {
            console.log("success : ", JSON.stringify(responce));
            var buf = [];
            for(var i = 0; i < responce.length;i++ ){
                buf[i] ={
                    name:responce[i]
                }
            }
            console.log(JSON.stringify(buf));
            $('#groups_table').DataTable(
                {
                    "language": {
                        "url": "//cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/Russian.json"
                    },
                    data: buf,
                    columns: [
                        { data: "name" }
                    ]
                }
            );
            delete (buf);
            stopProgressBar();
        },
        error: function (e) {

        }
    });
});

function stopProgressBar() {
    $("#load_obj").hide();
    $("#objects").show();
}
function showProgressBar() {
    var imgObj = $("#load_obj");
    imgObj.show();

    var centerY = $(window).scrollTop() + ($(window).height() + imgObj.height())/2;
    var centerX = $(window).scrollLeft() + ($(window).width() + imgObj.width())/2;
    imgObj.offset({top:centerY, left:centerX});

}