<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading custom_form_panel_heading">
		<div class="panel-title">
			<spring:message code="lbl.checklist" text="CheckList"/>
		</div>
	</div>
	
	<div style="padding: 0 15px;">
		<table class="table table-bordered" id="tblchecklist">
			<thead>
				<tr>
					<th><spring:message code="lbl.checklist.name" text="Name"/></th>
					<th><spring:message code="lbl.checklist.status" text="Status"/></th>
					<c:if test="${auditDetail.auditStatus == 'Pending with Department' }">
					<th style="display:none;"><spring:message code="lbl.checklist.date" text="Date"/></th>
					<th style="display:none;"><spring:message code="lbl.checklist.auditcomments" text="Auditor Comments"/></th>
					<th><spring:message code="lbl.checklist.usercomments" text="User Comments"/></th>
					</c:if>
					<c:if test="${auditDetail.auditStatus == 'Created' || auditDetail.auditStatus == 'Pending with Auditor'}">
					<th><spring:message code="lbl.checklist.date" text="Date"/></th>
					<th ><spring:message code="lbl.checklist.auditcomments" text="Auditor Comments"/></th>
					<th style="display:none;"><spring:message code="lbl.checklist.usercomments" text="Department Comments"/></th>
					</c:if>
					<c:if test="${auditDetail.auditStatus == 'Pending with Section Officer' || auditDetail.auditStatus == 'Pending with Examiner'}">
					<th><spring:message code="lbl.checklist.date" text="Date"/></th>
					<th style="display:none;"><spring:message code="lbl.checklist.auditcomments" text="Auditor Comments"/></th>
					<th style="display:none;"><spring:message code="lbl.checklist.usercomments" text="Department Comments"/></th>
					</c:if>
					<th><spring:message code="lbl.checklist.auditHistory" text="History"/></th>
					<c:if test="${mode !='view' }">
					<c:if test="${auditDetail.auditStatus == 'Created' || auditDetail.auditStatus == 'Pending with Auditor'}">
					<th>Add Row</th>
					</c:if>
					</c:if>
				</tr>
			</thead>
			<tbody id="tbl_posts_body">
			<c:forEach items="${auditDetail.checkList}" var="audit" varStatus="status">
			<tr id="rec-${status.index}">
			<td>
			<c:if test="${auditDetail.auditStatus != 'Created' && auditDetail.auditStatus != 'Pending with Auditor'}">
			<form:hidden path="checkList[${status.index}].checklist_description" id="checkList[${status.index}].checklist_description" class="checklist_description"/>
 			${audit.checklist_description}
 			</c:if>
 			<c:if test="${auditDetail.auditStatus == 'Created' || auditDetail.auditStatus == 'Pending with Auditor'}">
			<form:input path="checkList[${status.index}].checklist_description" id="checkList[${status.index}].checklist_description" value="${audit.checklist_description}"  class="form-control checklist_description" maxlength="200" ></form:input>
			</c:if>
			</td>
			<td>
			<c:if test="${auditDetail.auditStatus == 'Created' || auditDetail.auditStatus == 'Pending with Auditor'}">
				<form:select path="checkList[${status.index}].status" id="checkList[${status.index}].status"  required="required" class="form-control status">
				<form:option value="">-Select-</form:option>
				<form:option value="Seen/Checked">Seen/Checked</form:option>
				<form:option value="Incorrect">Incorrect</form:option>
				<form:option value="Not Required">Not Required</form:option>
				</form:select>
			</c:if>
			<c:if test="${auditDetail.auditStatus == 'Pending with Department' || auditDetail.auditStatus == 'Pending with Section Officer' || auditDetail.auditStatus == 'Pending with Examiner'}">
			<form:select path="checkList[${status.index}].status" id="checkList[${status.index}].status" readonly="true"  class="form-control status">
				<form:option value="">-Select-</form:option>
				<form:option value="Seen/Checked">Seen/Checked</form:option>
				<form:option value="Incorrect">Incorrect</form:option>
				<form:option value="Not Required">Not Required</form:option>
				</form:select>
			</c:if>
			</td>
			
			<c:if test="${auditDetail.auditStatus == 'Created' || auditDetail.auditStatus == 'Pending with Auditor'}">
			<c:if test="${audit.status == 'Seen/Checked' }">
			<td>
				<form:input id="checkList[${status.index}].checklist_date" path="checkList[${status.index}].checklist_date" class="form-control datepicker checklist_date" data-date-end-date="0d" readonly="true" placeholder="DD/MM/YYYY"/>
			</td>
			</c:if>
			<c:if test="${audit.status != 'Seen/Checked' }">
			<td>
				<form:input  id="checkList[${status.index}].checklist_date" path="checkList[${status.index}].checklist_date" class="form-control datepicker checklist_date" data-date-end-date="0d"  placeholder="DD/MM/YYYY"/>
			</td>
			</c:if>
			
			</c:if>
			<c:if test="${auditDetail.auditStatus == 'Pending with Section Officer' || auditDetail.auditStatus == 'Pending with Examiner'}">
			<td>
			<form:input  id="checkList[${status.index}].checklist_date" path="checkList[${status.index}].checklist_date" class="form-control datepicker checklist_date" data-date-end-date="0d" readonly="true"  placeholder="DD/MM/YYYY"/>
			</td>
			</c:if>
			<c:if test="${auditDetail.auditStatus == 'Created' || auditDetail.auditStatus == 'Pending with Auditor'}">
			<c:if test="${audit.status == 'Seen/Checked' }">
			<td>
				<form:input path="checkList[${status.index}].auditor_comments" id="checkList[${status.index}].auditor_comments"  class="form-control auditor_comments" readonly="true" maxlength="200" ></form:input>
			</td>
			</c:if>
			<c:if test="${audit.status != 'Seen/Checked' }">
			<td>
				<form:input path="checkList[${status.index}].auditor_comments" id="checkList[${status.index}].auditor_comments" required="required" class="form-control auditor_comments" maxlength="200" ></form:input>
			</td>
			</c:if>		
			
			<td style="display:none;">
			
				<form:textarea path="checkList[${status.index}].user_comments" id="checkList[${status.index}].user_comments" class="form-control user_comments"  maxlength="200" ></form:textarea>
			</td>
			</c:if>
			
			<c:if test="${auditDetail.auditStatus == 'Pending with Department' }">
			 <td style="display:none;">
				<form:textarea path="checkList[${status.index}].auditor_comments" id="checkList[${status.index}].auditor_comments" required="required" class="form-control auditor_comments" maxlength="200" ></form:textarea>
			</td> 
			<c:if test="${audit.status != 'Seen/Checked' }">
			<td>
				<form:textarea path="checkList[${status.index}].user_comments" id="checkList[${status.index}].user_comments" class="form-control user_comments" required="required" maxlength="200" ></form:textarea>
			</td>
			</c:if>
			<c:if test="${audit.status == 'Seen/Checked' }">
			<td>
				<form:textarea path="checkList[${status.index}].user_comments" id="checkList[${status.index}].user_comments" class="form-control user_comments" readonly="true" maxlength="200" ></form:textarea>
			</td>
			</c:if>
			</c:if>
			<td><a href="#" onclick="return openHistory('${auditDetail.auditId}','${ audit.checkListId}');" style="text-decoration: none">&nbsp;
			<img src="/services/egi/resources/erp2/images/history.png" border="0" /></a>
			</td>
			<c:if test="${mode !='view' }">
			<c:if test="${auditDetail.auditStatus == 'Created' || auditDetail.auditStatus == 'Pending with Auditor'}">
			<td class="text-center">
			    <!-- <span style="cursor:pointer;" onclick="addcheckListRow(this);" tabindex="0" id="tempSubLedger[0].addButton" data-toggle="tooltip" title="" data-original-title="" aria-hidden="true"><i class="fa fa-plus"></i></span> -->
				<%--  <span class="add-padding subledge-delete-row" data-id="${status.index}" onclick="deleteSubledgerRow(this);"><i class="fa fa-trash"  aria-hidden="true" data-toggle="tooltip" title="" data-original-title="Delete!"></i></span> --%>
				<span style="cursor:pointer;" tabindex="0" class="Add" data-toggle="tooltip" title="" data-original-title="" aria-hidden="true">
				        <i class="fa fa-plus"></i>
				</span>
				<span class="add-padding subledge-delete-row">
				       <a class="fa fa-trash dataDelete" data-id="${status.index}" style="color:black;" href="#" data-href="/services/audit/createAudit/deleteAuditchecklist/${audit.checkListId}"
				             data-toggle="modal" data-target="#confirm-delete" data-original-title="Delete!"></a>
				</span>
				 </td>
			
			</c:if>
			</c:if>
			</tr>
			
			</c:forEach>
			</tbody>
		</table>
		<c:if test="${auditDetail.auditStatus == 'Created' || auditDetail.auditStatus == 'Pending with Auditor' || auditDetail.auditStatus == 'Pending with Section Officer' || auditDetail.auditStatus == 'Pending with Examiner'}">
		Pass Under Objection
		<form:checkbox  path="passUnderobjection" id="passUnderobjection" value="${auditDetail.passUnderobjection}"></form:checkbox>
		</c:if>
		
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
        <input type="hidden"  name="rowindex" class="rowindex"/>
          <p>Do you want to proceed?</p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            <button type="button" class="btn btn-danger btn-ok" value="">Delete</button>
        </div>
      </div>
    </div>
</div>





