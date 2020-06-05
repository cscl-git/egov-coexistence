<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>
<form:form role="form" action="contractorSearch" modelAttribute="apnimandiContractor"
	id="librarysearchform"
	cssClass="form-horizontal form-groups-bordered"
	enctype="multipart/form-data">
	<div class="row">
		<div class="col-md-12">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
					<div class="panel-title"><spring:message code="title.contractor.search" /></div>
				</div>
				<div class="panel-body">
					<div class="form-group">
						<label class="col-sm-2 control-label text-right">
							<spring:message code="lbl.zone" /> <span class="mandatory"></span>: 
						</label>
						<div class="col-sm-3 add-margin">
							<form:select path="zone" id="zone" cssClass="form-control" required="required" cssErrorClass="form-control error">
								<form:option value="">
									<spring:message code="lbl.select" />
								</form:option>
								<form:options items="${zoneMasters}" itemValue="id" itemLabel="name" />
							</form:select>
							<form:errors path="zone" cssClass="error-msg" />
						</div>
						<label class="col-sm-2 control-label text-right">
							<spring:message code="lbl.name" />:
						</label>
						<div class="col-sm-3 add-margin">
							<form:input type="text" path="name" id="name" cssClass="form-control text-left patternvalidation"
									data-pattern="alphabetwithspace" maxlength="100"/>
						<form:errors path="name" cssClass="error-msg" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-2 control-label text-right">
							<spring:message code="lbl.valid.from.date" />:
						</label>
						<div class="col-sm-3 add-margin">
							<form:input path="validFromDate" class="form-control datepicker"
										title="Please enter a valid date" pattern="\d{1,2}/\d{1,2}/\d{4}" id="validFromDate"
										data-inputmask="'mask': 'd/m/y'"/>
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
						<label class="col-sm-2 control-label text-right">
							<spring:message code="lbl.active" />:
						</label>
						<div class="col-sm-3 add-margin">
							<form:select path="active" id="active" cssClass="form-control" cssErrorClass="form-control error">
								<form:option value="true">YES</form:option>
								<form:option value="false">NO</form:option>
								<form:errors path="active" cssClass="error-msg" />
							</form:select>
						</div>
						<label class="col-sm-2 control-label text-right">
							<spring:message code="lbl.status" />:
						</label>
						<div class="col-sm-3 add-margin">
							<form:select path="statusCode" id="statusCode" cssClass="form-control" cssErrorClass="form-control error">
								<form:option value=""><spring:message code="lbl.select" /></form:option>
								<form:option value="SDCCREATED">Created</form:option>
								<form:option value="JECONTRACTORAPPROVED">Junior Engineer Approved</form:option>								
								<form:option value="APPROVED">Superintendent Approved</form:option>
								<form:option value="REJECTED">Rejected</form:option>
								<form:option value="CONTRACTTERMINATED">Contract Terminated</form:option>
								<form:option value="DELETED">Deleted</form:option>
								<form:option value="RESUBMITTED">Resubmitted</form:option>
								<form:errors path="statusCode" cssClass="error-msg" />
							</form:select>							
						</div>
					</div>
					<div class="form-group">
						<div class="text-center">
							<button type='button' class='btn btn-primary' id="btnContractorSearch">
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
		<div id="defaultResultHeader" class="col-sm-12 col-md-12 text-left" style="display: block;">
			<spring:message code="lbl.contractor.search.result" />
		</div>
		<div id="postResultHeader" class="col-sm-12 col-md-12" style="display: none;">
			<div id="postResultLabel" class="col-sm-6 col-md-6 text-left">
				<spring:message code="lbl.contractor.search.result" />
			</div>
			<div id="postResultButton" class="col-sm-6 col-md-6 text-right">
				<input type="hidden" id="selectedZone" name="selectedZone"/>
				<button type='button' class='btn btn-primary' id="btnContractorAddNew">
					Add New
				</button>
			</div>
		</div>
	</div>
	<div class="col-md-12 form-group report-table-container">
		<table class="table table-bordered table-hover multiheadertbl"
			id="resultTable">
			<thead>
				<tr>
					<th><spring:message code="lbl.zone" /></th>
					<th><spring:message code="lbl.name" /></th>
					<th><spring:message code="lbl.valid.from.date" /></th>
					<th><spring:message code="lbl.valid.to.date" /></th>
					<th><spring:message code="lbl.active" /></th>
					<th><spring:message code="lbl.status" /></th> 
					<th><spring:message code="lbl.actions" /></th>
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
<script type="text/javascript" src="<cdn:url value='/resources/js/app/contractor-helper.js?rnd=${app_release_no}'/>"></script>