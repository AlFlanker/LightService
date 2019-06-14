import $ from "jquery";

const token = $("meta[name='_csrf']").attr("content");
const header = $("meta[name='_csrf_header']").attr("content");
const deleteHandlers = [];
const deleteWGHandlers = [];
export function subscribeToDelUser(handler) {
    deleteHandlers.push(handler);
}
export function subscribeToDelWG(handler) {
    deleteWGHandlers.push(handler);
}
export function deleteUser(id) {
    $.ajax({
        url: 'globalManager/users/delete/' + id,
        type: 'DELETE',
        contentType: "application/json",
        dataType: 'json',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            deleteHandlers.forEach(handler=>handler(data));
        },
        error: function (data) {
            deleteHandlers.forEach(handler=>handler(data));
        }
    });
}

export function deleteWorkGroup(id) {
    $.ajax({
        url: 'globalManager/workGroup/delete/' + id,
        type: 'DELETE',
        contentType: "application/json",
        dataType: 'json',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            deleteHandlers.forEach(handler=>handler(data));
        },
        error: function (data) {
            deleteHandlers.forEach(handler=>handler(data));
        }
    });

}

export function deleteLamp(id) {
    $.ajax({
        url: 'globalManager/lamp/delete/' + id,
        type: 'DELETE',
        contentType: "application/json",
        dataType: 'json',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
        },
        error: function (data) {
        }
    });
}

export function deleteCP(id) {
    $.ajax({
        url: 'globalManager/cp/delete/' + id,
        type: 'DELETE',
        contentType: "application/json",
        dataType: 'json',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
        },
        error: function (data) {
        }
    });
}