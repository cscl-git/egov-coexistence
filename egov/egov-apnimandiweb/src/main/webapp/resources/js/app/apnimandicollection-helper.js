function getContractorList(zoneid, collectionMonth, collectionYear){
	$.ajax({
		url: "/services/apnimandi/contractor/ajax/getContractorByZone",
		type: "GET",
		data: {
			zoneid:zoneid,
			month:collectionMonth,
			year:collectionYear
		},
		cache: false,
		dataType: "json",
		success: function (response) {
			console.log("success"+response);
			jQuery('#contractor').html("");
			jQuery('#contractor').append("<option value=''>Select</option>");
			var count=0;
			jQuery.each(response, function(index, obj) {
				jQuery('#contractor').append($('<option>').text(obj.Text).attr('value', obj.Value));
				count++;
			});
			if(count>0){
				showContractorList();
			}else{
				hideContractorList();
			}
		}, 
		error: function (response) {
			jQuery('#contractor').html("");
			jQuery('#contractor').append("<option value=''>Select</option>");
			hideContractorList();
		}
	});	
}

function findBankDetailsByIfsc(){
	var ifscCode = document.getElementById("ifscCode").value;
    if(ifscCode.length == 11){
    	const Http = new XMLHttpRequest();
    	const url='https://ifsc.razorpay.com/'+ifscCode;
    	Http.onreadystatechange = function() {
	    	if (this.readyState == 4 && this.status == 200) {
	    	    var response = JSON.parse(this.responseText);
	    	    loadBankDetailSuccessHandler(response);
	    	}else if(this.readyState == 4 && this.status == 404){
	    		loadBankDetailFailureHandler();
	        };
    	}
    	Http.open("GET", url, true);
    	Http.send();
    }else{
    	document.getElementById("bankExistenceResponseMessageId").innerHTML="<p style='color:red'>Please Enter 11 digit IFSC code</p>";
    }
}

loadBankDetailSuccessHandler = function(response){
	document.getElementById("bankExistenceResponseMessageId").innerHTML="";
	document.getElementById("bankName").value = response.BANK;
	document.getElementById("branchName").value = response.BRANCH;
	document.getElementById("bankCode").value = response.BANKCODE;
}
loadBankDetailFailureHandler = function(){
	document.getElementById("bankName").value = "";
	document.getElementById("branchName").value = "";
	document.getElementById("bankCode").value = "";
	document.getElementById("bankExistenceResponseMessageId").innerHTML="<p style='color:red'>IFSC code is not valid</p>";
}

function showHideBankOptions(){
	var paymentMode=$('#paymentMode').val();
	if(paymentMode=="cheque" || paymentMode=="dd"){
		$('#ddOrChequeNo').attr('required', 'required');
		$('#ddOrChequeDate').attr('required', 'required');
		$('#dvDDCheque').show();
		$('#ifscCode').attr('required', 'required');
		$('#bankName').attr('required', 'required');
		$('#dvIFSC').show();
		$('#branchName').attr('required', 'required');
		$('#dvBranch').show();
	}else{
		$('#ddOrChequeNo').val('');
		$('#ddOrChequeDate').val('');
		$('#ifscCode').val('');
		$('#bankName').val('');
		$('#branchName').val('');
		$('#bankCode').val('');
		$('#ddOrChequeNo').removeAttr('required');
		$('#ddOrChequeDate').removeAttr('required');
		$('#dvDDCheque').hide();
		$('#ifscCode').removeAttr('required');
		$('#bankName').removeAttr('required');
		$('#dvIFSC').hide();
		$('#branchName').removeAttr('required');
		$('#dvBranch').hide();
	}
}

