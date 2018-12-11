/**
 * Created by maodi on 2018/7/3.
 */
function loadInstanceData() {
    initShowVersionTab();
}

function loadMainData() {
    loadMainJsonEnvSelect(function () {
        var jsonEnvId = getSelectVal("json_env_select");
        getExcludePropertiesInfo(0, 1, jsonEnvId);
    });
    loadMainXmlEnvSelect(function () {
        var xmlEnvId = getSelectVal("xml_env_select");
        getExcludePropertiesInfo(0, 2, xmlEnvId);
    });
    loadMainYmlEnvSelect(function () {
        var ymlEnvId = getSelectVal("yml_env_select");
        getExcludePropertiesInfo(0, 3, ymlEnvId);
    });
}

function loadGrayData() {
    loadGrayJsonEnvSelect(function () {
        var jsonEnvId = getSelectVal("gray_json_env_select");
        getExcludePropertiesInfo(1, 1, jsonEnvId);
    });
    loadGrayXmlEnvSelect(function () {
        var xmlEnvId = getSelectVal("gray_xml_env_select");
        getExcludePropertiesInfo(1, 2, xmlEnvId);
    });
    loadGrayYmlEnvSelect(function () {
        var ymlEnvId = getSelectVal("gray_yml_env_select");
        getExcludePropertiesInfo(1, 3, ymlEnvId);
    });
}

function clickVersionBtn() {
    $("#main_version_btn").click(function () {
        if ($("#main_properties_table").children("tbody").length < 1) {
            loadMainPropertiesTable("main_properties_table");
            loadMainData();
            loadQuery("请输入键查询");
        } else {
            if ($("#properties_tab").hasClass("tab-li-active")) {
                $('#main_properties_table').DataTable().ajax.reload();
            }
            loadMainData();
        }
        mainVersionActive();
    });
    $("#gray_version_btn").click(function () {
        if ($("#gray_properties_table").children("tbody").length < 1) {
            loadGrayPropertiesTable("gray_properties_table");
            loadGrayData();
            loadQuery("请输入键查询");
        } else {
            if ($("#gray_properties_tab").hasClass("tab-li-active")) {
                $('#gray_properties_table').DataTable().ajax.reload();
            }
            loadGrayData();
        }
        grayVersionActive();
    });
}

function mainVersionActive() {
    $("#main_version_btn").addClass("version-btn-active");
    $("#gray_version_btn").removeClass("version-btn-active");
    $("#gray_version_tab").addClass("sys-hidden");
    $("#main_version_tab").removeClass("sys-hidden");
    var id = "properties_tab";
    $("#tab_instance").children("li.tab-li-active").each(function (i, el) {
        id = $(el).attr("id");
    });
    $("#gray_import_btn").addClass("sys-hidden");
    $("#gray_export_btn").addClass("sys-hidden");
    $("#gray_delete_btn").addClass("sys-hidden");
    $("#gray_release_btn").addClass("sys-hidden");
    $("#import_btn").removeClass("sys-hidden");
    $("#export_btn").removeClass("sys-hidden");
    if (id == "properties_tab") {
        $("#add_btn").removeClass("sys-hidden");
        $("#delete_btn").removeClass("sys-hidden");
        $("#release_btn").removeClass("sys-hidden");
    }
    setItem("versionId", 0);
}

function grayVersionActive() {
    $("#gray_version_btn").addClass("version-btn-active");
    $("#main_version_btn").removeClass("version-btn-active");
    $("#main_version_tab").addClass("sys-hidden");
    $("#gray_version_tab").removeClass("sys-hidden");
    var id = "gray_properties_tab";
    $("#gray_tab_instance").children("li.tab-li-active").each(function (i, el) {
        id = $(el).attr("id");
    });
    $("#import_btn").addClass("sys-hidden");
    $("#export_btn").addClass("sys-hidden");
    $("#add_btn").addClass("sys-hidden");
    $("#delete_btn").addClass("sys-hidden");
    $("#release_btn").addClass("sys-hidden");
    $("#gray_import_btn").removeClass("sys-hidden");
    $("#gray_export_btn").removeClass("sys-hidden");
    if (id == "gray_properties_tab") {
        $("#gray_delete_btn").removeClass("sys-hidden");
        $("#gray_release_btn").removeClass("sys-hidden");
    }
    setItem("versionId", 1);
}

