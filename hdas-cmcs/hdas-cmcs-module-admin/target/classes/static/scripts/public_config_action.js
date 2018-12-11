/**
 * Created by maodi on 2018/7/3.
 */
function loadApp(itemId, appIdd, envId, selectName) {
    $("#" + appIdd).empty();
    var appId = "app";
    if (appIdd) {
        appId = appIdd;
    }
    var name = "app-input"
    if (selectName && selectName != null) {
        name = selectName;
    }
    var idPre = appId.substring(0, appId.length - 3);
    var appDataAjax = $.ajax({
        url: "/app/app_data",
        type: "POST",
        timeout: 30000,
        async: false,
        data: {
            "itemId": itemId,
            "envId": envId
        },
        success: function (response) {
            if (typeof(response) == "string" && response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                    location.reload();
                });
            } else {
                var datas = response;
                var text = "";
                for (var i in datas) {
                    var data = datas[i];
                    var id = data.id;
                    var hostname = data.hostname;
                    text += '<div id="' + idPre + id + '_div" class="app-checkbox-div"><input id="' + idPre + id + '" type="checkbox" name="' + name + '"/><span>' + hostname + '</span></div>';
                }
                $("#" + appId).prepend(text);
                if (itemId) {
                    for (var i in datas) {
                        var data = datas[i];
                        var id = data.id;
                        var isSelect = data.is_select;
                        if (isSelect == 1) {
                            var checkBox = $("#" + idPre + id);
                            setItem(idPre + "app", true);
                            checkBox.prop({checked: true});
                        }
                    }
                }
            }
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                appDataAjax.abort();
                window.location.href = "/timeout";
            }
        },
        error: function (xhr, textStatus, thrownError) {
            ajaxError(xhr, textStatus, thrownError);
        }
    });
}

