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


	
<form:form name="create-estimate-form" role="form" method="post"
	action="updateDnit" modelAttribute="estimatePreparationApproval"
		id="estimatePreparationApproval1" enctype="multipart/form-data">
	
	

	<ul class="nav nav-tabs" id="settingstab">

		<li class="active"><a data-toggle="tab" href="#estimatescreen"
			data-tabidx=0><spring:message
					code="title.dnit.preparation.create" text="DNIT Details" /> </a></li>
	</ul>
	

	<div class="tab-content">
	<div class="tab-pane fade in active" id="estimatescreen">
			
		<jsp:include page="viewestimate.jsp" />
			
		</div>

		<br>
		<jsp:include page="fileupload.jsp" />
		<br> <br>
			

			
	<jsp:include page="../common/commonWorkflowhistory-view.jsp" /> 
		<br> <br>
		
	</div>

</form:form>
