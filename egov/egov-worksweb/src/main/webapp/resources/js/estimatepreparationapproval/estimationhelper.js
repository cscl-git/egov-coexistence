
/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

$(document).ready(function(){
	
});


function calculateMeasuredAmount(x)
{
	var dataId = $(x).attr("data-idx");
	var rate=document.getElementById("boQDetailsList["+x+ "].rate").value;
	var measured_quantity=document.getElementById("boQDetailsList["+ x+ "].measured_quantity").value;
	
	if (null == rate || rate =='null'  )
	{
		rate=0;
	}
	else if (null == measured_quantity || measured_quantity =='null')
	{
		measured_quantity=0;
	}
	else {
		var measuredAmount=rate*measured_quantity;
		document.getElementById("boQDetailsList["+x+"].measured_amount").value=measuredAmount;
	}
	
	
	}



function calcualtePerctAmount(x){
	
	var rowcount = $("#tblchecklist tbody tr").length;
	var estimateAmt = 0;
	var amt = 0;
	var paymentval=0;
	
	
	var work_amount=0;
	var dataId = $(x).attr("data-idx");

	
	  if (dataId==undefined)
	  {
	  	dataId=0;
	  }
	
	
	if (null != document.getElementById("work_amount")) {
		var val = document.getElementById("work_amount").value;
		if (val != "" && !isNaN(val)) {
			work_amount = val;
		}
	}
	

	
	if (null != document.getElementById("paymentDistribution[" + dataId
			+ "].payment_percent") && document.getElementById("paymentDistribution[" + dataId
					+ "].payment_percent").value != 0) {
		
		for (var i = 0; i <= rowcount; i++) {
			if (null != document.getElementById("paymentDistribution[" + i
					+ "].payment_percent") && document.getElementById("paymentDistribution[" + i
							+ "].payment_percent").value != 0) 
			{
			
			paymentval = document.getElementById("paymentDistribution["+i+ "].payment_percent").value;
			estimateAmt = +estimateAmt + +paymentval;
			}
		}
		
		if(estimateAmt <= 100)
		{
		var val = document.getElementById("paymentDistribution[" + dataId
				+ "].payment_percent").value;
		if (val != "" && !isNaN(val)) {
			var amt=(val*work_amount)/100;
			document.getElementById("paymentDistribution[" + dataId
					+ "].amount").value=parseFloat(Number(amt)).toFixed(2);
					}	
		}
		else{
			bootbox.alert("Payment Milestone Percentage exceed 100%");
		}	
	}
	
}


function calcualtePerctAmountedit(x){
	
	var rowcount = $("#tblchecklist tbody tr").length;
	var estimateAmt = 0;
	var amt = 0;
	var paymentval=0;
	
	
	var work_amount=0;
	var dataId = $(x).attr("data-idx");

	
	  if (dataId==undefined)
	  {
	  	dataId=0;
	  }
	
	
	if (null != document.getElementById("work_amount")) {
		var val = document.getElementById("work_amount").value;
		if (val != "" && !isNaN(val)) {
			work_amount = val;
		}
	}
	

	
	if (null != document.getElementById("paymentDistribution[" + x
			+ "].payment_percent") && document.getElementById("paymentDistribution[" + x
					+ "].payment_percent").value != 0) {
		
		for (var i = 0; i <= rowcount; i++) {
			if (null != document.getElementById("paymentDistribution[" + i
					+ "].payment_percent") && document.getElementById("paymentDistribution[" + i
							+ "].payment_percent").value != 0) 
			{
			
			paymentval = document.getElementById("paymentDistribution["+i+ "].payment_percent").value;
			estimateAmt = +estimateAmt + +paymentval;
			}
		}
		
		if(estimateAmt <= 100)
		{
				var val = document.getElementById("paymentDistribution[" + x
						+ "].payment_percent").value;
				if (val != "" && !isNaN(val)) {
						var amt=(val*work_amount)/100;
						document.getElementById("paymentDistribution[" + x
								+ "].amount").value=parseFloat(Number(amt)).toFixed(2);
					}	
		}
		else{
			bootbox.alert("Payment Milestone Percentage exceed 100%");
		}
	}
	
}

function completionPerctAmountedit(x){
	
	var rowcount = $("#tblchecklist tbody tr").length;
	
	var paymentval=0;
	
	
	var dataId = $(x).attr("data-idx");

	
	  if (dataId==undefined)
	  {
	  	dataId=0;
	  }
	  var percentage =document.getElementById("paymentDistribution[" + x
				+ "].completion_percent").value;
		

		if(percentage <=100)
		{
				
		}
		else{
			bootbox.alert("Completion Percentage exceed 100%");
		}
	}
	