function loadApplyDataTable(itemId, tableIdd, versionId, envId, applySelectName) {
    var tableId = "product_module_table";
    if (tableIdd) {
        tableId = tableIdd;
    }
    var name = "select";
    if (applySelectName && applySelectName != null) {
        name = applySelectName;
    }
    var idPre = tableId.substring(0, tableId.length - 20);
    if ($("#" + tableId + "_wrapper").length > 0) {
        var url = '/public_config/get_product_module_list?versionId=' + versionId + '&envId=' + envId;
        if (itemId) {
            url += '&itemId=' + itemId;
        }
        $('#' + tableId).DataTable().ajax.url(url).load(function () {
            hiddenApplyNullLine(tableId);
        });
    } else {
        var table = $('#' + tableId).DataTable({
            ajax: {
                url: '/public_config/get_product_module_list',
                dataSrc: 'data',
                data: {
                    "itemId": itemId,
                    "versionId": versionId,
                    "envId": envId
                }
            },
            columns: [
                {data: 'id'},
                {data: 'product_name'},
                {data: 'module_name'},
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
            columnDefs: [{
                targets: 0,
                createdCell: function (nTd, sData, oData, iRow, iCol) {
                    $(nTd).html('<input id="' + sData + '" type="checkbox" name="' + name + '"/>');
                    if (oData.is_select == 1) {
                        $(nTd).children("input").prop({checked: true});
                    }
                }
            }, {
                targets: 1,
                createdCell: function (nTd, sData, oData, iRow, iCol) {
                    $(nTd).html(`<div></div>`);
                    $(nTd).children("div").attr("title", `${sData}`);
                    $(nTd).children("div").text(`${sData.substring(0, 20)}`);
                }
            }, {
                targets: 2,
                createdCell: function (nTd, sData, oData, iRow, iCol) {
                    $(nTd).html(`<div></div>`);
                    $(nTd).children("div").attr("title", `${sData}`);
                    $(nTd).children("div").text(`${sData.substring(0, 20)}`);
                }
            }],
            info: false,
            paging: false,
            processing: true,
            searching: true,
            retrieve: true,
            serverSide: true,
            scrollCollapse: true,
            ordering: false,
            scrollY: 200,
            initComplete: function (settings, json) {
                loadPublicModuleSelectData(idPre + "module_select");
                hiddenApplyNullLine(tableId);
            },
            drawCallback: function (settings) {
                initApplyQuery(table, "/public_config", tableId + "_filter", versionId, itemId, envId);
                clickTableTr(applySelectName);
                $(".dataTables_scrollHeadInner").children("table").css("border-radius", 0);
                if (settings._iRecordsTotal < 1) {
                    $("#" + idPre + "product_module_table > tbody > tr > .dataTables_empty").css("width", "100%");
                }
                hiddenApplyNullLine(tableId);
            }
        });
    }
}

function loadUpdatePropertiesVal(versionId) {
    var key = getItem("key");
    var value = getItem("value");
    var comment = getItem("comment");
    var itemId = getItem("id");
    $("#key").val(key);
    $("#value").val(value);
    $("#comment").val(comment);
    $("#item_id").val(itemId);
    var envId = getItem("properties_envId");
    if (versionId == 1) {
        envId = getItem("gray_properties_envId");
    }
    loadApp(itemId);
    loadApplyDataTable(itemId, "product_module_table", versionId, envId);
}

function addPropertiesInstance() {
    addAction(function () {
        return getInstance(0);
    }, function () {
        return validInstanceData();
    }, "/public_config/insert", HTML.PUBLIC_CONFIG, true, "key_info");
}

function updatePropertiesInstance() {
    updateAction(function () {
        return getInstance(0);
    }, function () {
        return validInstanceData();
    }, "/public_config/update", HTML.PUBLIC_CONFIG, true, "key_info");
}

function getInstance(instanceTypeId) {
    var key = $("#key").val();
    var value = $("#value").val();
    var comment = $("#comment").val();
    var versionId = $("#version_id").val();
    var releaseStatus = $("#release_status").val();
    var itemId = $("#item_id").val();
    var versionNum = $("#version_num").val();
    var appIds = getSelectedAppIds();
    var instanceInfo = new Object();
    instanceInfo.key = key;
    instanceInfo.value = value;
    instanceInfo.instanceTypeId = instanceTypeId;
    instanceInfo.comment = comment;
    instanceInfo.versionId = versionId;
    instanceInfo.releaseStatus = releaseStatus == "未发布" ? 0 : 1;
    instanceInfo.versionId = versionId;
    instanceInfo.itemId = itemId;
    instanceInfo.resourceIdsListMap = getSelectedResourceIdsListMap();
    instanceInfo.appIds = appIds;
    instanceInfo.classId = getClassId();
    instanceInfo.mustChange = getMustChange();
    var envId = parseInt(getItem("properties_envId"));
    if (versionId == 1) {
        envId = parseInt(getItem("gray_properties_envId"));
    }
    instanceInfo.envId = envId;
    if (versionNum) {
        instanceInfo.versionNum = versionNum;
    }
    return JSON.stringify(instanceInfo);
}

function validInstanceData() {
    var key = $("#key").val();
    var value = $("#value").val();
    var comment = $("#comment").val();
    var flag = true;
    if (!validValueShowInfo(key, 50, "key_info")) {
        flag = false;
    }
    if (!validValueStartWithShowInfo(key, "key_info")) {
        flag = false;
    }
    if (!validValueShowInfo(value, -1, "value_info")) {
        flag = false;
    }
    if (!validValueShowInfo(comment, 500, "comment_info", true)) {
        flag = false;
    }
    if (!validSelectedApplyShowInfo()) {
        flag = false;
    }
    return flag;
}

function excludePropertiesRelease() {
    $("#json_release").click(function () {
        validRelease("json", 0, 1);
    });
    $("#gray_json_release").click(function () {
        validRelease("gray_json", 1, 1);
    });
    $("#xml_release").click(function () {
        validRelease("xml", 0, 2);
    });
    $("#gray_xml_release").click(function () {
        validRelease("gray_xml", 1, 2);
    });
    $("#yml_release").click(function () {
        validRelease("yml", 0, 3);
    });
    $("#gray_yml_release").click(function () {
        validRelease("gray_yml", 1, 3);
    });
}

function validRelease(idPre, versionId, instanceTypeId) {
    var url = "/public_config/release";
    var flag = true;
    if (!validSelectedAppShowInfo(idPre + "_app", idPre + "_app_info")) {
        flag = false;
    }
    if (!validSelectedApplyShowInfo(idPre + "_product_module_table", idPre + "_apply_info", idPre + "-select")) {
        flag = false;
    }
    if (flag) {
        $.MsgBox.Release(MESSAGES.NEWS, MESSAGES.IS_RELEASE, function () {
            excludeAjaxAction(url, getItemId(idPre), versionId, instanceTypeId, idPre);
        });
    }
}

function excludePropertiesOffline() {
    $("#json_offline").click(function () {
        validOffline("json", 0, 1);
    });
    $("#gray_json_offline").click(function () {
        validOffline("gray_json", 1, 1);
    });
    $("#xml_offline").click(function () {
        validOffline("xml", 0, 2);
    });
    $("#gray_xml_offline").click(function () {
        validOffline("gray_xml", 1, 2);
    });
    $("#yml_offline").click(function () {
        validOffline("yml", 0, 3);
    });
    $("#gray_yml_offline").click(function () {
        validOffline("gray_yml", 1, 3);
    });
}

function validOffline(idPre, versionId, instanceTypeId) {
    var url = "/public_config/offline";
    var releaseStatus = $("#" + idPre + "_release_status").text();
    if (releaseStatus == "未发布") {
        $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.OFFLINE);
    } else {
        $.MsgBox.Offline(MESSAGES.NEWS, MESSAGES.IS_OFFLINE, function () {
            excludeAjaxAction(url, getItemId(idPre), versionId, instanceTypeId, idPre);
        });
    }
}

