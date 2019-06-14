/**
 * Диалог регистрации лампы Новый
 */

function createRegistrationDialog() {
    let selected;
    let main_cont = $('<div></div>', {
        class: 'w-100 p-3'
    });
    let wg_selector;
    let card_header = $('<div></div>', {
        class: 'card-header'
    });
    let headersText = $('<h5></h5>', {
        text: 'Регистрационные данные:'
    });
    card_header.append(headersText);
    let card = $('<div></div>', {
        class: "card w-100 p-3",
    });

    card.attr("display", "inline-block");
    let card_body = $('<div></div>', {
        class: "card-body",
        id: 'main_info_popup_body'
    });
    let form = $('<form></form>', {
        class: "was-validated"
    });

    let table = $('<div></div>', {
        class: "container"
    });

    let table_row;
    let cell_1;
    let cell_2;
    table.append($('<div></div>', {
        class: 'dropdown-divider'
    }));
//******************************************
//*****************Организация**************
    if (user_role === "[ADMIN]") {
        let orgs_selector = $('<select></select>', {
            class: 'custom-select custom-select-sm',
            id: 'orgs_sel_id'
        });

        cell_1 = $('<div></div>', {
            class: "col-lg-4 col-md-5",
            text: "Организация:"
        });
        cell_2 = $('<div></div>', {
            class: "col-lg-8 col-md-7",
        });
        if (organizations.size !== 0) {
            organizations.forEach((value => {
                let elem = $('<option></option>', {
                    value: value.id,
                    label: value.name,
                    text:value.name
                });
                orgs_selector.append(elem);
            }));
            cell_2.append(orgs_selector);
            table.append($('<div></div>', {
                class: 'dropdown-divider'
            }));
            table_row = $('<div></div>', {class: "row justify-content-start"});

            let id = orgs_selector.val();
            selected = undefined;
            organizations.forEach(value => {
                if (value.id == id) {
                    selected = value;
                    return;
                }
            });

        } else {
            cell_2.append("нет активных организации");
        }
        table_row = $('<div></div>', {class: "row justify-content-start"});
        // table.append($('<div></div>', {
        //     class: 'dropdown-divider'
        // }));
        table_row.append(cell_1).append(cell_2);
        table.append(table_row);


        if (selected !== undefined) {
            let wg = undefined;
            wg_selector = $('<select></select>', {
                class: 'custom-select custom-select-sm',
                id: 'wg_sel_id'
            });
            selected.workGroups.forEach((value) => {
                let elem = $('<option></option>', {
                    value: value.id,
                    label: value.name,
                    text:value.name
                });
                wg_selector.append(elem);
            });
            cell_1 = $('<div></div>', {
                class: "col-lg-4 col-md-5",
                text: "Рабочая Группа:"
            });
            cell_2 = $('<div></div>', {
                class: "col-lg-8 col-md-7",
            });
            cell_2.append(wg_selector);
            table_row = $('<div></div>', {class: "row justify-content-start"});
            table.append($('<div></div>', {
                class: 'dropdown-divider'
            }));
            table_row.append(cell_1).append(cell_2);
            table.append(table_row);
            table.append($('<div></div>', {
                class: 'dropdown-divider'
            }));
//*********************************************
//******************Группы*********************
            let selected_wg = wg_selector.val();


            selected.workGroups.forEach((value) => {
                if (value.id == selected_wg) {
                    selected_wg = value;
                }
            });

            let group_selector = $('<select></select>', {
                class: 'custom-select custom-select-sm',
                id: 'reg_group_sel_id'
            });


            let def = $('<option></option>', {
                value: 0,
                label: 'без группы',
                text: 'без группы'
            });

            selected_wg.groups.forEach((value) => {
                let elem = $('<option></option>', {
                    value: value.id,
                    label: value.name,
                    text:value.name
                });
                group_selector.append(elem);
            });
            group_selector.append(def);

            cell_1 = $('<div></div>', {
                class: "col-lg-4 col-md-5",
                text: "Группа:"
            });
            cell_2 = $('<div></div>', {
                class: "col-lg-8 col-md-7",
            });
            cell_2.append(group_selector);
            table_row = $('<div></div>', {class: "row justify-content-start"});
            table.append($('<div></div>', {
                class: 'dropdown-divider'
            }));
            table_row.append(cell_1).append(cell_2);
            table.append(table_row);
            table.append($('<div></div>', {
                class: 'dropdown-divider'
            }));


        }


    } else if (user_role === "[SuperUserOwner]") {
        let wg_selector = $('<select></select>', {
            class: 'custom-select custom-select-sm',
            id: 'wg_sel_id'
        });
        let it = organizations.values();
        let first = it.next().value;
        first.workGroups.forEach((value) => {

            let elem = $('<option></option>', {
                value: value.id,
                label: value.name,
                text:value.name
            });

            wg_selector.append(elem);
        });
        cell_1 = $('<div></div>', {
            class: "col-lg-4 col-md-5",
            text: "Рабочая Группа:"
        });
        cell_2 = $('<div></div>', {
            class: "col-lg-8 col-md-7",
        });
        cell_2.append(wg_selector);
        table_row = $('<div></div>', {class: "row justify-content-start"});
        table.append($('<div></div>', {
            class: 'dropdown-divider'
        }));
        table_row.append(cell_1).append(cell_2);
        table.append(table_row);
        table.append($('<div></div>', {
            class: 'dropdown-divider'
        }));
//*********************************************
//******************Группы*********************
        let selected_wg = wg_selector.val();

        first.workGroups.forEach((value) => {
            if (value.id == selected_wg) {
                selected_wg = value;
            }
        });

        let group_selector = $('<select></select>', {
            class: 'custom-select custom-select-sm',
            id: 'reg_group_sel_id'
        });


        let def = $('<option></option>', {
            value: 0,
            label: 'без группы',
            text: 'без группы'
        });

        selected_wg.groups.forEach((value) => {
            let elem = $('<option></option>', {
                value: value.id,
                label: value.name,
                text:value.name
            });
            group_selector.append(elem);
        });
        group_selector.append(def);

        cell_1 = $('<div></div>', {
            class: "col-lg-4 col-md-5",
            text: "Группа:"
        });
        cell_2 = $('<div></div>', {
            class: "col-lg-8 col-md-7",
        });
        cell_2.append(group_selector);
        table_row = $('<div></div>', {class: "row justify-content-start"});
        table.append($('<div></div>', {
            class: 'dropdown-divider'
        }));
        table_row.append(cell_1).append(cell_2);
        table.append(table_row);
        table.append($('<div></div>', {
            class: 'dropdown-divider'
        }));
    } else if (user_role === "[SuperUser]") {
        let group_selector = $('<select></select>', {
            class: 'custom-select custom-select-sm',
            id: 'reg_group_sel_id'
        });
        let def = $('<option></option>', {
            value: 0,
            label: 'без группы',
            text: 'без группы'
        });
        mapOfGroups.forEach((value, key) => {
            let elem = $('<option></option>', {
                value: value,
                label: key,
                text:key
            });
            group_selector.append(elem);
        });
        group_selector.append(def);

        cell_1 = $('<div></div>', {
            class: "col-lg-4 col-md-5",
            text: "Группа:"
        });
        cell_2 = $('<div></div>', {
            class: "col-lg-8 col-md-7",
        });
        cell_2.append(group_selector);
        table_row = $('<div></div>', {class: "row justify-content-start"});
        table.append($('<div></div>', {
            class: 'dropdown-divider'
        }));
        table_row.append(cell_1).append(cell_2);
        table.append(table_row);
        table.append($('<div></div>', {
            class: 'dropdown-divider'
        }));
    }
//*********************************************
//********************EUI**********************
    table_row = $('<div></div>', {class: "row justify-content-between"});
    table_row.attr("display", "inline-block");
    cell_1 = $('<div></div>', {
        class: "col-lg-2 col-md-3",
        text: "EUI: "
    });
    cell_2 = $('<div></div>', {
        class: "col-lg-10 col-md-9",
    });
    let eui_input = $('<input/>', {
        id: 'new_reg_eui_id',
        class: "form-control form-control-sm"
    });
    eui_input.attr('type', 'text');
    eui_input.attr('placeholder', 'EUI');
    // eui_input.attr('pattern', '(([0-9a-fA-F]{2})-){7}([0-9a-fA-F]{2})');
    // eui_input.attr('required', true);
    cell_2.append(eui_input)
    table_row.append(cell_1).append(cell_2);
    table.append(table_row);
    table.append($('<div></div>', {
        class: 'dropdown-divider'
    }));
//***********************************************
//*********************************************
//**********Alias*********
    table_row = $('<div></div>', {class: "row justify-content-start"});
    cell_1 = $('<div></div>', {
        class: "col-lg-2 col-md-3",
        text: "Alias: "
    });


    cell_2 = $('<div></div>', {
        class: "col-lg-10 col-md-9",


    });
    let alias_input = $('<input/>', {
        id: 'new_reg_alias_id',
        class: "form-control form-control-sm "
    });
    alias_input.attr('type', 'text');
    alias_input.attr('placeholder', 'Alias');
    alias_input.attr('pattern', '\.{5,20}');
    alias_input.attr('required', true);
    cell_2.append(alias_input)
    table_row.append(cell_1).append(cell_2);
    table.append(table_row);

    table.append($('<div></div>', {
        class: 'dropdown-divider'
    }));
//**********************************************
    table_row = $('<div></div>', {class: "row justify-content-start"});
    cell_1 = $('<div></div>', {
        class: "col-lg-2 col-md-3",
        text: "КП: "
    });


    cell_2 = $('<div></div>', {
        class: "col-lg-10 col-md-9",
    });
    let cp_sel = $('<select></select>', {
        class: "custom-select custom-select-sm",
        id: "cp_selector_id"
    });
    cell_2.append(cp_sel);
    table_row.append(cell_1).append(cell_2);
    table.append(table_row);
//*********************************************
    table.append($('<div></div>', {
        class: 'dropdown-divider'
    }));
    table_row = $('<div></div>', {class: "row justify-content-start"});
    cell_1 = $('<div></div>', {
        class: "col-lg-6 col-md-6",
    });

    let btn = $("<button></button>", {
        class: "btn btn-primary mt-2",
        id: "reg_new_lamp_id",
        onclick: 'registrateLamp()',
        text: 'Добавить'
    });
    cell_1.append(btn);
    table_row.append(cell_1);
    table.append(table_row);
    // form.append(table);
    card.append(card_header)
    card.append(table);
    main_cont.append(card);
    return main_cont.html();
}

