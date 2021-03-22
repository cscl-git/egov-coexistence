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
	action="/services/works/boq/edit/work1"
	modelAttribute="workOrderAgreement" id="workOrderAgreement"
	class="form-horizontal form-groups-bordered"
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
						<form:textarea class="form-control" path="name_work_order"
							maxlength="2000" style="height: 100px;" readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.dnit.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="work_number"
							readonly="true" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.work.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control"
							path="work_agreement_number" readonly="true" />
					</div>
					
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.start.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="work_start_date" path="work_start_date"
							class="form-control datepicker" data-date-end-date="0d"
							readonly="true" placeholder="DD/MM/YYYY" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.intended.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="work_intended_date" path="work_intended_date"
							class="form-control datepicker" data-date-end-date="0d"
							readonly="true" placeholder="DD/MM/YYYY" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.actualstart.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="actual_start_date" path="actual_start_date"
							readonly="true" class="form-control datepicker"
							data-date-end-date="0d" placeholder="DD/MM/YYYY" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.actualend.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="actual_end_date" path="actual_end_date"
							readonly="true" class="form-control datepicker"
							data-date-end-date="0d" placeholder="DD/MM/YYYY" />
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
						<form:input type="number" class="form-control" path="work_amount"
							readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.details" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="work_details"
							readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.sector.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="sector" id="sector" multiple="multiple"
							readonly="true" cssClass="form-control"
							cssErrorClass="form-control error">
							<form:option value="${workOrderAgreement.sector}"
								label="${workOrderAgreement.sector}">
						
									</form:option>
									<form:option value="1">1 </form:option>
							<form:option value="2">2 </form:option>
							<form:option value="3">3 </form:option>
							<form:option value="4">4 </form:option>
							<form:option value="5">5 </form:option>
							<form:option value="6">6 </form:option>
							<form:option value="7">7 </form:option>
							<form:option value="8">8 </form:option>
							<form:option value="9">9 </form:option>
							<form:option value="10">10 </form:option>
							<form:option value="11">11 </form:option>
							<form:option value="12">12 </form:option>
							<form:option value="14">14 </form:option>
							<form:option value="15">15 </form:option>
							<form:option value="16">16 </form:option>
							<form:option value="17">17 </form:option>
							<form:option value="18">18 </form:option>
							<form:option value="19">19 </form:option>
							<form:option value="20">20 </form:option>
							<form:option value="21">21 </form:option>
							<form:option value="22">22 </form:option>
							<form:option value="23">23 </form:option>
							<form:option value="24">24 </form:option>
							<form:option value="25">25 </form:option>
							<form:option value="26">26 </form:option>
							<form:option value="26 (E)">26 (E) </form:option>
							<form:option value="27">27 </form:option>
							<form:option value="28">28 </form:option>
							<form:option value="29">29 </form:option>
							<form:option value="30">30 </form:option>
							<form:option value="31">31 </form:option>
							<form:option value="32">32 </form:option>
							<form:option value="33">33 </form:option>
							<form:option value="34">34 </form:option>
							<form:option value="35">35 </form:option>
							<form:option value="36">36 </form:option>
							<form:option value="37">37 </form:option>
							<form:option value="38">38 </form:option>
							<form:option value="38 West">38 West </form:option>
							<form:option value="39">39 </form:option>
							<form:option value="40">40 </form:option>
							<form:option value="41">41 </form:option>
							<form:option value="42">42 </form:option>
							<form:option value="43">43 </form:option>
							<form:option value="44">44 </form:option>
							<form:option value="45">45 </form:option>
							<form:option value="46">46 </form:option>
							<form:option value="47">47 </form:option>
							<form:option value="48">48 </form:option>
							<form:option value="49">49 </form:option>
							<form:option value="50">50 </form:option>
							<form:option value="51 (Nizam Pur Burail)">51 (Nizam Pur Burail) </form:option>
							<form:option value="51 (Colony number 5 Brick- Kilin)">51 (Colony number 5 Brick- Kilin) </form:option>
							<form:option value="51">51 </form:option>
							<form:option value="52">52 </form:option>
							<form:option value="53 (Nehru Colony)">53 (Nehru Colony) </form:option>
							<form:option value="54">54 </form:option>
							<form:option value="55 (Housing Board)">55 (Housing Board) </form:option>
							<form:option value="55 (Indira)">55 (Indira) </form:option>
							<form:option value="61">61 </form:option>
							<form:option value="63">63 </form:option>
							<form:option value="Bapudham Trangt Camp I Phase I)">Bapudham Trangt Camp I Phase I) </form:option>
							<form:option value="Bapudham Trangt Camp II Phase II)">Bapudham Trangt Camp II Phase II) </form:option>
							<form:option value="Bapudham Trangt Camp III Phase III)">Bapudham Trangt Camp III Phase III) </form:option>
							<form:option value="Sarangpur">Sarangpur </form:option>
							<form:option value="Dadu Majara">Dadu Majara </form:option>
							<form:option value="Kaimbala">Kaimbala </form:option>
							<form:option value="Kishangarh">Kishangarh </form:option>
							<form:option value="Malaya">Malaya </form:option>
							<form:option value="Khudda Lahora">Khudda Lahora </form:option>
							<form:option value="Khudda Jassu">Khudda Jassu </form:option>
							<form:option value="Khudda Alisher">Khudda Alisher </form:option>
							<form:option value="Dhanas">Dhanas </form:option>
							<form:option value="Palsora">Palsora </form:option>
							<form:option value="Badheri">Badheri </form:option>
							<form:option value="Baterla">Baterla </form:option>
							<form:option value="Attawa">Attawa </form:option>
							<form:option value="Faidan Burail">Faidan Burail </form:option>
							<form:option value="Char Taraf Burail">Char Taraf Burail </form:option>
							<form:option value="Kajheri Hallo Majra">Kajheri Hallo Majra </form:option>
							<form:option value="Bohlana">Bohlana </form:option>
							<form:option value="Raipur Khurd">Raipur Khurd </form:option>
							<form:option value="Raipur Kalan">Raipur Kalan </form:option>
							<form:option value="Makhan Majra">Makhan Majra </form:option>
							<form:option value="Mauli Jagran">Mauli Jagran </form:option>
							<form:option value="Daria">Daria </form:option>
							<form:option value="Mani Majara">Mani Majara </form:option>
							<form:option value="Indusrial Area Phase I">Indusrial Area Phase I </form:option>
							<form:option value="Indusrial Area Phase II">Indusrial Area Phase II </form:option>
								</form:select>
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.ward.number" /></label>
			<div class="col-sm-3 add-margin">
						<form:select path="wardNumber" id="wardNumber" multiple="multiple"
							readonly="true" cssClass="form-control"
							cssErrorClass="form-control error">
							<form:option value="${workOrderAgreement.wardNumber}"
								label="${workOrderAgreement.wardNumber}">
						
									</form:option>
									<form:option value="1">1 </form:option>
									<form:option value="2">2</form:option>
									<form:option value="3">3</form:option>
									<form:option value="4">4</form:option>
									<form:option value="5">5</form:option>
									<form:option value="6">6</form:option>
									<form:option value="7">7</form:option>
									<form:option value="8">8</form:option>
									<form:option value="9">9</form:option>
									<form:option value="10">10</form:option>
									<form:option value="11">11</form:option>
									<form:option value="12">12</form:option>
									<form:option value="13">13</form:option>
									<form:option value="14">14</form:option>
									<form:option value="15">15</form:option>
									<form:option value="16">16</form:option>
									<form:option value="17">17</form:option>
									<form:option value="18">18</form:option>
									<form:option value="19">19</form:option>
									<form:option value="20">20</form:option>
									<form:option value="21">21</form:option>
									<form:option value="22">22</form:option>
									<form:option value="23">23</form:option>
									<form:option value="24">24</form:option>
									<form:option value="25">25</form:option>
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
						<form:input type="text" class="form-control" path="workLocation"
							readonly="true" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.order.search.work.authority" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control"
							path="approval_competent_authority" readonly="true" />
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
					<form:select path="contractor_name" id="contractor_name"
						readonly="true" class="form-control"
						onchange="getContractorDetails(this)">
						<form:option value="">
							<spring:message code="lbl.select" text="Select" />
						</form:option>
						<form:options items="${workOrderAgreement.contractors}"
							itemValue="id" itemLabel="name" />
					</form:select>

				</div>

				<label class="col-sm-3 control-label text-left-audit"><spring:message
						code="lbl.code" /></label>
				<div class="col-sm-3 add-margin">
					<form:input type="text" class="form-control" id="contractor_code"
						readonly="true" path="contractor_code" />
				</div>

				<label class="col-sm-3 control-label text-left-audit"><spring:message
						code="lbl.adddress" /></label>
				<div class="col-sm-3 add-margin">
					<form:input type="text" class="form-control"
						id="contractor_address" path="contractor_address" readonly="true" />
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

		<br> <br> <br>
		<c:if test="${workOrderAgreement.milestonestatus == 'Yes' }">
