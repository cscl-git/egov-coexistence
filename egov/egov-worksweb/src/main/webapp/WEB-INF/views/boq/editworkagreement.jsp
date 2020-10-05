<%@ include file="/includes/taglibs.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<form:form name="edit-work-agreement" role="form" method="post"
	action="work1" modelAttribute="workOrderAgreement"
	id="workOrderAgreement" class="form-horizontal form-groups-bordered"
	enctype="multipart/form-data" style="margin-top:-20px;">

	<spring:hasBindErrors name="workOrderAgreement">
		<div class="alert alert-danger"
			style="margin-top: 20px; margin-bottom: 10px;">
			<form:errors path="*" />
			<br />
		</div>
	</spring:hasBindErrors>
	
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
							path="name_work_order" />
					</div>

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
						<form:input id="work_intended_date" path="work_intended_date"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.extended.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="work_end_date" path="work_end_date"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
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

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.amount" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control" path="work_amount" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.details" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="work_details" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.agreement.details" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control"
							path="agreement_details" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.agreement.status" /><span class="mandatory"></span></label>
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
							code="lbl.status" /><span class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
						<form:select path="work_status" id="work_status"
							cssClass="form-control" cssErrorClass="form-control error"
							required="required">
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
						<form:input type="text" class="form-control" path="category" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.sector.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control" path="sector" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.estimated.cost" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control"
							path="estimatedCost" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.work.type" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="workType" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.tendered.cost" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="tenderCost" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.fund" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control" path="fund" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.work.location" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="workLocation" />
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

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.category" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="category" />
					</div>

				</div>
			</div>
		</div>
		<!-- ===========boq here below======== -->


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
						code="lbl.name" /><span class="mandatory"></span></label>
				<div class="col-sm-3 add-margin">
					<form:select path="contractor_name" id="contractor_name"
						cssClass="form-control" cssErrorClass="form-control error"
						required="required">
						<form:option value="">
							<spring:message code="lbl.select" />
						</form:option>
						<form:option value="abc">ABC</form:option>
						<form:option value="def">DEF</form:option>
					</form:select>
				</div>

				<label class="col-sm-3 control-label text-left-audit"><spring:message
						code="lbl.code" /><span class="mandatory"></span></label>
				<div class="col-sm-3 add-margin">
					<form:select path="contractor_code" id="contractor_code"
						cssClass="form-control" cssErrorClass="form-control error"
						required="required">
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
						path="contractor_address" />
				</div>

				<label class="col-sm-3 control-label text-left-audit"><spring:message
						code="lbl.phone" /></label>
				<div class="col-sm-3 add-margin">
					<form:input type="text" class="form-control"
						path="contractor_phone" />
				</div>

				<label class="col-sm-3 control-label text-left-audit"><spring:message
						code="lbl.email" /></label>
				<div class="col-sm-3 add-margin">
					<form:input type="text" class="form-control"
						path="contractor_email" />
				</div>
				<br><br><br><br><br><br>
			</div>
		</div>

		<br>
		<br>
		<br>

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

					<button onclick="addFileInputField();" class="btn btn-primary"
						style="margin-bottom: 15px; float: right;" id="plus">+</button>

					<div>
						<table id="table" class="table table-bordered">
							<thead>
								<tr>
									<th><spring:message code="lbl.item.description" /></th>
									<th><spring:message code="lbl.ref.dsr" /></th>
									<th><spring:message code="lbl.unit" /></th>
									<th><spring:message code="lbl.rate" /></th>
									<th><spring:message code="lbl.quantity" /></th>
									<th><spring:message code="lbl.amount" /></th>
									<th><spring:message code="lbl.action" /></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="boq"
									items="${workOrderAgreement.boQDetailsList}" varStatus="status">
									<tr id="detailsrow" class="repeat-address">
										<td><form:input type="text"
												path="boQDetailsList[${status.index}].item_description"
												id="boQDetailsList[${status.index}].item_description"
												required="required" class="form-control item_description"
												maxlength="200"></form:input></td>
										<td><form:input type="text"
												path="boQDetailsList[${status.index}].ref_dsr"
												id="boQDetailsList[${status.index}].ref_dsr"
												required="required" class="form-control ref_dsr"
												maxlength="200"></form:input></td>
										<td><form:input type="text"
												path="boQDetailsList[${status.index}].unit"
												id="boQDetailsList[${status.index}].unit"
												required="required" class="form-control unit"
												maxlength="200"></form:input></td>
										<td><form:input type="number"
												path="boQDetailsList[${status.index}].rate" step=".01"
												id="boQDetailsList[${status.index}].rate"
												required="required" class="form-control rate"
												onchange="valueChanged()"></form:input></td>
										<td><form:input type="number"
												path="boQDetailsList[${status.index}].quantity" step=".01"
												id="boQDetailsList[${status.index}].quantity"
												required="required" class="form-control quantity"
												name="quantity" onchange="valueChanged()"></form:input></td>
										<td><form:input type="number"
												path="boQDetailsList[${status.index}].amount"
												id="boQDetailsList[${status.index}].amount"
												required="required" class="form-control amount"
												maxlength="200" name="amount" readonly="true"></form:input>
										</td>
										<td>
											<button onclick="deleteRow(this);" class="btn-info"
												style="margin-bottom: 15px; float: left;" id="plus">-</button>
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
			<input type="submit" id="work" class="btn btn-primary" name="work"
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

				var rate = document.getElementById("boQDetailsList["
						+ (rIndex - 1) + "].rate").value;

				var quantity = document.getElementById("boQDetailsList["
						+ (rIndex - 1) + "].quantity").value;

				var amt = quantity * rate;
				document.getElementById("boQDetailsList[" + (rIndex - 1)
						+ "].amount").value = amt;

				estimateAmt = estimateAmt + +amt;
				document.getElementById("estimatedCost").value = estimateAmt;
			}

		}

		function addFileInputField() {
			//var addressRow = $('.repeat-address').last();
			var addressRow = $('.repeat-address').first();
			var addressRowLength = $('.repeat-address').length;

			var newAddressRow = addressRow.clone(true).find("input").val("")
					.end();

			$(newAddressRow).find("td input,td select").each(
					function(index, item) {
						item.name = item.name.replace(/[0-9]/g,
								addressRowLength);
					});

			newAddressRow.insertBefore(addressRow)
			//newAddressRow.insertAfter(addressRow);
		}

		function deleteRow(r) {
			var i = r.parentNode.parentNode.rowIndex;
			document.getElementById("table").deleteRow(i);
		}
	</script>