function excludePropertiesDelete() {
    var url = "/public_config/delete_by_ids";
    $("#json_del").click(function () {
        excludeDeleteConfirm(url, getItemId("json"), 0, 1, "json");
    });
    $("#gray_json_del").click(function () {
        excludeDeleteConfirm(url, getItemId("gray_json"), 1, 1, "gray_json");
    });
    $("#xml_del").click(function () {
        excludeDeleteConfirm(url, getItemId("xml"), 0, 2, "xml");
    });
    $("#gray_xml_del").click(function () {
        excludeDeleteConfirm(url, getItemId("gray_xml"), 1, 2, "gray_xml");
    });
    $("#yml_del").click(function () {
        excludeDeleteConfirm(url, getItemId("yml"), 0, 3, "yml");
    });
    $("#gray_yml_del").click(function () {
        excludeDeleteConfirm(url, getItemId("gray_yml"), 1, 3, "gray_yml");
    });
}

function excludeDeleteConfirm(url, itemId, versionId, instanceTypeId, idPre) {
    $.MsgBox.Delete(MESSAGES.NEWS, MESSAGES.IS_DELETE, function () {
        excludeDeleteAction(url, itemId, versionId, instanceTypeId, idPre)
    });
}

function excludeDeleteAction(url, itemId, versionId, instanceTypeId, idPre) {
    var itemIds = new Array();
    itemIds.push(itemId)
    $.ajax({
        url: url,
        type: "post",
        data: {ids: itemIds},
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
                    $.MsgBox.Alert(MESSAGES.NEWS, data.message, function () {
                        var envId = getSelectVal(idPre + "_env_select");
                        getExcludePropertiesInfo(versionId, instanceTypeId, envId);
                    });
                    removeItem(idPre + "_value")
                } else {
                    $.MsgBox.Alert(MESSAGES.NEWS, data.message);
                }
            }
        }
    });
}

function getItemId(idPre) {
    return $("#" + idPre + "_item_id").val();
}

function excludeAjaxAction(url, itemId, versionId, instanceTypeId, idPre) {
    var itemIds = new Array();
    itemIds.push(itemId);
    $.ajax({
        url: url,
        type: "post",
        data: {
            versionId: versionId,
            ids: itemIds
        },
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
                    $.MsgBox.Alert(MESSAGES.NEWS, data.message, function () {
                        var envId = getSelectVal(idPre + "_env_select");
                        getExcludePropertiesInfo(versionId, instanceTypeId, envId);
                    });
                } else {
                    $.MsgBox.Alert(MESSAGES.NEWS, data.message);
                }
            }
        }
    });
}

