/**
 * Created by maodi on 2018/7/3.
 */
function loadData(versionId) {
    initShowVersionTab(versionId);
}

function clickIsReleasedBtn(versionId) {
    $("#released_btn").click(function () {
        if ($("#released_table").children("tbody").length < 1) {
            loadReleasedHistoryTable("released_table", versionId);
            loadQuery("请输入键查询");
        } else {
            $('#released_table').DataTable().ajax.reload();
        }
        releasedActive();
    });
    $("#unreleased_btn").click(function () {
        if ($("#unreleased_table").children("tbody").length < 1) {
            loadUnreleasedHistoryTable("unreleased_table", versionId);
            loadQuery("请输入键查询");
        } else {
            $('#unreleased_table').DataTable().ajax.reload();
        }
        unreleasedActive();
    });
}

function releasedActive() {
    $("#released_btn").addClass("version-btn-active");
    $("#unreleased_btn").removeClass("version-btn-active");
    $("#unreleased_tab").addClass("sys-hidden");
    $("#released_tab").removeClass("sys-hidden");
    $("#release_btn").removeClass("sys-hidden");
}

function unreleasedActive() {
    $("#unreleased_btn").addClass("version-btn-active");
    $("#released_btn").removeClass("version-btn-active");
    $("#released_tab").addClass("sys-hidden");
    $("#unreleased_tab").removeClass("sys-hidden");
    $("#release_btn").addClass("sys-hidden");
}

function loadReleasedHistoryTable(tableId, versionId) {
    var selectName = "released-select"
    releasedActive();
    var table = $('#' + tableId).DataTable({
        ajax: {
            url: '/history_log_manage/query',
            dataSrc: 'data',
            data: {
                'versionId': versionId,
                'releaseStatus': 1
            }
        },
        columns: [
            {data: '', visible: false},
            {data: 'num', visible: false},
            {data: 'apply'},
            {data: 'version_num'},
            {data: 'key'},
            {data: 'value'},
            {data: 'app_num'},
            {data: 'comment'},
            {data: 'property'},
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
            defaultContent: '<input type="checkbox" name="' + selectName + '"/>',
            targets: 0
        }, {
            targets: 2,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 40)}`);
            }
        }, {
            targets: 3,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 20)}`);
            }
        }, {
            targets: 4,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 20)}`);
            }
        }, {
            targets: 5,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 20)}`);
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
                $(nTd).children("div").text(`${sData.substring(0, 20)}`);
            }
        }, {
            targets: 8
        }, {
            targets: 9,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 20)}`);
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
                    $(nTd).html(CONTENT.RELEASE);
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
            batchAction(tableId, versionId);
            var productSelectId = "released_product";
            var userSelectId = "released_user";
            loadHistoryProductSelectData(productSelectId);
            loadHistoryUserSelectData(userSelectId);
        },
        drawCallback: function (settings) {
            initHistoryQuery(table, "/history_log_manage", tableId + "_filter", versionId, 1);
            initToPage(settings, table, tableId + "_wrapper");
            clickTableTr(selectName);
            singleAction(tableId, versionId);
            if (versionId == 0) {
                getUserAuthResourcePage(tableId, HTML.HISTORY_LOG_MANAGE_MAIN, 0);
            } else {
                getUserAuthResourcePage(tableId, HTML.HISTORY_LOG_MANAGE_GRAY, 1);
            }
            $("#" + tableId).css("width", "100%");
            if (settings._iRecordsTotal < 1) {
                $(".dataTables_empty").css("width", "100%");
            }
        }
    });
}

function loadUnreleasedHistoryTable(tableId, versionId) {
    var selectName = "unreleased-select";
    unreleasedActive();
    var table = $('#' + tableId).DataTable({
        ajax: {
            url: '/history_log_manage/query',
            dataSrc: 'data',
            data: {
                'versionId': versionId,
                'releaseStatus': 0
            }
        },
        columns: [
            {data: '', visible: false},
            {data: 'num', visible: false},
            {data: 'apply'},
            {data: 'version_num'},
            {data: 'key'},
            {data: 'value'},
            {data: 'app_num'},
            {data: 'comment'},
            {data: 'property'},
            {data: 'last_update_by'},
            {data: 'update_time'},
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
            defaultContent: '<input type="checkbox" name="' + selectName + '"/>'
        }, {
            targets: 2,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 40)}`);
            }
        }, {
            targets: 3,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 20)}`);
            }
        }, {
            targets: 4,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 20)}`);
            }
        }, {
            targets: 5,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 20)}`);
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
                $(nTd).children("div").text(`${sData.substring(0, 20)}`);
            }
        }, {
            targets: 8
        }, {
            targets: 9,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 20)}`);
            }
        }, {
            targets: 10
        }],
        info: false,
        lengthChange: false,
        paging: true,
        searching: true,
        processing: true,
        serverSide: true,
        ordering: false,
        initComplete: function (settings, json) {
            var productSelectId = "unreleased_product";
            var userSelectId = "unreleased_user";
            loadHistoryProductSelectData(productSelectId);
            loadHistoryUserSelectData(userSelectId);
        },
        drawCallback: function (settings) {
            initHistoryQuery(table, "/history_log_manage", tableId + "_filter", versionId, 0);
            initToPage(settings, table, tableId + "_wrapper");
            clickTableTr(selectName);
            if (versionId == 0) {
                getUserAuthResourcePage(tableId, HTML.HISTORY_LOG_MANAGE_MAIN, 0);
            } else {
                getUserAuthResourcePage(tableId, HTML.HISTORY_LOG_MANAGE_GRAY, 1);
            }
            $("#" + tableId).css("width", "100%");
            if (settings._iRecordsTotal < 1) {
                $(".dataTables_empty").css("width", "100%");
            }
        }
    });
}

