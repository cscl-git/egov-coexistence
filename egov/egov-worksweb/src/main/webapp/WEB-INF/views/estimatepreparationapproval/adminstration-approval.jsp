<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

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
								<form:option value="1">2020-21</form:option>
								<form:option value="2">2019-20</form:option>
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
				<form:input id="aadate" path="aadate"
									class="form-control datepicker" data-date-end-date="0d"
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
								<form:option value="Public Health">Public Health</form:option>
								<form:option value="Roads and Bridges">Roads and Bridges</form:option>
								<form:option value="Electrical & Horticulture">Electrical & Horticulture</form:option>
								<form:option value="Deposit Estimate works">Deposit Estimate works</form:option>
								<form:option value="Ward Development Funds">Ward Development Funds</form:option>
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
								<form:option value="Aug. of city W/S system including Ph- V&VI, VII&VIII">Aug. of city W/S system including Ph- V&VI, VII&VIII</form:option>
<form:option value="Urban Development - Sewerage and Sewerage treatment plant">Urban Development - Sewerage and Sewerage treatment plant</form:option>
<form:option value="Urban Development - Strom water drainage">Urban Development - Strom water drainage</form:option>
<form:option value="Urban Development - Civic works / Public toilets">Urban Development - Civic works / Public toilets</form:option>
<form:option value="Urban Development - NR Buildings">Urban Development - NR Buildings</form:option>
<form:option value="Minor Irrigation (Fountains/ water features)">Minor Irrigation (Fountains/ water features)</form:option>
<form:option value="Providing basic amenities/ services to economically weeker section">Providing basic amenities/ services to economically weeker section</form:option>
<form:option value="Infrastructure facilities in villages under MCC jurisdiction">Infrastructure facilities in villages under MCC jurisdiction</form:option>
<form:option value="Improvement of roads, parking places and infrastructure facilities">Improvement of roads, parking places and infrastructure facilities</form:option>
<form:option value="Civic works">Civic works</form:option>
<form:option value="Non-Residential Buildings">Non-Residential Buildings</form:option>
<form:option value="Providing basic amenities/ services to economically weeker section">Providing basic amenities/ services to economically weeker section</form:option>
<form:option value="Infrastructure facilities in villages under MCC jurisdiction">Infrastructure facilities in villages under MCC jurisdiction</form:option>
<form:option value="Swatch Bharat Mission">Swatch Bharat Mission</form:option>
<form:option value="Residential Buildings">Residential Buildings</form:option>
<form:option value="Motor and Vehicles">Motor and Vehicles</form:option>
<form:option value="Construction of Sehaj Safai Kendra (SSK)">Construction of Sehaj Safai Kendra (SSK)</form:option>
<form:option value="Electrification">Electrification</form:option>
<form:option value="Landscaping and Horticulture">Landscaping and Horticulture</form:option>
<form:option value="Minor Works">Minor Works</form:option>
<form:option value="Minor Works">Minor Works</form:option>
<form:option value="C&D waste project">C&D waste project</form:option>
<form:option value="Minor Works">Minor Works</form:option>
<form:option value="MP LAD">MP LAD</form:option>
<form:option value="Any other">Any other</form:option>
<form:option value="Ward Development Funds">Ward Development Funds</form:option>
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
		<div class="form-group" style="padding: 50px 20px 100px;">
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
					code="lbl.estimate.preparation.aa.meeting.date" /></label>
			<div class="col-sm-3 add-margin">
				<form:input id="meetDate" path="meetDate"
									class="form-control datepicker" data-date-end-date="0d"
									placeholder="DD/MM/YYYY" />
				</div>
				<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.aa.meeting.number" /></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" class="form-control" path="meetNumber" 
					 />
				</div>
		</div>	
				</div>	
				</div>

