// import $ from 'jquery'
// import $ from "jquery";

const token = $("meta[name='_csrf']").attr("content");
const header = $("meta[name='_csrf_header']").attr("content");
let Organization_username = $('#Organization_username');
let Organization_password = $('#Organization_password');
let Organization_email = $('#Organization_email');
let Organization_organization = $('#Organization_organization');

let WG_username = $('#WG_username');
let WG_password = $('#WG_password');
let WG_workGroup = $('#WG_workGroup');
let WG_email = $('#WG_email');

let userDetailsPost = [];
let worgGrouphandlers = [];
let organizationHandlers = [];
let userAddHandlers = [];

let currentOrganization;
let currentWorkGroup;
// let organizationRegistarate = $('#organization_registration');

export function subscribeAddUserPost(handler) {
    userAddHandlers.push(handler);
}

export function subscribeToUserDetailsPost(handler) {
    userDetailsPost.push(handler);
}

export function subscribeWorkGroupAddPost(handler) {
    worgGrouphandlers.push(handler);
}

export function subscribeOrganizationAddPost(handler) {
    organizationHandlers.push(handler);
}

export function registrateOrganizationRequest() {
    if (Organization_password.hasClass("is-invalid")) {
        Organization_password.removeClass("is-invalid");
    }

    if (Organization_organization.hasClass("is-invalid")) {
        Organization_organization.removeClass("is-invalid");
    }

    if (Organization_username.hasClass("is-invalid")) {
        Organization_username.removeClass("is-invalid");
    }

    if (Organization_email.hasClass("is-invalid")) {
        Organization_email.removeClass("is-invalid");
    }
    let json = {
        username: Organization_username.val(),
        password: Organization_password.val(),
        email: Organization_email.val(),
        organization: Organization_organization.val()
    }
    $.ajax({
        url: 'globalManager/addOrganization',
        type: "POST",
        contentType: "application/json",
        dataType: 'json',
        data: JSON.stringify(json),
        cache: false,
        timeout: 600000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            organizationHandlers.forEach(handler => handler(response));

        },
        error: function (response) {
            organizationHandlers.forEach(handler => handler(response));


        }
    });
}

export function registrateWG(currentOrganization) {
    let json = {
        username: WG_username.val(),
        password: WG_password.val(),
        workGroup: WG_workGroup.val(),
        email: WG_email.val(),
        organization: currentOrganization
    }
    if (WG_password.hasClass("is-invalid")) {
        WG_password.removeClass("is-invalid");
    }

    if (WG_workGroup.hasClass("is-invalid")) {
        WG_workGroup.removeClass("is-invalid");
    }

    if (WG_username.hasClass("is-invalid")) {
        WG_username.removeClass("is-invalid");
    }

    if (WG_email.hasClass("is-invalid")) {
        WG_email.removeClass("is-invalid");
    }
    $.ajax({
        url: 'globalManager/addWorkGroup',
        type: "POST",
        contentType: "application/json",
        dataType: 'json',
        data: JSON.stringify(json),
        cache: false,
        timeout: 600000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            worgGrouphandlers.forEach(handler => handler(response));
        },
        error: function (response) {
            worgGrouphandlers.forEach(handler => handler(response));
        }
    });
}

export function registateUser(currentWorkGroup) {

    if ($('#userAddForm_name_id').hasClass("is-invalid")) {
        $('#userAddForm_name_id').removeClass("is-invalid");
    }

    if ($('#userAddForm_pass_id').hasClass("is-invalid")) {
        $('#userAddForm_pass_id').removeClass("is-invalid");
    }
    if ($('#userAddFrom_email_id').hasClass("is-invalid")) {
        $('#userAddFrom_email_id').removeClass("is-invalid");
    }

    let id;
    if (typeof currentWorkGroup == "undefined") {
        id = -1;
    } else id = currentWorkGroup
    let data = {
        wg: id,
        username: $('#userAddForm_name_id').val(),
        password: $('#userAddForm_pass_id').val(),
        email: $('#userAddFrom_email_id').val()
    };
    $.ajax({
        url: 'globalManager/users/addnew',
        type: "POST",
        contentType: "application/json",
        dataType: 'json',
        data: JSON.stringify(data),
        cache: false,
        timeout: 600000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            userAddHandlers.forEach(handler => handler(response));
        },
        error: function (response) {
            userAddHandlers.forEach(handler => handler(response));
        }
    });
}

export function sendUserDetails(usr_id) {
    if (usr_id !== -1 && (!isNaN(parseFloat(usr_id)) && isFinite(usr_id))) {
        let role = $('#role_selector_id option:selected').text();
        let data = {
            id: parseInt(usr_id, 10),
            role: role,
            username: $('#username_id').val(),
            email: $('#email').val(),
            password: $('#user_pass_id').val(),
            password_rep: $('#user_pass_rep_id').val()
        };
        if (data.password.localeCompare(data.password_rep) !== 0 && data.password !== '') {
            $('#user_pass_rep_id').addClass("is-invalid");
            return;
        } else {
            if ($('#user_pass_rep_id').hasClass("is-invalid")) $('#user_pass_rep_id').removeClass("is-invalid");
        }
        $.ajax({
            url: 'globalManager/users/save',
            type: "POST",
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify(data),
            cache: false,
            timeout: 600000,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function (response) {
                if (!response.hasOwnProperty("status")) {
                    let ownPropertyNames = Object.getOwnPropertyNames(response);

                    ownPropertyNames.forEach((value, index) => {
                        switch (prop) {
                            case "usernameerror":
                                $('#username_id').addClass("is-invalid");
                                break;
                            case "emailerror":
                                $('#email').addClass("is-invalid");
                                break;
                        }
                    });
                } else {
                    if ($('#username_id').hasClass("is-invalid")) {
                        $('#username_id').removeClass("is-invalid");
                    }
                    if ($('#email').hasClass("is-invalid")) {
                        $('#email').removeClass("is-invalid");
                    }
                    if ($('#user_pass_id').hasClass("is-invalid")) {
                        $('#user_pass_id').removeClass("is-invalid");
                    }
                    if ($('#user_pass_rep_id').hasClass("is-invalid")) {
                        $('#user_pass_rep_id').removeClass("is-invalid");
                    }
                }
                $('#user_pass_id').val("");
                $('#user_pass_rep_id').val("");

                userDetailsPost.forEach(handler => handler(response));
            },
            error: function (response) {

                userDetailsPost.forEach(handler => handler(response));
                // console.log(JSON.stringify(response));
            }
        });


    }

}
