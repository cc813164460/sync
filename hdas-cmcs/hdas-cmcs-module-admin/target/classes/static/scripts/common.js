/**
 * Created by maodi on 2018/5/28.
 */
function loadLeftNav() {
    $("#left_nav").load("/views/common/left_nav.html", function (response, status, xhr) {
        if (typeof(response) == "string" && response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
            $("#left_nav").empty();
            $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                location.reload();
            });
        } else {
            if (xhr.status != 200) {
                ajaxError(xhr, status, response);
            }
        }
    });
}

function loadQuery(text, id) {
    if (id) {
        $("#" + id + " > label> input").attr("placeholder", text);
    } else {
        $(".dataTables_filter > label> input").attr("placeholder", text);
    }
}

function initTableStyle() {
    $(".dataTable").css({"width": "100% !important"});
}

function getCheckBoxArray(id) {
    var name = "select";
    if (id.startWith("main")) {
        name = "main-select";
    } else if (id.startWith("gray")) {
        name = "gray-select";
    } else if (id.startWith("released")) {
        name = "released-select";
    } else if (id.startWith("unreleased")) {
        name = "unreleased-select";
    }
    var checkBoxArray = new Array();
    $('input:checkbox[name=' + name + ']:checked').each(function (i) {
        var data = $('#' + id).DataTable().row($(this).parents('tr')).data();
        checkBoxArray.push(data.id);
    });
    return checkBoxArray;
}

function getCheckBoxStatusArray(id) {
    var name = "select";
    if (id.startWith("main")) {
        name = "main-select";
    } else if (id.startWith("gray")) {
        name = "gray-select";
    } else if (id.startWith("released")) {
        name = "released-select";
    } else if (id.startWith("unreleased")) {
        name = "unreleased-select";
    }
    var checkBoxArray = new Array();
    $('input:checkbox[name=' + name + ']:checked').each(function (i) {
        var data = $('#' + id).DataTable().row($(this).parents('tr')).data();
        checkBoxArray.push(data.release_status);
    });
    return checkBoxArray;
}

function getCheckBoxAppNumArray(id) {
    var name = "select";
    if (id.startWith("main")) {
        name = "main-select";
    } else if (id.startWith("gray")) {
        name = "gray-select";
    } else if (id.startWith("released")) {
        name = "released-select";
    } else if (id.startWith("unreleased")) {
        name = "unreleased-select";
    }
    var checkBoxArray = new Array();
    $('input:checkbox[name=' + name + ']:checked').each(function (i) {
        var data = $('#' + id).DataTable().row($(this).parents('tr')).data();
        var appNum = data.app_num.split(",").length;
        if (data.app_num == "") {
            appNum = 0;
        }
        checkBoxArray.push(appNum);
    });
    return checkBoxArray;
}

function logout() {
    $("#logout").click(function () {
        window.location.href = "/logout";
    });
}

function getSelectVal(id) {
    //获取选中的项
    var select = $("#" + id + " option:selected");
    //拿到选中项的值
    var val = select.val();
    return val;
}

function getSelectText() {
    //获取选中的项
    var select = $("#" + id + " option:selected");
    //拿到选中项的文本
    var text = select.text();
    return text;
}

