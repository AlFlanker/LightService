import $ from 'jquery'

export function WorkGroupInfo() {
    let current = this;
    let obj;
    this.getWorkGroup = function () {
        return obj;
    }
    this.setStategy = function (raw) {
        obj = raw;
    }
    this.getCard = function () {
        return getWorkGroupCard();
    }

    function getWorkGroupCard() {

        let btn_edit = $('<button></button>', {
            class: 'btn btn-info btn-sm',
            text: 'Подробнее',
            id: 'button_wg_' + current.id
        });
        let btn_del = $('<button></button>', {
            class: 'btn btn-danger btn-sm',
            text: 'Удалить',
            id: 'button_wg_del' + current.id
        });
        let btn_group = $('<div></div>', {
            class: 'btn-group mt-2'
        });
        btn_group.append(btn_edit);
        btn_group.append(btn_del);

        let buttomInfo = $('<div></div>', {
            class: 'd-flex justify-content-end align-items-center',
        });


        buttomInfo.append(btn_group);


        let li_group = getWorkGroupListGroup();

        let list_group = $('<ul>', {
            class: 'list-group'
        });
        li_group.forEach(function (item, i, arr) {
            list_group.append(item);
        });
        let rect_svg = $('<rect></rect>', {
            fill: '#868e96',
            width: '100%',
            height: '100%'
        });
        let rect_title = $('<title></title>', {
            text: 'Placeholder'
        });
        let svg_img = $('<svg></svg>', {
            class: 'bd-placeholder-img card-img-top',
            width: '100%',
            height: '200',
            preserveAspectRatio: 'xMidYMid slice',
            focusable: 'false',
            role: 'img',
            xmlns: 'http://www.w3.org/2000/svg'
        });

        svg_img.append(rect_title);
        svg_img.append(rect_svg);

        let cardBodyTitle = $('<h5></h5>', {
            text: 'Информация:'
        });

        let cardBody = $('<div></div>', {
            class: 'card-body'
        });
        let text_muted = $('<small></small>', {
            class: 'text-muted',
            text: 'Администратор: ' + current.wgFounder
        });
        // cardBody.append(svg_img);
        cardBody.append(cardBodyTitle);
        cardBody.append(text_muted);
        cardBody.append(list_group);
        cardBody.append(buttomInfo);

        let cardHeader = $('<div></div>', {
            class: 'card-header card-header-primary',
            style: 'background-color: rgba(52, 58, 64, 0.8)',
            text: current.name
        });

        let card = $('<div></div>', {
            class: 'card card-nav-tabs text-center',
            style: ' box-shadow: 0 1rem 2rem rgba(0,0,0,0.5), 0 0.5rem 0.7rem rgba(0,0,0,0.5)'
        });
        card.append(cardHeader);
        card.append(cardBody);
        card.hover(function () {

            },
            function () {

            });
        let col = $('<div></div>', {
            class: 'col-md-4 col-lg-4  p-3',
            id: current.name + '_' + current.id
        });

        col.append(card);
        return col;
    }

    function getWorkGroupListGroup() {
        let workGroup_sp;
        let workGroup_li = [];
        workGroup_sp = ($('<span></span>', {
            class: 'badge badge-primary badge-pill',
            text: current.userCount
        }));
        workGroup_li.push($('<li></li>', {
            class: 'list-group-item d-flex justify-content-between align-items-center',
            text: 'Пользователей:'
        }).append(workGroup_sp));

        workGroup_sp = ($('<span></span>', {
            class: 'badge badge-primary badge-pill',
            text: current.lampCount
        }));
        workGroup_li.push($('<li></li>', {
            class: 'list-group-item d-flex justify-content-between align-items-center',
            text: 'Светильники:'
        }).append(workGroup_sp));

        workGroup_sp = ($('<span></span>', {
            class: 'badge badge-primary badge-pill',
            text: current.cpCount
        }));
        workGroup_li.push($('<li></li>', {
            class: 'list-group-item d-flex justify-content-between align-items-center',
            text: 'КП:'
        }).append(workGroup_sp));

        return workGroup_li;
    }
}