function getPaymentType(){
	var collectionTypeId=$('#collectiontype').val();
	if(collectionTypeId!=""){
		$.ajax({
			url: "/services/apnimandi/collection/ajax/getPaymentType",
			type: "GET",
			data: {
				collectionTypeId : collectionTypeId
			},
			cache: false,
			dataType: "json",
			success: function (response) {
				console.log("success"+response);
				var count=0;
				jQuery('#serviceCategory').val(response.serviceCategory);
				jQuery('#serviceType').html("");
				jQuery('#serviceType').append("<option value=''>Select</option>");
				jQuery.each(response.serviceTypes, function(index, obj) {
					jQuery('#serviceType').append($('<option>').text(obj.Text).attr('value', obj.Value));
					count++;
				});
				if(count>0){
					showPaymentType();
				}else{
					hidePaymentType();
				}
			}, 
			error: function (response) {
				hidePaymentType();
				jQuery('#serviceType').html("");
				jQuery('#serviceType').append("<option value=''>Select</option>");
			}
		});
	}else{
		hidePaymentType();
		resetAmountTables();
	}
}

function showPaymentType(){
	$('#serviceType').attr('required', 'required');
	$('#dvApnimandiPaymentType').show();
}

function hidePaymentType(){
	$('#serviceType').removeAttr('required');
	$('#dvApnimandiPaymentType').hide();
	jQuery('#serviceCategory').val('');
	jQuery('#serviceType').html("");
	jQuery('#serviceType').append("<option value=''>Select</option>");
}

function loadAcountHeadDetails() {
	resetAmountTables();
	var serviceCategory = $('#serviceCategory').val();
	var serviceType = $('#serviceType').val();
	if(serviceCategory!=""&&serviceType!=""){
		var path = '/services/collection';
		var service = serviceCategory+"."+serviceType;
		var ajaxurl = path + "/receipts/ajaxReceiptCreate-ajaxTaxHeadMasterByService.action";
		$.ajax({
			url: ajaxurl,
			type: "GET",
			data: {
				serviceId : service
			},
			cache: false,
			dataType: "json",
			success: function (response) {
				console.log("success"+response);
				loadFinAccSuccessHandler(response);
			}, 
			error: function (response) {
				loadFinAccFailureHandler();
			}
		});
	}
}

