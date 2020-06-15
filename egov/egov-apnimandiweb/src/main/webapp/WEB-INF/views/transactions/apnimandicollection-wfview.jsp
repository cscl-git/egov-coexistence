<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>

<%@ include file="apnimandicollection-view.jsp"%>
<form:form method="post" action="/services/apnimandi/collection/workflow/update" modelAttribute="apnimandiCollectionDetails"
		   id="wfCollectionForm" class="form-horizontal form-groups-bordered" enctype="multipart/form-data">
	<input type="hidden" id="mode"  name="mode" value="${mode}" />
	<input type="hidden" name="apnimandiCollectionDetails" value="${apnimandiCollectionDetails.id}" />
	<jsp:include page="../workflow/commonWorkflowMatrix.jsp" />
	<div class="buttonbottom" align="center">
		<jsp:include page="../workflow/commonWorkflowMatrix-button.jsp" />
	</div>		
</form:form>