function excludePropertiesEdit() {
    var instanceInfo = new Object();
    instanceInfo.key = "content";
    $("#json_edit").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "json", "update");
    });
    $("#gray_json_edit").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "gray_json", "update");
    });
    $("#xml_edit").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "xml", "update");
    });
    $("#gray_xml_edit").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "gray_xml", "update");
    });
    $("#yml_edit").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "yml", "update");
    });
    $("#gray_yml_edit").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "gray_yml", "update");
    });
}

function excludePropertiesSave() {
    var instanceInfo = new Object();
    instanceInfo.key = "content";
    $("#json_save").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "json", "add");
    });
    $("#gray_json_save").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "gray_json", "add");
    });
    $("#xml_save").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "xml", "add");
    });
    $("#gray_xml_save").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "gray_xml", "add");
    });
    $("#yml_save").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "yml", "add");
    });
    $("#gray_yml_save").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "gray_yml", "add");
    });
}

function makeExcludePropertiesInstance(instanceInfo, idPre, action) {
    var itemId = getItemId(idPre);
    if (action == "update") {
        if (typeof(itemId) == "undefined" || itemId.length < 1) {
            $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.ADD_BEFORE);
            return;
        }
    }
    if (itemId) {
        if (action == "add") {
            $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.ADDED);
            return;
        }
        instanceInfo.itemId = itemId;
    }
    instanceInfo.value = $("#" + idPre + "_value").val();
    instanceInfo.comment = $("#" + idPre + "_comment").val();
    var instanceTypeId = 1;
    if (idPre.endWith("json")) {
        instanceTypeId = 1;
    } else if (idPre.endWith("xml")) {
        instanceTypeId = 2;
    } else if (idPre.endWith("yml")) {
        instanceTypeId = 3;
    }
    instanceInfo.instanceTypeId = instanceTypeId;
    instanceInfo.appIds = getSelectedAppIds(idPre + "_app", idPre + "-app-input");
    instanceInfo.releaseStatus = $("#" + idPre + "_release_status").text() == "未发布" ? 0 : 1;
    var versionId = 0;
    instanceInfo.resourceIdsListMap = getSelectedResourceIdsListMap(idPre + "_product_module_table", idPre + "-select");
    if (idPre.startWith("gray")) {
        versionId = 1;
    }
    instanceInfo.versionId = versionId;
    instanceInfo.classId = getClassId();
    instanceInfo.envId = getSelectVal(idPre + "_env_select");
    var versionNum = $("#" + idPre + "_version_num").val();
    if (versionNum) {
        instanceInfo.versionNum = versionNum;
    }
    if (action == "update") {
        excludePropertiesUpdateAction(instanceInfo, idPre, versionId, instanceTypeId);
    } else {
        excludePropertiesAddAction(instanceInfo, idPre, versionId, instanceTypeId);
    }
}

function validExcludePropertiesAddAndUpdate(instanceInfo, idPre) {
    var flag = true;
    $("#" + idPre + "_app_info").empty();
    if (!validValueShowInfo(instanceInfo.value, -1, idPre + "_value_info")) {
        flag = false;
    }
    if (!validSelectedApplyShowInfo(idPre + "_product_module_table", idPre + "_apply_info", idPre + "-select")) {
        flag = false;
    }
    if (!validValueShowInfo(instanceInfo.comment, 500, idPre + "_comment_info", true)) {
        flag = false;
    }
    return flag;
}