(function ($) {
    $.MsgBox = {
        //callback传方法名
        Alert: function (title, msg, callback) {
            GenerateHtml("alert", title, msg);
            btnOk(callback);
            btnNo();
        },
        Confirm: function (title, msg, callback) {
            GenerateHtml("confirm", title, msg);
            btnOk(callback);
            btnNo();
        },
        Upload: function (title, msg, callback, noCallback) {
            GenerateHtml("upload", title, msg);
            btnUpload(callback);
            btnNo(noCallback);
        },
        Release: function (title, msg, callback, noCallback) {
            GenerateHtml("release", title, msg);
            btnOk(callback);
            btnNo(noCallback);
        },
        OverRelease: function (title, msg, data, callback, noCallback) {
            GenerateHtml("over_release", title, msg, data);
            btnOk(callback);
            btnNo(noCallback);
        },
        Offline: function (title, msg, callback, noCallback) {
            GenerateHtml("offline", title, msg);
            btnOk(callback);
            btnNo(noCallback);
        },
        BatchDelete: function (title, msg, callback, noCallback) {
            GenerateHtml("batch_delete", title, msg);
            btnOk(callback);
            btnNo(noCallback);
        },
        Delete: function (title, msg, callback, noCallback) {
            GenerateHtml("delete", title, msg);
            btnOk(callback);
            btnNo(noCallback);
        },
        ViewAll: function (title, msg, data, callback, noCallback) {
            GenerateHtml("view_all", title, msg, data);
            btnOk(callback);
            btnNo(noCallback);
        }
    };
    //生成Html
    var GenerateHtml = function (type, title, msg, data) {
        var _html = "";
        var message = msg;
        if (msg.length > 45) {
            message = msg.substring(0, 45) + "...";
        }
        _html += '<div id="mb_box"></div><div id="mb_con">';
        if (type == "alert" || type == "confirm" || type == "upload") {
            _html += '<span id="mb_tit">' + title + '</span>';
            _html += '<a id="mb_ico">X</a>';
            _html += '<div id="mb_msg">' + message + '</div><div id="mb_btnbox">';
        }
        if (type == "over_release") {
            _html += '<div style="text-align: center;top: -31px;position: relative;"><img' +
                ' src="/img/icon_fugai.png"/></div>';
            _html += '<div id="mb_msg">' + msg + '</div><div id="mb_btnbox">';
        }
        if (type == "release") {
            _html += '<div style="text-align: center;top: -31px;position: relative;"><img' +
                ' src="/img/icon_fabu1.png"/></div>';
            _html += '<div id="mb_msg">' + msg + '</div><div id="mb_btnbox">';
        }
        if (type == "offline" || type == "view_all") {
            _html += '<div style="text-align: center;top: -31px;position: relative;"><img' +
                ' src="/img/icon_xiaxian.png"/></div>';
            _html += '<div id="mb_msg">' + msg + '</div><div id="mb_btnbox">';
        }
        if (type == "batch_delete") {
            _html += '<div style="text-align: center;top: -31px;position: relative;"><img' +
                ' src="/img/icon_piliangshanchu.png"/></div>';
            _html += '<div id="mb_msg">' + msg + '</div><div id="mb_btnbox">';
        }
        if (type == "delete") {
            _html += '<div style="text-align: center;top: -31px;position: relative;"><img' +
                ' src="/img/icon_piliangshanchu.png"/></div>';
            _html += '<div id="mb_msg">' + msg + '</div><div id="mb_btnbox">';
        }
        if (type == "alert") {
            _html += '<input id="mb_btn_ok" type="button" value="' + BUTTON.CONFIRM + '" />';
        }
        if (type == "confirm") {
            _html += '<input id="mb_btn_ok" type="button" value="' + BUTTON.CONFIRM + '" />';
            _html += '<input id="mb_btn_cancel" type="button" value="' + BUTTON.CANCEL + '" />';
        }
        if (type == "upload") {
            _html += '<form id="uploadForm"><input id="file" type="file" name="file"/></form>';
            _html += '<input id="mb_btn_upload" type="button" value="' + BUTTON.UPLOAD + '"/>';
            _html += '<input id="mb_btn_cancel" type="button" value="' + BUTTON.CANCEL + '" />';
        }
        if (type == "over_release") {
            _html += '<div style="margin-top:2em;margin-bottom:2em"><table id="release_info_table" style="margin: 10px;width:780px"><tr style="height: 2em"><th>键</th><th' +
                '>值</th><th>备注</th><th>修改人</th><th>修改时间</th></tr><tr style="height: 2em"><td><div id="release_key">' + data['key'] + '</div></td><td><div id="release_value">' + data['value'] + '</div></td><td><div id="release_comment">' + data['comment'] + '</div></td><td><div id="release_last_update_by">' + data['last_update_by'] + '</div></td><td><div id="release_last_update_time">' + data['last_update_time'] + '</div></td></tr></table></div>';
            _html += '<input id="mb_btn_no" type="button" value="' + BUTTON.NO + '" />';
            _html += '<input id="mb_btn_yes" type="button" value="' + BUTTON.YES + '"/>';
        }
        if (type == "release") {
            _html += '<img id="mb_btn_x" src="/img/kit_quxiao.png"/>';
            _html += '<input id="mb_btn_release_ok" type="button" value="确认"/>';
        }
        if (type == "batch_delete") {
            _html += '<img id="mb_btn_x" src="/img/kit_quxiao.png"/>';
            _html += '<input id="mb_btn_batch_delete_ok" type="button" value="确认"/>';
        }
        if (type == "delete") {
            _html += '<img id="mb_btn_x" src="/img/kit_quxiao.png"/>';
            _html += '<input id="mb_btn_delete_ok" type="button" value="确认"/>';
        }
        if (type == "offline") {
            _html += '<img id="mb_btn_x" src="/img/kit_quxiao.png"/>';
            _html += '<input id="mb_btn_offline_ok" type="button" value="确认"/>';
        }
        if (type == "view_all") {
            _html += '<div style="margin-top:1em;margin-bottom:1em"><textarea class="view-all-textarea"' +
                ' id="view_all_textarea" disabled="disabled"></textarea></div>';
            _html += '<img id="mb_btn_x" src="/img/kit_quxiao.png"/>';
            _html += '<input id="mb_btn_view_all_ok" type="button" value="导出"/>';
        }
        _html += '</div></div>';
        //必须先将_html添加到body，再设置Css样式，否侧样式不生效
        $("body").append(_html);
        $("#mb_msg").attr("title", `${msg}`);
        if (type == "view_all") {
            var value = "";
            var length = data.length
            for (var i in data) {
                value += data[i].key + ":" + data[i].value;
                if (i < length - 1) {
                    value += "\n";
                }
            }
            $("#view_all_textarea").val(value);
        }
        if (type == "over_release") {
            $("#release_key").attr("title", `${data['key']}`);
            $("#release_value").attr("title", `${data['value']}`);
            $("#release_comment").attr("title", `${data['comment']}`);
            $("#release_last_update_by").attr("title", `${data['last_update_by']}`);
            $("#release_last_update_time").attr("title", `${data['last_update_time']}`);
        }
        //生成Css
        GenerateCss(type);
    };

    //生成Css
    var GenerateCss = function (type) {
        $("#mb_box").css({
            width: '100%',
            height: '100%',
            zIndex: '99999',
            position: 'fixed',
            filter: 'Alpha(opacity=60)',
            backgroundColor: 'black',
            top: '0',
            left: '0',
            opacity: '0.6'
        });
        if (type == "over_release") {
            $("#mb_con").css({
                zIndex: '999999',
                width: '800px',
                position: 'fixed',
                backgroundColor: 'White',
                borderRadius: '15px'
            });
        } else if (type == "view_all") {
            $("#mb_con").css({
                zIndex: '999999',
                width: '1200px',
                position: 'fixed',
                backgroundColor: 'White',
                borderRadius: '15px'
            });
        } else {
            $("#mb_con").css({
                zIndex: '999999',
                width: '400px',
                position: 'fixed',
                backgroundColor: 'White',
                borderRadius: '15px'
            });
        }
        $("#mb_tit").css({
            display: 'block',
            fontSize: '14px',
            color: '#444',
            padding: '10px 15px',
            backgroundColor: '#DDD',
            borderRadius: '15px 15px 0 0',
            borderBottom: '3px solid #5686E8',
            fontWeight: 'bold'
        });
        if (type == "over_release" || type == "offline" || type == "release" || type == "batch_delete" || type == "delete") {
            $("#mb_msg").css({
                textAlign: 'center',
                fontWeight: 'bold',
                padding: '20px',
                lineHeight: '20px',
                fontSize: '13px'
            });
        } else if (type == "view_all") {
            $("#mb_msg").css({
                textAlign: 'center',
                fontWeight: 'bold',
                fontSize: '13px'
            });
        } else {
            $("#mb_msg").css({
                padding: '20px',
                lineHeight: '20px',
                borderBottom: '1px dashed #DDD',
                fontSize: '13px'
            });
        }
        $("#mb_ico").css({
            textDecoration: 'none',
            display: 'block',
            position: 'absolute',
            right: '10px',
            top: '9px',
            border: '1px solid Gray',
            width: '18px',
            height: '18px',
            textAlign: 'center',
            lineHeight: '16px',
            cursor: 'pointer',
            borderRadius: '12px',
            fontFamily: '微软雅黑'
        });
        $("#mb_btnbox").css({
            margin: '15px 0 10px 0',
            textAlign: 'center'
        });
        $("#file").css({
            width: 'auto',
            height: '2em',
            border: 'none',
            display: 'inline-block'
        });
        $("#mb_btn_upload,#mb_btn_ok,#mb_btn_cancel,#mb_btn_yes,#mb_btn_no").css({
            width: '85px',
            height: '30px',
            color: '#FFFFFF',
            border: 'none',
            borderRadius: '0.5em'
        });
        $("#mb_btn_upload:hover,#mb_btn_ok:hover,#mb_btn_cancel:hover,#mb_btn_yes:hover,#mb_btn_no:hover").hover(function () {
            $(this).css({
                backgroundColor: '#5686E8'
            });
        }, function () {
            $(this).css({
                backgroundColor: '#FFFFFF'
            })
        });
        $("#mb_btn_offline_ok,#mb_btn_release_ok,#mb_btn_batch_delete_ok,#mb_btn_delete_ok,#mb_btn_view_all_ok").css({
            width: '160px',
            height: '22px',
            border: 'none',
            borderRadius: '0.5em',
            backgroundColor: '#FFFFFF',
            color: '#5686E8',
            boxShadow: '0 1px 2px 0 rgba(86,134,232,0.15)',
            borderRadius: '100px',
            marginLeft: '1em'
        });
        $("#mb_btn_offline_ok,#mb_btn_release_ok,#mb_btn_batch_delete_ok,#mb_btn_delete_ok,#mb_btn_view_all_ok").hover(function () {
            $(this).css({
                backgroundColor: '#5686E8',
                color: '#FFFFFF'
            });
        }, function () {
            $(this).css({
                backgroundColor: '#FFFFFF',
                color: '#5686E8'
            });
        });
        $("#mb_btn_upload").css({
            backgroundColor: '#5686E8'
        });
        $("#mb_btn_yes").css({
            backgroundColor: '#FFFFFF',
            color: '#5686E8',
            boxShadow: '0 1px 2px 0 rgba(86,134,232,0.15)',
            borderRadius: '100px',
            marginLeft: '1em'
        });
        $("#mb_btn_no").css({
            backgroundColor: '#FFFFFF',
            color: '#5686E8',
            boxShadow: '0 1px 2px 0 rgba(86,134,232,0.15)',
            borderRadius: '100px'
        });
        $("#mb_btn_x").css({
            cursor: 'pointer'
        });
        $("#mb_btn_x").hover(function () {
            $(this).attr("src", "/img/icon_piliangshanchu.png");
            $(this).css({
                width: '28px',
                height: '28px'
            });
        }, function () {
            $(this).attr("src", "/img/kit_quxiao.png");
        });
        $("#mb_btn_ok").css({
            backgroundColor: '#5686E8'
        });
        $("#mb_btn_cancel").css({
            backgroundColor: 'gray',
            marginLeft: '20px'
        });
        //右上角关闭按钮hover样式
        $("#mb_ico").hover(function () {
            $(this).css({
                backgroundColor: 'Red',
                color: 'White'
            });
        }, function () {
            $(this).css({
                backgroundColor: '#DDD',
                color: 'black'
            });
        });
        var _width = document.documentElement.clientWidth; //屏幕宽
        var _height = document.documentElement.clientHeight; //屏幕高
        var boxWidth = $("#mb_con").width();
        var boxHeight = $("#mb_con").height();
        //让提示框居中
        $("#mb_con").css({
            top: (_height - boxHeight) / 2 + 'px',
            left: (_width - boxWidth) / 2 + 'px'
        });
    };
    //上传按钮事件
    var btnUpload = function (callback) {
        $("#mb_btn_upload").click(function () {
            if (typeof(callback) == 'function') {
                if (callback()) {
                    $("#mb_box,#mb_con").remove();
                }
            }
        });
    };
    //确定按钮事件
    var btnOk = function (callback) {
        $("#mb_btn_ok,#mb_btn_yes,#mb_btn_offline_ok,#mb_btn_release_ok,#mb_btn_batch_delete_ok,#mb_btn_delete_ok,#mb_btn_view_all_ok").click(function () {
            $("#mb_box,#mb_con").remove();
            if (typeof(callback) == 'function') {
                callback();
            }
        });
    };
    //取消按钮事件
    var btnNo = function (noCallback) {
        $("#mb_btn_cancel,#mb_ico,#mb_btn_no,#mb_btn_x").click(function () {
            $("#mb_box,#mb_con").remove();
            if (typeof(noCallback) == 'function') {
                noCallback();
            }
        });
    };
})(jQuery);

//判断是否包含特殊的字段
function arrayContains(arrays, key) {
    for (var i in arrays) {
        if (key.endWith(arrays[i])) {
            return true;
        }
    }
    return false;
};

String.prototype.isBlank = function () {
    return (!this || $.trim(this) === "");
};

String.prototype.endWith = function (str) {
    if (str == null || str == "" || this.length == 0 || str.length > this.length) {
        return false;
    }
    if (this.substring(this.length - str.length) == str) {
        return true;
    } else {
        return false;
    }
};

String.prototype.startWith = function (str) {
    if (str == null || str == "" || this.length == 0 || str.length > this.length) {
        return false;
    }
    if (this.substr(0, str.length) == str) {
        return true;
    } else {
        return false;
    }
};

