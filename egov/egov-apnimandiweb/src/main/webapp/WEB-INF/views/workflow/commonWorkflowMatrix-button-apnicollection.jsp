<%--
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) 2017  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~            Further, all user interfaces, including but not limited to citizen facing interfaces,
  ~            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
  ~            derived works should carry eGovernments Foundation logo on the top right corner.
  ~
  ~            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
  ~            For any further queries on attribution, including queries on brand guidelines,
  ~            please contact contact@egovernments.org
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  ~
  --%>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script>


	function validateWorkFlowApprover(name) {
		document.getElementById("workFlowAction").value=name;
		
	    var approverPosId = document.getElementById("approvalPosition");
		var rejectbutton=document.getElementById("workFlowAction").value;
		if(rejectbutton!=null && (rejectbutton=='Reject'||rejectbutton=='Cancel'|| rejectbutton=='Provide more info'))
			{
			$('#approvalDepartment').removeAttr('required');
			$('#approvalDesignation').removeAttr('required');
			$('#approvalPosition').removeAttr('required');
			$('#approvalComent').attr('required', 'required');	
			} 
		else if (rejectbutton == 'Approve') {
			$('#approvalDepartment').removeAttr('required');
			$('#approvalDesignation').removeAttr('required');
			$('#approvalPosition').removeAttr('required');
			$('#approvalComent').removeAttr('required');
		}
		else if(rejectbutton == 'Forward'){
			$('#approvalDepartment').attr('required', 'required');	
			$('#approvalDesignation').attr('required', 'required');	
			$('#approvalPosition').attr('required', 'required');	
			$('#approvalComent').removeAttr('required');
			}
	   document.forms[0].submit;
	   return true;
	}
</script>

<div class="buttonbottom" align="center">
<div class="panel-body">
	
			<c:if test="${currentState!= 'null' && !'Closed'.equalsIgnoreCase(currentState)}">
				<form:hidden path="" id="currentState" name="currentState" value="${currentState}"/>
			</c:if> 
			<c:if test="${currentState!= 'null' && 'Closed'.equalsIgnoreCase(currentState)}">
				<form:hidden path="" id="currentState" name="currentState" value=""/>
			</c:if> 
			<form:hidden path="" id="currentDesignation" name="currentDesignation" value="${currentDesignation}"/>
			<form:hidden path="" id="additionalRule" name="additionalRule" value="${additionalRule}"/>
			<form:hidden path="" id="amountRule" name="amountRule" value="${amountRule}"/>
			<form:hidden path="" id="workFlowDepartment" name="workFlowDepartment" value="${workFlowDepartment}"/>
			<form:hidden path="" id="pendingActions" name="pendingActions" value="${pendingActions}"/>
			<form:hidden path="" id="nextAction" name="nextAction" value="${nextAction}"/>
			<form:hidden path="" id="approverName" name="approverName"/>
			<form:hidden path="" id="nextDesignation" name="nextDesignation"/>
			<form:hidden path="" name="stateType" id="stateType" value="${stateType}"/>	
			<form:hidden path="" id="workFlowAction" name="workFlowAction"/>
			 		</div>
			 		
	<table>
		<tr>
			<td>
				<c:forEach items="${validActionList}" var="validButtons">
					<form:button type="submit" id="${validButtons}" class="btn btn-primary workflow-submit"  value="${validButtons}" name="Generate" onclick="validateWorkFlowApprover('${validButtons}');">
						Generate
					</form:button>
				</c:forEach>
				<c:if test="${(currentState!= 'null' && !'NEW'.equalsIgnoreCase(currentState))
								|| (stateType!= 'null' && 'CouncilMeeting'.equalsIgnoreCase(stateType))}">
				</c:if> 
			</td>
		</tr>
	</table>
	<c:if test="${nextAction != 'null' && 'END'.equalsIgnoreCase(nextAction)}" >
	<form:hidden path="" id="wfStatus" name="wfStatus" />
	<form:hidden path="" id="workFlowAction" name="workFlowAction" />
</c:if>
</div>