function excludePropertiesAddAction(instanceInfo, idPre, versionId, instanceTypeId) {
    if (validExcludePropertiesAddAndUpdate(instanceInfo, idPre)) {
        instanceInfo = JSON.stringify(instanceInfo);
        $.ajax({
            url: "/public_config/insert",
            type: "post",
            data: instanceInfo,
            cache: false,
            traditional: true,
            dataType: "json",
            contentType: "application/json",
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
                        $.MsgBox.Alert(MESSAGES.NEWS, data.message, function () {
                            var envId = getSelectVal(idPre + "_env_select");
                            getExcludePropertiesInfo(versionId, instanceTypeId, envId);
                            var itemId = $("#" + idPre + "_item_id").val();
                            var loadUrl = '/public_config/get_product_module_list?envId=' + envId + '&versionId=' + versionId + "&itemId=" + itemId;
                            var tableId = idPre + "_product_module_table";
                            $("#" + tableId).DataTable().ajax.url(loadUrl).load(function () {
                                hiddenApplyNullLine(tableId);
                            });
                            $("#" + idPre + "_module_select").find('option[value="-1"]').prop({selected: true});
                            $("#" + idPre + "_config_select").find('option[value="-1"]').prop({selected: true});
                            $("#" + idPre + "_product_module_table_filter > label > input").val("");
                        });
                    } else {
                        $.MsgBox.Alert(MESSAGES.NEWS, data.message);
                    }
                }
            }
        });
    }
}

function excludePropertiesUpdateAction(instanceInfo, idPre, versionId, instanceTypeId) {
    if (validExcludePropertiesAddAndUpdate(instanceInfo, idPre)) {
        instanceInfo = JSON.stringify(instanceInfo);
        $.ajax({
            url: "/public_config/update",
            type: "post",
            data: instanceInfo,
            cache: false,
            traditional: true,
            dataType: "json",
            contentType: "application/json",
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
                        $.MsgBox.Alert(MESSAGES.NEWS, data.message, function () {
                            var envId = getSelectVal(idPre + "_env_select");
                            getExcludePropertiesInfo(versionId, instanceTypeId, envId);
                            var itemId = $("#" + idPre + "_item_id").val();
                            var loadUrl = '/public_config/get_product_module_list?envId=' + envId + '&versionId=' + versionId + "&itemId=" + itemId;
                            var tableId = idPre + "_product_module_table";
                            $("#" + tableId).DataTable().ajax.url(loadUrl).load(function () {
                                hiddenApplyNullLine(tableId);
                            });
                            $("#" + idPre + "_module_select").find('option[value="-1"]').prop({selected: true});
                            $("#" + idPre + "_config_select").find('option[value="-1"]').prop({selected: true});
                            $("#" + idPre + "_product_module_table_filter > label > input").val("");
                        });
                    } else {
                        $.MsgBox.Alert(MESSAGES.NEWS, data.message);
                    }
                }
            }
        });
    }
}

function setPublicMainVersionNum() {
    $("#version_num").val("GGZ" + new Date().getTime() * 1000);
}

function setPublicGrayVersionNum() {
    $("#version_num").val("GGHD" + new Date().getTime() * 1000);
}

function loadPublicModuleSelectData(id) {
    loadPublicSelect(id, "/module/module_data");
}