var subledgerrowcount=0;
function addpaymentRow() { 
	
	
	var rowcount = $("#tblchecklist tbody tr").length;
	if (rowcount < 30) {
		if (document.getElementById('tblchecklistRow') != null) {
			addRowPayment('tblchecklist','tblchecklistRow');
			$('#tblchecklist tbody tr:eq('+rowcount+')').find('.payment_desc').val('');
			$('#tblchecklist tbody tr:eq('+rowcount+')').find('.payment_percent').val('');
			$('#tblchecklist tbody tr:eq('+rowcount+')').find('.amount').val('');
			
			++subledgerrowcount;
			
			addCustomEvent(rowcount,'temppayment[index].addButton','keydown',shortKeyFunForAddButton);
		}
	} else {
		  bootbox.alert($.i18n.prop('msg.limit.reached'));
	}
}





function addRowPayment(tableName,rowName) { 
	
	var rowcount = $("#"+tableName+" tbody tr").length;
	if (rowcount < 30) {
		if (document.getElementById(rowName) != null) {
			// get Next Row Index to Generate
			var nextIdx = 0;
				nextIdx = $("#"+tableName+" tbody tr").length;
			// validate status variable for exiting function
			var isValid = 1;// for default have success value 0

			// validate existing rows in table
			$("#"+tableName+" tbody tr").find('input,select').each(
					function() {
						if (($(this).data('optional') === 0)
								&& (!$(this).val()) && !($(this).attr('name')===undefined)) { 
							$(this).focus();
							bootbox.alert($(this).data('errormsg'));
							isValid = 0;// set validation failure
							return false;
						}
			});
			if (isValid === 0) {
				return false;
			}
			
			// Generate all textboxes Id and name with new index
			$("#"+rowName+"").clone().find("input,select,errors,span").each(
			function() {	
				if ($(this).data('server')) {
					$(this).removeAttr('data-server');
				}
				
				$(this).attr(
						{
							'name' : function(_, name) {
								if(!($(this).attr('name')===undefined))
									return name.replace(/\d+/, nextIdx); 
							},
							'class' : function(_, name) {
								if(!($(this).attr('class')===undefined))
									return name.replace(/\d+/, nextIdx); 
							},
							'id' : function(_, id) {
								if(!($(this).attr('id')===undefined))
									return id.replace(/\d+/, nextIdx); 
							},
							'data-idx' : function(_,dataIdx)
							{
								return nextIdx;
							}
						});
	
					// if element is static attribute hold values for next row, otherwise it will be reset
					if (!$(this).data('static')) {
						$(this).val('');
					}
	
			}).end().appendTo("#"+tableName+" tbody");		
			
		}
	} else {
		  bootbox.alert('limit reached!');
	}
}

function addRow(tableName,rowName) { 
	
	var rowcount = $("#"+tableName+" tbody tr").length;
	if (rowcount < 30) {
		if (document.getElementById(rowName) != null) {
			// get Next Row Index to Generate
			var nextIdx = 0;
				nextIdx = $('.tableBoq tbody tr').length;
			// validate status variable for exiting function
			var isValid = 1;// for default have success value 0

			// validate existing rows in table
			$("#"+tableName+" tbody tr").find('input,select,textarea').each(
					function() {
						if (($(this).data('optional') === 0)
								&& (!$(this).val()) && !($(this).attr('name')===undefined)) { 
							$(this).focus();
							bootbox.alert($(this).data('errormsg'));
							isValid = 0;// set validation failure
							return false;
						}
			});
			if (isValid === 0) {
				return false;
			}
			
			// Generate all textboxes Id and name with new index
			$("#"+rowName+"").clone().find("input,select,errors,span").each(
			function() {	
				if ($(this).data('server')) {
					$(this).removeAttr('data-server');
				}
				
				$(this).attr(
						{
							'name' : function(_, name) {
								if(!($(this).attr('name')===undefined))
									return name.replace(/\d+/, nextIdx); 
							},
							'class' : function(_, name) {
								if(!($(this).attr('class')===undefined))
									return name.replace(/\d+/, nextIdx); 
							},
							'id' : function(_, id) {
								if(!($(this).attr('id')===undefined))
									return id.replace(/\d+/, nextIdx); 
							},
							'data-idx' : function(_,dataIdx)
							{
								return nextIdx;
							}
						});
	
					// if element is static attribute hold values for next row, otherwise it will be reset
					if (!$(this).data('static')) {
						$(this).val('');
					}
	
			}).end().appendTo("#"+tableName+" tbody");		
			
		}
	} else {
		  bootbox.alert('limit reached!');
	}
}