function createRegistrationControlPointDialog() {
    let selected;
    let main_cont = $('<div></div>', {});
    main_cont.attr("display", "inline-block");
    main_cont.attr("style", "width:500px;");
    let wg_selector;
    let card_header = $('<div></div>', {
        class: 'card-header'
    });
    let headersText = $('<h5></h5>', {
        text: 'Регистрационные данные:'
    });
    headersText.attr("display", "inline-block");

    card_header.append(headersText);
    card_header.attr("display", "inline-block");
    let card = $('<div></div>', {
        class: "card",
    });

    card.attr("style", "width:500px;");
    card.attr("display", "inline-block");
    let card_body = $('<div></div>', {
        class: "card-body",
        id: 'main_info_popup_body'
    });
    let form = $('<form></form>', {
        class: "was-validated"
    });

    let table = $('<div></div>', {
        class: "container"
    });

    let table_row;
    let cell_1;
    let cell_2;
    table.append($('<div></div>', {
        class: 'dropdown-divider'
    }));
//******************************************
//*****************Организация**************
    if (user_role === "[ADMIN]") {
        let orgs_selector = $('<select></select>', {
            class: 'custom-select custom-select-sm',
            id: 'orgs_sel_id'
        });

        cell_1 = $('<div></div>', {
            class: "col-lg-4 col-md-5",
            text: "Организация:"
        });
        cell_2 = $('<div></div>', {
            class: "col-lg-8 col-md-7",
        });
        if (organizations.size !== 0) {
            organizations.forEach((value => {
                let elem = $('<option></option>', {
                    value: value.id,
                    label: value.name,
                    text: value.name,
                });
                orgs_selector.append(elem);
            }));
            cell_2.append(orgs_selector);
            table.append($('<div></div>', {
                class: 'dropdown-divider'
            }));
            table_row = $('<div></div>', {class: "row justify-content-start"});
            let id = orgs_selector.val();
            selected = undefined;
            organizations.forEach(value => {
                if (value.id == id) {
                    selected = value;
                    return;
                }
            });

        } else {
            cell_2.append("нет активных организации");
        }
        table_row = $('<div></div>', {class: "row justify-content-start"});
        table_row.append(cell_1).append(cell_2);
        table.append(table_row);


        if (selected !== undefined) {
            let wg = undefined;
            wg_selector = $('<select></select>', {
                class: 'custom-select custom-select-sm',
                id: 'wg_sel_id'
            });
            selected.workGroups.forEach((value) => {
                let elem = $('<option></option>', {
                    value: value.id,
                    label: value.name,
                    text: value.name,
                });
                wg_selector.append(elem);
            });
            cell_1 = $('<div></div>', {
                class: "col-lg-4 col-md-5",
                text: "Рабочая Группа:"
            });
            cell_2 = $('<div></div>', {
                class: "col-lg-8 col-md-7",
            });
            cell_2.append(wg_selector);
            table_row = $('<div></div>', {class: "row justify-content-start"});
            table.append($('<div></div>', {
                class: 'dropdown-divider'
            }));
            table_row.append(cell_1).append(cell_2);
            table.append(table_row);
            table.append($('<div></div>', {
                class: 'dropdown-divider'
            }));
        }
    } else if (user_role === "[SuperUserOwner]") {
        let wg_selector = $('<select></select>', {
            class: 'custom-select custom-select-sm',
            id: 'wg_sel_id'
        });
        let it = organizations.values();
        let first = it.next().value;
        first.workGroups.forEach((value) => {

            let elem = $('<option></option>', {
                value: value.id,
                label: value.name,
                text: value.name,
            });

            wg_selector.append(elem);
        });
        cell_1 = $('<div></div>', {
            class: "col-lg-4 col-md-5",
            text: "Рабочая Группа:"
        });
        cell_2 = $('<div></div>', {
            class: "col-lg-8 col-md-7",
        });
        cell_2.append(wg_selector);
        table_row = $('<div></div>', {class: "row justify-content-start"});
        table.append($('<div></div>', {
            class: 'dropdown-divider'
        }));
        table_row.append(cell_1).append(cell_2);
        table.append(table_row);

        table.append($('<div></div>', {
            class: 'dropdown-divider'
        }));
    }
//*********************************************
//***************Название КП**********************
    table_row = $('<div></div>', {class: "row justify-content-between"});
    table_row.attr("display", "inline-block");
    cell_1 = $('<div></div>', {
        class: "col-lg-4 col-md-5",
        text: "КП: "
    });
    cell_2 = $('<div></div>', {
        class: "col-lg-8 col-md-7",
    });
    let eui_input = $('<input/>', {
        id: 'new_reg_cp_id',
        class: "form-control form-control-sm"
    });
    eui_input.attr('type', 'text');
    eui_input.attr('placeholder', 'Название КП');
    cell_2.append(eui_input)
    table_row.append(cell_1).append(cell_2);
    table.append(table_row);
    table.append($('<div></div>', {
        class: 'dropdown-divider'
    }));
//***********************************************
    table.append($('<div></div>', {
        class: 'dropdown-divider'
    }));
    table_row = $('<div></div>', {class: "row justify-content-start"});
    cell_1 = $('<div></div>', {
        class: "col-lg-6 col-md-6",
    });

    let btn = $("<button></button>", {
        class: "btn btn-primary mt-2",
        id: "reg_new_cp_id",
        onclick: 'registrateControlPoint()',
        text: 'Добавить'
    });
    cell_1.append(btn);
    table_row.append(cell_1);
    table.append(table_row);
    card.append(card_header)
    card.append(table);
    main_cont.append(card);
    return main_cont.html();
}

