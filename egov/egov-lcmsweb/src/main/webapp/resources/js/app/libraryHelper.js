jQuery('#btnsearch').click(function(e) {		
	callAjaxSearch();
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
	reportdatatable = drillDowntableContainer
		.dataTable({
			ajax : {
				url : "/services/lcms/library/ajaxsearch/"+$('#mode').val(),      
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
			columns : [ { 
				"data" : "documentType", "sClass" : "text-center"} ,{ 
				"data" : "title", "sClass" : "text-center"} ,{ 
				"data" : "active", "sClass" : "text-center"},{ 
				"data" : "id","visible": false, "searchable": false }]				
		});
}

$("#resultTable").on('click','tbody tr',function(event) {
	window.open('/services/lcms/library/'+ $('#mode').val() +'/'+drillDowntableContainer.fnGetData(this,3),'','width=800, height=600');
});