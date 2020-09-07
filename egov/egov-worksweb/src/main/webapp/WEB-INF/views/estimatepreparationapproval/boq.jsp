<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<style>
.container {
	padding: 10px 50px 20px;
}

.card {
	/* Add shadows to create the "card" effect */
	box-shadow: 0 5px 9px 0 rgba(0, 0, 0, 0.7);
	padding: 20px 0;
}
</style>

<div class="card">
	<div class="container">
		<div class="row">
			<input type="hidden" name="estimatePreparationApproval"
				value="${estimatePreparationApproval.id}" />
			<div class="col-md-6">
				<div class="form-group">
					<label for="inputPassword"
						class="col-md-6 col-form-label block-colm"><spring:message
							code="lbl.estimate.preparation.works.wing" /></label>
					<div class="col-md-6 block-colm">
						${estimatePreparationApproval.worksWing}</div>
					<div class="clearfix"></div>
				</div>
			</div>

			<div class="col-md-6">
				<div class="form-group">
					<label for="inputPassword"
						class="col-md-6 col-form-label block-colm"><spring:message
							code="lbl.estimate.preparation.executing.division" /></label>
					<div class="col-md-6 block-colm">
						${estimatePreparationApproval.executingDivision}</div>
					<div class="clearfix"></div>
				</div>
			</div>

			<div class="col-md-6">
				<div class="form-group">
					<label for="inputPassword"
						class="col-md-6 col-form-label block-colm"><spring:message
							code="lbl.estimate.preparation.estimate.date" /></label>
					<div class="col-md-6 block-colm">
						${estimatePreparationApproval.estimateDate}</div>
					<div class="clearfix"></div>
				</div>
			</div>

			<div class="col-md-6">
				<div class="form-group">
					<label for="inputPassword"
						class="col-md-6 col-form-label block-colm"><spring:message
							code="lbl.estimate.preparation.estimate.number" /></label>
					<div class="col-md-6 block-colm">
						${estimatePreparationApproval.estimateNumber}</div>
					<div class="clearfix"></div>
				</div>
			</div>

			<div class="col-md-6">
				<div class="form-group">
					<label for="inputPassword"
						class="col-md-6 col-form-label block-colm"><spring:message
							code="lbl.estimate.preparation.work.location" /></label>
					<div class="col-md-6 block-colm">
						${estimatePreparationApproval.workLocation}</div>
					<div class="clearfix"></div>

				</div>
			</div>

			<div class="col-md-6">
				<div class="form-group">
					<label for="inputPassword"
						class="col-md-6 col-form-label block-colm"><spring:message
							code="lbl.estimate.preparation.sector.number" /></label>
					<div class="col-md-6 block-colm">
						${estimatePreparationApproval.sectorNumber}</div>
					<div class="clearfix"></div>
				</div>
			</div>

			<div class="col-md-6">
				<div class="form-group">
					<label for="inputPassword"
						class="col-md-6 col-form-label block-colm"><spring:message
							code="lbl.estimate.preparation.ward.number" /></label>
					<div class="col-md-6 block-colm">
						${estimatePreparationApproval.wardNumber}</div>
					<div class="clearfix"></div>
				</div>
			</div>

			<div class="col-md-6">
				<div class="form-group">
					<label for="inputPassword"
						class="col-md-6 col-form-label block-colm"><spring:message
							code="lbl.estimate.preparation.work.category" /></label>
					<div class="col-md-6 block-colm">
						${estimatePreparationApproval.workCategory}</div>
					<div class="clearfix"></div>
				</div>
			</div>

			<div class="col-md-6">
				<div class="form-group">
					<label for="inputPassword"
						class="col-md-6 col-form-label block-colm"><spring:message
							code="lbl.estimate.preparation.estimate.amount" /></label>
					<div class="col-md-6 block-colm">
						${estimatePreparationApproval.estimateAmount}</div>
					<div class="clearfix"></div>
				</div>
			</div>

			<div class="col-md-6">
				<div class="form-group">
					<label for="inputPassword"
						class="col-md-6 col-form-label block-colm"><spring:message
							code="lbl.estimate.preparation.estimate.prepared.by" /></label>
					<div class="col-md-6 block-colm">
						${estimatePreparationApproval.estimatePreparedBy}</div>
					<div class="clearfix"></div>
				</div>
			</div>

			<div class="col-md-6">
				<div class="form-group">
					<label for="inputPassword"
						class="col-md-6 col-form-label block-colm"><spring:message
							code="lbl.estimate.preparation.designation" /></label>
					<div class="col-md-6 block-colm">
						${estimatePreparationApproval.preparationDesignation}</div>
					<div class="clearfix"></div>

				</div>
			</div>




			<div class="col-md-12">
				<div class="form-group">
					<label for="inputPassword"
						class="col-md-3 col-form-label block-colm"><spring:message
							code="lbl.estimate.preparation.name.work" /></label>
					<div class="col-md-9 block-colm">
						${estimatePreparationApproval.workName}</div>
					<div class="clearfix"></div>
				</div>
			</div>

			<div class="col-md-12">
				<div class="form-group">
					<label for="inputPassword"
						class="col-md-3 col-form-label block-colm"><spring:message
							code="lbl.estimate.preparation.necessity" /></label>
					<div class="col-md-9 block-colm">
						${estimatePreparationApproval.necessity}</div>
					<div class="clearfix"></div>
				</div>
			</div>

			<div class="col-md-12">
				<div class="form-group">
					<label for="inputPassword"
						class="col-md-3 col-form-label block-colm"><spring:message
							code="lbl.estimate.preparation.scope.work" /></label>
					<div class="col-md-9 block-colm">
						${estimatePreparationApproval.workScope}</div>
					<div class="clearfix"></div>
				</div>
			</div>

			<div style="padding: 0 15px;">
						<table id="table" border="1" cellpadding="10">
					<thead>
						<tr>
							<th><spring:message code="lbl.item.description" /></th>
							<th><spring:message code="lbl.ref.dsr" /></th>
							<th><spring:message code="lbl.unit" /></th>
							<th><spring:message code="lbl.rate" /></th>
							<th><spring:message code="lbl.quantity" /></th>
							<th><spring:message code="lbl.amount" /></th>

						</tr>
					</thead>
					`
					<c:if
						test="${estimatePreparationApproval.boQDetailsList != null &&  !estimatePreparationApproval.boQDetailsList.isEmpty()}">
						<tbody>
							<c:forEach items="${estimatePreparationApproval.boQDetailsList}"
								var="result" varStatus="status">
								<tr id="detailsrow" class="repeat-address">
									<td><form:input type="text"
											path="boQDetailsList[${status.index}].slNo"
											id="boQDetailsList[${status.index}].slNo" required="required"
											class="form-control slNo" maxlength="200"></form:input></td>
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
								</tr>
							</c:forEach>
						<tbody>
					</c:if>
					<c:if
						test="${estimatePreparationApproval.boQDetailsList == null ||  estimatePreparationApproval.boQDetailsList.isEmpty()}">
					No records found
					</c:if>
				</table>
			</div>

		</div>
	</div>
</div>

<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>

	<script type="text/javascript">
		function valueChanged() {

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