function clickTabInstanceLi() {
    //主页面操作
    $("#properties_tab").click(function () {
        addMainInstanceClass("main_properties_div", "properties_tab");
        setItem("instanceTypeId", 0);
        tabTopBtn("properties_tab");
        showEnvSelect("properties_env_div");
    });
    $("#json_tab").click(function () {
        addMainInstanceClass("json_div", "json_tab");
        setItem("instanceTypeId", 1);
        tabTopBtn("json_tab");
        showEnvSelect("json_env_div");
        setApplyTableThWidth("json_product_module_table");
    });
    $("#xml_tab").click(function () {
        addMainInstanceClass("xml_div", "xml_tab");
        setItem("instanceTypeId", 2);
        tabTopBtn("xml_tab");
        showEnvSelect("xml_env_div");
        setApplyTableThWidth("xml_product_module_table");
    });
    $("#yml_tab").click(function () {
        addMainInstanceClass("yml_div", "yml_tab");
        setItem("instanceTypeId", 3);
        tabTopBtn("yml_tab");
        showEnvSelect("yml_env_div");
        setApplyTableThWidth("yml_product_module_table");
    });
    //灰度页面操作
    $("#gray_properties_tab").click(function () {
        addGrayInstanceClass("gray_properties_div", "gray_properties_tab");
        setItem("instanceTypeId", 0);
        tabTopGrayBtn("gray_properties_tab");
        showEnvSelect("gray_properties_env_div");
    });
    $("#gray_json_tab").click(function () {
        addGrayInstanceClass("gray_json_div", "gray_json_tab");
        setItem("instanceTypeId", 1);
        tabTopGrayBtn("gray_json_tab");
        showEnvSelect("gray_json_env_div");
        setApplyTableThWidth("gray_json_product_module_table");
    });
    $("#gray_xml_tab").click(function () {
        addGrayInstanceClass("gray_xml_div", "gray_xml_tab");
        setItem("instanceTypeId", 2);
        tabTopGrayBtn("gray_xml_tab");
        showEnvSelect("gray_xml_env_div");
        setApplyTableThWidth("gray_xml_product_module_table");
    });
    $("#gray_yml_tab").click(function () {
        addGrayInstanceClass("gray_yml_div", "gray_yml_tab");
        setItem("instanceTypeId", 3);
        tabTopGrayBtn("gray_yml_tab");
        showEnvSelect("gray_yml_env_div");
        setApplyTableThWidth("gray_yml_product_module_table");
    });
}

function tabTopBtn(tabId) {
    $("#add_btn").removeClass("sys-hidden");
    $("#delete_btn").removeClass("sys-hidden");
    $("#release_btn").removeClass("sys-hidden");
    if (tabId == "json_tab" || tabId == "xml_tab" || tabId == "yml_tab") {
        $("#add_btn").addClass("sys-hidden");
        $("#delete_btn").addClass("sys-hidden");
        $("#release_btn").addClass("sys-hidden");
    }
}

function tabTopGrayBtn(tabId) {
    $("#gray_delete_btn").removeClass("sys-hidden");
    $("#gray_release_btn").removeClass("sys-hidden");
    if (tabId == "gray_json_tab" || tabId == "gray_xml_tab" || tabId == "gray_yml_tab") {
        $("#gray_delete_btn").addClass("sys-hidden");
        $("#gray_release_btn").addClass("sys-hidden");
    }
}

function addMainInstanceClass(divId, liId) {
    $("#main_properties_div").addClass("sys-hidden");
    $("#json_div").addClass("sys-hidden");
    $("#xml_div").addClass("sys-hidden");
    $("#yml_div").addClass("sys-hidden");
    $("#" + divId).removeClass("sys-hidden");
    $("#properties_tab").removeClass("tab-li-active");
    $("#json_tab").removeClass("tab-li-active");
    $("#xml_tab").removeClass("tab-li-active");
    $("#yml_tab").removeClass("tab-li-active");
    $("#" + liId).addClass("tab-li-active");
}

function addGrayInstanceClass(divId, liId) {
    $("#gray_properties_div").addClass("sys-hidden");
    $("#gray_json_div").addClass("sys-hidden");
    $("#gray_xml_div").addClass("sys-hidden");
    $("#gray_yml_div").addClass("sys-hidden");
    $("#" + divId).removeClass("sys-hidden");
    $("#gray_properties_tab").removeClass("tab-li-active");
    $("#gray_json_tab").removeClass("tab-li-active");
    $("#gray_xml_tab").removeClass("tab-li-active");
    $("#gray_yml_tab").removeClass("tab-li-active");
    $("#" + liId).addClass("tab-li-active");
}

function loadMainPropertiesTable(tableId) {
    setItem("versionId", 0);
    loadSelectEnv("");
    var mainTable = $('#' + tableId).DataTable({
        ajax: {
            url: '/public_config/query_by_name',
            dataSrc: 'data',
            data: {
                'versionId': 0,
                'envId': getSelectVal("properties_env_select")
            }
        },
        columns: [
            {data: ''},
            {data: 'num', visible: false},
            {data: 'version_num'},
            {data: 'apply'},
            {data: 'key'},
            {data: 'value'},
            {data: 'app_num'},
            {data: 'comment'},
            {data: 'release_status'},
            {data: 'last_update_by'},
            {data: 'update_time'},
            {data: 'num'},
            {
                data: 'id',
                visible: false
            }, {
                data: 'hasRole',
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
            defaultContent: '<input type="checkbox" name="main-select"/>'
        }, {
            targets: 2,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 12)}`);
            }
        }, {
            targets: 3,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 12)}`);
            }
        }, {
            targets: 4,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 70)}`);
            }
        }, {
            targets: 5,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 70)}`);
            }
        }, {
            targets: 6,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                instanceNumSelect(sData, nTd);
            }
        }, {
            targets: 7,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 12)}`);
            }
        }, {
            targets: 8,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 12)}`);
            }
        }, {
            targets: 9,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 12)}`);
            }
        }, {
            targets: 10,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 20)}`);
            }
        }, {
            targets: 11,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                if (oData.hasRole == "1") {
                    if (oData.release_status == "已发布") {
                        $(nTd).html(CONTENT.ADD_EDIT_OFFLINE);
                    } else {
                        $(nTd).html(CONTENT.ADD_RELEASE_EDIT);
                    }
                } else {
                    $(nTd).html('<span class="sys-hidden"></span>');
                }
            }
        }],
        info: false,
        lengthChange: false,
        paging: true,
        searching: true,
        processing: true,
        serverSide: true,
        ordering: false,
        initComplete: function (settings, json) {
            batchDelete("/public_config/delete_by_ids?versionId=0", tableId);
            batchRelease(tableId);
        },
        drawCallback: function (settings) {
            initPublicQuery(mainTable, "/public_config", "main_properties_table_filter", 0, settings, "view_all", "copy_config");
            initToPage(settings, mainTable, "main_properties_table_wrapper");
            clickTableTr("main-select");
            updatePropertiesPage(tableId, HTML.PUBLIC_CONFIG_UPDATE);
            grayAddPage(tableId, HTML.PUBLIC_CONFIG_GRAY_ADD);
            singlePropertiesDelete("/public_config/delete_by_ids?versionId=0", tableId);
            singleRelease(tableId);
            offlineProperties(tableId);
            getUserAuthResourcePage(tableId, HTML.PUBLIC_CONFIG, 0);
            $("#main_version_tab").removeClass("sys-hidden");
            $("#main_properties_div").removeClass("sys-hidden");
            $("#" + tableId).css("width", "100%");
            if (settings._iRecordsTotal < 1) {
                $(".dataTables_empty").css("width", "100%");
            }
            loadQuery("请输入键名称", "main_properties_table_filter");
        }
    });
}