function initHistoryQuery(table, url, filterId, versionId, releaseStatus) {
    var select = ".dataTables_filter";
    if (filterId) {
        select = "#" + filterId + select;
    }
    var productSelectId;
    var propertySelectId;
    var userSelectId;
    var startTimeId;
    var endTimeId;
    var idPre;
    if (releaseStatus == 0) {
        productSelectId = "unreleased_product";
        propertySelectId = "unreleased_property";
        userSelectId = "unreleased_user";
        startTimeId = "unreleased_start_time";
        endTimeId = "unreleased_end_time";
        idPre = "un";
    } else {
        productSelectId = "released_product";
        propertySelectId = "released_property";
        userSelectId = "released_user";
        startTimeId = "released_start_time";
        endTimeId = "released_end_time";
        idPre = "";
    }
    if ($(select + " > #clear_query").length == 0) {
        var filter = $(select);
        filter.prepend('<div class="history-query-div-big" id="update_time_div"><span>修改时间：</span><input ' +
            'id="' + startTimeId + '" type="date" class="history-query-select-time"' +
            ' autocomplete="off"/><span>&nbsp;&nbsp;-</span><input id="' + endTimeId +
            '" type="date" class="history-query-select-time" autocomplete="off"/></div>');
        filter.prepend('<div class="history-query-div" id="update_user_div"><span>修改人：</span><select id="' + userSelectId + '" ' +
            'class="history-query-select"></select></div>');
        filter.prepend('<div class="history-query-div" id="property_div"><span>属性：</span><select id="' + propertySelectId + '"' +
            ' class="history-query-select"><option value="-1">全部</option><option value="0">私有</option><option ' +
            'value="1">公有</option></select></div>');
        filter.prepend('<div class="history-query-div" id="product_div"><span>项目：</span><select id="' + productSelectId + '"' +
            ' class="history-query-select"></div>');
        filter.prepend('<div class="query-close" id="clear_query"><img src="/img/icon_guanbi.png"/></div>');
        filter.append('<div class="query-search"><img src="/img/icon_sousuo.png"/></div>');
        $("#" + startTimeId).keydown(function (e) {
            return false;
        });
        $("#" + endTimeId).keydown(function (e) {
            return false;
        });
        $(select + " > .query-search").click(function () {
            var name = $(select + " > label > input").val();
            var appendUrl = "";
            if (name && name.length > 0) {
                appendUrl += "&name=" + name;
            }
            appendUrl += getQueryCondition(idPre);
            table.ajax.url(url + '/query_by_condition?versionId=' + versionId + '&releaseStatus=' + releaseStatus + appendUrl).load();
            $(select + " > label > input").val(name);
        });
        $(select + " > label > input").on('keypress', function (event) {
            if (event.keyCode == 13) {
                var name = $(this).val();
                var appendUrl = "";
                if (name && name.length > 0) {
                    appendUrl = "&name=" + name;
                }
                appendUrl += getQueryCondition(idPre);
                table.ajax.url(url + '/query_by_condition?versionId=' + versionId + '&releaseStatus=' + releaseStatus + appendUrl).load();
                $(select + " > label > input").val(name);
            }
        });
        $(select + "> #clear_query").click(function () {
            table.ajax.url(url + '/query_by_condition?versionId=' + versionId + '&releaseStatus=' + releaseStatus).load();
            $("#" + startTimeId).val("");
            $("#" + endTimeId).val("");
            $("#" + productSelectId).find('option[value="-1"]').prop({selected: true});
            $("#" + propertySelectId).find('option[value="-1"]').prop({selected: true});
            $("#" + userSelectId).find('option[value="-1"]').prop({selected: true});
            $(select + " > label > input").val("");
        })
    }
}

