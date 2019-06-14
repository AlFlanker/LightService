import {getNetData, sendForm} from 'util/profile'
import {
    deleteCP,
    deleteLamp,
    deleteUser,
    deleteWorkGroup,
    subscribeToDelUser,
    subscribeToDelWG
} from './util/ajaxDeleteQuery'
import {BaseObjDTO, OrganizationInfo, UserInfo, WorkGroupInfo,getActiveUsersTable} from './util/view_class'
import {isNumber} from './util/utilComponent'
import {
    getAllOrganizationsRequest,
    getUsersRequest,
    getWorkGroupRequest,
    loadCPPage,
    loadLampPage,
    loadUserDetails,
    loadActiveUsers,
    subscribeToCP,
    subscribeToLamp,
    subscribeToOrganizations,
    subscribeToUser,
    subscribeToUserDetails,
    subscribeToWG,
    subscribeToActiveUsers,
} from './util/ajaxGetQuery'
import {
    registateUser,
    registrateOrganizationRequest,
    registrateWG,
    sendUserDetails,
    subscribeAddUserPost,
    subscribeOrganizationAddPost,
    subscribeToUserDetailsPost,
    subscribeWorkGroupAddPost
} from './util/ajaxPostQuery';
import 'datatables.net-dt/css/jquery.dataTables.css'
import $ from 'jquery';
import 'material-dashboard/assets/css/material-dashboard.css'
import 'material-dashboard/assets/js/material-dashboard.js'
//import dt from 'datatables.net'
import dt from 'datatables.net'
let editor;
let container = $("#cards_container");
let organizationsCards = [];
let lampData = [];
let workGroupsCards = [];
let usersFromWorkGroupCards = [];
let lampsFromWorkGroupRows = [];
let cpsFromWorkGroupRows = [];
let floatingButtonAdd = $('#floating-button');
let floatingButtonBack = $('#back_button');
let regFormOrg = $('#RegOrganizationFrom');
let regFormWG = $('#RegWorkGroupFrom');
let userAddButton = $('#user_add_from');
let profileForm = $('#profileForm');
let WG_registrate = $('#registrate_workgroup');
const PageHeap = $('#page_heap');
const userButton = $("#userCheck");
const lampButton = $("#lampCheck");
const cpButton = $("#cpCheck");
const userCheck = $('#inner_userCheck');
const lampCheck = $('#inner_lampCheck');
const lampTableCard = $('#lampTableCard');
const CPTableCard = $('#CPTableCard');
const cpCheck = $('#inner_cpCheck');
const selectObjType = $('#SelectObjectType');
const userAddFrom = $('#userAddForm');
//************************************
const userInfo = $('#userEditForm');
const saveUserInfoButton = $('#save_user_form_id');
const roleSelector = $('#role_selector_id');
const groupSelector = $('#wg_selector_id');
let editableUser_id = -1;
//************************************
let heaps = ['Менеджер организации', 'Менеджер рабочих групп', 'Менеджер рабочей группы'];
let Organization_organization = $('#Organization_organization');
let currentOrganization;
let currentWorkGroup;
let organizationRegistarate = $('#organization_registration');

let netDataBtn = $('#net_data_btn');

let totalLamp;
let totalPage;
let currentPage;
/**
 *
 * Role of current User
 * @see #AdminToolController
 */
const user_role = $("meta[name='user_role']").attr("content");

let MENU_LEVELS = Object.freeze({
    "ORGANIZATIONS": 0,
    "ORGANIZATION": 1,
    "WORK_GROUP": 2
});

let UsersRang = Object.freeze({
    "[ADMIN]": 0,
    "[SuperUserOwner]": 1,
    "[SuperUser]": 2,
    "[USER]": 3
});

let currentMenuLevel;
let currentUserRang;

const token = $("meta[name='_csrf']").attr("content");
const header = $("meta[name='_csrf_header']").attr("content");