function initApplyQuery(table, url, filterId, versionId, itemId, envId) {
    var tableId = filterId.substring(0, filterId.length - 7);
    var idPre = filterId.substring(0, filterId.length - 27);
    loadQuery("请输入项目名称", filterId);
    var configDiv = idPre + "config_div";
    var moduleDiv = idPre + "module_div";
    var configSelectId = idPre + "config_select";
    var moduleSelectId = idPre + "module_select";
    var clearQueryId = idPre + "clear_query";
    var select = ".dataTables_filter";
    if (filterId) {
        select = "#" + filterId + select;
    }
    if ($(select + " > #" + clearQueryId).length == 0) {
        var filter = $(select);
        filter.prepend('<div class="history-query-div" id="' + configDiv + '"><span>配置：</span><select id="' + configSelectId + '"' +
            ' class="history-query-select"><option value="-1">全部</option><option value="0">未配置</option><option ' +
            'value="1">已配置</option></select></div>');
        filter.prepend('<div class="history-query-div" id="' + moduleDiv + '"><span>模块：</span><select id="' + moduleSelectId + '"' +
            ' class="history-query-select"></select></div>');
        filter.prepend('<div class="query-close" id="' + clearQueryId + '"><img src="/img/icon_guanbi.png"/></div>');
        filter.append('<div class="query-search"><img src="/img/icon_sousuo.png"/></div>');
        $(select + " > .query-search").click(function () {
            itemId = $("#" + idPre + "item_id").val();
            if (typeof(itemId) == "undefined" || itemId == "") {
                itemId = -1;
            }
            var name = $(select + " > label > input").val();
            var configId = getSelectVal(configSelectId);
            var moduleId = getSelectVal(moduleSelectId);
            var appendUrl = "";
            if (name && name.length > 0) {
                appendUrl += "&name=" + name;
            }
            table.ajax.url(url + '/get_product_module_list?envId=' + envId + '&versionId=' + versionId + '&itemId=' + itemId + "&configId=" + configId + "&moduleId=" + moduleId + appendUrl).load(function () {
                hiddenApplyNullLine(tableId);
            });
            $(select + " > label > input").val(name);
        });
        $(select + " > label > input").on('keypress', function (event) {
            if (event.keyCode == 13) {
                itemId = $("#" + idPre + "item_id").val();
                if (typeof(itemId) == "undefined" || itemId == "") {
                    itemId = -1;
                }
                var name = $(this).val();
                var configId = getSelectVal(configSelectId);
                var moduleId = getSelectVal(moduleSelectId);
                var appendUrl = "";
                if (name && name.length > 0) {
                    appendUrl = "&name=" + name;
                }
                table.ajax.url(url + '/get_product_module_list?envId=' + envId + '&versionId=' + versionId + '&itemId=' + itemId + "&configId=" + configId + "&moduleId=" + moduleId + appendUrl).load(function () {
                    hiddenApplyNullLine(tableId);
                });
                $(select + " > label > input").val(name);
            }
        });
        $(select + "> #" + clearQueryId).click(function () {
            itemId = $("#" + idPre + "item_id").val();
            if (typeof(itemId) == "undefined" || itemId == "") {
                itemId = -1;
            }
            var loadUrl = url + '/get_product_module_list?envId=' + envId + '&versionId=' + versionId + "&itemId=" + itemId;
            table.ajax.url(loadUrl).load(function () {
                hiddenApplyNullLine(tableId);
            });
            $("#" + configSelectId).find('option[value="-1"]').prop({selected: true});
            $("#" + moduleSelectId).find('option[value="-1"]').prop({selected: true});
            $(select + " > label > input").val("");
        });
    }
}

function loadPublicSelect(id, url) {
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

function loadConfigData(versionId, envId) {
    $.ajax({
        url: "/public_config/get_all_key_and_value?envId=" + envId + "&versionId=" + versionId,
        type: "post",
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
                $("#config_table > tbody").empty();
                $("#config_select_all").prop({checked: false});
                var num = 0;
                for (var i in data) {
                    var keyAndValue = data[i];
                    var versionNum;
                    if (versionId == 0) {
                        versionNum = "GGZ" + (new Date().getTime() * 1000 + num++);
                    } else {
                        versionNum = "GGHD" + (new Date().getTime() * 1000 + num++);
                    }
                    var key = keyAndValue.key;
                    var value = keyAndValue.value;
                    var comment = "";
                    var classId = keyAndValue.class_id;
                    var lineNum = keyAndValue.line_num;
                    var mustChange = keyAndValue.must_change;
                    if (keyAndValue.comment) {
                        comment = keyAndValue.comment;
                    }
                    var htmlText = '<tr><td><input name="config-input" type="checkbox"/></td><td style="display: none">'
                        + versionNum + '</td><td style="display: none">' + classId + '</td><td style="display: none">'
                        + lineNum + '</td><td><input id="key_' + i + '" type="text" name="key-input"/></td><td><input'
                        + ' id="value_' + i + '" type="text" name="value-input"/></td><td><input id="comment_' + i
                        + '" type="text" name="comment-input"/></td><td style="display: none"><input id="must_change_' + i + '" type="text" name="must-input"/></td></tr>';
                    $("#config_table > tbody").append(htmlText);
                    $("#key_" + i).val(`${key}`);
                    $("#value_" + i).val(`${value}`);
                    $("#comment_" + i).val(`${comment}`);
                    $("#must_change_" + i).val(`${mustChange}`);
                    if (mustChange == "1") {
                        $("#value_" + i).val("");
                    }
                }
                $("input:text[name=key-input]").prop({disabled: true});
                $("input:checkbox[name=config-input]").prop({checked: true});
                $("#config_select_all").prop({checked: true});
            }
        }
    });
}

