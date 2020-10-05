<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<form:form name="search-tender-form" role="form" method="post"
	action="tenderSearch" modelAttribute="tender" id="tender"
	class="form-horizontal form-groups-bordered" style="margin-top:-20px;">

	<div class="tab-content">
		<div class="tab-pane fade in active">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="form-group" style="padding: 50px 20px 0;">

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.estimate.from.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="fromDt" path="fromDt"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.work.estimate.to.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="toDt" path="toDt" class="form-control datepicker"
							data-date-end-date="0d" placeholder="DD/MM/YYYY" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.tender.loa.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control" path="loaNumber" />
					</div>

				</div>
			</div>
		</div>

		<div class="buttonbottom" align="center">
						<input type="submit" id="tenderSearch" class="btn btn-primary"
				name="tenderSearch" code="lbl.search.tender" value="Search" />
					</div>

		<br> <br> <br>
		<div class="tab-pane fade in active" id="resultheader">
			<h3>Search Result</h3>
			<div class="panel panel-primary" data-collapsed="0">
					<div style="padding: 0 15px;">
						<table class="table table-bordered" id="table">
							<thead>
								<tr>
									<th><spring:message code="lbl.tender.amount" /></th>
									<th><spring:message code="lbl.tender.date" /></th>
									<th><spring:message code="lbl.tender.vendor.details" /></th>
									<th><spring:message code="lbl.tender.loa.number" /></th>

								</tr>
							</thead>
							`
							<c:if
								test="${tender.tenderList != null &&  !tender.tenderList.isEmpty()}">
								<tbody>
									<c:forEach items="${tender.tenderList}" var="result"
										varStatus="status">
										<tr>
											<td><form:hidden
													path="tenderList[${status.index}].procurementAmount"
													id="tenderList[${status.index}].procurementAmount" />
												${result.procurementAmount }</td>
											<td><form:hidden
													path="tenderList[${status.index}].procurementDate"
													id="tenderList[${status.index}].procurementDate" />
												${result.procurementDate }</td>
											<td><form:hidden
													path="tenderList[${status.index}].contractorDetails"
													id="tenderList[${status.index}].contractorDetails" />
												${result.contractorDetails }</td>
											<td><form:hidden
													path="tenderList[${status.index}].loaNumber"
													id="tenderList[${status.index}].loaNumber" />
												${result.loaNumber }</td>
										</tr>
									</c:forEach>
								<tbody>
							</c:if>
							<c:if
								test="${tender.tenderList == null ||  tender.tenderList.isEmpty()}">
					No records found
					</c:if>
						</table>
					</div>
				</div>
			</div>
		</div>
</form:form>


