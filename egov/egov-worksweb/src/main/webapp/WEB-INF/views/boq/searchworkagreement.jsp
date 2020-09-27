<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<form:form name="search-work-agreement-form" role="form" method="post"
	action="workOrderAgreementSearch1" modelAttribute="workOrderAgreement"
	id="workOrderAgreement" class="form-horizontal form-groups-bordered"
	style="margin-top:-20px;">

	<div class="tab-content">
		<div class="tab-pane fade in active">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="form-group" style="padding: 50px 20px 0;">

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="work_number" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.start.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="work_start_date" path="work_start_date"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.intended.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="work_end_date" path="work_end_date"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.executing.department" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="department" id="department"
							class="form-control">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:options items="${workOrderAgreement.departments}"
								itemValue="code" itemLabel="name" />
						</form:select>
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.agreement.status" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="work_agreement_status"
							id="work_agreement_status" cssClass="form-control"
							cssErrorClass="form-control error">
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

					<div align="center">
						<input type="submit" id="workOrderAgreementSearch1"
							class="btn btn-primary" name="workOrderAgreementSearch1"
							code="lbl.search.work.estimate" value="Search Work Estimate" />
					</div>

					<div style="padding: 0 15px;">
						<table class="table table-bordered" id="table" style="width: 100%">
							<thead>
								<tr>
									<th><spring:message code="lbl.name.work" /></th>
									<th><spring:message code="lbl.estimate.number" /></th>
									<th><spring:message code="lbl.amount" /></th>
									<th><spring:message code="lbl.agreement.status" /></th>

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
													path="workOrderList[${status.index}].work_amount"
													id="workOrderList[${status.index}].work_amount" />
												${result.work_amount }</td>
											<td><form:hidden
													path="workOrderList[${status.index}].work_agreement_status"
													id="workOrderList[${status.index}].work_agreement_status" />
												${result.work_agreement_status }</td>
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
	</div>

</form:form>