function deleteRow(obj,tableName) {
    var rIndex = getRow(obj).rowIndex;
    
    var id = $(getRow(obj)).children('td:first').children('input:first').val();
    //To get all the deleted rows id
    var aIndex = rIndex - 1;
	var tbl=document.getElementById(tableName);	
	var rowcount=$(".tableBoq tbody tr").length;
    if(rowcount<=1 && tableName!=tableName) {
		bootbox.alert("This row can not be deleted");
		return false;
	} else {
		tbl.deleteRow(rIndex);
		//starting index for table fields
		var idx=0;
		//regenerate index existing inputs in table row
		$("#"+tableName+" tbody tr").each(function() {
			console.log('Index:'+idx)
			$(this).find("input,select,errors,span").each(function() {
				   $(this).attr({
				      'name': function(_, name) {
				    	  if(!($(this).attr('name')===undefined)){
				    		  name= name.replace(/\_./g, '_'+ idx )
				    		  return name.replace(/\[.\]/g, '['+ idx +']'); 
				    	  }
				      },
				      'id': function(_, id) {
				    	  if(!($(this).attr('id')===undefined)){
				    		  id= id.replace(/\_./g, '_'+ idx )
				    		  return id.replace(/\[.\]/g, '['+ idx +']'); 
				    	  }
				      },
				      'class': function(_, id) {
				    	  if(!($(this).attr('class')===undefined)){
				    		  id= id.replace(/\_./g, '_'+ idx )
				    		  return id.replace(/\[.\]/g, '['+ idx +']'); 
				    	  }
				      },
					  'data-idx' : function(_,dataIdx)
					  {
						  return idx;
					  }
				   });
		    });
			idx++;
		
			//hiddenElem=$(this).find("input:hidden");
			
			/*if(!$(hiddenElem).val())
			{*/
				
			//}
		});
		return true;
	}	
}

function getRow(obj) {
	if(!obj)return null;
	tag = obj.nodeName.toUpperCase();
	while(tag != 'BODY'){
		if (tag == 'TR') return obj;
		obj=obj.parentNode ;
		tag = obj.nodeName.toUpperCase();
	}
	return null;
}









var subledgerrowcount=0;
function addcheckListRow(x) { 
	var dataId = $(x).attr("data-idx");

	
  if (dataId==undefined)
  {
  	dataId=1;
  }
   else{
   	dataId++;
   }  
	
     
     

	var rowcount = $('.tableBoq tbody tr').length;
	if (rowcount < 30) {
		if (document.getElementById('boq'+x+'tableBoqrow') != null) {
			addRow('boq'+x+'tableBoq','boq'+x+'tableBoqrow');
			$('#boq'+x+'tableBoq tbody tr:eq('+rowcount+')').find('.milestone').val('');
			$('#boq'+x+'tableBoq tbody tr:eq('+rowcount+')').find('.item_description').val('');
			$('#boq'+x+'tableBoq tbody tr:eq('+rowcount+')').find('.ref_dsr').val('');
			$('#boq'+x+'tableBoq tbody tr:eq('+rowcount+')').find('.unit').val('');
			$('#boq'+x+'tableBoq tbody tr:eq('+rowcount+')').find('.rate').val('');
			$('#boq'+x+'tableBoq tbody tr:eq('+rowcount+')').find('.quantity').val('');
			$('#boq'+x+'tableBoq tbody tr:eq('+rowcount+')').find('.amount').val('');
			
			++subledgerrowcount;
			
			addCustomEvent(rowcount,'tempSubLedger[index].addButton','keydown',shortKeyFunForAddButton);
		}
	} else {
		  bootbox.alert($.i18n.prop('msg.limit.reached'));
	}
}

function deleteEsRow(obj,x) {
	var rowcount=$(".tableBoq tbody tr").length;
   
		deleteRow(obj,'boq'+x+'tableBoqrow');
		--subledgerrowcount;
		return true;
	
    
   // resetDebitCodes();
}


function deleteFSRow(r) {
	var i = r.parentNode.parentNode.rowIndex;
	document.getElementById("table").deleteRow(i);
	calculateTotal();
	document.getElementById("boq_div").focus();
}



	







/*function deleteSubledgerRow(obj,x) {
	var rowcount=$("#tableBoq tbody tr").length;
    if(rowcount<=3) {
		bootbox.alert($.i18n.prop('msg.this.row.can.not.be.deleted'));
		return false;
	} else {
		deleteRow(obj,'boq'+x+'tableBoqrow');
		--subledgerrowcount;
		return true;
	}
    
   // resetDebitCodes();
}*/

function addCustomEvent(index,target,type,func){
	target = target.replace('index',index);
	addCustomEventListener(target, type, func);
}

function addCustomEventListener(target,type,func){
	var targArea = document.getElementById (target);
	if(targArea != null){
		targArea.addEventListener(type,  func);	
	}
}