function envSelectChange(versionId) {
    $("#env_select").change(function () {
        var envId = $(this).val();
        loadConfigData(versionId, envId);
    });
}

function addCopyConfig(versionId, instanceTypeId) {
    $("#save").click(function () {
        var copyConfigData = getAndValidCopyConfig(versionId, instanceTypeId);
        var flag = copyConfigData.isValid;
        if (flag) {
            $.ajax({
                url: "/public_config/insert_copy_config",
                type: "post",
                cache: false,
                traditional: true,
                data: JSON.stringify(copyConfigData),
                dataType: "json",
                contentType: "application/json",
                error: function (xhr, textStatus, thrownError) {
                    ajaxError(xhr, textStatus, thrownError);
                },
                success: function (data) {
                    if (typeof(data) == "string" && data.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                        $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                            location.reload();
                        });
                    } else {
                        if (typeof(data) == "string" && data.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                            $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                                location.reload();
                            });
                        } else {
                            $.MsgBox.Alert(MESSAGES.NEWS, data.message, function () {
                                $("#main").load(HTML.PUBLIC_CONFIG, function (response, status, xhr) {
                                    if (xhr.status != 200) {
                                        ajaxError(xhr, status, response);
                                    }
                                });
                            });
                        }
                    }
                }
            });
        }
    });
    $("#cancel").click(function () {
        $("#main").load(HTML.PUBLIC_CONFIG, function (response, status, xhr) {
            if (xhr.status != 200) {
                ajaxError(xhr, status, response);
            }
        });
    });
}

function getAndValidCopyConfig(versionId, instanceTypeId) {
    var copyConfigData = new Object();
    var copyConfigArray = new Array();
    var notValid = false;
    $("#config_info").empty();
    $("#config_table > tbody > tr").each(function () {
        if (notValid) {
            return;
        }
        var copyConfigTr = new Object();
        copyConfigTr.versionNum = $(this).children("td:eq(1)").text();
        copyConfigTr.classId = $(this).children("td:eq(2)").text();
        copyConfigTr.lineNum = $(this).children("td:eq(3)").text();
        copyConfigTr.key = $(this).children("td:eq(4)").children("input").val();
        copyConfigTr.value = $(this).children("td:eq(5)").children("input").val();
        copyConfigTr.comment = $(this).children("td:eq(6)").children("input").val();
        copyConfigTr.mustChange = $(this).children("td:eq(7)").children("input").val();
        if (typeof(copyConfigTr.value) == "undefined" || copyConfigTr.value.length < 1) {
            $("#config_info").append("请把值填写完整");
            copyConfigData.isValid = false;
            notValid = true;
            return;
        }
        copyConfigArray.push(copyConfigTr);
    });
    if (!validTableCheckBoxSelected("config_table", "config_info", "config-input")) {
        copyConfigData.isValid = false;
        return copyConfigData;
    }
    if (notValid) {
        return copyConfigData;
    }
    copyConfigData.isValid = true;
    if (versionId == 0) {
        copyConfigData.envId = getItem("copyEnvId");
    } else {
        copyConfigData.envId = getItem("grayCopyEnvId");
    }
    copyConfigData.versionId = versionId;
    copyConfigData.copyConfigList = copyConfigArray;
    copyConfigData.instanceTypeId = instanceTypeId;
    return copyConfigData;
}

function validTableCheckBoxSelected(tableId, infoId, name) {
    var isSelected = false;
    $('#' + tableId + ' > tbody > tr > td > input:checkbox[name=' + name + ']:checked').each(function (i) {
        isSelected = true;
    });
    if (!isSelected) {
        $("#" + infoId).empty();
        $("#" + infoId).append(MESSAGES.NOT_SELECTED);
    }
    return isSelected;
}

function hiddenApplyNullLine (tableId) {
    if (tableId.indexOf("json") != -1 || tableId.indexOf("xml") != -1 || tableId.indexOf("yml") != -1) {
        $("#" + tableId + " > thead").css({"display": "none"});
    }
}