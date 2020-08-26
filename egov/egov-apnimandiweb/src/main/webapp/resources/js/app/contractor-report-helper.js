$('#btnContractorReportbyDate').click(function(e) {
	if ($('form').valid()) {
		callContractorReportByDateRange();
	} else {
		e.preventDefault();
	}
});

$('#btnContractorReportbyZone').click(function(e) {
	if ($('form').valid()) {
		callContractorReportByZone();
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

function callContractorReportByDateRange() {
	drillDowntableContainer = jQuery("#resultTable");		
	jQuery('.report-section').removeClass('display-hide');
	reportdatatable = drillDowntableContainer.dataTable({
		ajax : {
			url : "/services/apnimandi/contractor/ajax/dm-contractor-by-date-range",      
			type: "POST",
			"data":  getFormData(jQuery('form'))
		},
		"bDestroy" : true,
		'bAutoWidth': false,
		"sDom" : "<'row'<'col-xs-12 hidden col-right'f>r>t<'row'<'col-xs-3'i><'col-xs-3 col-right'l><'col-xs-3 col-right'<'export-data'T>><'col-xs-3 text-right'p>>",
		"aLengthMenu" : [ [ 10, 25, 50, -1 ], [ 10, 25, 50, "All" ] ],
		"oTableTools" : {
			"sSwfPath" : "../../../../../../services/egi/resources/global/swf/copy_csv_xls_pdf.swf",
			"aButtons" : [ "xls" ]
		},		
		aaSorting: [],				
		columns : [ 
			{
				"data" : "zone",
				"className" : "text-left"
			},
			{
				"data" : "name",
				"className" : "text-left"
			},
			{
				"data" : "contractSignedOn",
				"className" : "text-right"
			},
			{
				"data" : "validFromDate",
				"className" : "text-right"
			},
			{
				"data" : "validToDate",
				"className" : "text-right"
			},
			{
				"data" : "status",
				"className" : "text-left"
			},
			{"data" : "id","visible": false, "searchable": false }
		]
	});
}	

function callContractorReportByZone() {
	drillDowntableContainer = jQuery("#resultTable");		
	jQuery('.report-section').removeClass('display-hide');
	reportdatatable = drillDowntableContainer.dataTable({
		ajax : {
			url : "/services/apnimandi/contractor/ajax/dm-contractor-by-zone",      
			type: "POST",
			"data":  getFormData(jQuery('form'))
		},
		"bDestroy" : true,
		'bAutoWidth': false,
		"sDom" : "<'row'<'col-xs-12 hidden col-right'f>r>t<'row'<'col-xs-3'i><'col-xs-3 col-right'l><'col-xs-3 col-right'<'export-data'T>><'col-xs-3 text-right'p>>",
		"aLengthMenu" : [ [ 10, 25, 50, -1 ], [ 10, 25, 50, "All" ] ],
		"oTableTools" : {
			"sSwfPath" : "../../../../../../services/egi/resources/global/swf/copy_csv_xls_pdf.swf",
			"aButtons" : [ "xls" ]
		},		
		aaSorting: [],				
		columns : [ 
			{
				"data" : "zone",
				"className" : "text-left"
			},
			{
				"data" : "name",
				"className" : "text-left"
			},
			{
				"data" : "contractSignedOn",
				"className" : "text-right"
			},
			{
				"data" : "validFromDate",
				"className" : "text-right"
			},
			{
				"data" : "validToDate",
				"className" : "text-right"
			},
			{
				"data" : "status",
				"className" : "text-left"
			},
			{"data" : "id","visible": false, "searchable": false }
		]
	});
}