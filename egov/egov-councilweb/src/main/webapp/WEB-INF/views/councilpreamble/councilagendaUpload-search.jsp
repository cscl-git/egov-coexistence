<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>


<link rel="stylesheet"
	href="<cdn:url value='/resources/app/css/council-style.css?rnd=${app_release_no}'/>" />
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css' context='/services/egi'/>" />
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css' context='/services/egi'/>">
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js' context='/services/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js' context='/services/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.tableTools.js' context='/services/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/TableTools.min.js' context='/services/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/bootstrap/typeahead.bundle.js' context='/services/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.inputmask.bundle.min.js' context='/services/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.validate.min.js' context='/services/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/datetime-moment.js' context='/services/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/app/js/councilAgendaUploadHelper.js?rnd=${app_release_no}'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/app/js/common-util-helper.js?rnd=${app_release_no}'/>"></script>
</head>
<body>

<form:form role="form" action="searchagendaupload" modelAttribute="councilAgendaUpload"
	id="councilAgendaUploadform"
	cssClass="form-horizontal form-groups-bordered"
	enctype="multipart/form-data">
	
	<jsp:include page="../councilpreamble/councilAgendaUpload-search-form.jsp" />
	
</form:form>

<%-- <div class="row display-hide report-section">
	<div class="col-md-12 table-header text-left">
		<spring:message code="title.agenda.search.result" />
	</div>
	<div class="col-md-12 form-group report-table-container">
	<h3>Agenda Search Result</h3> --%>
<c:choose>
	<c:when test="${agendaUploadR != null}">
	<table class="table table-bordered table-hover multiheadertbl">
	<thead>
		<tr>
			<th>Id</th>
			<th>Department</th>
			<th>Date</th>
			<th>File</th>
		</tr>
	</thead>
		<c:forEach var="CouncilAgendaUpload" items="${agendaUploadR}">
			<tr>
				<td>${CouncilAgendaUpload.id }</td>
				<td>${CouncilAgendaUpload.departmentName }</td>
				<td>${CouncilAgendaUpload.createdDate }</td>
				<td>
				<a href="/services/council/agendaUpload/downloadfile/${CouncilAgendaUpload.filestoreid.fileStoreId}"
									data-gallery target="_blank">${CouncilAgendaUpload.filestoreid.fileName}</a>
				</td>
			</tr>
		</c:forEach>
	</table>
</c:when>
<c:otherwise>
  </c:otherwise>
</c:choose>
<!-- 	</div>
</div>  -->
<%-- <div class="row display-hide report-section">
	<div class="col-md-12 table-header text-left">
		<spring:message code="title.agenda.search.result" />
	</div>
	<div class="col-md-12 form-group report-table-container">
		<table class="table table-bordered table-hover multiheadertbl"
			id="resultTable">
			<thead>
				<tr>
					<th><spring:message code="lbl.serial.no" /></th>
					<th><spring:message code="lbl.department" /></th>
					<th><spring:message code="lbl.view" /></th>
					<th>&nbsp;</th>
				</tr>
			</thead>
		</table>
	</div>
</div> 
 --%>


</body>
</html>

<script>
	$('#btnsearch').click(function(e) {
		if ($('form').valid()) {
		} else {
			e.preventDefault();
		}
	});
</script>