export function OrganizationInfo() {
    let current = this;
    let obj;
    this.getOrganization = function () {
        return obj;
    }
    this.setStategy = function (raw) {
        obj = raw;
    }
    this.getCard = function () {
        return getOrganizationCard();
    }

    function getOrganizationCard() {
        let text_muted = $('<small></small>', {
            class: 'text-muted',
            text: current.founderUser
        });
        let btn_edit = $('<button></button>', {
            class: 'btn btn-primary btn-sm',
            text: 'Подробнее',
            id: 'button_org_' + current.id
        });
        let btn_group = $('<div></div>', {
            class: 'btn-group mt-2'
        });
        btn_group.append(btn_edit);

        let buttomInfo = $('<div></div>', {
            class: 'd-flex justify-content-between align-items-center'
        });

        buttomInfo.append(btn_group);
        buttomInfo.append(text_muted);

        let li_group = getOrganizationListGroup();

        let list_group = $('<ul>', {
            class: 'list-group'
        });
        li_group.forEach(function (item, i, arr) {
            list_group.append(item);
        });
        let rect_svg = $('<rect></rect>', {
            fill: '#868e96',
            width: '100%',
            height: '100%'
        });
        let rect_title = $('<title></title>', {
            text: 'Placeholder'
        });
        let svg_img = $('<svg></svg>', {
            class: 'bd-placeholder-img card-img-top',
            width: '100%',
            height: '200',
            preserveAspectRatio: 'xMidYMid slice',
            focusable: 'false',
            role: 'img',
            xmlns: 'http://www.w3.org/2000/svg'
        });

        svg_img.append(rect_title);
        svg_img.append(rect_svg);

        let cardBodyTitle = $('<h5></h5>', {
            text: 'Информация:'
        });

        let cardBody = $('<div></div>', {
            class: 'card-body'
        });
        cardBody.append(svg_img);
        cardBody.append(cardBodyTitle);
        cardBody.append(list_group);
        cardBody.append(buttomInfo);

        let cardHeader = $('<div></div>', {
            class: 'card-header card-header-primary',
            style: 'background-color: rgba(52, 58, 64, 0.8)',
            text: current.name
        });

        let card = $('<div></div>', {
            class: 'card card-nav-tabs text-center',
            style: ' box-shadow: 0 1rem 2rem rgba(0,0,0,0.5), 0 0.5rem 0.7rem rgba(0,0,0,0.5)'
        });
        card.append(cardHeader);
        card.append(cardBody);
        card.hover(function () {

            },
            function () {

            });
        let col = $('<div></div>', {
            class: 'col-md-4 col-lg-4 p-3',
            id: current.name + '_' + current.id
        });

        col.append(card);

        return col;
    }

    function getOrganizationListGroup() {
        let workGroup_sp;
        let workGroup_li = [];
        workGroup_sp = ($('<span></span>', {
            class: 'badge badge-primary badge-pill',
            text: current.workGroupCount
        }));
        workGroup_li.push($('<li></li>', {
            class: 'list-group-item d-flex justify-content-between align-items-center',
            text: 'Рабочие группы:'
        }).append(workGroup_sp));

        workGroup_sp = ($('<span></span>', {
            class: 'badge badge-primary badge-pill',
            text: current.usersCount
        }));
        workGroup_li.push($('<li></li>', {
            class: 'list-group-item d-flex justify-content-between align-items-center',
            text: 'Пользователи:'
        }).append(workGroup_sp));

        workGroup_sp = ($('<span></span>', {
            class: 'badge badge-primary badge-pill',
            text: current.lampCount
        }));
        workGroup_li.push($('<li></li>', {
            class: 'list-group-item d-flex justify-content-between align-items-center',
            text: 'Светильники:'
        }).append(workGroup_sp));

        workGroup_sp = ($('<span></span>', {
            class: 'badge badge-primary badge-pill',
            text: current.cpCount
        }));
        workGroup_li.push($('<li></li>', {
            class: 'list-group-item d-flex justify-content-between align-items-center',
            text: 'КП:'
        }).append(workGroup_sp));

        return workGroup_li;
    }

}


