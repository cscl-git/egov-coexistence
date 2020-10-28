<%@ include file="/includes/taglibs.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<script
        src="<cdn:url value='/resources/js/estimateworks.js?rnd=${app_release_no}' context='/services/works'/>"></script>
<form:form name="workOrderAgreementForm" role="form" method="post"
	action="/services/works/boq/progress/updateProgress" modelAttribute="workOrderAgreement"
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
					<div class="col-sm-9 add-margin">
							<form:textarea class="form-control" path="name_work_order" maxlength="2000" style="height: 100px;" readonly="true"
					 />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="work_number" readonly="true" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.work.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="work_agreement_number" readonly="true" />
					</div>
					
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.start.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="work_start_date" path="work_start_date"
							class="form-control datepicker" data-date-end-date="0d" readonly="true"
							placeholder="DD/MM/YYYY" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.intended.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="work_intended_date" path="work_intended_date"
							class="form-control datepicker" data-date-end-date="0d" readonly="true"
							placeholder="DD/MM/YYYY" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.executing.department" /><span class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
						<form:select path="department" id="department" readonly="true"
							class="form-control" required="required">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:options items="${workOrderAgreement.departments}"
								itemValue="code" itemLabel="name" />
						</form:select>
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.amount.wrk" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control" path="work_amount"  readonly="true"/>
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.details" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="work_details" readonly="true" />
					</div>


					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.category" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="category" id="category"
									cssClass="form-control" cssErrorClass="form-contro error" readonly="true"
									>
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:option value="Road Work">Road Work </form:option>
									<form:option value="Bridge Work">Bridge Work</form:option>
									<form:option value="Maintaince Work">Maintaince Work</form:option>
								</form:select>
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.sector.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="sector" id="sector" readonly="true"
									cssClass="form-control" cssErrorClass="form-control error">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:option value="Sector 1">Sector 1 </form:option>
									<form:option value="Sector 2">Sector 2</form:option>
									<form:option value="Sector 3">Sector 3</form:option>
								</form:select>
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.ward.number" /></label>
			<div class="col-sm-3 add-margin">
				<form:select path="wardNumber" id="wardNumber" readonly="true"
									cssClass="form-control" cssErrorClass="form-control error">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:option value="Ward 1">Ward 1 </form:option>
									<form:option value="Ward 2">Ward 2</form:option>
									<form:option value="Ward 3">Ward 3</form:option>
								</form:select>
							</div>

					


					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.fund" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="fund" id="fund" readonly="true"
									cssClass="form-control" cssErrorClass="form-control error">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:option value="Municipal Fund">Municipal Fund</form:option>
									<form:option value="Earmarked Fund">Earmarked Fund</form:option>
								</form:select>
							</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.work.location" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="workLocation" readonly="true" />
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
						code="lbl.name" /></label>
				<div class="col-sm-3 add-margin">
					 <form:select path="contractor_name" id="contractor_name" readonly="true"
						class="form-control"  onchange="getContractorDetails(this)">
						<form:option value="">
							<spring:message code="lbl.select" text="Select" />
						</form:option>
						<form:options items="${workOrderAgreement.contractors}" itemValue="id"
							itemLabel="name" />
					</form:select>

				</div>

				<label class="col-sm-3 control-label text-left-audit"><spring:message
						code="lbl.code" /></label>
				<div class="col-sm-3 add-margin">
					<form:input type="text" class="form-control" id="contractor_code" readonly="true"
						path="contractor_code" />
				</div>

				<label class="col-sm-3 control-label text-left-audit"><spring:message
						code="lbl.adddress" /></label>
				<div class="col-sm-3 add-margin">
					<form:input type="text" class="form-control" id="contractor_address"
						path="contractor_address" readonly="true"/>
				</div>

				<label class="col-sm-3 control-label text-left-audit"><spring:message
						code="lbl.phone" /></label>
				<div class="col-sm-3 add-margin">
					<form:input type="text" class="form-control" id="contractor_phone"
						path="contractor_phone" readonly="true"/>
				</div>

				<label class="col-sm-3 control-label text-left-audit"><spring:message
						code="lbl.email" /></label>
				<div class="col-sm-3 add-margin">
					<form:input type="text" class="form-control" id ="contractor_email"
						path="contractor_email" readonly="true"/>
				</div>
				<br> <br> <br> <br> <br> <br>
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

					<c:if test="${fileuploadAllowed != 'Y' }">
					<a target="_blank" style="float:right;"
							href="/services/works/resources/app/formats/BOQ_Upload_Format.xlsx"><img style="height:30px;" title="BoQ Upload Format" src="/services/egi/resources/erp2/images/download.gif" border="0" /></a>
					<br>
					<input type="file" name="file" style="color: #000000;"> <br>
					<br>
					<div class="buttonbottom" align="center">
						<input type="submit" id="save" class="btn btn-primary" name="save"
							value="Upload" /> <br>
					</div>
					</c:if>
					<div>
					<c:if test="${fileuploadAllowed == 'Y' }">
						<table id="table" class="table table-bordered">
							<thead>
								<tr>
									<th><spring:message code="lbl.item.description" /></th>
									<th><spring:message code="lbl.ref.dsr" /></th>
									<th><spring:message code="lbl.unit" /></th>
									<th><spring:message code="lbl.rate" /></th>
									<th><spring:message code="lbl.quantity" /></th>
									<th><spring:message code="lbl.measure.quantity" /></th>
									<th><spring:message code="lbl.amount" /></th>
									<th><spring:message code="lbl.measure.amount" /></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="boq"
									items="${workOrderAgreement.boQDetailsList}" varStatus="status">
									<tr id="detailsrow" class="repeat-address">
										<td>
										<form:hidden path="boQDetailsList[${status.index}].slNo" id="boQDetailsList[${status.index}].slNo"/>
										<form:input type="text" readonly="true"
												path="boQDetailsList[${status.index}].item_description" style="width:300px;"
												id="boQDetailsList[${status.index}].item_description"
												required="required" class="form-control item_description"
												maxlength="200"></form:input></td>
										<td><form:input type="text" readonly="true"
												path="boQDetailsList[${status.index}].ref_dsr" style="width:150px;"
												id="boQDetailsList[${status.index}].ref_dsr"
												required="required" class="form-control ref_dsr"
												maxlength="200"></form:input></td>
										<td><form:input type="text" readonly="true"
												path="boQDetailsList[${status.index}].unit"
												id="boQDetailsList[${status.index}].unit"
												required="required" class="form-control unit"
												maxlength="200"></form:input></td>
										<td><form:input type="number" readonly="true"
												path="boQDetailsList[${status.index}].rate" step=".01"
												id="boQDetailsList[${status.index}].rate"
												required="required" class="form-control rate"
												onchange="valueChanged()"></form:input></td>
										<td><form:input type="number" readonly="true"
												path="boQDetailsList[${status.index}].quantity" step=".01"
												id="boQDetailsList[${status.index}].quantity"
												required="required" class="form-control quantity"
												name="quantity" onchange="valueChanged()"></form:input></td>
										<td><form:input type="number" 
												path="boQDetailsList[${status.index}].measured_quantity" step=".01"
												id="boQDetailsList[${status.index}].measured_quantity"
												required="required" class="form-control quantity"
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
												required="required" class="form-control amount"
												maxlength="200" name="measured_amount" readonly="true"></form:input>
										</td>
										
									</tr>
								</c:forEach>
							</tbody>

						</table>
						</c:if>
					</div>
				</div>
			</div>
		</div>

		<!-- ========================code end=========== -->

		<jsp:include page="fileupload.jsp" />
		<br> <br>
		<jsp:include page="../common/commonWorkflowhistory-view.jsp" /> 
		<br> <br>
				<div class="buttonbottom" align="center">
				<input type="submit" id="workEstimateSearch" class="btn btn-primary"
						name="workEstimateSearch" code="lbl.search.work.estimate"
						value="Save and Update" />
		</div>

	</div>
