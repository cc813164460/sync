/**
 * Created by maodi on 2018/5/29.
 */
function loadLoginInfo() {
    var loadLoginInfoAjax = $.ajax({
        url: "/get_login_info",
        type: "POST",
        timeout: 30000,
        async: false,
        success: function (response) {
            if (typeof(response) == "string" && response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                    location.reload();
                });
            } else {
                var user = response.message;
                $("#left_account").append(user.user_name);
                $("#left_account").attr("title", user.user_name);
                $("#left_role").append(user.name);
                $("#left_role").attr("title", user.name);
                $("#left_email").append(user.email);
                $("#left_email").attr("title", user.email);
                $("#left_lastDateTime").append(user.last_date_time);
                $("#left_lastDateTime").attr("title", user.last_date_time);
            }
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                loadLoginInfoAjax.abort();
                window.location.href = "/timeout";
            }
        },
        error: function (xhr, textStatus, thrownError) {
            ajaxError(xhr, textStatus, thrownError);
        }
    });
}

function clickOuterLi() {
    $(".outer-li").click(function () {
        var spanId = $(this).children("span").attr("id");
        if (spanId == "public_config" || spanId == "history_config") {
            $(this).parent("li").css("background", "#1D2127").addClass("a-active");
            var spanText = $(this).children("span").text();
            if (spanText.indexOf(NAV.PUBLIC_CONFIG) != -1) {
                removeItem("productModuleEnvId");
                removeItem("versionId");
                $("#history_config_li").removeClass("a-active");
                $("#main").load(HTML.PUBLIC_CONFIG, function (response, status, xhr) {
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
            } else if (spanText.indexOf(NAV.HISTORY_MANAGE) != -1) {
                $("#public_config_li").removeClass("a-active");
                $("#main").load(HTML.HISTORY_LOG_MANAGE_MAIN, function (response, status, xhr) {
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
            }
        } else {
            if ($(this).find(".back-img").attr("src") == "/img/kit_xiala.png") {
                $(this).find(".back-img").attr("src", "/img/kit_xiala_zhankai.png");
                $(this).parent("li").css("background", "#1D2127");
                $(this).next("ul").addClass("ul-active");
                $(this).next('ul').slideDown(100).children('li');
            } else {
                $(this).find(".back-img").attr("src", "/img/kit_xiala.png");
                $(this).parent("li").css("background", "#282D34");
                $(this).next('ul').slideUp(100);
                $(this).next("ul").removeClass("ul-active");
            }
        }
        if ($(this).next("ul").length == 0) {
            $("body").find(".inner-li").removeClass("a-active");
        }
    });
}

function clickMiddleLi() {
    $(".middle-li").click(function () {
        if ($(this).find(".back-img").attr("src") == "/img/kit_xiala.png") {
            $(this).find(".back-img").attr("src", "/img/kit_xiala_zhankai.png");
            $(this).parent("li").css("background", "#1D2127");
            $(this).next("ul").addClass("ul-active");
            $(this).next('ul').slideDown(100).children('li');
        } else {
            $(this).find(".back-img").attr("src", "/img/kit_xiala.png");
            $(this).parent("li").css("background", "#282D34");
            $(this).next('ul').slideUp(100);
            $(this).next("ul").removeClass("ul-active");
        }
    });
}

function clickInnerLi() {
    $(".inner-li").click(function () {
        if ($(this).attr("class") != "a-active") {
            $("body").find(".inner-li").removeClass("a-active");
            $("#public_config_li").removeClass("a-active");
            $("#history_config_li").removeClass("a-active");
            $(this).addClass("a-active");
        }
        var text = $(this).text();
        removeItem("versionId");
        removeItem("productModuleEnvId");
        if (text.indexOf(NAV.PRODUCT_MANAGE) != -1) {
            $("#main").load(HTML.PRODUCT, function (response, status, xhr) {
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
        } else if (text.indexOf(NAV.MODULE_MANAGE) != -1) {
            $("#main").load(HTML.MODULE_PRODUCT, function (response, status, xhr) {
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
        } else if (text.indexOf(NAV.ENV_MANAGE) != -1) {
            $("#main").load(HTML.ENV_MODULE_PRODUCT, function (response, status, xhr) {
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
        } else if (text.indexOf(NAV.AREA_MANAGE) != -1) {
            $("#main").load(HTML.AREA, function (response, status, xhr) {
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
        } else if (text.indexOf(NAV.ORGAN_MANAGE) != -1) {
            $("#main").load(HTML.ORGAN_AREA, function (response, status, xhr) {
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
        } else if (text.indexOf(NAV.USER_MANAGE) != -1) {
            $("#main").load(HTML.USER, function (response, status, xhr) {
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
        } else if (text.indexOf(NAV.ROLE_MANAGE) != -1) {
            $("#main").load(HTML.ROLE, function (response, status, xhr) {
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
        } else if (text.indexOf(NAV.AUTH_MANAGE) != -1) {
            $("#main").load(HTML.ROLE_AUTH_RES, function (response, status, xhr) {
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
        } /*else if (text.indexOf(NAV.MAIN_VERSION) != -1) {
         $("#main").load(HTML.HISTORY_LOG_MANAGE_MAIN, function (response, status, xhr) {
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
         } else if (text.indexOf(NAV.GRAY_VERSION) != -1) {
         $("#main").load(HTML.HISTORY_LOG_MANAGE_GRAY, function (response, status, xhr) {
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
         }*/ else {
            var productModuleEnvId = $(this).parent("li").attr("id");
            var productName = $(this).parent("li").parent("ul").parent("li").parent("ul").parent("li").children("div").children("span").text();
            var moduleName = $(this).parent("li").parent("ul").parent("li").children("div").children("span").text();
            var envName = $(this).text();
            $('#main').load("/instance/page?productModuleEnvId=" + productModuleEnvId, function (response, status, xhr) {
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
            setItem("productModuleEnvId", productModuleEnvId);
            loadItem(productName, moduleName, envName, NAV.LIST_PAGE, HTML.INSTANCE + "?productModuleEnvId=" + productModuleEnvId);
        }
        showMainTopVirgule();
    });
}

function setLeftLi(outerText, innerText) {
    if (outerText == NAV.PUBLIC_CONFIG) {
        var select = "#public_config_li > .outer-li";
        $(select).parent("li").css("background", "#1D2127").addClass("a-active");
        $("body").find(".inner-li").removeClass("a-active");
    } else if (outerText == NAV.HISTORY_MANAGE) {
        var select = "#history_config_li > .outer-li";
        $(select).parent("li").css("background", "#1D2127").addClass("a-active");
        $("body").find(".inner-li").removeClass("a-active");
    } else {
        var outerSpan = $('.img-span').filter(function () {
            return $(this).text() == outerText;
        });
        outerSpan.next().attr("src", "/img/kit_xiala_zhankai.png");
        outerSpan.parent("li").css("background", "#1D2127");
        var outerSpanParent = outerSpan.parent("div");
        outerSpanParent.next('ul').addClass('ul-active');
        outerSpanParent.next('ul').slideDown(100).children('li');
    }
    if (innerText) {
        $('.inner-li').filter(function () {
            return $(this).text() == innerText;
        }).addClass("a-active");
    }
    showMainTopVirgule();
}

function setInstanceLeftLi(productId, moduleId, envId) {
    var outerSpan = $(".img-span#" + productId + "_span");
    outerSpan.next().attr("src", "/img/kit_xiala_zhankai.png");
    outerSpan.parent("li").css("background", "#1D2127");
    var outerSpanParent = outerSpan.parent("div");
    outerSpanParent.next("ul").addClass("ul-active");
    outerSpanParent.next('ul').slideDown(100).children('li');
    var middleSpan = $(".img-span#" + productId + "_" + moduleId + "_span");
    middleSpan.next().attr("src", "/img/kit_xiala_zhankai.png");
    middleSpan.parent("li").css("background", "#1D2127");
    var middleSpanParent = middleSpan.parent("div");
    middleSpanParent.next("ul").addClass("ul-active");
    middleSpanParent.next('ul').slideDown(100).children('li');
    $(".inner-li#" + productId + "_" + moduleId + "_" + envId + "_span").addClass("a-active");
    showMainTopVirgule();
}

/**
 * 加载左边栏之后,加载第一个页面
 */
function judgeLastHtml() {
    $("#main").css("height", "");
    var lastHtml = getItem("lastHtml");
    if (lastHtml == null) {
        lastHtml = getFirstHtml();
    }
    loadAndSetLastHtml(lastHtml);
}

/**
 * 加载设置加载的网页
 * @param lastHtml 上次的网页
 */
function loadAndSetLastHtml(lastHtml) {
    $("#main").load(lastHtml, function (response, status, xhr) {
        if (typeof(response) == "string" && response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
            $("#main").empty();
            $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                location.reload();
            });
        } else {
            if (xhr.status == 403) {
                lastHtml = getFirstHtml();
                loadAndSetLastHtml(lastHtml);
            } else if (xhr.status != 200) {
                ajaxError(xhr, status, response);
            }
        }
    });
    if (lastHtml) {
        if (lastHtml == HTML.PRODUCT || lastHtml == HTML.PRODUCT_ADD || lastHtml == HTML.PRODUCT_UPDATE) {
            setLeftLi(NAV.SETTING, NAV.PRODUCT_MANAGE);
        } else if (lastHtml == HTML.MODULE_PRODUCT || lastHtml == HTML.MODULE_PRODUCT_ADD || lastHtml == HTML.MODULE_PRODUCT_UPDATE) {
            setLeftLi(NAV.SETTING, NAV.MODULE_MANAGE);
        } else if (lastHtml == HTML.ENV_MODULE_PRODUCT || lastHtml == HTML.ENV_MODULE_PRODUCT_ADD || lastHtml == HTML.ENV_MODULE_PRODUCT_UPDATE) {
            setLeftLi(NAV.SETTING, NAV.ENV_MANAGE);
        } else if (lastHtml == HTML.AREA || lastHtml == HTML.AREA_ADD || lastHtml == HTML.AREA_UPDATE) {
            setLeftLi(NAV.SETTING, NAV.AREA_MANAGE);
        } else if (lastHtml == HTML.ORGAN_AREA || lastHtml == HTML.ORGAN_AREA_ADD || lastHtml == HTML.ORGAN_AREA_UPDATE) {
            setLeftLi(NAV.SETTING, NAV.ORGAN_MANAGE);
        } else if (lastHtml == HTML.USER || lastHtml == HTML.USER_ADD || lastHtml == HTML.USER_UPDATE) {
            setLeftLi(NAV.SETTING, NAV.USER_MANAGE);
        } else if (lastHtml == HTML.ROLE || lastHtml == HTML.ROLE_ADD || lastHtml == HTML.ROLE_UPDATE) {
            setLeftLi(NAV.SETTING, NAV.ROLE_MANAGE);
        } else if (lastHtml == HTML.ROLE_AUTH_RES || lastHtml == HTML.ROLE_AUTH_RES_ADD || lastHtml == HTML.ROLE_AUTH_RES_UPDATE) {
            setLeftLi(NAV.SETTING, NAV.AUTH_MANAGE);
        } else if (lastHtml == HTML.PUBLIC_CONFIG || lastHtml == HTML.PUBLIC_CONFIG_ADD || lastHtml == HTML.PUBLIC_CONFIG_UPDATE || lastHtml == HTML.PUBLIC_CONFIG_GRAY_ADD || lastHtml == HTML.PUBLIC_CONFIG_GRAY_UPDATE || lastHtml == HTML.PUBLIC_CONFIG_COPY_CONFIG || lastHtml == HTML.PUBLIC_CONFIG_GRAY_COPY_CONFIG) {
            setLeftLi(NAV.PUBLIC_CONFIG);
        } else if (lastHtml == HTML.HISTORY_LOG_MANAGE_MAIN) {
            setLeftLi(NAV.HISTORY_MANAGE);
        } /*else if (lastHtml == HTML.HISTORY_LOG_MANAGE_MAIN) {
         setLeftLi(NAV.HISTORY_MANAGE, NAV.MAIN_VERSION);
         } else if (lastHtml == HTML.HISTORY_LOG_MANAGE_GRAY) {
         setLeftLi(NAV.HISTORY_MANAGE, NAV.GRAY_VERSION);
         } */ else {
            var productName = getItem("left_product_name");
            var moduleName = getItem("left_module_name");
            var envName = getItem("left_env_name");
            loadItem(productName, moduleName, envName, NAV.LIST_PAGE, lastHtml);
        }
    }
}

function leftQueryAction() {
    var val = $('#left_query_input').val();
    $('.query-text').each(function () {
        var text = $(this).text();
        if (text.indexOf(val) == -1) {
            $(this).parent("div").parent("li").addClass("query-hidden");
            $(this).parent("div").parent("li").parent("ul").parent("li").addClass("query-hidden");
            $(this).parent("li").addClass("query-hidden");
            $(this).parent("li").parent("ul").parent("li").addClass("query-hidden");
            $(this).parent("li").parent("ul").parent("li").parent("ul").parent("li").addClass("query-hidden");
        }
    });
    $('.query-text').each(function () {
        var text = $(this).text();
        if (text.indexOf(val) != -1) {
            //去除父节点li的隐藏
            $(this).parent("div").parent("li").removeClass("query-hidden");
            $(this).parent("div").parent("li").parent("ul").parent("li").removeClass("query-hidden");
            $(this).parent("li").removeClass("query-hidden");
            $(this).parent("li").parent("ul").parent("li").removeClass("query-hidden");
            $(this).parent("li").parent("ul").parent("li").parent("ul").parent("li").removeClass("query-hidden");
            //去除子节点li的隐藏
            $(this).parent("div").next("ul").children("li").removeClass("query-hidden");
            $(this).parent("div").next("ul").children("li").children("ul").children("li").removeClass("query-hidden");
        }
    });
}

function leftQuery() {
    $("#left_query_input").on('keypress', function (event) {
        if (event.keyCode == 13) {
            leftQueryAction();
        }
    });
    $("#left_query").click(function () {
        leftQueryAction();
    });
}

function leftCancel() {
    $("#left_cancel").click(function () {
        $('#left_query_input').val("");
        $('#left_ul').find(".query-hidden").removeClass("query-hidden");
    });
}

function getFirstHtml() {
    var firstHtml = null;
    $("#left_ul").children("li").each(function () {
        if (firstHtml) {
            return firstHtml;
        }
        if (!$(this).hasClass("auth-hidden")) {
            var oneId = $(this).attr("id");
            if (oneId == "public_config_li") {
                firstHtml = HTML.PUBLIC_CONFIG;
            } else if (oneId == "history_config_li") {
                firstHtml = HTML.HISTORY_LOG_MANAGE_MAIN;
            } else {
                var productName = $(this).children("div").children("span").text();
                $(this).children("ul").children("li").each(function () {
                    if (firstHtml) {
                        return firstHtml;
                    }
                    if (!$(this).hasClass("auth-hidden")) {
                        var id = $(this).attr("id");
                        if (isNumber(id.substring(0, 16))) {
                            var moduleName = $(this).children("div").children("span").text();
                            $(this).children("ul").children("li").each(function () {
                                if (firstHtml) {
                                    return firstHtml;
                                }
                                if (!$(this).hasClass("auth-hidden")) {
                                    var envName = $(this).children("span").text();
                                    setItem("left_product_name", productName);
                                    setItem("left_module_name", moduleName);
                                    setItem("left_env_name", envName);
                                    var productModuleEnvId = $(this).attr("id");
                                    firstHtml = "/instance/page?productModuleEnvId=" + productModuleEnvId;
                                }
                            });
                        } else {
                            switch (id) {
                                /*case "history_main":
                                 firstHtml = HTML.HISTORY_LOG_MANAGE_MAIN;
                                 break;
                                 case "history_gray":
                                 firstHtml = HTML.HISTORY_LOG_MANAGE_GRAY;
                                 break;*/
                                case "area_manage":
                                    firstHtml = HTML.AREA;
                                    break;
                                case "organ_manage":
                                    firstHtml = HTML.ORGAN_AREA;
                                    break;
                                case "role_manage":
                                    firstHtml = HTML.ROLE;
                                    break;
                                case "user_manage":
                                    firstHtml = HTML.USER;
                                    break;
                                case "product_manage":
                                    firstHtml = HTML.PRODUCT;
                                    break;
                                case "module_manage":
                                    firstHtml = HTML.MODULE_PRODUCT;
                                    break;
                                case "env_manage":
                                    firstHtml = HTML.ENV_MODULE_PRODUCT;
                                    break;
                                case "auth_manage":
                                    firstHtml = HTML.ROLE_AUTH_RES;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                });
            }
        }
    });
    return firstHtml;
}