function loadGrayPropertiesTable(tableId) {
    setItem("versionId", 1);
    loadSelectEnv("gray_");
    var grayTable = $('#' + tableId).DataTable({
        ajax: {
            url: '/public_config/query_by_name',
            dataSrc: 'data',
            data: {
                'versionId': 1,
                'envId': getSelectVal("gray_properties_env_select")
            }
        },
        columns: [
            {data: ''},
            {data: 'num', visible: false},
            {data: 'version_num'},
            {data: 'apply'},
            {data: 'key'},
            {data: 'main_value'},
            {data: 'value'},
            {data: 'app_num'},
            {data: 'comment'},
            {data: 'release_status'},
            {data: 'last_update_by'},
            {data: 'update_time'},
            {data: 'num'},
            {
                data: 'id',
                visible: false
            }, {
                data: 'hasRole',
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
            defaultContent: '<input type="checkbox" name="gray-select"/>'
        }, {
            targets: 2,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 12)}`);
            }
        }, {
            targets: 3,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 12)}`);
            }
        }, {
            targets: 4,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 45)}`);
            }
        }, {
            targets: 5,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 45)}`);
            }
        }, {
            targets: 6,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 48)}`);
            }
        }, {
            targets: 7,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                instanceNumSelect(sData, nTd);
            }
        }, {
            targets: 8,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 12)}`);
            }
        }, {
            targets: 9,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 12)}`);
            }
        }, {
            targets: 10,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 12)}`);
            }
        }, {
            targets: 11,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 20)}`);
            }
        }, {
            targets: 12,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                if (oData.hasRole == "1") {
                    if (oData.release_status == "已发布") {
                        $(nTd).html(CONTENT.EDIT_OFFLINE);
                    } else {
                        $(nTd).html(CONTENT.RELEASE_EDIT);
                    }
                } else {
                    $(nTd).html('<span class="sys-hidden"></span>');
                }
            }
        }],
        info: false,
        lengthChange: false,
        paging: true,
        searching: true,
        processing: true,
        serverSide: true,
        ordering: false,
        initComplete: function (settings, json) {
            batchGrayDelete("/public_config/delete_by_ids?versionId=1", tableId);
            batchGrayRelease(tableId);
        },
        drawCallback: function (settings) {
            initPublicQuery(grayTable, "/public_config", "gray_properties_table_filter", 1, settings, "gray_view_all", "gray_copy_config");
            initToPage(settings, grayTable, "gray_properties_table_wrapper");
            clickTableTr("gray-select");
            updateGrayPropertiesPage(tableId, HTML.PUBLIC_CONFIG_GRAY_UPDATE);
            singleGrayPropertiesDelete("/public_config/delete_by_ids?versionId=1", tableId);
            singleGrayRelease(tableId);
            offlineGrayProperties(tableId);
            getUserAuthResourcePage(tableId, HTML.PUBLIC_CONFIG, 1);
            $("#gray_version_tab").removeClass("sys-hidden");
            $("#gray_properties_div").removeClass("sys-hidden");
            $("#" + tableId).css("width", "100%");
            if (settings._iRecordsTotal < 1) {
                $(".dataTables_empty").css("width", "100%");
            }
            loadQuery("请输入键名称", "gray_properties_table_filter");
        }
    });
}

function importInstance() {
    $("#import_btn").click(function () {
        importInstanceAction(0);
    });
    $("#gray_import_btn").click(function () {
        importInstanceAction(1);
    });
}

function importInstanceAction(versionId) {
    var instanceTypeId = getInstanceTypeId(versionId);
    var msgHead = "请选择ANSI或者UTF-8无BOM格式编码";
    var msgBody = "";
    var msgFoot = "文件";
    var selectId;
    if (instanceTypeId == 0) {
        msgBody = ".properties";
        selectId = "properties_env_select";
    } else if (instanceTypeId == 1) {
        msgBody = ".json";
        selectId = "json_env_select";
    } else if (instanceTypeId == 2) {
        msgBody = ".xml";
        selectId = "xml_env_select";
    } else if (instanceTypeId == 3) {
        msgBody = ".yml";
        selectId = "yml_env_select";
    }
    var tableId = "";
    if (versionId == 0) {
        tableId = "main_properties_table";
    } else {
        tableId = "gray_properties_table";
        selectId = "gray_" + selectId;
    }
    var envId = getSelectVal(selectId);
    $.MsgBox.Upload("上传文件", msgHead + msgBody + msgFoot, function () {
        upload(versionId, instanceTypeId, tableId);
    }, function () {
        if (instanceTypeId == 0) {
            var table = $('#' + tableId).DataTable();
            table.ajax.url('/public_config/query_by_name?versionId=' + versionId + "&envId=" + envId).load();
        } else {
            getExcludePropertiesInfo(versionId, instanceTypeId, envId);
        }
    });
}

function upload(versionId, instanceTypeId, tableId) {
    var formData = new FormData($("#uploadForm")[0]);
    var file = formData.get("file");
    if (file.size < 1) {
        alert(file.name + "为空文件");
        return false;
    }
    if (file.name.length < 1) {
        alert("未选择上传的文件");
        return false;
    }
    var idPre;
    if (versionId == 0) {
        idPre = "";
    } else {
        idPre = "gray_";
    }
    var msgBody = "";
    var selectId = idPre + "_env_select";
    if (instanceTypeId == 0) {
        msgBody = ".properties";
        selectId = idPre + "properties_env_select";
    } else if (instanceTypeId == 1) {
        msgBody = ".json";
        idPre += "json";
        selectId = idPre + "_env_select";
    } else if (instanceTypeId == 2) {
        msgBody = ".xml";
        idPre += "xml";
        selectId = idPre + "_env_select";
    } else if (instanceTypeId == 3) {
        msgBody = ".yml";
        idPre += "yml";
        selectId = idPre + "_env_select";
    }
    var envId = getSelectVal(selectId);
    if (!file.name.endWith(msgBody)) {
        alert("请选择" + msgBody + "文件");
        return false;
    }
    formData.append("instanceTypeId", parseInt(instanceTypeId));
    formData.append("versionId", parseInt(versionId));
    formData.append("envId", parseInt(envId));
    var itemId = $("#" + idPre + "_item_id").val();
    if (itemId) {
        formData.append("itemId", parseInt(itemId));
    }
    var importDataAjax = $.ajax({
        url: "/public_config/upload",
        type: "POST",
        timeout: 30000,
        async: false,
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        success: function (response) {
            if (typeof(response) == "string" && response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                //超时取消上传框
                $("#mb_box,#mb_con").remove();
                $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                    location.reload();
                });
            } else {
                //上传过后取消上传框
                $("#mb_box,#mb_con").remove();
                $.MsgBox.Alert(MESSAGES.NEWS, response.message, function () {
                    //刷新页面数据
                    if (instanceTypeId == 0) {
                        var table = $('#' + tableId).DataTable();
                        var selectId;
                        if (versionId == 0) {
                            selectId = "properties_env_select";
                        } else {
                            selectId = "gray_properties_env_select";
                        }
                        table.ajax.url('/public_config/query_by_name?versionId=' + versionId + "&envId=" + getSelectVal(selectId)).load();
                    } else {
                        getExcludePropertiesInfo(versionId, instanceTypeId, envId);
                    }
                });
            }
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                importDataAjax.abort();
                window.location.href = "/timeout";
            }
        },
        error: function (xhr, textStatus, thrownError) {
            //出错过后取消上传框
            $("#mb_box,#mb_con").remove();
            ajaxError(xhr, textStatus, thrownError);
        }
    });
    return true;
}

function exportInstance() {
    $("#export_btn").click(function () {
        exportInstanceAction(0);
    });
    $("#gray_export_btn").click(function () {
        exportInstanceAction(1);
    });
}

function exportInstanceAction(versionId) {
    var instanceTypeId = getInstanceTypeId(versionId);
    var tableId = "";
    if (parseInt(versionId) == 0) {
        tableId = "main_properties_table";
    } else {
        tableId = "gray_properties_table";
    }
    if (instanceTypeId == 0) {
        //获取到选中的
        var itemIds = getCheckBoxArray(tableId);
        if (itemIds.length < 1) {
            $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.NOT_SELECTED);
            return false;
        }
        var itemIdsUrl = "&itemIds[]="
        var url = "/public_config/download?instanceTypeId=" + instanceTypeId + "&versionId=" + versionId;
        for (var i in itemIds) {
            var id = itemIds[i];
            url += itemIdsUrl + id;
        }
        $.MsgBox.Confirm(MESSAGES.NEWS, MESSAGES.IS_EXPORT, function () {
            window.location.href = url;
        })
    } else {
        var idPre;
        if (versionId == 0) {
            idPre = "";
        } else if (versionId == 1) {
            idPre = "gray_";
        }
        if (instanceTypeId == 1) {
            idPre += "json_";
        } else if (instanceTypeId == 2) {
            idPre += "xml_";
        } else if (instanceTypeId == 3) {
            idPre += "yml_";
        }
        var value = getItem(idPre + "value");
        if (value) {
            var itemId = $("#" + idPre + "item_id").val();
            var url = "/public_config/download?instanceTypeId=" + instanceTypeId + "&versionId=" + versionId + "&itemIds[]=" + itemId;
            window.location.href = url;
        } else {
            $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.NOT_SAVE_VALUE);
        }
    }
}

function batchRelease(tableId) {
    $("#release_btn").click(function () {
        batchReleaseAction(tableId, 0);
    });
}

function batchGrayRelease(tableId) {
    $("#gray_release_btn").click(function () {
        batchReleaseAction(tableId, 1);
    });
}

function batchReleaseAction(tableId, versionId) {
    var statusArray = getCheckBoxStatusArray(tableId);
    for (var i in statusArray) {
        if (statusArray[i] == "已发布") {
            $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.SELECTED_NOT_CONTAIN_RELEASED);
            return false;
        }
    }
    var appNumArray = getCheckBoxAppNumArray(tableId);
    for (var i in appNumArray) {
        if (appNumArray[i] < 1) {
            $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.SELECTED_CONFIG_CONTAINS_NO_APP);
            return false;
        }
    }
    var itemIds = getCheckBoxArray(tableId);
    if (itemIds.length < 1) {
        $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.NOT_SELECTED);
        return false;
    }
    $.MsgBox.Release(MESSAGES.NEWS, MESSAGES.IS_BATCH_RELEASE, function () {
        var releaseAjax = $.ajax({
            url: "/public_config/release?versionId=" + versionId,
            type: "POST",
            timeout: 30000,
            async: false,
            data: {ids: itemIds},
            cache: false,
            traditional: true,
            success: function (response) {
                if (typeof(response) == "string" && response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                    $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                        location.reload();
                    });
                } else {
                    $.MsgBox.Alert(MESSAGES.NEWS, response.message, function () {
                        var table = $('#' + tableId).DataTable();
                        var selectId;
                        if (versionId == 0) {
                            selectId = "properties_env_select";
                        } else {
                            selectId = "gray_properties_env_select";
                        }
                        table.ajax.url('/public_config/query_by_name?versionId=' + versionId + "&envId=" + getSelectVal(selectId)).load();
                        $('#list_select_all').prop({checked: false});
                        if (versionId == 0) {
                            $('#main_list_select_all').prop({checked: false});
                        } else {
                            $('#gray_list_select_all').prop({checked: false});
                        }
                    });
                }
            },
            complete: function (XMLHttpRequest, status) {
                if (status == 'timeout') {
                    releaseAjax.abort();
                    window.location.href = "/timeout";
                }
            },
            error: function (xhr, textStatus, thrownError) {
                ajaxError(xhr, textStatus, thrownError);
            }
        });
    });
}

function singleRelease(tableId) {
    $("span#properties_release").click(function () {
        propertiesReleaseAction(tableId, 0, this);
    });
}

function singleGrayRelease(tableId) {
    $("span#gray_properties_release").click(function () {
        propertiesReleaseAction(tableId, 1, this);
    });
}

function propertiesReleaseAction(tableId, versionId, trThis) {
    var data = $('#' + tableId).DataTable().row($(trThis).parents('tr')).data();
    var itemId = data["id"];
    setItem("id", itemId);
    var isRelease = data["release_status"];
    if (isRelease == "已发布") {
        $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.RELEASED);
        return;
    }
    var appNum = data['app_num'].split(',');
    if (appNum < 1) {
        $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.CONFIG_APP);
        return;
    }
    var itemIds = new Array();
    itemIds.push(itemId);
    $.MsgBox.Release(MESSAGES.NEWS, MESSAGES.IS_RELEASE, function () {
        var releaseAjax = $.ajax({
            url: "/public_config/release?versionId=" + versionId,
            type: "POST",
            timeout: 30000,
            async: false,
            data: {ids: itemIds},
            cache: false,
            traditional: true,
            success: function (response) {
                if (typeof(response) == "string" && response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                    $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                        location.reload();
                    });
                } else {
                    $.MsgBox.Alert(MESSAGES.NEWS, response.message, function () {
                        var table = $('#' + tableId).DataTable();
                        var selectId;
                        if (versionId == 0) {
                            selectId = "properties_env_select";
                        } else {
                            selectId = "gray_properties_env_select";
                        }
                        table.ajax.url('/public_config/query_by_name?versionId=' + versionId + "&envId=" + getSelectVal(selectId)).load();
                    })
                }
            },
            complete: function (XMLHttpRequest, status) {
                if (status == 'timeout') {
                    releaseAjax.abort();
                    window.location.href = "/timeout";
                }
            },
            error: function (xhr, textStatus, thrownError) {
                ajaxError(xhr, textStatus, thrownError);
            }
        });
    })
}

function offlineProperties(tableId) {
    $("span#properties_offline").click(function () {
        offlinePropertiesAction(tableId, 0, this)
    });
}

function offlineGrayProperties(tableId) {
    $("span#gray_properties_offline").click(function () {
        offlinePropertiesAction(tableId, 1, this)
    });
}

function offlinePropertiesAction(tableId, versionId, trThis) {
    var data = $('#' + tableId).DataTable().row($(trThis).parents('tr')).data();
    var itemId = data["id"]
    setItem("id", itemId);
    var isRelease = data["release_status"];
    if (isRelease == "未发布") {
        $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.OFFLINE);
        return;
    }
    var itemIds = new Array();
    itemIds.push(itemId);
    $.MsgBox.Offline(null, MESSAGES.IS_OFFLINE, function () {
        var releaseAjax = $.ajax({
            url: "/public_config/offline?versionId=" + versionId,
            type: "POST",
            timeout: 30000,
            async: false,
            data: {ids: itemIds},
            cache: false,
            traditional: true,
            success: function (response) {
                if (typeof(response) == "string" && response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                    $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                        location.reload();
                    });
                } else {
                    $.MsgBox.Alert(MESSAGES.NEWS, response.message, function () {
                        var table = $('#' + tableId).DataTable();
                        var selectId;
                        if (versionId == 0) {
                            selectId = "properties_env_select";
                        } else {
                            selectId = "gray_properties_env_select";
                        }
                        table.ajax.url('/public_config/query_by_name?versionId=' + versionId + "&envId=" + getSelectVal(selectId)).load();
                    });
                }
            },
            complete: function (XMLHttpRequest, status) {
                if (status == 'timeout') {
                    releaseAjax.abort();
                    window.location.href = "/timeout";
                }
            },
            error: function (xhr, textStatus, thrownError) {
                ajaxError(xhr, textStatus, thrownError);
            }
        });
    })
}

function grayAddPage(tableId, html) {
    $("span#gray_properties_add").click(function () {
        var data = $('#' + tableId).DataTable().row($(this).parents('tr')).data();
        setItem("id", data["id"]);
        setItem("key", data["key"]);
        setItem("main_value", data["value"]);
        if (html == "/public_config_gray_add.html") {
            setItem("gray_properties_envId", getSelectVal("gray_properties_env_select"));
        }
        $("#main").load(html, function (response, status, xhr) {
            if (typeof(response) == "string" && response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                $("#main").empty();
                $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                    location.reload();
                });
            } else {
                if (xhr.status != 200) {
                    ajaxError(xhr, status, response);
                }
            }
        });
    });
}

function initShowVersionTab() {
    var ajax = $.ajax({
        url: "/role_auth_resource/get_main_version_auth",
        type: "POST",
        timeout: 30000,
        async: false,
        data: {
            "isPublic": true
        },
        success: function (response) {
            if (typeof(response) == "string" && response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                    location.reload();
                });
            } else {
                if (response == 0) {
                    if (getItem("versionId") == "1") {
                        loadGrayPropertiesEnvSelect(function () {
                            loadGrayPropertiesTable("gray_properties_table");
                            loadGrayData();
                        });
                    } else {
                        loadMainPropertiesEnvSelect(function () {
                            loadMainPropertiesTable("main_properties_table");
                            loadMainData();
                        });
                    }
                } else {
                    loadGrayPropertiesEnvSelect(function () {
                        loadGrayPropertiesTable("gray_properties_table");
                        loadGrayData();
                    });
                }
            }
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                ajax.abort();
                window.location.href = "/timeout";
            }
        },
        error: function (xhr, textStatus, thrownError) {
            ajaxError(xhr, textStatus, thrownError);
        }
    });
}

function getExcludePropertiesInfo(versionId, instanceTypeId, envId) {
    var excludePropertiesInfoAjax = $.ajax({
        url: "/public_config/query_exclude_properties",
        type: "POST",
        timeout: 30000,
        async: false,
        data: {
            "instanceTypeId": instanceTypeId,
            "versionId": versionId,
            "envId": envId
        },
        success: function (responses) {
            if (typeof(responses) == "string" && responses.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                    location.reload();
                });
            } else {
                var versionName = "";
                if (versionId == 1) {
                    versionName = "gray_";
                }
                var instanceTypeName = "";
                if (instanceTypeId == 1) {
                    instanceTypeName = "json";
                } else if (instanceTypeId == 2) {
                    instanceTypeName = "xml";
                } else if (instanceTypeId == 3) {
                    instanceTypeName = "yml";
                }
                var viName = versionName + instanceTypeName;
                var appInputName = viName.replace("_", "-") + "-app-input";
                var applySelectName = viName.replace("_", "-") + "-select";
                if (responses.length > 0) {
                    var response = responses[0];
                    var value = response.value;
                    setItem(viName + "_value", value);
                    var comment = response.comment;
                    var itemId = response.id;
                    var versionNum = response.versionNum;
                    var isRelease = response.releaseStatus == 0 ? "未发布" : "已发布";
                    if (isRelease == "已发布") {
                        $(`#${viName}_release`).addClass("sys-hidden");
                        $(`#${viName}_offline`).removeClass("sys-hidden");
                    } else {
                        $(`#${viName}_offline`).addClass("sys-hidden");
                        $(`#${viName}_release`).removeClass("sys-hidden");
                    }
                    $(`#${viName}_save`).addClass("sys-hidden");
                    $(`#${viName}_edit`).removeClass("sys-hidden");
                    $(`#${viName}_del`).removeClass("sys-hidden");
                    $("#" + viName + "_value").val(value);
                    $("#" + viName + "_comment").val(comment);
                    $("#" + viName + "_release_status").text(isRelease);
                    $("#" + viName + "_item_id").val(itemId);
                    $("#" + viName + "_version_num").val(versionNum);
                    loadApp(itemId, viName + "_app", envId, appInputName);
                    loadApplyDataTable(itemId, viName + "_product_module_table", versionId, envId, applySelectName);
                } else {
                    $("#" + viName + "_value").val("");
                    $("#" + viName + "_comment").val("");
                    $("#" + viName + "_release_status").text("未发布");
                    $("#" + viName + "_item_id").val("");
                    $("#" + viName + "_version_num").val("");
                    $(`#${viName}_release`).addClass("sys-hidden");
                    $(`#${viName}_offline`).addClass("sys-hidden");
                    $(`#${viName}_edit`).addClass("sys-hidden");
                    $(`#${viName}_del`).addClass("sys-hidden");
                    $(`#${viName}_save`).removeClass("sys-hidden");
                    loadApp(itemId, viName + "_app", envId, appInputName);
                    loadApplyDataTable(-1, viName + "_product_module_table", versionId, envId, applySelectName);
                }
            }
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                excludePropertiesInfoAjax.abort();
                window.location.href = "/timeout";
            }
        },
        error: function (xhr, textStatus, thrownError) {
            ajaxError(xhr, textStatus, thrownError);
        }
    });
}

