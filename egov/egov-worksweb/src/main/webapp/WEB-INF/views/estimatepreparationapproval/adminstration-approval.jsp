<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading custom_form_panel_heading">
		<div class="panel-title">
			<spring:message code="title.estimate.administration.approval"
				text="Administration Approval" />
		</div>
	</div>

	<div class="panel panel-primary">
		<div class="container">
			<div class="row">
<input type="hidden" name="estimatePreparationApproval"
						value="${estimatePreparationApproval.id}" />
				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.estimate.preparation.financing.details" /></label>
						<div class="col-md-6 block-colm">
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
						<div class="clearfix"></div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.estimate.preparation.fund.source" /></label>
						<div class="col-md-6 block-colm">
							<form:input type="text" class="form-control txtRight"
								path="fundSource" />
						</div>
						<div class="clearfix"></div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.estimate.preparation.financial.year" /></label>
						<div class="col-md-6 block-colm">
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
						<div class="clearfix"></div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.estimate.preparation.estimate.percentage" /></label>
						<div class="col-md-6 block-colm">
							<form:input type="text" class="form-control txtRight"
								path="estimatePercentage" />
						</div>
						<div class="clearfix"></div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.work.order.search.agency.work.order" /></label>
						<div class="col-md-6 block-colm">
							<form:input type="text" class="form-control txtRight"
								path="agencyWorkOrder" />
						</div>
						<div class="clearfix"></div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.work.order.search.date" /></label>
						<div class="col-md-6 block-colm">
							<%-- <form:input type="date" class="form-control txtRight datepicker"
								path="dt" pattern="dd/MM/yyyy" required="required" /> --%>
								<form:input id="date" path="date"
									class="form-control datepicker" data-date-end-date="0d"
									placeholder="DD/MM/YYYY" />
							<form:errors path="dt" cssClass="add-margin error-msg" />
						</div>
						<div class="clearfix"></div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.work.order.search.time.limit" /></label>
						<div class="col-md-6 block-colm">
							<form:input type="text" class="form-control txtRight"
								path="timeLimit" />
						</div>
						<div class="clearfix"></div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.work.order.search.work.type" /></label>
						<div class="col-md-6 block-colm">
							<form:input type="text" class="form-control txtRight"
								path="workType" />
						</div>
						<div class="clearfix"></div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.work.order.search.tendered.cost" /></label>
						<div class="col-md-6 block-colm">
							<form:input type="text" class="form-control txtRight"
								path="tenderCost" />
						</div>
						<div class="clearfix"></div>
					</div>
				</div>


			</div>
		</div>
	</div>
</div>