function lampPopunWithEditFunc(cur_lamp) {
    let isMultiEdit = (selectedObj != null && selectedObj.size > 1);
    let current_org;
    let current_workGroup;
    let card = $('<div></div>', {
        class: "card",
        id: 'main_info_popup_card'
    });
    card.attr("width", "100%");
    card.attr("display", "inline-block");
    let card_body = $('<div></div>', {
        class: "card-body",
        id: 'main_info_popup_body'
    });
    let card_body_info = drawLampProperties(cur_lamp);

    let table = $('<div></div>', {
        class: "container"
    });

    let breadcrumb = $('<nav></nav>');
    breadcrumb.attr('aria-label', "breadcrumb");
    breadcrumb.attr("style", 'background:#ffffffff')
    let ol = $('<ol></ol>', {
        class: "breadcrumb"
    });
    ol.attr("display", "flex");
    let li_1 = $('<li></li>', {
        class: "breadcrumb-item"
    });
    let a_1 = $('<a></a>', {
        href: '#',
        text: 'Основаная информация',
        onclick: 'showMainInfo()'
    });
    li_1.attr("display", "inline-block")
    li_1.append(a_1);
    let li_2 = $('<li></li>', {
        class: "breadcrumb-item"
    });
    li_2.append($('<a></a>', {
        href: '#',
        text: 'Измерения',
        onclick: 'showLampInfo()'
    }));
    li_2.attr("display", "block");
    ol.append(li_1).append(li_2);
    breadcrumb.append(ol);
    let table_row;
    let cell_1;
    let cell_2;
    //*********************************************
//*****************Организация**************

    if (user_role === "[ADMIN]") {
        let selected;
        let wg_selector;
        let orgs_selector = $('<select></select>', {
            class: 'custom-select custom-select-sm',
            id: 'orgs_sel_id',

        });

        cell_1 = $('<div></div>', {
            class: "col-lg-4 col-md-5",
            text: "Организация:"
        });
        cell_2 = $('<div></div>', {
            class: "col-lg-8 col-md-7",
        });
        if (organizations.size !== 0) {
            organizations.forEach((value => {
                let elem = $('<option></option>', {
                    value: value.id,
                    label: value.name,
                    text: value.name
                });
                if (!isMultiEdit) {
                    if (value.id === cur_lamp.organization.id) {
                        elem.attr("selected", 'selected');
                    }
                }
                orgs_selector.append(elem);
            }));
            cell_2.append(orgs_selector);
            table.append($('<div></div>', {
                class: 'dropdown-divider'
            }));
            table_row = $('<div></div>', {class: "row justify-content-start"});

            let id = orgs_selector.val();
            selected = undefined;
            organizations.forEach(value => {
                if (value.id == id) {
                    selected = value;
                    return;
                }
            });
            current_org = selected;
        } else {
            cell_2.append("нет активных организации");
        }
        table_row = $('<div></div>', {class: "row justify-content-start"});
        table_row.append(cell_1).append(cell_2);
        table.append(table_row);
        if (selected !== undefined) {

            let wg = undefined;
            wg_selector = $('<select></select>', {
                class: 'custom-select custom-select-sm',
                id: 'wg_sel_id',

            });
            // wg_selector.on("change",'#wg_sel_id', (e) => reloadLocalGroupSelector());

            selected.workGroups.forEach((value) => {
                let elem = $('<option></option>', {
                    value: value.id,
                    label: value.name,
                    text: value.name
                });

                if (value.id === cur_lamp.workGroup.id) {
                    current_workGroup = value;
                    if (!isMultiEdit) {
                        elem.attr("selected", 'selected');
                    }
                }

                wg_selector.append(elem);
            });
            if(isMultiEdit) current_workGroup = selected.workGroups[0];

            cell_1 = $('<div></div>', {
                class: "col-lg-4 col-md-5",
                text: "Рабочая Группа:"
            });
            cell_2 = $('<div></div>', {
                class: "col-lg-8 col-md-7",
            });
            cell_2.append(wg_selector);
            wg_selector.val(cur_lamp.workGroup.id);
            table_row = $('<div></div>', {class: "row justify-content-start"});
            table.append($('<div></div>', {
                class: 'dropdown-divider'
            }));
            table_row.append(cell_1).append(cell_2);
            table.append(table_row);
            table.append($('<div></div>', {
                class: 'dropdown-divider'
            }));
        }
    }
//*********************************************
//*****************Рабочая группа**************
    else if (user_role === "[SuperUserOwner]") {

        let wg_selector = $('<select></select>', {
            class: 'custom-select custom-select-sm',
            id: 'wg_sel_id'
        });
        let it = organizations.values();
        let first = it.next().value;
        first.workGroups.forEach((value) => {
            let elem = $('<option></option>', {
                value: value.id,
                label: value.name,
                text: value.name
            });

                if (value.id === cur_lamp.workGroup.id) {
                    if(!isMultiEdit) {elem.attr("selected", 'selected')};
                    current_workGroup = value;
                }


            wg_selector.append(elem);
        });

        cell_1 = $('<div></div>', {
            class: "col-lg-4 col-md-5",
            text: "Рабочая Группа:"
        });
        cell_2 = $('<div></div>', {
            class: "col-lg-8 col-md-7",
        });
        cell_2.append(wg_selector);
        table_row = $('<div></div>', {class: "row justify-content-start"});
        table.append($('<div></div>', {
            class: 'dropdown-divider'
        }));
        table_row.append(cell_1).append(cell_2);
        table.append(table_row);

        table.append($('<div></div>', {
            class: 'dropdown-divider'
        }));
    }
//*********************************************
//********************EUI********************[SuperUserOwner]
    table_row = $('<div></div>', {class: "row justify-content-between"});
    table_row.attr("display", "inline-block");
    cell_1 = $('<div></div>', {
        class: "col-lg-2 col-md-3",
        text: "EUI: "
    });
    cell_2 = $('<div></div>', {
        class: "col-lg-8 col-md-7",
    });

    inp = $('<div></div>', {
        id: "head_eui_id",
        text: cur_lamp.name
    });
    inp.attr("align", "center");
    let invCheck = $('<label></label>', {
        class: "invalid-feedback",
        id: "check_eui",
        text: 'некорретный eui'
    });
    cell_2.append(inp).append(invCheck);


    let cell_3 = $('<div></div>', {
        class: "col-lg-2 col-md-2",
    });
    let edit_btn = $('<input/>', {
        type: 'image',
        src: 'map_api/icon/edit.png',
        onclick: 'editLampEui()',
        title: "Изменить EUI"
    });
    cell_3.append(edit_btn);
    if(isMultiEdit) {
        table_row.append(cell_1);
    } else table_row.append(cell_1).append(cell_2).append(invCheck).append(cell_3);

    table.append(table_row);

    table.append($('<div></div>', {
        class: 'dropdown-divider'
    }));
//*********************************************
//**********Alias*********
    table_row = $('<div></div>', {class: "row justify-content-start"});
    cell_1 = $('<div></div>', {
        class: "col-lg-2 col-md-3",
        text: "Alias: "
    });


    cell_2 = $('<div></div>', {
        class: "col-lg-8 col-md-7",
        id: "popun_alias",
        text: cur_lamp.alias
    });
    cell_2.attr("align", "center");

    cell_3 = $('<div></div>', {
        class: "col-lg-2 col-md-2",
    });
    edit_btn = $('<input/>', {
        type: 'image',
        src: 'map_api/icon/edit.png',
        onclick: 'editLampAlias()',
        title: "Изменить Alias"
    });
    cell_3.append(edit_btn);
    if(isMultiEdit){
        table_row.append(cell_1);
    }
    else table_row.append(cell_1).append(cell_2).append(cell_3);
    table.append(table_row);
    table.append($('<div></div>', {
        class: 'dropdown-divider'
    }));

//*********************************************
//**********дата последнего обновления*********
    table_row = $('<div></div>', {class: "row justify-content-start"});
    cell_1 = $('<div></div>', {
        class: "col-lg-4 col-md-4",
        text: "дата обновления: "
    });
    let last_update;
    if (cur_lamp.objStates != null) {
        if (cur_lamp.objStates.length > 0) {
            last_update = cur_lamp.objStates[0].currentDate;
        }
    }


    cell_2 = $('<div></div>', {
        id: "popup_last_upd",
        class: "col-lg-8 col-md-8",
        text: (last_update === undefined) ? "обновлений не было" : last_update
    });
    cell_2.attr("align", "left");
    if(isMultiEdit){
        table_row.append(cell_1);
    }else table_row.append(cell_1).append(cell_2);
    table.append(table_row);
    table.append($('<div></div>', {
        class: 'dropdown-divider'
    }));
//*********************************************
//****************Яркость**********************
    if(!isMultiEdit) {
        table_row = $('<div></div>', {class: "row justify-content-between"});
        cell_1 = $('<div></div>', {
            class: "col-lg-3 col-md-4",
            text: "яркость: "
        });
        let br;
        if (cur_lamp.objStates != null) {
            if (cur_lamp.objStates.length > 0) {
                br = cur_lamp.objStates[0].data.brightness;
            }
        }
        cell_2 = $('<div></div>', {
            id: "popup_bri",
            class: "col-lg-6 col-md-5 ",
            text: br === undefined ? "нет данных" : br + " %"
        });
        cell_2.attr("align", "center");
        let button = $('<input/>', {
            type: 'image',
            src: 'map_api/icon/brightness.png',
            id: 'expandMenu',
            title: "Изменить яркость лампы"

        });
        button.attr('data-toggle', 'dropdown');
        button.attr('aria-haspopup', 'true');
        button.attr('aria-expanded', 'false');
        let menu = $('<div></div>', {
            class: 'dropdown-menu'
        });

        let elem = $('<a></a>', {
            class: "dropdown-item",
            href: '#',
            onclick: 'sendCommand(0)',
            text: 'Off'
        });
        menu.append(elem);
        menu.append($('<div></div>', {
            class: 'dropdown-divider'
        }));

        for (let i = 1; i <= 10; i++) {
            elem = $('<a></a>', {
                class: "dropdown-item",
                href: '#',
                onclick: 'sendCommand(' + i * 10 + ')',
                text: i * 10 + ' %'
            });
            menu.append(elem);
        }

        cell_3 = $('<div></div>', {
            class: "col-lg-2 col-md-3",
        });
        cell_3.append(menu);
        cell_3.append(button);

        table_row.append(cell_1).append(cell_2).append(cell_3);
        table.append(table_row);
        table.append($('<div></div>', {
            class: 'dropdown-divider'
        }));
    }
    //*********************************************
//********************группа*******************
    table_row = $('<div></div>', {class: "row justify-content-between"});
    cell_1 = $('<div></div>', {
        class: "col-lg-3 col-md-3",
        text: "группа: "
    });

    cell_2 = $('<div></div>', {
        class: "col-lg-7 col-md-7"
    });


    let in_group = $('<div></div>', {
        class: 'input-group'
    });

    let group_selector = $('<select></select>', {
        class: 'custom-select custom-select-sm',
        id: 'reg_group_sel_id'
    });


    let def = $('<option></option>', {
        value: 0,
        label: 'без группы',
        text: 'без группы'
    });
    group_selector.append(def);
    if (user_role === "[SuperUser]" || user_role === "[USER]") {
        mapOfGroups.forEach((value, key) => {
            let elem = $('<option></option>', {
                value: value,
                label: key,
                text: key

            });
            if (cur_lamp.workGroup.groups != null) {
                if(!isMultiEdit) {
                    if (value == cur_lamp.workGroup.groups[0].id) {
                        elem.attr("selected", "selected");
                    }
                }
            }

            group_selector.append(elem);
        })
    } else {

        current_workGroup.groups.forEach((value) => {
            let elem = $('<option></option>', {
                value: value.id,
                label: value.name,
                text: value.name

            });
            if (cur_lamp.workGroup.groups != null) {
                if(!isMultiEdit) {
                    if (value.id === cur_lamp.workGroup.groups[0].id) {
                        elem.attr("selected", "selected");
                    }
                }
            }

            group_selector.append(elem);
        });
    }

    group_selector.change(() => {
        group_selector.removeClass("is-valid");
    });
    in_group.append(group_selector);
    let input_gr_addon = $('<div></div>', {
        class: 'input-group-addon input-group-button'
    });

    in_group.append(input_gr_addon);
    cell_2.append(in_group);


    /* кнопка*/
    let gr_button = $('<input/>', {
        type: 'image',
        src: 'map_api/icon/brightness.png',
        id: 'expandMenu',
        title: "Изменить яркость группы"
    });
    gr_button.attr('data-toggle', 'dropdown');
    gr_button.attr('aria-haspopup', 'true');
    gr_button.attr('aria-expanded', 'false');
    let gr_menu = $('<div></div>', {
        class: 'dropdown-menu'
    });

    let command = 'sendGroupCommand(' + "\'" + cur_lamp.group + "\'" + ', 0)';
    elem = $('<a></a>', {
        class: "dropdown-item",
        href: '#',
        onclick: command,
        text: 'Off'
    });
    gr_menu.append(elem);
    gr_menu.append($('<div></div>', {
        class: 'dropdown-divider'
    }));

    for (let i = 1; i <= 10; i++) {
        let command = 'sendGroupCommand(' + "\'" + cur_lamp.group + "\'" + ', ' + i * 10 + ')';
        elem = $('<a></a>', {
            class: "dropdown-item",
            href: '#',
            onclick: command,
            text: i * 10 + ' %'
        });
        gr_menu.append(elem);
    }
    /* кнопка*/
    cell_3 = $('<div></div>', {
        class: "col-lg-2 col-md-2",
    });
    cell_3.append(gr_menu);
    cell_3.append(gr_button);
    table_row.append(cell_1).append(cell_2).append(cell_3);
    table.append(table_row);
    table.append($('<div></div>', {
        class: 'dropdown-divider'
    }));
//*********************************************
//******************Выбор  КП******************
    table_row = $('<div></div>', {class: "row justify-content-start"});
    cell_1 = $('<div></div>', {
        class: "col-lg-3 col-md-3",
        text: "КП: "
    });
    cell_2 = $('<div></div>', {
        class: "col-lg-7 col-md-7"
    });


    let group = $('<div></div>', {
        class: 'input-group'
    });

    let cp_selector = $('<select></select>', {
        class: 'custom-select custom-select-sm',
        id: 'cp_selector_id'
    });
    elem = $('<option></option>', {
        value: null,
        label: 'Без Кп',
        text: 'Без Кп'
    });
    cp_selector.append(elem);
    control_points.forEach((value, key) => {
        if (value.workGroup.id == cur_lamp.workGroup.id) {
            elem = $('<option></option>', {
                value: value.objectName,
                label: value.objectName,
                text: value.objectName
            });
            if (cur_lamp.cp_owner == value.id) {
                elem.attr('selected', 'selected');
            }
            cp_selector.append(elem);
        }
    });
//загружать текущие КП
    cp_selector.change(() => {
        cp_selector.removeClass("is-valid");
    });
    group.append(cp_selector);
    input_gr_addon = $('<div></div>', {
        class: 'input-group-addon input-group-button'
    });

    group.append(input_gr_addon);
    cell_2.append(group);
    table_row.append(cell_1).append(cell_2);
    table.append(table_row);
    table.append($('<div></div>', {
        class: 'dropdown-divider'
    }));
    if ((user_role === "[SuperUserOwner]") || (user_role === "[ADMIN]")) {
        table.append(getEditPanel(cur_lamp,isMultiEdit));
    } else if ((user_role === "[SuperUser]")) {
        table.append(getAknowledgePanel(cur_lamp,isMultiEdit));
        table.append($('<div></div>', {
            class: 'dropdown-divider'
        }));
        table.append(getEditPanel(cur_lamp,isMultiEdit));
    } else {
        table.append(getAknowledgePanel(cur_lamp,isMultiEdit));
    }


    card_body.append(table);
    if (!isMultiEdit) card.append(breadcrumb);
    card.append(card_body);
    card.append(card_body_info);

    return card.html();
}