<div class="panel panel-primary" data-collapsed="0"
			style="scrollable: true;">
			<div class="panel-heading">
					<div class="panel-title">Milestones</div>
			</div>
				<c:if
					test="${workOrderAgreement.paymentDistribution != null &&  !workOrderAgreement.paymentDistribution.isEmpty()}">
<div class="panel-body">
	<table class="table table-bordered" id="tblchecklist">
		<thead>
			<tr>
				<th>Milestone Details</th>
				<th>Payment Percentage(%)</th>
				<th>Amount</th>
									<th>Completion Percentage</th>
				<th>Completed?</th>

				<th><spring:message code="lbl.action" text="Action"/></th> 					
			</tr>
		</thead>
		<tbody>
		
								<c:forEach items="${workOrderAgreement.paymentDistribution}"
									var="result" varStatus="status">
			<tr id="tblchecklistRow">
										<td><form:input type="text" style="width:200px;"
												path="paymentDistribution[${status.index}].payment_desc"
												id="boQDetailsList[${status.index}].milestone"
												class="form-control payment_desc"></form:input></td>
										<td><form:input type="text" style="width:200px;"
												path="paymentDistribution[${status.index}].payment_percent"
												id="paymentDistribution[${status.index}].payment_percent"
												onchange="calcualtePerctAmountedit(${status.index});"
											 class="form-control payment_percent" data-idx="0"></form:input>
											 
				</td>
										<td><form:input type="text" style="width:100px;"
												path="paymentDistribution[${status.index}].amount"
												id="paymentDistribution[${status.index}].amount"
												class="form-control amount" data-idx="0"></form:input></td>
												
										<td><form:input type="text" style="width:200px;"
												path="paymentDistribution[${status.index}].completion_percent"
												id="paymentDistribution[${status.index}].completion_percent"
												class="form-control payment_percent"
												onchange="completionPerctAmountedit(${status.index});"></form:input>

				</td> 
				
										<td><form:checkbox
												path="paymentDistribution[${status.index}].payment_completed"
												id="paymentDistribution[${status.index}].payment_completed" /></td>


				
										<td class="text-center"><span style="cursor: pointer;"
											onclick="addpaymentRow(this);" tabindex="0"
											id="temppayment[0].addButton" data-toggle="tooltip" title=""
											data-original-title="press ENTER to Add!" aria-hidden="true"><i
												class="fa fa-plus"></i></span> <span
											class="add-padding debit-delete-row"
											onClick="$(this).closest('tr').remove();"><i
												class="fa fa-trash" aria-hidden="true" data-toggle="tooltip"
												title="" data-original-title="Delete!"></i></span></td>
			
			
			</tr>
			</c:forEach>
		</tbody>
	</table>
