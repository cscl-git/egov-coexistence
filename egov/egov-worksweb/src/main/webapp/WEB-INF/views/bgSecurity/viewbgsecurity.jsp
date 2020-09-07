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

.btn-info {
	background: #f0794f;
	border: none;
	border-radius: 3px;
	font-size: 18px;
	padding: 10px 20px;
	color: white;
}

.btn-info:hover {
	background: #fdd3b6;
	transition: 0.5s background;
	cursor: pointer;
}
</style>


<form:form name="view-bg-security-form" role="form" method="post"
	action="bgSecurityget" modelAttribute="bgSecurityDetails"
	id="bgSecurityDetails">
	<div class="card">
		<div class="container">
			<!-- <h4>Work Order/Agreement Creation(2nd Part)</h4> -->
			<div class="row">
				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.bg.security.validity" /></label>
						<div class="col-md-6 block-colm">
							<form:input type="text" class="form-control txtRight"
								path="security_validity" readonly="true" />
						</div>
						<div class="clearfix"></div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.bg.security.amount" /></label>
						<div class="col-md-6 block-colm">
							<form:input type="number" class="form-control txtRight"
								path="security_amount" readonly="true" />
						</div>
						<div class="clearfix"></div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.bg.security.start.date" /></label>
						<div class="col-md-6 block-colm">
							<form:input id="security_start_date" path="security_start_date"
								class="form-control datepicker" data-date-end-date="0d"
								placeholder="DD/MM/YYYY" readonly="true" />
						</div>
						<div class="clearfix"></div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.bg.security.end.date" /></label>
						<div class="col-md-6 block-colm">
							<form:input id="security_end_date" path="security_end_date"
								class="form-control datepicker" data-date-end-date="0d"
								placeholder="DD/MM/YYYY" readonly="true" />
						</div>
						<div class="clearfix"></div>

					</div>
				</div>

				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.bg.security.loa.number" /></label>
						<div class="col-md-6 block-colm">
							<form:input type="text" class="form-control txtRight"
								path="loa_number" readonly="true" />
						</div>
						<div class="clearfix"></div>
					</div>
				</div>
			</div>
			<div class="vertical-center">
				<input type="submit" id="bgSecurityget" class="btn-info"
					name="bgSecurityget" code="lbl.save.bg.security"
					value="Get security details" />
			</div>
		</div>
	</div>
</form:form>
