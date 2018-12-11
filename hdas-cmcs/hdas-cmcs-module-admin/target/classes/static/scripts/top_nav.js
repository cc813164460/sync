/**
 * Created by maodi on 2018/5/30.
 */
function loadItem(text1, text2, text3, text4, html) {
    emptyLiValue();
    setLiValue(text1, text2, text3, text4);
    setItem("lastHtml", html);
    setItem("left_product_name", text1);
    setItem("left_module_name", text2);
    setItem("left_env_name", text3);
}

function setLiValue(val1, val2, val3, val4) {
    $("#oblique_1").removeClass("sys-hidden");
    $("#oblique_2").removeClass("sys-hidden");
    $("#oblique_3").removeClass("sys-hidden");
    $("#li_1").append(val1);
    if (val2 != null) {
        $("#li_2").append(val2);
    } else {
        $("#oblique_2").addClass("sys-hidden");
    }
    $("#li_3").append(val3);
    if (val4 != null) {
        $("#li_4").append(val4);
    } else {
        $("#oblique_3").addClass("sys-hidden");
    }
}

function emptyLiValue() {
    $("#li_1").empty();
    $("#li_2").empty();
    $("#li_3").empty();
    $("#li_4").empty();
}