</div>			
			
</c:if>			
			

</div>
		</c:if>
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
							href="/services/works/resources/app/formats/BOQ_Upload_Format.xlsx"><img
							style="height: 30px;" title="BoQ Upload Format"
							src="/services/egi/resources/erp2/images/download.gif" border="0" /></a>
						<br>
						<input type="file" name="file" style="color: #000000;">
					<br>
					<br>
					<div class="buttonbottom" align="center">
							<input type="submit" id="save" class="btn btn-primary"
								name="save" value="Upload" /> <br>
					</div>
					</c:if>
					<div>
					<c:if test="${fileuploadAllowed == 'Y' }">
							<c:forEach var="mapboq" items="${milestoneList}"
								varStatus="mapstatus">
								<table id="boq${mapstatus.index}tableBoq"
									class="table table-bordered tableBoq">
				
				
				
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
											<!-- <th>Measured Quantity</th>
								<th>Measured Amount</th> -->
								</tr>
							</thead>
						
				
							<tbody>
					
						
										<c:forEach var="boq" items="${mapboq.value}"
											varStatus="status">
						
						<%-- <c:if test="${mapboq.key == boq.milestone }"> --%>
											<tr id="boq${mapstatus.index}tableBoqrow"
												class="boq${status.index}repeat-address">
												<td><form:hidden
														path="boQDetailsList[${boq.sizeIndex}].slNo"
														id="boQDetailsList[${boq.sizeIndex}].slNo" /> <form:input
														type="text" style="width:100px;" readonly="true"
											path="boQDetailsList[${boq.sizeIndex}].milestone"
											id="boQDetailsList[${boq.sizeIndex}].milestone"
														required="required" class="form-control milestone"
														title="${boq.milestone}"></form:input></td>
												<td><form:input type="text" style="width:100px;"
														readonly="true"
											path="boQDetailsList[${boq.sizeIndex}].item_description"
											id="boQDetailsList[${boq.sizeIndex}].item_description"
												required="required" class="form-control item_description"
											 title="${boq.item_description}"></form:input></td>
												<td><form:input type="text" style="width:100px;"
														readonly="true"
											path="boQDetailsList[${boq.sizeIndex}].ref_dsr"
											id="boQDetailsList[${boq.sizeIndex}].ref_dsr"
												required="required" class="form-control ref_dsr"
											maxlength="200"  title="${boq.ref_dsr}"></form:input></td>
												<td><form:input type="text" style="width:80px;"
														readonly="true"
											path="boQDetailsList[${boq.sizeIndex}].unit"
												id="boQDetailsList[${boq.sizeIndex}].unit"
												required="required" class="form-control unit"
												maxlength="200"></form:input></td>
												<td><form:input type="number" style="width:80px;"
														readonly="true"
											path="boQDetailsList[${boq.sizeIndex}].rate" step=".01"
												id="boQDetailsList[${boq.sizeIndex}].rate"
												required="required" class="form-control rate"
												onchange="valueChanged()"></form:input></td>
												<td><form:input type="number" style="width:100px;"
														readonly="true"
														path="boQDetailsList[${boq.sizeIndex}].quantity"
														step=".01" id="boQDetailsList[${boq.sizeIndex}].quantity"
												required="required" class="form-control quantity"
												name="quantity" onchange="valueChanged()"></form:input></td>
												<td><form:input type="number" style="width:100px;"
														readonly="true"
											path="boQDetailsList[${boq.sizeIndex}].amount"
											id="boQDetailsList[${boq.sizeIndex}].amount"
											required="required"  class="form-control amount"
											maxlength="200" name="amount" ></form:input></td>
												<%-- <td><form:input type="number"  style="width:100px;"  step=".01"
											path="boQDetailsList[${boq.sizeIndex}].measured_quantity"
											id="boQDetailsList[${boq.sizeIndex}].measured_quantity"
											required="required"  class="form-control measured_quantity"
											maxlength="200" name="measured_quantity" onchange="calculateMeasuredAmount(${boq.sizeIndex})" ></form:input></td>
										<td><form:input type="number" style="width:100px;" readonly="true"
											path="boQDetailsList[${boq.sizeIndex}].measured_amount"
											id="boQDetailsList[${boq.sizeIndex}].measured_amount"
											required="required"  class="form-control measured_amount"
											maxlength="200" name="measured_amount" ></form:input></td>		 --%>
											
										
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
		<jsp:include page="../common/commonWorkflowhistory-view.jsp" /> 
		<br> <br>
				
		<jsp:include page="../common/commonWorkflowMatrix.jsp" />
				<div class="buttonbottom" align="center">
				<jsp:include page="../common/commonWorkflowMatrix-button.jsp" />
		</div>

	</div>
</form:form>


<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>

<script type="text/javascript">

	
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

<script
	src="<cdn:url value='/resources/js/estimatepreparationapproval/estimationhelper.js?rnd=${app_release_no}'/>"></script>