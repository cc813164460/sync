/**
 * Created by maodi on 2018/6/5.
 */
function addOrganArea() {
    addAction(getOrganAreas, validOrganAreaData, "/organ_area/insert", HTML.ORGAN_AREA, false, "organ_info");
}

function updateOrganArea() {
    updateAction(getOrganAreas, validOrganAreaData, "/organ_area/update", HTML.ORGAN_AREA, false, "organ_info");
}

function validOrganAreaData() {
    var organName = $("#organ_name").val();
    var flag = true;
    if (!validValueShowInfo(organName, 50, "organ_info")) {
        flag = false;
    }
    empty("area_info");
    if (getSelectedIdArray("area").length < 1) {
        append("area_info");
        flag = false;
    }
    return flag;
}

function getOrganAreas() {
    var organAreas = {};
    organAreas.organName = $("#organ_name").val();
    organAreas.areaIds = getSelectedIdArray("area");
    return organAreas;
}

function loadUpdateOrganAreaVal() {
    $("#id").text(getItem("id"));
    $("#organ_name").val(getItem("organ_name"));
}