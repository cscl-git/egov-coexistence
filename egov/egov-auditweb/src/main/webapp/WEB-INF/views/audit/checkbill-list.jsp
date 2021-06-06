<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>



<div class="tab-content">
   <div class="tab-pane fade in active" id="">
    <h3>Add Check List</h3>
    <div class="panel panel-primary" data-collapsed="0">
    	 
      	<c:if test="${billTypeCheckListdata.messege != null && !billTypeCheckListdata.messege.isEmpty()}">
	      <div class="alert alert-success alert-dismissible" role="alert">
		  <button type="button" class="close" data-dismiss="alert" aria-label="Close">
			<span aria-hidden="true">&times;</span>
		 </button>
		 <strong>Message !</strong>${billTypeCheckListdata.messege}
	     </div> 
	   </c:if>
    	 
    	 <div class="form-group" style="padding : 50px 20px 0;">
    	 
    	   <form:form name="showBillCheckList" role="form" method="post"
		       action="showBillCheckList" modelAttribute="billTypeCheckListdata"
	           id="showBillCheckList" class="form-horizontal form-groups-bordered" enctype="multipart/form-data"
	           style="margin-top:-20px;">
	           
	           <label class="col-sm-3 control-label text-left-audit"><spring:message code="lbl.bill.type" text="Bill Type"/>
			      <span class="mandatory"></span>
		         </label>
		         <div class="col-sm-3 add-margin">
			      <form:select data-first-option="false" onchange="showBillCheckList.submit()" id="billType" path="billType" class="form-control" required="required">
				    <option value=""><spring:message code="lbl.select" text="Select"/>
				    <c:forEach var="billtypelist" items="${billSubTypes}">
				    <option value="${billtypelist.name}">${billtypelist.name}</option>
                    </c:forEach>
                    <c:if test="${billTypeCheckListdata.billType != null && !billTypeCheckListdata.billType.isEmpty()}">
				    <option selected value="${billTypeCheckListdata.billType}">${billTypeCheckListdata.billType}</option>
				    </c:if>
			      </form:select>
		         </div>
		         
		         <label class="col-sm-3 control-label text-left-audit">
		         </label>
		         <div class="col-sm-3 add-margin">
		         </div>
		         
	       </form:form>   
	       <br><br>
	       <div class="panel-body">
	       
	       <form:form name="showBillDescriptionList" role="form" method="post"
		       action="showBillDescriptionList" modelAttribute="billTypeCheckListdata"
	           id="showBillDescriptionList" class="form-horizontal form-groups-bordered" enctype="multipart/form-data"
	           style="margin-top:-20px;">
	           <input type="hidden" name="billType" value="${billTypeCheckListdata.billType}" required="required">
	         <table class="table table-bordered" id="tbl_posts">
		       <thead>
			     <tr>
				   <th><spring:message code="lbl.bill.type.description" text="Bill Type Description"/></th>
				   <th><spring:message code="lbl.action" text="Action"/></th>
			     </tr>
		       </thead>
		       <c:if test="${billTypeCheckListdata.billType != null && !billTypeCheckListdata.billType.isEmpty()}">
		       <tbody id="tbl_posts_body">
		         <c:if test="${billTypeCheckListdata.billTypeCheckLists != null &&  !billTypeCheckListdata.billTypeCheckLists.isEmpty()}">
		         <c:forEach items="${billTypeCheckListdata.billTypeCheckLists}" var="billTypeCheckList" varStatus="status">
		           <tr id="rec-${status.index}">
				    <td>
				    <input type="hidden" name="billTypeCheckLists[${status.index}].id" value="${billTypeCheckList.id}" class="form-control"/>
		            <input type="hidden" name="billTypeCheckLists[${status.index}].billType" value="${billTypeCheckList.billType}" class="form-control"/>
				    <input type="text" name="billTypeCheckLists[${status.index}].billtypedescrip" value="${billTypeCheckList.billtypedescrip}" required readonly maxlength="3000" id="ed-${status.index}" class="form-control"/>
			        </td>
			        <td class="text-center">
				     <span class="add-padding">
				      <i class="fa fa-edit tdEnable"  style="color:black;" aria-hidden="true" data-toggle="tooltip" title="" data-original-title="Edit!"></i>
				     </span> 
				     <span class="add-padding">
				       <a class="fa fa-trash" style="color:black;" href="#" data-href="<spring:url value="/checkListMaster/deleteBillDescription/${billTypeCheckList.id}"/>" 
				             data-toggle="modal" data-target="#confirm-delete" data-original-title="Delete!"></a>
				     </span>
				    </td>
				   </tr>
				 </c:forEach>
				 </c:if>
				   <tr id="rec-${len}">
		             <td>
		               <input type="hidden" name="billTypeCheckLists[${len}].id" class="form-control"/>
		               <input type="hidden" name="billTypeCheckLists[${len}].billType" class="form-control"/>
		               <input type="text" name="billTypeCheckLists[${len}].billtypedescrip" maxlength="3000" required class="form-control"/>
				      </td>
				      <td class="text-center">
				       <span style="cursor:pointer;" tabindex="0" class="Add" data-toggle="tooltip" title="" data-original-title="" aria-hidden="true">
				        <i class="fa fa-plus"></i>
				       </span>
				       <span class="add-padding">
				        <i class="fa fa-trash Remove-row" data-id="${len}" aria-hidden="true" data-toggle="tooltip" title="" data-original-title="Delete!"></i>
				       </span>
				      </td>
		            </tr>
		          </tbody>
		          </c:if> 
		         </table>
		         <c:if test="${billTypeCheckListdata.billType != null && !billTypeCheckListdata.billType.isEmpty()}">
	            <div class="buttonbottom" align="center">
			      <input type="submit" id="billtypedescripSave" class="btn btn-primary"
					name="billtypedescripSave" code="lbl.bill.type.description" value="Save" />
				</div>
				</c:if>
	       </form:form> 
	       
	       </div>
	   
    	 </div>
    	 
    </div>
   </div>
