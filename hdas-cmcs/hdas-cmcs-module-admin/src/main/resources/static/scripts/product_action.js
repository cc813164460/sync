/**
 * Created by maodi on 2018/6/5.
 */
function addProduct() {
    addAction(getProducts, validProductData, "/product/insert", HTML.PRODUCT, false, "product_info");
}

function updateProduct() {
    updateAction(getProducts, validProductData, "/product/update", HTML.PRODUCT, false, "product_info");
}

function validProductData() {
    var productName = $("#product_name").val();
    var area = getSelectVal("area");
    var organ = getSelectVal("organ");
    var user = getSelectVal("user");
    var flag = true;
    if (!validValueShowInfo(productName, 20, "product_info")) {
        flag = false;
    }
    empty("area_info");
    if (!validIsSelect(area)) {
        append("area_info");
        flag = false;
    }
    empty("organ_info");
    if (!validIsSelect(organ)) {
        append("organ_info");
        flag = false;
    }
    empty("user_info");
    if (!validIsSelect(user)) {
        append("user_info");
        flag = false;
    }
    return flag;
}

function getProducts() {
    var product = {};
    product.name = $("#product_name").val();
    product.areaId = getSelectVal("area");
    product.organId = getSelectVal("organ");
    product.userId = getSelectVal("user");
    return product;
}

function loadUpdateProductVal() {
    $("#id").text(getItem("id"));
    $("#product_name").val(getItem("product_name"));
}