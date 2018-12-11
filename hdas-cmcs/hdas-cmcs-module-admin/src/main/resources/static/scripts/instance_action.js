/**
 * Created by maodi on 2018/6/5.
 */
function loadApp(itemId, appIdd, selectName) {
    $("#" + appIdd).empty();
    var appId = "app";
    if (appIdd) {
        appId = appIdd;
    }
    var name = "app-input";
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
            "itemId": itemId
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

function loadUpdatePropertiesVal() {
    var key = getItem("key");
    var value = getItem("value");
    var mainValue = getItem("main_value");
    var comment = getItem("comment");
    var itemId = getItem("id");
    $("#key").val(key);
    $("#value").val(value);
    $("#main_value").val(mainValue);
    $("#comment").val(comment);
    $("#item_id").val(itemId);
    loadApp(itemId);
}

function addPropertiesInstance(productModuleEnvId) {
    addAction(function () {
        return getInstance(0);
    }, function () {
        return validInstanceData();
    }, "/instance/insert?productModuleEnvId=" + productModuleEnvId, HTML.INSTANCE + "?productModuleEnvId=" + productModuleEnvId, false, "key_info");
}

function updatePropertiesInstance(productModuleEnvId) {
    updateAction(function () {
        return getInstance(0);
    }, function () {
        return validInstanceData();
    }, "/instance/update?productModuleEnvId=" + productModuleEnvId, HTML.INSTANCE + "?productModuleEnvId=" + productModuleEnvId, false, "key_info");
}

function getInstance(instanceTypeId) {
    var key = $("#key").val();
    var value = $("#value").val();
    var comment = $("#comment").val();
    var versionId = $("#version_id").val();
    var releaseStatus = $("#release_status").val();
    var itemId = $("#item_id").val();
    var resourceId = $("#resource_id").val();
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
    instanceInfo.resourceId = resourceId;
    instanceInfo.appIds = appIds;
    instanceInfo.classId = getClassId();
    instanceInfo.mustChange = getMustChange();
    if (versionNum) {
        instanceInfo.versionNum = versionNum;
    }
    return instanceInfo;
}

function validInstanceData() {
    var key = $("#key").val();
    var value = $("#value").val();
    var comment = $("#comment").val();
    var flag = true;
    if (!validValueShowInfo(key, 50, "key_info")) {
        flag = false;
    }
    if (!validValueNotStartWithShowInfo(key, "key_info")) {
        flag = false;
    }
    if (!validValueShowInfo(value, -1, "value_info")) {
        flag = false;
    }
    if (!validValueShowInfo(comment, 500, "comment_info", true)) {
        flag = false;
    }
    return flag;
}

function excludePropertiesRelease(productModuleEnvId) {
    $("#json_release").click(function () {
        validRelease("json", productModuleEnvId, 0, 1);
    });
    $("#gray_json_release").click(function () {
        validRelease("gray_json", productModuleEnvId, 1, 1);
    });
    $("#xml_release").click(function () {
        validRelease("xml", productModuleEnvId, 0, 2);
    });
    $("#gray_xml_release").click(function () {
        validRelease("gray_xml", productModuleEnvId, 1, 2);
    });
    $("#yml_release").click(function () {
        validRelease("yml", productModuleEnvId, 0, 3);
    });
    $("#gray_yml_release").click(function () {
        validRelease("gray_yml", productModuleEnvId, 1, 3);
    });
}

function validRelease(idPre, productModuleEnvId, versionId, instanceTypeId) {
    var url = "/instance/release?productModuleEnvId=" + productModuleEnvId + "&versionId=" + versionId;
    if (validSelectedAppShowInfo(idPre + "_app", idPre + "_app_info")) {
        $.MsgBox.Release(MESSAGES.NEWS, MESSAGES.IS_RELEASE, function () {
            excludeAjaxAction(url, getItemId(idPre), productModuleEnvId, versionId, instanceTypeId);
        });
    }
}

function excludePropertiesOffline(productModuleEnvId) {
    $("#json_offline").click(function () {
        validOffline("json", productModuleEnvId, 0, 1);
    });
    $("#gray_json_offline").click(function () {
        validOffline("gray_json", productModuleEnvId, 1, 1);
    });
    $("#xml_offline").click(function () {
        validOffline("xml", productModuleEnvId, 0, 2);
    });
    $("#gray_xml_offline").click(function () {
        validOffline("gray_xml", productModuleEnvId, 1, 2);
    });
    $("#yml_offline").click(function () {
        validOffline("yml", productModuleEnvId, 0, 3);
    });
    $("#gray_yml_offline").click(function () {
        validOffline("gray_yml", productModuleEnvId, 1, 3);
    });
}

function validOffline(idPre, productModuleEnvId, versionId, instanceTypeId) {
    var url = "/instance/offline?productModuleEnvId=" + productModuleEnvId + "&versionId=" + versionId;
    var releaseStatus = $("#" + idPre + "_release_status").text();
    if (releaseStatus == "未发布") {
        $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.OFFLINE);
    } else {
        $.MsgBox.Offline(MESSAGES.NEWS, MESSAGES.IS_OFFLINE, function () {
            excludeAjaxAction(url, getItemId(idPre), productModuleEnvId, versionId, instanceTypeId);
        });
    }
}

