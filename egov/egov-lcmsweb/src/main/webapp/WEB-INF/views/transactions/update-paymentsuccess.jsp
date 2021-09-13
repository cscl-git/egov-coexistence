<%--
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) 2017  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~            Further, all user interfaces, including but not limited to citizen facing interfaces,
  ~            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
  ~            derived works should carry eGovernments Foundation logo on the top right corner.
  ~
  ~            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
  ~            For any further queries on attribution, including queries on brand guidelines,
  ~            please contact contact@egovernments.org
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  ~
  --%>


<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>

<div class="row">
	<div class="col-md-12">
		<form:form method="post" modelAttribute="updatePayment"
			id="updatePayment" class="form-horizontal form-groups-bordered"
			enctype="multipart/form-data">
			<%-- <input type="hidden" id="mode"  name="mode" value="${mode}" /> --%>
			<input type="hidden" name="legalcaseid" id="legalcaseid"
				value="${legalCase.id}" />
			<input type="hidden" name="legallcnumber" id="legallcnumber"
				value="${legalCase.lcNumber}" />
			<div class="panel panel-primary" data-collapsed="0">

				<div class="panel-body custom-form ">

					<c:if test="${not empty listPaymentUpadte}">
						<div class="panel-heading">
							<div class="panel-title">Updated Payment Table</div>
						</div>
						<table class="table table-striped table-bordered" id="payDetails">
							<thead>
								<tr>
									<th class="text-center">SI NO</th>
									<th class="text-center">Name Of Defending Council</th>
									<th class="text-center">Email</th>
									<th class="text-center">Phone No.</th>
									<th class="text-center">Case Fee</th>
									<th class="text-center">Misc Expense</th>
									<th class="text-center">Amount</th>
									<th class="text-center">Mode of Payment</th>
									<th class="text-center">Status</th>
									<th class="text-center">Payment of Issuance</th>
									<th class="text-center">Finding of Reply Case</th>
									<th class="text-center">Final Disposal</th>
								</tr>
							</thead>
							<tbody>

								<c:forEach var="bipartisanRespondentDetailsList"
									items="${listPaymentUpadte}" varStatus="status">

									<tr>

										<td class="text-center"><input type="hidden"
											id="check[${status.index}].value" />${status.index+1}</td>

										<td class="text-right">
											<%-- <input type="text"
										class="form-control table-input text-left"
										id="bipartisanRespondentDetailsList[${status.index}].nameOfDefendingCounsil"
										name="bipartisanRespondentDetailsList[${status.index}].nameOfDefendingCounsil"
										value="${bipartisanRespondentDetailsList.nameOfDefendingCounsil} " /> --%>${bipartisanRespondentDetailsList.nameOfDefendingCounsil}
										</td>


										<td class="text-right">
											<%-- <input type="text"
										class="form-control table-input text-left"
										id="bipartisanRespondentDetailsList[${status.index}].caseFee"
										name="bipartisanRespondentDetailsList[${status.index}].caseFee"
										value="${bipartisanRespondentDetailsList.caseFee}" /> --%>${bipartisanRespondentDetailsList.email}</td>

										<td class="text-right">
											<%-- <input type="text"
										id="bipartisanRespondentDetailsList[${status.index}].email"
										name="bipartisanRespondentDetailsList[${status.index}].email"
										class="form-control table-input text-left patternvalidation"
										value="${bipartisanRespondentDetailsList.email}" /> --%>${bipartisanRespondentDetailsList.phoneNo}</td>
										<td class="text-right">
											<%-- <input type="text"
										id="bipartisanRespondentDetailsList[${status.index}].miscExpenses"
										name="bipartisanRespondentDetailsList[${status.index}].miscExpenses"
										class="form-control table-input text-left patternvalidation"
										value="${bipartisanRespondentDetailsList.miscExpenses}" /> --%>${bipartisanRespondentDetailsList.caseFee}</td>
										<td class="text-right">
											<%-- <input type="text"
										id="bipartisanRespondentDetailsList[${status.index}].phoneNo"
										name="bipartisanRespondentDetailsList[${status.index}].phoneNo"
										class="form-control table-input text-left patternvalidation"
										value="${bipartisanRespondentDetailsList.phoneNo}" /> --%>${bipartisanRespondentDetailsList.miscExpenses}</td>
										<td class="text-right">
											<%-- <input type="text"
										id="bipartisanRespondentDetailsList[${status.index}].status"
										name="bipartisanRespondentDetailsList[${status.index}].status"
										class="form-control table-input text-left patternvalidation"
										value="${bipartisanRespondentDetailsList.status}" /> --%>${bipartisanRespondentDetailsList.amount}</td>
										<td class="text-right">
											<%-- <input type="text"
										id="bipartisanRespondentDetailsList[${status.index}].modeOfPayment"
										name="bipartisanRespondentDetailsList[${status.index}].modeOfPayment"
										class="form-control table-input text-left patternvalidation"
										value="${bipartisanRespondentDetailsList.modeOfPayment}" /> --%>${bipartisanRespondentDetailsList.modeOfPayment}</td>
										<td class="text-right">
											<%-- <input type="text"
										id="bipartisanRespondentDetailsList[${status.index}].amount"
										name="bipartisanRespondentDetailsList[${status.index}].amount"
										class="form-control table-input text-left patternvalidation"
										value="${bipartisanRespondentDetailsList.amount}" /> --%>${bipartisanRespondentDetailsList.status}</td>
										<td class="text-right">
											<%-- <input type="text"
										id="bipartisanRespondentDetailsList[${status.index}].paymentOfIssuance"
										name="bipartisanRespondentDetailsList[${status.index}].paymentOfIssuance"
										class="form-control table-input text-left patternvalidation"
										value="${bipartisanRespondentDetailsList.paymentOfIssuance}" /> --%>${bipartisanRespondentDetailsList.paymentOfIssuance}</td>
										<td class="text-right">
											<%-- <input type="text"
										id="bipartisanRespondentDetailsList[${status.index}].findingOfReplyCase"
										name="bipartisanRespondentDetailsList[${status.index}].findingOfReplyCase"
										class="form-control table-input text-left patternvalidation"
										value="${bipartisanRespondentDetailsList.findingOfReplyCase}" /> --%>${bipartisanRespondentDetailsList.findingOfReplyCase}</td>
										<td class="text-right">
											<%-- <input type="text"
										id="bipartisanRespondentDetailsList[${status.index}].finalDisposal"
										name="bipartisanRespondentDetailsList[${status.index}].finalDisposal"
										class="form-control table-input text-left patternvalidation"
										value="${bipartisanRespondentDetailsList.finalDisposal}" /> --%>${bipartisanRespondentDetailsList.finalDisposal}</td>

									</tr>
								</c:forEach>
							</tbody>
						</table>

					</c:if>

					<script
						src="<cdn:url value='/resources/global/js/bootstrap/bootstrap-datepicker.js' context='/services/egi'/>"
						type="text/javascript"></script>



				</div>
			</div>


			<div class="buttonbottom" align="center">
				<div class="form-group text-center">


					<button type="button" class="btn btn-primary" value="Print"
						onclick="window.print();" id="buttonid">Print</button>
				</div>
			</div>
		</form:form>
	</div>
</div>

<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/bootstrap/bootstrap-datepicker.css' context='/services/egi'/>" />
<script
	src="<cdn:url value='/resources/global/js/bootstrap/bootstrap-datepicker.js' context='/services/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/egov/inbox.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
<%-- <script
	src="<cdn:url value='/resources/js/app/legalcase-ajax.js?rnd=${app_release_no}'/>"></script>
<script
	src="<cdn:url value='/resources/js/app/populateDropdown.js?rnd=${app_release_no}'/>"></script> --%>
<%-- <script
	src="<cdn:url value='/resources/js/app/updatePayment.js?rnd=${app_release_no}'/>"></script> --%>
<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script> -->