/**
 * Factory Pattern
 * @author Alex Flanker
 * @see #OrganizationInfo, #WorkGroupInfo,#UserInfo
 * @see #BaseObjDTO
 */
class InfoFactory {
    createCard(data) {
        let card;
        switch (data.type) {
            case 'OrganizationInfo':
                card = new OrganizationInfo();
                Object.assign(card, data);
                break;
            case 'WorkGroupInfo':
                card = new WorkGroupInfo();
                Object.assign(card, data);
                break;
            case 'UserInfo':
                card = new UserInfo();
                Object.assign(card, data);
        }
        return card;
    }

    createRow(data) {
        let row;
        switch (data.type) {
            case 'BaseObjDTO':
                row = new BaseObjDTO();
                Object.assign(row, data);
                break;
        }
        return row;
    }
}


const factory = new InfoFactory();


floatingButtonAdd.click(function () {
    switch (currentMenuLevel) {
        case MENU_LEVELS.ORGANIZATIONS:
            if (regFormOrg.is(':hidden')) {
                regFormOrg.show('slow');
                container.hide();

            } else {
                regFormOrg.hide();
                container.show();
            }
            break;
        case MENU_LEVELS.ORGANIZATION:

            if (regFormWG.is(':hidden')) {
                regFormWG.show('slow');
                container.hide();
            } else {
                regFormWG.hide();
                container.show();
            }

            break;
        case MENU_LEVELS.WORK_GROUP:
            if (userCheck.prop("checked")) {
                if (userAddFrom.is(':hidden')) {
                    userAddFrom.show();
                    userInfo.hide();
                    container.hide();
                    selectObjType.hide();


                } else {
                    userAddFrom.hide();
                    container.show();
                    selectObjType.show();
                }
            }

            break;
    }

});

/**
 * @author Alex Flanker
 * очищает контейнер с информацией ( карты/таблицы)
 */
function clearContainer() {
    workGroupsCards.forEach((elem) => elem.remove());
    usersFromWorkGroupCards.forEach((elem) => elem.remove());
    organizationsCards.forEach((elem) => elem.remove());
    lampsFromWorkGroupRows.forEach((elem) => elem.remove());
    cpsFromWorkGroupRows.forEach((elem) => elem.remove());
    organizationsCards = [];
    usersFromWorkGroupCards = [];
    workGroupsCards = [];
    lampsFromWorkGroupRows = [];
    cpsFromWorkGroupRows = [];
    lampTableCard.hide();
    CPTableCard.hide();
    userInfo.hide();
    userAddFrom.hide();
    profileForm.hide();
    regFormOrg.hide();
    regFormWG.hide();
    container.empty();


}

/**
 * @author Alex Flanker
 * очищает View
 */
function clearView() {
    netDataBtn.hide();
    selectObjType.hide();
    userInfo.hide();
    profileForm.hide();
    lampTableCard.hide();
    CPTableCard.hide();
    regFormWG.hide();
    userAddFrom.hide();
    clearContainer();

}

/**
 * @author Alex Flanker
 * Очищает контейнер и делает запрос новых данных на сервер
 *
 */
function updateView() {
    clearContainer();
    switch (currentMenuLevel) {
        case MENU_LEVELS.ORGANIZATIONS:
            getAllOrganizationsRequest();
            break;
        case MENU_LEVELS.ORGANIZATION:
            getWorkGroupRequest(currentOrganization);
            break;
        case  MENU_LEVELS.WORK_GROUP:

            if (userCheck.prop("checked")) {
                console.log("test !");
                if (isNumber(editableUser_id) && (editableUser_id !== -1)) {
                    if (userInfo.is(':hidden')) {
                        userInfo.show();
                        loadUserDetails(editableUser_id);
                    }
                } else {
                    getUsersRequest(currentWorkGroup);
                }
            }
            if (lampCheck.prop("checked")) {
                CPTableCard.hide();
                userAddFrom.hide();
                loadLampByWorkGroupID(currentWorkGroup);

            }
            if (cpCheck.prop("checked")) {
                lampTableCard.hide();
                userAddFrom.hide();
                loadCPByWorkGroupID(currentWorkGroup);
            }
            if(!userCheck.prop("checked") && !lampCheck.prop("checked") && !cpCheck.prop("checked")){
                userButton.toggleClass('image-checkbox-checked');
                userCheck.prop("checked",true);
                getUsersRequest(currentWorkGroup);
            }
            break;
    }
    UpdateMenu();
}

