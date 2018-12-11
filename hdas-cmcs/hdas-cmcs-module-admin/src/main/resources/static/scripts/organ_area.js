/**
 * Created by maodi on 2018/6/5.
 */
function loadOrganAreaTable() {
    var id = "organ_area_table";
    var table = $('#' + id).DataTable({
        ajax: {
            url: '/organ_area/query',
            dataSrc: 'data'
        },
        columns: [
            {data: ''},
            {data: 'num'},
            {data: 'organ_name'},
            {data: 'area_name'},
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
                $(nTd).children("div").text(`${sData.substring(0, 66)}`);
            }
        }, {
            targets: 3,
            createdCell: function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html(`<div></div>`);
                $(nTd).children("div").attr("title", `${sData}`);
                $(nTd).children("div").text(`${sData.substring(0, 99)}`);
            }
        }, {
            targets: 4,
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
            batchDelete("/organ_area/delete_by_ids", id);
        },
        drawCallback: function (settings) {
            initToPage(settings, table);
            clickTableTr("select");
            updatePage(id, HTML.ORGAN_AREA_UPDATE);
            singleDelete("/organ_area/delete_by_ids", id);
            getUserAuthResourcePage(id, HTML.ORGAN_AREA);
            $("#" + id).css("width", "100%");
            if (settings._iRecordsTotal < 1) {
                $(".dataTables_empty").css("width", "100%");
            }
        }
    });
}