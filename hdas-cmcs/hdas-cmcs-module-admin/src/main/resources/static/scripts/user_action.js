/**
 * Created by maodi on 2018/6/5.
 */
function addUser() {
    addAction(getUser, validUserData, "/user/insert", HTML.USER, false, "username_info", "mobile_info", "email_info");
}

function updateUser() {
    updateAction(getUser, validUserData, "/user/update", HTML.USER, false, "username_info", "mobile_info", "email_info");
}

function validUserData() {
    var nickname = $("#nickname").val();
    var username = $("#username").val();
    var area = getSelectVal("area");
    var organ = getSelectVal("organ");
    var role = getSelectVal("role");
    var mobile = $("#mobile").val();
    var email = $("#email").val();
    var password = $("#password").val();
    var flag = true;
    if (!validValueShowInfo(nickname, 20, "nickname_info")) {
        flag = false;
    }
    if (!validValueShowInfo(username, 20, "username_info")) {
        flag = false;
    }
    if (!validPasswordShowInfo(password, "password_info")) {
        flag = false;
    }
    empty("mobile_info");
    if (!validMobile(mobile, MESSAGES.MOBILE)) {
        if (!mobile || mobile.isBlank()) {
            append("mobile_info", MESSAGES.INPUT_IS_NOT_NULL);
        } else {
            append("mobile_info", "电话格式有误");
        }
        flag = false;
    }
    empty("email_info");
    if (!validEmail(email, MESSAGES.EMAIL)) {
        if (!mobile || mobile.isBlank()) {
            append("email_info", MESSAGES.INPUT_IS_NOT_NULL);
        } else {
            append("email_info", "邮箱格式有误");
        }
        flag = false;
    }
    empty("area_info");
    if (!validIsSelect(area, MESSAGES.AREA)) {
        append("area_info");
        flag = false;
    }
    empty("organ_info");
    if (!validIsSelect(organ, MESSAGES.ORGAN)) {
        append("organ_info");
        flag = false;
    }
    empty("role_info");
    if (!validIsSelect(role, MESSAGES.ROLE)) {
        append("role_info");
        flag = false;
    }
    return flag;
}

function getUser() {
    var user = {};
    user.nickname = $("#nickname").val();
    user.username = $("#username").val();
    user.areaId = getSelectVal("area");
    user.organId = getSelectVal("organ");
    user.roleIds = getSelectedIdArray("role");
    user.mobile = $("#mobile").val();
    user.email = $("#email").val();
    user.password = $("#password").val();
    return user;
}

function loadUpdateUserVal() {
    $("#id").text(getItem("id"));
    $("#nickname").val(getItem("nickname"));
    $("#username").val(getItem("username"));
    $("#mobile").val(getItem("mobile"));
    $("#email").val(getItem("email"));
    $("#password").val(getItem("password"));
}