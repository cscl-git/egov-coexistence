<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>
<form role="form" action="ApnimandiCollectionSearch" id="librarysearchform" class="form-horizontal form-groups-bordered" enctype="multipart/form-data">
	<div class="row">
		<div class="col-md-12">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
					<div class="panel-title"><spring:message code="title.dm.collection.report" /></div>
				</div>
				<div class="panel-body">	
					<div class="form-group">
						<label class="col-sm-2 control-label text-right">
							<spring:message code="lbl.from.date" /> <span class="mandatory"></span>:
						</label>
						<div class="col-sm-3 add-margin">
							<input id="fromDate" name="fromDate" class="form-control datepicker" title="Please enter a valid date" 
							       pattern="\d{1,2}/\d{1,2}/\d{4}" data-inputmask="'mask': 'd/m/y'" required="required"/>
						</div>
						<label class="col-sm-2 control-label text-right">
							<spring:message code="lbl.to.date" />:
						</label>
						<div class="col-sm-3 add-margin">
							<input id="toDate" name="toDate" class="form-control datepicker" title="Please enter a valid date" 
							       pattern="\d{1,2}/\d{1,2}/\d{4}" data-inputmask="'mask': 'd/m/y'"/>
						</div>
					</div>
					<div class="form-group">
						<div class="text-center">
							<button type='button' class='btn btn-primary' id="btnReportSearch">
								<spring:message code='lbl.search' />
							</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</form>

<div class="row display-hide report-section">
	<div class="col-md-12 table-header text-left"><spring:message code="lbl.collection.search.result" /></div>
	<div class="col-md-12 form-group report-table-container">
		<table class="table table-bordered table-hover multiheadertbl" id="resultTable">
			<thead>
				<tr>
					<th><spring:message code="lbl.zone" /></th>
					<th><spring:message code="lbl.site" /></th>
					<th><spring:message code="lbl.collection.type" /></th>
					<th><spring:message code="lbl.contractor" /></th>
					<th><spring:message code="lbl.collection.month" /></th>
					<th><spring:message code="lbl.collection.year" /></th>
					<th><spring:message code="lbl.collection.date" /></th>
					<th><spring:message code="lbl.amount.type" /></th>
					<th><spring:message code="lbl.collection.amount" /></th>
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
<script src="<cdn:url value='/resources/js/app/dm-collection-searchhelper.js?rnd=${app_release_no}'/>"></script>