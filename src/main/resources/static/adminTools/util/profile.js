import $ from 'jquery'
const token = $("meta[name='_csrf']").attr("content");
const header = $("meta[name='_csrf_header']").attr("content");


export function showNetDataForm(netData) {
    let net_data = netData;

    $('#serviceType_id').change(function () {
        let elem = $('#req_form');
        let form = $('#profile_form');
        let selector = $('#serviceType_id');
        let inner;
        if (elem) elem.remove();
        switch (selector.val()) {
            case 'Vega':
                elem = $('<div id="req_form" class="form-group row"></div>');
                elem.append('<label class="col-3 col-form-label" style="width: fit-content" for="vega_user_id">Введите имя пользователя:</label>');
                inner = $('<div class="col-9"></div>');
                inner.append('<input type="text" class="form-control"   placeholder="Имя пользователя" id ="vega_user_id" /></intput>');
                elem.append(inner);
                elem.append('<label class="col-3 col-form-label" style="width: fit-content" for="vega_pass_id">Введите пароль:</label>');
                inner = $('<div class="col-9"></div>');
                inner.append('<input type="text"  class="form-control mr-5" placeholder="Пароль" id="vega_pass_id"/>');
                elem.append(inner);
                elem.append('<label class="col-3 col-form-label " style="width: fit-content" for="url_id">WSS:</label>');
                inner = $('<div class="col-9"></div>');
                inner.append('<input type="url"  class="form-control mr-5"  title="ws://xxx.xxx.xxx.xxx:port" id="url_id" />');
                elem.append(inner);
                inner = $('<div class="col-md-2"></div>');
                inner.append('<button  class="btn btn-primary mt-5"  id="sendProfile_id">Сохранить</button>');
                // inner.append('<div class="form-check">\n' +
                //     '  <input class="form-check-input" type="checkbox" name="ProfileRadios" id="ProfileRadios_id" value="">\n' +
                //     '  <label class="form-check-label" for="ProfileRadios_id">\n' +
                //     '    Активный\n' +
                //     '  </label>\n' +
                //     '</div>');
                inner.append( '<div class="form-check">\n'+
                '<label class="form-check-label">\n'+
                '<input class="form-check-input" type="checkbox" value="" id="ProfileRadios_id">\n'+
                'Активный \n'+
                '<span class="form-check-sign"> \n'+
                '<span class="check"></span> \n'+
                '</span> \n'+
                '</label> \n'+
                '</div> \n');



                elem.append(inner);
                form.append(elem);
                netData.forEach(function (data) {
                    if (data.typeOfService === 'Vega') {
                        $('#vega_user_id').val(data.login);
                        $('#vega_pass_id').val(data.password);
                        $('#url_id').val(data.url);
                        $('#ProfileRadios_id').prop('checked', data.active);
                    }
                });
                break;
            case 'Net868':
                elem = $('<div id="req_form" class="form-group row"></div>');
                elem.append('<label class="col-3 col-form-label" style="width: fit-content" for="net868_token_id">Введите токен:</label>');
                inner = $('<div class="col-9"></div>');
                inner.append('<input type="text" class="form-control" placeholder="токен" id="net868_token_id" /></intput>');
                elem.append(inner);
                elem.append('<label class="col-3 col-form-label " style="width: fit-content" for="url_id">Appdata URL:</label>');
                inner = $('<div class="col-9"></div>');
                inner.append('<input type="url"  class="form-control mr-5" title="https://bs.net868.ru:20010/externalapi/appdata" id="url_id" />');
                elem.append(inner);
                elem.append('<label class="col-3 col-form-label " style="width: fit-content" for="url2_id">Command URL:</label>');
                inner = $('<div class="col-9"></div>');
                inner.append('<input type="url"  class="form-control mr-5" title="https://bs.net868.ru:20010/externalapi/sendCommand"  id="url2_id" />');
                elem.append(inner);

                elem.append('<label class="col-3 col-form-label " style="width: fit-content" for="url3_id">WSS:</label>');
                inner = $('<div class="col-9"></div>');
                inner.append('<input type="url"  class="form-control mr-5" title="wss://bs.net868.ru:20010/devstatus" id="url3_id" />');
                elem.append(inner);

                inner = $('<div class="col-md-2"></div>');
                inner.append('<button  class="btn btn-primary mt-5" id="sendProfile_id">Сохранить</button>');
                inner.append( '<div class="form-check">\n'+
                    '<label class="form-check-label">\n'+
                    '<input class="form-check-input" type="checkbox" value="" id="ProfileRadios_id">\n'+
                    'Активный \n'+
                    '<span class="form-check-sign"> \n'+
                    '<span class="check"></span> \n'+
                    '</span> \n'+
                    '</label> \n'+
                    '</div> \n');
                elem.append(inner);
                form.append(elem);
                netData.forEach(function (data) {
                    if (data.typeOfService === 'Net868') {
                        $('#net868_token_id').val(data.token);
                        $('#url_id').val(data.url);
                        $('#url2_id').val(data.url2);
                        $('#url3_id').val(data.wss);
                        $('#ProfileRadios_id').prop('checked', data.active);
                    }
                });
                break;
            default:
                break;
        }


    })
}

