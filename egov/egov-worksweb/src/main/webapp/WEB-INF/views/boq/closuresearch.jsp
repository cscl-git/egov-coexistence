<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<form:form name="search-closure-work-agreement-form" role="form"
	method="post" action="searchclosure"
	modelAttribute="workOrderAgreement" id="workOrderAgreement"
	class="form-horizontal form-groups-bordered" style="margin-top:-20px;">

	<div class="tab-content">
		<div class="tab-pane fade in active">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="form-group" style="padding: 50px 20px 0;">

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
					<div align="center">
						<input type="submit" id="searchclosure" class="btn btn-primary"
							name="searchclosure" code="lbl.search.work.estimate"
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





