<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>
<style>
.table thead > tr > th {
    color: black;
    background-color: #acbfd0;
    vertical-align: top;
}
.table tbody > tr > td {
    color: black;
    vertical-align: top;
}
</style>
<script
        src="<cdn:url value='/resources/js/estimateworks.js?rnd=${app_release_no}' context='/services/works'/>"></script>


	
		<form:form name="create-estimate-form" role="form"
		method="post" action="updateDnit"
		modelAttribute="estimatePreparationApproval"
		id="estimatePreparationApproval" enctype="multipart/form-data">
	
	<spring:hasBindErrors name="estimatePreparationApproval">
		<div class="alert alert-danger"
			style="margin-top: 20px; margin-bottom: 10px;">
			<form:errors path="*" />
			<br />
		</div>
	</spring:hasBindErrors>
<input type="hidden" name="estimatePreparationApproval"
						value="${estimatePreparationApproval.id}" />

	<ul class="nav nav-tabs" id="settingstab">

		<li class="active"><a data-toggle="tab" href="#estimatescreen"
			data-tabidx=0><spring:message
					code="title.dnit.preparation.create"
					text="DNIT Details" /> </a></li>
	</ul>
	<div class="tab-content">
	<div class="tab-pane fade in active" id="estimatescreen">
	<c:if test="${estimatePreparationApproval.status.code == 'Created' }">
			
				<jsp:include page="editestimatenew.jsp" />
			
	</c:if>

	<c:if test="${estimatePreparationApproval.status.code == 'Pending for Approval' }">
			
				<jsp:include page="editestimatenew.jsp" />
	</c:if>

		<c:if test="${estimatePreparationApproval.status.code == 'Approved' }">
			
				<jsp:include page="viewestimate.jsp" />
			
		</c:if>

	
	
	</div>
	

		<br><br><br>
		<jsp:include page="fileupload.jsp" />
		<br> <br>
		 <jsp:include page="../common/commonWorkflowhistory-view.jsp" /> 
		 <br>
		 <br>
		<jsp:include page="../common/commonWorkflowMatrix.jsp" />
				<div class="buttonbottom" align="center">
					<jsp:include page="../common/commonWorkflowMatrix-button.jsp" />
				</div>
	</div>

</form:form>
