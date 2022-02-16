<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>
 <link href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/themes/base/jquery-ui.css" rel="stylesheet" />
    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/jquery-ui.min.js"></script>
    <script>
				$(document).ready(function() {
		$('input[id$=meetDate]').datepicker({
			dateFormat: 'dd/mm/yy'
		});
		$('input[id$=aadate]').datepicker({
			dateFormat: 'dd/mm/yy'
		});
	});
	function ismaxlength(obj){
    	var mlength=obj.getAttribute? parseInt(obj.getAttribute("maxlength")) : ""
    	if (obj.getAttribute && obj.value.length>mlength)
    	obj.value=obj.value.substring(0,mlength)
    }
</script>

<spring:hasBindErrors name="estimatePreparationApproval">
	<div class="alert alert-danger"
		style="margin-top: 20px; margin-bottom: 10px;">
		<form:errors path="*" />
		<br />
	</div>
</spring:hasBindErrors>



<div class="tab-pane fade in active">
<div class="panel panel-primary" data-collapsed="0">
		<div class="form-group" style="padding: 50px 20px 200px;">

<input type="hidden" name="estimatePreparationApproval"
				value="${estimatePreparationApproval.id}" /> 
				
				<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.fund.source" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:select path="fundSource" id="fundSource"
								cssClass="form-control" cssErrorClass="form-control error">
								<form:option value="">
									<spring:message code="lbl.select" />
								</form:option>
								<form:option value="1">Municipal Fund</form:option>
								<form:option value="2">Earmarked Fund</form:option>
							</form:select>
					</div>
				<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.financial.year" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
							<form:select path="financialYear" id="financialYear"
								cssClass="form-control" cssErrorClass="form-control error">
								<form:option value="">
									<spring:message code="lbl.select" />
								</form:option>
								<form:option value="1">2021-22</form:option>
								<form:option value="2">2020-21</form:option>
								<form:option value="3">2019-20</form:option>
								<form:option value="3">2018-19</form:option>
							</form:select>
						</div>
				
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.work.order.search.tendered.cost" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" class="form-control" path="tenderCost" readonly="true"
					 />
				</div>
			
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.estimate.aanumber" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" class="form-control"
					path="aanumber" readonly="true" />
				<form:errors path="aanumber"
					cssClass="add-margin error-msg" />
					</div>
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.estimate.aadate" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" id="aadate" path="aadate"
									class="form-control" 
									placeholder="DD/MM/YYYY" />
								<form:errors path="aadate" cssClass="add-margin error-msg" />
					</div>
					

						</div>
					</div>
				<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
			<div class="panel-title">
					<spring:message code="lbl.aa.head" text="Expenditure Head" />
		</div>
		</div>
		<div class="form-group" style="padding: 50px 20px 100px;">
		<label
				class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.aa.exp.head" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
							<form:select path="expHead" id="expHead"
								cssClass="form-control" cssErrorClass="form-control error">
								<form:option value="">
									<spring:message code="lbl.select" />
								</form:option>
								<form:option value="Capital">Capital</form:option>
								<form:option value="Revenue">Revenue</form:option>
								<form:option value="Deposit Estimate works">Deposit Estimate works</form:option>
								<form:option value="Ward Development Funds">Ward Development Funds</form:option>
							</form:select>
						</div>
				<label
				class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.aa.exp.cat" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
							<form:select path="expCategory" id="expCategory"
								cssClass="form-control" cssErrorClass="form-control error">
								<form:option value="">
									<spring:message code="lbl.select" />
								</form:option>
								<form:options items="${estimatePreparationApproval.headList}"
								itemValue="expenditureHead" itemLabel="expenditureHead" />
							</form:select>
							
						</div>
				<label
				class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.aa.exp.sub.cat" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
							<form:select path="expSubCategory" id="expSubCategory"
								cssClass="form-control" cssErrorClass="form-control error">
								<form:option value="">
									<spring:message code="lbl.select" />
								</form:option>
								<form:options items="${estimatePreparationApproval.subHeadList}"
								itemValue="subHead" itemLabel="subHead" /> 
								<%-- <form:option value="Augmentation of Water Supply">Augmentation of Water Supply</form:option>