function selected(id, attr) {
    var text = getItem(attr);
    if (text.length > 1) {
        selectedByText(id, text);
    } else {
        var option = '<option value="-1"></option>'
        $('#' + id).prepend(option);
        $('#' + id).find('option[value="-1"]').prop({selected: true});
    }
}

function selectedByText(id, text) {
    $('#'+ id + ' > option').filter(function(){return $(this).text()==text;}).prop({selected: true});
}

function selectedByVal(id, val) {
    $('#' + id).val(val).prop({selected: true});
}

function setItem(key, value) {
    window.sessionStorage.setItem(key, value);
}

function getItem(key) {
    return window.sessionStorage.getItem(key)
}

function removeItem(key) {
    return window.sessionStorage.removeItem(key);
}

function clickSetMainHeight(isSet) {
    $("#main").css("height", "");
    if (isSet) {
        $("#main").css("height", "100%");
    }
}

function initQuery(table, url, filterId, productModuleEnvId, versionId, settings, viewAllId, copyConfigId) {
    var flag1 = typeof(productModuleEnvId) != "undefined";
    var flag2 = typeof(versionId) != "undefined";
    var select = ".dataTables_filter";
    if (filterId) {
        select = "#" + filterId + select;
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
                                $.MsgBox.ViewAll(MESSAGES.NEWS, "所有配置", data, function () {
                                    var url = "/instance/download?isAllDownload=true&instanceTypeId=0&versionId=" + versionId + "&productModuleEnvId=" + productModuleEnvId;
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
            if (copyConfigId.startsWith("gray")) {
                html = HTML.INSTANCE_GRAY_COPY_CONFIG;
            } else {
                html = HTML.INSTANCE_COPY_CONFIG;
            }
            $(select + " > #" + copyConfigId).click(function () {
                $("#main").load(html, function (response, status, xhr) {
                    if (xhr.status != 200) {
                        ajaxError(xhr, status, response);
                    }
                });
            });
        }
        $(select + " > .query-search").click(function () {
            var name = $(select + " > label > input").val();
            if (flag1 && flag2) {
                table.ajax.url(url + '/query_by_name?productModuleEnvId=' + productModuleEnvId + '&versionId=' + versionId + '&name=' + name).load();
            } else {
                table.ajax.url(url + '/query_by_name?name=' + name).load();
            }
            //重新加载有清除的数据，重新设置刚才查询数据，避免问题
            $(select + " > label > input").val(name);
        });
        $(select + " > label > input").on('keypress', function (event) {
            if (event.keyCode == 13) {
                var name = $(this).val();
                if (flag1 && flag2) {
                    table.ajax.url(url + '/query_by_name?productModuleEnvId=' + productModuleEnvId + '&versionId=' + versionId + '&name=' + name).load();
                } else {
                    table.ajax.url(url + '/query_by_name?name=' + name).load();
                }
                $(select + " > label > input").val(name);
            }
        });
        $(select + "> #clear_query").click(function () {
            var loadUrl;
            if (flag2) {
                loadUrl = url + '/query_properties?versionId=' + versionId;
                if (flag1) {
                    loadUrl += '&productModuleEnvId=' + productModuleEnvId;
                }
            } else {
                if (url == "/history_log_manage/") {
                    loadUrl = url + '/query?versionId=' + versionId;
                } else {
                    loadUrl = url + '/query';
                }
            }
            table.ajax.url(loadUrl).load();
            $(select + " > label > input").val("");
        })
    }
}

function initToPage(settings, table, wrapperId) {
    var select = "";
    if (wrapperId) {
        select = "#" + wrapperId + " > ";
    }
    var total = settings._iRecordsTotal;
    var length = settings._iDisplayLength;
    var pageNum = Math.ceil(total / length);
    $(select + ".dataTables_paginate").prepend('共' + total + '条&nbsp;&nbsp;');
    $(select + ".dataTables_paginate").append('<span class="refresh-to">' + BUTTON.REFRESH_TO + '</span><span' +
        ' class="btm-page-info">&nbsp;&nbsp;<input class="to-page" id="to_page" autocomplete="off" type="number"/>&nbsp;&nbsp;页</span><span id="paginate_btn" class="paginate-sure">' + BUTTON.REFRESH + '</span>');
    $(select + ".dataTables_paginate > #paginate_btn").hover(function () {
        if ($('#to_page').val()) {
            var page = parseInt($(select + '.dataTables_paginate > .btm-page-info > #to_page').val());
            if (page > pageNum || page < 1) {
                $(select + '.dataTables_paginate > #paginate_btn').css("cursor", "not-allowed");
            } else {
                $(select + '.dataTables_paginate > #paginate_btn').css("cursor", "pointer");
            }
        }
    })
    $(select + '.dataTables_paginate > #paginate_btn').click(function () {
        var page = $(select + '.dataTables_paginate > .btm-page-info > #to_page').val();
        page = parseInt(page) || 1;
        if (page <= pageNum && page > 0) {
            page -= 1;
            table.page(page).draw(false);
        }
    });
    $(select + '.dataTables_paginate > .btm-page-info > #to_page').on('keypress', function (event) {
        if (event.keyCode == 13) {
            var page = $(select + '.dataTables_paginate > .btm-page-info > #to_page').val();
            page = parseInt(page) || 1;
            if (page <= pageNum && page > 0) {
                page -= 1;
                table.page(page).draw(false);
            }
        }
    });
}

function clickTableTr(selectName) {
    var name = "select";
    if (selectName && selectName != null) {
        name = selectName;
    }
    $(".dataTable > tbody > tr").click(function () {
        var checkBox = $(this).find('input[name="' + name + '"]');
        if (checkBox.prop("checked")) {
            checkBox.prop({checked: false});
        } else {
            checkBox.prop({checked: true});
        }
    });
    $('.dataTable > tbody > tr > td > input[name="' + name + '"]').click(function () {
        if ($(this).prop("checked")) {
            $(this).prop({checked: false});
        } else {
            $(this).prop({checked: true});
        }
    });
}

function delPost(url, ids, tableId) {
    //请求删除
    var ajax = $.ajax({
        url: url,
        type: "post",
        data: {ids: ids},
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
                        $('#' + tableId).DataTable().ajax.reload(function () {
                            if (url.startsWith("/instance/delete_by_ids")) {
                                setViewStatus(tableId);
                            }
                        });
                        $('#list_select_all').prop({checked: false});
                        if (getItem("versionId") == "0") {
                            $('#main_list_select_all').prop({checked: false});
                        } else {
                            $('#gray_list_select_all').prop({checked: false});
                        }
                        var productFlag = url.startsWith("/product/delete_by_ids");
                        var moduleProductFlag = url.startsWith("/module_product/delete_by_ids");
                        var envProductFlag = url.startsWith("/env_module_product/delete_by_ids");
                        //项目，模块，环境有删除刷新页面
                        if (productFlag || moduleProductFlag || envProductFlag) {
                            location.reload();
                        }
                    });
                } else {
                    $.MsgBox.Alert(MESSAGES.NEWS, data.message);
                }
            }
        },
        complete: function (XMLHttpRequest, status) {
            ajaxTimeout(status, ajax);
        },
    });
}

function isNumber(val) {
    //非负浮点数
    var regPos = /^\d+(\.\d+)?$/;
    var regNeg = /^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$/; //负浮点数
    if (regPos.test(val) || regNeg.test(val)) {
        return true;
    } else {
        return false;
    }
}

function keyUpCheckKey(id) {
    $("#" + id).bind("keyup", function () {
        $(this).val(checkOtherLanguage($(this).val()));
        $(this).val(checkIsAllNumber($(this).val()));
    });
}

function checkIsAllNumber(val) {
    var temp = "";
    //非负浮点数
    var regPos = /^\d+(\.\d+)?$/;
    var regNeg = /^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$/; //负浮点数
    if (regPos.test(val) || regNeg.test(val)) {
        return temp;
    } else {
        var headNumFlag = true;
        for (var i = 0; i < val.length; i++) {
            if ((val.charCodeAt(i) > 57 && val.charCodeAt(i) < 255) || (val.charCodeAt(i) > 0 && val.charCodeAt(i) < 48)) {
                temp += val.charAt(i);
                headNumFlag = false;
            } else {
                if (!headNumFlag) {
                    temp += val.charAt(i);
                }
            }
        }
        return temp;
    }
}

function checkOtherLanguage(str) {
    var temp = ""
    for (var i = 0; i < str.length; i++) {
        if (str.charCodeAt(i) > 0 && str.charCodeAt(i) < 255) {
            temp += str.charAt(i);
        }
    }
    return temp
}

