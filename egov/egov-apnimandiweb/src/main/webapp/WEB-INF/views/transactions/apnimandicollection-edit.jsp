<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>

<form:form method="post" action="/services/apnimandi/collection/update" modelAttribute="apnimandiCollectionDetails"
		   id="newCollectionForm" class="form-horizontal form-groups-bordered" enctype="multipart/form-data">
	<input type="hidden" id="mode"  name="mode" value="${mode}" />
	<%@ include file="apnimandicollection-form.jsp"%>
	<%@ include file="apnimandicollection-documentdetails.jsp"%>
	<jsp:include page="../workflow/applicationhistory-view.jsp" />
	<input type="hidden" name="apnimandiCollectionDetails" value="${apnimandiCollectionDetails.id}" />
	<div class="form-group">
		<div class="text-center">
			<button type='submit' class='btn btn-primary' id="buttonSubmit">
				<spring:message code='lbl.submit' />
			</button>
		</div>
	</div>		
</form:form>

<link rel="stylesheet" href="<cdn:url value='/resources/global/css/bootstrap/bootstrap-datepicker.css' context='/services/egi'/>" />
<script src="<cdn:url value='/resources/global/js/bootstrap/bootstrap-datepicker.js' context='/services/egi'/>"></script>
<script src="<cdn:url value='/resources/js/app/apnimandicollection-edit.js?rnd=${app_release_no}'/>"></script>
<script src="<cdn:url value='/resources/js/app/apnimandicollection-helper.js?rnd=${app_release_no}'/>"></script>