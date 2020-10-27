<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>
<script
        src="<cdn:url value='/resources/js/estimateworks.js?rnd=${app_release_no}' context='/services/works'/>"></script>


	
		<form:form name="create-estimate-form" role="form"
		method="post" action="saveestimate1"
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
					code="title.estimate.preparation.create"
					text="Estimate Details" /> </a></li>
	<c:if test="${estimatePreparationApproval.status.code == 'AA Initiated' || estimatePreparationApproval.status.code == 'AA Pending for Approval' || estimatePreparationApproval.status.code == 'TS Initiated' || estimatePreparationApproval.status.code == 'TS Pending for Approval' || estimatePreparationApproval.status.code == 'Approved'}">
		<li><a data-toggle="tab" href="#administration" data-tabidx=1><spring:message
					code="title.estimate.administration.approval"
					text="Administration Approval" /> </a></li>
	</c:if>
	</ul>
	<div class="tab-content">
	<div class="tab-pane fade in active" id="estimatescreen">
	
		
			
				<jsp:include page="viewestimate.jsp" />
			
		

		

	
	
	</div>
	<div class="tab-pane fade" id="administration">

				<jsp:include page="administrationapprovalview.jsp" />
	</div>	

		<br><br><br>
		<jsp:include page="fileupload.jsp" />
		<br> <br>
		 <jsp:include page="../common/commonWorkflowhistory-view.jsp" /> 
		 <br>
		 <br>
		
	</div>

</form:form>