function singlePropertiesDelete(url, tableId) {
    var ids = new Array();
    $('span#properties_del').click(function () {
        var data = $('#' + tableId).DataTable().row($(this).parents('tr')).data();
        ids[0] = data.id;
        $.MsgBox.Delete(MESSAGES.CONFIRM, MESSAGES.IS_DELETE, function () {
            delPost(url, ids, tableId);
        });
    });
}

function singleGrayPropertiesDelete(url, tableId) {
    var ids = new Array();
    $('span#gray_properties_del').click(function () {
        var data = $('#' + tableId).DataTable().row($(this).parents('tr')).data();
        ids[0] = data.id;
        $.MsgBox.Delete(MESSAGES.CONFIRM, MESSAGES.IS_DELETE, function () {
            delPost(url, ids, tableId);
        });
    });
}

function updatePropertiesPage(tableId, html) {
    $('span#properties_edit').click(function () {
        var data = $('#' + tableId).DataTable().row($(this).parents('tr')).data();
        for (var name in data) {
            setItem(name, data[name]);
        }
        removeItem("productModuleEnvId");
        if (html == "/public_config_update.html") {
            setItem("properties_envId", getSelectVal("properties_env_select"));
        }
        $("#main").load(html, function (response, status, xhr) {
            if (typeof(response) == "string" && response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                $("#main").empty();
                $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                    location.reload();
                });
            } else {
                if (xhr.status != 200) {
                    ajaxError(xhr, status, response);
                }
            }
        });
    });
}

