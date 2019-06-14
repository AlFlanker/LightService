
const token = $("meta[name='_csrf']").attr("content");
const header = $("meta[name='_csrf_header']").attr("content");
function updateUserProp() {

    let form = {
        name: $('#username').val(),
        email: $('#email').val(),
        password: $('#pass').val(),
        repPassword: $('#rep_pass').val()
    };


    $.ajax({
        url: '/profile/user/save',
        type: "POST",
        contentType: "application/json",
        dataType: 'json',
        data: JSON.stringify(form),
        cache: false,
        timeout: 600000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {

            $('#username').addClass("is-valid");
            $('#email').addClass("is-valid");
            $('#pass').addClass("is-valid");
            $('#rep_pass').addClass("is-valid");
        },
        error: function (response) {
            debugger;
            $('#username').addClass("is-invalid");
            $('#email').addClass("is-invalid");
            $('#pass').addClass("is-invalid");
            $('#rep_pass').addClass("is-invalid");

        }
    });
}