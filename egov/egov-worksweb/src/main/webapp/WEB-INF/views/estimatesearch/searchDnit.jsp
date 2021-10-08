<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<script
        src="<cdn:url value='/resources/js/estimateworks.js?rnd=${app_release_no}' context='/services/works'/>"></script>
        <script
	src="<cdn:url value='/resources/js/estimatepreparationapproval/estimationhelper.js?rnd=${app_release_no}'/>"></script>

	<form:form name="search-dnit-form-mis" role="form" method="post"
		action="workDnitSearchnew" modelAttribute="workdnitDetails"
	id="workEstimateDetails" class="form-horizontal form-groups-bordered"
	style="margin-top:-20px;">

	<div class="tab-content">
		<div class="tab-pane fade in active">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="form-group" style="padding: 50px 20px 0;">


<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.search.work.name" /></label>
					<div class="col-sm-3 add-margin">
						<form:textarea class="form-control" path="workName"
							maxlength="2000" />
							</div>
							
							
				<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.search.status" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="workStatusSearch" id="workStatusSearch"
							cssClass="form-control" cssErrorClass="form-control error">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
						<form:option value="Created">Created</form:option>
							<form:option value="Pending for Approval">Pending for Approval</form:option>
							<form:option value="Approved">Approved</form:option>
						</form:select>
					</div>			
<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.works.wing" /></label>
					<div class="col-sm-3 add-margin">
								<form:select path="worksWing" id="worksWing"
									cssClass="form-control" cssErrorClass="form-control error">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:options items="${workdnitDetails.workswings}"
								itemValue="id" itemLabel="workswingname" />
									<%-- <form:option value="Building & Roads">Building & Roads</form:option>
									<form:option value="Public Health">Public Health</form:option>
									<form:option value="Horticulture & Electrical">Horticulture & Electrical</form:option> --%>
								</form:select>
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.preparation.executing.division" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="department" id="department" 
							class="form-control">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
							<form:options items="${workdnitDetails.departments}"
								itemValue="code" itemLabel="name" />
								</form:select>
							</div>
							<label class="col-sm-3 control-label text-left-audit">Sub-Division</label>
					<div class="col-sm-3 add-margin">
						<form:select path="subdivision" id="subdivision"
							cssClass="form-control"
							cssErrorClass="form-control-works error" >
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:options items="${workdnitDetails.subdivisions}"
								itemValue="id" itemLabel="subdivision" />
							
						</form:select>
						</div>
					
					
					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.dnit.from.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="fromDt" path="fromDt"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.dnit.to.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="toDt" path="toDt" class="form-control datepicker"
							data-date-end-date="0d" placeholder="DD/MM/YYYY" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.preparation.aa.exp.head" /><span
						class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
						<form:select path="expHead_est" id="wardCheck"
							cssClass="form-control" cssErrorClass="form-control error">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:option value="Capital">Capital</form:option>
							<form:option value="Revenue">Revenue</form:option>
							<form:option value="Deposit Estimate works">Deposit Estimate works</form:option>
							<form:option value="Ward Development Funds">Ward Development Funds</form:option>
						</form:select>
					</div>
					
					
					
					
							
					
							</div>
						</div>
					</div>

		<div class="buttonbottom" align="center">
			<input type="submit" id="workDnitSearch" class="btn btn-primary"
						name="workDnitSearch" code="lbl.search.work.estimate"
						value="Search" />
				</div>
				<!-- <div class="buttonbottom" align="center">
			<input type="submit" id="workEditDnit" class="btn btn-primary"
						name="workEditDnit" code="lbl.search.work.estimate"
						value="Edit DNIT Search" />
				</div> -->

		<br> <br> <br>
		<div class="tab-pane fade in active" id="resultheader">
			<h3>Search Result</h3>
			<div class="" data-collapsed="0">
				<div style="padding: 0 15px;">
					<table class="table table-bordered" id="table">
						<thead>
							<tr>
								<th><spring:message
										code="lbl.estimate.preparation.name.work" /></th>
								
								<th>Wing</th>
								<th>Division</th>
								<th>Sub-Division</th>
								<th>DNIT Status</th>
								<th>Expenditure Head</th>
								<th>DNIT Cost</th>
								<th>Date of DNIT creation</th>
								<th>Date of DNIT approval</th>

							</tr>
						</thead>
						`
						<c:if
							test="${workdnitDetails.estimateList != null &&  !workdnitDetails.estimateList.isEmpty()}">
							<tbody>
								<c:forEach items="${workdnitDetails.estimateList}"
									var="result" varStatus="status">
									<tr>
										<td><form:hidden
												path="estimateList[${status.index}].workName"
												id="estimateList[${status.index}].workName" />
											${result.workName }</td>
										
										<td><form:hidden
												path="estimateList[${status.index}].worksWing"
												id="estimateList[${status.index}].worksWing" />
											${result.worksWing }</td>
										<td><form:hidden
												path="estimateList[${status.index}].executeDiv"
												id="estimateList[${status.index}].executeDiv" />
											${result.executeDiv }</td>
										<td><form:hidden
												path="estimateList[${status.index}].subdivis"
												id="estimateList[${status.index}].subdivis" />
											${result.subdivis }</td>
										<td><form:hidden
												path="estimateList[${status.index}].statussearch"
												id="estimateList[${status.index}].statussearch" />
											${result.statussearch }</td>
									<td><form:hidden
												path="estimateList[${status.index}].expHead_est"
												id="estimateList[${status.index}].expHead_est" />
											${result.expHead_est }</td>
											<td><form:hidden
												path="estimateList[${status.index}].estimateAmount"
												id="estimateList[${status.index}].estimateAmount" />
											${result.estimateAmount }</td>
											<td><form:hidden
												path="estimateList[${status.index}].createdDt"
												id="estimateList[${status.index}].createdDt" />
											${result.createdDt }</td>
											<td><form:hidden
												path="estimateList[${status.index}].approveDt"
												id="estimateList[${status.index}].approveDt" />
											${result.approveDt }</td>
									</tr>
								</c:forEach>
							<tbody>
						</c:if>
						<c:if
							test="${workdnitDetails.estimateList == null ||  workdnitDetails.estimateList.isEmpty()}">
					No records found
					</c:if>
					</table>
				</div>

			</div>
		</div>
	</div>

	</form:form>



