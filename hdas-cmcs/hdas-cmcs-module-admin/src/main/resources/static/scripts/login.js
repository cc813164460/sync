/**
 * Created by maodi on 2018/5/28.
 */
var infoId = "info";
var usernameInfoId = "username_info";
var passwordInfoId = "password_info";

function appendLoginInfo(id, loginInfo) {
    if (loginInfo != null) {
        if (loginInfo != "") {
            $("#" + id).append(loginInfo);
        }
    }
}

function emptyLoginInfo() {
    $("#" + infoId).empty();
    $("#" + usernameInfoId).empty();
    $("#" + passwordInfoId).empty();
}

function allLogin() {
    clickLogin();
    keyupLogin();
}

function clickLogin() {
    $("#login").click(function () {
        login();
    })
}

function keyupLogin() {
    $("#username").bind('keyup', function (e) {
        if (e.keyCode == 13) {
            login();
        }
    });
    $("#password").bind('keyup', function (e) {
        if (e.keyCode == 13) {
            login();
        }
    });
}

function login() {
    emptyLoginInfo();
    var username = $("#username").val();
    var password = $("#password").val();
    var validCount = 0;
    if (username.isBlank()) {
        appendLoginInfo(usernameInfoId, MESSAGES.INPUT_IS_NOT_NULL);
    } else {
        validCount++;
    }
    if (password.isBlank()) {
        appendLoginInfo(passwordInfoId, MESSAGES.INPUT_IS_NOT_NULL);
    } else {
        validCount++;
    }
    if (2 > validCount) {
        return;
    }
    var ajaxTimeout = $.ajax({
        url: "/login",
        type: "POST",
        timeout: 30000,
        async: false,
        data: {
            "username": username,
            "password": password,
        },
        success: function (response) {
            window.sessionStorage.clear();
            window.location.href = "/";
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                ajaxTimeout.abort();
                window.location.href = "/timeout";
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status = 401) {
                if (xhr.responseJSON.message.indexOf("账号") != -1) {
                    appendLoginInfo(usernameInfoId, xhr.responseJSON.message);
                } else {
                    appendLoginInfo(passwordInfoId, xhr.responseJSON.message);
                }
            } else if (xhr.status == 403) {
                appendLoginInfo(infoId, MESSAGES.NO_AUTH);
            } else {
                appendLoginInfo(infoId, xhr.status + MESSAGES.SYSTEM_EXCEPTION);
            }
        }
    });
}