function excludePropertiesDelete(productModuleEnvId) {
    var url = "/instance/delete_by_ids?productModuleEnvId=" + productModuleEnvId;
    $("#json_del").click(function () {
        excludeDeleteConfirm(url + "&versionId=0", getItemId("json"), productModuleEnvId, 0, 1, "json");
    });
    $("#gray_json_del").click(function () {
        excludeDeleteConfirm(url + "&versionId=1", getItemId("gray_json"), productModuleEnvId, 1, 1, "gray_json");
    });
    $("#xml_del").click(function () {
        excludeDeleteConfirm(url + "&versionId=0", getItemId("xml"), productModuleEnvId, 0, 2, "xml");
    });
    $("#gray_xml_del").click(function () {
        excludeDeleteConfirm(url + "&versionId=1", getItemId("gray_xml"), productModuleEnvId, 1, 2, "gray_xml");
    });
    $("#yml_del").click(function () {
        excludeDeleteConfirm(url + "&versionId=0", getItemId("yml"), productModuleEnvId, 0, 3, "yml");
    });
    $("#gray_yml_del").click(function () {
        excludeDeleteConfirm(url + "&versionId=1", getItemId("gray_yml"), productModuleEnvId, 1, 3, "gray_yml");
    });
}

function excludeDeleteConfirm(url, itemId, productModuleEnvId, versionId, instanceTypeId, idPre) {
    $.MsgBox.Delete(MESSAGES.NEWS, MESSAGES.IS_DELETE, function () {
        excludeDeleteAction(url, itemId, productModuleEnvId, versionId, instanceTypeId, idPre)
    });
}

function excludeDeleteAction(url, itemId, productModuleEnvId, versionId, instanceTypeId, idPre) {
    var itemIds = new Array();
    itemIds.push(itemId);
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
                        getExcludePropertiesInfo(productModuleEnvId, versionId, instanceTypeId);
                    });
                    removeItem(idPre + "_value");
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

function excludeAjaxAction(url, itemId, productModuleEnvId, versionId, instanceTypeId) {
    var itemIds = new Array();
    itemIds.push(itemId);
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
                        getExcludePropertiesInfo(productModuleEnvId, versionId, instanceTypeId);
                    });
                } else {
                    $.MsgBox.Alert(MESSAGES.NEWS, data.message);
                }
            }
        }
    });
}

function excludePropertiesEdit(productModuleEnvId) {
    var instanceInfo = new Object();
    instanceInfo.key = "content";
    $("#json_edit").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "json", productModuleEnvId, "update");
    });
    $("#gray_json_edit").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "gray_json", productModuleEnvId, "update");
    });
    $("#xml_edit").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "xml", productModuleEnvId, "update");
    });
    $("#gray_xml_edit").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "gray_xml", productModuleEnvId, "update");
    });
    $("#yml_edit").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "yml", productModuleEnvId, "update");
    });
    $("#gray_yml_edit").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "gray_yml", productModuleEnvId, "update");
    });
}

