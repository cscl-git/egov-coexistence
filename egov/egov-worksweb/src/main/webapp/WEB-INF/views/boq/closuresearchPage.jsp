<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<script
        src="<cdn:url value='/resources/js/estimateworks.js?rnd=${app_release_no}' context='/services/works'/>"></script>
         <script
	src="<cdn:url value='/resources/js/estimatepreparationapproval/estimationhelper.js?rnd=${app_release_no}'/>"></script>

	<form:form name="search-closure-work-agreement-form" role="form"
	method="post" action="searchclosurePage"
	modelAttribute="workOrderAgreement" id="workOrderAgreement"
	class="form-horizontal form-groups-bordered" style="margin-top:-20px;">

	<div class="tab-content">
		<div class="tab-pane fade in active">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="form-group" style="padding: 50px 20px 0;">

					<label
						class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.name.work" /></label>
					<div class="col-sm-9 add-margin">
							<form:textarea class="form-control" path="name_work_order_search" maxlength="2000" style="height: 100px;"
					 />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="work_number_search"  />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.work.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="work_agreement_number_search" />
					</div>
					
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.start.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="fromDate" path="fromDate"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.intended.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="toDate" path="toDate"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.works.wing" /></label>
					<div class="col-sm-3 add-margin">
								<form:select path="worksWing" id="worksWing"
									cssClass="form-control" cssErrorClass="form-control error">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:options items="${workOrderAgreement.workswings}"
								itemValue="id" itemLabel="workswingname" />
								</form:select>
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.executing.department" /><span class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
						<form:select path="department" id="department"
							class="form-control" required="required">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:options items="${workOrderAgreement.departments}"
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
							<form:options items="${workEstimateDetails.subdivisions}"
								itemValue="id" itemLabel="subdivision" />
					
						</form:select>
						</div>
							</div>
						</div>
					</div>

		<div class="buttonbottom" align="center">
			<input type="submit" id="searchclosure" class="btn btn-primary"
							name="searchclosure" code="lbl.search.work.estimate"
							value="Search" />
				</div>

		<br> <br> <br>
		<div class="tab-pane fade in active" id="resultheader">
			<h3>Search Result</h3>
			<div class="panel panel-primary" data-collapsed="0">
				<div style="padding: 0 15px;">
					<table class="table table-bordered" id="table">
							<thead>
								<tr>
									<th><spring:message code="lbl.name.work" /></th>
									<th><spring:message code="lbl.estimate.number" /></th>
									<th><spring:message code="lbl.estimate.work.number" /></th>
									<th><spring:message code="lbl.start.date" /></th>
									<th><spring:message code="lbl.intended.date" /></th>
									<th><spring:message code="lbl.amount" /></th>
									<th><spring:message code="lbl.works.status" /></th>

								</tr>
							</thead>
							`
							<c:if
								test="${workOrderAgreement.workOrderList != null &&  !workOrderAgreement.workOrderList.isEmpty()}">
								<tbody>
									<c:forEach items="${workOrderAgreement.workOrderList}"
										var="result" varStatus="status">
										<tr>
											<td><form:hidden
													path="workOrderList[${status.index}].name_work_order"
													id="workOrderList[${status.index}].name_work_order" />
												${result.name_work_order }</td>
											<td><form:hidden
													path="workOrderList[${status.index}].work_number"
													id="workOrderList[${status.index}].work_number" />
												${result.work_number }</td>
											<td><form:hidden
													path="workOrderList[${status.index}].work_agreement_number"
													id="workOrderList[${status.index}].work_agreement_number" />
												<a href="#" onclick="openClosure('${result.id}')" > ${result.work_agreement_number }</a>
												</td>
												<td><form:hidden
													path="workOrderList[${status.index}].startDate"
													id="workOrderList[${status.index}].startDate" />
												${result.startDate }</td>
												<td><form:hidden
													path="workOrderList[${status.index}].endDate"
													id="workOrderList[${status.index}].endDate" />
												${result.endDate }</td>
											<td><form:hidden
													path="workOrderList[${status.index}].work_amount"
													id="workOrderList[${status.index}].work_amount" />
												${result.work_amount }</td>
											<td><form:hidden
													path="workOrderList[${status.index}].statusDescp"
													id="workOrderList[${status.index}].statusDescp" />
												${result.statusDescp }</td>
										</tr>
									</c:forEach>
								<tbody>
							</c:if>
							<c:if
								test="${workOrderAgreement.workOrderList == null ||  workOrderAgreement.workOrderList.isEmpty()}">
					No records found
					</c:if>
						</table>
				</div>

			</div>
		</div>
	</div>

	</form:form>



