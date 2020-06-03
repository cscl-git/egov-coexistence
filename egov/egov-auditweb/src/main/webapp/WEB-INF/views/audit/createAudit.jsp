<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>

<form:form name="auditForm" role="form" method="post" action="save" modelAttribute="auditDetail" id="auditDetail" class="form-horizontal form-groups-bordered" enctype="multipart/form-data">
	<div class="panel-title text-center" style="color: green;">
	       <c:out value="${message}"/>
	        <br/>
	</div>
	<spring:hasBindErrors name="auditDetail">
	       <div class="alert alert-danger"
	            style="margin-top: 20px; margin-bottom: 10px;">
	           <form:errors path="*"/>
	           <br/>
	       </div>
	</spring:hasBindErrors>
	<ul class="nav nav-tabs" id="settingstab">
        <li class="active"><a data-toggle="tab" href="#auditheader"
                              data-tabidx=0><spring:message code="lbl.header" text="Header"/></a></li>
        <li><a data-toggle="tab" href="#checklist" data-tabidx=1><spring:message
                code="lbl.checklist" text="CheckList"/> </a></li>
    </ul>
    <div class="tab-content">
    	<div class="tab-pane fade in active" id="auditheader">
    	<div class="panel panel-primary" data-collapsed="0">
    		<div class="form-group">
    			<label class="col-sm-3 control-label text-right"><spring:message code="lbl.auditnumber" text="Audit Number"/>
					<span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:input class="form-control patternvalidation" data-pattern="alphanumericwithspecialcharacters" id="auditnumber" path="auditNumber" maxlength="50" required="required" />
					<form:errors path="auditNumber" cssClass="add-margin error-msg" />
				</div>
				
				<label class="col-sm-2 control-label text-right"><spring:message code="lbl.schdauditdate"  text="Audit Scheduled Date"/>
				<span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:input id="auditScheduledDate" path="auditScheduledDate" class="form-control datepicker" data-date-end-date="0d" required="required" placeholder="DD/MM/YYYY"/>
					<form:errors path="auditScheduledDate" cssClass="add-margin error-msg" />
				</div>
				<label class="col-sm-2 control-label text-right"><spring:message code="lbl.auditType"  text="Audit Type"/>
				<span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:input class="form-control patternvalidation" data-pattern="alphanumericwithspecialcharacters" id="auditType" path="auditType" readonly="true" value="Pre-Audit" />
				</div>
				<label class="col-sm-2 control-label text-right"><spring:message code="lbl.billDetails"  text="Bill Details"/>
				</label>
				<div class="col-sm-3 add-margin">
					<a href="#" id="sourceLink" onclick=" return openSource('${billSource}');">View Bill</a>
				</div>
    		</div>
    	</div>
    	<input type="hidden" nanme="billId" id="billId" value="${auditDetail.billId}"/>
    	<jsp:include page="billdocument-upload.jsp"/>
    	</div>
    	<div class="tab-pane fade" id="checklist">
    	<jsp:include page="audit-checklist.jsp"/>
    	</div>
    	 <jsp:include page="commonworkflowmatrix-audit.jsp"/>
        <div class="buttonbottom" align="center">
            <jsp:include page="commonworkflowmatrix-button.jsp"/>
        </div>
    </div>
    
</form:form>
<script
        src="<cdn:url value='/resources/app/js/audit/audit.js?rnd=${app_release_no}' context='/services/audit'/>"></script>
<script
        src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
        