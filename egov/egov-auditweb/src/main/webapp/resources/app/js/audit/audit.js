
$(document).ready(function(){
	 var passUnderObjection=$('#passUnderobjection').val();
	 console.log("pass ::"+passUnderObjection);
	 if(passUnderObjection==0 || passUnderObjection=='null' || passUnderObjection==null){

	 $('input[type=checkbox]').removeAttr('checked');
	 }
	
	
	
});
$('body').on('focus',".datepicker", function(){
    $(this).datepicker();
});

function getLocale(paramName){
	return getCookie(paramName) ? getCookie(paramName) : navigator.language;
}

function getCookie(name){
	let cookies = document.cookie;
	if(cookies.search(name) != -1){
		var keyValue = cookies.match('(^|;) ?' + name + '=([^;]*)(;|$)');
	    return keyValue ? keyValue[2] : null;
	}
}

function openHistory(auditId,checkListId){
	var sourcepath="/services/audit/createAudit/history/"+auditId+"/"+checkListId;
			window.open(sourcepath,'Source','resizable=yes,scrollbars=yes,left=300,top=40, width=900, height=700');  
	}

function searchCheck(){
if($("#passUnderobjection").is(':checked')){
		
		$('#passUnderobjection').val("1");
	}
}

function setWorkFLowAction(name)
{
	console.log(name);
	if(name =='sectionOfficer' && (document.getElementById('auditStatus').value == 'Created' || document.getElementById('auditStatus').value == 'Pending with Auditor' || document.getElementById('auditStatus').value =='Pending with Examiner') )
		{
			if (document.getElementById('leadAuditorEmpNo').value == '-1')
				{
					bootbox.alert("Please select the RSA employee");
					return false;
				}
		}
	document.getElementById('workFlowAction').value=name;
	
if($("#passUnderobjection").is(':checked')){
		
		
		$('#passUnderobjection').val("1");
	}
	
}

function openSource(sourcepath){
	window.open(sourcepath,'Source','resizable=yes,scrollbars=yes,left=300,top=40, width=900, height=700');  
}

function openBill(url){
	window.open(url,'Source','resizable=yes,scrollbars=yes,left=300,top=40, width=900, height=700');  
}

function openVoucher(vid)
{
	var url = "/services/EGF/voucher/preApprovedVoucher-loadvoucherview.action?vhid="+ vid;
	window.open(url,'','width=900, height=700');
}
function openAudit(auditId)
{
	var url = "/services/audit/createAudit/view/"+ auditId;
	window.open(url,'','width=900, height=700');
}

function updateAuditorsScreen(auditId)
{
	
	var type =$('#type').val();
	 
	if(type=='null' || type==null || type==''){
		type='Auditor';
	}
	var url = "/services/audit/createAudit/updateAuditorScreen/"+ auditId+"/"+type;
	window.open(url,'','width=900, height=700');
}

function updateOwner(auditId)
{
	var url = "/services/audit/createAudit/updateauditor/"+ auditId;
	window.open(url,'','width=900, height=700');
}

