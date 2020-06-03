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
					<th><spring:message code="lbl.checklist.severity" text="Severity"/></th>
					<th><spring:message code="lbl.checklist.usercomments" text="User Comments"/></th>
					<th><spring:message code="lbl.checklist.auditcomments" text="Auditor Comments"/></th>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${auditDetail.checkList}" var="audit" varStatus="status">
			<tr>
			<td>
			<input type="hidden" name="checkList[${status.index}].checklist_description" id="checkList[${status.index}].checklist_description" value="${audit.checklist_description}">
			${audit.checklist_description}
			</td>
			<td>
			<select name="checkList[${status.index}].status" id="checkList[${status.index}].status" value="${audit.status}" class="form-control">
			<option value="">-Select-</option>
			<option value="Pass">Pass</option>
			<option value="Fail">Fail</option>
			</select>
			</td>
			<td>
			<select name="checkList[${status.index}].severity" id="checkList[${status.index}].severity" value="${audit.severity}" class="form-control">
			<option value="">-Select-</option>
			<option value="High">High</option>
			<option value="Medium">Medium</option>
			<option value="Low">Low</option>
			</select>
			</td>
			<td>
			<textarea value="${audit.user_comments}" name="checkList[${status.index}].user_comments" id="checkList[${status.index}].user_comments" class="form-control" maxlength="200" ></textarea>
			</td>
			<td>
			<textarea value="${audit.auditor_comments}" name="checkList[${status.index}].auditor_comments" id="checkList[${status.index}].auditor_comments" class="form-control" maxlength="200" ></textarea>
			</td>
			</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>
</div>