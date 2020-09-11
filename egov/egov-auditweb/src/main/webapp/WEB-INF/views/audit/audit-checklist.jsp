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
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${auditDetail.checkList}" var="audit" varStatus="status">
			<tr>
			<td>
			<form:hidden path="checkList[${status.index}].checklist_description" id="checkList[${status.index}].checklist_description"/>
			${audit.checklist_description}
			</td>
			<td>
			<c:if test="${auditDetail.auditStatus == 'Created' || auditDetail.auditStatus == 'Pending with Auditor'}">
				<form:select path="checkList[${status.index}].status" id="checkList[${status.index}].status"  required="required" class="form-control">
				<form:option value="">-Select-</form:option>
				<form:option value="Seen/Checked">Seen/Checked</form:option>
				<form:option value="Incorrect">Incorrect</form:option>
				<form:option value="Not Required">Not Required</form:option>
				</form:select>
			</c:if>
			<c:if test="${auditDetail.auditStatus == 'Pending with Department' || auditDetail.auditStatus == 'Pending with Section Officer' || auditDetail.auditStatus == 'Pending with Examiner'}">
			<form:select path="checkList[${status.index}].status" id="checkList[${status.index}].status" readonly="true"  class="form-control">
				<form:option value="">-Select-</form:option>
				<form:option value="Pass">Pass</form:option>
				<form:option value="Fail">Fail</form:option>
				</form:select>
			</c:if>
			</td>
			
			<c:if test="${auditDetail.auditStatus == 'Created' || auditDetail.auditStatus == 'Pending with Auditor'}">
			<c:if test="${audit.status == 'Seen/Checked' }">
			<td>
				<form:input id="checkList[${status.index}].checklist_date" path="checkList[${status.index}].checklist_date" class="form-control datepicker" data-date-end-date="0d" readonly="true" placeholder="DD/MM/YYYY"/>
			</td>
			</c:if>
			<c:if test="${audit.status != 'Seen/Checked' }">
			<td>
				<form:input id="checkList[${status.index}].checklist_date" path="checkList[${status.index}].checklist_date" class="form-control datepicker" data-date-end-date="0d"  placeholder="DD/MM/YYYY"/>
			</td>
			</c:if>
			
			</c:if>
			<c:if test="${auditDetail.auditStatus == 'Pending with Section Officer' || auditDetail.auditStatus == 'Pending with Examiner'}">
			<td>
			<form:input id="checkList[${status.index}].checklist_date" path="checkList[${status.index}].checklist_date" class="form-control datepicker" data-date-end-date="0d" readonly="true"  placeholder="DD/MM/YYYY"/>
			</td>
			</c:if>
			<c:if test="${auditDetail.auditStatus == 'Created' || auditDetail.auditStatus == 'Pending with Auditor'}">
			<c:if test="${audit.status == 'Seen/Checked' }">
			<td>
				<form:textarea path="checkList[${status.index}].auditor_comments" id="checkList[${status.index}].auditor_comments" readonly="true" class="form-control" maxlength="200" ></form:textarea>
			</td>
			</c:if>
			<c:if test="${audit.status != 'Seen/Checked' }">
			<td>
				<form:textarea path="checkList[${status.index}].auditor_comments" id="checkList[${status.index}].auditor_comments" required="required" class="form-control" maxlength="200" ></form:textarea>
			</td>
			</c:if>		
			
			<td style="display:none;">
			
				<form:textarea path="checkList[${status.index}].user_comments" id="checkList[${status.index}].user_comments" class="form-control"  maxlength="200" ></form:textarea>
			</td>
			</c:if>
			
			<c:if test="${auditDetail.auditStatus == 'Pending with Department' }">
			<td style="display:none;">
				<form:textarea path="checkList[${status.index}].auditor_comments" id="checkList[${status.index}].auditor_comments" required="required" class="form-control" maxlength="200" ></form:textarea>
			</td>
			<c:if test="${audit.status != 'Seen/Checked' }">
			<td>
				<form:textarea path="checkList[${status.index}].user_comments" id="checkList[${status.index}].user_comments" class="form-control" required="required" maxlength="200" ></form:textarea>
			</td>
			</c:if>
			<c:if test="${audit.status == 'Seen/Checked' }">
			<td>
				<form:textarea path="checkList[${status.index}].user_comments" id="checkList[${status.index}].user_comments" class="form-control" readonly="true" maxlength="200" ></form:textarea>
			</td>
			</c:if>
			</c:if>
			<td><a href="#" onclick="return openHistory('${auditDetail.auditId}','${ audit.checkListId}');" style="text-decoration: none">&nbsp;
			<img src="/services/egi/resources/erp2/images/history.png" border="0" /></a>
			</td>
			</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>
</div>