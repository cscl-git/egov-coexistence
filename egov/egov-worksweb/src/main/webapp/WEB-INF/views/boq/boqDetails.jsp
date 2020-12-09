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
<form:form name="workOrderAgreementForm" role="form" method="post"
	action="/services/works/boq/work" modelAttribute="workOrderAgreement"
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
							<form:textarea class="form-control" path="name_work_order" maxlength="2000" style="height: 100px;"
					 />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="work_number"  />
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
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.intended.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="work_intended_date" path="work_intended_date"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.executing.department" /><span class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
						<form:select path="department" id="department"
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
						<form:input type="number" class="form-control" id="work_amount" path="work_amount" readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.details" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="work_details" />
					</div>


					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.category" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="category" id="category"
									cssClass="form-control" cssErrorClass="form-contro error"
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
						<form:select path="sector" id="sector"
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
				<form:select path="wardNumber" id="wardNumber"
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
						<form:select path="fund" id="fund"
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
						<form:input type="text" class="form-control" path="workLocation" />
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
						code="lbl.name" /><span class="mandatory"></span></label>
				<div class="col-sm-3 add-margin">
					 <form:select path="contractor_name" id="contractor_name"
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
					<form:input type="text" class="form-control" id="contractor_code"
						path="contractor_code" readonly="true"/>
				</div>

				<label class="col-sm-3 control-label text-left-audit"><spring:message
						code="lbl.adddress" /></label>
				<div class="col-sm-3 add-margin">
					<form:input type="text" class="form-control" id= "contractor_address"
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
					<form:input type="text" class="form-control" id="contractor_email"
						path="contractor_email" readonly="true"/>
				</div>
				<br> <br> <br> <br> <br> <br>
			</div>
		</div>

		<br>
		<br>
		<br>

		<!-- ========================code end=========== -->


<div class="panel panel-primary" data-collapsed="0"
			style="scrollable: true;">
			<div class="panel-heading">
				<div class="panel-title">
					<spring:message code="lbl.work.contractor.details"
						text="Payment Distribution" />
				</div>
			</div>
<div class="panel-body">
	<table class="table table-bordered" id="tblchecklist">
		<thead>
			<tr>
				<th>Payment Details</th>
				<th>Percentage %</th>
				<th>Amount</th>
				<th><spring:message code="lbl.action" text="Action"/></th> 					
			</tr>
		</thead>
		<tbody>
			<tr id="tblchecklistRow">
				<td>
				<form:input type="text" style="width:200px;" path="paymentDistribution[0].payment_desc"	id="boQDetailsList[0].milestone"
											 class="form-control payment_desc" ></form:input>
					</td>
				<td>
				
											 
				<form:select path="paymentDistribution[0].payment_percent"  id="paymentDistribution[0].payment_percent" onchange="calcualtePerctAmount(this);" class="form-control table-input text-right payment_percent" >
					<form:option value="0">---Select---</form:option>
					<form:option value="0.125">0.125%</form:option>
					<form:option value="0.25">0.25%</form:option>
					<form:option value="1.500">1.500%</form:option>
					<form:option value="1">1%</form:option>
					<form:option value="2">2%</form:option>
					<form:option value="2.5">2.5%</form:option>
					<form:option value="3">3%</form:option>
					<form:option value="5">5%</form:option>
					<form:option value="6">6%</form:option>
					<form:option value="9">9%</form:option>
					<form:option value="10">10%</form:option>
					<form:option value="12">12%</form:option>
					<form:option value="14">14%</form:option>
					<form:option value="15">15%</form:option>
					<form:option value="18">18%</form:option>
					<form:option value="20">20%</form:option>
					<form:option value="28">28%</form:option>
					<form:option value="30">30%</form:option>
					</form:select>							 
											 
				</td>
				<td>
				<form:input type="text" style="width:80px;" path="paymentDistribution[0].amount" id="paymentDistribution[0].amount"
											 class="form-control amount" ></form:input>
				</td> 
				<td class="text-center"><span style="cursor:pointer;" onclick="addpaymentRow(this);" tabindex="0" id="temppayment[0].addButton" data-toggle="tooltip" title="" data-original-title="press ENTER to Add!" aria-hidden="true"><i class="fa fa-plus"></i></span>
				 <span class="add-padding debit-delete-row" onClick="$(this).closest('tr').remove();"><i class="fa fa-trash"  aria-hidden="true" data-toggle="tooltip" title="" data-original-title="Delete!"></i></span> </td>
			</tr>
		</tbody>
	</table>
</div>			
			
			
			
			
</div>
			

		

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
								<form:input type="text" style="width:200px;"
											path="boQDetailsList[${boq.sizeIndex}].milestone"
											id="boQDetailsList[${boq.sizeIndex}].milestone"
											required="required" readonly="true" class="form-control milestone" title="${boq.milestone}"></form:input></td>
									<td><form:input type="text" style="width:200px;"
											path="boQDetailsList[${boq.sizeIndex}].item_description"
											id="boQDetailsList[${boq.sizeIndex}].item_description"
											required="required" readonly="true" class="form-control item_description"
											 title="${boq.item_description}"></form:input></td>
									<td><form:input type="text" style="width:150px;"
											path="boQDetailsList[${boq.sizeIndex}].ref_dsr"
											id="boQDetailsList[${boq.sizeIndex}].ref_dsr"
												required="required" class="form-control ref_dsr"
											maxlength="200" readonly="true" title="${boq.ref_dsr}"></form:input></td>
										<td><form:input type="text"
											path="boQDetailsList[${boq.sizeIndex}].unit"
												id="boQDetailsList[${boq.sizeIndex}].unit"
												required="required" readonly="true" class="form-control unit"
												maxlength="200"></form:input></td>
										<td><form:input type="number"
											path="boQDetailsList[${boq.sizeIndex}].rate" step=".01"
												id="boQDetailsList[${boq.sizeIndex}].rate"
												required="required" readonly="true" class="form-control rate"
												onchange="valueChanged()"></form:input></td>
										<td><form:input type="number"
											path="boQDetailsList[${boq.sizeIndex}].quantity" step=".01"
											id="boQDetailsList[${boq.sizeIndex}].quantity"
											required="required" readonly="true" class="form-control quantity"
												name="quantity" onchange="valueChanged()"></form:input></td>
										<td><form:input type="number"
											path="boQDetailsList[${boq.sizeIndex}].amount"
											id="boQDetailsList[${boq.sizeIndex}].amount"
											required="required" readonly="true" class="form-control amount"
											maxlength="200" name="amount" ></form:input></td>
									<%-- <td class="text-center"><span style=" cursor:pointer;  color: black;" onclick="addcheckListRow(${mapstatus.index});" tabindex="0" id="tempSubLedger[0].addButton" data-toggle="tooltip" title="" data-original-title="" aria-hidden="true"><i class="fa fa-plus"></i></span>
				 				<span style=" cursor:pointer;  color: black;" class="add-padding subledge-delete-row" onClick="$(this).closest('tr').remove();"><i class="fa fa-trash"  aria-hidden="true" data-toggle="tooltip" title="" data-original-title="Delete!"></i></span>
				 		
				 				 </td> --%>
			
									</tr>
						<%-- 	</c:if>	 --%>
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
		<br> <br>
		<jsp:include page="../common/commonWorkflowMatrix.jsp" />

				<div class="buttonbottom" align="center">
					<jsp:include page="../common/commonWorkflowMatrix-button.jsp" />
		</div>

	</div>
</form:form>


<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
<script src="<cdn:url value='/resources/js/estimatepreparationapproval/estimationhelper.js?rnd=${app_release_no}'/>"></script>

