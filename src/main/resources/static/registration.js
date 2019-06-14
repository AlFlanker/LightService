function registration(){
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
            if(data.status ==="ok"){
                /*if($('#vega_user_id') && $('#vega_pass_id')){
                    $('#vega_user_id').addClass('form-control is-valid');
                    $('#vega_pass_id').addClass('form-control is-valid');
                    $('#url_id').addClass('form-control is-valid');

                }
                if($('#net868_token_id')){
                    $('#net868_token_id').addClass('form-control is-valid');
                    $('#url_id').addClass('form-control is-valid');
                    $('#url2_id').addClass('form-control is-valid');
                }*/

            }
        },
        error: function (e) {
            console.log("ERROR : ", e);
           /* if($('#vega_user_id') && $('#vega_pass_id')) {
                $('#vega_user_id').addClass('form-control is-invalid');
                $('#vega_pass_id').addClass('form-control is-invalid');
                $('#url_id').addClass('form-control is-invalid');
            }
            if($('#net868_token_id')){
                $('#net868_token_id').addClass('form-control is-invalid');
                $('#url_id').addClass('form-control is-invalid');
                $('#url2_id').addClass('form-control is-invalid');
            }*/

        }
    });
}