<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>

<form:form name="auditForm" role="form" method="post" action="searchResult" modelAttribute="auditDetail" id="auditDetail" 
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
    	<h3>Search Audit</h3>
    	<div class="panel panel-primary" data-collapsed="0">
    		<div class="form-group" style="padding : 50px 20px 0;">
    			<label class="col-sm-3 control-label text-left-audit"><spring:message code="lbl.auditType" text="Audit Type"/>
					<span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="auditType" id="auditType"  required="required" class="form-control">
						<form:option value="">-Select-</form:option>
						<form:option value="Pre-Audit">Pre-Audit</form:option>
						<form:option value="Post-Audit">Post-Audit</form:option>
					</form:select>
				</div>
				
				<label class="col-sm-3 control-label text-left-audit"><spring:message code="lbl.auditnumber" text="Audit Number"/>
				</label>
				<div class="col-sm-3 add-margin">
					<form:input class="form-control patternvalidation" data-pattern="alphanumericwithspecialcharacters" id="auditnumber" path="auditNumber" maxlength="50" />
				</div>
				
				<label class="col-sm-3 control-label text-left-audit"><spring:message code="lbl.audit.dateFrom"  text="Audit Date From"/>
				</label>
				<div class="col-sm-3 add-margin">
					<form:input id="billFrom" path="billFrom" class="form-control datepicker" data-date-end-date="0d" placeholder="DD/MM/YYYY"/>
				</div>
				<label class="col-sm-3 control-label text-left-audit"><spring:message code="lbl.audit.dateTo"  text="Audit Date To"/>
				</label>
				<div class="col-sm-3 add-margin">
					<form:input id="billTo" path="billTo" class="form-control datepicker" data-date-end-date="0d"  placeholder="DD/MM/YYYY"/>
				</div>
				<label class="col-sm-3 control-label text-left-audit"><spring:message code="lbl.department"  text="Department"/>
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="department" id="department" class="form-control">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${auditDetail.departments}" itemValue="code" itemLabel="name" />
						</form:select>
				</div>
				<label class="col-sm-3 control-label text-left-audit"><spring:message code="lbl.auditor"  text="Auditor"/>
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path = "leadAuditorEmpNo" id="leadAuditorEmpNo" class="form-control">
                     <form:option value = "-1" label = "Select Auditor"/>
                     <form:options items = "${auditDetail.auditEmployees}" itemValue="empCode" itemLabel="empName" />
                  </form:select>
				</div>
				<label class="col-sm-3 control-label text-left-audit">Pass Under Objection
				</label>
					<div class="col-sm-3 add-margin">
					<form:checkbox  path="passUnderobjection" id="passUnderobjection" value="0" ></form:checkbox>
					
					</div>
    		</div>
    	</div>
    	
    	</div>
    	
        <div class="buttonbottom" align="center">
        <input type="submit" id="search" class="btn btn-primary btn-wf-primary" name="search"  onclick="searchCheck()" value="Search"/>
        </div>
        <br>
        <br>
        <br>
        <div class="tab-pane fade in active" id="resultheader">
        <h3> Search Result</h3>
	        <div class="panel panel-primary" data-collapsed="0">
	        <form:hidden path="counter" id="counter" />
	        	<div style="padding: 0 15px;">
				<table class="table table-bordered" id="searchResult">
					<thead>
					<tr>
						<th><spring:message code="lbl.serial" text="SL.No."/></th>
						<th><spring:message code="lbl.auditType" text="Audit Type"/></th>
						<th><spring:message code="lbl.auditnumber" text="Audit Number"/></th>
						<th><spring:message code="lbl.billNumber" text="Bill Number"/></th>
						<th><spring:message code="lbl.schdauditdate" text="Audit Scheduled Date"/></th>
						<th><spring:message code="lbl.checklist.status" text="Status"/></th>
						<th><spring:message code="lbl.pendingWith" text="Pending With"/></th>
					</tr>
					</thead>
`					 <c:if test="${auditDetail.auditSearchList != null &&  !auditDetail.auditSearchList.isEmpty()}">
					<tbody>
					<c:forEach items="${auditDetail.auditSearchList}" var="result" varStatus="status">
						<tr>
							<td>
								${ status.index+1}
						    </td>
							<td>
								${result.type }
							</td>
							<td>
							<a href="#" onclick="openAudit('${result.id}')" >
							${result.auditno }</a>
							</td>
							<td>
							${result.billNumber}
							</td>
							<td>
							${result.schdDate }
							</td>
							<td>
							${result.statusDescription }
							</td>
							<td>
							${result.pendingWith }
							</td>
						</tr>
						</c:forEach>
					<tbody>
					</c:if>	
					<c:if test="${auditDetail.auditSearchList == null ||  auditDetail.auditSearchList.isEmpty()}">
					No records found
					</c:if>				
				</table>
				</div>
			<br>
			<br>
	        </div>
        </div>
    </div>
    
</form:form>
<script
        src="<cdn:url value='/resources/app/js/audit/audit.js?rnd=${app_release_no}' context='/services/audit'/>"></script>
<script
        src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
        