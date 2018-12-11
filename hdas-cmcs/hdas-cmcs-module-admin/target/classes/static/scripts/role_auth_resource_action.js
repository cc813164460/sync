/**
 * Created by maodi on 2018/6/5.
 */
function addRoleAuthResource() {
    addAction(getRoleAuthResource, validRoleAuthResourceData, "/role_auth_resource/insert", HTML.ROLE_AUTH_RES, true);
}

function updateRoleAuthResource() {
    updateAction(getRoleAuthResource, validRoleAuthResourceData, "/role_auth_resource/update", HTML.ROLE_AUTH_RES, true);
}

function validRoleAuthResourceData() {
    var role = getSelectVal("role");
    var flag = true;
    //权限验证不能为空
    empty("auth_info");
    if (!validSelectedAuth()) {
        append("auth_info");
        flag = false;
    }
    empty("role_info");
    if (!validIsSelect(role)) {
        append("role_info");
        flag = false;
    }
    return flag;
}

function getRoleAuthResource() {
    var roleAuthResource = {};
    roleAuthResource.roleId = parseInt(getSelectVal("role"));
    roleAuthResource.data = getDataTableData();
    var classId = getClassId();
    roleAuthResource.classId = classId;
    return JSON.stringify(roleAuthResource);
}

function selectLinkageTable() {
    $("#role").change(function () {
        var roleId = getSelectVal("role");
        $("#role_auth_resource_action_table").DataTable().ajax.url('/role_auth_resource/query_action?roleId=' + roleId).load();
    });
}

function loadRoleAuthResourceActionTable(id) {
    var table = $('#' + id).DataTable({
        ajax: {
            url: '/role_auth_resource/query_action',
            dataSrc: 'data',
            data: {
                "roleId": function () {
                    return getSelectVal("role");
                }
            }
        },
        columns: [
            {
                data: 'num',
                orderable: false,
                createdCell: function (nTd, sData, oData, iRow, iCol) {
                    $(nTd).html(CONTENT.SELECT_LINE_CHECKBOX);
                }
            },
            {data: 'product_name'},
            {data: 'module_name'},
            {data: 'env_name'},
            {data: 'version_name'},
            {
                data: 'select',
                orderable: false,
                createdCell: function (nTd, sData, oData, iRow, iCol) {
                    $(nTd).html(CONTENT.SELECT_CHECKBOX);
                    loadSelected(sData, $(nTd));
                }
            },
            {
                data: 'insert',
                orderable: false,
                createdCell: function (nTd, sData, oData, iRow, iCol) {
                    if (oData.product_name == NAV.HISTORY_MANAGE) {
                        $(nTd).html("");
                    } else {
                        $(nTd).html(CONTENT.INSERT_CHECKBOX);
                        loadSelected(sData, $(nTd));
                    }
                }
            },
            {
                data: 'update',
                orderable: false,
                createdCell: function (nTd, sData, oData, iRow, iCol) {
                    if (oData.product_name == NAV.HISTORY_MANAGE) {
                        $(nTd).html("");
                    } else {
                        $(nTd).html(CONTENT.UPDATE_CHECKBOX);
                        loadSelected(sData, $(nTd));
                    }
                }
            },
            {
                data: 'delete',
                orderable: false,
                createdCell: function (nTd, sData, oData, iRow, iCol) {
                    if (oData.product_name == NAV.HISTORY_MANAGE) {
                        $(nTd).html("");
                    } else {
                        $(nTd).html(CONTENT.DELETE_CHECKBOX);
                        loadSelected(sData, $(nTd));
                    }
                }
            },
            {
                data: 'release',
                orderable: false,
                createdCell: function (nTd, sData, oData, iRow, iCol) {
                    if ((oData.product_name == NAV.HISTORY_MANAGE && oData.env_name == "未发布") || (oData.product_name == NAV.SETTING)) {
                        $(nTd).html("");
                    } else {
                        $(nTd).html(CONTENT.RELEASE_CHECKBOX);
                        loadSelected(sData, $(nTd));
                    }
                }
            },
            {
                data: 'offline',
                orderable: false,
                createdCell: function (nTd, sData, oData, iRow, iCol) {
                    if ((oData.product_name == NAV.HISTORY_MANAGE) || (oData.product_name == NAV.SETTING)) {
                        $(nTd).html("");
                    } else {
                        $(nTd).html(CONTENT.OFFLINE_CHECKBOX);
                        loadSelected(sData, $(nTd));
                    }
                }
            },
            {
                data: 'id',
                visible: false
            }
        ],
        language: {
            emptyTable: MESSAGES.EMPTY_TABLE,
            zeroRecords: MESSAGES.ZERO_RECORDS,
            loadingRecords: MESSAGES.LOADING_RECORDS,
            processing: MESSAGES.PROCESSING
        },
        info: false,
        paging: false,
        processing: true,
        searching: true,
        retrieve: true,
        serverSide: true,
        scrollCollapse: true,
        ordering: false,
        scrollY: 350,
        initComplete: function (settings, json) {
            loadAuthProductSelectData("product_select");
            loadAuthModuleSelectData("module_select");
            loadAuthEnvSelectData("env_select");
            loadAuthOrganSelectData("organ_select");
            loadAuthAreaSelectData("area_select");
        },
        drawCallback: function (settings) {
            initAuthQuery(table, "/role_auth_resource", id + "_filter");
            $(".dataTables_scrollHeadInner").children("table").css("border-radius", 0);
            clickQueryOtherCheckBox();
            selectLineCheckBox();
        }
    });
}