function addPage(html, productModuleEnvId) {
    $("#add_btn").click(function () {
        if (productModuleEnvId) {
            setItem("productModuleEnvId", productModuleEnvId);
        } else {
            window.sessionStorage.removeItem("productModuleEnvId");
        }
        if (html == "/public_config_add.html") {
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

function updatePage(tableId, html, productModuleEnvId) {
    $('span#edit').click(function () {
        var data = $('#' + tableId).DataTable().row($(this).parents('tr')).data();
        for (var name in data) {
            setItem(name, data[name]);
        }
        if (productModuleEnvId) {
            setItem("productModuleEnvId", productModuleEnvId);
        } else {
            window.sessionStorage.removeItem("productModuleEnvId");
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

function singleDelete(url, tableId) {
    var ids = new Array();
    $('span#del').click(function () {
        var data = $('#' + tableId).DataTable().row($(this).parents('tr')).data();
        ids[0] = data.id;
        $.MsgBox.Delete(MESSAGES.CONFIRM, MESSAGES.IS_DELETE, function () {
            delPost(url, ids, tableId);
        });
    });
}

function batchDelete(url, tableId) {
    var ids = new Array();
    $("#delete_btn").click(function () {
        //获取到选中的
        ids = getCheckBoxArray(tableId);
        if (ids.length < 1) {
            $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.NOT_SELECTED);
        } else {
            $.MsgBox.BatchDelete(MESSAGES.CONFIRM, MESSAGES.IS_BATCH_DELETE, function () {
                delPost(url, ids, tableId);
            });
        }
    });
}

function batchGrayDelete(url, tableId) {
    var ids = new Array();
    $("#gray_delete_btn").click(function () {
        //获取到选中的
        ids = getCheckBoxArray(tableId);
        if (ids.length < 1) {
            $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.NOT_SELECTED);
        } else {
            $.MsgBox.BatchDelete(MESSAGES.CONFIRM, MESSAGES.IS_BATCH_DELETE, function () {
                delPost(url, ids, tableId);
            });
        }
    });
}

function loadSelect(id, url, attr, isLoad, isSelect, fun) {
    //清空
    $("#" + id).empty();
    if (typeof(isSelect) == "undefined" || !isSelect) {
        var option = '<option value="-1" style="display: none"></option>'
        $("#" + id).append(option);
    }
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
                    if (isSelect) {
                        $("#" + id + "[multiple='multiple']").fSelect();
                        if (isLoad) {
                            $(".fs-option").each(function () {
                                var text = $(this).children("div").text();
                                var item = getSessionItem(id);
                                var names = getItem(item).split(",");
                                for (var i in names) {
                                    if (text == names[i]) {
                                        fsOptionClick($(this));
                                    }
                                }
                            });
                        }
                    } else if (isLoad) {
                        selected(id, attr);
                    }
                    if (fun && typeof(fun) == 'function') {
                        fun();
                    }
                }
            }
        }
    });
}

function getSessionItem(id) {
    var item = "";
    switch (id) {
        case "product":
            item = "product_name";
            break;
        case "module":
            item = "module_name";
            break;
        case "env":
            item = "env_name";
            break;
        case "area":
            item = "area_name";
            break;
        case "organ":
            item = "organ_name";
            break;
        case "user":
            item = "user_name";
            break;
        case "role":
            item = "role_name";
            break;
        default:
            break;
    }
    return item;
}

function fsOptionClick(obj) {
    var $wrap = obj.closest('.fs-wrap');
    if ($wrap.hasClass('multiple')) {
        var selected = [];
        obj.toggleClass('selected');
        $wrap.find('.fs-option.selected').each(function (i, el) {
            selected.push($(el).attr('data-value'));
        });
    } else {
        var selected = obj.attr('data-value');
        $wrap.find('.fs-option').removeClass('selected');
        obj.addClass('selected');
        $wrap.find('.fs-dropdown').hide();
    }
    $wrap.find('select').val(selected);
    $wrap.find('select').fSelect('reloadDropdownLabel');
}

function action(getFunction, validFunction, url, html, type, isJson, id1, id2, id3) {
    if (typeof(validFunction) == 'boolean') {
        validFunction = function () {
            return true;
        }
    }
    $("#save").click(function () {
        //清空错误信息
        $("#" + id1).empty();
        $("#" + id2).empty();
        $("#" + id3).empty();
        var obj = getFunction();
        if (type == "update") {
            obj.id = $("#id").text();
        }
        //验证数据格式
        if (validFunction()) {
            if (isJson) {
                $.ajax({
                    url: url,
                    type: "post",
                    data: obj,
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
                            if (data.message.indexOf("成功") != -1) {
                                $.MsgBox.Alert(MESSAGES.NEWS, data.message, function () {
                                    $("#main").load(html, function (response, status, xhr) {
                                        if (xhr.status != 200) {
                                            ajaxError(xhr, status, response);
                                        } else {
                                            var productFlag = url.startsWith("/product/insert") || url.startsWith("/product/update");
                                            var moduleProductFlag = url.startsWith("/module_product/insert") || url.startsWith("/module_product/update");
                                            var envProductFlag = url.startsWith("/env_module_product/insert") || url.startsWith("/env_module_product/update");
                                            //项目，模块，环境有改变刷新页面
                                            if (productFlag || moduleProductFlag || envProductFlag) {
                                                location.reload();
                                            }
                                        }
                                    });
                                });
                            } else {
                                //验证重复
                                if (data.message.indexOf("重复") != -1) {
                                    if (!id2 && !id3) {
                                        if (url.indexOf("instance") != -1 || url.indexOf("public") != -1) {
                                            $("#" + id1).append(data.message);
                                        } else {
                                            $("#" + id1).append(MESSAGES.INPUT_IS_EXIST);
                                        }
                                    } else {
                                        if (data.message.indexOf("用户") != -1) {
                                            $("#" + id1).append(MESSAGES.INPUT_IS_EXIST);
                                        }
                                        if (data.message.indexOf("电话") != -1) {
                                            $("#" + id2).append(MESSAGES.INPUT_IS_EXIST);
                                        }
                                        if (data.message.indexOf("邮箱") != -1) {
                                            $("#" + id3).append(MESSAGES.INPUT_IS_EXIST);
                                        }
                                    }
                                } else {
                                    $.MsgBox.Alert(MESSAGES.NEWS, data.message);
                                }
                            }
                        }
                    }
                });
            } else {
                $.ajax({
                    url: url,
                    type: "post",
                    data: obj,
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
                            if (data.message.indexOf("成功") != -1) {
                                $.MsgBox.Alert(MESSAGES.NEWS, data.message, function () {
                                    $("#main").load(html, function (response, status, xhr) {
                                        if (xhr.status != 200) {
                                            ajaxError(xhr, status, response);
                                        } else {
                                            var productFlag = url.startsWith("/product/insert") || url.startsWith("/product/update");
                                            var moduleProductFlag = url.startsWith("/module_product/insert") || url.startsWith("/module_product/update");
                                            var envProductFlag = url.startsWith("/env_module_product/insert") || url.startsWith("/env_module_product/update");
                                            //项目，模块，环境有改变刷新页面
                                            if (productFlag || moduleProductFlag || envProductFlag) {
                                                location.reload();
                                            }
                                        }
                                    });
                                });
                            } else {
                                //验证重复
                                if (data.message.indexOf("重复") != -1) {
                                    if (!id2 && !id3) {
                                        if (url.indexOf("instance") != -1 || url.indexOf("public") != -1) {
                                            $("#" + id1).append(data.message);
                                        } else {
                                            $("#" + id1).append(MESSAGES.INPUT_IS_EXIST);
                                        }
                                    } else {
                                        if (data.message.indexOf("用户") != -1) {
                                            $("#" + id1).append(MESSAGES.INPUT_IS_EXIST);
                                        }
                                        if (data.message.indexOf("电话") != -1) {
                                            $("#" + id2).append(MESSAGES.INPUT_IS_EXIST);
                                        }
                                        if (data.message.indexOf("邮箱") != -1) {
                                            $("#" + id3).append(MESSAGES.INPUT_IS_EXIST);
                                        }
                                    }
                                } else {
                                    $.MsgBox.Alert(MESSAGES.NEWS, data.message);
                                }
                            }
                        }
                    }
                });
            }
        }
    });
    $("#cancel").click(function () {
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
    })
}

function addAction(getFunction, validFunction, url, html, isJson, id1, id2, id3) {
    action(getFunction, validFunction, url, html, "insert", isJson, id1, id2, id3);
}

function updateAction(getFunction, validFunction, url, html, isJson, id1, id2, id3) {
    action(getFunction, validFunction, url, html, "update", isJson, id1, id2, id3);
}

function getSelectedIdArray(id) {
    var textArray = new Array();
    var selectedIdArray = new Array();
    $("#" + id).parent("div").children(".fs-dropdown").children(".fs-options").children(".selected").each(function () {
        textArray.push($(this).children("div").text());
    });
    $("option").each(function () {
        for (var i in textArray) {
            if ($(this).text() == textArray[i]) {
                var selectedId = parseInt($(this).val());
                selectedIdArray.push(selectedId);
            }
        }
    });
    return selectedIdArray;
}

