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
<%@ include file="/includes/taglibs.jsp"%>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<form:form role="form" action="/services/audit/manageAuditor/updateAuditor"
	modelAttribute="manageAuditors" id="approvebudgetsearchform"
	cssClass="form-horizontal form-groups-bordered"
	enctype="multipart/form-data">
	<div class="main-content">
		<div class="row">
			<div class="col-md-12">
				<div class="panel panel-primary" data-collapsed="0">
					<c:if test="${not empty message}">
						<div id="message" class="success" style="color: green;margin-top:15px;">
							<spring:message code="${message}" />
						</div>
					</c:if>
					<div class="panel-heading">
						<div class="panel-title">Manage Auditors/ RSA</div>
					</div>
					<div class="panel-body">
						<div class="form-group">
							<label class="col-sm-3 control-label text-right">Type <span class="mandatory1">*</span></label>
							<div class="col-sm-3 add-margin">
								<form:select path="type" required="required"
									id="auditorType" cssClass="form-control"
									cssErrorClass="form-control error">
									<form:option value="${manageAuditors.type }" label="${manageAuditors.type }">
										<spring:message code="lbl.select" />
									</form:option>
									<form:option value="Auditor" label="Auditor">
									</form:option>
									<form:option value="RSA" label="RSA">
									</form:option>
								</form:select>
								<form:errors path="" cssClass="error-msg" />
							</div>
							
							<!-- EmployeeDropDown -->
							<div class="show-row form-group" >
						<label class="col-sm-3 control-label text-right">Department<span class="mandatory"></span></label>
						<div class="col-sm-3 add-margin">
							<form:select path="deptid" data-first-option="false" 
								id="approvalDepartment" cssClass="form-control"
								cssErrorClass="form-control error" required="required">
								<form:option value="${manageAuditors.deptid}" label="${manageAuditors.deptName}">
								</form:option>
								<form:options items="${departments}" itemValue="code"
									itemLabel="name" />     
							</form:select>
						</div>
						
					</div>
					<div class="show-row form-group">
						<label class="col-sm-3 control-label text-right">Employee<span class="mandatory"></span></label>
						<div class="col-sm-3 add-margin">
						 <%-- <input type="hidden" name="id" id="approvalDesignationValue" value="${manageAuditors.id }" />  --%>
						 <form:hidden path="id" id="auditorid"  name="id" value="${manageAuditors.id}"/>
						
						<form:select path="employeeid" data-first-option="false" 
							id="approvalPosition"  cssClass="form-control"  
							cssErrorClass="form-control error" >  
							<form:option value="${manageAuditors.employeeid}" label="${manageAuditors.employeeName}" >
								</form:option>
								  <form:options items="${approverListEdit}" itemValue="user.id"
									itemLabel="user.name" />  
						</form:select>		
						</div> 
					</div>
							
							
							
							
							 
						</div>
						<br>
						<br>
						<br>
						
						<br>
						<br>
						<div class="form-group">
							<div class="text-center">
								<button type='submit' class='btn btn-primary' id="btnsearch">
									Update
								</button>
							
								<a href='javascript:void(0)' class='btn btn-default'
									onclick='self.close()'><spring:message code='lbl.close' /></a>
									<form:hidden path="" id="workAction" name="workAction" value="${manageAuditors.id }"/>
									
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</form:form>
<script>
	$('#btnsearch').click(function(e) {

		if ($('form').valid()) {

		} else {

		e.preventDefault();

	}  

});



	$('#auditorType').change(function(){
		
		
		var todayDate = new Date(Date.now()).toLocaleString();
		
		var type =$('#auditorType').val();

	$.ajax({
	url: "/services/audit/manageAuditor/employee/"+type,     
	type: "GET",
	contentType:'application/json',
	//data: JSON.stringify(jsonData),
	success: function (response) {
		console.log("success"+response);
		$('#approvalPosition').empty();
		$('#approvalPosition').append($("<option value=''>Select from below</option>"));
		$.each(response, function(index, value) {
			//$('#approvalPosition').append($('<option>').text(value.userName+'/'+value.positionName).attr('value', value.positionId));  
			$('#approvalPosition').append($('<option>').text(value.user.name).attr('value', value.assignments[0].position));  
		});
		//$('#approvalPosition').val($('#approvalPositionValue').val());
	}, 
	error: function (response) {
		console.log("failed");
	}
	});
	});


</script>
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/bootstrap/bootstrap-datepicker.css' context='/services/egi'/>" />
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css' context='/services/egi'/>"/>
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css' context='/services/egi'/>">
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js' context='/services/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js' context='/services/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.tableTools.js' context='/services/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/TableTools.min.js' context='/services/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/bootstrap/typeahead.bundle.js' context='/services/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.inputmask.bundle.min.js' context='/services/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.validate.min.js' context='/services/egi'/>"></script>
	
<script
	src="<cdn:url value='/resources/global/js/bootstrap/bootstrap-datepicker.js' context='/services/egi'/>"
	type="text/javascript"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/app/js/budgetUploadReportHelper.js?rnd=${app_release_no}'/>"></script>