<form:option value="Sewerage Treatment Plant (STP)">Sewerage Treatment Plant (STP)</form:option>
<form:option value="Storm Water Drainage (SWD)">Storm Water Drainage (SWD)</form:option>
<form:option value="Civic Works/Public Toilets">Civic Works/Public Toilets</form:option>
<form:option value="Non residential Buildings (NRB)">Non residential Buildings (NRB)</form:option>
<form:option value="Minor Irrigation">Minor Irrigation</form:option>
<form:option value="Basic Ammenities to EWS">Basic Ammenities to EWS</form:option>
<form:option value="Infrastructure Facilities in Villages">Infrastructure Facilities in Villages</form:option>
<form:option value="Improvement of Roads, Parking, Recarpeting">Improvement of Roads, Parking, Recarpeting</form:option>
<form:option value="Swacchh Bharat Mission (SBM)">Swacchh Bharat Mission (SBM)</form:option>
<form:option value="Residential Buildings (Govt. Quarters of MCC)">Residential Buildings (Govt. Quarters of MCC)</form:option>
<form:option value="Sehaj Safai Kendra">Sehaj Safai Kendra</form:option>
<form:option value="Electrification">Electrification</form:option>
<form:option value="Landscaping/Horticulture">Landscaping/Horticulture</form:option>
<form:option value="Sanitation">Sanitation</form:option>
<form:option value="Meat Hygiene">Meat Hygiene</form:option>
<form:option value="Cattle Pond">Cattle Pond</form:option>
<form:option value="Primary Health">Primary Health</form:option>
<form:option value="Purchase of Machinery and Equipments">Purchase of Machinery and Equipments</form:option>
<form:option value="Minor Works">Minor Works</form:option>
<form:option value="Other Charges (Capital)">Other Charges (Capital)</form:option>
<form:option value="Fire Academy">Fire Academy</form:option>
<form:option value="Acquisition Cost of Land">Acquisition Cost of Land</form:option>
<form:option value="Motor and Vehicles">Motor and Vehicles</form:option>
<form:option value="Capital Head Under Transport">Capital Head Under Transport</form:option>
<form:option value="Contruction of Sheds for Sanitation Asstt/Helpers">Contruction of Sheds for Sanitation Asstt/Helpers</form:option>
<form:option value="Construction of Smart School">Construction of Smart School</form:option>
<form:option value="Housing scheme for councillor/officers/officials">Housing scheme for councillor/officers/officials</form:option>
<form:option value="Cow Fee">Cow Fee</form:option>
<form:option value="Contruction of Vehicle washing and parking slots">Contruction of Vehicle washing and parking slots</form:option>
<form:option value="Infrastructure at Petrol pumps">Infrastructure at Petrol pumps</form:option>
<form:option value="Salaries">Salaries</form:option>
<form:option value="Wages">Wages</form:option>
<form:option value="Medical Treatment">Medical Treatment</form:option>
<form:option value="Office Expenses">Office Expenses</form:option>
<form:option value="Payment of Electricity Bills">Payment of Electricity Bills</form:option>
<form:option value="Minor works">Minor works</form:option>
<form:option value="Rent, Rate and Taxes">Rent, Rate and Taxes</form:option>
<form:option value="C and D waste Project">C and D waste Project</form:option>
<form:option value="Travelling Expenses">Travelling Expenses</form:option>
<form:option value="Supply and Material">Supply and Material</form:option>
<form:option value="Repair and Maintenance of Works (Fire Wing)">Repair and Maintenance of Works (Fire Wing)</form:option>
<form:option value="Repair and Maintenance of Machinery and Equipments">Repair and Maintenance of Machinery and Equipments</form:option>
<form:option value="Disaster Management">Disaster Management</form:option>
<form:option value="Pension Contribution">Pension Contribution</form:option>
<form:option value="Defined Pension and Contributory Schemes (DPCS)">Defined Pension and Contributory Schemes (DPCS)</form:option>
<form:option value="Estate Expenditure">Estate Expenditure</form:option>
<form:option value="Loan and Advances">Loan and Advances</form:option>
<form:option value="Computerization and IT">Computerization and IT</form:option>
<form:option value="Professional/Legal Fees">Professional/Legal Fees</form:option>
<form:option value="Hospitality">Hospitality</form:option>
<form:option value="Art and Culture and Sports Activities">Art and Culture and Sports Activities</form:option>
<form:option value="Capacity Building Prograames/Exposure Visits">Capacity Building Prograames/Exposure Visits</form:option>
<form:option value="Refund of Booking (Liability Account)">Refund of Booking (Liability Account)</form:option>
<form:option value="Service Tax/Luxury Tax (Liability Account)">Service Tax/Luxury Tax (Liability Account)</form:option>
<form:option value="Refund of Security (Liability Account)">Refund of Security (Liability Account)</form:option>
<form:option value="IEC Activities">IEC Activities</form:option>
<form:option value="P.O. L/HSD- Petrol, oil and Lubricant and High Speed Diesel">P.O. L/HSD- Petrol, oil and Lubricant and High Speed Diesels</form:option>
<form:option value="Other Charges (Revenue)">Other Charges (Revenue)</form:option>
<form:option value="Covid-19">Covid-19</form:option>
<form:option value="Cow Fee (Revenue)">Cow Fee (Revenue)</form:option>
<form:option value="Garbage/Processing/Transportation/Collection">Garbage/Processing/Transportation/Collection</form:option>
<form:option value="Wages/OC(Primary Health)">Wages/OC(Primary Health)</form:option>
<form:option value="Covid Cess">Covid Cess</form:option>
<form:option value="Welfare Fund">Welfare Fund</form:option>
<form:option value="Ward Development fund">Ward Development fund</form:option>
<form:option value="Mayor Development Fund">Mayor Development Fund</form:option>
<form:option value="SR.DY.DEV.FUND">SR.DY.DEV.FUND</form:option>
<form:option value="DY.MAYOR DEV FUND">DY.MAYOR DEV FUND</form:option>
<form:option value="VILLAGE DEV. WORK">VILLAGE DEV. WORK</form:option>
<form:option value="CARPETTING WORKs">CARPETTING WORK</form:option>
<form:option value="Deposit Works">Deposit Works</form:option> --%>
							</form:select>
						</div>
		</div>
		</div>	
				<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
			<div class="panel-title">
					<spring:message code="lbl.aa.meeting" text="Meeting Details" />
		</div>
		</div>
		<div class="form-group" style="padding: 50px 20px 175px;">
		<label
				class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.aa.meeting.cat" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
							<form:select path="meetCategory" id="meetCategory"
								cssClass="form-control" cssErrorClass="form-control error">
								<form:option value="">
									<spring:message code="lbl.select" />
								</form:option>
								<form:option value="Meeting Category 1">Meeting Category 1</form:option>
								<form:option value="Meeting Category 2">Meeting Category 2</form:option>
							</form:select>
						</div>
			
				<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.aa.meeting.date" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" id="meetDate" path="meetDate"
									class="form-control"  
									placeholder="DD/MM/YYYY" />
				</div>
				<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.aa.meeting.number" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" class="form-control" path="meetNumber" 
					 />
				</div>
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.aa.meeting.agenda" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:textarea class="form-control" path="meetAgenda" 
					 maxlength="1000"  onkeyup="return ismaxlength(this)" rows = "5" cols = "30"/>
				</div>
		</div>	
				</div>	
				</div>
				
