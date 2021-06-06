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

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading">
		
	</div>
	<div class="panel-body">
		<div class="row add-border">
			<div class="col-md-3 col-xs-6 add-margin">
				<spring:message code="lbl.billnumber" text="Bill Number"/>
			</div>
			<div class="col-md-3 col-xs-6 add-margin view-content" id="ct-mobno">
				<c:out default="N/A" value="${egBillregister.billnumber }" />
			</div>
			<div class="col-md-3 col-xs-6 add-margin">
				<spring:message code="lbl.billdate" text="Bill Date"/>
			</div>
			<div class="col-md-3 col-xs-6 add-margin view-content" id="ct-email">
				<fmt:formatDate value="${egBillregister.billdate}" pattern="dd/MM/yyyy" />
			</div>
		</div>
		<jsp:include page="expense-view-trans-filter.jsp"/>
		
		<div class="row add-border">
			<div class="col-md-3 col-xs-6 add-margin">
				<spring:message code="lbl.function" text="Function"/>
			</div>
			<div class="col-md-3 col-xs-6 add-margin view-content" id="ct-mobno">
				<c:out default="N/A" value="${egBillregister.egBillregistermis.function.name }" />
			</div>
			<div class="col-md-3 col-xs-6 add-margin">
				<spring:message code="lbl.narration" text="Narration"/>
			</div>
			<div class="col-md-3 col-xs-6 add-margin view-content" id="ct-email">
				<c:out default="N/A" value="${egBillregister.egBillregistermis.narration }" />
			</div>
		</div>
		
		<div class="row add-border">
			<div class="col-md-3 col-xs-6 add-margin">
				<spring:message code="lbl.party.billnumber" text="Party Bill Number"/>
			</div>
			<div class="col-md-3 col-xs-6 add-margin view-content" id="ct-mobno">
				<c:out default="N/A" value="${egBillregister.egBillregistermis.partyBillNumber }" />
			</div>
			<div class="col-md-3 col-xs-6 add-margin">
				<spring:message code="lbl.party.billdate" text="Party Bill Date"/>
			</div>
			<div class="col-md-3 col-xs-6 add-margin view-content" id="ct-email">
				<fmt:formatDate value="${egBillregister.egBillregistermis.partyBillDate}" pattern="dd/MM/yyyy" />
			</div>
		</div>
		
		
		<div class="row add-border">
			<div class="col-md-3 col-xs-6 add-margin">
				<spring:message code="lbl.billsubtype" text="Bill SubType"/>
			</div>
			<div class="col-md-3 col-xs-6 add-margin view-content" id="ct-mobno">
				<c:out default="N/A" value="${egBillregister.egBillregistermis.egBillSubType.name }" />
				 <form:hidden path="" id="billSubType" value="${egBillregister.egBillregistermis.egBillSubType.name }"/>
			</div>
			<div class="col-md-3 col-xs-6 add-margin">
				<spring:message code="lbl.ban.number" text="BAN Number"/>
			</div>
			<div class="col-md-3 col-xs-6 add-margin view-content" id="ct-email">
				<c:out default="N/A" value="${egBillregister.egBillregistermis.budgetaryAppnumber }" />
			</div>
		</div>
		<c:if test="${not empty egBillregister.egBillregistermis.budget ||not empty egBillregister.egBillregistermis.previousexpenditure}">
		<div class="row add-border">
			<c:if test="${not empty egBillregister.egBillregistermis.budget}">
			<div class="col-md-3 col-xs-6 add-margin">
				<spring:message code="lbl.budget.amount" text="Budget (Amount)"/>
			</div>
			<div class="col-md-3 col-xs-6 add-margin view-content" id="ct-mobno">
				<c:out default="N/A" value="${egBillregister.egBillregistermis.budget }" />				 
			</div>
			</c:if>
			  <c:if test="${not empty egBillregister.egBillregistermis.previousexpenditure}">
			<div class="col-md-3 col-xs-6 add-margin">
				<spring:message code="lbl.previousexpenditure" text="Previous expenditure (Till Date)"/>
			</div>
			<div class="col-md-3 col-xs-6 add-margin view-content" id="ct-mobno">
				<c:out default="N/A" value="${egBillregister.egBillregistermis.previousexpenditure }" />				 
			</div>
			</c:if>
			
		</div>
		</c:if>
		<c:if test="${ not empty egBillregister.egBillregistermis.balance ||not empty egBillregister.egBillregistermis.currentexpenditure}">	
		<div class="row add-border">
		  
			<c:if test="${not empty egBillregister.egBillregistermis.currentexpenditure}">
			<div class="col-md-3 col-xs-6 add-margin">
				<spring:message code="lbl.currentexpenditure" text="Current Expenditure"/>
			</div>
			<div class="col-md-3 col-xs-6 add-margin view-content" id="ct-email">
				<c:out default="N/A" value="${egBillregister.egBillregistermis.currentexpenditure }"/>
			</div>
			</c:if>
			
			
			<c:if test="${not empty egBillregister.egBillregistermis.balance}">
			<div class="col-md-3 col-xs-6 add-margin">
				<spring:message code="lbl.balance" text="Balance"/>
			</div>
			<div class="col-md-3 col-xs-6 add-margin view-content" id="ct-email">
				<c:out default="N/A" value="${egBillregister.egBillregistermis.balance }" />
			</div>
			</c:if>
		</div>
		</c:if>
		
<div class="row add-border">
	<div class="works">
	
	
		<div class="col-md-3 col-xs-6 add-margin">
				<spring:message code="lbl.billtype" text="Bill Type"/>
			</div>
			<div class="col-md-3 col-xs-6 add-margin view-content" id="ct-mobno">
				<c:out default="N/A" value="${egBillregister.billtype }" />
			</div>
		
		
		<div class="col-md-3 col-xs-6 add-margin">
				<spring:message code="lbl.workorder" text="Work Order"/>
			</div>
			<div class="col-md-3 col-xs-6 add-margin view-content" id="ct-mobno">
				<c:out default="N/A" value="${egBillregister.workordernumber }" />
			</div>
		
		
		
	
	</div>
		</div>
		<div class="row add-border">
			<div class="col-md-3 col-xs-6 add-margin">
				<spring:message code="lbl.budget.details" text="Budget Details"/>
			</div>
			<div class="col-md-3 col-xs-6 add-margin view-content" id="ct-mobno">
				<a href="#" onclick="openPaymentBudgetDetails()">Click</a>
			</div>
			<div class="col-md-3 col-xs-6 add-margin">
			</div>
			<div class="col-md-3 col-xs-6 add-margin view-content" id="ct-email">
			</div>
		</div>
		
	</div>
	
</div>