
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading custom_form_panel_heading">
		<div class="panel-title">
			${checkListDesc}
		</div>
	</div>
	
	<div style="padding: 0 15px;">
	
		<table class="table table-bordered" id="tblchecklist">
			<thead>
				<tr>
					<th><spring:message code="lbl.checklist.status" text="Status"/></th>
					<th><spring:message code="lbl.checklist.date" text="Date"/></th>
					<th><spring:message code="lbl.checklist.auditcomments" text="Auditor Comments"/></th>
					<th><spring:message code="lbl.checklist.usercomments" text="Department Comments"/></th>
				</tr>
			</thead>
			<tbody>
			<c:if test="${history != null && !history.isEmpty()}">
			<c:forEach items="${history}" var="audit" varStatus="status">
			<tr>
			<td>
				${audit.status}
			</td>
			<td>
				${audit.checklist_date}
			</td>
			<td>
				${audit.auditor_comments}
			</td>
			<td>
				${audit.user_comments}
			</td>
			
			</tr>
			</c:forEach>
			</c:if>
			<c:if test="${history == null || history.isEmpty()}">
				No History available
			</c:if>
			</tbody>
		</table>
	</div>
	<div class="text-center"><input type="button" name="button2" id="button2" value="Close" class="btn btn-default" onclick="window.parent.postMessage('close','*');window.close();"/></div>
</div>