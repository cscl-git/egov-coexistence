<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>


<form:form name="search-bgsecurity-form" role="form" method="post"
	action="bgsecuritySearch" modelAttribute="bgSecurityDetails"
	id="bgSecurityDetails" class="form-horizontal form-groups-bordered"
	style="margin-top:-20px;">

	<div class="tab-content">
		<div class="tab-pane fade in active">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="form-group" style="padding: 50px 20px 0;">

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.bg.security.start.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="security_start_date" path="security_start_date"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.bg.security.end.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="security_end_date" path="security_end_date"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.bg.security.loa.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="loaNumber" />
					</div>
				</div>
			</div>
		</div>

		<div class="buttonbottom" align="center">
						<input type="submit" id="bgsecuritySearch" class="btn btn-primary"
							name="bgsecuritySearch" code="lbl.search.bg.security"
							value="Search" />
					</div>

		<br> <br> <br>
		<div class="tab-pane fade in active" id="resultheader">
			<h3>Search Result</h3>
			<div class="panel panel-primary" data-collapsed="0">
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
	</div>

</form:form>