function shortKeyFunForAddButton (zEvent) {
	var currId = zEvent.target.id;
	if(currId.startsWith('checkList') && zEvent.keyCode == 32){
		zEvent.preventDefault ();
    	addDebitDetailsRow();
    }else if(currId.startsWith('checkList') && zEvent.keyCode == 32){
    	zEvent.preventDefault ();
    	addCreditDetailsRow();
    }
//	$('[data-toggle="tooltip"]').tooltip("hide");
    zEvent.stopPropagation ();
}


function addFileInputField(x) {
	var addressRow = $('boq['+x+'].repeat-address').first();
	var addressRowLength = $('boq['+x+'].repeat-address').length;

	var newAddressRow = addressRow.clone(true).find("input").val("").end();

	$(newAddressRow).find("td input,td select").each(function(index, item) {
		item.name = item.name.replace(/[0-9]/g, addressRowLength);
	});

	newAddressRow.insertBefore(addressRow)
	calculateTotal();
	document.getElementById("boq_div").focus();
}


function contengencyPercentage(estimateamount){
	//var estimateamount=$('#estimatedCost').val();
	if(estimateamount >= 10000000){
		$('#contingentPercentage').val("3");
		
	var percAmount=	((estimateamount/100)*3).toFixed(2);
	$('#contingentAmount').val(percAmount);
	
	}else{
		$('#contingentPercentage').val("5");
		
		var percAmount=	((estimateamount/100)*5).toFixed(2);;
		$('#contingentAmount').val(percAmount);
	}
}



function valueChanged() {
	var rowcount=$(".tableBoq tbody tr").length;
		var estimateAmt = 0;
		var amt = 0;
		for (var i = 0; i <= rowcount; i++) {
			if (document.getElementById("boQDetailsList[" + i + "].rate") !== null
					&& document.getElementById("boQDetailsList[" + i + "].quantity") !== null
					&& document.getElementById("boQDetailsList[" + i + "].quantity") !== "") {

				var rate = document.getElementById("boQDetailsList[" + i + "].rate").value;
				var quantity = document.getElementById("boQDetailsList[" + i + "].quantity").value;
				amt = (quantity * rate).toFixed(2);
				if (document.getElementById("boQDetailsList[" + i + "].amount") != null) {
					document.getElementById("boQDetailsList[" + i + "].amount").value = amt;
				}

			}
		}
		
		calculateTotal();
	}

	



		
		
function calculateTotal() {
	var rowcount=$(".tableBoq tbody tr").length;
		var estimateAmt = 0;
		var rate = 0;
		var quantity = 0;
		var amt = 0;
		
		var contingentAmount = 0;
		var consultantFee = 0;
		var unforseenCharges = 0;

		for (var i = 0; i <= rowcount; i++) {
			// get the selected row index

			if (document.getElementById("boQDetailsList[" + i + "].rate") != null
					&& document.getElementById("boQDetailsList[" + i
							+ "].quantity") != null) {
				rate = document
						.getElementById("boQDetailsList[" + i + "].rate").value;
			}

			if (document.getElementById("boQDetailsList[" + i + "].quantity") != null) {
				quantity = document.getElementById("boQDetailsList[" + i
						+ "].quantity").value;
			}

			if (quantity != 0 && quantity != "" && rate != 0 && rate != "") {
				amt = (quantity * rate).toFixed(2);
				if (document.getElementById("boQDetailsList[" + i + "].amount") != null) {
					document.getElementById("boQDetailsList[" + i + "].amount").value = amt;

					estimateAmt = +estimateAmt + +amt;
				}
			}
		}
		
		
		
		contengencyPercentage(estimateAmt);
		contingentAmount=$('#contingentAmount').val();
		consultantFee=$('#consultantFee').val();
		unforseenCharges=$('#unforseenCharges').val();
		
		if(unforseenCharges==""||unforseenCharges==null||unforseenCharges=='null')
		{unforseenCharges=0;}
		if(consultantFee==""||consultantFee==null||consultantFee=='null')
		{consultantFee=0;}
		
		estimateAmt=	parseFloat(estimateAmt)+ parseFloat(unforseenCharges)+parseFloat(consultantFee)+ parseFloat(contingentAmount);
		
		document.getElementById("estimatedCost").value = estimateAmt.toFixed(2);
	}


function caluclateestamt(){

	var estimateAmt=document.getElementById("estimatedCost").value;
	
		var consultantFee=$('#consultantFee').val();
		var unforseenCharges=$('#unforseenCharges').val();
	if(unforseenCharges==""||unforseenCharges==null||unforseenCharges=='null')
		{unforseenCharges=0;}
		if(consultantFee==""||consultantFee==null||consultantFee=='null')
		{consultantFee=0;}
		
		estimateAmt=	parseFloat(estimateAmt)+ parseFloat(unforseenCharges)+parseFloat(consultantFee);
		
		document.getElementById("estimatedCost").value = estimateAmt;
	 
		contengencyPercentage(estimateAmt);

}

