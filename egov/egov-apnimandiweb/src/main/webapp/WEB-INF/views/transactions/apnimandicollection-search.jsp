<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>
<form:form role="form" action="ApnimandiCollectionSearch" modelAttribute="apnimandiCollectionDetails"
	id="librarysearchform"
	cssClass="form-horizontal form-groups-bordered"
	enctype="multipart/form-data">
	<div class="row">
		<div class="col-md-12">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
					<div class="panel-title"><spring:message code="title.apnimandicollection.search" /></div>
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
							<spring:message code="lbl.collection.type" /> <span class="mandatory"></span>:
						</label>
						<div class="col-sm-3 add-margin">
							<form:select path="collectiontype" id="collectiontype" cssClass="form-control" required="required" cssErrorClass="form-control error">
								<form:option value=""><spring:message code="lbl.select" /></form:option>
								<form:options items="${apnimandiCollectionTypes}" itemValue="id" itemLabel="name"/>
							</form:select>
							<form:errors path="collectiontype" cssClass="error-msg" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-2 control-label text-left">
							<spring:message code="lbl.collection.month" /> :
						</label>
						<div class="col-sm-3 add-margin">
							<form:select path="collectionForMonth" id="collectionForMonth" cssClass="form-control" cssErrorClass="form-control error">
								<form:option value=""><spring:message code="lbl.select" /></form:option>
								<form:options items="${months}" />
							</form:select>
							<form:errors path="collectionForMonth" cssClass="error-msg" />
						</div>
						<label class="col-sm-2 control-label text-left">
							<spring:message code="lbl.collection.year" /> :
						</label>
						<div class="col-sm-3 add-margin">
							<form:select path="collectionForYear" id="collectionForYear" cssClass="form-control" cssErrorClass="form-control error">
								<form:option value=""><spring:message code="lbl.select" /></form:option>
								<form:options items="${years}" />
							</form:select>
							<form:errors path="collectionForYear" cssClass="error-msg" />
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
								<form:option value="AMCCREATED">Created</form:option>
								<form:option value="APPROVED">Sub Divisional Engineer Approved</form:option>								
								<form:option value="REJECTED">Rejected</form:option>
								<form:option value="AMCREJECTED">Rejected By Initiator</form:option>
								<form:errors path="statusCode" cssClass="error-msg" />
							</form:select>							
						</div>
					</div>
					<div class="form-group">
						<div class="text-center">
							<button type='button' class='btn btn-primary' id="btnCollectionSearch">
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
	<div class="col-md-12 table-header text-left"><spring:message code="lbl.collection.search.result" /></div>
	<div class="col-md-12 form-group report-table-container">
		<table class="table table-bordered table-hover multiheadertbl" id="resultTable">
			<thead>
				<tr>
					<th><spring:message code="lbl.zone" /></th>
					<th><spring:message code="lbl.collection.type" /></th>
					<th><spring:message code="lbl.receipt.no" /></th>
					<th><spring:message code="lbl.collection.month" /></th>
					<th><spring:message code="lbl.collection.year" /></th>
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
<script src="<cdn:url value='/resources/js/app/apnimandicollection-searchhelper.js?rnd=${app_release_no}'/>"></script>