<%@ include file="/includes/taglibs.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>


<form:form name="edit-measured-quantity-work-agreement" role="form"
	method="post" modelAttribute="workOrderAgreement" action="workmeasured"
	id="workOrderAgreement" class="form-horizontal form-groups-bordered"
	enctype="multipart/form-data" style="margin-top:-20px;">
	<div class="tab-content">
		<div class="tab-pane fade in active" id="auditheader">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="form-group" style="padding: 50px 20px 0;">
					<input type="hidden" name="workOrderAgreement"
						value="${workOrderAgreement.id}" /> <label
						class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.name.work" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control"
							path="name_work_order" readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="work_number"
							readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.start.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="work_start_date" path="work_start_date"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" readonly="true" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.intended.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="work_intended_date" path="work_intended_date"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.extended.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="work_end_date" path="work_end_date"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.executing.department" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="department" id="department"
							class="form-control" readonly="true">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:options items="${workOrderAgreement.departments}"
								itemValue="code" itemLabel="name" />
						</form:select>
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.amount" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control" path="work_amount"
							readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.details" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="work_details"
							readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.agreement.details" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control"
							path="agreement_details" readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.agreement.status" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="work_agreement_status"
							id="work_agreement_status" cssClass="form-control"
							cssErrorClass="form-control error" readonly="true">
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
							code="lbl.status" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="work_status" id="work_status"
							cssClass="form-control" cssErrorClass="form-control error"
							readonly="true">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:option value="Pending">Pending</form:option>
							<form:option value="Ongoing">Ongoing</form:option>
							<form:option value="Complete">Complete</form:option>
						</form:select>
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.category" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="category"
							readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.sector.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control" path="sector"
							readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.estimated.cost" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control"
							path="estimatedCost" readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.work.type" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="workType"
							readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.tendered.cost" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="tenderCost"
							readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.fund" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control" path="fund"
							readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.work.location" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="workLocation"
							readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.agency.work.order" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control"
							path="agencyWorkOrder" readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="date" path="date" class="form-control datepicker"
							data-date-end-date="0d" placeholder="DD/MM/YYYY" readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.time.limit" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="timeLimit"
							readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.category" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="category"
							readonly="true" />
					</div>

				</div>
			</div>
		</div>
		<!-- ===========contractor here below======== -->

		<div class="panel panel-primary" data-collapsed="0"
			style="scrollable: true;">
			<div class="panel-heading">
				<div class="panel-title">
					<spring:message code="lbl.work.contractor.details"
						text="Contractor Details" />
				</div>
			</div>
			<br>
			<div>
				<label class="col-sm-3 control-label text-left-audit"><spring:message
						code="lbl.name" /></label>
				<div class="col-sm-3 add-margin">
					<form:select path="contractor_name" id="contractor_name"
						cssClass="form-control" cssErrorClass="form-control error"
						readonly="true">
						<form:option value="">
							<spring:message code="lbl.select" />
						</form:option>
						<form:option value="abc">ABC</form:option>
						<form:option value="def">DEF</form:option>
					</form:select>
				</div>

				<label class="col-sm-3 control-label text-left-audit"><spring:message
						code="lbl.code" /></label>
				<div class="col-sm-3 add-margin">
					<form:select path="contractor_code" id="contractor_code"
						cssClass="form-control" cssErrorClass="form-control error"
						readonly="true">
						<form:option value="">
							<spring:message code="lbl.select" />
						</form:option>
						<form:option value="abc">ABC</form:option>
						<form:option value="def">DEF</form:option>
					</form:select>
				</div>

				<label class="col-sm-3 control-label text-left-audit"><spring:message
						code="lbl.adddress" /></label>
				<div class="col-sm-3 add-margin">
					<form:input type="text" class="form-control"
						path="contractor_address" readonly="true" />
				</div>

				<label class="col-sm-3 control-label text-left-audit"><spring:message
						code="lbl.phone" /></label>
				<div class="col-sm-3 add-margin">
					<form:input type="text" class="form-control"
						path="contractor_phone" readonly="true" />
				</div>

				<label class="col-sm-3 control-label text-left-audit"><spring:message
						code="lbl.email" /></label>
				<div class="col-sm-3 add-margin">
					<form:input type="text" class="form-control"
						path="contractor_email" readonly="true" />
				</div>
				<br> <br> <br> <br> <br> <br>
			</div>
		</div>

		<br> <br> <br>

		<!-- ========================code end=========== -->

		<!-- ===========boq here below======== -->


		<div class="panel panel-primary" data-collapsed="0"
			style="scrollable: true;">
			<div class="panel-heading">
				<div class="panel-title">
					<spring:message code="lbl.work.boq" text="BoQ Document" />
				</div>
				<br>
				<div>
					<div>
						<table id="table" class="table table-bordered">
							<thead>
								<tr>
									<th><spring:message code="lbl.item.description" /></th>
									<th><spring:message code="lbl.item.description" /></th>
									<th><spring:message code="lbl.ref.dsr" /></th>
									<th><spring:message code="lbl.unit" /></th>
									<th><spring:message code="lbl.rate" /></th>
									<th><spring:message code="lbl.quantity" /></th>
									<th><spring:message code="lbl.measured.quantity" /></th>
									<th><spring:message code="lbl.amount" /></th>
									<th><spring:message code="lbl.measured.amount" /></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="boq"
									items="${workOrderAgreement.boQDetailsList}" varStatus="status">
									<tr id="detailsrow" class="repeat-address">
									<td><form:input type="text"
												path="boQDetailsList[${status.index}].slNo"
												id="boQDetailsList[${status.index}].slNo"
												required="required" class="form-control slNo"
												maxlength="200" readonly="true"></form:input></td>
												
										<td><form:input type="text"
												path="boQDetailsList[${status.index}].item_description"
												id="boQDetailsList[${status.index}].item_description"
												required="required" class="form-control item_description"
												maxlength="200" readonly="true"></form:input></td>
										<td><form:input type="text"
												path="boQDetailsList[${status.index}].ref_dsr"
												id="boQDetailsList[${status.index}].ref_dsr"
												required="required" class="form-control ref_dsr"
												maxlength="200" readonly="true"></form:input></td>
										<td><form:input type="text"
												path="boQDetailsList[${status.index}].unit"
												id="boQDetailsList[${status.index}].unit"
												required="required" class="form-control unit"
												maxlength="200" readonly="true"></form:input></td>
										<td><form:input type="number"
												path="boQDetailsList[${status.index}].rate" step=".01"
												id="boQDetailsList[${status.index}].rate"
												required="required" class="form-control rate"
												readonly="true" onchange="valueChanged()"></form:input></td>
										<td><form:input type="number"
												path="boQDetailsList[${status.index}].quantity" step=".01"
												id="boQDetailsList[${status.index}].quantity"
												required="required" class="form-control quantity"
												name="quantity" readonly="true"></form:input></td>

										<td><form:input type="number"
												path="boQDetailsList[${status.index}].measured_quantity"
												step=".01"
												id="boQDetailsList[${status.index}].measured_quantity"
												required="required" class="form-control measured_quantity"
												name="measured_quantity" onchange="valueChanged()"></form:input></td>

										<td><form:input type="number"
												path="boQDetailsList[${status.index}].amount"
												id="boQDetailsList[${status.index}].amount"
												required="required" class="form-control amount"
												maxlength="200" name="amount" readonly="true"></form:input>
										</td>
										<td><form:input type="number"
												path="boQDetailsList[${status.index}].measured_amount"
												id="boQDetailsList[${status.index}].measured_amount"
												required="required" class="form-control measured_amount"
												maxlength="200" name="measured_amount" readonly="true"></form:input>
										</td>
									</tr>
								</c:forEach>
							</tbody>

						</table>
					</div>
				</div>
			</div>
		</div>

		<!-- ========================code end=========== -->
		<br> <br>
		<div class="buttonbottom" align="center">
			<input type="submit" id="workmeasured" class="btn btn-primary" name="workmeasured"
				code="lbl.select" value="Save Work Order/Agreement Creation" />
		</div>

	</div>
</form:form>


<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
<script type="text/javascript">
	function valueChanged() {
		var estimateAmt = 0;
		for (var i = 1; i < table.rows.length; i++) {
			// get the seected row index
			rIndex = i;

			var rate = document.getElementById("boQDetailsList[" + (rIndex - 1)
					+ "].rate").value;

			var quantity = document.getElementById("boQDetailsList["
					+ (rIndex - 1) + "].measured_quantity").value;

			var amt = quantity * rate;
			document.getElementById("boQDetailsList[" + (rIndex - 1)
					+ "].measured_amount").value = amt;

		}
	}
</script>