function loadProductSelectData(isLoad, isSelect, param) {
    if (param) {
        loadSelect("product", "/product/product_data?isAll=" + param, "product_name", isLoad, isSelect);
    } else {
        loadSelect("product", "/product/product_data", "product_name", isLoad, isSelect);
    }

}

function loadEnvSelectData(isLoad, isSelect, fun) {
    loadSelect("env", "/env/env_data", "env_name", isLoad, isSelect, fun);
}

function loadAreaSelectData(isLoad, isSelect, fun) {
    loadSelect("area", "/area/area_data", "area_name", isLoad, isSelect, fun);
}

function loadOrganSelectData(isLoad, isSelect, fun) {
    loadSelect("organ", "/organ/organ_data", "organ_name", isLoad, isSelect, fun);
}

function loadUserSelectData(isLoad, isSelect, fun) {
    loadSelect("user", "/user/user_data", "user_name", isLoad, isSelect, fun);
}

function loadRoleSelectData(isLoad, isSelect, fun) {
    loadSelect("role", "/role/role_data", "role_name", isLoad, isSelect, fun);
}

function initLinkageAreaOrgan() {
    areaChange();
}

function initLinkageOrganUser() {
    organChange();
}

function areaChange() {
    $("#area").change(function () {
        linkageOrgan();
    });
}

function organChange() {
    $("#organ").change(function () {
        linkageUser();
    });
}

function linkageOrgan() {
    var areaSelectId = "area";
    var organSelectId = "organ";
    var userSelectId = "user";
    $("#" + organSelectId).empty();
    $("#" + userSelectId).empty();
    var data = new Object();
    data.areaId = getSelectVal(areaSelectId);
    var url = "/organ/organ_data";
    linkage(url, data, organSelectId);
}

function linkageUser() {
    var areaSelectId = "area";
    var organSelectId = "organ";
    var userSelectId = "user";
    $("#" + userSelectId).empty();
    var url = "/user/user_data";
    var data = new Object();
    data.areaId = getSelectVal(areaSelectId);
    data.organId = getSelectVal(organSelectId);
    linkage(url, data, userSelectId);
}

function linkage(url, data, tId, fun) {
    $.ajax({
        url: url,
        type: "post",
        data: data,
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
                $("#" + tId).empty();
                if (data && data.length != 0) {
                    var option = '<option value="-1" style="display: none"></option>'
                    $("#" + tId).append(option);
                    for (var i = 0; i < data.length; i++) {
                        var option = "<option value=\"" + data[i].id + "\"";
                        option += ">" + data[i].name + "</option>";
                        $("#" + tId).append(option);
                    }
                }
                if (typeof(fun) == "function") {
                    fun();
                }
            }
        }
    });
}

function validText(text, name, length) {
    var textLength = 20;
    if (length) {
        textLength = length;
    }
    if (!validNotBlank(text, name)) {
        return false;
    } else if (text.length > textLength) {
        $.MsgBox.Alert(MESSAGES.NEWS, name + MESSAGES.EXCEED_CHARACTER);
        return false;
    } else {
        return true;
    }
}

function getByteLen(val) {
    var len = 0;
    for (var i = 0; i < val.length; i++) {
        var a = val.charAt(i);
        if (a.match(/[^\x00-\xff]/ig) != null) {
            len += 2;
        } else {
            len += 1;
        }
    }
    return len;
}

function validValueShowInfo(value, length, id, notValidBlank) {
    var textLength = -1;
    if (length) {
        textLength = length;
    }
    $("#" + id).empty();
    if (!notValidBlank && (!value || value.isBlank())) {
        $("#" + id).append(MESSAGES.INPUT_IS_NOT_NULL);
        return false;
    } else if (textLength != -1 && getByteLen(value) > textLength) {
        $("#" + id).append(MESSAGES.EXCEED + length + MESSAGES.CHARACTER);
        return false;
    } else {
        return true;
    }
}

function validValueStartWithShowInfo(value, id) {
    $("#" + id).empty();
    if (!value.startsWith("pub.")) {
        $("#" + id).append(MESSAGES.PUBLIC_CONFIG_KEY_MUST_START_WITH_PUB);
        return false;
    } else if (value == "pub."){
        $("#" + id).append(MESSAGES.PUBLIC_CONFIG_KEY_NOT_EQUAL_PUB);
        return false;
    } else {
        return true;
    }
}

function validValueNotStartWithShowInfo(value, id) {
    $("#" + id).empty();
    if (value.startsWith("pub.")) {
        $("#" + id).append(MESSAGES.PRODUCT_CONFIG_KEY_CAN_NOT_START_WITH_PUB);
        return false;
    } else {
        return true;
    }
}

function validPasswordShowInfo(value, id) {
    var minLength = 6;
    var maxLength = 20;
    $("#" + id).empty();
    if (!value || value.isBlank()) {
        $("#" + id).append(MESSAGES.INPUT_IS_NOT_NULL);
        return false;
    } else if (getByteLen(value) > maxLength) {
        $("#" + id).append(MESSAGES.EXCEED + maxLength + MESSAGES.CHARACTER);
        return false;
    } else if (getByteLen(value) < minLength) {
        $("#" + id).append(MESSAGES.AT_LEAST + minLength + MESSAGES.CHARACTER);
        return false;
    } else {
        return true;
    }
}

function validNotBlank(text, name) {
    if (!text || text.isBlank()) {
        $.MsgBox.Alert(MESSAGES.NEWS, name + MESSAGES.IS_NULL);
        return false;
    } else {
        return true;
    }
}

function validSelectedApp(appIdd) {
    var appId = "app";
    if (appIdd) {
        appId = appIdd;
    }
    var isSelected = false;
    $('#' + appId + ' > div > input:checkbox[name=app-input]:checked').each(function (i) {
        isSelected = true;
    });
    if (!isSelected) {
        $.MsgBox.Alert(MESSAGES.NEWS, "app" + MESSAGES.NOT_SELECTED);
    }
    return isSelected;
}

function validSelectedAppShowInfo(appIdd, appInfoIdd) {
    var appId = "app";
    var appInfoId = "app_info";
    if (appIdd) {
        appId = appIdd;
    }
    if (appInfoIdd) {
        appInfoId = appInfoIdd;
    }
    var isSelected = false;
    $('#' + appId + ' > div').each(function () {
        $(this).children('input:checkbox:checked').each(function () {
            isSelected = true;
        });
    });
    $("#" + appInfoId).empty();
    if (!isSelected) {
        $("#" + appInfoId).append(MESSAGES.NOT_SELECTED)
    }
    return isSelected;
}

function validSelectedApply(applyIdd) {
    var applyId = "product_module_table";
    if (applyIdd) {
        applyId = applyIdd;
    }
    var isSelected = false;
    $('#' + applyId + ' > tbody > tr > td > input:checkbox[name=product-module-input]:checked').each(function (i) {
        isSelected = true;
    });
    if (!isSelected) {
        $.MsgBox.Alert(MESSAGES.NEWS, "应用范围" + MESSAGES.NOT_SELECTED);
    }
    return isSelected;
}

function validSelectedApplyShowInfo(applyIdd, applyInfoIdd, selectName) {
    var applyId = "product_module_table";
    var applyInfoId = "apply_info";
    if (applyIdd) {
        applyId = applyIdd;
    }
    if (applyInfoIdd) {
        applyInfoId = applyInfoIdd;
    }
    var name = "select";
    if (selectName) {
        name = selectName;
    }
    var isSelected = false;
    $('#' + applyId + ' > tbody > tr > td > input:checkbox[name=' + name + ']:checked').each(function (i) {
        isSelected = true;
    });
    $("#" + applyInfoId).empty();
    if (!isSelected) {
        $("#" + applyInfoId).append(MESSAGES.NOT_SELECTED);
    }
    return isSelected;
}

function validSelectedAuth() {
    var isSelected = false;
    $('input:checkbox:checked').each(function (i) {
        isSelected = true;
    });
    return isSelected;
}

function getSelectedAppIds(appIdd, selectName) {
    var appId = "app";
    if (appIdd) {
        appId = appIdd;
    }
    var name = "app-input";
    if (selectName) {
        name = selectName;
    }
    var appIds = new Array();
    $('#' + appId + ' > div > input:checkbox[name=' + name + ']:checked').each(function (i) {
        var id = $(this).attr("id");
        if (id.indexOf("_") != -1) {
            var ids = id.split("_");
            id = ids[ids.length - 1];
        }
        appIds.push(id);
    });
    return appIds;
}

function getSelectedResourceIds(tableIdd, selectName) {
    var tableId = "product_module_table";
    if (tableIdd) {
        tableId = tableIdd;
    }
    var name = "select";
    if (selectName) {
        name = selectName;
    }
    var tableIds = new Array();
    $('#' + tableId + ' > tbody > tr > td >  input:checkbox[name=' + name + ']:checked').each(function (i) {
        var id = $(this).attr("id");
        if (typeof(id) == "string" && id.indexOf("_") != -1) {
            var ids = id.split("_");
            id = ids[ids.length - 2];
        }
        tableIds.push(id);
    });
    return tableIds;
}

