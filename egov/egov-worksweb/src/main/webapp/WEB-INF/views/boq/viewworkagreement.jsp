<%@ include file="/includes/taglibs.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>
<style>
.table thead > tr > th {
    color: black;
    background-color: #acbfd0;
    vertical-align: top;
}
.table tbody > tr > td {
    color: black;
    vertical-align: top;
}
</style> 
<script
        src="<cdn:url value='/resources/js/estimateworks.js?rnd=${app_release_no}' context='/services/works'/>"></script>
<form:form name="workOrderAgreementForm" role="form" method="post"
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
					<div class="col-sm-9 add-margin">
							<form:textarea class="form-control" path="name_work_order" maxlength="2000" style="height: 100px;" readonly="true"
					 />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.dnit.number" /></label>
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
							code="lbl.actualstart.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="actual_start_date" path="actual_start_date" readonly="true"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.actualend.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="actual_end_date" path="actual_end_date" readonly="true"
							class="form-control datepicker" data-date-end-date="0d"
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
							code="lbl.work.order.search.sector.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="sector" readonly="true" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.ward.number" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" class="form-control" path="wardNumber" readonly="true" />
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
					<label class="col-sm-3 control-label text-left-audit"></label>
			<div class="col-sm-3 add-margin">
				<input type="text" class="form-control" style="visibility:hidden"
									 />
							</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.project.closure.comments" /></label>
					<div class="col-sm-9 add-margin">
					<form:textarea class="form-control" path="project_closure_comments" maxlength="2000" style="height: 100px;" readonly="true"
					 />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.contractor.comments" /></label>
					<div class="col-sm-9 add-margin">
					<form:textarea class="form-control" path="contractor_performance_comments" maxlength="2000" style="height: 100px;" readonly="true"
					 />
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
<div class="panel panel-primary" data-collapsed="0"
			style="scrollable: true;">
			<div class="panel-heading">
				<div class="panel-title">
					 Milestones
				</div>
			</div>

<c:if test="${workOrderAgreement.paymentDistribution != null &&  !workOrderAgreement.paymentDistribution.isEmpty()}">
			
<div class="panel-body">
	<table class="table table-bordered" id="tblchecklist">
		<thead>
			<tr>
				<th>Milestone Details</th>
				<th>Payment Percentage(%)</th>
				<th>Amount</th>
				<th>Completed?</th>
								
			</tr>
		</thead>
		<tbody>
		
		<c:forEach items="${workOrderAgreement.paymentDistribution}" var="result" varStatus="status">

			<tr id="tblchecklistRow">
				<td>
				<form:input type="text" style="width:200px;" path="paymentDistribution[${status.index}].payment_desc"	id="boQDetailsList[${status.index}].milestone" readonly="true"
											 class="form-control payment_desc" ></form:input>
					</td>
				<td>
				
				<form:input type="text" style="width:200px;" path="paymentDistribution[${status.index}].payment_percent" readonly="true"	id="paymentDistribution[${status.index}].payment_percent" onchange="calcualtePerctAmount(this);"
											 class="form-control payment_percent" ></form:input>
											 
				</td>
				<td>
				<form:input type="text" style="width:100px;" path="paymentDistribution[${status.index}].amount" readonly="true" id="paymentDistribution[${status.index}].amount"
											 class="form-control amount" ></form:input>
				</td> 
				<td><form:checkbox 	path="paymentDistribution[${status.index}].payment_completed" readonly="true"	id="paymentDistribution[${status.index}].payment_completed" />
												</td> 
				
			</tr>
			</c:forEach>
		</tbody>
	</table>
</div>			
			
			
	</c:if>		
			
</div>
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
						<c:forEach var="mapboq" items="${milestoneList}" varStatus="mapstatus">
					<table id="boq${mapstatus.index}tableBoq" class="table table-bordered tableBoq">
				
				
				
							<thead>
								<tr>
							<th><c:out value="${mapboq.key}"/></th>
							</tr>
							<tr>
								<th><spring:message code="lbl.item.Milestone" /></th>	
									<th><spring:message code="lbl.item.description" /></th>
									<th><spring:message code="lbl.ref.dsr" /></th>
									<th><spring:message code="lbl.unit" /></th>
									<th><spring:message code="lbl.rate" /></th>
									<th><spring:message code="lbl.quantity" /></th>
									<th><spring:message code="lbl.amount" /></th>
								</tr>
							</thead>
						
				
							<tbody>
					
						
						<c:forEach var="boq" items="${mapboq.value}" varStatus="status">
						
						<%-- <c:if test="${mapboq.key == boq.milestone }"> --%>
								<tr id="boq${mapstatus.index}tableBoqrow" class="boq${status.index}repeat-address">
										<td>
								<form:hidden path="boQDetailsList[${boq.sizeIndex}].slNo"
												id="boQDetailsList[${boq.sizeIndex}].slNo" />
								<form:input type="text" style="width:150px;"
											path="boQDetailsList[${boq.sizeIndex}].milestone"
											id="boQDetailsList[${boq.sizeIndex}].milestone"
											required="required" readonly="true" class="form-control milestone" title="${boq.milestone}"></form:input></td>
									<td><form:input type="text" style="width:200px;"
											path="boQDetailsList[${boq.sizeIndex}].item_description"
											id="boQDetailsList[${boq.sizeIndex}].item_description"
											required="required" readonly="true" class="form-control item_description"
											 title="${boq.item_description}"></form:input></td>
									<td><form:input type="text" style="width:80px;"
											path="boQDetailsList[${boq.sizeIndex}].ref_dsr"
											id="boQDetailsList[${boq.sizeIndex}].ref_dsr"
												required="required" class="form-control ref_dsr"
											maxlength="200" readonly="true" title="${boq.ref_dsr}"></form:input></td>
									<td><form:input type="text" style="width:80px;"
											path="boQDetailsList[${boq.sizeIndex}].unit"
												id="boQDetailsList[${boq.sizeIndex}].unit"
												required="required" readonly="true" class="form-control unit"
												maxlength="200"></form:input></td>
									<td><form:input type="number" style="width:100px;"
											path="boQDetailsList[${boq.sizeIndex}].rate" step=".01"
												id="boQDetailsList[${boq.sizeIndex}].rate"
												required="required" readonly="true" class="form-control rate"
												onchange="valueChanged()"></form:input></td>
									<td><form:input type="number" style="width:100px;"
											path="boQDetailsList[${boq.sizeIndex}].quantity" step=".01"
											id="boQDetailsList[${boq.sizeIndex}].quantity"
											required="required" readonly="true" class="form-control quantity"
												name="quantity" onchange="valueChanged()"></form:input></td>
										<td><form:input type="number" style="width:100px;"
											path="boQDetailsList[${boq.sizeIndex}].amount"
											id="boQDetailsList[${boq.sizeIndex}].amount"
											required="required" readonly="true" class="form-control amount"
											maxlength="200" name="amount" ></form:input></td>
									</tr>
								</c:forEach>
							
							</tbody>
				</table>
				
					

				</c:forEach>
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
		</div>

	</div>
</form:form>


<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>

<script type="text/javascript">
	function valueChanged() {

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

