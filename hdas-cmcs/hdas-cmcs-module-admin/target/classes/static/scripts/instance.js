/**
 * Created by maodi on 2018/6/5.
 */
function loadInstanceData(productModuleEnvId) {
    initShowVersionTab(productModuleEnvId);
}

function loadMainData(productModuleEnvId) {
    getExcludePropertiesInfo(productModuleEnvId, 0, 1);
    getExcludePropertiesInfo(productModuleEnvId, 0, 2);
    getExcludePropertiesInfo(productModuleEnvId, 0, 3);
}

function loadGrayData(productModuleEnvId) {
    getExcludePropertiesInfo(productModuleEnvId, 1, 1);
    getExcludePropertiesInfo(productModuleEnvId, 1, 2);
    getExcludePropertiesInfo(productModuleEnvId, 1, 3);
}

function clickVersionBtn(productModuleEnvId) {
    $("#main_version_btn").click(function () {
        if ($("#main_properties_table").children("tbody").length < 1) {
            loadMainPropertiesTable("main_properties_table", productModuleEnvId);
            loadMainData(productModuleEnvId);
            loadQuery("请输入键查询");
        } else {
            if ($("#properties_tab").hasClass("tab-li-active")) {
                $('#main_properties_table').DataTable().ajax.reload();
            }
            loadMainData(productModuleEnvId);
        }
        mainVersionActive();
    });
    $("#gray_version_btn").click(function () {
        if ($("#gray_properties_table").children("tbody").length < 1) {
            loadGrayPropertiesTable("gray_properties_table", productModuleEnvId);
            loadGrayData(productModuleEnvId);
            loadQuery("请输入键查询");
        } else {
            if ($("#gray_properties_tab").hasClass("tab-li-active")) {
                $('#gray_properties_table').DataTable().ajax.reload();
            }
            loadGrayData(productModuleEnvId);
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
        tabTopBtn("properties_tab");
        $("#import_btn").removeClass("public-hidden");
    });
    $("#json_tab").click(function () {
        addMainInstanceClass("json_div", "json_tab");
        tabTopBtn("json_tab");
        setPublicImportBtn("json_property");
    });
    $("#xml_tab").click(function () {
        addMainInstanceClass("xml_div", "xml_tab");
        ;
        tabTopBtn("xml_tab")
        setPublicImportBtn("xml_property");
    });
    $("#yml_tab").click(function () {
        addMainInstanceClass("yml_div", "yml_tab");
        tabTopBtn("yml_tab");
        setPublicImportBtn("yml_property");
    });
    //灰度页面操作
    $("#gray_properties_tab").click(function () {
        addGrayInstanceClass("gray_properties_div", "gray_properties_tab");
        tabTopGrayBtn("gray_properties_tab");
        $("#import_btn").removeClass("public-hidden");
    });
    $("#gray_json_tab").click(function () {
        addGrayInstanceClass("gray_json_div", "gray_json_tab");
        tabTopGrayBtn("gray_json_tab");
        setPublicImportBtn("gray_json_property");
    });
    $("#gray_xml_tab").click(function () {
        addGrayInstanceClass("gray_xml_div", "gray_xml_tab");
        tabTopGrayBtn("gray_xml_tab");
        setPublicImportBtn("gray_xml_property");
    });
    $("#gray_yml_tab").click(function () {
        addGrayInstanceClass("gray_yml_div", "gray_yml_tab");
        tabTopGrayBtn("gray_yml_tab");
        setPublicImportBtn("gray_yml_property");
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

function loadMainPropertiesTable(tableId, productModuleEnvId) {
    setItem("versionId", 0);
    var mainTable = $('#' + tableId).DataTable({
        ajax: {
            url: '/instance/query_properties',
            dataSrc: 'data',
            data: {
                'productModuleEnvId': productModuleEnvId,
                'versionId': 0
            }
        },
        columns: [
            {data: ''},
            {data: 'num', visible: false},
            {data: 'property'},
            {data: 'version_num'},
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
            defaultContent: '<input type="checkbox" name="main-select"/>',
            targets: 0
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
                $(nTd).children("div").text(`${sData.substring(0, 60)}`);
            }
        }, {
            targets: 5,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 60)}`);
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
                $(nTd).children("div").text(`${sData.substring(0, 24)}`);
            }
        }, {
            targets: 8,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 15)}`);
            }
        }, {
            targets: 9,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 15)}`);
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
                    if (oData.property == "公有") {
                        $(nTd).children("#properties_edit").addClass("sys-hidden");
                        $(nTd).children("#properties_del").addClass("sys-hidden");
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
            batchDelete("/instance/delete_by_ids?productModuleEnvId=" + productModuleEnvId + "&versionId=0", tableId);
            batchRelease(tableId, productModuleEnvId);
        },
        drawCallback: function (settings) {
            initQuery(mainTable, "/instance", "main_properties_table_filter", productModuleEnvId, 0, settings, "view_all", "copy_config");
            initToPage(settings, mainTable, "main_properties_table_wrapper");
            clickTableTr("main-select");
            updatePropertiesPage(tableId, HTML.INSTANCE_UPDATE + "?productModuleEnvId=" + productModuleEnvId, productModuleEnvId);
            grayAddPage(tableId, HTML.INSTANCE_GRAY_ADD + "?productModuleEnvId=" + productModuleEnvId, productModuleEnvId);
            singlePropertiesDelete("/instance/delete_by_ids?productModuleEnvId=" + productModuleEnvId + "&versionId=0", tableId);
            singleRelease(tableId, productModuleEnvId);
            offlineProperties(tableId, productModuleEnvId);
            getUserAuthResourcePage(tableId, HTML.INSTANCE + "?productModuleEnvId=" + productModuleEnvId, 0, productModuleEnvId);
            $("#main_version_tab").removeClass("sys-hidden");
            $("#main_properties_div").removeClass("sys-hidden");
            $("#" + tableId).css("width", "100%");
            if (settings._iRecordsTotal < 1) {
                $(".dataTables_empty").css("width", "100%");
            }
        }
    });
}

function loadGrayPropertiesTable(tableId, productModuleEnvId) {
    setItem("versionId", 1);
    var grayTable = $('#' + tableId).DataTable({
        ajax: {
            url: '/instance/query_properties',
            dataSrc: 'data',
            data: {
                'productModuleEnvId': productModuleEnvId,
                'versionId': 1
            }
        },
        columns: [
            {data: ''},
            {data: 'num', visible: false},
            {data: 'property'},
            {data: 'version_num'},
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
            defaultContent: '<input type="checkbox" name="gray-select"/>',
            targets: 0
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
                $(nTd).children("div").text(`${sData.substring(0, 45)}`);
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
                    if (oData.property == "公有") {
                        $(nTd).children("#properties_edit").addClass("sys-hidden");
                        $(nTd).children("#properties_del").addClass("sys-hidden");
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
            batchGrayDelete("/instance/delete_by_ids?productModuleEnvId=" + productModuleEnvId + "&versionId=1", tableId);
            batchGrayRelease(tableId, productModuleEnvId);
        },
        drawCallback: function (settings) {
            initQuery(grayTable, "/instance", "gray_properties_table_filter", productModuleEnvId, 1, settings, "gray_view_all", "gray_copy_config");
            initToPage(settings, grayTable, "gray_properties_table_wrapper");
            clickTableTr("gray-select");
            updateGrayPropertiesPage(tableId, HTML.INSTANCE_GRAY_UPDATE + "?productModuleEnvId=" + productModuleEnvId, productModuleEnvId);
            singleGrayPropertiesDelete("/instance/delete_by_ids?productModuleEnvId=" + productModuleEnvId + "&versionId=1", tableId);
            singleGrayRelease(tableId, productModuleEnvId);
            offlineGrayProperties(tableId, productModuleEnvId);
            getUserAuthResourcePage(tableId, HTML.INSTANCE + "?productModuleEnvId=" + productModuleEnvId, 1, productModuleEnvId);
            $("#gray_version_tab").removeClass("sys-hidden");
            $("#gray_properties_div").removeClass("sys-hidden");
            $("#" + tableId).css("width", "100%");
            if (settings._iRecordsTotal < 1) {
                $(".dataTables_empty").css("width", "100%");
            }
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
    if (instanceTypeId == 0) {
        msgBody = ".properties";
    } else if (instanceTypeId == 1) {
        msgBody = ".json";
    } else if (instanceTypeId == 2) {
        msgBody = ".xml";
    } else if (instanceTypeId == 3) {
        msgBody = ".yml";
    }
    var tableId;
    if (versionId == 0) {
        tableId = "main_properties_table";
    } else {
        tableId = "gray_properties_table";
    }
    var productModuleEnvId = getItem("productModuleEnvId");
    $.MsgBox.Upload("上传文件", msgHead + msgBody + msgFoot, function () {
        upload(versionId, instanceTypeId, tableId);
    }, function () {
        if (instanceTypeId == 0) {
            var table = $('#' + tableId).DataTable();
            table.ajax.url('/instance/query_properties?productModuleEnvId=' + productModuleEnvId + '&versionId=' + versionId).load(function () {
                setViewStatus(tableId);
            });
        } else {
            getExcludePropertiesInfo(productModuleEnvId, versionId, instanceTypeId);
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
    if (instanceTypeId == 0) {
        msgBody = ".properties";
    } else if (instanceTypeId == 1) {
        msgBody = ".json";
        idPre += "json"
    } else if (instanceTypeId == 2) {
        msgBody = ".xml";
        idPre += "xml"
    } else if (instanceTypeId == 3) {
        msgBody = ".yml";
        idPre += "yml"
    }
    if (!file.name.endWith(msgBody)) {
        alert("请选择" + msgBody + "文件");
        return false;
    }
    var productModuleEnvId = getItem("productModuleEnvId");
    formData.append("productModuleEnvId", productModuleEnvId);
    formData.append("instanceTypeId", parseInt(instanceTypeId));
    formData.append("versionId", parseInt(versionId));
    var itemId = $("#" + idPre + "_item_id").val();
    if (itemId) {
        formData.append("itemId", parseInt(itemId));
    }
    var importDataAjax = $.ajax({
        url: "/instance/upload",
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
                        table.ajax.url('/instance/query_properties?productModuleEnvId=' + productModuleEnvId + '&versionId=' + versionId).load(function () {
                            setViewStatus(tableId);
                        });
                    } else {
                        getExcludePropertiesInfo(productModuleEnvId, versionId, instanceTypeId);
                    }
                });
            }
        },
        complete: function (XMLHttpRequest, status) {
            ajaxTimeout(status, importDataAjax);
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
    var productModuleEnvId = getItem("productModuleEnvId");
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
        var url = "/instance/download?instanceTypeId=" + instanceTypeId + "&versionId=" + versionId + "&productModuleEnvId=" + productModuleEnvId;
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
            var url = "/instance/download?instanceTypeId=" + instanceTypeId + "&versionId=" + versionId + "&productModuleEnvId=" + productModuleEnvId + "&itemIds[]=" + itemId;
            window.location.href = url;
        } else {
            $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.NOT_SAVE_VALUE);
        }
    }
}

function batchRelease(tableId, productModuleEnvId) {
    $("#release_btn").click(function () {
        batchReleaseAction(tableId, productModuleEnvId, 0);
    });
}

function batchGrayRelease(tableId, productModuleEnvId) {
    $("#gray_release_btn").click(function () {
        batchReleaseAction(tableId, productModuleEnvId, 1);
    });
}

function batchReleaseAction(tableId, productModuleEnvId, versionId) {
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
            url: "/instance/release?productModuleEnvId=" + productModuleEnvId + "&versionId=" + versionId,
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
                        table.ajax.url('/instance/query_properties?productModuleEnvId=' + productModuleEnvId + '&versionId=' + versionId).load();
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

function singleRelease(tableId, productModuleEnvId) {
    $("span#properties_release").click(function () {
        propertiesReleaseAction(tableId, productModuleEnvId, 0, this);
    });
}

function singleGrayRelease(tableId, productModuleEnvId) {
    $("span#gray_properties_release").click(function () {
        propertiesReleaseAction(tableId, productModuleEnvId, 1, this);
    });
}

function propertiesReleaseAction(tableId, productModuleEnvId, versionId, trThis) {
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
            url: "/instance/release?productModuleEnvId=" + productModuleEnvId + "&versionId=" + versionId,
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
                        table.ajax.url('/instance/query_properties?productModuleEnvId=' + productModuleEnvId + '&versionId=' + versionId).load();
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

function offlineProperties(tableId, productModuleEnvId) {
    $("span#properties_offline").click(function () {
        offlinePropertiesAction(tableId, productModuleEnvId, 0, this);
    });
}

function offlineGrayProperties(tableId, productModuleEnvId) {
    $("span#gray_properties_offline").click(function () {
        offlinePropertiesAction(tableId, productModuleEnvId, 1, this);
    });
}

function offlinePropertiesAction(tableId, productModuleEnvId, versionId, trThis) {
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
            url: "/instance/offline?productModuleEnvId=" + productModuleEnvId + "&versionId=" + versionId,
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
                        table.ajax.url('/instance/query_properties?productModuleEnvId=' + productModuleEnvId + '&versionId=' + versionId).load();
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

function grayAddPage(tableId, html, productModuleEnvId) {
    $("span#gray_properties_add").click(function () {
        var data = $('#' + tableId).DataTable().row($(this).parents('tr')).data();
        setItem("id", data["id"]);
        setItem("key", data["key"]);
        setItem("productModuleEnvId", productModuleEnvId);
        setItem("main_value", data["value"]);
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

function initShowVersionTab(productModuleEnvId) {
    var ids = productModuleEnvId.split("_");
    var productId = ids[0];
    var moduleId = ids[1];
    var envId = ids[2];
    var mainVersionAuthAjax = $.ajax({
        url: "/role_auth_resource/get_main_version_auth",
        type: "POST",
        timeout: 30000,
        async: false,
        data: {
            "productId": productId,
            "moduleId": moduleId,
            "envId": envId
        },
        success: function (response) {
            if (typeof(response) == "string" && response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                    location.reload();
                });
            } else {
                if (response == 0) {
                    if (getItem("versionId") == "1") {
                        loadGrayPropertiesTable("gray_properties_table", productModuleEnvId);
                        loadGrayData(productModuleEnvId);
                    } else {
                        loadMainPropertiesTable("main_properties_table", productModuleEnvId);
                        loadMainData(productModuleEnvId);
                    }
                } else {
                    loadGrayPropertiesTable("gray_properties_table", productModuleEnvId);
                    loadGrayData(productModuleEnvId);
                }
            }
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                mainVersionAuthAjax.abort();
                window.location.href = "/timeout";
            }
        },
        error: function (xhr, textStatus, thrownError) {
            ajaxError(xhr, textStatus, thrownError);
        }
    });
}

function getExcludePropertiesInfo(productModuleEnvId, versionId, instanceTypeId) {
    var excludePropertiesInfoAjax = $.ajax({
        url: "/instance/query_exclude_properties?productModuleEnvId=" + productModuleEnvId + "&versionId=" + versionId,
        type: "POST",
        timeout: 30000,
        async: false,
        data: {
            "productModuleEnvId": productModuleEnvId,
            "versionId": versionId,
            "instanceTypeId": instanceTypeId
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
                    instanceTypeName = "json"
                } else if (instanceTypeId == 2) {
                    instanceTypeName = "xml";
                } else if (instanceTypeId == 3) {
                    instanceTypeName = "yml";
                }
                var viName = versionName + instanceTypeName;
                var appInputName = viName.replace("_", "-") + "-app-input";
                if (responses.length > 0) {
                    var response = responses[0];
                    var isPublic = response.is_public;
                    $("#" + viName + "_property").empty();
                    if (isPublic == 1) {
                        $("#" + viName + "_property").text("公有");
                        $("#" + viName + "_edit").addClass("public-hidden");
                        $("#" + viName + "_save").addClass("public-hidden");
                        $("#" + viName + "_del").addClass("public-hidden");
                    } else {
                        $("#" + viName + "_property").text("私有");
                        $("#" + viName + "_edit").removeClass("public-hidden");
                        $("#" + viName + "_save").removeClass("public-hidden");
                        $("#" + viName + "_del").removeClass("public-hidden");
                    }
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
                    loadApp(itemId, viName + "_app", appInputName);
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
                    loadApp(itemId, viName + "_app", appInputName);
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

function updatePropertiesPage(tableId, html, productModuleEnvId) {
    $('span#properties_edit').click(function () {
        var data = $('#' + tableId).DataTable().row($(this).parents('tr')).data();
        for (var name in data) {
            setItem(name, data[name]);
        }
        if (productModuleEnvId) {
            setItem("productModuleEnvId", productModuleEnvId);
        } else {
            removeItem("productModuleEnvId");
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

function updateGrayPropertiesPage(tableId, html, productModuleEnvId) {
    $('span#gray_properties_edit').click(function () {
        var data = $('#' + tableId).DataTable().row($(this).parents('tr')).data();
        for (var name in data) {
            setItem(name, data[name]);
        }
        if (productModuleEnvId) {
            setItem("productModuleEnvId", productModuleEnvId);
        } else {
            removeItem("productModuleEnvId");
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

function appSelectAll() {
    selectAll("json_app_select_all", "json-app-input");
    selectAll("xml_app_select_all", "xml-app-input");
    selectAll("yml_app_select_all", "yml-app-input");
    selectAll("gray_json_app_select_all", "gray-json-app-input");
    selectAll("gray_xml_app_select_all", "gray-xml-app-input");
    selectAll("gray_yml_app_select_all", "gray-yml-app-input");
}

function setPublicImportBtn(id) {
    var text = $("#" + id).text();
    if (text == "公有") {
        $("#import_btn").addClass("public-hidden");
    } else {
        $("#import_btn").removeClass("public-hidden");
    }
}