function updateGrayPropertiesPage(tableId, html) {
    $('span#gray_properties_edit').click(function () {
        var data = $('#' + tableId).DataTable().row($(this).parents('tr')).data();
        for (var name in data) {
            setItem(name, data[name]);
        }
        removeItem("productModuleEnvId");
        if (html == "/public_config_gray_update.html") {
            setItem("gray_properties_envId", getSelectVal("gray_properties_env_select"));
        }
        $("#main").load(html, function (response, status, xhr) {
            if (typeof(response) == "string" && response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                $("#main").empty();
                $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                    location.reload();
                });
            } else {
                if (xhr.status != 200) {
                    ajaxError(xhr, status, response);
                }
            }
        });
    });
}

function loadMainPropertiesEnvSelect(callback) {
    var url = "/env/env_data";
    loadEnvSelect("properties_env_select", url, callback);
}

function loadMainJsonEnvSelect(callback) {
    var url = "/env/env_data";
    loadEnvSelect("json_env_select", url, callback);
}

function loadMainXmlEnvSelect(callback) {
    var url = "/env/env_data";
    loadEnvSelect("xml_env_select", url, callback);
}

function loadMainYmlEnvSelect(callback) {
    var url = "/env/env_data";
    loadEnvSelect("yml_env_select", url, callback);
}

