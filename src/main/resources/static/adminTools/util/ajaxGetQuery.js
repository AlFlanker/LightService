import $ from 'jquery'

const workGroupHandlers =[];
const organizationHandler = [];
const organizationsHandler = [];
const userHandler = [];
const lampHandler = [];
const controlPointHandler = [];
const userdetailsHandlers = [];
const activeUsersHanders = [];

export function subscribeToActiveUsers(handler) {
    activeUsersHanders.push(handler);
}

export function subscribeToUserDetails(handler) {
    userdetailsHandlers.push(handler);
}

export function subscribeToWG(handler) {
    workGroupHandlers.push(handler);
}

export function subscribeToOrganization(handler) {
    organizationHandler.push(handler);
}

export function subscribeToUser(handler) {
    userHandler.push(handler);
}

export function subscribeToLamp(handler) {
    lampHandler.push(handler);
}

export function subscribeToCP(handler) {
    controlPointHandler.push(handler);
}

export function subscribeToOrganizations(handler) {
    organizationsHandler.push(handler);
}

export function getWorkGroupRequest(org_id) {
      if (typeof org_id == "undefined") {
        $.ajax({
            url: "/globalManager/organization",
            type: 'GET',
            contentType: "application/json",
            dataType: 'json',
            success: function (responce) {
                workGroupHandlers.forEach(handler=> handler(responce));
            },
            error: function (e) {

            }
        });
    } else {
        let id = org_id;
        $.ajax({
            url: "/globalManager/organizations/" + id,
            type: 'GET',
            contentType: "application/json",
            dataType: 'json',
            success: function (responce) {
                workGroupHandlers.forEach(handler=> handler(responce));
            },
            error: function (e) {

            }
        });
    }
}

export function getUsersRequest(id) {
    //id будет из сессии
    if (typeof id == "undefined") {
        $.ajax({
            url: "/globalManager/organizations/objects",
            type: 'GET',
            contentType: "application/json",
            dataType: 'json',
            data: {id: '', type: 'Users', page: 0, size: 100},
            success: function (responce) {
                userHandler.forEach(handler=> handler(responce));
            },
            error: function (e) {

            }
        });
    } else {
        $.ajax({
            url: "/globalManager/organizations/objects",
            type: 'GET',
            contentType: "application/json",
            dataType: 'json',
            data: {id: id, type: 'Users', page: 0, size: 100},
            success: function (responce) {
                userHandler.forEach(handler=> handler(responce));
            },
            error: function (e) {

            }
        });
    }
}

export function getAllOrganizationsRequest() {
    $.ajax({
        url: "/globalManager/organizations",
        type: 'GET',
        contentType: "application/json",
        dataType: 'json',
        success: function (responce) {
            organizationsHandler.forEach(handler=> handler(responce));
        },
        error: function (e) {

        }
    });
}

/**
 *
 * @param workGroup - id workGroup
 * @param page - какую страницу
 * @param size - элемент/страница, если 0, то 20(default)
 */
export function loadLampPage(workGroup, page, size){
    if (typeof workGroup == "undefined") {
        $.ajax({
            url: "/globalManager/organizations/objects",
            type: 'GET',
            contentType: "application/json",
            dataType: 'json',
            data: {id: '', type: 'Lamp', page: page, size: size === 0 ? 100 : size},
            success: function (responce) {
                lampHandler.forEach(handler=> handler(responce));
            },
            error: function (e) {

            }
        });
    } else {
        $.ajax({
            url: "/globalManager/organizations/objects",
            type: 'GET',
            contentType: "application/json",
            dataType: 'json',
            data: {id: workGroup, type: 'Lamp', page: page, size: size === 0 ? 100 : size},
            success: function (responce) {
                lampHandler.forEach(handler=> handler(responce));
            },
            error: function (e) {

            }
        });
    }
}

export function loadCPPage(workGroup, page, size) {
    if (typeof workGroup == "undefined") {
        $.ajax({
            url: "/globalManager/organizations/objects",
            type: 'GET',
            contentType: "application/json",
            dataType: 'json',
            data: {id: '', type: 'CP', page: page, size: size === 0 ? 100 : size},
            success: function (responce) {
                controlPointHandler.forEach(handler=> handler(responce));
            },
            error: function (e) {

            }
        });
    } else {
        $.ajax({
            url: "/globalManager/organizations/objects",
            type: 'GET',
            contentType: "application/json",
            dataType: 'json',
            data: {id: workGroup, type: 'CP', page: page, size: size === 0 ? 100 : size},
            success: function (responce) {
                controlPointHandler.forEach(handler=> handler(responce));
            },
            error: function (e) {

            }
        });
    }
}

export function loadUserDetails(id) {
    $.ajax({
        url: "/globalManager/users/info",
        type: 'GET',
        contentType: "application/json",
        dataType: 'json',
        data: {id: id},
        success: function (responce) {
            userdetailsHandlers.forEach(handler=> handler(responce));
        },
        error: function (e) {

        }
    });
}

export function loadActiveUsers() {
    $.ajax({
        url: "/globalManager/activeUsers",
        type: 'GET',
        contentType: "application/json",
        dataType: 'json',
        success: function (responce) {
            activeUsersHanders.forEach(handler=> handler(responce));
        },
        error: function (e) {

        }
    });
}