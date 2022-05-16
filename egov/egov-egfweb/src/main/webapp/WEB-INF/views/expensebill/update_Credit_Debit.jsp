<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<form:form name="expenseBillForm" role="form" action=""
	modelAttribute="egBillregister" id="egBillregister"
	class="form-horizontal form-groups-bordered"
	enctype="multipart/form-data">
	<%-- <input type="hidden" name="auditId" id="auditId" value="${egBillregister.auditId }" /> --%>
	<%-- <form:hidden path="auditId" id="auditId"
		value="${egBillregister.auditId }" /> --%>
	<div class="panel panel-primary" data-collapsed="0">
		<div class="panel-heading custom_form_panel_heading">
			<div class="panel-title">
				<spring:message code="lbl.accountdetails" text="Account Details" />
			</div>
		</div>
		<div style="padding: 0 15px;">
			<table class="table table-bordered" id="tblaccountdetails">
				<thead>
					<tr>
						<th><spring:message code="lbl.account.code"
								text="Account Code" /></th>
						<th><spring:message code="lbl.account.head"
								text="Account Head" /></th>
						<th><spring:message code="lbl.debit.amount.new"
								text="Enter New Debit Amount" /></th>
						<th><spring:message code="lbl.credit.amount.new"
								text="Enter New Credit Amount" /></th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when
							test="${egBillregister.billDetails!=null && egBillregister.billDetails.size() > 0}">
							<c:forEach items="${egBillregister.billDetails}"
								var="billDeatils" varStatus="item">
								<tr id="accountdetailsrow">
									<td><span class="accountDetailsGlCode_${item.index }">${billDeatils.chartOfAccounts.glcode }</span>
									</td>
									<td><span class="accountDetailsAccountHead_${item.index }">${billDeatils.chartOfAccounts.name }</span>
									</td>
									<td class="text-right"><c:if
											test="${billDeatils.debitamount == '0.00'}">
											<input type="text" id="accountDebitAmount_Value"
												class="accountDetailsDebitAmount"
												value="${billDeatils.debitamount }"
												name="accountDebitAmount_Value" readonly />
										</c:if> <c:if test="${billDeatils.debitamount != '0.00'}">
											<input type="text" id="accountDebitAmount_Value"
												class="accountDetailsDebitAmount"
												value="${billDeatils.debitamount }"
												name="accountDebitAmount_Value" />
										</c:if> <input type="hidden" id="accountDebitAmount_Value"
										class="accountDetailsDebitAmount_${item.index } accountDetailsDebitAmount"
										value="${billDeatils.debitamount }" /></td>

									<td class="text-right"><c:if
											test="${billDeatils.creditamount== '0.00'}">
											<input type="text" id="accountCreditAmount_Value"
												class="accountDetailsCreditAmount"
												value="${billDeatils.creditamount }"
												name="accountCreditAmount_Value" readonly />
										</c:if> <c:if test="${billDeatils.creditamount != '0.00'}">
											<input type="text" id="accountCreditAmount_Value"
												class="accountDetailsCreditAmount"
												value="${billDeatils.creditamount }"
												name="accountCreditAmount_Value" />
										</c:if> <input type="hidden" id="accountCreditAmount_Value"
										class="accountDetailsCreditAmount_${item.index } accountDetailsCreditAmount"
										value="${billDeatils.creditamount }" /></td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>
			<div>
				

				<div class="form-group">
					<div class="text-center">
						<input type='submit' class='btn btn-primary' id="btnsearch"
							name="saveCreditDebitDetails" value="Save" /> <a
							href='javascript:void(0)' class='btn btn-default'
							onclick='self.close()'><spring:message code='lbl.close' /></a>

					</div>
				</div>
			</div>
		</div>
</form:form>

<script
	src="<cdn:url value='/resources/app/js/expensebill/viewexpensebill.js?rnd=${app_release_no}'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/egov/patternvalidation.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/egov/inbox.js?rnd=${app_release_no}' context='/services/egi'/>"></script>