</form:form>


<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>

<script type="text/javascript">
	function valueChanged() {
debugger;
		for (var i = 1; i < table.rows.length; i++) {
			// get the seected row index
			rIndex = i;

			var rate = document.getElementById("boQDetailsList[" + (rIndex - 1)
					+ "].rate").value;

			var quantity = document.getElementById("boQDetailsList["
					+ (rIndex - 1) + "].quantity").value;
			var measured_quantity=0;
			if(document.getElementById("boQDetailsList["
					+ (rIndex - 1) + "].measured_quantity").value != null && document.getElementById("boQDetailsList["
							+ (rIndex - 1) + "].measured_quantity").value != '')
				{
				measured_quantity =document.getElementById("boQDetailsList["
						+ (rIndex - 1) + "].measured_quantity").value;
				}
			else
				{
				document.getElementById("boQDetailsList["
						+ (rIndex - 1) + "].measured_quantity").value=measured_quantity;
				}

			var amt = quantity * rate;
			var measuredAmt= measured_quantity * rate;
			document.getElementById("boQDetailsList[" + (rIndex - 1)
					+ "].amount").value = amt;
			document.getElementById("boQDetailsList[" + (rIndex - 1)
					+ "].measured_amount").value = measuredAmt;
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
	
	function getContractorDetails(obj){
		var id=obj.value;
		$.ajax({
			type : "GET",
			data: 'html',
			url : "/services/works/boq/contractorid/"+id,
			success : function(result) {
				console.log("success : "+result);
				$(result).each(function(i, obj) 
			    {
					var contractor_email=obj.email;
					var contractor_phone=obj.mobileNumber;
					var contractor_address=obj.correspondenceAddress;
					var contractor_code=obj.code;
					
					($('#contractor_email').val(contractor_email));
					($('#contractor_phone').val(contractor_phone));
					($('#contractor_address').val(contractor_address));
					($('#contractor_code').val(contractor_code));
				    
				});
			}
		});
	}
</script>

