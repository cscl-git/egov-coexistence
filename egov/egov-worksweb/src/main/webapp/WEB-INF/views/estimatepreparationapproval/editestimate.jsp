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


	<div class="panel panel-primary" data-collapsed="0">
		<div class="form-group" style="padding: 50px 20px 670px;">

					<input type="hidden" name="estimatePreparationApproval"
				value="${estimatePreparationApproval.id}" /> <label
				class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.works.wing" /></label>
			<div class="col-sm-3 add-margin">
				<form:select path="worksWing" id="worksWing" cssClass="form-control-works"
					cssErrorClass="form-control-works error" >
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
				<form:select path="department" id="department" class="form-control-works"
					required="required">
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
					code="lbl.estimate.preparation.estimate.number" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" class="form-control-works" path="estimateNumber"
					readonly="true" />
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
									cssClass="form-control-works" cssErrorClass="form-control-works error"
									>
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
				<form:input type="number" class="form-control-works" path="estimateAmount" readonly="true"/>
							</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.estimate.prepared.by" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" path="estimatePreparedBy"
					id="estimatePreparedBy" cssClass="form-control-works"
					cssErrorClass="form-control-works error"  />
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
			<label class="col-sm-3 control-label text-left-audit1"></label>
			<div class="col-sm-3 add-margin">
				<input type="text" class="form-control-works" style="visibility:hidden"
									 />
							</div>				
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.name.work" /></label>
			<div class="col-sm-9 block-colm">
				<form:textarea class="form-control-works" path="workName" maxlength="2000" style="height: 100px;"
					 />
							</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.necessity" /></label>
			<div class="col-sm-9 block-colm">
					<form:textarea class="form-control-works" path="necessity" maxlength="2000" style="height: 100px;"
					 />
							</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.scope.work" /></label>
			<div class="col-sm-9 block-colm">
					 <form:textarea class="form-control-works" path="workScope" maxlength="2000" style="height: 100px;"
					 />
			</div>
							</div>
						</div>
					

<div id="boq_div" class="panel panel-primary" data-collapsed="0"
	style="scrollable: true;">
	<div class="panel-heading">
		<div class="panel-title">
			<spring:message code="lbl.work.boq" text="BoQ Details" />
					</div>
		<br>
		<div>
			
			<div>
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
							<th><spring:message code="lbl.action" /></th>
								</tr>
							</thead>
						
				
								<tbody>
					
						
						<c:forEach var="boq" items="${mapboq.value}" varStatus="status">
						
						<%-- <c:if test="${mapboq.key == boq.milestone }"> --%>
								<tr id="boq${mapstatus.index}tableBoqrow" class="boq${status.index}repeat-address">
								<td><form:input type="text" style="width:300px;"
											path="boQDetailsList[${boq.slNo}].milestone"
											id="boQDetailsList[${boq.slNo}].milestone"
											required="required" class="form-control milestone"
											maxlength="200"></form:input></td>
									<td><form:input type="text" style="width:300px;"
											path="boQDetailsList[${boq.slNo}].item_description"
											id="boQDetailsList[${boq.slNo}].item_description"
													required="required" class="form-control item_description"
													maxlength="200"></form:input></td>
											<td><form:input type="text" style="width:150px;"
											path="boQDetailsList[${boq.slNo}].ref_dsr"
											id="boQDetailsList[${boq.slNo}].ref_dsr"
													required="required" class="form-control ref_dsr"
													maxlength="200"></form:input></td>
											<td><form:input type="text"
											path="boQDetailsList[${boq.slNo}].unit"
												id="boQDetailsList[${boq.slNo}].unit"
												required="required" class="form-control unit"
												maxlength="200"></form:input></td>
											<td><form:input type="number"
											path="boQDetailsList[${boq.slNo}].rate" step=".01"
												id="boQDetailsList[${boq.slNo}].rate"
												required="required" class="form-control rate"
												onchange="valueChanged()"></form:input></td>
											<td><form:input type="number"
											path="boQDetailsList[${boq.slNo}].quantity" step=".01"
											id="boQDetailsList[${boq.slNo}].quantity"
													required="required" class="form-control quantity"
													name="quantity" onchange="valueChanged()"></form:input></td>
											<td><form:input type="number"
											path="boQDetailsList[${boq.slNo}].amount"
											id="boQDetailsList[${boq.slNo}].amount"
													required="required" class="form-control amount"
											maxlength="200" name="amount" ></form:input></td>
									<td class="text-center"><span style=" cursor:pointer;  color: black;" onclick="addcheckListRow(${mapstatus.index});" tabindex="0" id="tempSubLedger[0].addButton" data-toggle="tooltip" title="" data-original-title="" aria-hidden="true"><i class="fa fa-plus"></i></span>
				 				<span style=" cursor:pointer;  color: black;" class="add-padding subledge-delete-row" onclick="deleteSubledgerRow(this);"><i class="fa fa-trash"  aria-hidden="true" data-toggle="tooltip" title="" data-original-title="Delete!"></i></span>
				 		
									</td>
			
										</tr>
						<%-- 	</c:if>	 --%>
									</c:forEach>
							
					</tbody>
						</table>
				
					
					
				</c:forEach>
				</div>
				</div>
				<br>
				<br>
				<div class="panel-title"> Rough Cost Estimate </div>
				<div>
				<jsp:include page="RoughWorkfileupload.jsp" />
				</div>
				
			</div>
		</div>





	<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
<script src="<cdn:url value='/resources/js/estimatepreparationapproval/estimationhelper.js?rnd=${app_release_no}'/>"></script>
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

			newAddressRow.insertBefore(addressRow);
			//newAddressRow.insertAfter(addressRow);
			document.getElementById("boq_div").focus();
		}

		function deleteRow(r) {
			var i = r.parentNode.parentNode.rowIndex;
			document.getElementById("table").deleteRow(i);
			document.getElementById("boq_div").focus();
		}
	</script>