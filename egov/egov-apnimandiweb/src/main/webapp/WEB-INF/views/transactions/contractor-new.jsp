<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>

<form:form method="post" action="/services/apnimandi/contractor/create" modelAttribute="apnimandiContractor"
		   id="newContractorForm" class="form-horizontal form-groups-bordered" enctype="multipart/form-data">
	<input type="hidden" id="mode"  name="mode" value="${mode}" />
	<%@ include file="contractor-form.jsp"%>
	<%@ include file="contractor-documentdetails.jsp"%>		
	<input type="hidden" name="apnimandiContractor" value="${apnimandiContractor.id}" />
	<jsp:include page="../workflow/dm-contractor-commonWorkflowMatrix.jsp" />
	<div class="buttonbottom" align="center">
		<jsp:include page="../workflow/dm-contractor-commonWorkflowMatrix-button.jsp" />
	</div>		
</form:form>

<link rel="stylesheet" href="<cdn:url value='/resources/global/css/bootstrap/bootstrap-datepicker.css' context='/services/egi'/>" />
<script src="<cdn:url value='/resources/global/js/bootstrap/bootstrap-datepicker.js' context='/services/egi'/>"></script>
<script src="<cdn:url value='/resources/js/app/contractor-helper.js?rnd=${app_release_no}'/>"></script>