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
	<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js" integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj" crossorigin="anonymous">
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
<meta charset="UTF-8">
<title>BoQ</title>
</head>
<body>

	<form:form name="edit-work-agreement" role="form" method="post"
		action="work1" modelAttribute="workOrderAgreement"
		id="workOrderAgreement" enctype="multipart/form-data">

		<spring:hasBindErrors name="workOrderAgreement">
			<div class="alert alert-danger"
				style="margin-top: 20px; margin-bottom: 10px;">
				<form:errors path="*" />
				<br />
			</div>
		</spring:hasBindErrors>

		<div class="panel panel-primary">
			<div class="container">
				<div class="row">

					<div class="col-md-6">
						<input type="hidden" name="workOrderAgreement"
							value="${workOrderAgreement.id}" />
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.name.work" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="text" class="form-control txtRight"
									path="name_work_order" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.estimate.number" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="text" class="form-control txtRight"
									path="work_number" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.start.date" /></label>
							<div class="col-md-6 block-colm">
								<%-- <form:input type="date" class="form-control txtRight"
									path="work_start_date" /> --%>
								<form:input id="work_start_date" path="work_start_date"
									class="form-control datepicker" data-date-end-date="0d"
									placeholder="DD/MM/YYYY" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.intended.date" /></label>
							<div class="col-md-6 block-colm">
								<%-- <form:input type="date" class="form-control txtRight"
									path="work_intended_date" /> --%>
								<form:input id="work_intended_date" path="work_intended_date"
									class="form-control datepicker" data-date-end-date="0d"
									placeholder="DD/MM/YYYY" />
							</div>
							<div class="clearfix"></div>

						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.extended.date" /></label>
							<div class="col-md-6 block-colm">
								<%-- <form:input type="date" class="form-control txtRight"
									path="work_end_date" /> --%>
								<form:input id="work_end_date" path="work_end_date"
									class="form-control datepicker" data-date-end-date="0d"
									placeholder="DD/MM/YYYY" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.executing.department" /></label>
							<div class="col-md-6 block-colm">
								<form:select path="department" id="department"
									class="form-control">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:options items="${workOrderAgreement.departments}"
										itemValue="code" itemLabel="name" />
								</form:select>
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.amount" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="text" class="form-control txtRight"
									path="work_amount" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.work.details" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="text" class="form-control txtRight"
									path="work_details" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.agreement.details" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="text" class="form-control txtRight"
									path="agreement_details" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.agreement.status" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="text" class="form-control txtRight"
									path="work_agreement_status" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.status" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="text" class="form-control txtRight"
									path="work_status" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<!-- -------------------------- -->
					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.work.order.search.category" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="text" class="form-control txtRight"
									path="category" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<%-- <div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.work.order.search.work.location" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="text" class="form-control txtRight"
									path="workLocation" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div> --%>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.work.order.search.sector.number" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="text" class="form-control txtRight"
									path="sector" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.work.order.search.estimated.cost" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="text" class="form-control txtRight"
									path="estimatedCost" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.work.order.search.work.type" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="text" class="form-control txtRight"
									path="workType" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.work.order.search.tendered.cost" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="text" class="form-control txtRight"
									path="tenderCost" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.work.order.search.fund" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="text" class="form-control txtRight"
									path="fund" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.work.order.search.work.location" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="text" class="form-control txtRight"
									path="workLocation" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.work.order.search.agency.work.order" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="text" class="form-control txtRight"
									path="agencyWorkOrder" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.work.order.search.date" /></label>
							<div class="col-md-6 block-colm">
								<%-- <form:input type="date" class="form-control txtRight"
									path="date" /> --%>
								<form:input id="date" path="date"
									class="form-control datepicker" data-date-end-date="0d"
									placeholder="DD/MM/YYYY" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.work.order.search.time.limit" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="text" class="form-control txtRight"
									path="timeLimit" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.work.order.search.category" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="text" class="form-control txtRight"
									path="category" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>



					<!-- ---------------------------------- -->
				</div>
			</div>
		</div>

		<br />

		<div class="panel panel-primary">
			<div class="container">
				<div>
					<p style="color: #4e799f; font-size: 25px;">Contractor Details</p>
				</div>
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.name" /></label>
							<div class="col-md-6 block-colm">
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
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.code" /></label>
							<div class="col-md-6 block-colm">
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
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.adddress" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="text" class="form-control txtRight"
									path="contractor_address" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.phone" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="text" class="form-control txtRight"
									path="contractor_phone" />
							</div>
							<div class="clearfix"></div>

						</div>
					</div>


					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.email" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="text" class="form-control txtRight"
									path="contractor_email" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

				</div>
			</div>
		</div>

		<br />

		<div class="panel panel-primary">
			<div class="container">
				<div>
					<p style="color: #4e799f; font-size: 25px;">Boq Document</p>
				</div>

				<div class="row">
					<div class="col-md-5">
						<h4>BoQ Details</h4>
						<button onclick="addFileInputField();" class="btn-info"
							style="margin-bottom: 15px; float: left;" id="plus">Add
							new row</button>
					</div>
				</div>

				<div class="tab tab-1">

					<table id="table" border="1" cellpadding="10" style="width: 100%">
						<thead>
							<tr>
								
								<th><spring:message code="lbl.item.description" /></th>
								<th><spring:message code="lbl.ref.dsr" /></th>
								<th><spring:message code="lbl.unit" /></th>
								<th><spring:message code="lbl.rate" /></th>
								<th><spring:message code="lbl.quantity" /></th>
								<th><spring:message code="lbl.amount" /></th>
								<th><spring:message code="lbl.amount" /></th>
								<th><spring:message code="lbl.item.description" /></th>
								<th><spring:message code="lbl.action" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="boq" items="${workOrderAgreement.boQDetailsList}"
								varStatus="status">
								<tr id="detailsrow" class="repeat-address">
									
									<td>
										 <form:input type="text"
											path="boQDetailsList[${status.index}].item_description"
											id="boQDetailsList[${status.index}].item_description"
											required="required" class="form-control item_description"
											maxlength="200"></form:input>

									</td>
									<td>
										<form:input type="text"
											path="boQDetailsList[${status.index}].ref_dsr"
											id="boQDetailsList[${status.index}].ref_dsr"
											required="required" class="form-control ref_dsr"
											maxlength="200"></form:input>

									</td>
									<td>
										<form:input type="text"
											path="boQDetailsList[${status.index}].unit"
											id="boQDetailsList[${status.index}].unit" required="required"
											class="form-control unit" maxlength="200"></form:input>
									<td>
										<form:input type="number"
											path="boQDetailsList[${status.index}].rate" step=".01"
											id="boQDetailsList[${status.index}].rate" required="required"
											class="form-control rate" onchange="valueChanged();"></form:input>

									</td>
									<td>
										 <form:input type="number"
											path="boQDetailsList[${status.index}].quantity" step=".01"
											id="boQDetailsList[${status.index}].quantity"
											required="required" class="form-control quantity"
											name="quantity" onchange="valueChanged();"></form:input>

									</td>
									<td><form:input type="number"
											path="boQDetailsList[${status.index}].amount"
											id="boQDetailsList[${status.index}].amount"
											required="required" class="form-control amount"
											maxlength="200" name="amount" readonly="true"></form:input> 
									</td>
									<td><form:input type="number"
											path="boQDetailsList[${status.index}].workOrderAgreement.id"
											id="boQDetailsList[${status.index}].workOrderAgreement.id"
											required="required" class="form-control amount"
											maxlength="200" name="workOrderAgreement.id" readonly="true"></form:input>
										
									</td>
									<td><form:hidden
											path="boQDetailsList[${status.index}].slNo"
											id="boQDetailsList[${status.index}].slNo" /> ${boq.slNo }
									</td>
									<td>
										<button onclick="deleteRow(this);" class="btn-info"
											style="margin-bottom: 15px; float: left;" id="plus">-</button>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<br /> <br />
					<div class="vertical-center">
						<input type="submit" id="work1" class="btn-info" name="work1"
							code="lbl.select" value="Save Work Order/Agreement Creation" />
					</div>
				</div>
			</div>
		</div>
	</form:form>


	<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>

	<script type="text/javascript">
		function valueChanged() {
			var  estimateAmt = 0;
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

</body>
</html>