/**
 * Created by maodi on 2018/6/5.
 */
function loadRoleAuthResourceTable() {
    var id = "role_auth_resource_table";
    var table = $('#' + id).DataTable({
        ajax: {
            url: '/role_auth_resource/query',
            dataSrc: 'data'
        },
        columns: [
            {data: 'num'},
            {data: 'role_name'},
            {data: 'product_names'},
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
            targets: 1,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 44)}`);
            }
        }, {
            targets: 2,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 116)}`);
            }
        }, {
            targets: 3,
            defaultContent: CONTENT.EDIT_DELETE
        }],
        info: false,
        lengthChange: false,
        paging: true,
        searching: false,
        processing: true,
        serverSide: true,
        ordering: false,
        initComplete: function (settings, json) {
        },
        drawCallback: function (settings) {
            initToPage(settings, table);
            updatePage(id, HTML.ROLE_AUTH_RES_UPDATE);
            singleDelete("/role_auth_resource/delete_by_ids", id);
            getUserAuthResourcePage(id, HTML.ROLE_AUTH_RES);
            $("#" + id).css("width", "100%");
            if (settings._iRecordsTotal < 1) {
                $(".dataTables_empty").css("width", "100%");
            }
        }
    });
}