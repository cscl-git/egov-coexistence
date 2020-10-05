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
		<div class="form-group" style="padding: 50px 20px 0;">

<input type="hidden" name="estimatePreparationApproval"
				value="${estimatePreparationApproval.id}" /> <label
				class="col-sm-3 control-label text-left-audit"><spring:message
					code="lbl.estimate.preparation.financing.details" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
							<form:select path="financingDetails" id="financingDetails"
								cssClass="form-control" cssErrorClass="form-control error"
								required="required">
								<form:option value="">
									<spring:message code="lbl.select" />
								</form:option>
								<form:option value="1">Govt</form:option>
								<form:option value="2">Pvt</form:option>
							</form:select>
						</div>
			<label class="col-sm-3 control-label text-left-audit"><spring:message
					code="lbl.estimate.preparation.fund.source" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" class="form-control" path="fundSource"
					required="required" />
				<form:errors path="fundSource" cssClass="add-margin error-msg" />
					</div>
			<label class="col-sm-3 control-label text-left-audit"><spring:message
					code="lbl.estimate.preparation.financial.year" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
							<form:select path="financialYear" id="financialYear"
								cssClass="form-control" cssErrorClass="form-control error"
								required="required">
								<form:option value="">
									<spring:message code="lbl.select" />
								</form:option>
								<form:option value="1">2020</form:option>
								<form:option value="2">2019</form:option>
							</form:select>
						</div>
			<label class="col-sm-3 control-label text-left-audit"><spring:message
					code="lbl.estimate.preparation.estimate.percentage" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" class="form-control"
					path="estimatePercentage" required="required" />
				<form:errors path="estimatePercentage"
					cssClass="add-margin error-msg" />
					</div>
			<label class="col-sm-3 control-label text-left-audit"><spring:message
					code="lbl.work.order.search.agency.work.order" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" class="form-control" path="agencyWorkOrder"
					required="required" />
				<form:errors path="agencyWorkOrder" cssClass="add-margin error-msg" />
				</div>
			<label class="col-sm-3 control-label text-left-audit"><spring:message
					code="lbl.work.order.search.date" /><span class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:input id="date" path="date" class="form-control datepicker"
					data-date-end-date="0d" placeholder="DD/MM/YYYY"
					required="required" />
				<form:errors path="date" cssClass="add-margin error-msg" />
						</div>
			<label class="col-sm-3 control-label text-left-audit"><spring:message
					code="lbl.work.order.search.time.limit" /><span class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" class="form-control" path="timeLimit"
					required="required" />
				<form:errors path="timeLimit" cssClass="add-margin error-msg" />
					</div>
			<label class="col-sm-3 control-label text-left-audit"><spring:message
					code="lbl.work.order.search.work.type" /><span class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" class="form-control" path="workType"
					required="required" />
				<form:errors path="workType" cssClass="add-margin error-msg" />
				</div>
			<label class="col-sm-3 control-label text-left-audit"><spring:message
					code="lbl.work.order.search.tendered.cost" /><span
				class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:input type="text" class="form-control" path="tenderCost"
					required="required" />
				<form:errors path="tenderCost" cssClass="add-margin error-msg" />
				</div>

						</div>
					</div>
				</div>