function UpdateMenu() {

    switch (currentMenuLevel) {
        case MENU_LEVELS.ORGANIZATIONS:
            PageHeap.text(heaps[currentMenuLevel]);
            netDataBtn.hide();
            selectObjType.hide();
            break;
        case MENU_LEVELS.ORGANIZATION:
            PageHeap.text(heaps[currentMenuLevel]);
            selectObjType.hide();
            if (netDataBtn.is(':hidden')) {
                netDataBtn.show();
            }
            break;
        case MENU_LEVELS.WORK_GROUP:
            PageHeap.text(heaps[currentMenuLevel]);
            netDataBtn.hide();
            if (selectObjType.is(':hidden')) {
                selectObjType.show();
            }
            break;
    }

}

function loadLampByWorkGroupID(id) {
    loadLampPage(id, 0, 0);
}

/**
 *
 * @param workGroup - id workGroup
 * @param page - какую страницу
 * @param size - элемент/страница, если 0, то 20(default)
 */

function loadCPByWorkGroupID(id) {
    loadCPPage(id, 0, 0);
}


$(".image-checkbox").each(function () {
    if ($(this).find('input[type="checkbox"]').first().attr("checked")) {
        $(this).addClass('image-checkbox-checked');
    } else {
        $(this).removeClass('image-checkbox-checked');
    }
});

//******************************************************
//              Обработчики кнопок
//******************************************************
userAddButton.on("click", function (e) {
    registateUser(currentWorkGroup);
    // UpdateMenu();
})

netDataBtn.on("click", (e) => {
    if (profileForm.is(':hidden')) {
        getNetData(currentOrganization);
        profileForm.show();
        if(regFormWG.is(":visible"))regFormWG.hide();
        container.hide();
    } else {
        container.show();
        profileForm.hide();
    }
})

saveUserInfoButton.on("click", () => {
    sendUserDetails(editableUser_id);
})

organizationRegistarate.on("click", function (e) {
    registrateOrganizationRequest();

});

WG_registrate.on("click", function (e) {
    registrateWG(currentOrganization);

});

userButton.on("click", function (e) {
    userInfo.hide();
    $(this).toggleClass('image-checkbox-checked');
    if (!userCheck.prop("checked")) {
        userCheck.prop("checked", !userCheck.prop("checked"));
        lampCheck.prop("checked", false);
        cpCheck.prop("checked", false);
        lampButton.removeClass('image-checkbox-checked');
        cpButton.removeClass('image-checkbox-checked');
        getUsersRequest(currentWorkGroup);
    } else {
        userCheck.prop("checked", !userCheck.prop("checked"));
        updateView();
    }
    e.preventDefault();
});

lampButton.on("click", function (e) {
    userInfo.hide();
    CPTableCard.hide();
    $(this).toggleClass('image-checkbox-checked');
    if (!lampCheck.prop("checked")) {
        lampCheck.prop("checked", !lampCheck.prop("checked"));
        cpCheck.prop("checked", false);
        userCheck.prop("checked", false);
        cpButton.removeClass('image-checkbox-checked');
        userButton.removeClass('image-checkbox-checked');
        updateView();
    } else {
        lampCheck.prop("checked", !userCheck.prop("checked"));
        lampTableCard.hide();

    }
    e.preventDefault();

    // updateSelector();

});