</div>


 <div class="modal fade" id="confirm-delete" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
       <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
             <h4 class="modal-title" id="myModalLabel">Confirm Delete</h4>
        </div>
        <div class="modal-body">
          <p>Do you want to proceed?</p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            <a class="btn btn-danger btn-ok">Delete</a>
        </div>
      </div>
    </div>
</div>

  
  <div class="modal fade" id="confirm-save" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
       <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
             <h4 class="modal-title" id="myModalsave">Confirm Save</h4>
        </div>
        <div class="modal-body">
          <p>Please select Bill Type</p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">OK</button>
        </div>
      </div>
    </div>
</div>
  
  
  <div style="display:none;">
    <table id="sample_table">
    <tr id="">
	<td>
	<input type="hidden" name="billTypeCheckLists[0].id" class="form-control"/>
    <input type="hidden" name="billTypeCheckLists[0].billType"  class="form-control"/>
	<input type="text" name="billTypeCheckLists[0].billtypedescrip"  maxlength="3000" class="form-control customtd"/>
	</td>
    <td class="text-center"><span style="cursor:pointer;" tabindex="0" 
		class="Add" data-toggle="tooltip" title="" data-original-title="" aria-hidden="true">
		<i class="fa fa-plus"></i></span>
		<span class="add-padding">
		<i class="fa fa-trash Remove-row" data-id="0" 
		   aria-hidden="true" data-toggle="tooltip" title="" data-original-title="Delete!"></i></span>
	</td>
    </tr>
   </table>
 </div>
  
  
  
  
	
<script
        src="<cdn:url value='/resources/app/js/audit/audit.js?rnd=${app_release_no}' context='/services/audit'/>"></script>	

	
 <script type="text/javascript">
 
 /* function ValidationEvent(e) {
	  var x = document.getElementById("billType").value;
	  if (x == "") {
	    $('#confirm-save').modal('show');
	    e.preventDefault();
       //return false;
      }
     else{
    	// return;
	  }
	} */
 
 $('#confirm-delete').on('show.bs.modal', function(e) {
     $(this).find('.btn-ok').attr('href', $(e.relatedTarget).data('href'));
 });
 
//function enableEdit(){
	$(".tdEnable").on('click',function(){
	    var currentRow=$(this).closest("#tbl_posts tr");
	    var edid=currentRow.find("td:eq(0) input:eq(2)").attr('id');
		$('#'+edid).prop('readonly', false);
		});
 
 jQuery(document).delegate('.Add', 'click', function(e) {
     e.preventDefault();    
     var content = jQuery('#sample_table tr');
     var size = jQuery('#tbl_posts >tbody >tr').length;
     var element = null;    
     element = content.clone();
     element.attr('id', 'rec-'+size);
     element.find('.customtd').attr('name', "billTypeCheckLists["+size+"].billtypedescrip");
     element.find('.customtd').attr('required', true);
     element.find('.Remove-row').attr('data-id', size);
     element.appendTo('#tbl_posts_body');
   });
   
   jQuery(document).delegate('.Remove-row', 'click', function(e) {
     e.preventDefault();    
     var didConfirm = true;
     if (didConfirm == true) {
      var id = jQuery(this).attr('data-id');
      var targetDiv = jQuery(this).attr('targetDiv');
      jQuery('#rec-' + id).remove();
    return true;
  } else {
    return false;
  }
});
 
</script>
 
	



