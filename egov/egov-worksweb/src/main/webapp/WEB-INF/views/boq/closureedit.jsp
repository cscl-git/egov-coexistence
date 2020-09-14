<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>


<form:form name="closure-work-agreement" role="form" method="post"
	modelAttribute="workOrderAgreement" id="workOrderAgreement"
	enctype="multipart/form-data">
	<div class="panel panel-primary">
		<div class="container">
			<div class="row">
				<input type="hidden" name="workOrderAgreement"
					value="${workOrderAgreement.id}" />
				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.name.work" /></label>
						<div class="col-md-6 block-colm">
							<form:input type="text" class="form-control txtRight"
								path="name_work_order" readonly="true" />
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
								path="work_number" readonly="true" />
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
							<form:input type="date" class="form-control txtRight"
								path="work_start_date" readonly="true" />
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
							<form:input type="date" class="form-control txtRight"
								path="work_intended_date" readonly="true" />
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
							<form:input type="date" class="form-control txtRight"
								path="work_end_date" readonly="true" />
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
								class="form-control" readonly="true">
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
							<form:input type="number" class="form-control txtRight"
								path="work_amount" readonly="true" />
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
								path="work_details" readonly="true" />
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
								path="agreement_details" readonly="true" />
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
								path="work_agreement_status" readonly="true" />
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
								path="work_status" readonly="true" />
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
								path="category" readonly="true" />
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
							<form:input type="number" class="form-control txtRight"
								path="sector" readonly="true" />
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
							<form:input type="number" class="form-control txtRight"
								path="estimatedCost" readonly="true" />
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
								path="workType" readonly="true" />
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
								path="tenderCost" readonly="true" />
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
							<form:input type="number" class="form-control txtRight"
								path="fund" readonly="true" />
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
								path="workLocation" readonly="true" />
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
								path="agencyWorkOrder" readonly="true" />
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
							<form:input type="date" class="form-control txtRight" path="date" />
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
								path="timeLimit" readonly="true" />
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
								path="category" readonly="true" />
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
								readonly="true">
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
								readonly="true">
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
								path="contractor_address" readonly="true" />
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
								path="contractor_phone" readonly="true" />
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
								path="contractor_email" readonly="true" />
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
				<p style="color: #4e799f; font-size: 25px;">BOQ Document</p>
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

						</tr>
					</thead>
					<tbody>
						<c:forEach var="boq" items="${workOrderAgreement.boQDetailsList}"
							varStatus="status">
							<tr id="detailsrow" class="repeat-address">
								<td><form:hidden
										path="boQDetailsList[${status.index}].item_description"
										id="boQDetailsList[${status.index}].item_description" />
									${boq.item_description}</td>
								<td><form:hidden
										path="boQDetailsList[${status.index}].ref_dsr"
										id="boQDetailsList[${status.index}].ref_dsr" />
									${boq.ref_dsr}</td>
								<td><form:hidden
										path="boQDetailsList[${status.index}].unit"
										id="boQDetailsList[${status.index}].unit" /> ${boq.unit}</td>
								<td><form:hidden
										path="boQDetailsList[${status.index}].rate"
										id="boQDetailsList[${status.index}].rate" /> ${boq.rate}</td>
								<td><form:hidden
										path="boQDetailsList[${status.index}].quantity"
										id="boQDetailsList[${status.index}].quantity" />
									${boq.quantity}</td>
								<td><form:hidden
										path="boQDetailsList[${status.index}].amount"
										id="boQDetailsList[${status.index}].amount" /> ${boq.amount}
								</td>

							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>

	<div class="panel panel-primary">
		<div class="container">
			<div>
				<p style="color: #4e799f; font-size: 25px;">Closure</p>
			</div>

			<div>
				<%-- <c:if
					test="${workOrderAgreement.status.code == 'set code here' }"> --%>
				<div class="row">
					<div class="col-md-3">
						<%-- <form:checkbox path="id"/> --%>
						</td>
					</div>
					<div class="col-md-9">
						<div class="show-row form-group">
							<label class="col-sm-3 control-label text-right"><spring:message
									code="lbl.comments" text="Comments" /></label>
							<div class="col-sm-9 add-margin">
								<form:textarea class="form-control" />
							</div>
						</div>

					</div>
				</div>

				<%-- </c:if> --%>
			</div>
		</div>
	</div>
	<br />
	<div class="vertical-center">
		<input type="submit" id="work1" class="btn-info" name="work1"
			code="lbl.select" value="Save Work Order/Agreement Creation" />
	</div>

</form:form>


<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>