cpButton.on("click", function (e) {
    userInfo.hide();
    lampTableCard.hide();
    $(this).toggleClass('image-checkbox-checked');

    if (!cpCheck.prop("checked")) {
        cpCheck.prop("checked", !cpCheck.prop("checked"));
        userCheck.prop("checked", false);
        lampCheck.prop("checked", false);
        lampButton.removeClass('image-checkbox-checked');
        userButton.removeClass('image-checkbox-checked');
        updateView();
    } else {
        cpCheck.prop("checked", !cpCheck.prop("checked"));
        CPTableCard.hide();

    }
    e.preventDefault();
});
//******************************************
//*********На уровень меню вверх!********
function back() {
    if(userAddFrom.is(':visible')){
        $('#userAddForm_name_id').val("");
        $('#userAddForm_pass_id').val("");
        $('#userAddFrom_email_id').val("");
    }
    if (profileForm.is(':visible')) {
        $('#req_form').remove();
        $('#serviceType_id').val("");
        updateView(); return;
    }
    if(regFormOrg.is(":visible")) {
        regFormOrg.hide();
        $('#Organization_username').val("");
        $('#Organization_password').val("");
        $('#Organization_email').val("");
        $('#Organization_organization').val("");
        updateView();return;
    }
    if(regFormWG.is(":visible")){
        regFormWG.hide();
        $('#WG_username').val("");
        $('#WG_password').val("");
        $('#WG_workGroup').val("");
        $('#WG_email').val("");
        updateView();
        return;
    }
    if (currentMenuLevel > 0) {
        //Если ранг не позволяет, то не пускать
        if ((userCheck.prop("checked") && userInfo.is(':visible')) ||(userAddFrom.is(':visible') && userCheck.prop("checked"))) {
            editableUser_id = -1;

        } else if (currentUserRang < currentMenuLevel) {
            currentMenuLevel -= 1;
        }
        updateView();
    }
}


