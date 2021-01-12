<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>

<form:form name="auditForm" role="form" method="post" action="/services/audit/createAudit/updateauditorOwner" modelAttribute="auditDetail" id="auditDetail" 
class="form-horizontal form-groups-bordered" enctype="multipart/form-data" style="margin-top:-20px;">
	<spring:hasBindErrors name="auditDetail">
	       <div class="alert alert-danger"
	            style="margin-top: 20px; margin-bottom: 10px;">
	           <form:errors path="*"/>
	           <br/>
	       </div>
	</spring:hasBindErrors>
	
    <div class="tab-content">
    	<div class="tab-pane fade in active" id="searcheader">
    	<h3>Update Audit Owner</h3>
    	<div class="panel panel-primary" data-collapsed="0">
    		<div class="form-group" style="padding : 50px 20px 0;">
    			
				
				
				
				<%-- <label class="col-sm-3 control-label text-left-audit"><spring:message code="lbl.auditor"  text="Auditor"/>
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path = "leadAuditorEmpNo" id="leadAuditorEmpNo" class="form-control">
                     <form:option value="">-Select-</form:option>
                  </form:select>
				</div> --%>
				
				
				
				
				
				
    		</div>
    	</div>
    	
    	</div>
    	
       
        <br>
        <br>
        <br>
        <div class="tab-pane fade in active" id="resultheader">
      
	        <div class="panel panel-primary" data-collapsed="0">
	      
	        	<div style="padding: 0 15px;">
				<table class="table table-bordered" id="searchResult">
					<thead>
					<tr>
						<th><spring:message code="lbl.serial" text="SL.No."/></th>
						<th><spring:message code="lbl.auditType" text="Audit Type"/></th>
						<th><spring:message code="lbl.auditnumber" text="Audit Number"/></th>
						<th><spring:message code="lbl.schdauditdate" text="Audit Scheduled Date"/></th>
						<th><spring:message code="lbl.checklist.status" text="Status"/></th>
						<th><spring:message code="lbl.checklist.Ownername" text="Ownername"/></th>
						
					</tr>
					</thead>
`					
					<tbody>
				
						<tr>
							<td>
								1
						    </td>
							<td>
								${auditDetail.auditType }
							</td>
							<td>
							<a href="#" onclick="openAudit('${auditDetail.auditId}')" >
							${auditDetail.auditNumber }</a>
							</td>
							<td>
							${auditDetail.auditScheduledDate }
							</td>
							<td>
							${auditDetail.auditStatus }
							</td>
							<td>
								<form:select path = "leadAuditorEmpNo" id="leadAuditorEmpNo" class="form-control">
				                     <form:option value="${auditDetail.leadAuditorEmpNo}" label="${auditDetail.leadAuditorName}"></form:option>
				                     	<form:options items="${approverList}" itemValue="user.id"
									itemLabel="user.name" /> 
				                  </form:select>
								
							</td>
							<form:hidden path="stateId" id="stateId" value="${auditDetail.stateId}"/>
							
						</tr>
						
					<tbody>
				
								
				</table>
				</div>
			<br>
			<br>
	        </div>
	     <div class="buttonbottom" align="center">
        <input type="submit" id="search" class="btn btn-primary btn-wf-primary" name="search"  value="Update"/>
        </div>
	        
        </div>
    </div>
    
</form:form>
<script
        src="<cdn:url value='/resources/app/js/audit/audit.js?rnd=${app_release_no}' context='/services/audit'/>"></script>
<script
        src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
        