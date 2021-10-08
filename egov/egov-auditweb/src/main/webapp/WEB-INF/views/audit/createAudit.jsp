<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>

<form:form name="auditForm" role="form" method="post" action="save" modelAttribute="auditDetail" id="auditDetail" 
class="form-horizontal form-groups-bordered" enctype="multipart/form-data" style="margin-top:-20px;">
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
         <c:if test="${auditDetail.auditType == 'Post-Audit' }">
         <li><a data-toggle="tab" href="#billDetails" data-tabidx=2><spring:message
                code="lbl.billDetails" text="Bill Details"/> </a></li>
         </c:if>      
    </ul>
    <div class="tab-content">
    	<div class="tab-pane fade in active" id="auditheader">
    	<div class="panel panel-primary" data-collapsed="0">
    		<div class="form-group" style="padding : 50px 20px 0;">
    			<label class="col-sm-3 control-label text-left-audit"><spring:message code="lbl.auditnumber" text="Audit Number"/>
					<span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:input class="form-control patternvalidation" data-pattern="alphanumericwithspecialcharacters" id="auditnumber" path="auditNumber" maxlength="50" required="required" readonly="true" />
					<form:errors path="auditNumber" cssClass="add-margin error-msg" />
				</div>
				
				<label class="col-sm-3 control-label text-left-audit"><spring:message code="lbl.schdauditdate"  text="Audit Scheduled Date"/>
				<span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
				<c:if test="${auditDetail.auditStatus == 'Created' }">
					<form:input id="auditScheduledDate" path="auditScheduledDate" class="form-control datepicker" data-date-end-date="0d" required="required" placeholder="DD/MM/YYYY"/>
					<form:errors path="auditScheduledDate" cssClass="add-margin error-msg" />
				</c:if>
				<c:if test="${auditDetail.auditStatus == 'Pending with Department'  || auditDetail.auditStatus =='Pending with Auditor' || auditDetail.auditStatus =='Pending with Section Officer'  || auditDetail.auditStatus =='Pending with Examiner'}">
				<form:input id="auditScheduledDate" path="auditScheduledDate" class="form-control datepicker" data-date-end-date="0d" readonly="true" required="required" placeholder="DD/MM/YYYY"/>
				</c:if>
				</div>
				<label class="col-sm-3 control-label text-left-audit"><spring:message code="lbl.auditType"  text="Audit Type"/>
				<span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:input class="form-control patternvalidation" data-pattern="alphanumericwithspecialcharacters" id="auditType" path="auditType" readonly="true" />
				</div>
				<c:if test="${auditDetail.auditType == 'Pre-Audit' }">
				<label class="col-sm-3 control-label text-left-audit"><spring:message code="lbl.billDetails"  text="Bill Details"/>
				</label>
				<div class="col-sm-3 add-margin">
					<a href="#" id="sourceLink" onclick="return openSource('${billSource}');">View Bill</a>
				</div>
				</c:if>
    		</div>
    	</div>
    	<form:hidden id="billId" path="billId" />
    	<form:hidden id ="auditId" path="auditId" />
    	<form:hidden path="workFlowAction" id="workFlowAction"/>
    	<form:hidden id ="auditStatus" path="auditStatus" />
    	</div>
    	<div class="tab-pane fade" id="checklist">
    	<jsp:include page="audit-checklist.jsp"/>
    	</div>
    	<c:if test="${auditDetail.auditType == 'Post-Audit' }">
	    	<div class="tab-pane fade" id="billDetails">
	    	<jsp:include page="audit-billDetails.jsp"/>
    	</div>
    	</c:if>
    	<jsp:include page="billdocument-upload.jsp"/>
    	<br>
    	<br>
    	<jsp:include page="commonworkflowhistory-view.jsp"/>
    	<c:if test="${mode !='view' }">
    	<c:if test="${auditDetail.auditStatus != 'Pending with Department' }">
    	<div class="show-row form-group">
						<label class="col-sm-3 control-label text-right"><spring:message code="lbl.comments" text="Comments"/></label>
						<div class="col-sm-8 add-margin">
							<form:textarea class="form-control" path="approvalComent"  id="approvalComent" required="required"/>
						</div>
		</div>
		</c:if>
		<c:if test="${auditDetail.auditStatus == 'Pending with Department' }">
		<jsp:include page="commonworkflowmatrix-auditbill.jsp"/>
		</c:if>
        <div class="buttonbottom" align="center">
            <jsp:include page="commonworkflowmatrix-button.jsp"/>
        </div>
        </c:if>
    </div>
    
</form:form>
<script
        src="<cdn:url value='/resources/app/js/audit/audit.js?rnd=${app_release_no}' context='/services/audit'/>"></script>
<script
        src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
 <script
        src="<cdn:url value='/resources/app/js/audit/helper.js?rnd=${app_release_no}' context='/services/audit'/>"></script>       
		
 <script
        src="<cdn:url value='/resources/global/js/egov/inbox.js?rnd=${app_release_no}' context='/services/egi'/>"></script> 		