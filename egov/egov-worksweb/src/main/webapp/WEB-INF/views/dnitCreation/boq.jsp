<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>
<style>
.table thead > tr > th {
    color: black;
    background-color: #acbfd0;
    vertical-align: top;
}
.table tbody > tr > td {
    color: black;
    background-color: #acbfd0;
    vertical-align: top;
}
</style>


<div class="tab-pane fade in active">
	<div class="panel panel-primary" data-collapsed="0">
		<div class="form-group" style="padding: 50px 20px 765px;">

			<input type="hidden" name="estimatePreparationApproval"
				value="${estimatePreparationApproval.id}" /> <label
				class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.works.wing" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:select path="worksWing" id="worksWing" cssClass="form-control-works"
					cssErrorClass="form-control-works error" readonly="true">
					<form:option value="">
						<spring:message code="lbl.select" />
					</form:option>
					<form:option value="Building & Roads">Building & Roads</form:option>
					<form:option value="Public Health">Public Health</form:option>
					<form:option value="Horticulture & Electrical">Horticulture & Electrical</form:option>
				</form:select>
			</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.executing.division" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:select path="department" id="department" class="form-control-works"
					readonly="true">
					<form:option value="">
						<spring:message code="lbl.select" />
					</form:option>
					<form:options items="${estimatePreparationApproval.departments}"
						itemValue="code" itemLabel="name" />
				</form:select>
			</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.dnit.date" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:input id="estimateDate" path="estimateDate"
					class="form-control-works datepicker" data-date-end-date="0d"
					placeholder="DD/MM/YYYY" readonly="true" />

				</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.work.location" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" class="form-control-works" path="workLocation"
					readonly="true" />
			</div>

			<label class="col-sm-3 control-label text-left-audit1"><spring:message
							code="lbl.estimate.preparation.dnit.number" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" class="form-control-works" path="estimateNumber"
					readonly="true" />
			</div>

			<label class="col-sm-3 control-label text-left-audit1"><spring:message
							code="lbl.estimate.preparation.sector.number" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="number" class="form-control-works" path="sectorNumber"
					readonly="true" />
				</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
							code="lbl.estimate.preparation.ward.number" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="number" class="form-control-works" path="wardNumber"
					readonly="true" />
				</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.work.category" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:select path="workCategory" id="workCategory"
					cssClass="form-control-works" cssErrorClass="form-control-works error"
					readonly="true">
					<form:option value="">
						<spring:message code="lbl.select" />
					</form:option>
					<form:option value="Road Work">Road Work </form:option>
					<form:option value="Bridge Work">Bridge Work</form:option>
					<form:option value="Maintaince Work">Maintaince Work</form:option>
				</form:select>
			</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
							code="lbl.estimate.preparation.dnit.amount" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="number" class="form-control-works" path="estimateAmount"
					readonly="true" />
				</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
							code="lbl.estimate.preparation.dnit.prepared.by" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" path="estimatePreparedBy"
					id="estimatePreparedBy" cssClass="form-control-works"
					cssErrorClass="form-control-works error" readonly="true" />
				</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.designation" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" class="form-control-works"
					path="preparationDesignation" readonly="true" />
			</div>
			
			
			<!-- New field added here -->		
				
				
				
					
				<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.estimate.Contingency.Percentage" /></label>

					<div class="col-sm-3 add-margin">
						<form:input type="text" path="contingentPercentage"
							id="contingentPercentage" cssClass="form-control-works"
							cssErrorClass="form-control-works error" readonly="true"/>
					</div>
<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.estimate.Contingency.amount" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" path="contingentAmount"
							id="contingentAmount" value="" cssClass="form-control-works" readonly="true"
							cssErrorClass="form-control-works error" />
							</div>	
										
				<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.estimate.Consultancy.fee" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" path="consultantFee" value=""
							id="consultantFee" cssClass="form-control-works" onchange="valueChanged()"  readonly="true"
							cssErrorClass="form-control-works error" />
							</div>
							
				<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.estimate.Unforeseen.charges" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" path="unforseenCharges" value=""
							id="unforseenCharges" cssClass="form-control-works" onchange="valueChanged()"  readonly="true"
							cssErrorClass="form-control-works error" />
							</div>
				
													
							
				<!-- New field end here -->		
			
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.name.work" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 block-colm">
				<form:textarea class="form-control-works" path="workName" maxlength="2000" style="height: 100px;"
					readonly="true" />
				</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.necessity" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 block-colm">
				<form:input type="text" class="form-control-works" path="necessity" style="height: 100px;"
					readonly="true" />
			</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.scope.work" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 block-colm">
				<form:input type="text" class="form-control-works" path="workScope" style="height: 100px;"
					readonly="true" />
				</div>
			</div>
				</div>
			</div>

<div class="panel panel-primary" data-collapsed="0"
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
								<td>
								<form:hidden path="boQDetailsList[${boq.sizeIndex}].slNo"
												id="boQDetailsList[${boq.sizeIndex}].slNo" />
								<form:input type="text" style="width:200px;"
											path="boQDetailsList[${boq.sizeIndex}].milestone"
											id="boQDetailsList[${boq.sizeIndex}].milestone"
											required="required" class="form-control milestone"
											maxlength="200"  title="${boq.milestone}"></form:input></td>
									<td><form:input type="text" style="width:200px;"
											path="boQDetailsList[${boq.sizeIndex}].item_description"
											id="boQDetailsList[${boq.sizeIndex}].item_description"
											required="required" class="form-control item_description"
											maxlength="200"  title="${boq.item_description}"></form:input></td>
									<td><form:input type="text" style="width:150px;"
											path="boQDetailsList[${boq.sizeIndex}].ref_dsr"
											id="boQDetailsList[${boq.sizeIndex}].ref_dsr"
											required="required" class="form-control ref_dsr"
											maxlength="200"  title="${boq.ref_dsr}"></form:input></td>
									<td><form:input type="text"
											path="boQDetailsList[${boq.sizeIndex}].unit"
												id="boQDetailsList[${boq.sizeIndex}].unit"
												required="required" class="form-control unit"
												maxlength="200"></form:input></td>
									<td><form:input type="number"
											path="boQDetailsList[${boq.sizeIndex}].rate" step=".01"
												id="boQDetailsList[${boq.sizeIndex}].rate"
												required="required" class="form-control rate"
												onchange="valueChanged()"></form:input></td>
									<td><form:input type="number"
											path="boQDetailsList[${boq.sizeIndex}].quantity" step=".01"
											id="boQDetailsList[${boq.sizeIndex}].quantity"
											required="required" class="form-control quantity"
											name="quantity" onchange="valueChanged()"></form:input></td>
									<td><form:input type="number"
											path="boQDetailsList[${boq.sizeIndex}].amount"
											id="boQDetailsList[${boq.sizeIndex}].amount"
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
		
		
	</div>
</div>



<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
<script src="<cdn:url value='/resources/js/estimatepreparationapproval/estimationhelper.js?rnd=${app_release_no}'/>"></script>