function getSelectedResourceIdsListMap(tableIdd, selectName) {
    var resourceIdsJsonArray = new Array();
    var tableId = "product_module_table";
    if (tableIdd) {
        tableId = tableIdd;
    }
    var name = "select";
    if (selectName) {
        name = selectName;
    }
    $('#' + tableId + ' > tbody > tr > td >  input:checkbox[name=' + name + ']').each(function (i) {
        var resourceIdsJson = new Object();
        var id = $(this).attr("id");
        if (typeof(id) == "string" && id.indexOf("_") != -1) {
            var ids = id.split("_");
            id = ids[ids.length - 2];
        }
        var checked = $(this).prop('checked');
        resourceIdsJson.id = id;
        if (checked) {
            resourceIdsJson.is_select = 1;
        } else {
            resourceIdsJson.is_select = 0;
        }
        resourceIdsJsonArray.push(resourceIdsJson);
    });
    return resourceIdsJsonArray;
}

function validSelectdAuth() {
    var isSelected = false;
    $('input:checkbox[class=auth-input]:checked').each(function (i) {
        isSelected = true;
    });
    if (!isSelected) {
        $.MsgBox.Alert(MESSAGES.NEWS, "权限" + MESSAGES.NOT_SELECTED);
    }
    return isSelected;
}

function validMobile(mobileText) {
    var phone = /^1[34578]\d{9}$/;
    if (!mobileText || mobileText.match(phone) == null) {
        return false;
    }
    return true;
}

function validTel(telText) {
    var tel = /^(\d3,4|\d{3,4}-)?\d{7,8}$/;
    if (!telText || telText.match(tel) == null) {
        return false;
    }
    return true;
}

function validEmail(emailText) {
    var mail = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
    if (!emailText || emailText.match(mail) == null) {
        return false;
    }
    return true;
}

function alertSelectIsBlank(val, text) {
    if (!val || val.isBlank()) {
        $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.NOT_SELECTED + text);
        return false;
    }
    return true;
}

function validIsSelect(id) {
    if (id == -1 || typeof(id) == "undefined") {
        return false;
    }
    return true;
}

function loadSelected(i, obj) {
    if (i == 1) {
        obj.children("input").prop({checked: true});
    }
}

function getDataTableData() {
    var dataTableData = new Array();
    var dataTable = $('#role_auth_resource_action_table').DataTable();
    var trs = dataTable.rows().nodes();
    for (var i = 0; i < trs.length; i++) {
        var obj = dataTable.rows(trs[i]).data()[0];
        var retObj = new Object();
        retObj.resourceId = parseInt(obj.id);
        retObj.select = 0;
        retObj.insert = 0;
        retObj.update = 0;
        retObj.delete = 0;
        retObj.release = 0;
        retObj.offline = 0;
        $(trs[i]).children('td').children('input:checkbox:checked').each(function () {
            var name = $(this).attr("name");
            switch (name) {
                case "select":
                    retObj.select = 1;
                    break;
                case "insert":
                    retObj.insert = 1;
                    break;
                case "update":
                    retObj.update = 1;
                    break;
                case "delete":
                    retObj.delete = 1;
                    break;
                case "release":
                    retObj.release = 1;
                    break;
                case "offline":
                    retObj.offline = 1;
                    break;
                default:
                    break;
            }
        });
        dataTableData.push(retObj);
    }
    return dataTableData;
}

function getUserAuthResourceSetLeft() {
    var userAuthResourceAjax = $.ajax({
        url: "/role_auth_resource/get_user_auth_resource",
        type: "POST",
        timeout: 30000,
        async: false,
        success: function (response) {
            if (typeof(response) == "string" && response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                    location.reload();
                });
            } else {
                var resources = response.resources;
                for (var productId in resources) {
                    setLeftResource(productId, resources[productId]);
                }
                clickOuterLi();
                clickMiddleLi();
                clickInnerLi();
                var isAdmin = response.is_admin;
                if (isAdmin) {
                    $(".auth-hidden").removeClass("auth-hidden");
                } else {
                    var datas = response.data;
                    for (var i in datas) {
                        var data = datas[i];
                        var productId = data.product_id;
                        var moduleId = data.module_id;
                        var envId = data.env_id;
                        var key = data.product_name + data.module_name;
                        //配置的id
                        var id = NAV.SETTING_IDS[key];
                        //动态项目id
                        if (!id) {
                            var productModuleEnvId = productId + "_" + moduleId + "_" + envId;
                            $("#" + productModuleEnvId).removeClass("auth-hidden");
                            $("#" + productModuleEnvId).parent("ul").parent("li").removeClass("auth-hidden");
                            $("#" + productModuleEnvId).parent("ul").parent("li").parent("ul").parent("li").removeClass("auth-hidden");
                        } else {
                            $("#" + id).removeClass("auth-hidden");
                            $("#" + id).parent("ul").parent("li").removeClass("auth-hidden");
                            $("#" + id).parent("div").parent("li").removeClass("auth-hidden");
                        }
                    }
                }
                //获取第一个页面
                var firstHtml = "";
                judgeLastHtml(firstHtml);
            }
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                userAuthResourceAjax.abort();
                window.location.href = "/timeout";
            }
        },
        error: function (xhr, textStatus, thrownError) {
            ajaxError(xhr, textStatus, thrownError);
        }
    });
}

