<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>

<form:form name="auditForm" role="form" method="post"
	action="passUnderObjSearch" modelAttribute="auditDetail"
	id="auditDetail" class="form-horizontal form-groups-bordered"
	enctype="multipart/form-data" style="margin-top:-20px;">
	<spring:hasBindErrors name="auditDetail">
		<div class="alert alert-danger"
			style="margin-top: 20px; margin-bottom: 10px;">
			<form:errors path="*" />
			<br />
		</div>
	</spring:hasBindErrors>

	<div class="tab-content">
		<div class="tab-pane fade in active" id="searcheader">
			<h3>Search Pass Under Objection Audit</h3>
			<div class="panel panel-primary" data-collapsed="0">
				<div class="form-group" style="padding: 50px 20px 0;">

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.audit.dateFrom" text="Audit Date From" /><span
						class="mandatory"></span> </label>
					<div class="col-sm-3 add-margin">
						<form:input id="billFrom" path="billFrom" required="required"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.audit.dateTo" text="Audit Date To" /><span
						class="mandatory"></span> </label>
					<div class="col-sm-3 add-margin">
						<form:input id="billTo" path="billTo" required="required"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.department" text="Department" /> </label>
					<div class="col-sm-3 add-margin">
						<form:select path="department" id="department"
							class="form-control">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:options items="${auditDetail.departments}" itemValue="code"
								itemLabel="name" />
						</form:select>
					</div>
				</div>
			</div>

		</div>

		<div class="buttonbottom" align="center">
			<input type="submit" id="search"
				class="btn btn-primary btn-wf-primary" name="passUnderObjSearch"
				onclick="searchCheck()" value="Search" />
		</div>
		<br> <br> <br>
		<div class="tab-pane fade in active" id="resultheader">
			<h3>Search Result</h3>
			<div class="panel panel-primary" data-collapsed="0">
				<form:hidden path="counter" id="counter" />
				<div style="padding: 0 15px;">
					<table class="table table-bordered" id="searchResult">
						<thead>
							<tr>
								<th><spring:message text="SL.No." /></th>
								<th><spring:message text="Audit Number" /></th>
								<th><spring:message text="Payment Request No." /></th>
								<th><spring:message text="Pass UnderObjecton Date" /></th>
								<th><spring:message text="Pass UnderObjection Comment" /></th>
								<th><spring:message text="Resolution Date" /></th>
								<th><spring:message text="Resolution Comment" /></th>
								<th><spring:message text="Action" /></th>


							</tr>
						</thead>

						<c:if
							test="${auditDetail.auditSearchList != null &&  !auditDetail.auditSearchList.isEmpty()}">
							<tbody>
								<c:forEach items="${auditDetail.auditSearchList}" var="result"
									varStatus="status">
									<tr>
										<td>${ status.index+1}</td>
										<td>${result.auditno }</td>
										<td>${result.paymentReqNumber}</td>
										<td>${result.passunderobjectiondate }</td>
										<td>${result.passunderobjectioncomment }</td>
										<td>${result.resolutionDate }</td>
										<td>${result.resolutionComment }</td>
										<td><a href='javascript:void(0)'
											onclick="editPassUnderObj(${result.id })">Edit</a></td>

									</tr>
								</c:forEach>
							<tbody>
						</c:if>
						<c:if
							test="${auditDetail.auditSearchList == null ||  auditDetail.auditSearchList.isEmpty()}">
					No records found
					</c:if>
					</table>

					<c:if
						test="${auditDetail.auditSearchList != null &&  !auditDetail.auditSearchList.isEmpty()}">
						<center>
							<iframe id="txtArea1" style="display: none"></iframe>
							

							<input type="button" id="exportToExcel"
								class="btn btn-primary btn-wf-primary" name="exportToExcel"
								onclick="searchCheck();downloadexcel();" value="Export to Excel" />
						
						</center>
					</c:if>

				</div>
				<br> <br>


			</div>
		</div>
	</div>

</form:form>

<script>
function editPassUnderObj(auditId){

	var url = "/services/audit/createAudit/passUnderObjedit/"+ auditId;
	window.open(url,'','width=900, height=700');

	}
	
</script>

<script
	src="<cdn:url value='/resources/app/js/audit/audit.js?rnd=${app_release_no}' context='/services/audit'/>"></script>
<script
	src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