$('#type').change(function(){
	
	
	var todayDate = new Date(Date.now()).toLocaleString();
	
	var type =$('#type').val();

$.ajax({
url: "/services/audit/createAudit/employee/"+type,     
type: "GET",
contentType:'application/json',
//data: JSON.stringify(jsonData),
success: function (response) {
	console.log("success"+response);
	$('#leadAuditorEmpNo').empty();
	$('#leadAuditorEmpNo').append($("<option value=''>Select from below</option>"));
	$.each(response, function(index, value) {
		//$('#approvalPosition').append($('<option>').text(value.userName+'/'+value.positionName).attr('value', value.positionId));  
		$('#leadAuditorEmpNo').append($('<option>').text(value.user.name).attr('value', value.assignments[0].position));  
	});
	//$('#approvalPosition').val($('#approvalPositionValue').val());
}, 
error: function (response) {
	console.log("failed");
}
});
});



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
	
     
     

	var rowcount = $("#tblchecklist tbody tr").length;
	if (rowcount < 30) {
		if (document.getElementById('tblchecklistRow') != null) {
			addRow('tblchecklist','tblchecklistRow');
			$('#tblchecklist tbody tr:eq('+rowcount+')').find('.checklist_description').val('');
			$('#tblchecklist tbody tr:eq('+rowcount+')').find('.status').val('');
			$('#tblchecklist tbody tr:eq('+rowcount+')').find('.checklist_date').val('');
			$('#tblchecklist tbody tr:eq('+rowcount+')').find('.auditor_comments').val('');
			
			++subledgerrowcount;
			
			addCustomEvent(rowcount,'tempSubLedger[index].addButton','keydown',shortKeyFunForAddButton);
		}
	} else {
		  bootbox.alert($.i18n.prop('msg.limit.reached'));
	}
}

function deleteSubledgerRow(obj) {
	var rowcount=$("#tblchecklist tbody tr").length;
    if(rowcount<=3) {
		bootbox.alert($.i18n.prop('msg.this.row.can.not.be.deleted'));
		return false;
	} else {
		deleteRow(obj,'tblchecklist');
		--subledgerrowcount;
		return true;
	}
    
   // resetDebitCodes();
}


// written by Sonu Prajapati

function addcheckListRowNew() {
    
    var size = $('#tblchecklist >tbody >tr').length;
    if (size < 30) {
    var content = $('#blank_table tr');
    var element = null;   
    
    element = content.clone(true,true);
    element.attr('id', 'rec-'+size);
    element.find('.checklist_description').attr('path', "checkList["+size+"].checklist_description");
    element.find('.checklist_description').val('');
    element.find('.status').attr('path', "checkList["+size+"].status");
    element.find('.status').val('');
    element.find('.checklist_date').attr('path', "checkList["+size+"].checklist_date");
    //element.find('.checklist_date').attr('class', "form-control datepicker checklist_date");
    element.find('.auditor_comment').attr('path', "checkList["+size+"].auditor_commente");
    element.find('.auditor_comment').val('');
    element.find('.user_comments').attr('path', "checkList["+size+"].user_comments");
    element.find('.user_comments').val('');
    element.find('.dataDelete').attr('data-id', size);
    element.find('.dataDelete').attr('data-href', '0,'+size);
    element.appendTo('#tbl_add_body');
    } else {
		  bootbox.alert($.i18n.prop('msg.limit.reached'));
	}
  }

$('#confirm-deleterow').on('show.bs.modal', function(e) {
	$(this).find('#deletebtnok').attr('href', $(e.relatedTarget).data('href'));
	

});

jQuery(document).delegate('.btn-okrow', 'click', function(e) {
    e.preventDefault(); 
    var hrefdata=$('#deletebtnok').attr('href');
    var deleteurl=hrefdata.split(",");
    var rowcount=$('#tblchecklist >tbody >tr').length;
    if(rowcount<=1) {
		bootbox.alert($.i18n.prop('msg.this.row.can.not.be.deleted'));
		return false;
	}else{
   if(deleteurl[0] !== "0"){
    $.ajax({
    	url: "/services/audit/createAudit/deleteAuditchecklist/"+deleteurl[0],     
    	type: "GET",
    	contentType:'application/json',
    	//data: JSON.stringify(jsonData),
    	success: function (response) {
    		console.log("success"+response);
    		 //var id = jQuery(this).attr('data-id');
             jQuery('#rec-' + deleteurl[1]).remove();
             $('#confirm-deleterow').modal('hide');
    	}, 
    	error: function (response) {
    		console.log("failed");
    	}
    	});
      }else{
    	 //var id = jQuery(this).attr('data-id');
         jQuery('#rec-' + deleteurl[1]).remove();
         $('#confirm-deleterow').modal('hide');
    }
	}
});

//End by Sonu Prajapati




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




