<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>
	
	
	
<div class="tab-pane fade in active">
	<div class="panel panel-primary" data-collapsed="0">
		<div class="form-group" style="padding: 50px 20px 670px;">
	
				<input type="hidden" name="estimatePreparationApproval"
				value="${estimatePreparationApproval.id}" /> <label
				class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.works.wing" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:select path="worksWing" id="worksWing" cssClass="form-control"
					cssErrorClass="form-control error" readonly="true">
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
				<form:select path="department" id="department" class="form-control"
					readonly="true">
					<form:option value="">
						<spring:message code="lbl.select" />
					</form:option>
					<form:options items="${estimatePreparationApproval.departments}"
						itemValue="code" itemLabel="name" />
				</form:select>
			</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.estimate.date" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:input id="estimateDate" path="estimateDate"
					class="form-control datepicker" data-date-end-date="0d"
					placeholder="DD/MM/YYYY" readonly="true" />

						</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.work.location" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" class="form-control" path="workLocation"
					readonly="true" />
					</div>

			<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.estimate.number" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" class="form-control" path="estimateNumber"
					readonly="true" />
					</div>

			<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.sector.number" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="number" class="form-control" path="sectorNumber" value="${estimatePreparationApproval.sectorNumber}"
					readonly="true" />
						</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.ward.number" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="number" class="form-control" path="wardNumber" value="${estimatePreparationApproval.wardNumber}"
					readonly="true" />
						</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.work.category" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:select path="workCategory" id="workCategory"
					cssClass="form-control" cssErrorClass="form-control error"
					readonly="true">
						<form:option value="${estimatePreparationApproval.workCategory}" label="${estimatePreparationApproval.workCategory}">
						
					</form:option>
					<form:option value="1">Road Work </form:option>
					<form:option value="2">Bridge Work</form:option>
					<form:option value="3">Maintaince Work</form:option>
				</form:select>
					</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.estimate.amount" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="number" class="form-control" path="estimateAmount"
					readonly="true" />
						</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.estimate.prepared.by" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" path="estimatePreparedBy"
					id="estimatePreparedBy" cssClass="form-control"
					cssErrorClass="form-control error" readonly="true" />
						</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.designation" /></label>
			<div class="col-sm-3 add-margin">
				<form:select path="preparationDesignation" id="preparationDesignation" readonly="true"
									class="form-control">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
							<form:options items="${estimatePreparationApproval.designations}"
										itemValue="code" itemLabel="name" />
								</form:select>
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
										
					
					
					<label class="col-sm-3 control-label text-left-audit1"></label>
			<div class="col-sm-3 add-margin">
				<input type="text" class="form-control" style="visibility:hidden"
									 />
							</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.name.work" /><span
				class="mandatory"></span></label>
			<div class="col-sm-9 block-colm">
				<form:textarea class="form-control" path="workName" maxlength="2000" readonly="true" style="height: 100px;"
					 />
							</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.necessity" /><span
				class="mandatory"></span></label>
			<div class="col-sm-9 block-colm">
					<form:textarea class="form-control" path="necessity" maxlength="2000" readonly ="true" style="height: 100px;"
					 />
							</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.scope.work" /><span
				class="mandatory"></span></label>
			<div class="col-sm-9 block-colm">
					 <form:textarea class="form-control" path="workScope" maxlength="2000" readonly="true" style="height: 100px;"
					 />
			</div>
					</div>
						</div>
					</div>

<div class="panel panel-primary" data-collapsed="0"
	style="scrollable: true;">
	<div class="panel-heading">
		<div class="panel-title">
			<spring:message code="lbl.work.boq" text="BoQ Section" />
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
								
							</tr>
						</thead>
						
				
						<tbody>
					
						
						<c:forEach var="boq" items="${mapboq.value}" varStatus="status">
								<tr id="boq${mapstatus.index}tableBoqrow" class="boq${status.index}repeat-address">
									<td>${boq.milestone }</td>
									<td>${boq.item_description }</td>
									<td>${boq.ref_dsr }</td>
									<td>${boq.unit }</td>
									<td>${boq.rate }</td>
									<td>${boq.quantity }</td>
									<td>${boq.amount }</td>
								</tr>
							</c:forEach>
							
						</tbody>
				</table>
				
					
					
				</c:forEach>
					</div>
				</div>
				
				<br> <br>
			</div>
		</div>



	<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
<script src="<cdn:url value='/resources/js/estimatepreparationapproval/estimationhelper.js?rnd=${app_release_no}'/>"></script>
