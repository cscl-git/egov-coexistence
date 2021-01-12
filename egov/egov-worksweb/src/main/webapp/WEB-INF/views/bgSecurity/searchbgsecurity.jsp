<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>
<script
        src="<cdn:url value='/resources/js/estimateworks.js?rnd=${app_release_no}' context='/services/works'/>"></script>


<form:form name="search-bgsecurity-form" role="form" method="post"
	action="bgsecuritySearch" modelAttribute="bgSecurityDetails"
	id="bgSecurityDetails" class="form-horizontal form-groups-bordered"
	style="margin-top:-20px;">

	<div class="tab-content">
		<div class="tab-pane fade in active">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="form-group" style="padding: 50px 20px 0;">

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.bg.security.start.date" /><span
								class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
						<form:input id="fromDt" path="fromDt" required="true"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.bg.security.end.date" /><span
								class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
						<form:input id="fromDt" path="security_end_date" fromDt="true"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.bg.security.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="security_number" />
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
									<th><spring:message code="lbl.tender.preparation.name.work" /></th>
									<th><spring:message code="lbl.bg.tender.number" /></th>
									<th><spring:message code="lbl.tender.preparation.naration" /></th>
									<th><spring:message code="lbl.bg.security.number" /></th>
									<th><spring:message code="lbl.bg.security.validity" /></th>
									<th><spring:message code="lbl.bg.security.amount" /></th>
									<th><spring:message code="lbl.bg.security.start.date" /></th>
									<th><spring:message code="lbl.bg.security.end.date" /></th>
									<th><spring:message code="lbl.bg.security.loa.number" /></th>
									<th><spring:message code="lbl.tender.file" /></th>
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
													path="bgSecurityDetailsList[${status.index}].project_name"
													id="bgSecurityDetailsList[${status.index}].project_name" />
												${result.project_name }</td>
												<td><form:hidden
													path="bgSecurityDetailsList[${status.index}].security_tender_number"
													id="bgSecurityDetailsList[${status.index}].security_tender_number" />
												${result.security_tender_number }</td>
												<td><form:hidden
													path="bgSecurityDetailsList[${status.index}].narration"
													id="bgSecurityDetailsList[${status.index}].narration" />
												${result.narration }</td>
											<td><form:hidden
													path="bgSecurityDetailsList[${status.index}].security_number"
													id="bgSecurityDetailsList[${status.index}].security_number" />
												${result.security_number }</td>
											<td><form:hidden
													path="bgSecurityDetailsList[${status.index}].security_validity"
													id="bgSecurityDetailsList[${status.index}].security_validity" />
												${result.security_validity }</td>
											<td><form:hidden
													path="bgSecurityDetailsList[${status.index}].security_amount"
													id="bgSecurityDetailsList[${status.index}].security_amount" />
												${result.security_amount }</td>
											<td><form:hidden
													path="bgSecurityDetailsList[${status.index}].bgStartDate"
													id="bgSecurityDetailsList[${status.index}].bgStartDate" />
												${result.bgStartDate }</td>
											<td><form:hidden
													path="bgSecurityDetailsList[${status.index}].bgEndDate"
													id="bgSecurityDetailsList[${status.index}].bgEndDate" />
												${result.bgEndDate }</td>
											<td><form:hidden
													path="bgSecurityDetailsList[${status.index}].loaNumber"
													id="bgSecurityDetailsList[${status.index}].loaNumber" />
												${result.loaNumber }</td>
											<td>
												<a href="#" onclick="openBG('${result.id}')">View</a>
											</td>
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