$(document).ready(function () {
    //NavBar
    $('#my_profile_btn_id').on('click',function () {
        $('#container_my_profile').show();
        $('#main_container').children().remove();
        container.hide();
        selectObjType.hide();
        floatingButtonAdd.hide();
        netDataBtn.hide();
        lampTableCard.hide();
        CPTableCard.hide();
        profileForm.hide();
        $('#page_heap').hide();
        $('#struct_parent_id').removeClass('active');
        $('#main_info_').removeClass('active');
        $('#my_profile_parent_btn').addClass('active');
    });

    $('#struct_btn_id').on('click',function () {
        $('#container_my_profile').hide();
        $('#main_container').children().remove();
        container.show();
        floatingButtonAdd.show();
        $('#page_heap').show();
        updateView();
        $('#my_profile_parent_btn').removeClass('active');
        $('#main_info_').removeClass('active');
        $('#struct_parent_id').addClass('active');
    });
    $('#profileBtn_id').on('click',function () {
        console.log('test click');
        updateUserProp();
    });
    $('#main_info_btn').on('click',function () {
        container.hide();
        selectObjType.hide();
        floatingButtonAdd.hide();
        netDataBtn.hide();
        lampTableCard.hide();
        CPTableCard.hide();
        profileForm.hide();
        $('#container_my_profile').hide();
        $('#struct_parent_id').removeClass('active');
        $('#my_profile_parent_btn').removeClass('active');
        $('#main_info_').addClass('active');
        loadActiveUsers();
    });
    //******************************************
    //*******Обработчики CallBack***************
    //******************************************
    /**@see #ajaxQuery.getWorkGroup()
     *
     * Call back для REST запроса getWorkGroup()
     */
    subscribeToWG(responce => {
        console.log("success : ", JSON.stringify(responce));
        responce.groupInfoList.forEach(function (workGroup) {
            let card = factory.createCard(workGroup);
            workGroupsCards.push(card.getCard());
        });
        currentMenuLevel = MENU_LEVELS.ORGANIZATION;
        container.append(workGroupsCards);
        container.show();


    });

    //*************************************
    //*******Все активные пользователи*****

    subscribeToActiveUsers(responce =>{
        container.hide();
        selectObjType.hide();
        floatingButtonAdd.hide();
        netDataBtn.hide();
        lampTableCard.hide();
        CPTableCard.hide();
        profileForm.hide();
        $('#page_heap').hide();
        $('#main_container').append(getActiveUsersTable(responce))
    });
    //********************
    /**@see #ajaxQuery.getUsersRequest()
     * Call back для REST запроса getUsersRequest()
     */
    subscribeToUser(responce => {
        console.log("success Users: ", JSON.stringify(responce));
        let data = responce.data;
        data.forEach(function (data) {
            let card = factory.createCard(data);
            usersFromWorkGroupCards.push(card.getCard());
        });
        lampTableCard.hide();
        CPTableCard.hide();
        container.append(usersFromWorkGroupCards);
        container.show();
    });

    //********************
    //********************
    /**@see #ajaxQuery.getOrganizationsRequest()
     * Call back для REST запроса getOrganizationsRequest()
     */
    subscribeToOrganizations(responce => {
        console.log("success : ", JSON.stringify(responce));
        responce.infoList.forEach(function (organization) {
            let card = factory.createCard(organization);
            organizationsCards.push(card.getCard());
        });
        currentMenuLevel = MENU_LEVELS.ORGANIZATIONS;
        container.append(organizationsCards);
        container.show();

    });
    //******************************************

    //********************
    /**@see #ajaxQuery.loadLampPage()
     * Call back для REST запроса loadLampPage()
     */
    subscribeToLamp(responce => {
        console.log("success: ", JSON.stringify(responce));
        let data = responce.data;
        totalLamp = responce.totalElem;
        totalPage = responce.totalPage;
        currentPage = responce.currentPage;
        let dataSet = [];
        let dataSlice = [];
        responce.data.forEach(elem => {
            dataSlice = [];
            dataSlice.push(elem.id);
            dataSlice.push(elem.name);
            dataSlice.push(elem.alias);
            dataSlice.push(elem.group);
            dataSlice.push(elem.latitude);
            dataSlice.push(elem.longitude);
            dataSet.push(dataSlice);
        });
        let table;
        if ($.fn.dataTable.isDataTable('#lamp_table_id')) {

            table = $('#lamp_table_id').DataTable();
            table.clear();
            table.rows.add(dataSet);
            table.columns.adjust().draw();
        } else {

            table = $('#lamp_table_id').DataTable({
                data: dataSet,
                "language": {
                    "url": "//cdn.datatables.net/plug-ins/1.10.19/i18n/Russian.json"
                },
                columns: [
                    {title: "id"},
                    {title: "EUI"},
                    {title: "Alias"},
                    {title: "Группа"},
                    {title: "Широта"},
                    {title: "Долгота"},
                    {defaultContent: '<a href="" class="editor_remove" title="Удалить" style="color: red">Х</a>'}
                ]
            });
        }
        if (lampTableCard.is(':hidden')) {
            lampTableCard.show();
        }

        $('#lamp_table_id').on('click', 'a.editor_remove', function (e) {
            e.preventDefault();
            let row = $(this).parent().parent();
            let id = row[0].cells[0].innerText;
            deleteLamp(id);
            table.row(row).remove().draw();
        });
        container.show();
    });

//******************************************
//********************
    /**@see #ajaxQuery.loadCPPage()
     * Call back для REST запроса loadCPPage()
     */
    subscribeToCP(responce => {
        console.log("success: ", JSON.stringify(responce));
        let data = responce.data;
        let dataSet = [];
        let dataSlice = [];
        responce.data.forEach(elem => {
            dataSlice = [];
            dataSlice.push(elem.id);
            dataSlice.push(elem.name);
            dataSlice.push(elem.latitude);
            dataSlice.push(elem.longitude);
            dataSet.push(dataSlice);
        });
        let table;
        if ($.fn.dataTable.isDataTable('#cp_table_id')) {
            table = $('#cp_table_id').DataTable();
            table.clear();
            table.rows.add(dataSet);
            table.columns.adjust().draw();
        } else {
            table = $('#cp_table_id').DataTable({
                data: dataSet,
                "language": {
                    "url": "//cdn.datatables.net/plug-ins/1.10.19/i18n/Russian.json"
                },
                columns: [
                    {title: "id"},
                    {title: "Название"},
                    {title: "Широта"},
                    {title: "Долгота"},
                    {defaultContent: '<a href="" class="editor_remove" title="Удалить" style="color: red">Х</a>'}
                ]
            });
        }
        if (CPTableCard.is(':hidden')) {
            CPTableCard.show();
        }
        $('#cp_table_id').on('click', 'a.editor_remove', function (e) {
            e.preventDefault();
            let row = $(this).parent().parent();
            let id = row[0].cells[0].innerText;
            deleteCP(id);
            table.row(row).remove().draw();

        });
        container.show();
    });
//******************************************
//********************
    /**@see #ajaxQuery.()
     * Call back для REST запроса ()
     */
    subscribeToUserDetails(responce => {
        let ownPropertyNames = Object.getOwnPropertyNames(UsersRang);
        roleSelector.find('option').remove();
        ownPropertyNames.forEach((value, index) => {
            roleSelector.append(new Option(value, index));
        });
        $('#username_id').val(responce.username);
        $('#email').val(responce.email);

        let item = '[' + responce.roles + ']';
        roleSelector.val(UsersRang[item]);
        console.log(JSON.stringify(responce));
        selectObjType.hide();
        if(userAddFrom.is(':visible')){
            userAddFrom.hide();
        }

    });
//******************************************
//********************
    /**@see #ajaxQuery.sendUserDetails()
     * Call back для REST запроса sendUserDetails()
     */

    subscribeToUserDetailsPost(responce => {
        editableUser_id = -1;
        let role_sel = $('#role_selector_id');
        if (responce.status == "403") {
            // alert('У вас недостаточно привелегий!');
            role_sel.addClass("is-invalid");
            role_sel.on("change", (() => role_sel.removeClass("is-invalid")));
        } else if (responce.status == "400") {
            $('#user_pass_rep_id').addClass("is-invalid");
        } else
            selectObjType.show();
            updateView();
    });
//******************************************
//********************
    /**@see #ajaxQuery.deleteUser()
     * Call back для REST запроса deleteUser()
     */
    subscribeToDelUser(responce => {
        if (responce.hasOwnProperty("wg")) {
            if (responce.wg == "true") {
                back();
            } else {
                updateView();
            }
            return;
        }

        if (responce.status == "403" || responce.status == "406") {
            alert('У вас недостаточно полномочий!');
        }
        if (responce.status == "409") {
            alert('Нельзя удалить себя!');
        }
        updateView();


    });
    /**@see #ajaxQuery.deleteWorkGroup()
     * Call back для REST запроса deleteWorkGroup()
     */
    subscribeToDelWG(responce => {
        console.log(JSON.stringify(responce.responseText))
        if (responce.status == "403" || responce.status == "406") {
            alert('У вас недостаточно полномочий!');
        }
        updateView();
    });
//******************************************
//********************
    /**@see #ajaxQuery.registrateWG()
     /**@see #ajaxQuery.registrateOrganizationRequest()
     /**@see #ajaxQuery.registateUser()
     * Call back для REST запроса
     */
    subscribeWorkGroupAddPost(responce => {
        console.log(responce);
        let WG_username = $('#WG_username');
        let WG_password = $('#WG_password');
        let WG_workGroup = $('#WG_workGroup');
        let WG_email = $('#WG_email');
        if (responce.status == "ok") {
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

            WG_username.val("");
            WG_password.val("");
            WG_workGroup.val("");
            WG_email.val("");
            updateView();
            UpdateMenu();
        } else if (responce.status == "406") {
            let answer = JSON.parse(responce.responseText);
            Object.getOwnPropertyNames(answer).forEach((prop) => {
                switch (prop) {
                    case "passworderror":
                        WG_password.addClass("is-invalid");
                        $("#WG_password_invalid").text(answer.passworderror);
                        break;
                    case "nameOfWorkGroup":
                        WG_workGroup.addClass("is-invalid");
                        $("#WG_workGroup_invalid").text(answer.nameOfWorkGroup);
                        break;
                        case "workGrouperror":
                        WG_workGroup.addClass("is-invalid");
                        $("#WG_workGroup_invalid").text(answer.workGrouperror);
                        break;
                    case "user":
                        WG_username.addClass("is-invalid");
                        $('#wg_username_invalid').text(answer.user);
                        break;
                    case "workGrouperror":
                        WG_workGroup.addClass("is-invalid");
                        break;
                    case "usernameerror":
                        $("#wg_username_invalid").text(answer.usernameerror);
                        WG_username.addClass("is-invalid");
                        break;
                    case "emailerror":
                        WG_email.addClass("is-invalid");
                        $('#WG_email_invalid').text(answer.emailerror);
                        break;
                }
            });
        }

    });
    subscribeOrganizationAddPost(responce => {
        let Organization_username = $('#Organization_username');
        let Organization_password = $('#Organization_password');
        let Organization_email = $('#Organization_email');
        let Organization_organization = $('#Organization_organization');
        if (responce.status == "ok") {
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
            Organization_username.val("");
            Organization_password.val("");
            Organization_email.val("");
            Organization_organization.val("");
            updateView();
            UpdateMenu();
        } else if (responce.status == "406") {
            let answer = JSON.parse(responce.responseText);
            console.log(answer);
            Object.getOwnPropertyNames(answer).forEach((prop) => {
                switch (prop) {
                    case "passworderror":
                        Organization_password.addClass("is-invalid");
                        $("#Organization_password_invalid").text(answer.passworderror);
                        break;
                    case "organizationerror":
                        Organization_organization.addClass("is-invalid");
                        $("#Organization_organization_invalid").text(answer.organizationerror);
                        break;
                    case "nameOfOrganization":
                        Organization_organization.addClass("is-invalid");
                        $("#Organization_organization_invalid").text(answer.nameOfOrganization);
                        break;
                    case "usernameerror":
                        Organization_username.addClass("is-invalid");
                        $("#Organization_username_invalid").text(answer.usernameerror);
                        break;
                    case "user":
                        Organization_username.addClass("is-invalid");
                        $("#Organization_username_invalid").text(answer.user);
                        break;
                    case "emailerror":
                        Organization_email.addClass("is-invalid");
                        $("#Organization_email_invalid").text(answer.emailerror);
                        break;
                    case "user":
                        Organization_username.addClass("is-invalid");
                        break;
                }
            });
        }


    });
    subscribeAddUserPost(responce => {
        let username = $('#userAddForm_name_id');
        let password = $('#userAddForm_pass_id');
        let email = $('#userAddFrom_email_id');

        if (responce.status == "ok") {
            if (username.hasClass("is-invalid")) {
                username.removeClass("is-invalid");
            }

            if (password.hasClass("is-invalid")) {
                password.removeClass("is-invalid");
            }

            if (email.hasClass("is-invalid")) {
                email.removeClass("is-invalid");
            }

            username.val("");
            password.val("");
            email.val("");

            updateView();
            UpdateMenu();
        } else if (responce.status == "406") {
            let answer = JSON.parse(responce.responseText);
            Object.getOwnPropertyNames(answer).forEach((prop) => {
                switch (prop) {
                    case "usernameerror":
                        username.addClass("is-invalid");
                        $("#userAddForm_name_id_invalid").text(answer.usernameerror);
                        break;
                    case "user":
                        username.addClass("is-invalid");
                        $("#userAddForm_name_id_invalid").text(answer.user);
                        break;
                    case "passworderror":
                        password.addClass("is-invalid");
                        $("#userAddForm_pass_id_invalid").text(answer.passworderror);
                        break;
                    case "emailerror":
                        email.addClass("is-invalid");
                        $("#userAddFrom_email_id_invalid").text(answer.emailerror);
                        break;
                    case "workroup":
                        alert("Группа уже удалена! Нельзя добавить пользователя!");
                        break;
                }
            });
        }

    });


//******************************************
//*******Обработчики CallBack***************
//****************END***********************
//******************************************

    floatingButtonBack.click(function () {
        back();
    });

    switch (user_role) {
        case '[ADMIN]':
            currentMenuLevel = MENU_LEVELS.ORGANIZATIONS;
            currentUserRang = UsersRang["[ADMIN]"];
            break;
        case '[SuperUserOwner]':
            currentMenuLevel = MENU_LEVELS.ORGANIZATION;
            currentUserRang = UsersRang["[SuperUserOwner]"];
            break;
        case '[SuperUser]':
            currentMenuLevel = MENU_LEVELS.WORK_GROUP;
            currentUserRang = UsersRang["[SuperUser]"];
            break;
    }
    updateView();
    if (currentOrganization !== undefined)
        getNetData(currentOrganization);
});