export function sendForm(org_id) {
    let vegaUserId = $('#vega_user_id');
    let vegaPassId = $('#vega_pass_id');
    let urlId = $('#url_id');
    let net868TokenId = $('#net868_token_id');
    let url2Id = $('#url2_id');
    let url3Id = $('#url3_id');

    let selector = $('#serviceType_id');
    let username = vegaUserId.val();
    let password = vegaPassId.val();
    let net_token = net868TokenId.val();
    let url = urlId.val();
    let url2 = url2Id.val();
    let url3 = url3Id.val();
    let isActive = $('#ProfileRadios_id').prop('checked');

    let data = {
        "service": selector.val(),
        "net868_token": net_token ? net_token : "",
        "vega_username": username ? username : "",
        "vega_password": password ? password : "",
        "url": url ? url : "",
        "url2": url2 ? url2 : "",
        "url3": url3 ? url3 : "",
        "isActive": isActive,
        "id":org_id
    };

    console.log("sendData : ", data);
    // jQuery.noConflict();
    $.ajax({
        url: "/profile/save_profile",
        type: "POST",
        contentType: "application/json",
        dataType: 'json',
        data: JSON.stringify(data),
        cache: false,
        timeout: 600000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            console.log("success : ", data);
            if (data.status === "ok") {
                if (vegaUserId && vegaPassId) {
                    vegaUserId.addClass('form-control is-valid');
                    vegaPassId.addClass('form-control is-valid');
                    urlId.addClass('form-control is-valid');

                }
                if (net868TokenId) {
                    net868TokenId.addClass('form-control is-valid');
                    urlId.addClass('form-control is-valid');
                    url2Id.addClass('form-control is-valid');
                }

            }
        },
        error: function (e) {
            console.log("ERROR : ", e);

            if (vegaUserId && vegaPassId) {
                vegaUserId.addClass('form-control is-invalid');
                vegaPassId.addClass('form-control is-invalid');
                urlId.addClass('form-control is-invalid');
            }
            if (net868TokenId) {
                net868TokenId.addClass('form-control is-invalid');
                urlId.addClass('form-control is-invalid');
                url2Id.addClass('form-control is-invalid');
            }

        }
    });

}

export function getNetData(currentOrganization) {

    $.ajax({
        url: "/profile/getNetData",
        type: 'GET',
        contentType: "application/json",
        dataType: 'json',
        data: {id: currentOrganization},
        success: function (responce) {
            let netData = responce;
            console.log(netData);
            showNetDataForm(netData);

        },
        error: function (e) {

        }
    });

}
