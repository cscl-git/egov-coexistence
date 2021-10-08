$('#btnReportSearch').click(function(e) {
	if ($('form').valid()) {
		callAjaxSearch();
	} else {
		e.preventDefault();
	}
});

function callAjaxSearch() {
	var fromDate=$('#fromDate').val();
	var toDate=$('#toDate').val();
	drillDowntableContainer = jQuery("#resultTable");		
	jQuery('.report-section').removeClass('display-hide');
	reportdatatable = drillDowntableContainer.dataTable({
		ajax : {
			url : "/services/apnimandi/collection/ajax/day-market-collection",      
			type: "GET",
			data: {
				fromDate:fromDate,
				toDate:toDate
			},
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
				"data" : "site",
				"className" : "text-left"
			},
			{
				"data" : "type",
				"className" : "text-left"
			},
			{
				"data" : "contractor",
				"className" : "text-left"
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
				"data" : "collectionDate",
				"className" : "text-right"
			},
			{
				"data" : "amountType",
				"className" : "text-left"
			},
			{
				"data" : "totalAmount",
				"className" : "text-left"
			},
			{
				"data" : "status",
				"className" : "text-left"
			},
			{"data" : "id","visible": false, "searchable": false}
		]
	});
}