function getAknowledgePanel(cur_lamp,isMulti) {
    let table_row = $('<div></div>', {class: "row justify-content-between"});
    let cell_1 = $('<div></div>', {
        class: "col-4"
    });

    let cell_2 = $('<div></div>', {
        class: "col-4"
    });
    let cell_3 = $('<div></div>', {
        class: "col-2"
    });
    let aknowledge = $('<a></a>', {
        href: '#',
        onclick: 'acknowledge(\'' + cur_lamp.name + '\')',
        text: 'квитировать'
    });
    let aknowledgeAll = $('<a></a>', {
        href: '#',
        onclick: 'acknowledgeGroup(\'' + cur_lamp.name + '\')',
        text: 'квитировать группу'
    });
    cell_1.append(aknowledge);
    cell_2.append(aknowledgeAll);
    if(!isMulti) {
        table_row.append(cell_1).append(cell_2).append(cell_3);
    }else table_row.append(cell_2).append(cell_3);
    return table_row;
}

function getEditPanel(cur_lamp,isMulti) {
    let table_row = $('<div></div>', {class: "row justify-content-between"});
    let cell_1 = $('<div></div>', {
        class: "col-4"
    });

    let cell_2 = $('<div></div>', {
        class: "col-4"
    });
    let cell_3 = $('<div></div>', {
        class: "col-2"
    });


    let saveLampChanges = $('<a></a>', {
        href: '#',
        onclick: 'updateLampData()',
        text: 'сохранить изменения'
    });

    let deleteLamp = $('<a></a>', {
        href: '#',
        onclick: 'deleteLamp()',
        text: 'удалить'
    });
    let setCoord = $('<input/>', {
        type: 'image',
        src: 'map_api/icon/location.png',
        title: "Задать координаты на карте",
        onclick: 'setMarkerLatLng(\'' + cur_lamp.name + '\')'
    });
    cell_1.append(saveLampChanges);
    cell_2.append(deleteLamp);
    cell_3.append(setCoord)
    if(!isMulti) {
        table_row.append(cell_1).append(cell_2).append(cell_3);
    } else {
        table_row.append(cell_1);
    }
    return table_row;

}
