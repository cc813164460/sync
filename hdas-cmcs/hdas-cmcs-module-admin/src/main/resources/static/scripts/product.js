/**
 * Created by maodi on 2018/6/5.
 */
function loadProductTable() {
    var id = "product_table";
    var table = $('#' + id).DataTable({
        ajax: {
            url: '/product/query',
            dataSrc: 'data'
        },
        columns: [
            {data: ''},
            {data: 'num'},
            {data: 'product_name'},
            {data: 'area_name'},
            {data: 'organ_name'},
            {data: 'user_name'},
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
                $(nTd).children("div").text(`${sData.substring(0, 96)}`);
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
                $(nTd).children("div").text(`${sData.substring(0, 22)}`);
            }
        }, {
            targets: 5,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 22)}`);
            }
        }, {
            targets: 6,
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
            batchDelete("/product/delete_by_ids", id);
        },
        drawCallback: function (settings) {
            initQuery(table, "/product");
            initToPage(settings, table);
            clickTableTr("select");
            updatePage(id, HTML.PRODUCT_UPDATE);
            singleDelete("/product/delete_by_ids", id);
            getUserAuthResourcePage(id, HTML.PRODUCT);
            $("#" + id).css("width", "100%");
            if (settings._iRecordsTotal < 1) {
                $(".dataTables_empty").css("width", "100%");
            }
        }
    });
}