function excludePropertiesSave(productModuleEnvId) {
    var instanceInfo = new Object();
    instanceInfo.key = "content";
    $("#json_save").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "json", productModuleEnvId, "add");
    });
    $("#gray_json_save").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "gray_json", productModuleEnvId, "add");
    });
    $("#xml_save").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "xml", productModuleEnvId, "add");
    });
    $("#gray_xml_save").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "gray_xml", productModuleEnvId, "add");
    });
    $("#yml_save").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "yml", productModuleEnvId, "add");
    });
    $("#gray_yml_save").click(function () {
        makeExcludePropertiesInstance(instanceInfo, "gray_yml", productModuleEnvId, "add");
    });
}

function makeExcludePropertiesInstance(instanceInfo, idPre, productModuleEnvId, action) {
    var back;
    if (action == "add") {
        back = "_save";
    } else {
        back = "_edit"
    }
    if ($("#" + idPre + back).prop("disabled") == "disabled") {
        return;
    }
    $("#" + idPre + back).prop({disabled: "disabled"});
    var itemId = getItemId(idPre);
    if (action == "update") {
        if (typeof(itemId) == "undefined" || itemId.length < 1) {
            $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.ADD_BEFORE);
            $("#" + idPre + "_edit").removeAttr("disabled");
            return;
        }
    }
    if (itemId) {
        if (action == "add") {
            $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.ADDED);
            $("#" + idPre + "_save").removeAttr("disabled");
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
    instanceInfo.resourceId = getItem("mainResourceId");
    if (idPre.startWith("gray")) {
        versionId = 1;
        instanceInfo.resourceId = getItem("grayResourceId");
    }
    instanceInfo.versionId = versionId;
    var versionNum = $("#" + idPre + "_version_num").val();
    if (versionNum) {
        instanceInfo.versionNum = versionNum;
    }
    if (action == "update") {
        excludePropertiesUpdateAction(instanceInfo, idPre, productModuleEnvId, versionId, instanceTypeId);
    } else {
        excludePropertiesAddAction(instanceInfo, idPre, productModuleEnvId, versionId, instanceTypeId);
    }
}

function validExcludePropertiesAddAndUpdate(instanceInfo, idPre) {
    var flag = true;
    $("#" + idPre + "_app_info").empty();
    if (!validValueShowInfo(instanceInfo.value, -1, idPre + "_value_info")) {
        flag = false;
    }
    if (!validValueShowInfo(instanceInfo.comment, 500, idPre + "_comment_info", true)) {
        flag = false;
    }
    return flag;
}

function validExcludeProperties(instanceInfo, idPre) {
    var flag = true;
    if (!validValueShowInfo(instanceInfo.value, -1, idPre + "_value_info")) {
        flag = false;
    }
    if (!validSelectedAppShowInfo(idPre + "_app", idPre + "_app_info")) {
        flag = false;
    }
    if (!validValueShowInfo(instanceInfo.comment, 500, idPre + "_comment_info", true)) {
        flag = false;
    }
    return flag;
}

function excludePropertiesAddAction(instanceInfo, idPre, productModuleEnvId, versionId, instanceTypeId) {
    if (validExcludePropertiesAddAndUpdate(instanceInfo, idPre)) {
        $.ajax({
            url: "/instance/insert?productModuleEnvId=" + productModuleEnvId + "&versionId=" + versionId,
            type: "post",
            data: instanceInfo,
            cache: false,
            traditional: true,
            dataType: "json",
            error: function (xhr, textStatus, thrownError) {
                ajaxError(xhr, textStatus, thrownError);
                $("#" + idPre + "_save").removeAttr("disabled");
            },
            success: function (data) {
                if (typeof(data) == "string" && data.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                    $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                        location.reload();
                    });
                } else {
                    if (data.code == 200) {
                        $.MsgBox.Alert(MESSAGES.NEWS, data.message, function () {
                            getExcludePropertiesInfo(productModuleEnvId, versionId, instanceTypeId);
                        });
                    } else {
                        $.MsgBox.Alert(MESSAGES.NEWS, data.message);
                    }
                }
                $("#" + idPre + "_save").removeAttr("disabled");
            }
        });
    } else {
        $("#" + idPre + "_save").removeAttr("disabled");
    }
}

