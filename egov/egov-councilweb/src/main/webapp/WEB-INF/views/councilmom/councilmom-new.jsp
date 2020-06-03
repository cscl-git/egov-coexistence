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
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>
<form:form role="form" action="../update"
	modelAttribute="councilMeeting" id="councilMomform"
	cssClass="form-horizontal form-groups-bordered"
	enctype="multipart/form-data">
	<div class="panel-heading">
		<ul class="nav nav-tabs" id="settingstab">
			<li class="active"><a data-toggle="tab" href="#councilmom"
				data-tabidx=0><spring:message code="tab.council.mom" /></a></li>
			<li><a data-toggle="tab" href="#councilattendance" data-tabidx=1><spring:message
						code="tab.council.attendance" /></a></li>
		</ul>
	</div>
	<div class="panel-body custom-form">
		<div class="tab-content">
			<div class="tab-pane fade in active" id="councilmom">
				<%@ include file="councilmom-form.jsp"%>
				
				<c:if test="${currentState!= 'null' && 'NEW'.equalsIgnoreCase(currentState)}">
					<div class="form-group">
						<div class="text-center">
							<button type="button" id="add-sumoto" class='btn btn-primary' style="width:175px"><spring:message code='lbl.AddSumoto'/></button>
						</div>
					</div>
				</c:if>
				<c:if test="${currentState!= 'null' && !'NEW'.equalsIgnoreCase(currentState)
										&& !'Closed'.equalsIgnoreCase(currentState)}">
					<div class="panel panel-primary" data-collapsed="0">
						<jsp:include page="../workflow/applicationhistory-view.jsp" />
					</div>
				</c:if>
				<div class="form-group">
					<div class="text-center">
						<c:if test="${currentState!= 'null' && !'Closed'.equalsIgnoreCase(currentState)}">
							<jsp:include page="../workflow/commonWorkflowMatrix.jsp" />
							<div class="buttonbottom" align="center">
								<jsp:include page="../workflow/commonWorkflowMatrix-button.jsp" />
							</div>
						</c:if>
						<c:if test="${currentState!= 'null' && 'Closed'.equalsIgnoreCase(currentState)}">
							<button type="button" id="buttonFinalSubmit" class='btn btn-primary' style="width:230px"><spring:message code='lbl.resolutionpdf'/></button>
							<a href='javascript:void(0)' class='btn btn-default'
								onclick='self.close()'><spring:message code='lbl.close' /></a>
						</c:if>
					</div>
				</div>
			</div>
			<div id="councilattendance" class="tab-pane fade">
				<%@ include file="councilmeeting-attendsearch-view.jsp"%>
			</div>
		</div>
	</div>

</form:form>
<style>
.nav-tabs{
	z-index: 0;
	position: relative;
}
.tab-content {
    margin-top: 0px;
}
.form-horizontal .form-group {
    margin-right: 0px;
    margin-left: 0px;
}
</style>
<script
	src="<cdn:url value='/resources/global/js/bootstrap/bootstrap-datepicker.js' context='/services/egi'/>"></script>
<script
	src="<cdn:url value='/resources/app/js/councilMomHelper.js?rnd=${app_release_no}'/>"></script>
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/bootstrap/bootstrap-datepicker.css' context='/services/egi'/>">
<script type="text/javascript"
	src="<cdn:url value='/resources/app/js/councilMom.js?rnd=${app_release_no}'/>"></script>
