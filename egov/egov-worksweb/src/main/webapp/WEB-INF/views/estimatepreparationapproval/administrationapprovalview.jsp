<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<style>
.container {
	padding: 10px 50px 20px;
}

.card {
	/* Add shadows to create the "card" effect */
	box-shadow: 0 5px 9px 0 rgba(0, 0, 0, 0.7);
	padding: 20px 0;
}
</style>

<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading custom_form_panel_heading">
		<div class="panel-title">
			<spring:message code="title.estimate.administration.approval"
				text="Administration Approval" />
		</div>
	</div>

	<div class="card">
		<div class="container">
			<div class="row">

				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.estimate.preparation.financing.details" /></label>
						<div class="col-md-6 block-colm">
							${estimatePreparationApproval.financingDetails}</div>
						<div class="clearfix"></div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.estimate.preparation.fund.source" /></label>
						<div class="col-md-6 block-colm">
							${estimatePreparationApproval.fundSource}</div>
						<div class="clearfix"></div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.estimate.preparation.financial.year" /></label>
						<div class="col-md-6 block-colm">
							${estimatePreparationApproval.financialYear}</div>
						<div class="clearfix"></div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.estimate.preparation.estimate.percentage" /></label>
						<div class="col-md-6 block-colm">
							${estimatePreparationApproval.estimatePercentage}</div>
						<div class="clearfix"></div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.work.order.search.agency.work.order" /></label>
						<div class="col-md-6 block-colm">
							${estimatePreparationApproval.agencyWorkOrder}</div>
						<div class="clearfix"></div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.work.order.search.date" /></label>
						<div class="col-md-6 block-colm">
							${estimatePreparationApproval.date}</div>
						<div class="clearfix"></div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.work.order.search.time.limit" /></label>
						<div class="col-md-6 block-colm">
							${estimatePreparationApproval.timeLimit}</div>
						<div class="clearfix"></div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.work.order.search.work.type" /></label>
						<div class="col-md-6 block-colm">
							${estimatePreparationApproval.workType}</div>
						<div class="clearfix"></div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.work.order.search.tendered.cost" /></label>
						<div class="col-md-6 block-colm">
							${estimatePreparationApproval.tenderCost}</div>
						<div class="clearfix"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>