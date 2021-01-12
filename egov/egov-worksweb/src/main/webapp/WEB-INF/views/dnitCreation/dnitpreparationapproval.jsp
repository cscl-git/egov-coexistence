
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
        
<style>
.table thead > tr > th {
    color: black;
    background-color: #acbfd0;
    vertical-align: top;
}
</style>

<form:form name="estimatepreparationapproval-form" role="form"
	method="post" action="dnitCreation"
	modelAttribute="dnitCreation"
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
									<form:option value="Building & Roads">Building & Roads</form:option>
									<form:option value="Public Health">Public Health</form:option>
									<form:option value="Horticulture & Electrical">Horticulture & Electrical</form:option>
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
							<form:options items="${dnitCreation.departments}"
										itemValue="code" itemLabel="name" />
								</form:select>
							</div>
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.dnit.date" /></label>
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
						<form:select path="sectorNumber" id="sectorNumber" multiple="multiple"
									cssClass="form-control-works" cssErrorClass="form-control-works error">
									<form:option value="">
										<spring:message code="lbl.select" />
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
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.ward.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="wardNumber" id="wardNumber" multiple="multiple"
									cssClass="form-control-works" cssErrorClass="form-control-works error">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:option value="Ward 1">Ward 1 </form:option>
									<form:option value="Ward 2">Ward 2</form:option>
									<form:option value="Ward 3">Ward 3</form:option>
								</form:select>
							</div>
					
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.dnit.amount" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control-works" id="estimatedCost" readonly="true"
									path="estimateAmount" />
							</div>
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.dnit.prepared.by" /></label>
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
							<form:options items="${dnitCreation.designations}"
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
							id="contingentAmount"  cssClass="form-control-works" readonly="true"
							cssErrorClass="form-control-works error" />
							</div>	
										
				<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.estimate.Consultancy.fee" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" path="consultantFee" value=""
							id="consultantFee" cssClass="form-control-works" onchange="valueChanged()"
							cssErrorClass="form-control-works error" />
							</div>
							
				<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.estimate.Unforeseen.charges" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" path="unforseenCharges" value=""
							id="unforseenCharges" cssClass="form-control-works" onchange="valueChanged()"
							cssErrorClass="form-control-works error" />
							</div>
				
													
							
				<!-- New field end here -->			
							
							
					<label class="col-sm-3 control-label text-left-audit1"></label>
			<div class="col-sm-3 add-margin">
				<input type="text" class="form-control-works" style="visibility:hidden"
									 />
							</div>		
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.name.work" /></label>
					<div class="col-sm-9 block-colm">
						<form:textarea class="form-control-works" path="workName" style="height: 100px;"
									maxlength="2000"  />
							</div>
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.necessity" /></label>
					<div class="col-sm-9 block-colm">
							 <form:textarea class="form-control-works" path="necessity" style="height: 100px;"
									maxlength="2000"  />
							</div>
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.scope.work" /></label>
					<div class="col-sm-9 block-colm">
							 <form:textarea class="form-control-works" path="workScope" style="height: 100px;"
									maxlength="2000"  />
					</div>
				</div>
			</div>
		</div>
		<div id="boq_div" class="panel panel-primary" data-collapsed="0">
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
					<%-- <c:if test="${fileuploadAllowed == 'Y' }">
					<a style="float:right;" onclick="addFileInputField();"
							href="#"><img style="height:30px;" title="Add new BoQ" src="/services/egi/resources/erp2/images/add.png" border="0" /></a>
					</c:if> --%>
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
								<th><spring:message code="lbl.action" /></th>
							</tr>
						</thead>
						
				
						<tbody>
					
						
						<c:forEach var="boq" items="${mapboq.value}" varStatus="status">
						
						<%-- <c:if test="${mapboq.key == boq.milestone }"> --%>
								<tr id="boq${mapstatus.index}tableBoqrow" class="boq${status.index}repeat-address">
								<td><form:input type="text" style="width:150px;"
											path="boQDetailsList[${boq.slNo}].milestone"
											id="boQDetailsList[${boq.slNo}].milestone"
											required="required" class="form-control milestone"
											 title="${boq.milestone}"></form:input></td>
									<td><form:input type="text" style="width:200px;"
											path="boQDetailsList[${boq.slNo}].item_description"
											id="boQDetailsList[${boq.slNo}].item_description"
											required="required" class="form-control item_description"
											 title="${boq.item_description}"></form:input></td>
									<td><form:input type="text" style="width:75px;"
											path="boQDetailsList[${boq.slNo}].ref_dsr"
											id="boQDetailsList[${boq.slNo}].ref_dsr"
											required="required" class="form-control ref_dsr"
											 title="${boq.item_description}"></form:input></td>
									<td><form:input type="text" style="width:80px;"
											path="boQDetailsList[${boq.slNo}].unit"
												id="boQDetailsList[${boq.slNo}].unit"
												required="required" class="form-control unit"
												maxlength="200"></form:input></td>
									<td><form:input type="number" style="width:100px;"
											path="boQDetailsList[${boq.slNo}].rate" step=".01"
												id="boQDetailsList[${boq.slNo}].rate"
												required="required" class="form-control rate"
												onchange="valueChanged()"></form:input></td>
									<td><form:input type="number" style="width:100px;"
											path="boQDetailsList[${boq.slNo}].quantity" step=".01"
											id="boQDetailsList[${boq.slNo}].quantity"
											required="required" class="form-control quantity"
											name="quantity" onchange="valueChanged()"></form:input></td>
									<td><form:input type="number" style="width:100px;"
											path="boQDetailsList[${boq.slNo}].amount"
											id="boQDetailsList[${boq.slNo}].amount"
											required="required" class="form-control amount"
											maxlength="200" name="amount" ></form:input></td>
									<td class="text-center"><span style=" cursor:pointer; color: black;" onclick="addcheckListRow(${mapstatus.index});" tabindex="0" id="tempSubLedger[0].addButton" data-toggle="tooltip" title="" data-original-title="" aria-hidden="true"><i class="fa fa-plus"></i></span>
				 				<span style=" cursor:pointer;  color: black;" class="add-padding subledge-delete-row" onclick="deleteEsRow(this,${mapstatus.index});"><i class="fa fa-trash"  aria-hidden="true" data-toggle="tooltip" title="" data-original-title="Delete!"></i></span>
				 		
				 				 </td>
			
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
		<jsp:include page="fileupload.jsp" />
		<br> <br>
				<jsp:include page="../common/commonWorkflowMatrix.jsp" />
				<div class="buttonbottom" align="center">
					<jsp:include page="../common/commonWorkflowMatrix-button.jsp" />
				</div>
	</div>
</form:form>
	<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
<script src="<cdn:url value='/resources/js/estimatepreparationapproval/estimationhelper.js?rnd=${app_release_no}'/>"></script>
 