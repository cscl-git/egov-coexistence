<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<script
        src="<cdn:url value='/resources/js/estimateworks.js?rnd=${app_release_no}' context='/services/works'/>"></script>

	<form:form name="search-estimate-form" role="form" method="post"
		action="workEstimateDetailedSearch" modelAttribute="workEstimateDetails"
	id="workEstimateDetails" class="form-horizontal form-groups-bordered"
	style="margin-top:-20px;">

	<div class="tab-content">
		<div class="tab-pane fade in active">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="form-group" style="padding: 50px 20px 0;">

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.preparation.executing.division" /><span
						class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
						<form:select path="department" id="department" required="true"
							class="form-control">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
							<form:options items="${workEstimateDetails.departments}"
								itemValue="code" itemLabel="name" />
								</form:select>
							</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.preparation.estimate.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="estimateNumber" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.estimate.from.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="fromDt" path="fromDt"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.estimate.to.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="toDt" path="toDt" class="form-control datepicker"
							data-date-end-date="0d" placeholder="DD/MM/YYYY" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.works.wing" /></label>
					<div class="col-sm-3 add-margin">
								<form:select path="worksWing" id="worksWing"
									cssClass="form-control" cssErrorClass="form-control error">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:option value="1">Building & Roads</form:option>
									<form:option value="2">Public Health</form:option>
									<form:option value="3">Horticulture & Electrical</form:option>
								</form:select>
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.work.location" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="workLocation" />
							</div>
					
					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.work.category" /></label>
					<div class="col-sm-3 add-margin">
								<form:select path="workCategory" id="workCategory"
									cssClass="form-control" cssErrorClass="form-control error">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:option value="1">Road Work </form:option>
									<form:option value="2">Bridge Work</form:option>
									<form:option value="3">Maintaince Work</form:option>
								</form:select>
							</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.estimate.amount" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control" id="estimatedCost" 
									path="estimateAmount" />
							</div>
					
							</div>
						</div>
					</div>

		<div class="buttonbottom" align="center">
			<input type="submit" id="workEstimateSearch" class="btn btn-primary"
						name="workEstimateSearch" code="lbl.search.work.estimate"
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
								<th><spring:message
										code="lbl.estimate.preparation.name.work" /></th>
								<th><spring:message
										code="lbl.estimate.preparation.work.category" /></th>
								<th><spring:message
										code="lbl.estimate.preparation.estimate.number" /></th>
								<th><spring:message
										code="lbl.estimate.preparation.estimate.date" /></th>
								<th><spring:message
										code="lbl.estimate.preparation.estimate.amount" /></th>
								<th><spring:message
										code="lbl.estimate.preparation.work.status" /></th>

							</tr>
						</thead>
						`
						<c:if
							test="${workEstimateDetails.estimateList != null &&  !workEstimateDetails.estimateList.isEmpty()}">
							<tbody>
								<c:forEach items="${workEstimateDetails.estimateList}"
									var="result" varStatus="status">
									<tr>
										<td><form:hidden
												path="estimateList[${status.index}].workName"
												id="estimateList[${status.index}].workName" />
											${result.workName }</td>
										<td><form:hidden
												path="estimateList[${status.index}].workCategry"
												id="estimateList[${status.index}].workCategry" />
											${result.workCategry }</td>
										<td><form:hidden
												path="estimateList[${status.index}].estimateNumber"
												id="estimateList[${status.index}].estimateNumber" />
											<a href="#" onclick="editEstimate('${result.id}')">${result.estimateNumber }</a></td>
										<td><form:hidden
												path="estimateList[${status.index}].estimateDt"
												id="estimateList[${status.index}].estimateDt" />
											${result.estimateDt }</td>
										<td><form:hidden
												path="estimateList[${status.index}].estimateAmount"
												id="estimateList[${status.index}].estimateAmount" />
											${result.estimateAmount }</td>
										<td><form:hidden
												path="estimateList[${status.index}].statusDescription"
												id="estimateList[${status.index}].statusDescription" />
											${result.statusDescription }</td>

									</tr>
								</c:forEach>
							<tbody>
						</c:if>
						<c:if
							test="${workEstimateDetails.estimateList == null ||  workEstimateDetails.estimateList.isEmpty()}">
					No records found
					</c:if>
					</table>
				</div>

			</div>
		</div>
	</div>

	</form:form>



