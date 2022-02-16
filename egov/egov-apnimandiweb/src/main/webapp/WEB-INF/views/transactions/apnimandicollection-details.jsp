<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>
<form:form name="cashBookReport" role="form" method="post"
	action="searchCashFlowReportData" modelAttribute="cashFlowReport"
	id="cashFlowReport" class="form-horizontal form-groups-bordered"
	enctype="multipart/form-data" style="margin-top:-20px;">
	<div class="tab-pane fade in active">
		<div class="panel panel-primary" data-collapsed="0">
			<div class="form-group" style="padding: 50px 20px 250px;">
				<label class="col-sm-3 control-label text-left-audit1"><spring:message
						code="lbl.estimate.preparation.estimate.aadate" /><span
					class="mandatory"></span></label>
				<div class="col-sm-3 add-margin">
					<form:input id="fromDate" path="fromDate"
						class="form-control datepicker" data-date-end-date="0d"
						placeholder="DD/MM/YYYY" />

				</div>
				<label class="col-sm-3 control-label text-left-audit1"><spring:message
						code="lbl.estimate.preparation.estimate.aadate" /><span
					class="mandatory"></span></label>
				<div class="col-sm-3 add-margin">
					<form:input id="toDate" path="toDate"
						class="form-control datepicker" data-date-end-date="0d"
						placeholder="DD/MM/YYYY" />

				</div>
			</div>
		</div>
	</div>
	<div class="buttonbottom" align="center">
		<input type="submit" id="search"
			class="btn btn-primary btn-wf-primary" name="search" value="Search" />
	</div>
	<br>
	<div class="tab-pane fade in active" id="resultheader">
		<h3>Search Result</h3>
		<div class="panel panel-primary" data-collapsed="0">

			<div style="padding: 0 15px;">
				<table class="table table-bordered" id="searchResult">
					<thead>
						<tr>
							<th width="70%"></th>
							<th width="15%" aligh="center">Current Year</th>
							<th width="15%" aligh="center">Previous Year</th>
						</tr>

					</thead>

					<tbody>
						<c:if
							test="${cashFlowReport.cashFlowResultList != null &&  !cashFlowReport.cashFlowResultList.isEmpty()}">
							<c:forEach items="${cashFlowReport.cashFlowResultList}"
								var="result" varStatus="status">
								<tr>

									<td colspan="3">A. Cash flows from operating activities
										Gross Surplus/(deficit) over expenditure</td>
								</tr>

								<tr>
									<td>A-B</td>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td>Excess of Income over Expenditure</td>
									<td>${result.incomeOverExpenditureCurr}</td>
									<td>${result.incomeOverExpenditurePrevYear }</td>
								</tr>
								<tr>
									<td>Add:</td>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td>Depreciation</td>
									<td>${result.depreciationCurr}</td>
									<td>${result.depreciationPrevYear }</td>
								</tr>
								<tr>
									<td>Interest and finance charges</td>
									<td>${result.interestFinanceChargesCurrYear }</td>
									<td>${result.interestFinanceChargesPrevYear }</td>
								</tr>
								<tr>
									<td>Less:</td>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td>Profit on disposal of assets</td>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td>Dividend Income</td>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td>Investment Income</td>
									<td>${result.investmentIncomeCurrYear }</td>
									<td>${result.investmentIncomePrevYear }</td>
								</tr>
								<tr>
									<td>Adjusted income over expenditure before effecting
										changes in current assets and current</td>
									<td>${result.adjustedIncomeCurrYear}</td>
									<td>${result.adjustedIncomePrevYear }</td>
								</tr>
								<tr>
									<td>Changes in Current assets and Current liabilities</td>
									<td></td>
									<td></td>
								</tr>
								</c:forEach>
						</c:if>
											</tbody>

				</table>
			</div>
		</div>
	</div>
</form:form>