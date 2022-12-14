<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>

<form:form name="auditForm" role="form" method="post"
	action="searchRetrenchment" modelAttribute="auditDetail"
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
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
					<div class="panel-title" style="text-align: center;">
						Retrenchment Register Report
					</div>
				</div>	
				<div class="panel-body">
				<!-- <div class="form-group" style="padding: 50px 20px 0;"> -->
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.dateFrom" text="Bill Date From" /> <span
						class="mandatory"></span> </label>
					<div class="col-sm-3 add-margin">
						<form:input id="fromDate" path="fromDate"
							class="form-control datepicker" data-date-end-date="0d" onkeyup="DateFormat(this,this.value,event,false,'3')" 
							required="required" placeholder="DD/MM/YYYY" data-inputmask="'mask': 'd/m/y'" autocomplete="off" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.dateTo" text="Bill Date To" /> <span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:input id="toDate" path="toDate"
							class="form-control datepicker" data-date-end-date="0d" required="required" placeholder="DD/MM/YYYY" />
					</div>
					<label class="col-sm-3 control-label text-left-audit">
					<spring:message code="lbl.department" text="Department" />
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="department" id="department"
							class="form-control">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:options items="${auditDetail.departments}" itemValue="name"
								itemLabel="name" />
						</form:select>
					</div>
				</div>
			</div>
		</div>
		<div class="buttonbottom" align="center">
			<input type="submit" id="search"
				class="btn btn-primary btn-wf-primary" name="search" value="Search" />
		</div>
		<br> <br> <br>
</form:form>
<!-- Result Table -->

	<div class="panel panel-primary" data-collapsed="0">
		<div class="panel-heading">
			<div class="panel-title">
				<spring:message code="asset-search-result" text="Search Result" />
			</div>
		</div>
		<div class="panel-body">
			<table class="table table-bordered" id="resultHeader">
				<thead>
					<tr>
						<th><spring:message code="lbl-sl-no" text="Sr. No." /></th>
						<th><spring:message code="date" text="Date" /></th>
						<th><spring:message code="deptName" text="Department Name" /></th>
						<th><spring:message code="billAmt" text="Amount of Bill" /></th>
						<th><spring:message code="amtPassedByAudit" text="Amount Passed by the Audit" /></th>
						<th><spring:message code="amtRetrenched" text="Amount Retrenched" /></th>
						<th><spring:message code="detailOfBill" text="Detail of the Bill" /></th>
						<th><spring:message code="auditorSignature" text="Signature of the Auditor" /></th>
						<th><spring:message code="rsaSignature" text="Signature of RSA/Examiner" /></th>
						<th><spring:message code="remarks" text="Remarks" /></th>
						<th><spring:message code="status" text="Status" /></th>
					</tr>
				</thead>
				<tbody>
				<c:if test="${auditDetail.retrachmentDetailsList != null && auditDetail.retrachmentDetailsList.size()>0}">
					<c:forEach items="${auditDetail.retrachmentDetailsList }" var="ret" varStatus="item">
						<tr>
							<td>${item.index+1 }</td><!-- ${item.index+1 } -->
							<td>${ret.retdate }</td><!-- ${ret.date } -->
							<td>${ret.department_name }</td><!-- ${ret.department_name } -->
							<td>${ret.amountofbill }</td>
							<td>${ret.amountbyaudit }</td>
							<td>${ret.amountretrached }</td>
							<td>${ret.billdetail }</td>
							<td></td>
							<td></td>
							<td>${ret.remarks }</td>
							<td>${ret.status}</td>
						</tr>
					</c:forEach>
				</c:if>
		
				</tbody>

			</table>
		</div>
	</div>


<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css' context='/services/egi'/>" />
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css' context='/services/egi'/>">
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/buttons.bootstrap.min.css' context='/services/egi'/>">
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/dataTables.buttons.min.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/buttons.bootstrap.min.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/buttons.flash.min.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/jszip.min.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/pdfmake.min.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/vfs_fonts.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/buttons.html5.min.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/buttons.print.min.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.validate.min.js' context='/services/egi'/>"></script>

<style>
@media ( max-width : 768px) {
	.table-bordered tbody>tr {
		border-bottom: 1px solid #ebebeb;
	}
}

@media ( max-width : 768px) {
	.table-bordered tbody>tr td {
		border: none;
	}
}

</style>

<script>
	$(document).ready(function() {
		$('#resultHeader').DataTable({
			dom : 'Bfrtip',
			aaSorting : [],
			buttons : [ 'excel', 'print' ]
		});
	});
	
</script>