/**
 * @author Alex Flanker
 * перехватчик событий нажатия кнопок
 */
container.on('click', function (e) {
    let target = e.target.id;
    if (typeof target != "undefined" || strValue !== "") {
        switch (currentMenuLevel) {
            case MENU_LEVELS.ORGANIZATIONS:
                if (target.startsWith('button_org_')) {
                    let org = target.substring('button_org_'.length);
                    if (!isNaN(parseFloat(org)) && isFinite(org)) {
                        currentOrganization = org;
                        currentMenuLevel = MENU_LEVELS.ORGANIZATION;
                        updateView();
                    }
                }
                break;
            case MENU_LEVELS.ORGANIZATION:
                if (target.startsWith('button_wg_')) {
                    let wg = target.substring('button_wg_'.length);
                    if (!isNaN(parseFloat(wg)) && isFinite(wg)) {
                        console.log(wg);
                        currentMenuLevel = MENU_LEVELS.WORK_GROUP;
                        currentWorkGroup = wg;
                        updateView();
                    }
                }
                if (target.startsWith('button_wg_del')) {
                    let wg = target.substring('button_wg_del'.length);
                    if (!isNaN(parseFloat(wg)) && isFinite(wg)) {
                        deleteWorkGroup(wg);
                    }
                }
                break;
            case MENU_LEVELS.WORK_GROUP:
                let usr_id;
                if (target.startsWith('button_usr_')) {
                    usr_id = target.substring('button_usr_'.length);
                    if (!isNaN(parseFloat(usr_id)) && isFinite(usr_id)) {
                        console.log(usr_id);
                        editableUser_id = usr_id;
                        updateView();
                    }
                } else if (target.startsWith('button_del_usr_')) {
                    usr_id = target.substring('button_del_usr_'.length);
                    if (!isNaN(parseFloat(usr_id)) && isFinite(usr_id)) {
                        console.log(usr_id);
                        if (confirm('Вы действительно хотите удалить пользователя?'))
                            deleteUser(usr_id);
                    }
                }

                break;
            default:
                break;
        }

    }
});
profileForm.on('click', function (e) {
    let target = e.target.id;
    // if(e.target.id === 'serviceType_id'){
    //     getNetData(currentOrganization);
    // }
    if (e.target.id === 'sendProfile_id') {
        sendForm(currentOrganization);
        getNetData(currentOrganization);
    }
});

//ToDo: перенести в POST util
function updateUserProp() {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
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
            console.log(response.toString());
            $('#username').addClass("is-invalid");
            $('#email').addClass("is-invalid");
            $('#pass').addClass("is-invalid");
            $('#rep_pass').addClass("is-invalid");

        }
    });
}

