/**
 * Created by maodi on 2018/6/5.
 */
function loadModuleProductTable() {
    var id = "module_product_table";
    var table = $('#' + id).DataTable({
        ajax: {
            url: '/module_product/query',
            dataSrc: 'data'
        },
        columns: [
            {data: ''},
            {data: 'num'},
            {data: 'product_name'},
            {data: 'module_name'},
            {data: 'description'},
            {data: ''},
            {
                data: 'id',
                visible: false
            }
        ],
        language: {
            emptyTable: MESSAGES.EMPTY_TABLE,
            zeroRecords: MESSAGES.ZERO_RECORDS,
            loadingRecords: MESSAGES.LOADING_RECORDS,
            processing: MESSAGES.PROCESSING
        },
        columnDefs: [{
            targets: 0,
            defaultContent: '<input type="checkbox" name="select"/>'
        }, {
            targets: 2,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 74)}`);
            }
        }, {
            targets: 3,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 22)}`);
            }
        }, {
            targets: 4,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 62)}`);
            }
        }, {
            targets: 5,
            defaultContent: CONTENT.EDIT_DELETE
        }],
        info: false,
        lengthChange: false,
        paging: true,
        searching: true,
        processing: true,
        serverSide: true,
        ordering: false,
        initComplete: function (settings, json) {
            batchDelete("/module_product/delete_by_ids", id);
        },
        drawCallback: function (settings) {
            initQuery(table, "/module_product");
            initToPage(settings, table);
            clickTableTr("select");
            updatePage(id, HTML.MODULE_PRODUCT_UPDATE);
            singleDelete("/module_product/delete_by_ids", id);
            getUserAuthResourcePage(id, HTML.MODULE_PRODUCT);
            $("#" + id).css("width", "100%");
            if (settings._iRecordsTotal < 1) {
                $(".dataTables_empty").css("width", "100%");
            }
        }
    });
}