function clickQueryOtherCheckBox() {
    $('input:checkbox[name=insert],input:checkbox[name=update],input:checkbox[name=delete],input:checkbox[name=release],input:checkbox[name=offline]').click(function () {
        if ($(this).prop("checked")) {
            $(this).parent('td').parent('tr').find('input[name="select"]').prop({checked: true});
        }
    });
}

function selectLineCheckBox() {
    $('input:checkbox[name=select_line]').click(function () {
        if ($(this).prop("checked")) {
            $(this).parent('td').parent('tr').find('input:checkbox[name=select],input:checkbox[name=insert],input:checkbox[name=update],input:checkbox[name=delete],input:checkbox[name=release],input:checkbox[name=offline]').prop({checked: true});
        } else {
            $(this).parent('td').parent('tr').find('input:checkbox[name=select],input:checkbox[name=insert],input:checkbox[name=update],input:checkbox[name=delete],input:checkbox[name=release],input:checkbox[name=offline]').prop({checked: false});
        }
    });
    $('input:checkbox[name=select_line]').on('blur', function () {
        if ($(this).prop("checked")) {
            $(this).parent('td').parent('tr').find('input:checkbox[name=select],input:checkbox[name=insert],input:checkbox[name=update],input:checkbox[name=delete],input:checkbox[name=release],input:checkbox[name=offline]').prop({checked: true});
        } else {
            $(this).parent('td').parent('tr').find('input:checkbox[name=select],input:checkbox[name=insert],input:checkbox[name=update],input:checkbox[name=delete],input:checkbox[name=release],input:checkbox[name=offline]').prop({checked: false});
        }
    });
}

function loadClass() {
    var roleId = parseInt(getSelectVal("role"));
    $.ajax({
        url: "/role/get_classId_by_roleId",
        type: "post",
        data: {"roleId": roleId},
        cache: false,
        traditional: true,
        dataType: "json",
        error: function (xhr, textStatus, thrownError) {
            ajaxError(xhr, textStatus, thrownError);
        },
        success: function (data) {
            if (typeof(data) == "string" && data.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                    location.reload();
                });
            } else {
                if (data.code == 200) {
                    if (data.info == "1") {
                        $("#secrecy").prop({checked: true});
                    } else if (data.info == "2") {
                        $("#confidential").prop({checked: true});
                    } else {
                        $("#normal").prop({checked: true});
                    }
                } else {
                    $.MsgBox.Alert(MESSAGES.NEWS, data.message);
                }
            }
        }
    });
}

