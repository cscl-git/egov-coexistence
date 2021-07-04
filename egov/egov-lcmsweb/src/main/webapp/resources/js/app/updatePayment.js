/**
 * 
 */
$(document).ready(function(){
$('#nameOfDefendingCounsil').val('');
$('#caseFee').val('');
$('#email').val('');
$('#miscExpenses').val('');
$('#phoneNo').val('');
$('#status').val('');
$('#modeOfPayment').val('');
$('#amount').val('');
$('#paymentOfIssuance').val('');
$('#findingOfReplyCase').val('');
$('#finalDisposal').val('');
var count = $('#payDetails tr').length;

if((count -1) >= 3){
	bootbox.alert("Only three Payments are allowed");
	$('#nameOfDefendingCounsil').attr('readonly', true);
	$('#caseFee').attr('readonly', 'readonly');
	$('#email').attr('readonly', true);
	$('#miscExpenses').attr('readonly', 'readonly');
	$('#phoneNo').attr('readonly', true);
	$('#status').attr('readonly', true);
	$('#modeOfPayment').attr('readonly', true);
	$('#amount').attr('readonly', true);
	$('#paymentOfIssuance').attr('readonly', true);
	$('#findingOfReplyCase').attr('readonly', true);
	$('#finalDisposal').attr('readonly', true);
	
	/*$('#nameOfDefendingCounsil').attr('readonly', true);
	$('#caseFee').attr('readonly', true);
	$('#email').attr('readonly', true);
	$('#miscExpenses').attr('readonly', true);
	$('#phoneNo').attr('readonly', true);
	$('#status').attr('readonly', true);
	$('#modeOfPayment').attr('readonly', true);
	$('#amount').attr('readonly', true);
	$('#paymentOfIssuance').attr('readonly', true);
	$('#findingOfReplyCase').attr('readonly', true);
	$('#finalDisposal').attr('readonly', true);*/
	}
});

function totalAmount()
{
var caseValue =document.getElementById("caseFee").value;
var miscValue =document.getElementById("miscExpenses").value;
//alert("::case: "+caseValue+"::mis: "+miscValue);
//var total=+caseValue + +miscValue;

if(caseValue !='' && miscValue!= ''){
	var total=+caseValue + +miscValue;
	$('#amount').val(total);
}else if(miscValue!= ''){
	var total=miscValue;
	$('#amount').val(total);
}else if(caseValue !=''){
	var total=caseValue;
	$('#amount').val(total);
}
//alert("::total: "+total);
//$('#amount').val(total);
}

function checktable(){
//	alert("check");
	var count = $('#payDetails tr').length;
	
	if((count -1) >= 3){
		bootbox.alert("Only three Payments are allowed");
		return false
		
	}
	return true;
}

function fillvalue(){
	
	var vale = document.getElementById("nameOfDefendingCounsil").value;
//	alert("vale--"+vale);
	
	$.ajax({
		 url:'/services/lcms/popup/getupdatePaymet/'+vale,
		 contentType:"application/json",		
		 dataType:"json",
		 success:function(r)
		 {
//			 debugger;ss
			 data=r;
			for (var i=0; i<data.length; i++) {
					
					console.log(data[i].id+ '------KK----' + data[i].counselEmail +'--'+ data[i].counselphoneNo);
					 $('#email').val(data[i].counselEmail);
					 $('#phoneNo').val(data[i].counselphoneNo);
						
			
			}
		 }
	
	 })
}