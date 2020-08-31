$('#btnCollectionSearch').click(function(e) {
	if ($('form').valid()) {
		callAjaxSearch();
	} else {
		e.preventDefault();
	}
});

function getFormData($form){
    var unindexed_array = $form.serializeArray();
    var indexed_array = {};
    $.map(unindexed_array, function(n, i){
        indexed_array[n['name']] = n['value'];
    });
    return indexed_array;
}

function callAjaxSearch() {
	drillDowntableContainer = jQuery("#resultTable");		
	jQuery('.report-section').removeClass('display-hide');
	reportdatatable = drillDowntableContainer.dataTable({
		ajax : {
			url : "/services/apnimandi/collection/ajaxsearch",      
			type: "POST",
			"data":  getFormData(jQuery('form'))
		},
		"bDestroy" : true,
		'bAutoWidth': false,
		"sDom" : "<'row'<'col-xs-12 hidden col-right'f>r>t<'row'<'col-xs-3'i><'col-xs-3 col-right'l><'col-xs-3 col-right'<'export-data'T>><'col-xs-3 text-right'p>>",
		"aLengthMenu" : [ [ 10, 25, 50, -1 ], [ 10, 25, 50, "All" ] ],
		"oTableTools" : {
			"sSwfPath" : "../../../../../../services/egi/resources/global/swf/copy_csv_xls_pdf.swf",
			"aButtons" : [ "xls", "pdf", "print" ]
		},		
		aaSorting: [],				
		columns : [			
			{
				"data" : "zone",
				"className" : "text-left"
			},
			{
				"data" : "site",
				"className" : "text-left"
			},
			{
				"data" : "type",
				"className" : "text-left"
			},
			{
				"data" : "receiptNo",
				"className" : "text-left",
				"render" : function(data, type, full, meta) {
					if(full.receiptNo != 'NA'){
						return '<a href="javascript:void(0);" onclick="viewCollectionReceipt(\''+ full.paymentId +'\')">' + full.receiptNo + '</a>';
					}else{
						return 'NA';
					}					
				}
			},
			{
				"data" : "collectionMonth",
				"className" : "text-left"
			},
			{
				"data" : "collectionYear",
				"className" : "text-right"
			},
			{
				"data" : "active",
				"className" : "text-left"
			},
			{
				"data" : "status",
				"className" : "text-left"
			},
			{
				"className" : "text-right",
				render : function(data, type, full) {
					if(full.statusCode == "APPROVED"){
						return ('<select class="dropchange" id="additionconn"><option>Select action</option><option value="1">View Collection</option></select>');
					}else if(full.statusCode == "REJECTED"){
						return ('<select class="dropchange" id="additionconn"><option>Select action</option><option value="1">View Collection</option><option value="2">Edit Collection</option></select>');
					}else{
						return ('<select class="dropchange" id="additionconn"><option>Select action</option><option value="1">View Collection</option></select>');
					}					
				}
			},
			{"data" : "id","visible": false, "searchable": false}
		]
	});
}

$("#resultTable").on('change', 'tbody tr td .dropchange', function() {
	var id = drillDowntableContainer.fnGetData($(this).parent().parent(),9);
	if (this.value == 1) {
		window.open('/services/apnimandi/collection/view/' + id,'','width=800, height=600');
	}
	if (this.value == 2) {
		window.open('/services/apnimandi/collection/edit/' + id,'','width=800, height=600');
	}	
});

function viewCollectionReceipt(paymentId){
	window.open('/services/apnimandi/collection/view-receipt/' + paymentId,'','width=1200, height=800');
}