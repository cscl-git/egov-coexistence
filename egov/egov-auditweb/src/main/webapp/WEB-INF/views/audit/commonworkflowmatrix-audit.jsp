<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<form:hidden path="" name="stateType" id="stateType" value="${stateType}"/>	
<form:hidden path="" id="workFlowAction" name="workFlowAction"/>	
<div class="panel panel-primary" data-collapsed="0" >	
	<c:if test="${nextAction !='END'}" > 
			
		<div class="panel-heading">
			<div class="panel-title">
				<spring:message code="lbl.approverdetails" text="Approval Details"/>
			</div>					
		</div>
	</c:if>
		<div class="panel-body">
		
			 <c:if test="${currentState!= 'null' && !'Closed'.equalsIgnoreCase(currentState)}">
				<form:hidden path="" id="currentState" name="currentState" value="${currentState}"/>
			</c:if> 
			 <c:if test="${currentState!= 'null' && 'Closed'.equalsIgnoreCase(currentState)}">
				<form:hidden path="" id="currentState" name="currentState" value=""/>
			</c:if> 
																																																									
	
			<form:hidden path="" id="currentDesignation" name="currentDesignation" value="${currentDesignation}"/>
			<form:hidden path="" id="additionalRule" name="additionalRule" value="${additionalRule}"/>
			<form:hidden  path="" id="amountRule" name="amountRule" value="${amountRule}"/>
			<form:hidden path="" id="workFlowDepartment" name="workFlowDepartment" value="${workFlowDepartment}"/>
			<form:hidden path="" id="pendingActions" name="pendingActions" value="${pendingActions}"/>
			<%-- <form:hidden path="" id="approverName" name="approverName" /> --%>

				<div class="row show-row"  id="approverDetailHeading">
				<c:if test="${nextAction !='END'}" > 
					<div class="show-row form-group" >
						<label class="col-sm-3 control-label text-right"><spring:message code="lbl.approverdepartment" text="Approver Department"/><span class="mandatory"></span></label>
						<div class="col-sm-3 add-margin">
							<form:select path="approvalDepartment" data-first-option="false" name="approvalDepartment"
								id="approvalDepartment" cssClass="form-control"
								cssErrorClass="form-control error" required="required">
								<form:option value="">
									<spring:message code="lbl.select" text="Select"/>
								</form:option>
								<form:options items="${approvalDepartmentList}" itemValue="code"
									itemLabel="name" />     
							</form:select>
						</div>
						<label class="col-sm-2 control-label text-right"><spring:message code="lbl.approverdesignation" text="Approver Designation"/><span class="mandatory"></span></label>
						<div class="col-sm-3 add-margin">
						<input type="hidden" id="approvalDesignationValue" value="${approvalDesignation }" />
							<form:select path="" data-first-option="false" name="approvalDesignation"
								id="approvalDesignation" cssClass="form-control" onfocus="callAlertForDepartment();"
								cssErrorClass="form-control error" required="required">  
								<form:option value="">
									<spring:message code="lbl.select" text="Select"/>
								</form:option>
								
							</form:select>					
						</div>
					</div>
					<div class="show-row form-group">
						<label class="col-sm-3 control-label text-right"><spring:message code="lbl.approver" text="Approver"/><span class="mandatory"></span></label>
						<div class="col-sm-3 add-margin">
						<input type="hidden" id="approvalPositionValue" value="${approvalPosition }" />
						<input type="hidden" id="approverName" name="approverName" />
						<form:select path="" data-first-option="false" 
							id="approvalPosition" name="approvalPosition" cssClass="form-control" onfocus="callAlertForDesignation();" 
							cssErrorClass="form-control error" required="required">  
							<form:option value="">
									<spring:message code="lbl.select" text="Select"/>
								</form:option>
						</form:select>		
						</div> 
					</div>
					</c:if>
					<div class="show-row form-group">
						<label class="col-sm-3 control-label text-right"><spring:message code="lbl.comments" text="Comments"/></label>
						<div class="col-sm-8 add-margin">
							<form:textarea class="form-control" path="approvalComent"  id="approvalComent" name="approvalComent" />
						</div>
					</div>
				</div>
			</div>		

</div>

<script src="<cdn:url value='/resources/app/js/audit/commonworkflowaudit.js?rnd=${app_release_no}' context='/services/audit'/>"></script>

