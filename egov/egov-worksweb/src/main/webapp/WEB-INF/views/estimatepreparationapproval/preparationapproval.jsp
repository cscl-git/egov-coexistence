
<%@ include file="/includes/taglibs.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>
<script
        src="<cdn:url value='/resources/js/estimateworks.js?rnd=${app_release_no}' context='/services/works'/>"></script>

<form:form name="estimatepreparationapproval-form" role="form"
	method="post" action="estimate"
	modelAttribute="estimatePreparationApproval"
	id="estimatePreparationApproval"
	class="form-horizontal form-groups-bordered"
	enctype="multipart/form-data" style="margin-top:-20px;">

	
	<div class="tab-content">
	 	<div class="tab-pane fade in active" id="estimate">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="form-group" style="padding: 50px 20px 0;">
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.works.wing" /></label>
					<div class="col-sm-3 add-margin">
								<form:select path="worksWing" id="worksWing"
									cssClass="form-control-works" cssErrorClass="form-control-works error">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:option value="1">Building & Roads</form:option>
									<form:option value="2">Public Health</form:option>
									<form:option value="3">Horticulture & Electrical</form:option>
								</form:select>
					</div>
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.executing.division" /><span
								class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
								<form:select path="department" id="department"
									class="form-control-works" required="required">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
							<form:options items="${estimatePreparationApproval.departments}"
										itemValue="code" itemLabel="name" />
								</form:select>
							</div>
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.estimate.date" /></label>
					<div class="col-sm-3 add-margin">
									<form:input id="estimateDate" path="estimateDate"
									class="form-control-works datepicker" data-date-end-date="0d"
									placeholder="DD/MM/YYYY" />
								<form:errors path="estimateDt" cssClass="add-margin error-msg" />
							</div>
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.work.location" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control-works" path="workLocation" />
							</div>
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.sector.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="sectorNumber" id="sectorNumber"
									cssClass="form-control-works" cssErrorClass="form-control-works error">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:option value="1">Sector 1 </form:option>
									<form:option value="2">Sector 2</form:option>
									<form:option value="3">Sector 3</form:option>
								</form:select>
							</div>
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.ward.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="wardNumber" id="wardNumber"
									cssClass="form-control-works" cssErrorClass="form-control-works error">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:option value="1">Ward 1 </form:option>
									<form:option value="2">Ward 2</form:option>
									<form:option value="3">Ward 3</form:option>
								</form:select>
							</div>
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.work.category" /></label>
					<div class="col-sm-3 add-margin">
								<form:select path="workCategory" id="workCategory"
									cssClass="form-control-works" cssErrorClass="form-control-works error">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:option value="1">Road Work </form:option>
									<form:option value="2">Bridge Work</form:option>
									<form:option value="3">Maintaince Work</form:option>
								</form:select>
							</div>
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.estimate.amount" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control-works" id="estimatedCost" readonly="true"
									path="estimateAmount" />
							</div>
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.estimate.prepared.by" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" path="estimatePreparedBy"
							id="estimatePreparedBy" cssClass="form-control-works"
							cssErrorClass="form-control-works error" />
							</div>
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.designation" /></label>
					<div class="col-sm-3 add-margin">
									<form:select path="preparationDesignation" id="preparationDesignation"
									class="form-control-works">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
							<form:options items="${estimatePreparationApproval.designations}"
										itemValue="code" itemLabel="name" />
								</form:select>
							</div>
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.name.work" /></label>
					<div class="col-sm-9 block-colm">
						<form:textarea class="form-control-works" path="workName"
									maxlength="2000"  />
							</div>
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.necessity" /></label>
					<div class="col-sm-9 block-colm">
							 <form:textarea class="form-control-works" path="necessity"
									maxlength="2000"  />
							</div>
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.scope.work" /></label>
					<div class="col-sm-9 block-colm">
							 <form:textarea class="form-control-works" path="workScope"
									maxlength="2000"  />
					</div>
				</div>
			</div>
		</div>
		<div class="panel panel-primary" data-collapsed="0">
			<div class="panel-heading">
			<div class="panel-title">
					<spring:message code="lbl.work.boq" text="BoQ Details" />
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
					<br>
					<c:if test="${fileuploadAllowed == 'Y' }">
					<a style="float:right;" onclick="addFileInputField();"
							href="#"><img style="height:30px;" title="Add new BoQ" src="/services/egi/resources/erp2/images/add.png" border="0" /></a>
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
								<th><spring:message code="lbl.amount" /></th>
								<th><spring:message code="lbl.action" /></th>
							</tr>
						</thead>
						<tbody>
						<c:forEach var="boq"
								items="${estimatePreparationApproval.boQDetailsList}"
								varStatus="status">
								<tr id="detailsrow" class="repeat-address">
									<td><form:input type="text" style="width:300px;"
											path="boQDetailsList[${status.index}].item_description"
											id="boQDetailsList[${status.index}].item_description"
											required="required" class="form-control-works item_description"
											maxlength="200"></form:input></td>
									<td><form:input type="text" style="width:300px;"
											path="boQDetailsList[${status.index}].ref_dsr"
											id="boQDetailsList[${status.index}].ref_dsr"
											required="required" class="form-control-works ref_dsr"
											maxlength="200"></form:input></td>
									<td><form:input type="text"
											path="boQDetailsList[${status.index}].unit"
												id="boQDetailsList[${status.index}].unit"
												required="required" class="form-control-works unit"
												maxlength="200"></form:input></td>
									<td><form:input type="number"
											path="boQDetailsList[${status.index}].rate" step=".01"
												id="boQDetailsList[${status.index}].rate"
												required="required" class="form-control-works rate"
												onchange="valueChanged()"></form:input></td>
									<td><form:input type="number"
											path="boQDetailsList[${status.index}].quantity" step=".01"
											id="boQDetailsList[${status.index}].quantity"
											required="required" class="form-control-works quantity"
											name="quantity" onchange="valueChanged()"></form:input></td>
									<td><form:input type="number"
											path="boQDetailsList[${status.index}].amount"
											id="boQDetailsList[${status.index}].amount"
											required="required" class="form-control-works amount"
											maxlength="200" name="amount" readonly="true"></form:input></td>
									<td>
											<button onclick="deleteRow(this);" class="btn btn-primary"
											style="margin-bottom: 15px; float: left;" id="plus">-</button>
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
		<jsp:include page="fileupload.jsp" />
		<br> <br>
				<jsp:include page="../common/commonWorkflowMatrix.jsp" />
				<div class="buttonbottom" align="center">
					<jsp:include page="../common/commonWorkflowMatrix-button.jsp" />
				</div>
	</div>