function loadGrayPropertiesEnvSelect(callback) {
    var url = "/env/env_data";
    loadEnvSelect("gray_properties_env_select", url, callback);
}

function loadGrayJsonEnvSelect(callback) {
    var url = "/env/env_data";
    loadEnvSelect("gray_json_env_select", url, callback);
}

function loadGrayXmlEnvSelect(callback) {
    var url = "/env/env_data";
    loadEnvSelect("gray_xml_env_select", url, callback);
}

function loadGrayYmlEnvSelect(callback) {
    var url = "/env/env_data";
    loadEnvSelect("gray_yml_env_select", url, callback);
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

function showEnvSelect(envDivId) {
    $("#properties_env_div").addClass("sys-hidden");
    $("#json_env_div").addClass("sys-hidden");
    $("#xml_env_div").addClass("sys-hidden");
    $("#yml_env_div").addClass("sys-hidden");
    $("#gray_properties_env_div").addClass("sys-hidden");
    $("#gray_json_env_div").addClass("sys-hidden");
    $("#gray_xml_env_div").addClass("sys-hidden");
    $("#gray_yml_env_div").addClass("sys-hidden");
    $("#" + envDivId).removeClass("sys-hidden");
}

function envAllSelect() {
    var url = "/public_config";
    envSelect("properties_env_select", function () {
        $("#main_properties_table_filter > label > input").val("");
        var table = $('#main_properties_table').DataTable();
        var envId = getSelectVal("properties_env_select");
        table.ajax.url(url + '/query_by_name?versionId=0&envId=' + envId).load();
    });
    envSelect("json_env_select", function () {
        var envId = getSelectVal("json_env_select");
        getExcludePropertiesInfo(0, 1, envId);
    });
    envSelect("xml_env_select", function () {
        var envId = getSelectVal("xml_env_select");
        getExcludePropertiesInfo(0, 2, envId);
    });
    envSelect("yml_env_select", function () {
        var envId = getSelectVal("yml_env_select");
        getExcludePropertiesInfo(0, 3, envId);
    });
    envSelect("gray_properties_env_select", function () {
        $("#gray_properties_table_filter > label > input").val("");
        var table = $('#gray_properties_table').DataTable();
        var envId = getSelectVal("gray_properties_env_select");
        table.ajax.url(url + '/query_by_name?versionId=1&envId=' + envId).load();
    });
    envSelect("gray_json_env_select", function () {
        var envId = getSelectVal("gray_json_env_select");
        getExcludePropertiesInfo(1, 1, envId);
    });
    envSelect("gray_xml_env_select", function () {
        var envId = getSelectVal("gray_xml_env_select");
        getExcludePropertiesInfo(1, 2, envId);
    });
    envSelect("gray_yml_env_select", function () {
        var envId = getSelectVal("gray_yml_env_select");
        getExcludePropertiesInfo(1, 3, envId);
    });
}

function envSelect(id, callback) {
    $("#" + id).change(function () {
        var value = $(this).val();
        if (id == "properties_env_select") {
            setItem("properties_envId", value);
        } else if (id == "gray_properties_env_select") {
            setItem("gray_properties_envId", value);
        }
        callback();
    });
}

function initPublicQuery(table, url, filterId, versionId, settings, viewAllId, copyConfigId) {
    var select = ".dataTables_filter";
    if (filterId) {
        select = "#" + filterId + select;
    }
    var selectId;
    if (versionId == 0) {
        selectId = "properties_env_select";
    } else {
        selectId = "gray_properties_env_select";
    }
    if ($(select + " > #clear_query").length == 0) {
        var filter = $(select);
        filter.prepend('<div class="query-close" id="clear_query"><img src="/img/icon_guanbi.png"/></div>');
        if (viewAllId) {
            filter.prepend('<div class="view-all-div" id="' + viewAllId + '"><span>查看全部</span></div>');
        }
        if (copyConfigId) {
            filter.prepend('<div class="copy-config-div auth-hidden" id="' + copyConfigId + '"><span>复制配置</span></div>');
        }
        filter.append('<div class="query-search"><img src="/img/icon_sousuo.png"/></div>');
        if (viewAllId) {
            if (settings._iRecordsTotal < 1) {
                viewAllNotAllowed(filterId);
            } else {
                viewAllAllowed(filterId);
                $(select + " > #" + viewAllId).click(function () {
                    var envId = getSelectVal(selectId);
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
                                $.MsgBox.ViewAll(MESSAGES.NEWS, "所有配置", data, function () {
                                    var url = "/public_config/download?isAllDownload=true&instanceTypeId=0&versionId=" + versionId + "&envId=" + envId;
                                    window.location.href = url;
                                    $("#mb_box,#mb_con").remove();
                                });
                            }
                        }
                    });
                });
            }
        }
        if (copyConfigId) {
            var html;
            var copyEnvSessionName;
            if (copyConfigId.startsWith("gray")) {
                html = HTML.PUBLIC_CONFIG_GRAY_COPY_CONFIG;
                copyEnvSessionName = "grayCopyEnvId";
            } else {
                html = HTML.PUBLIC_CONFIG_COPY_CONFIG;
                copyEnvSessionName = "copyEnvId";
            }
            $(select + " > #" + copyConfigId).click(function () {
                setItem(copyEnvSessionName, getSelectVal(selectId));
                $("#main").load(html, function (response, status, xhr) {
                    if (xhr.status != 200) {
                        ajaxError(xhr, status, response);
                    }
                });
            });
        }
        $(select + " > .query-search").click(function () {
            var name = $(select + " > label > input").val();
            table.ajax.url(url + '/query_by_name?envId=' + getSelectVal(selectId) + '&versionId=' + versionId + '&name=' + name).load();
            //重新加载有清除的数据，重新设置刚才查询数据，避免问题
            $(select + " > label > input").val(name);
        });
        $(select + " > label > input").on('keypress', function (event) {
            if (event.keyCode == 13) {
                var name = $(this).val();
                table.ajax.url(url + '/query_by_name?envId=' + getSelectVal(selectId) + '&versionId=' + versionId + '&name=' + name).load();
                //重新加载有清除的数据，重新设置刚才查询数据，避免问题
                $(select + " > label > input").val(name);
            }
        });
        $(select + "> #clear_query").click(function () {
            var loadUrl = url + '/query_by_name?envId=' + getSelectVal(selectId) + '&versionId=' + versionId;
            table.ajax.url(loadUrl).load();
            $(select + " > label > input").val("");
        })
    }
}

