<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

	<form:form name="work-order-search-form" role="form" method="post"
		action="workorderSearch" modelAttribute="workOrderSearchDetails"
	id="workOrderSearchDetails"
	class="form-horizontal form-groups-bordered" style="margin-top:-20px;">

	<div class="tab-content">
		<div class="tab-pane fade in active">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="form-group" style="padding: 50px 20px 0;">

					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.order.search.status" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="work_agreement_status"
							id="work_agreement_status" cssClass="form-control"
							cssErrorClass="form-control error" required="required">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:option value="Initiated">Initiated</form:option>
							<form:option value="Under Verification">Under Verification</form:option>
							<form:option value="Approved">Approved</form:option>
							<form:option value="AA Approved">AA Approved</form:option>
							<form:option value="Detailed Estimate Approved">Detailed Estimate Approved</form:option>
						</form:select>
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.order.search.category" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="category" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.order.search.department" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="department" id="department"
							class="form-control">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:options items="${workOrderSearchDetails.departments}"
								itemValue="code" itemLabel="name" />
						</form:select>
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.order.search.work.name" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control"
									path="name_work_order" />
							</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.order.search.work.type" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="workType" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.order.search.work.location" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="workLocation" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.order.search.sector.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="sector" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.order.search.ward.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="wardNumber" />
							</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.order.search.fund" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="fund" />
							</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.order.search.estimated.cost" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="estimatedCost" />
							</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.order.search.tendered.cost" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="tenderCost" />
							</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.order.search.agency.work.order" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control"
									path="agencyWorkOrder" />
							</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.order.search.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="date" path="date" class="form-control datepicker"
							data-date-end-date="0d" placeholder="DD/MM/YYYY" />
							</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.order.search.time.limit" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="timeLimit" />
							</div>

					<div class="buttonbottom" align="center">
						<input type="submit" id="workorderSearch" class="btn btn-primary"
							name="workorderSearch" code="lbl.search.work.estimate"
							value="Search" />
						</div>

					<div style="padding: 0 15px;">
						<table class="table table-bordered" id="table" style="width: 100%">
							<thead>
								<tr>
									<th><spring:message code="lbl.name.work" /></th>
									<th><spring:message code="lbl.estimate.number" /></th>
									<th><spring:message code="lbl.amount" /></th>
									<th><spring:message code="lbl.agreement.status" /></th>
									<th><spring:message code="lbl.agreement.status" /></th>
								</tr>
							</thead>
							`
							<c:if
								test="${workOrderSearchDetails.workOrderList != null &&  !workOrderSearchDetails.workOrderList.isEmpty()}">
								<tbody>
									<c:forEach items="${workOrderSearchDetails.workOrderList}"
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
													path="workOrderList[${status.index}].work_amount"
													id="workOrderList[${status.index}].work_amount" />
												${result.work_amount }</td>
											<td><form:hidden
													path="workOrderList[${status.index}].work_agreement_status"
													id="workOrderList[${status.index}].work_agreement_status" />
												${result.work_agreement_status }</td>
											<td><form:hidden
													path="workOrderList[${status.index}].progressCompletion"
													id="workOrderList[${status.index}].progressCompletion" />
												${result.progressCompletion }</td>
										</tr>
									</c:forEach>
								<tbody>
							</c:if>
							<c:if
								test="${workOrderSearchDetails.workOrderList == null ||  workOrderSearchDetails.workOrderList.isEmpty()}">
					No records found
					</c:if>
						</table>
					</div>
				</div>
				</div>
			</div>
		</div>

	</form:form>


