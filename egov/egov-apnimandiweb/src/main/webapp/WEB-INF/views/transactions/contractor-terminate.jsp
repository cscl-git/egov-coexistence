<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>

<%@ include file="contractor-details.jsp"%>	
<%@ include file="contractor-documentdetails.jsp"%>	
<jsp:include page="../workflow/applicationhistory-view.jsp" />
<form:form method="post" action="/services/apnimandi/contractor//terminate/contract" modelAttribute="apnimandiContractor"
		   id="wfContractorForm" class="form-horizontal form-groups-bordered" enctype="multipart/form-data">
	<input type="hidden" id="mode"  name="mode" value="${mode}" />		
	<input type="hidden" name="apnimandiContractor" value="${apnimandiContractor.id}" />
	<spring:hasBindErrors name="apnimandiContractor">
		<c:forEach var="error" items="${errors.allErrors}">
			<div style="color: red;">
				<b><spring:message message="${error}" /></b> <br />
			</div>
		</c:forEach>
	</spring:hasBindErrors>	
	<%@ include file="contractor-terminateform.jsp"%>
	<div class="form-group">
		<div class="text-center">
			<button type='submit' class='btn btn-primary' id="buttonSubmit">
				<spring:message code='lbl.terminate.contract' />
			</button>
		</div>
	</div>		
</form:form>

<link rel="stylesheet" href="<cdn:url value='/resources/global/css/bootstrap/bootstrap-datepicker.css' context='/services/egi'/>" />
<script src="<cdn:url value='/resources/global/js/bootstrap/bootstrap-datepicker.js' context='/services/egi'/>"></script>
<script>
$('#buttonSubmit').click(function(e) {
	if ($('form').valid()) {
	} else {
		e.preventDefault();
	}
});
</script>
