<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>
<form:form role="form" action="search" modelAttribute="library"
	id="librarysearchform"
	cssClass="form-horizontal form-groups-bordered"
	enctype="multipart/form-data">
	<div class="row">
		<div class="col-md-12">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
					<div class="panel-title"><spring:message code="title.library.search" /></div>
				</div>
				<div class="panel-body">
					<div class="form-group">
						<label class="col-sm-2 control-label text-right">
							<spring:message code="lbl.document.type" />: 
						</label>
						<div class="col-sm-3 add-margin">
							<form:select path="documentType" id="documentType"
								cssClass="form-control" cssErrorClass="form-control error">
								<form:option value="">
									<spring:message code="lbl.select" />
								</form:option>
								<form:options items="${documentTypeMasters}" itemValue="id"
									itemLabel="documentType" />
							</form:select>
							<form:errors path="documentType" cssClass="error-msg" />
						</div>
						<label class="col-sm-2 control-label text-right">
							<spring:message code="lbl.title" /> :
						</label>
						<div class="col-sm-3 add-margin">
							<form:input path="title" class="form-control text-left patternvalidation" data-pattern="address" maxlength="255" />
							<form:errors path="title" cssClass="error-msg" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-2 control-label text-right">
							<spring:message code="lbl.active" /> :
						</label>
						<div class="col-sm-2 add-margin">
							<form:select path="active" id="active" cssClass="form-control"
								cssErrorClass="form-control error">
								<form:option value="true">YES</form:option>
								<form:option value="false">NO</form:option>
								<form:errors path="active" cssClass="error-msg" />
							</form:select>
						</div>
					</div>
					<input type="hidden" id="mode" name="mode" value="${mode}" />
					<div class="form-group">
						<div class="text-center">
							<button type='button' class='btn btn-primary' id="btnsearch">
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
	<div class="col-md-12 table-header text-left">Library Search Result</div>
	<div class="col-md-12 form-group report-table-container">
		<table class="table table-bordered table-hover multiheadertbl"
			id="resultTable">
			<thead>
				<tr>
					<th><spring:message code="lbl.document.type" /></th>
					<th><spring:message code="lbl.title" /></th>
					<th><spring:message code="lbl.active" /></th>
				</tr>
			</thead>
		</table>
	</div>
</div>
<script>
	$('#btnsearch').click(function(e) {
		if ($('form').valid()) {
		} else {
			e.preventDefault();
		}
	});
</script>
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css' context='/services/egi'/>" />
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css' context='/services/egi'/>">
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.tableTools.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/TableTools.min.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.validate.min.js' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/js/app/libraryHelper.js?rnd=${app_release_no}'/>"></script>