/**
 * Created by maodi on 2018/6/5.
 */
function addModuleProduct() {
    addAction(getModuleProduct, validModuleProductData, "/module_product/insert", HTML.MODULE_PRODUCT, false, "module_info");
}

function updateModuleProduct() {
    updateAction(getModuleProduct, validModuleProductData, "/module_product/update", HTML.MODULE_PRODUCT, false, "module_info");
}

function validModuleProductData() {
    var moduleName = $("#module_name").val();
    var comment = $("#description").val();
    var flag = true;
    if (!validValueShowInfo(moduleName, 50, "module_info")) {
        flag = false;
    }
    empty("product_info");
    if (getSelectedIdArray("product").length < 1) {
        append("product_info");
        flag = false;
    }
    if (!validValueShowInfo(comment, 500, "comment_info", true)) {
        flag = false;
    }
    return flag;
}

function getModuleProduct() {
    var modules = {};
    modules.productIds = getSelectedIdArray("product");
    modules.name = $("#module_name").val();
    modules.description = $("#description").val();
    return modules;
}

function loadUpdateModuleProductVal() {
    $("#id").text(getItem("id"));
    $("#description").val(getItem("description"));
    $("#module_name").val(getItem("module_name"));
}