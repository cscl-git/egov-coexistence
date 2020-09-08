<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<style>
.tab {
	float: left;
}

.tab-2 {
	margin-left: 50px
}

.tab-2 input {
	display: block;
	margin-bottom: 10px
}

tr {
	transition: all .25s ease-in-out
}

tr:hover {
	background-color: #EEE;
	cursor: pointer
}

.btn-info {
	background: #f0794f;
	border: none;
	border-radius: 3px;
	font-size: 15px;
	padding: 10px 20px;
	color: white;
}

.btn-info:hover {
	background: #fdd3b6;
	transition: 0.5s background;
	cursor: pointer;
}

.container {
	padding: 10px 50px 20px;
}

.card {
	/* Add shadows to create the "card" effect */
	box-shadow: 0 5px 9px 0 rgba(0, 0, 0, 0.7);
	padding: 20px 0;
}

* {
	box-sizing: border-box;
}

.row:after {
	content: "";
	display: table;
	clear: both;
}

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

.txtRight {
	float: right
}

.block-colm {
	display: inline-block;
	float: left;
	text-align: right;
}

.vertical-center {
	text-align: center;
}
</style>


<form:form name="search-bgsecurity-form" role="form" method="post"
	action="bgsecuritySearch" modelAttribute="bgSecurityDetails"
	id="bgSecurityDetails">
	<div class="card">
		<div class="container">
			<div class="row">
				<div class="clearfix"></div>
				<div class="col-md-6">
					<div class="form-group">
						<label for="inputPassword"
							class="col-md-6 col-form-label block-colm"><spring:message
								code="lbl.bg.security.start.date" /></label>
						<div class="col-md-6 block-colm">
							<form:input id="security_start_date" path="security_start_date"
								class="form-control datepicker" data-date-end-date="0d"
								placeholder="DD/MM/YYYY" />
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
								placeholder="DD/MM/YYYY" />
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
								path="loaNumber" />
						</div>
						<div class="clearfix"></div>
					</div>
				</div>
			</div>
			<div class="vertical-center">
				<input type="submit" id="bgsecuritySearch" class="btn-info"
					name="bgsecuritySearch" code="lbl.search.bg.security"
					value="Search BqSecurity" />
			</div>

			<div style="padding: 0 15px;">
				<table class="table table-bordered" id="table">
					<thead>
						<tr>
							<th><spring:message code="lbl.bg.security.validity" /></th>
							<th><spring:message code="lbl.bg.security.amount" /></th>
							<th><spring:message code="lbl.bg.security.start.date" /></th>
							<th><spring:message code="lbl.bg.security.end.date" /></th>
							<th><spring:message code="lbl.bg.security.loa.number" /></th>
						</tr>
					</thead>
					`
					<c:if
						test="${bgSecurityDetails.bgSecurityDetailsList != null &&  !bgSecurityDetails.bgSecurityDetailsList.isEmpty()}">
						<tbody>
							<c:forEach items="${bgSecurityDetails.bgSecurityDetailsList}"
								var="result" varStatus="status">
								<tr>
									<td><form:hidden
											path="bgSecurityDetailsList[${status.index}].security_validity"
											id="bgSecurityDetailsList[${status.index}].security_validity" />
										${result.security_validity }</td>
									<td><form:hidden
											path="bgSecurityDetailsList[${status.index}].security_amount"
											id="bgSecurityDetailsList[${status.index}].security_amount" />
										${result.security_amount }</td>
									<td><form:hidden
											path="bgSecurityDetailsList[${status.index}].security_start_date"
											id="bgSecurityDetailsList[${status.index}].security_start_date" />
										${result.security_start_date }</td>
									<td><form:hidden
											path="bgSecurityDetailsList[${status.index}].security_end_date"
											id="bgSecurityDetailsList[${status.index}].security_end_date" />
										${result.security_end_date }</td>
									<td><form:hidden
											path="bgSecurityDetailsList[${status.index}].loaNumber"
											id="bgSecurityDetailsList[${status.index}].loaNumber" />
										${result.loaNumber }</td>
								</tr>
							</c:forEach>
						<tbody>
					</c:if>
					<c:if
						test="${bgSecurityDetails.bgSecurityDetailsList == null ||  bgSecurityDetails.bgSecurityDetailsList.isEmpty()}">
					No records found
					</c:if>
				</table>
			</div>
		</div>
	</div>
</form:form>


