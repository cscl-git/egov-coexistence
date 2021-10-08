var isDayMarket=false;
var acountHeadCount=0;

$(document).ready(function(){
	var collectionTypeCode=$('#collectionTypeCode').val();
	var zoneid=$('#zoneId').val();
	var collectionMonth=$('#collectionForMonth').val();
	var collectionYear=$('#collectionForYear').val();
	if(collectionTypeCode == "DAY_MARKET"){
		showContractorList();
		isDayMarket=true;
	}else{
		hideContractorList();
		isDayMarket=false;
	}
	showHideBankOptions();
	acountHeadCount = $("#receiptHeadCount").val();
});
$('#buttonSubmit').click(function(e) {
	if ($('form').valid()) {
	} else {
		e.preventDefault();
	}
});

$('#collectionForMonth').change(function(){
	getContractorByZone();
});

$('#collectionForYear').change(function(){
	getContractorByZone();
});

$('#paymentMode').change(function(){
	showHideBankOptions();
});

$("#amount").on("keyup", function(){
    var valid = /^\d{0,8}(\.\d{0,2})?$/.test(this.value),
        val = this.value;
    if(!valid){
        console.log("Invalid input!");
        this.value = val.substring(0, val.length - 1);
    }
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

function getContractorByZone(){
	var zoneid=$('#zoneId').val();
	var collectionMonth=$('#collectionForMonth').val();
	var collectionYear=$('#collectionForYear').val();
	if(zoneid!="" && collectionMonth!="" && collectionYear!="" && isDayMarket){
		getContractorList(zoneid, collectionMonth, collectionYear);
	}
}