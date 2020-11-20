
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
	var rowcount=$("#tableBoq tbody tr").length;
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

function deleteSubledgerRow(x) {
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
}

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