function loadFinAccSuccessHandler(res) {
	acountHeadCount=0;
	for (i = 0; i < res.ResultSet.Result.length; i++) {
		if(i==0){
			if(null != document.getElementById('apnimandiCollectionAmountDetails[0].accountHead')){
				document.getElementById('apnimandiCollectionAmountDetails[0].accountHead').value=res.ResultSet.Result[i].accounthead;
			}
			if(null != document.getElementById('apnimandiCollectionAmountDetails[0].glCodeIdDetail')){
				document.getElementById('apnimandiCollectionAmountDetails[0].glCodeIdDetail').value=res.ResultSet.Result[i].glcodeIdDetail;
			}
			if(null != document.getElementById('apnimandiCollectionAmountDetails[0].amountType')){
				document.getElementById('apnimandiCollectionAmountDetails[0].amountType').value=res.ResultSet.Result[i].amountType;
			}
			if(null != document.getElementById('apnimandiCollectionAmountDetails[0].creditAmountDetail')){
				document.getElementById('apnimandiCollectionAmountDetails[0].creditAmountDetail').value=0;
			}
		}else{
			var tbody = document.getElementById("paymentDetailTbody");
			var tr = document.createElement("tr");
			tr.setAttribute("id", "paymentDetailTR_"+i);
			
			var td = document.createElement("td");			
			var inputText = document.createElement("input");
			inputText.setAttribute("type", "text");
			inputText.setAttribute("name", "apnimandiCollectionAmountDetails["+i+"].accountHead");
			inputText.setAttribute("id", "apnimandiCollectionAmountDetails["+i+"].accountHead");
			inputText.setAttribute("class", "form-control patternvalidation text-left");
			inputText.setAttribute("readonly", "readonly");
			inputText.setAttribute("maxlength", "100");
			inputText.setAttribute("value", res.ResultSet.Result[i].accounthead);			
			td.appendChild(inputText);			
			var inputHidden = document.createElement("input");
			inputHidden.setAttribute("type", "hidden");
			inputHidden.setAttribute("name", "apnimandiCollectionAmountDetails["+i+"].glCodeIdDetail");
			inputHidden.setAttribute("id", "apnimandiCollectionAmountDetails["+i+"].glCodeIdDetail");
			inputHidden.setAttribute("value", res.ResultSet.Result[i].glcodeIdDetail);
			td.appendChild(inputHidden);			
			var inputHidden1 = document.createElement("input");
			inputHidden1.setAttribute("type", "hidden");
			inputHidden1.setAttribute("name", "apnimandiCollectionAmountDetails["+i+"].amountType");
			inputHidden1.setAttribute("id", "apnimandiCollectionAmountDetails["+i+"].amountType");
			inputHidden1.setAttribute("value", res.ResultSet.Result[i].amountType);
			td.appendChild(inputHidden1);
			tr.appendChild(td);
			
			var td1 = document.createElement("td");
			var inputText1 = document.createElement("input");
			inputText1.setAttribute("type", "text");
			inputText1.setAttribute("name", "apnimandiCollectionAmountDetails["+i+"].creditAmountDetail");
			inputText1.setAttribute("id", "apnimandiCollectionAmountDetails["+i+"].creditAmountDetail");
			inputText1.setAttribute("class", "form-control patternvalidation text-right");
			inputText1.setAttribute("maxlength", "10");
			inputText1.setAttribute("data-pattern", "number");
			inputText1.setAttribute("value", 0);		
			inputText1.setAttribute("onblur", ";updateTotalAmount()");
			td1.appendChild(inputText1);
			tr.appendChild(td1);
			tbody.appendChild(tr);
		}
		acountHeadCount++;
	}
	$("#receiptHeadCount").val(acountHeadCount);
	updateTotalAmount();
	patternvalidation();
}

function loadFinAccFailureHandler() {
	resetAmountTables();
	bootbox.alert('unable to load Function');
}

function resetAmountTables(){
	acountHeadCount=0;
	$("#receiptHeadCount").val(acountHeadCount);
	document.getElementById('totalcramount').value=0;
	document.getElementById('amount').value=0;
	$('#totalAmount').html('0');
	var tbody = document.getElementById("paymentDetailTbody");
	var trNo = (tbody.childElementCount ? tbody.childElementCount : tbody.childNodes.length) + 1;
	if(trNo>0){				
		if(null != document.getElementById('apnimandiCollectionAmountDetails[0].accountHead')){
			document.getElementById('apnimandiCollectionAmountDetails[0].accountHead').value="";
		}
		if(null != document.getElementById('apnimandiCollectionAmountDetails[0].glCodeIdDetail')){
			document.getElementById('apnimandiCollectionAmountDetails[0].glCodeIdDetail').value="";
		}
		if(null != document.getElementById('apnimandiCollectionAmountDetails[0].amountType')){
			document.getElementById('apnimandiCollectionAmountDetails[0].amountType').value="";
		}
		if(null != document.getElementById('apnimandiCollectionAmountDetails[0].creditAmountDetail')){
			document.getElementById('apnimandiCollectionAmountDetails[0].creditAmountDetail').value=0;
		}
		for (var i=0; i<trNo; i++ ){
			if(i>0){
				$("#paymentDetailTR_" + i).remove();
			}
		}
		patternvalidation();
	}
}

function updateTotalAmount(){
	var totalamount = 0;
	for (i = 0; i < acountHeadCount; i++) {
		var amount = document.getElementById('apnimandiCollectionAmountDetails['+i+'].creditAmountDetail').value;
		totalamount = totalamount + parseFloat(amount);
	}
	document.getElementById('totalcramount').value=totalamount;
	document.getElementById('amount').value=totalamount;
	$('#totalAmount').html(totalamount);
}