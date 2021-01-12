<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<form:form name="estimate-detail-search-form" role="form" method="post"
	action="estimateSearch" modelAttribute="estimateSearchDetails"
	id="estimateSearchDetails" class="form-horizontal form-groups-bordered"
	style="margin-top:-20px;">

	<div class="tab-content">
		<div class="tab-pane fade in active">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="form-group" style="padding: 50px 20px 0;">

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.search.status" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="workStatus" id="workStatus"
							cssClass="form-control" cssErrorClass="form-control error">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:option value="1">Pending</form:option>
							<form:option value="2">Ongoing</form:option>
							<form:option value="3">Complete</form:option>
						</form:select>
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.search.category" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="workCategory" id="workCategory"
							cssClass="form-control" cssErrorClass="form-control error">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:option value="1">Initiated</form:option>
							<form:option value="2">Under Verification</form:option>
							<form:option value="3">Approved</form:option>
							<form:option value="4">AA Approved</form:option>
							<form:option value="5">Detailed Estimate Approved</form:option>
						</form:select>
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.search.department" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="department" id="department"
							class="form-control">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
							<form:options items="${estimateSearchDetails.departments}"
								itemValue="code" itemLabel="name" />
								</form:select>
							</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.search.work.name" /></label>
					<div class="col-sm-3 add-margin">
						<form:textarea class="form-control" path="workName"
							maxlength="2000" />
							</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.search.work.type" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="wardNumber" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.search.work.location" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="workLocation" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.search.sector.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control" path="sectorNumber" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.search.ward.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control" path="wardNumber" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.search.fund" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="fundSource" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.search.estimated.cost" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control"
							path="estimateAmount" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.search.tendered.cost" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control" path="tenderCost" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.search.agency.work.order" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control"
							path="agencyWorkOrder" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.search.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="date" path="date" class="form-control datepicker"
							data-date-end-date="0d" placeholder="DD/MM/YYYY" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.search.time.limit" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="timeLimit" />
					</div>

					<div class="buttonbottom" align="center">
						<input type="submit" id="estimateSearch" class="btn btn-primary"
							name="estimateSearch" code="lbl.search.work.estimate"
						value="Search" />
				</div>

				<div style="padding: 0 15px;">
						<table class="table table-bordered" id="table" style="width: 100%">
						<thead>
							<tr>
								<th><spring:message
										code="lbl.estimate.preparation.name.work" /></th>
								<th><spring:message
										code="lbl.estimate.preparation.work.category" /></th>
								<th><spring:message
										code="lbl.estimate.preparation.work.status" /></th>
								<th><spring:message
										code="lbl.estimate.preparation.estimate.amount" /></th>

							</tr>
						</thead>
						`
						<c:if
								test="${estimateSearchDetails.estimateList != null &&  !estimateSearchDetails.estimateList.isEmpty()}">
							<tbody>
									<c:forEach items="${estimateSearchDetails.estimateList}"
									var="result" varStatus="status">
									<tr>
										<td><form:hidden
												path="estimateList[${status.index}].workName"
												id="estimateList[${status.index}].workName" />
											${result.workName }</td>
										<td><form:hidden
												path="estimateList[${status.index}].workCategory"
												id="estimateList[${status.index}].workCategory" />
											${result.workCategory }</td>
										<td><form:hidden
												path="estimateList[${status.index}].workStatus"
												id="estimateList[${status.index}].workStatus" />
											${result.workStatus }</td>
										<td><form:hidden
												path="estimateList[${status.index}].estimateAmount"
												id="estimateList[${status.index}].estimateAmount" />
											${result.estimateAmount }</td>
									</tr>
								</c:forEach>
							<tbody>
						</c:if>
						<c:if
								test="${estimateSearchDetails.estimateList == null ||  estimateSearchDetails.estimateList.isEmpty()}">
					No records found
					</c:if>
					</table>
				</div>
			</div>
		</div>
		</div>
	</div>

	</form:form>


