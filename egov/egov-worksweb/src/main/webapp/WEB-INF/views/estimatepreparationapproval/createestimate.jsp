<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>


<%-- <form:form name="create-estimate-form" role="form" method="post"
	action="saveestimate1" modelAttribute="estimatePreparationApproval"
	id="estimatePreparationApproval"
	class="form-horizontal form-groups-bordered"
	enctype="multipart/form-data" style="margin-top:-20px;"> --%>
	
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

	<ul class="nav nav-tabs" id="settingstab">

		<li class="active"><a data-toggle="tab" href="#estimatescreen"
			data-tabidx=1><spring:message
					code="title.estimate.preparation.create"
					text="Estimate Preparation & Approval" /> </a></li>

		<li><a data-toggle="tab" href="#administration" data-tabidx=2><spring:message
					code="title.estimate.administration.approval"
					text="Administration Approval" /> </a></li>

	</ul>
	<div class="tab-content">

		<c:if test="${estimatePreparationApproval.status.code == 'Created' }">
			<div class="tab-pane fade" id="estimatescreen">
				<jsp:include page="editestimate.jsp" />
			</div>

		</c:if>

		<c:if
			test="${estimatePreparationApproval.status.code == 'Pending for Approval' }">
			<div class="tab-pane fade" id="estimatescreen">
				<jsp:include page="editestimate.jsp" />
			</div>

		</c:if>

		<c:if
			test="${estimatePreparationApproval.status.code == 'AA Initiated' }">
			<div class="tab-pane fade" id="estimatescreen">
				<jsp:include page="viewestimate.jsp" />
			</div>
			<div class="tab-pane fade" id="administration">
				<jsp:include page="adminstration-approval.jsp" />
			</div>
		</c:if>

		<c:if
			test="${estimatePreparationApproval.status.code == 'AA Pending for Approval' }">
			<div class="tab-pane fade" id="estimatescreen">
				<jsp:include page="viewestimate.jsp" />
			</div>
			<div class="tab-pane fade" id="administration">
				<jsp:include page="adminstration-approval.jsp" />
			</div>
		</c:if>

		<c:if
			test="${estimatePreparationApproval.status.code == 'TS Initiated' }">
			<div class="tab-pane fade" id="estimatescreen">
				<%-- <jsp:include page="viewestimate.jsp" /> --%>
				<jsp:include page="boq.jsp" />
			</div>
			<div class="tab-pane fade" id="administration">
				<jsp:include page="administrationapprovalview.jsp" />
			</div>
		</c:if>

		<c:if
			test="${estimatePreparationApproval.status.code == 'TS Pending for Approval' }">
			<div class="tab-pane fade" id="estimatescreen">
				<%-- <jsp:include page="viewestimate.jsp" /> --%>
				<jsp:include page="boq.jsp" />
			</div>
			<div class="tab-pane fade" id="administration">
				<jsp:include page="administrationapprovalview.jsp" />
			</div>
		</c:if>

		<c:if test="${estimatePreparationApproval.status.code == 'Approved' }">
			<div class="tab-pane fade" id="estimatescreen">
				<jsp:include page="viewestimate.jsp" />
			</div>
			<div class="tab-pane fade" id="administration">
				<jsp:include page="administrationapprovalview.jsp" />
			</div>
		</c:if>



		<br> <br>
		 <jsp:include page="../common/commonWorkflowhistory-view.jsp" /> 
		<c:if test="${mode !='view' }">
			<div class="show-row form-group">
				<label class="col-sm-3 control-label text-right"><spring:message
						code="lbl.comments" text="Comments" /></label>
				<div class="col-sm-8 add-margin">
					<form:textarea class="form-control" path="approvalComent"
						id="approvalComent" required="required" />
				</div>
			</div>
			<div class="buttonbottom" align="center">
				<%-- <jsp:include page="commonWorkflowMatrix-button.jsp" /> --%>
				<jsp:include page="../common/commonWorkflowMatrix-button.jsp" />
			</div>
		</c:if>
	</div>

</form:form>
<script
	src="<cdn:url value='/resources/app/js/estimatepreparationapproval/estimate.js?rnd=${app_release_no}' context='/services/works'/>"></script>
<%-- <script
        src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>"></script> --%>
