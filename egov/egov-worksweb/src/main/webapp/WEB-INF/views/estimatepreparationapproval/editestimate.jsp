<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<script
        src="<cdn:url value='/resources/js/estimateworks.js?rnd=${app_release_no}' context='/services/works'/>"></script>

		<spring:hasBindErrors name="estimatePreparationApproval">
			<div class="alert alert-danger"
				style="margin-top: 20px; margin-bottom: 10px;">
				<form:errors path="*" />
				<br />
			</div>
		</spring:hasBindErrors>


<div class="tab-pane fade in active">
	<div class="panel panel-primary" data-collapsed="0">
		<div class="form-group" style="padding: 50px 20px 0;">

					<input type="hidden" name="estimatePreparationApproval"
				value="${estimatePreparationApproval.id}" /> <label
				class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.works.wing" /><span
								class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:select path="worksWing" id="worksWing" cssClass="form-control"
					cssErrorClass="form-control error" required="required">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:option value="1">Building & Roads</form:option>
									<form:option value="2">Public Health</form:option>
									<form:option value="3">Horticulture & Electrical</form:option>
								</form:select>
							</div>
			<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.executing.division" /><span
								class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:select path="department" id="department" class="form-control"
					required="required">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
					<form:options items="${estimatePreparationApproval.departments}"
										itemValue="code" itemLabel="name" />
								</form:select>
							</div>
			<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.estimate.date" /><span
								class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
									<form:input id="estimateDate" path="estimateDate"
									class="form-control datepicker" data-date-end-date="0d"
									placeholder="DD/MM/YYYY" />
								<form:errors path="estimateDt" cssClass="add-margin error-msg" />
							</div>
			<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.work.location" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" class="form-control" path="workLocation" />
							</div>

			<label class="col-sm-3 control-label text-left-audit"><spring:message
					code="lbl.estimate.preparation.estimate.number" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" class="form-control" path="estimateNumber"
					readonly="true" />
					</div>

			<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.sector.number" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="number" class="form-control" path="sectorNumber" />
							</div>
			<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.ward.number" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="number" class="form-control" path="wardNumber" />
							</div>
			<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.work.category" /><span
								class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
								<form:select path="workCategory" id="workCategory"
									cssClass="form-control" cssErrorClass="form-control error"
									required="required">
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
				<form:input type="number" class="form-control" path="estimateAmount" />
							</div>
			<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.estimate.prepared.by" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" path="estimatePreparedBy"
					id="estimatePreparedBy" cssClass="form-control"
					cssErrorClass="form-control error" required="required" />
							</div>
			<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.designation" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" class="form-control"
									path="preparationDesignation" />
							</div>
			<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.name.work" /><span
								class="mandatory"></span></label>
			<div class="col-sm-3 block-colm">
				<form:textarea class="form-control" path="workName" maxlength="2000"
					required="required" />
							</div>
			<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.necessity" /><span
								class="mandatory"></span></label>
			<div class="col-sm-3 block-colm">
				<form:input type="text" class="form-control" path="necessity"
					required="required" />
							</div>
			<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.scope.work" /><span
								class="mandatory"></span></label>
			<div class="col-sm-3 block-colm">
				<form:input type="text" class="form-control" path="workScope"
					required="required" />
			</div>
							</div>
						</div>
					</div>

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
										items="${estimatePreparationApproval.boQDetailsList}"
							varStatus="status">
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
										id="boQDetailsList[${status.index}].unit" required="required"
										class="form-control unit" maxlength="200"></form:input></td>
											<td><form:input type="number"
													path="boQDetailsList[${status.index}].rate" step=".01"
										id="boQDetailsList[${status.index}].rate" required="required"
										class="form-control rate" onchange="valueChanged()"></form:input></td>
											<td><form:input type="number"
													path="boQDetailsList[${status.index}].quantity" step=".01"
													id="boQDetailsList[${status.index}].quantity"
													required="required" class="form-control quantity"
													name="quantity" onchange="valueChanged()"></form:input></td>
											<td><form:input type="number"
													path="boQDetailsList[${status.index}].amount"
													id="boQDetailsList[${status.index}].amount"
													required="required" class="form-control amount"
										maxlength="200" name="amount" readonly="true"></form:input></td>
											<td>
									<button onclick="deleteRow(this);" class="btn btn-primary"
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
						+ (rIndex - 1) + "].quantity").value;

				var amt = quantity * rate;
				document.getElementById("boQDetailsList[" + (rIndex - 1)
						+ "].amount").value = amt;

			estimateAmt = estimateAmt + +amt;
			document.getElementById("estimateAmount").value = estimateAmt;
			}
		}

		function addFileInputField() {
			//var addressRow = $('.repeat-address').last();
			var addressRow = $('.repeat-address').first();
			var addressRowLength = $('.repeat-address').length;

		var newAddressRow = addressRow.clone(true).find("input").val("").end();

		$(newAddressRow).find("td input,td select").each(function(index, item) {
			item.name = item.name.replace(/[0-9]/g, addressRowLength);
					});

			newAddressRow.insertBefore(addressRow)
			//newAddressRow.insertAfter(addressRow);
		}

		function deleteRow(r) {
			var i = r.parentNode.parentNode.rowIndex;
			document.getElementById("table").deleteRow(i);
		}
	</script>