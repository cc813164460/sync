/**
 * Created by maodi on 2018/6/5.
 */
function addArea() {
    addAction(getArea, validAreaData, "/area/insert", HTML.AREA, false, "area_info");
}

function updateArea() {
    updateAction(getArea, validAreaData, "/area/update", HTML.AREA, false, "area_info");
}

function validAreaData() {
    var areaName = $("#area_name").val();
    var flag = true;
    if (!validValueShowInfo(areaName, 50, "area_info")) {
        flag = false;
    }
    return flag;
}

function getArea() {
    var area = {};
    area.name = $("#area_name").val();
    return area;
}

function loadUpdateAreaVal() {
    $("#id").text(getItem("id"));
    $("#area_name").val(getItem("area_name"));
}