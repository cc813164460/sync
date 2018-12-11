/**
 * Created by maodi on 2018/6/5.
 */
function loadRoleTable() {
    var id = "role_table";
    var table = $('#' + id).DataTable({
        ajax: {
            url: '/role/query',
            dataSrc: 'data'
        },
        columns: [
            {data: ''},
            {data: 'num'},
            {data: 'name'},
            {data: 'description', visible: false},
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
                $(nTd).children("div").text(`${sData.substring(0, 168)}`);
            }
        }, {
            targets: 4,
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
            batchDelete("/role/delete_by_ids", id);
        },
        drawCallback: function (settings) {
            initQuery(table, "/role");
            initToPage(settings, table);
            clickTableTr("select");
            updatePage(id, HTML.ROLE_UPDATE);
            singleDelete("/role/delete_by_ids", id);
            getUserAuthResourcePage(id, HTML.ROLE);
            $("#" + id).css("width", "100%");
            if (settings._iRecordsTotal < 1) {
                $(".dataTables_empty").css("width", "100%");
            }
        }
    });
}