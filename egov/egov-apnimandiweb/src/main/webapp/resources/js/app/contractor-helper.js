$('#buttonSubmit').click(function(e) {
	if ($('form').valid()) {
	} else {
		e.preventDefault();
	}
});

$('#btnContractorSearch').click(function(e) {
	if ($('form').valid()) {
		callAjaxSearch();
	} else {
		e.preventDefault();
	}
});

$("#rentAmountPerDay").on("keyup", function(){  // validate 7 digits and two decimal points
    var valid = /^\d{0,9}(\.\d{0,2})?$/.test(this.value),
        val = this.value;
    if(!valid){
        console.log("Invalid input!");
        this.value = val.substring(0, val.length - 1);
    }
});

$("#securityFees").on("keyup", function(){  // validate 7 digits and two decimal points
    var valid = /^\d{0,9}(\.\d{0,2})?$/.test(this.value),
        val = this.value;
    if(!valid){
        console.log("Invalid input!");
        this.value = val.substring(0, val.length - 1);
    }
});

$("#penaltyAmountPerDay").on("keyup", function(){  // validate 7 digits and two decimal points
    var valid = /^\d{0,9}(\.\d{0,2})?$/.test(this.value),
        val = this.value;
    if(!valid){
        console.log("Invalid input!");
        this.value = val.substring(0, val.length - 1);
    }
});

$("#contractorSharePercentage").on("keyup", function(){  // validate 7 digits and two decimal points
    var valid = /^\d{0,3}(\.\d{0,2})?$/.test(this.value),
        val = this.value;
    if(!valid){
        console.log("Invalid input!");
        this.value = val.substring(0, val.length - 1);
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
	hideAddnewForm();
	drillDowntableContainer = jQuery("#resultTable");		
	jQuery('.report-section').removeClass('display-hide');
	reportdatatable = drillDowntableContainer.dataTable({
		ajax : {
			url : "/services/apnimandi/contractor/ajaxsearch",      
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
				"data" : "name",
				"className" : "text-left"
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
						return ('<select class="dropchange" id="additionconn"><option>Select action</option><option value="1">View Contractor</option><option value="2">Edit Contractor</option><option value="3">Terminate Contractor</option></select>');
					}else if(full.statusCode == "REJECTED"){
						return ('<select class="dropchange" id="additionconn"><option>Select action</option><option value="1">View Contractor</option><option value="2">Edit Contractor</option></select>');
					}else if(full.statusCode == "CONTRACTTERMINATED"){
						return ('<select class="dropchange" id="additionconn"><option>Select action</option><option value="1">View Contractor</option><option value="3">Edit Termination Details</option></select>');
					}else{
						return ('<select class="dropchange" id="additionconn"><option>Select action</option><option value="1">View Contractor</option></select>');
					}					
				}
			},
			{"data" : "id","visible": false, "searchable": false }
		],
		"initComplete": function(settings, json) {
			showAddnewForm();
		}
	});
}

$("#resultTable").on('change', 'tbody tr td .dropchange', function() {
	var id = drillDowntableContainer.fnGetData($(this).parent().parent(),7);
	if (this.value == 1) {
		window.open('/services/apnimandi/contractor/view/' + id,'','width=800, height=600');
	}
	if (this.value == 2) {
		window.open('/services/apnimandi/contractor/edit/' + id,'','width=800, height=600');
	}
	if (this.value == 3) {
		window.open('/services/apnimandi/contractor/terminate/edit/' + id,'','width=800, height=600');
	}	
});

function showAddnewForm(){
	$('#postResultHeader').show();
	$('#defaultResultHeader').hide();
	var zone = $("#zone option:selected").text();
	$("#selectedZone").val($("#zone option:selected").val());
	$('#postResultLabel').html("Contractor Search Result for " + zone);
}

function hideAddnewForm(){
	$('#postResultHeader').hide();
	$('#defaultResultHeader').show();
}

$('#btnContractorAddNew').click(function(e) {
	var selectedZone = $("#selectedZone").val();
	var url;
	if(selectedZone){
		url="/services/apnimandi/contractor/new/" + selectedZone;
	}else{
		url="/services/apnimandi/contractor/new/1";
	}
	window.location = url;
});
		