function getQueryCondition(idPre) {
    var productSelectId = idPre + "released_product";
    var propertySelectId = idPre + "released_property";
    var userSelectId = idPre + "released_user";
    var startTimeId = idPre + "released_start_time";
    var endTimeId = idPre + "released_end_time";
    var productId = getSelectVal(productSelectId);
    var propertyId = getSelectVal(propertySelectId);
    var userId = getSelectVal(userSelectId);
    var startTime = $("#" + startTimeId).val();
    var endTime = $("#" + endTimeId).val();
    var appendUrl = "";
    if (productId != -1) {
        appendUrl += "&productId=" + productId;
    }
    if (propertyId != -1) {
        var isPublic;
        if (propertyId == 0) {
            isPublic = "私有";
        } else {
            isPublic = "公有";
        }
        appendUrl += "&isPublic=" + isPublic;
    }
    if (userId != -1) {
        appendUrl += "&userId=" + userId;
    }
    if (startTime && startTime.length > 0) {
        startTime = startTime + "T00:00";
        appendUrl += "&startTime=" + startTime;
    }
    if (endTime && endTime.length > 0) {
        endTime = endTime + "T24:00";
        appendUrl += "&endTime=" + endTime;
    }
    return appendUrl;
}

function batchAction(tableId, versionId) {
    var btnId = "release_btn";
    $("#" + btnId).click(function () {
        historyAction(tableId, versionId, null);
    });
}

function singleAction(tableId, versionId) {
    var spanActionId = "release";
    $("span#" + spanActionId).click(function () {
        historyNewAction(tableId, versionId, this);
    });
}

function historyNewAction(tableId, versionId, trThis) {
    var itemIds = new Array();
    var isPublic;
    var data;
    var versionNumBack;
    if (trThis != null) {
        data = $('#' + tableId).DataTable().row($(trThis).parents('tr')).data();
        var versionNum = data['version_num'];
        versionNumBack = versionNum.substring(versionNum.length - 16, versionNum.length);
        var itemId = data["id"];
        isPublic = data["property"] == "公有" ? 1 : 0;
        setItem("id", itemId);
        itemIds.push(itemId);
    } else {
        itemIds = getCheckBoxArray(tableId);
    }
    var outAjax = $.ajax({
        url: "/history_log_manage/get_current_use_item?versionNumBack=" + versionNumBack,
        type: "POST",
        timeout: 30000,
        async: false,
        cache: false,
        traditional: true,
        success: function (response) {
            if (typeof(response) == "string" && response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                    location.reload();
                });
            } else {
                $.MsgBox.OverRelease(null, MESSAGES.IS_OVER_RELEASE, response, function () {
                    var ajax = $.ajax({
                        url: "/history_log_manage/release?versionId=" + versionId + "&releaseStatus=1&isPublic=" + isPublic,
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
                                    table.ajax.url('/history_log_manage/query?versionId=' + versionId + "&releaseStatus=1").load();
                                })
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
                })
            }
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                outAjax.abort();
                window.location.href = "/timeout";
            }
        },
        error: function (xhr, textStatus, thrownError) {
            ajaxError(xhr, textStatus, thrownError);
        }
    });
}

function historyAction(tableId, versionId, trThis) {
    var itemIds = new Array();
    var isPublic;
    if (trThis != null) {
        var data = $('#' + tableId).DataTable().row($(trThis).parents('tr')).data();
        var itemId = data["id"];
        isPublic = data["property"] == "公有" ? 1 : 0;
        setItem("id", itemId);
        itemIds.push(itemId);
    } else {
        itemIds = getCheckBoxArray(tableId);
    }
    var msg = MESSAGES.IS_RELEASE;
    $.MsgBox.OverRelease(MESSAGES.NEWS, msg, function () {
        var ajax = $.ajax({
            url: "/history_log_manage/release?versionId=" + versionId + "&releaseStatus=1&isPublic=" + isPublic,
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
                        table.ajax.url('/history_log_manage/query?versionId=' + versionId + "&releaseStatus=1").load();
                    })
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
    })
}

function initShowVersionTab(versionId) {
    var ajax = $.ajax({
        url: "/role_auth_resource/get_release_status",
        type: "POST",
        timeout: 30000,
        async: false,
        data: {"versionId": versionId},
        success: function (response) {
            if (typeof(response) == "string" && response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                    location.reload();
                });
            } else {
                if (response == 1) {
                    loadReleasedHistoryTable("released_table", versionId);
                } else {
                    loadUnreleasedHistoryTable("unreleased_table", versionId);
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

function loadHistoryProductSelectData(id) {
    loadHistorySelect(id, "/product/product_data");
}

function loadHistoryUserSelectData(id) {
    loadHistorySelect(id, "/user/user_data");
}

function loadHistorySelect(id, url) {
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

function historySelectAll(selectAllId) {
    var name = "select";
    if (selectAllId.startWith("released")) {
        name = "released-select";
    } else if (selectAllId.startWith("unreleased")) {
        name = "unreleased-select";
    }
    $("#" + selectAllId).change(function () {
        var checked = $(this).prop('checked');
        if (checked) {
            $('input:checkbox[name=' + name + ']').prop({checked: true});
        } else {
            $('input:checkbox[name=' + name + ']').prop({checked: false});
        }
    });
}