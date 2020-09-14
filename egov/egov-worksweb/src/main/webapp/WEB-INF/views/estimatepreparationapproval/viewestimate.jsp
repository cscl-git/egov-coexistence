<%@page import="java.util.ArrayList"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html lang="en">
<head>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"
	integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm"
	crossorigin="anonymous">
<script type="text/javascript">
	
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
	
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
	
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
	
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
	
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>
<!-- JS, Popper.js, and jQuery -->
	
</script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"
	integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
	crossorigin="anonymous"></script>
<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"
	integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN"
	crossorigin="anonymous"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"
	integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
	crossorigin="anonymous"></script>
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js" integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj" crossorigin="anonymous"></script>


<meta charset="UTF-8">

</head>
<body>

		<div class="panel panel-primary">
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
						<table id="table" border="1" cellpadding="10" style="width: 100%">
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
									<c:forEach
										items="${estimatePreparationApproval.boQDetailsList}"
										var="result" varStatus="status">
										<tr id="detailsrow" class="repeat-address">
											<td><form:hidden
													path="boQDetailsList[${status.index}].item_description"
													id="boQDetailsList[${status.index}].item_description" />
												${result.item_description }</td>
											<td><form:hidden
													path="boQDetailsList[${status.index}].ref_dsr"
													id="boQDetailsList[${status.index}].ref_dsr" />
												${result.ref_dsr }</td>
											<td><form:hidden
													path="boQDetailsList[${status.index}].unit"
													id="boQDetailsList[${status.index}].unit" /> ${result.unit }</td>
											<td><form:hidden
													path="boQDetailsList[${status.index}].rate"
													id="boQDetailsList[${status.index}].rate" /> ${result.rate }</td>
											<td><form:hidden
													path="boQDetailsList[${status.index}].quantity"
													id="boQDetailsList[${status.index}].quantity" />
												${result.quantity }</td>
											<td><form:hidden
													path="boQDetailsList[${status.index}].amount"
													id="boQDetailsList[${status.index}].amount" />
												${result.amount }</td>
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

</body>
</html>