export function UserInfo() {
    let current = this;
    let obj;
    this.getUser = function () {
        return obj;
    }
    this.setStategy = function (raw) {
        obj = raw;
    }
    this.getCard = function () {
        return getUserCard();
    }

    function getUserCard() {
        let id = current.id;

        let btn_delete = $('<button></button>', {
            id: 'button_del_usr_' + current.id,
            class: 'btn btn-danger btn-sm',
            text: 'Удалить'
        })

        let btn_edit = $('<button></button>', {
            id: 'button_usr_' + current.id,
            class: 'btn btn-info btn-sm ',
            text: 'Подробнее'
        });
        let btn_col1 = $('<div></div>', {
            class: 'col-md-6 col-lg-6'
        });

        let btn_col2 = $('<div></div>', {
            class: 'col-md-6 col-lg-6'
        });


        btn_col1.append(btn_edit);
        btn_col2.append(btn_delete);


        let buttonGroup = $('<div></div>', {
            class: 'row justify-content-between',

        });

        buttonGroup.append(btn_col1);
        buttonGroup.append(btn_col2);


        let li_group = getUserListGroup();

        let list_group = $('<ul>', {
            class: 'list-group'
        });
        li_group.forEach(function (item, i, arr) {
            list_group.append(item);
        });

        let cardBodyTitle = $('<h5></h5>', {
            text: 'Информация:'
        });

        let cardBody = $('<div></div>', {
            class: 'card-body'
        });

        cardBody.append(cardBodyTitle);
        cardBody.append(list_group);
        let cardBody_btn = $('<div></div>', {
            class: 'card-body'
        });
        cardBody_btn.append(buttonGroup);

        let cardHeader = $('<div></div>', {
            class: 'card-header card-header-primary',
            style: 'background-color: rgba(52, 58, 64, 0.8)',
            text: ''
        });

        let card = $('<div></div>', {
            class: 'card card-nav-tabs text-center',
            style: ' box-shadow: 0 1rem 2rem rgba(0,0,0,0.5), 0 0.5rem 0.7rem rgba(0,0,0,0.5);width: 15rem;'
        });
        card.append(cardHeader);
        card.append(cardBody);
        card.append(cardBody_btn);
        card.hover(function () {

            },
            function () {

            });
        let col = $('<div></div>', {
            class: 'col-md-4 col-lg-4 p-3',
            id: current.name + '_' + current.id
        });

        col.append(card);
        return col;
    }

    function getUserListGroup() {
        let user_sp;
        let user_li = [];
        user_sp = ($('<span></span>', {
            class: 'badge badge-primary badge-pill',
            text: current.username
        }));
        user_li.push($('<li></li>', {
            class: 'list-group-item d-flex justify-content-between align-items-center',
            text: 'Username:'
        }).append(user_sp));

        user_sp = ($('<span></span>', {
            class: 'badge badge-primary badge-pill',
            text: current.role
        }));
        user_li.push($('<li></li>', {
            class: 'list-group-item d-flex justify-content-between align-items-center',
            text: 'Роль:'
        }).append(user_sp));

        return user_li;
    }
}

export function BaseObjDTO() {
    let current = this;
    let obj;
    this.getLamp = function () {
        return obj;
    }
    this.setStategy = function (raw) {
        obj = raw;
    }
    this.getRow = function () {
        return getRow();
    }

    function getRow() {
        let td = [];
        let row = $('<tr>');
        td.push($('<td>').text(current.id));
        row.append(td);
        td.push($('<td>').text(current.name));
        row.append(td);

        td.push($('<td>').text(current.alias));
        row.append(td);
        td.push($('<td>').text(current.group));
        row.append(td);
        td.push($('<td>').append($('<a>', {href: '#', text: ''}).append($('<img>', {
            alt: 'map',
            src: 'https://material.io/tools/icons/static/icons/baseline-place-24px.svg',
            style: ''
        }))));
        row.append(td);
        return row;
    }
}

export function getActiveUsersTable(users) {
    let body = $('<div></div>', {
        class: 'col-md-6 col-lg-12',
        id: 'active_users_table'
    });
    let inner_card = $('<div></div>', {
        class: "card"
    });
    let card_header = $('<div></div>', {
        class: 'card-header card-header-warning',
        text: "Активные пользователи"
    });
    let card_body = $('<div></div>', {
        class: 'card-body table-responsive'
    });

    let table = $('<table></table>', {
        class: "table table-hover"
    });

    let table_head = $('<thead></thead>', {
        class: "text-warning"
    });

    let tr = $('<tr></tr>');
    tr.append($('<th></th>', {text: 'Имя пользователя'}));
    tr.append($('<th></th>', {text: 'Роль'}));
    tr.append($('<th></th>', {text: 'Организация'}));
    tr.append($('<th></th>', {text: 'Рабочая группа'}));
    table_head.append(tr);

    let tbody = $('<tbody></tbody>');
    tr = $('<tr></tr>');
    users.forEach(function (user) {
        tr = $('<tr></tr>');
        tr.append($('<th></th>', {text: user.username}));
        tr.append($('<th></th>', {text: user.role}));
        tr.append($('<th></th>', {text: user.organization}));
        tr.append($('<th></th>', {text: user.workGroup}));
        tbody.append(tr);
    });

    table.append(table_head).append(tbody);
    card_body.append(table);
    inner_card.append(card_header).append(card_body);
    body.append(inner_card);
    return body;
}
