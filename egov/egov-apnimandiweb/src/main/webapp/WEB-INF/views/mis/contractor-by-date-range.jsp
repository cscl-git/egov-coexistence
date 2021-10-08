<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>
<form:form role="form" action="contractorReportbyDate" modelAttribute="apnimandiContractor" id="contractorReportbyDate"
		   cssClass="form-horizontal form-groups-bordered" enctype="multipart/form-data">
	<div class="row">
		<div class="col-md-12">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
					<div class="panel-title"><spring:message code="title.dm.contractor.report.date" /></div>
				</div>
				<div class="panel-body">
					<div class="form-group">
						<label class="col-sm-2 control-label text-right">
							<spring:message code="lbl.valid.from.date" /> <span class="mandatory"></span>:
						</label>
						<div class="col-sm-3 add-margin">
							<form:input path="validFromDate" class="form-control datepicker"
										title="Please enter a valid date" pattern="\d{1,2}/\d{1,2}/\d{4}" id="validFromDate"
										data-inputmask="'mask': 'd/m/y'" required="required"/>
							<form:errors path="validFromDate" cssClass="add-margin error-msg" />
						</div>
						<label class="col-sm-2 control-label text-right">
							<spring:message code="lbl.valid.to.date" />:
						</label>
						<div class="col-sm-3 add-margin">
							<form:input path="validToDate" class="form-control datepicker"
										title="Please enter a valid date" pattern="\d{1,2}/\d{1,2}/\d{4}" id="validToDate"
										data-inputmask="'mask': 'd/m/y'"/>
							<form:errors path="validToDate" cssClass="add-margin error-msg" />
						</div>
					</div>					
					<div class="form-group">
						<div class="text-center">
							<button type='button' class='btn btn-primary' id="btnContractorReportbyDate">
								<spring:message code='lbl.search' />
							</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</form:form>
<div class="row display-hide report-section">
	<div class="col-md-12 table-header">
		<div class="col-sm-12 col-md-12 text-left">
			<spring:message code="lbl.contractor.search.result" />
		</div>
	</div>
	<div class="col-md-12 form-group report-table-container">
		<table class="table table-bordered table-hover multiheadertbl"
			id="resultTable">
			<thead>
				<tr>
					<th><spring:message code="lbl.zone" /></th>
					<th><spring:message code="lbl.name" /></th>
					<th><spring:message code="lbl.contract.signed.on" /></th>
					<th><spring:message code="lbl.valid.from.date" /></th>
					<th><spring:message code="lbl.valid.to.date" /></th>
					<th><spring:message code="lbl.status" /></th> 
				</tr>
			</thead>
		</table>
	</div>
</div>
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css' context='/services/egi'/>" />
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css' context='/services/egi'/>">
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/bootstrap/bootstrap-datepicker.css' context='/services/egi'/>" />
<script type="text/javascript" src="<cdn:url value='/resources/global/js/bootstrap/bootstrap-datepicker.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.tableTools.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/TableTools.min.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.validate.min.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/js/app/contractor-report-helper.js?rnd=${app_release_no}'/>"></script>