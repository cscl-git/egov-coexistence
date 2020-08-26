var isDayMarket=false;
var acountHeadCount=0;

$(document).ready(function(){
	hideContractorList();
	hidePaymentType();
	showHideBankOptions();
	resetAmountTables();
	$("#receiptHeadCount").val(acountHeadCount);
});

$('#buttonSubmit').click(function(e) {
	if ($('form').valid()) {
	} else {
		e.preventDefault();
	}
});

$('#collectiontype').change(function(){
	var collectionTypeText=$("#collectiontype option:selected").text();
	if(collectionTypeText == 'Day Market'){
		showContractorList();
	}else{
		hideContractorList();
	}
	isCollectionForDayMarket();
	getPaymentType();
	resetAmountTables();
});

$('#zone').change(function(){
	getContractorByZone();
	getDepartmentByZone();
	getSitesByZone();
});

$('#collectionForMonth').change(function(){
	getContractorByZone();
});

$('#collectionForYear').change(function(){
	getContractorByZone();
});

$("#amount").on("keyup", function(){
    var valid = /^\d{0,8}(\.\d{0,2})?$/.test(this.value),
        val = this.value;
    if(!valid){
        console.log("Invalid input!");
        this.value = val.substring(0, val.length - 1);
    }
});

$('#paymentMode').change(function(){
	showHideBankOptions();
});

$('#serviceType').change(function(){
	loadAcountHeadDetails();
});

function showContractorList(){
	$('#contractor').attr('required', 'required');
	$('#dvApnimandiContractor').show();
}

function hideContractorList(){
	$('#contractor').val('');
	$('#contractor').removeAttr('required');
	$('#dvApnimandiContractor').hide();
}

function isCollectionForDayMarket(){
	isDayMarket=false;
	var collectionTypeId=$('#collectiontype').val();
	if(collectionTypeId!=""){
		$.ajax({
			url: "/services/apnimandi/collection/ajax/isCollectionForDayMarket",
			type: "GET",
			data: {
				collectionTypeId : collectionTypeId
			},
			cache: false,
			dataType: "json",
			success: function (response) {
				console.log("success"+response);
				if(response.isCollectionForDayMarket){
					isDayMarket=true;
					getContractorByZone();
				}else{
					isDayMarket=false;
					jQuery('#contractor').html("");
					jQuery('#contractor').append("<option value=''>Select</option>");
				}
			}, 
			error: function (response) {
				hideContractorList();
				jQuery('#contractor').html("");
				jQuery('#contractor').append("<option value=''>Select</option>");
			}
		});
	}
}

function getContractorByZone(){
	var zoneid=$('#zone').val();
	var collectionMonth=$('#collectionForMonth').val();
	var collectionYear=$('#collectionForYear').val();
	if(zoneid!="" && collectionMonth!="" && collectionYear!="" && isDayMarket){
		getContractorList(zoneid, collectionMonth, collectionYear);
	}
}