function initAuthQuery(table, url, filterId) {
    loadQuery("请输入一级板块名称");
    var select = ".dataTables_filter";
    if (filterId) {
        select = "#" + filterId + select;
    }
    var moduleSelectId = "module_select";
    var envSelectId = "env_select";
    var organSelectId = "organ_select";
    var areaSelectId = "area_select";
    var configSelectId = "config_select";
    if ($(select + " > #clear_query").length == 0) {
        var filter = $(select);
        filter.prepend('<div class="history-query-div" id="config_div"><span>配置：</span><select id="' + configSelectId + '"' +
            ' class="history-query-select"><option value="-1">全部</option><option value="0">未配置</option><option ' +
            'value="1">已配置</option></select></div>');
        filter.prepend('<div class="history-query-div" id="area_div"><span>分布：</span><select id="' + areaSelectId + '"' +
            ' class="history-query-select"></div>');
        filter.prepend('<div class="history-query-div" id="organ_div"><span>部门：</span><select id="' + organSelectId + '"' +
            ' class="history-query-select"></div>');
        filter.prepend('<div class="history-query-div" id="env_div"><span>环境：</span><select id="' + envSelectId + '"' +
            ' class="history-query-select"></div>');
        filter.prepend('<div class="history-query-div" id="module_div"><span>模块：</span><select id="' + moduleSelectId + '"' +
            ' class="history-query-select"></div>');
        filter.prepend('<div class="query-close" id="clear_query"><img src="/img/icon_guanbi.png"/></div>');
        filter.append('<div class="query-search"><img src="/img/icon_sousuo.png"/></div>');
        $(select + " > .query-search").click(function () {
            var name = $(select + " > label > input").val();
            var appendUrl = "";
            if (name && name.length > 0) {
                appendUrl += "&name=" + name;
            }
            appendUrl += getQueryCondition();
            table.ajax.url(url + '/query_action_by_condition?' + appendUrl).load();
            $(select + " > label > input").val(name);
        });
        $(select + " > label > input").on('keypress', function (event) {
            if (event.keyCode == 13) {
                var name = $(this).val();
                var appendUrl = "";
                if (name && name.length > 0) {
                    appendUrl += "&name=" + name;
                }
                appendUrl += getQueryCondition();
                table.ajax.url(url + '/query_action_by_condition?' + appendUrl).load();
                $(select + " > label > input").val(name);
            }
        });
        $(select + "> #clear_query").click(function () {
            table.ajax.url(url + '/query_action').load();
            $("#" + moduleSelectId).find('option[value="-1"]').prop({selected: true});
            $("#" + envSelectId).find('option[value="-1"]').prop({selected: true});
            $("#" + organSelectId).find('option[value="-1"]').prop({selected: true});
            $("#" + areaSelectId).find('option[value="-1"]').prop({selected: true});
            $("#" + configSelectId).find('option[value="-1"]').prop({selected: true});
            $(select + " > label > input").val("");
        });
    }
}

function getQueryCondition() {
    var moduleSelectId = "module_select";
    var envSelectId = "env_select";
    var organSelectId = "organ_select";
    var areaSelectId = "area_select";
    var configSelectId = "config_select";
    var moduleId = getSelectVal(moduleSelectId);
    var envId = getSelectVal(envSelectId);
    var organId = getSelectVal(organSelectId);
    var areaId = getSelectVal(areaSelectId);
    var configId = getSelectVal(configSelectId);
    var appendUrl = "";
    if (moduleId != -1) {
        appendUrl += "&moduleId=" + moduleId;
    }
    if (envId != -1) {
        appendUrl += "&envId=" + envId;
    }
    if (organId != -1) {
        appendUrl += "&organId=" + organId;
    }
    if (areaId != -1) {
        appendUrl += "&areaId=" + areaId;
    }
    if (configId != -1) {
        appendUrl += "&configId=" + configId;
    }
    return appendUrl;
}

function loadAuthProductSelectData(id) {
    loadAuthSelect(id, "/product/product_data");
}

function loadAuthModuleSelectData(id) {
    loadAuthSelect(id, "/module/module_data");
}

function loadAuthEnvSelectData(id) {
    loadAuthSelect(id, "/env/env_data");
}

function loadAuthOrganSelectData(id) {
    loadAuthSelect(id, "/organ/organ_data");
}

function loadAuthAreaSelectData(id) {
    loadAuthSelect(id, "/area/area_data");
}

function loadAuthSelect(id, url) {
    //清空
    $("#" + id).empty();
    $("#" + id).append('<option value="-1">全部</option>');
    $.ajax({
        url: url,
        type: "post",
        cache: false,
        error: function (xhr, textStatus, thrownError) {
            ajaxError(xhr, textStatus, thrownError);
        },
        success: function (data) {
            if (typeof(data) == "string" && data.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                    location.reload();
                });
            } else {
                if (data && data.length != 0) {
                    for (var i = 0; i < data.length; i++) {
                        var option = '<option value="' + data[i].id + '">' + data[i].name + '</option>'
                        $("#" + id).append(option);
                    }
                }
            }
        }
    });
}

function authSelectAll() {
    selectAll("select_select_all", "select");
    selectAll("insert_select_all", "insert");
    selectAll("update_select_all", "update");
    selectAll("delete_select_all", "delete");
    selectAll("release_select_all", "release");
    selectAll("offline_select_all", "offline");
}