function appSelectAll() {
    selectAll("json_app_select_all", "json-app-input");
    selectAll("xml_app_select_all", "xml-app-input");
    selectAll("yml_app_select_all", "yml-app-input");
    selectAll("gray_json_app_select_all", "gray-json-app-input");
    selectAll("gray_xml_app_select_all", "gray-xml-app-input");
    selectAll("gray_yml_app_select_all", "gray-yml-app-input");
}

function applySelectAll() {
    selectAll("json_select_all", "json-select");
    selectAll("xml_select_all", "xml-select");
    selectAll("yml_select_all", "yml-select");
    selectAll("gray_json_select_all", "gray-json-select");
    selectAll("gray_xml_select_all", "gray-xml-select");
    selectAll("gray_yml_select_all", "gray-yml-select");
}

function setApplyTableThWidth(id) {
    var width = $('#' + id).parent("div").parent("div").children(".dataTables_scrollHead").children(".dataTables_scrollHeadInner").children("table").css("width");
    if (width == "100px") {
        var offsetWidth = $('#' + id).parent('.dataTables_scrollBody')[0].offsetWidth;
        var scrollWidth = $('#' + id).parent('.dataTables_scrollBody')[0].scrollWidth;
        var scrollbarWidth = offsetWidth - scrollWidth;
        var dataTables_scrollHeadInner = $('#' + id).parent('.dataTables_scrollBody').parent('.dataTables_scroll').children('.dataTables_scrollHead').children('.dataTables_scrollHeadInner');
        dataTables_scrollHeadInner.css({"width": scrollWidth, "padding-right": scrollbarWidth});
        dataTables_scrollHeadInner.children("table").css("width", scrollWidth);
    }
}

function loadSelectEnv(idPre) {
    var envId = getItem(idPre + "properties_envId");
    if (envId && envId != null) {
        selectedByVal(idPre + "properties_env_select", envId);
    }
}