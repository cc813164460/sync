/**
 * Created by maodi on 2018/8/14.
 */
function updatePassword() {
    $("#save").click(function () {
        if (!validPasswordUpdate()) {
            return;
        }
        var password = $("#new_password").val();
        var ajax = $.ajax({
            url: "/user/update_password",
            type: "POST",
            timeout: 30000,
            async: false,
            data: {"password": password},
            success: function (response) {
                if (typeof(response) == "string" && response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                    $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                        location.reload();
                    });
                } else {
                    $.MsgBox.Alert(MESSAGES.NEWS, response.message, function () {
                        window.location.href = "/logout";
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
    });
    $("#cancel").click(function () {
        window.location.href = "/"
    });
}

function validOldPassword() {
    var password = $("#old_password").val();
    var flag = true;
    if (!validValueShowInfo(password, 20, "old_password_info")) {
        return false;
    }
    var ajax = $.ajax({
        url: "/user/get_password_by_username",
        type: "POST",
        timeout: 30000,
        async: false,
        data: {"password": password},
        success: function (response) {
            if (typeof(response) == "string" && response.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                    location.reload();
                });
            } else {
                $("#old_password_info").empty();
                if (response != "1") {
                    $("#old_password_info").append(MESSAGES.INPUT_CORRECT_PASSWORD);
                    flag = false;
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
    return flag;
}

function validPasswordUpdate() {
    var newPassword = $("#new_password").val();
    var confirmNewPassword = $("#confirm_new_password").val();
    var flag = true;
    if (!validOldPassword()) {
        flag = false;
    }
    if (!validPasswordShowInfo(newPassword, "new_password_info")) {
        flag = false;
    }
    if (!validPasswordShowInfo(confirmNewPassword, "confirm_new_password_info")) {
        flag = false;
    } else {
        $("#confirm_new_password_info").empty();
        if (newPassword != confirmNewPassword) {
            $("#confirm_new_password_info").append(MESSAGES.NEW_PASSWORD_AND_CONFIRM_NEW_PASSWORD_SAME);
            flag = false;
        }
    }
    return flag;
}