</form:form>
	<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>

	<script type="text/javascript">
 
 function valueChanged() {
		
		var estimateAmt = 0;
		var amt = 0;
		for (var i = 0; i <= table.rows.length; i++) {
			if (document.getElementById("boQDetailsList[" + i + "].rate") !== null
					&& document.getElementById("boQDetailsList[" + i + "].quantity") !== null
					&& document.getElementById("boQDetailsList[" + i + "].quantity") !== "") {

				var rate = document.getElementById("boQDetailsList[" + i + "].rate").value;
				var quantity = document.getElementById("boQDetailsList[" + i + "].quantity").value;
				amt = quantity * rate;
				if (document.getElementById("boQDetailsList[" + i + "].amount") != null) {
					document.getElementById("boQDetailsList[" + i + "].amount").value = amt;
				}

			}
		}
		
		calculateTotal();
	}

	function addFileInputField() {
		var addressRow = $('.repeat-address').first();
		var addressRowLength = $('.repeat-address').length;

		var newAddressRow = addressRow.clone(true).find("input").val("").end();

		$(newAddressRow).find("td input,td select").each(function(index, item) {
			item.name = item.name.replace(/[0-9]/g, addressRowLength);
		});

		newAddressRow.insertBefore(addressRow)
		calculateTotal();
	}



		function deleteRow(r) {
			var i = r.parentNode.parentNode.rowIndex;
			document.getElementById("table").deleteRow(i);
			calculateTotal();
		}
		
 function calculateTotal() {
		var estimateAmt = 0;
		var rate = 0;
		var quantity = 0;
		var amt = 0;

		for (var i = 0; i <= table.rows.length; i++) {
			// get the selected row index

			if (document.getElementById("boQDetailsList[" + i + "].rate") != null
					&& document.getElementById("boQDetailsList[" + i
							+ "].quantity") != null) {
				rate = document
						.getElementById("boQDetailsList[" + i + "].rate").value;
			}

			if (document.getElementById("boQDetailsList[" + i + "].quantity") != null) {
				quantity = document.getElementById("boQDetailsList[" + i
						+ "].quantity").value;
			}

			if (quantity != 0 && quantity != "" && rate != 0 && rate != "") {
				amt = quantity * rate;
				if (document.getElementById("boQDetailsList[" + i + "].amount") != null) {
					document.getElementById("boQDetailsList[" + i + "].amount").value = amt;

					estimateAmt = +estimateAmt + +amt;
				}
			}
		}
		document.getElementById("estimatedCost").value = estimateAmt;
	}


	
</script>