function getUserAuthResourcePage(tableId, html, versionId, productModuleEnvIdP) {
    var userAuthResourceAjax = $.ajax({
        url: "/role_auth_resource/get_user_auth_resource",
        type: "POST",
        timeout: 30000,
        async: false,
        success: function (response) {
            if (typeof(response) == "string" && response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                    location.reload();
                });
            } else {
                var isAdmin = response.is_admin;
                if (isAdmin) {
                    $("span#edit").removeClass("auth-hidden");
                    $("span#del").removeClass("auth-hidden");
                    $("span#properties_edit").removeClass("auth-hidden");
                    $("span#properties_offline").removeClass("auth-hidden");
                    $("span#properties_release").removeClass("auth-hidden");
                    $("span#properties_del").removeClass("auth-hidden");
                    $("span#gray_properties_add").removeClass("auth-hidden");
                    $("span#gray_properties_edit").removeClass("auth-hidden");
                    $("span#gray_properties_offline").removeClass("auth-hidden");
                    $("span#gray_properties_release").removeClass("auth-hidden");
                    $("span#gray_properties_del").removeClass("auth-hidden");
                    $(".btm-btn-div > span.auth-hidden").removeClass("auth-hidden");
                    $("#main_version_btn").removeClass("auth-hidden");
                    $("#gray_version_btn").removeClass("auth-hidden");
                    $("#copy_config").removeClass("auth-hidden");
                    $("#gray_copy_config").removeClass("auth-hidden");
                    if (html == HTML.HISTORY_LOG_MANAGE_GRAY || html == HTML.HISTORY_LOG_MANAGE_MAIN) {
                        $("#release_btn").removeClass("auth-hidden");
                        $("#offline_btn").removeClass("auth-hidden");
                        $("span#release").removeClass("auth-hidden");
                        $("span#offline").removeClass("auth-hidden");
                        $("#unreleased_btn").removeClass("auth-hidden");
                        $("#unreleased_tab").removeClass("auth-hidden");
                        $("#released_btn").removeClass("auth-hidden");
                        $("#released_tab").removeClass("auth-hidden");
                    }
                    if (typeof(versionId) != "undefined") {
                        if (versionId == 0) {
                            $("#main_version_tab").removeClass("auth-hidden");
                            $("#main_version_btn").addClass("version-btn-active");
                            $("#add_btn").removeClass("auth-hidden");
                            $("#import_btn").removeClass("auth-hidden");
                            $("#export_btn").removeClass("auth-hidden");
                            $("#release_btn").removeClass("auth-hidden");
                            $("#delete_btn").removeClass("auth-hidden");
                        } else {
                            $("#gray_version_tab").removeClass("auth-hidden");
                            $("#gray_version_btn").addClass("version-btn-active");
                            $("#gray_import_btn").removeClass("auth-hidden");
                            $("#gray_export_btn").removeClass("auth-hidden");
                            $("#gray_release_btn").removeClass("auth-hidden");
                            $("#gray_delete_btn").removeClass("auth-hidden");
                        }
                    } else {
                        $("#add_btn").removeClass("auth-hidden");
                        $("#delete_btn").removeClass("auth-hidden");
                    }
                } else {
                    var datas = response.data;
                    for (var i in datas) {
                        var data = datas[i];
                        var auths = data.auths.split(",");
                        var productName = data.product_name;
                        var moduleName = data.module_name;
                        var envName = data.env_name;
                        var versionName = data.version_name;
                        var productModuleEnvId = data.product_id + "_" + data.module_id + "_" + data.env_id;
                        var dataId = data.id;
                        var key = productName + moduleName + envName + versionName;
                        if (dataId == "9" && html == HTML.PUBLIC_CONFIG) {
                            $("#main_version_btn").removeClass("auth-hidden");
                            $("#main_version_tab").removeClass("auth-hidden");
                            if (versionId == 0) {
                                $("#gray_version_btn").removeClass("version-btn-active");
                                $("#main_version_btn").addClass("version-btn-active");
                            }
                        }
                        if (dataId == "10" && html == HTML.PUBLIC_CONFIG) {
                            $("#gray_version_btn").removeClass("auth-hidden");
                            $("#gray_version_tab").removeClass("auth-hidden");
                            if (versionId == 1) {
                                $("#main_version_btn").removeClass("version-btn-active");
                                $("#gray_version_btn").addClass("version-btn-active");
                            }
                        }
                        if ((dataId == "11" && html == HTML.HISTORY_LOG_MANAGE_MAIN) || (dataId == "13" && html == HTML.HISTORY_LOG_MANAGE_GRAY)) {
                            $("#released_btn").removeClass("auth-hidden");
                            $("#released_tab").removeClass("auth-hidden");
                        }
                        if ((dataId == "12" && html == HTML.HISTORY_LOG_MANAGE_MAIN) || (dataId == "14" && html == HTML.HISTORY_LOG_MANAGE_GRAY)) {
                            $("#unreleased_btn").removeClass("auth-hidden");
                            $("#unreleased_tab").removeClass("auth-hidden");
                        }
                        if (productModuleEnvIdP == productModuleEnvId && versionName == "无") {
                            $("#main_version_btn").removeClass("auth-hidden");
                            $("#main_version_tab").removeClass("auth-hidden");
                        }
                        if (productModuleEnvIdP == productModuleEnvId && versionName == "灰度版本") {
                            $("#gray_version_btn").removeClass("auth-hidden");
                            $("#gray_version_tab").removeClass("auth-hidden");
                        }
                        if (productModuleEnvIdP == productModuleEnvId && versionId == 0) {
                            $("#gray_version_btn").removeClass("version-btn-active");
                            $("#main_version_btn").addClass("version-btn-active");
                        }
                        if (productModuleEnvIdP == productModuleEnvId && versionId == 1) {
                            $("#main_version_btn").removeClass("version-btn-active");
                            $("#gray_version_btn").addClass("version-btn-active");
                        }
                        var page = NAV.SETTING_PAGES[key];
                        if (page == html || (HTML.INSTANCE + "?productModuleEnvId=" + productModuleEnvId) == html) {
                            for (var j in auths) {
                                var kj = auths[j];
                                if (kj != "查看") {
                                    var ids = NAV.PAGE_IDS[kj].split(",");
                                    for (var k in ids) {
                                        var id = ids[k];
                                        var flag11 = dataId == "11" && html == HTML.HISTORY_LOG_MANAGE_MAIN;
                                        var flag12 = dataId == "12" && html == HTML.HISTORY_LOG_MANAGE_MAIN;
                                        var flag13 = dataId == "13" && html == HTML.HISTORY_LOG_MANAGE_GRAY;
                                        var flag14 = dataId == "14" && html == HTML.HISTORY_LOG_MANAGE_GRAY;
                                        var flag15 = dataId == "15" && html == HTML.PRODUCT;
                                        var flag16 = dataId == "16" && html == HTML.MODULE_PRODUCT;
                                        var flag17 = dataId == "17" && html == HTML.ENV_MODULE_PRODUCT;
                                        var flag18 = dataId == "18" && html == HTML.AREA;
                                        var flag19 = dataId == "19" && html == HTML.ORGAN_AREA;
                                        var flag20 = dataId == "20" && html == HTML.ROLE;
                                        var flag21 = dataId == "21" && html == HTML.USER;
                                        var flag22 = dataId == "22" && html == HTML.ROLE_AUTH_RES;
                                        if (flag11 || flag12 || flag13 || flag14 || flag15 || flag16 || flag17 || flag18 || flag19 || flag20 || flag21 || flag22) {
                                            $('#' + id).removeClass("auth-hidden");
                                            $('span#' + id).removeClass("auth-hidden");
                                        }
                                        if (id.startWith("gray_")) {
                                            var publicFlag = dataId == "10" && envName == "灰度版本" && id == "gray_properties_add";
                                            var grayAdd = versionName == "灰度版本" && id == "gray_properties_add";
                                            var grayNormal = versionName == "灰度版本" && versionId == 1;
                                            var flag10 = dataId == "10" && html == HTML.PUBLIC_CONFIG && versionId == 1;
                                            if (publicFlag || grayAdd || grayNormal || flag10) {
                                                $('#' + id).removeClass("auth-hidden");
                                                $('span#' + id).removeClass("auth-hidden");
                                            }
                                        } else {
                                            var mainFlag = versionName == "无" && versionId == 0;
                                            var flag9 = dataId == "9" && html == HTML.PUBLIC_CONFIG && versionId == 0;
                                            if (mainFlag || flag9) {
                                                $('#' + id).removeClass("auth-hidden");
                                                $('span#' + id).removeClass("auth-hidden");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                var count = 0;
                var hiddenCount = 0;
                $("#" + tableId).children("tbody").children("tr").each(function () {
                    count++;
                    var trCount = 0;
                    var trHiddenCount = 0;
                    $(this).children("td:last").children("span").each(function () {
                        trCount++;
                        if ($(this).hasClass("auth-hidden") || $(this).hasClass("sys-hidden")) {
                            trHiddenCount++;
                        }
                    });
                    if (trCount != 0 && trCount == trHiddenCount) {
                        hiddenCount++;
                    }
                });
                //获取表格有操作的class和没操作的class
                var classNames = $("#" + tableId).prop("class").split(" ");
                var actionTableClassName = "";
                var noActionTableClassName = "";
                for (var i in classNames) {
                    if (classNames[i].endWith("-table")) {
                        actionTableClassName = classNames[i];
                        noActionTableClassName = actionTableClassName + "-no-action";
                        break;
                    }
                }
                //处理没有操作去掉操作列
                if (count != 0 && count == hiddenCount) {
                    //去除有操作的class
                    $("#" + tableId).removeClass(actionTableClassName);
                    //添加没有操作的class
                    $("#" + tableId).addClass(noActionTableClassName);
                    $("#" + tableId).children("tbody").children("tr").each(function () {
                        //隐藏td的最后一行操作
                        $(this).children("td:last").css("display", "none");
                    });
                    $("#" + tableId).children("thead").children("tr").each(function () {
                        //隐藏th的最后一行操作
                        $(this).children("th:last").css("display", "none");
                    });
                }
            }
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                userAuthResourceAjax.abort();
                window.location.href = "/timeout";
            }
        },
        error: function (xhr, textStatus, thrownError) {
            ajaxError(xhr, textStatus, thrownError);
        }
    });
}

function setLeftResource(productId, resource) {
    var productName = resource.productName;
    var data = resource.data;
    var textHtml1 = '<li class="auth-hidden" id="' + productId + '"><div class="outer-li"><img src="/img/home_gn_resource.png" class="front-img"/><span class="img-span query-text" id="' + productId + '_span">' + productName + '</span><img src="/img/kit_xiala.png" class="back-img"/></div><ul' +
        ' class="ul-no-active list-ul">';
    var textHtml2 = "";
    var textHtml3 = '</ul></li>';
    for (var i = 0; i < data.length; i++) {
        var moduleId = data[i].moduleId;
        var moduleName = data[i].moduleName;
        var envIds = data[i].envIds.split(",");
        var envNames = data[i].envNames.split(",");
        textHtml2 += '<li class="auth-hidden" id="' + productId + "_" + moduleId + '"><div class="middle-li"><span class="img-span query-text" id="' + productId + "_" + moduleId + '_span">' + moduleName + '</span><img src="/img/kit_xiala.png" class="back-img"/></div><ul class="ul-no-active list-ul">';
        for (var j in envNames) {
            var envName = envNames[j];
            var envId = envIds[j];
            textHtml2 += '<li class="auth-hidden" id="' + productId + "_" + moduleId + "_" + envId + '"><span class="inner-li query-text" id="' + productId + "_" + moduleId + "_" + envId + '_span">' + envName + '</span></li>';
        }
        textHtml2 += '</ul></li>';
    }
    $("#left_ul").prepend(textHtml1 + textHtml2 + textHtml3);
}

function hiddenMainTopVirgule() {
    $("#main").load(HTML.MAIN, function (response, status, xhr) {
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
    $("#oblique_1").addClass("sys-hidden");
    $("#oblique_2").addClass("sys-hidden");
    $("#oblique_3").addClass("sys-hidden");
}

function showMainTopVirgule() {
    $("#oblique_1").removeClass("sys-hidden");
    $("#oblique_2").removeClass("sys-hidden");
    $("#oblique_3").removeClass("sys-hidden");
}

function selectAllApply(selectAllId, idPre, datas) {
    $("#" + selectAllId).change(function () {
        var checked = $(this).prop('checked');
        if (checked) {
            for (var i in datas) {
                var id = datas[i].id;
                $("#" + idPre + id + "_apply").prop({checked: true});
            }
        } else {
            for (var i in datas) {
                var id = datas[i].id;
                $("#" + idPre + id + "_apply").prop({checked: false});
            }
        }
    });
}

function selectAll(selectAllId, selectName) {
    var name = "select";
    if (selectAllId.startWith("main")) {
        name = "main-select";
    } else if (selectAllId.startWith("gray")) {
        name = "gray-select";
    }
    if (selectName && selectName != null) {
        name = selectName;
    }
    $("#" + selectAllId).change(function () {
        var checked = $(this).prop('checked');
        if (checked) {
            $('input:checkbox[name=' + name + ']').prop({checked: true});
        } else {
            $('input:checkbox[name=' + name + ']').prop({checked: false});
        }
        if (selectName == "select_line") {
            $('input:checkbox[name=select_line]').blur();
        }
    });
}

function ajaxError(xhr, textStatus, thrownError) {
    if (xhr.status == 400) {
        $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.FORMAT_ERROR, function () {
            location.reload();
        });
    } else if (xhr.status == 403) {
        $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.NO_AUTH);
    } else if (xhr.status == 500) {
        $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.SYSTEM_EXCEPTION);
    } else if (xhr.status == 302) {
        $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.SYSTEM_EXCEPTION);
    } else if (xhr.status == 200) {
        $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
            location.reload();
        });
    } else if (xhr.status == 0) {
        $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.SERVER_NOT_RESPONDING, function () {
            location.reload();
        });
    } else {
        $.MsgBox.Alert(MESSAGES.NEWS, xhr.status + MESSAGES.SYSTEM_ERROR, function () {
            location.reload();
        });
    }
}

