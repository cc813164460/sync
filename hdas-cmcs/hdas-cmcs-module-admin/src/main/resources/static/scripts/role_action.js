/**
 * Created by maodi on 2018/6/5.
 */
function addRole() {
    addAction(getRole, validRoleData, "/role/insert", HTML.ROLE, false, "role_info");
}

function updateRole() {
    updateAction(getRole, validRoleData, "/role/update", HTML.ROLE, false, "role_info");
}

function validRoleData() {
    var roleName = $("#role_name").val();
    var flag = true;
    if (!validValueShowInfo(roleName, 50, "role_info")) {
        flag = false
    }
    return flag;
}

function getRole() {
    var role = {};
    role.name = $("#role_name").val();
    role.description = $("#description").val();
    return role;
}

function loadUpdateRoleVal() {
    $("#id").text(getItem("id"));
    $("#role_name").val(getItem("name"));
    $("#description").val(getItem("description"));
}