function excludePropertiesUpdateAction(instanceInfo, idPre, productModuleEnvId, versionId, instanceTypeId) {
    if (validExcludePropertiesAddAndUpdate(instanceInfo, idPre)) {
        $.ajax({
            url: "/instance/update?productModuleEnvId=" + productModuleEnvId + "&versionId=" + versionId,
            type: "post",
            data: instanceInfo,
            cache: false,
            traditional: true,
            dataType: "json",
            error: function (xhr, textStatus, thrownError) {
                ajaxError(xhr, textStatus, thrownError);
                $("#" + idPre + "_edit").removeAttr("disabled");
            },
            success: function (data) {
                if (typeof(data) == "string" && data.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                    $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                        location.reload();
                    });
                } else {
                    if (data.code == 200) {
                        $.MsgBox.Alert(MESSAGES.NEWS, data.message, function () {
                            getExcludePropertiesInfo(productModuleEnvId, versionId, instanceTypeId);
                        });
                    } else {
                        $.MsgBox.Alert(MESSAGES.NEWS, data.message);
                    }
                }
                $("#" + idPre + "_edit").removeAttr("disabled");
            }
        });
    } else {
        $("#" + idPre + "_edit").removeAttr("disabled");
    }
}

function setInstanceMainVersionNum() {
    $("#version_num").val("Z" + new Date().getTime() * 1000);
}

function setInstanceGrayVersionNum() {
    $("#version_num").val("HD" + new Date().getTime() * 1000);
}

function envSelectChange(productModuleEnvId, versionId) {
    $("#env_select").change(function () {
        var envId = $(this).val();
        loadConfigData(productModuleEnvId, versionId, envId);
    });
}

function loadConfigData(productModuleEnvId, versionId, envId) {
    var ids = productModuleEnvId.split("_");
    productModuleEnvId = ids[0] + "_" + ids[1] + "_" + envId;
    $.ajax({
        url: "/instance/get_all_key_and_value?productModuleEnvId=" + productModuleEnvId + "&versionId=" + versionId,
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
                        versionNum = "Z" + (new Date().getTime() * 1000 + num++);
                    } else {
                        versionNum = "HD" + (new Date().getTime() * 1000 + num++);
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

function addCopyConfig(productModuleEnvId, versionId, instanceTypeId) {
    $("#save").click(function () {
        var copyConfigData = getAndValidCopyConfig(productModuleEnvId, versionId, instanceTypeId);
        var flag = copyConfigData.isValid;
        if (flag) {
            $.ajax({
                url: "/instance/insert_copy_config",
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
                                $("#main").load(HTML.INSTANCE + "?productModuleEnvId=" + productModuleEnvId, function (response, status, xhr) {
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
        $("#main").load(HTML.INSTANCE + "?productModuleEnvId=" + productModuleEnvId, function (response, status, xhr) {
            if (xhr.status != 200) {
                ajaxError(xhr, status, response);
            }
        });
    });
}

function getAndValidCopyConfig(productModuleEnvId, versionId, instanceTypeId) {
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
            $("#config_info").append("值有空，请把值填写完整");
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
    copyConfigData.productModuleEnvId = productModuleEnvId;
    copyConfigData.versionId = versionId;
    copyConfigData.copyConfigList = copyConfigArray;
    copyConfigData.instanceTypeId = instanceTypeId;
    copyConfigData.resourceId = getItem("mainResourceId");
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

function loadEnvSelect(id, url, callback) {
    //清空
    $("#" + id).empty();
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
                callback();
            }
        }
    });
}