function ajaxTimeout(status, ajax) {
    if (status == 'timeout') {
        ajax.abort();
        window.location.href = "/timeout";
    }
}

function getInstanceTypeId(versionId) {
    var tabId = "";
    if (versionId == 0) {
        $("#tab_instance").children("li.tab-li-active").each(function (i, el) {
            tabId = $(el).attr("id");
        });
    } else {
        $("#gray_tab_instance").children("li.tab-li-active").each(function (i, el) {
            tabId = $(el).attr("id");
        });
    }
    var instanceTypeId
    if (tabId == "gray_properties_tab" || tabId == "properties_tab") {
        instanceTypeId = 0;
    } else if (tabId == "gray_json_tab" || tabId == "json_tab") {
        instanceTypeId = 1;
    } else if (tabId == "gray_xml_tab" || tabId == "xml_tab") {
        instanceTypeId = 2;
    } else {
        instanceTypeId = 3;
    }
    return instanceTypeId;
}

function removeItemValue() {
    removeItem("json_value");
    removeItem("json_app");
    removeItem("json_product_module_table");
    removeItem("xml_value");
    removeItem("xml_app");
    removeItem("xml_product_module_table");
    removeItem("yml_value");
    removeItem("yml_app");
    removeItem("yml_product_module_table");
    removeItem("gray_json_value");
    removeItem("gray_json_app");
    removeItem("gray_json_product_module_table");
    removeItem("gray_xml_value");
    removeItem("gray_xml_app");
    removeItem("gray_xml_product_module_table");
    removeItem("gray_yml_value");
    removeItem("gray_yml_app");
    removeItem("gray_yml_product_module_table");
}

function instanceNumSelect(sData, nTd) {
    var apps = sData.split(",");
    var length = apps.length;
    if (sData.length < 1) {
        length = 0;
    }
    //设置select可以下拉只读，但不可以修改
    var selectText = '<select onfocus="this.defaultIndex=this.selectedIndex;" onchange="this.selectedIndex=this.defaultIndex;" style="width: 100%;">';
    selectText += '<option style="display: none">' + length + '</option>';
    if (sData.length > 0) {
        for (var i in apps) {
            selectText += '<option>' + apps[i] + '</option>';
        }
    }
    selectText += '</select>';
    $(nTd).html(selectText);
}

function empty(id) {
    $("#" + id).empty();
}

function append(id, text) {
    if (text) {
        $("#" + id).append(text);
    } else {
        $("#" + id).append(MESSAGES.NOT_SELECTED);
    }
}

function updatePasswordPage() {
    $("#update_password").click(function () {
        var getAjax = $.ajax({
            url: "/password_update",
            type: "GET",
            timeout: 30000,
            async: false,
            data: {},
            success: function (response) {
                if (typeof(response) == "string") {
                    if (response.indexOf(MESSAGES.CONFIRM_NEW_PASSWORD) != -1) {
                        window.location.href = "/password_update";
                    } else if (response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                        $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                            location.reload();
                        });
                    }
                }
            },
            complete: function (XMLHttpRequest, status) {
                if (status == 'timeout') {
                    getAjax.abort();
                    window.location.href = "/timeout";
                }
            },
            error: function (xhr, textStatus, thrownError) {
                ajaxError(xhr, textStatus, thrownError);
            }
        });
    });
}

function getMaxClassIdAndSetClassDisplay() {
    var getMaxClassIdAjax = $.ajax({
        url: "/user/get_max_classId_by_username",
        type: "POST",
        timeout: 30000,
        async: false,
        data: {},
        success: function (response) {
            if (typeof(response) == "string" && response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                    location.reload();
                });
            } else {
                if (response.code == 200) {
                    setDisplayClass(response.info);
                } else {
                    $.MsgBox.Alert(MESSAGES.NEWS, response.message);
                }
            }
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                getMaxClassIdAjax.abort();
                window.location.href = "/timeout";
            }
        },
        error: function (xhr, textStatus, thrownError) {
            ajaxError(xhr, textStatus, thrownError);
        }
    });
}

function getClassId() {
    var classId = 0;
    $("input:radio[name=class]").each(function () {
        var checked = $(this).prop('checked');
        var id = $(this).attr("id");
        if (checked) {
            if (id == "confidential") {
                classId = 2;
            } else if (id == "secrecy") {
                classId = 1;
            } else if (id == "normal") {
                classId = 0;
            }
        }
    });
    return classId;
}

function getMustChange() {
    var mustChangeId = 0;
    $("input:radio[name=change]").each(function () {
        var checked = $(this).prop('checked');
        var id = $(this).attr("id");
        if (checked) {
            if (id == "must_change") {
                mustChangeId = 1;
            } else if (id == "not_must_change") {
                mustChangeId = 0;
            }
        }
    });
    return mustChangeId;
}

function setClassDisplayByGetClassIdByItemId() {
    var itemId = $("#item_id").val();
    var getMaxClassIdAjax = $.ajax({
        url: "/item/get_classId_by_id",
        type: "POST",
        timeout: 30000,
        async: false,
        data: {"id": itemId},
        success: function (response) {
            if (typeof(response) == "string" && response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                    location.reload();
                });
            } else {
                if (response.code == 200) {
                    setDisplayClass(response.info);
                } else {
                    $.MsgBox.Alert(MESSAGES.NEWS, response.message);
                }
            }
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                getMaxClassIdAjax.abort();
                window.location.href = "/timeout";
            }
        },
        error: function (xhr, textStatus, thrownError) {
            ajaxError(xhr, textStatus, thrownError);
        }
    });
}

function setDisplayClass(classId) {
    var lastHtml = getItem("lastHtml");
    if (lastHtml.indexOf("update") != -1) {
        var configClassId = getItem("class_id");
        if (configClassId == "1") {
            $("#secrecy").prop({checked: true});
        } else if (configClassId == "2") {
            $("#confidential").prop({checked: true});
        } else {
            $("#normal").prop({checked: true});
        }
    }
    if (classId == "0") {
        $("#secrecy_div").css("display", "none");
        $("#confidential_div").css("display", "none");
    } else if (classId == "1") {
        $("#confidential_div").css("display", "none");
    }
}

function viewAllNotAllowed(filter) {
    var select = $("#" + filter + " > #view_all");
    select.css({"cursor": "not-allowed", "background": "#D7D7D7", "box-shadow": "0 0.1em 0.1em 0 #D7D7D7"});
}

function viewAllAllowed(filter) {
    var select = $("#" + filter + " > #view_all");
    select.css({"cursor": "pointer", "background": "#5686E8", "box-shadow": "0 0.1em 0.1em 0 #5686E8"});
}

function setViewStatus(tableId) {
    var total = $('#' + tableId).DataTable().settings()[0]._iRecordsTotal;
    if (total > 0) {
        viewAllAllowed(tableId + "_filter");
    } else {
        viewAllNotAllowed(tableId + "_filter");
    }
}

function setMustChange() {
    var mustChange = getItem("must_change");
    if (mustChange == "1") {
        $("#must_change").prop({checked: true});
    }
}