/**
 * Created by maodi on 2018/6/5.
 */
function addEnvModuleProduct() {
    addAction(getEnvModuleProduct, validEnvModuleProductData, "/env_module_product/insert", HTML.ENV_MODULE_PRODUCT, true, "env_info");
}

function updateEnvModuleProduct() {
    updateAction(getEnvModuleProduct, validEnvModuleProductData, "/env_module_product/update", HTML.ENV_MODULE_PRODUCT, true, "env_info");
}

function validEnvModuleProductData() {
    var envName = $("#env_name").val();
    var comment = $("#description").val();
    var flag = true;
    if (!validValueShowInfo(envName, 50, "env_info")) {
        flag = false;
    }
    empty("product_module_info");
    if (!validSelectedProductAndModule()) {
        append("product_module_info");
        flag = false;
    }
    if (!validValueShowInfo(comment, 500, "comment_info", true)) {
        flag = false;
    }
    return flag;
}

function getEnvModuleProduct() {
    var envModulesProducts = new Object();
    envModulesProducts.productModuleJson = getProductModuleJson();
    envModulesProducts.name = $("#env_name").val();
    envModulesProducts.description = $("#description").val();
    var id = $("#id").text();
    if (id != "") {
        envModulesProducts.id = id;
    }
    return JSON.stringify(envModulesProducts);
}

function getProductModuleJson() {
    var productModuleJson = new Object();
    var dataTable = $('#product_module_table').DataTable();
    var trs = dataTable.rows().nodes();
    for (var i = 0; i < trs.length; i++) {
        var obj = dataTable.rows(trs[i]).data()[0];
        $(trs[i]).children('td').children('input:checkbox:checked').each(function () {
            var retObj = new Array();
            var product_id = parseInt(obj.product_id);
            if (productModuleJson[product_id] != null) {
                retObj = productModuleJson[product_id];
            }
            retObj.push(parseInt(obj.module_id));
            productModuleJson[product_id] = retObj;
        });
    }
    return productModuleJson;
}

function loadUpdateEnvModuleProductVal() {
    $("#id").text(getItem("id"));
    $("#description").val(getItem("description"));
    $("#env_name").val(getItem("env_name"));
}

function validSelectedProductAndModule() {
    var isSelected = false;
    $('input:checkbox[name=select]:checked').each(function (i) {
        isSelected = true;
    });
    return isSelected;
}

function loadProductModuleTable() {
    var id = "product_module_table";
    var envId = $("#id").text();
    if (typeof(envId) == "undefined") {
        envId = -1;
    }
    var table = $('#' + id).DataTable({
        ajax: {
            url: '/env_module_product/product_module_data',
            dataSrc: 'data',
            data: {
                "envId": envId
            }
        },
        columns: [
            {
                data: 'is_select',
                orderable: false,
                createdCell: function (nTd, sData, oData, iRow, iCol) {
                    $(nTd).html(CONTENT.SELECT_CHECKBOX);
                    loadSelected(sData, $(nTd));
                }
            },
            {data: 'product_name'},
            {data: 'product_id', visible: false},
            {data: 'module_name'},
            {data: 'module_id', visible: false},
            {data: 'id', visible: false}
        ],
        language: {
            emptyTable: MESSAGES.EMPTY_TABLE,
            zeroRecords: MESSAGES.ZERO_RECORDS,
            loadingRecords: MESSAGES.LOADING_RECORDS,
            processing: MESSAGES.PROCESSING
        },
        columnDefs: [{
            targets: 0
        }, {
            targets: 1,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 20)}`);
            }
        }, {
            targets: 3,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 20)}`);
            }
        }],
        info: false,
        paging: false,
        processing: true,
        searching: true,
        retrieve: true,
        serverSide: true,
        scrollCollapse: true,
        ordering: false,
        scrollY: 300,
        initComplete: function (settings, json) {
            loadModuleSelect("module_select", "/module/module_data");
        },
        drawCallback: function (settings) {
            clickTableTr("select");
            initProductModuleQuery(table, "/env_module_product", id + "_filter");
        }
    });
}

function initProductModuleQuery(table, url, filterId) {
    loadQuery("请输入项目名称");
    var configSelectId = "config_select";
    var moduleSelectId = "module_select";
    var select = ".dataTables_filter";
    if (filterId) {
        select = "#" + filterId + select;
    }
    if ($(select + " > #clear_query").length == 0) {
        var filter = $(select);
        filter.prepend('<div class="history-query-div" id="config_div"><span>配置：</span><select id="' + configSelectId + '"' +
            ' class="history-query-select"><option value="-1">全部</option><option value="0">未配置</option><option ' +
            'value="1">已配置</option></select></div>');
        filter.prepend('<div class="history-query-div" id="module_div"><span>模块：</span><select id="' + moduleSelectId + '"' +
            ' class="history-query-select"></div>');
        filter.prepend('<div class="query-close" id="clear_query"><img src="/img/icon_guanbi.png"/></div>');
        filter.append('<div class="query-search"><img src="/img/icon_sousuo.png"/></div>');
        $(select + " > .query-search").click(function () {
            var name = $(select + " > label > input").val();
            var configId = getSelectVal(configSelectId);
            var moduleId = getSelectVal(moduleSelectId);
            var appendUrl = "";
            if (name && name.length > 0) {
                appendUrl += "&name=" + name;
            }
            table.ajax.url(url + '/product_module_data?moduleId=' + moduleId + "&configId=" + configId + appendUrl).load();
            $(select + " > label > input").val(name);
        });
        $(select + " > label > input").on('keypress', function (event) {
            if (event.keyCode == 13) {
                var name = $(this).val();
                var configId = getSelectVal(configSelectId);
                var moduleId = getSelectVal(moduleSelectId);
                var appendUrl = "";
                if (name && name.length > 0) {
                    appendUrl = "&name=" + name;
                }
                table.ajax.url(url + '/product_module_data?moduleId=' + moduleId + "&configId=" + configId + appendUrl).load();
                $(select + " > label > input").val(name);
            }
        });
        $(select + "> #clear_query").click(function () {
            var loadUrl = url + '/product_module_data';
            table.ajax.url(loadUrl).load();
            $("#" + configSelectId).find('option[value="-1"]').prop({selected: true});
            $("#" + moduleSelectId).find('option[value="-1"]').prop({selected: true});
            $(select + " > label > input").val("");
        });
    }
}

function loadModuleSelect(id, url) {
    //清空
    $("#" + id).empty();
    $("#" + id).append('<option value="-1">全部</option>');
    $.ajax({
        url: url,
        type: "post",
        cache: false,
        error: function (xhr, textStatus, thrownError) {
            ajaxError(xhr, textStatus, thrownError);
        },
        success: function (data) {
            if (typeof(data) == "string" && data.indexOf(MESSAGES.INPUT_ACCOUNT) != -1) {
                $.MsgBox.Alert(MESSAGES.NEWS, MESSAGES.AUTH_TIMEOUT, function () {
                    location.reload();
                });
            } else {
                if (data && data.length != 0) {
                    for (var i = 0; i < data.length; i++) {
                        var option = '<option value="' + data[i].id + '">' + data[i].name + '</option>'
                        $("#" + id).append(option);
                    }
                }
            }
        }
    });
}