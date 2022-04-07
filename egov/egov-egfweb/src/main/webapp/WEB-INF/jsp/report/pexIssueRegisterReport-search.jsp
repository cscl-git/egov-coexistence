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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ include file="/includes/taglibs.jsp"%>
<%@ page language="java"%>

<html>
<head>
<meta http-equiv="Content-Type"
	content="text/html; charset=windows-1252">
<style type="text/css">
#codescontainer {
	position: absolute;
	left: 11em;
	width: 9%;
	text-align: left;
}

#codescontainer .yui-ac-content {
	position: absolute;
	width: 600px;
	border: 1px solid #404040;
	background: #fff;
	overflow: hidden;
	z-index: 9050;
}

#codescontainer .yui-ac-shadow {
	position: absolute;
	margin: .3em;
	width: 300px;
	background: #a0a0a0;
	z-index: 9049;
}

#codescontainer ul {
	padding: 5px 0;
	width: 100%;
}

#codescontainer li {
	padding: 0 5px;
	cursor: default;
	white-space: nowrap;
}

#codescontainer li.yui-ac-highlight {
	background: #ff0;
}

#codescontainer li.yui-ac-prehighlight {
	background: #FFFFCC;
}
</style>
<title><s:text name="pex.issueregister.report" /></title>

</head>
<link type="text/css" rel="stylesheet"
	href="https://cdn.datatables.net/1.11.3/css/jquery.dataTables.min.css">
<link type="text/css" rel="stylesheet"
	href="https://cdn.datatables.net/buttons/2.1.0/css/buttons.dataTables.min.css">
<script type="text/javascript"
	src="https://code.jquery.com/jquery-3.5.1.js"></script>
<script type="text/javascript"
	src="https://cdn.datatables.net/1.11.3/js/jquery.dataTables.min.js"></script>
<script type="text/javascript"
	src="https://cdn.datatables.net/buttons/2.1.0/js/dataTables.buttons.min.js"></script>
<script type="text/javascript"
	src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
<script type="text/javascript"
	src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.53/pdfmake.min.js"></script>
<script type="text/javascript"
	src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.53/vfs_fonts.js"></script>
<script type="text/javascript"
	src="https://cdn.datatables.net/buttons/2.1.0/js/buttons.html5.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/2.1.0/js/buttons.print.min.js"></script>
<!-- <script type="text/javascript" src="https://cdn.datatables.net/searchpanes/1.4.0/js/dataTables.searchPanes.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/select/1.3.4/js/dataTables.select.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/searchpanes/1.4.0/css/searchPanes.dataTables.min.css"></script> 
<script src="https://cdn.datatables.net/plug-ins/1.10.19/api/sum().js"></script>-->
<script>
$(document).ready(function() {
    $('#resultHeader').DataTable( {
        dom: 'Bfrtip',
        aaSorting : [],
        buttons: [
        	{ extend: 'excelHtml5'},
        	{
                extend: 'pdfHtml5',
                orientation: 'landscape',
                pageSize: 'LEGAL'
            },
        	{ extend: 'copyHtml5'},
        	
        	{ extend: 'csvHtml5'}
        	
        ]
    } );
   /*var sum = $('#resultHeader').DataTable().column(8).data().sum();
   console.log(sum);
   $('#totalVal').html(sum);*/
} ); 
</script>
<script>
	function populateBankBranch(bank) {
		var bankId = bank.options[bank.selectedIndex].value;
		populatebankbranch({
			bankId : bankId
		})
	}
	function populateBankAccount(branch) {
		var branchId = branch.options[branch.selectedIndex].value;
		populatebankaccount({
			branchId : branchId
		})
	}
	function populateInstrumentNumber(instrument) {
		var bankaccountId = instrument.options[instrument.selectedIndex].value;
		populateinstrumentnumber({
			bankaccountId : bankaccountId
		})
	}
	var path = "../..";
	var oAutoCompEntityForJV;
	function autocompleteRTGSNumbers(obj) {
		oACDS = new YAHOO.widget.DS_XHR(path
				+ "/services/EGF/voucher/common-ajaxLoadRTGSNumberByAccountId.action",
				[ "~^" ]);
		oACDS.responseType = YAHOO.widget.DS_XHR.TYPE_FLAT;
		oACDS.scriptQueryParam = "startsWith";

		oAutoCompEntityForJV = new YAHOO.widget.AutoComplete(obj.name,
				'codescontainer', oACDS);
		oAutoCompEntityForJV.doBeforeSendQuery = function(sQuery) {
			return sQuery + "&bankaccountId="
					+ document.getElementById('bankaccount').value
					+ "&rtgsNumber="
					+ document.getElementById('instrumentnumber').value;
		}
		oAutoCompEntityForJV.queryDelay = 0.5;
		oAutoCompEntityForJV.minQueryLength = 3;
		oAutoCompEntityForJV.prehighlightClassName = "yui-ac-prehighlight";
		oAutoCompEntityForJV.useShadow = true;
		//oAutoCompEntityForJV.forceSelection = true;
		oAutoCompEntityForJV.maxResultsDisplayed = 20;
		oAutoCompEntityForJV.useIFrame = true;
		oAutoCompEntityForJV.doBeforeExpandContainer = function(oTextbox,
				oContainer, sQDetauery, aResults) {
			clearWaitingImage();
			var pos = YAHOO.util.Dom.getXY(oTextbox);
			pos[1] += YAHOO.util.Dom.get(oTextbox).offsetHeight + 6;
			oContainer.style.width = 300;
			YAHOO.util.Dom.setXY(oContainer, pos);
			return true;
		}
	}
</script>
<script type="text/javascript" src="/services/EGF/resources/javascript/autocomplete-debug.js"></script>
<body>
<div class="container">
	<s:form action="rtgsIssueRegisterReport" name="rtgsIssueRegisterReport"
		theme="simple" method="post">
		<span class="mandatory1"> <s:actionerror /> <s:fielderror />
			<s:actionmessage />
		</span>
		<font style='color: red; font-weight: bold'>
			<p class="error-block" id="lblError"></p>
		</font>
		<div class="panel panel-primary" data-collapsed="0">
			<div class="panel-heading">
				<div class="panel-title">
					<spring:message code="pex.issue.report" text="PEX Issue Report" />
				</div>
			</div>

			<div class="panel-body">
			<table align="center" width="100%" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluebox"></td>
					<td class="bluebox"><s:text name="voucher.fund" /><span
						class="mandatory1">*</span></td>
					<td class="bluebox"><s:select name="fundId" id="fundId"
							list="dropdownData.fundList" listKey="id" listValue="name"
							headerKey="-1" headerValue="----Choose----"
							onChange="loadBank(this);" value="%{fundId.id}" /></td>
					<td class="bluebox"><s:text name="voucher.department" />
					<td class="bluebox"><s:select name="departmentid"
							id="departmentid" list="dropdownData.departmentList" listKey="code"
							listValue="name" headerKey="-1" headerValue="----Choose----"
							value="%{departmentId.id}" /></td>
				</tr>
				<tr>
					<td class="bluebox"></td>
					<%-- <td class="greybox"><s:text name="report.pexassignedfromdate" />:</td>
					<td class="greybox"><s:date name="fromDate" var="fromDateId"
							format="dd/MM/yyyy" /> <s:textfield id="rtgsAssignedFromDate"
							name="rtgsAssignedFromDate" value="%{fromDateId}"
							data-date-end-date="0d"
							onkeyup="DateFormat(this,this.value,event,false,'3')"
							placeholder="DD/MM/YYYY" cssClass="form-control datepicker"
							data-inputmask="'mask': 'd/m/y'" /></td> --%>
					<td class="greybox"><s:text name="report.pexassignedfromdate" />:</td>
					<s:date name="fromDate" format="dd/MM/yyyy" var="fromDateId" />
					<td class="greybox"><s:textfield name="rtgsAssignedFromDate" id="rtgsAssignedFromDate"
					maxlength="20"
					onkeyup="DateFormat(this,this.value,event,false,'3')"
					value="%{fromDateId}" autocomplete="off"/><a
					href="javascript:show_calendar('forms[0].rtgsAssignedFromDate');"
					style="text-decoration: none">&nbsp;<img
					src="/services/egi/resources/erp2/images/calendaricon.gif" border="0" /></a></td>
					<%-- <td class="greybox"><s:text name="report.pexassignedtodate" />:</td>
					<td class="greybox"><s:date name="toDate" var="toDateId"
							format="dd/MM/yyyy" /> <s:textfield id="rtgsAssignedToDate"
							name="rtgsAssignedToDate" value="%{toDateId}"
							data-date-end-date="0d"
							onkeyup="DateFormat(this,this.value,event,false,'3')"
							placeholder="DD/MM/YYYY" cssClass="form-control datepicker"
							data-inputmask="'mask': 'd/m/y'" /></td> --%>
						<td class="greybox"><s:text name="report.pexassignedtodate" />:</td>
						<s:date name="toDate" format="dd/MM/yyyy" var="toDateId" />
						<td class="greybox"><s:textfield name="rtgsAssignedToDate" id="rtgsAssignedToDate"
						maxlength="20"
						onkeyup="DateFormat(this,this.value,event,false,'3')"
						value="%{toDateId}" autocomplete="off"/><a
						href="javascript:show_calendar('forms[0].rtgsAssignedToDate');"
						style="text-decoration: none">&nbsp;<img
						src="/services/egi/resources/erp2/images/calendaricon.gif" border="0" /></a></td>
				</tr>
				<tr>
					<td class="bluebox"></td>
					<egov:ajaxdropdown id="bank" fields="['Text','Value']"
						dropdownId="bank"
						url="voucher/common-ajaxLoadAllBanksByFund.action" />
					<td class="bluebox"><s:text name="bank" />:</td>
					<td class="bluebox"><s:select name="bank" id="bank"
							list="dropdownData.bankList" listKey="id" listValue="name"
							headerKey="-1" headerValue="----Choose----"
							onclick="validateFund()" onChange="populateBankBranch(this);" /></td>
					<egov:ajaxdropdown id="bankbranch" fields="['Text','Value']"
						dropdownId="bankbranch"
						url="voucher/common-ajaxLoadBankBranchFromBank.action" />
					<td class="bluebox"><s:text name="bankbranch" />:</td>
					<td class="bluebox"><s:select name="bankbranch.id"
							id="bankbranch" list="dropdownData.bankBranchList" listKey="id"
							listValue="branchname" headerKey="-1"
							headerValue="----Choose----"
							onChange="populateBankAccount(this);" /></td>
				</tr>
				<tr>
					<td class="bluebox"></td>
					<egov:ajaxdropdown id="bankaccount" fields="['Text','Value']"
						dropdownId="bankaccount"
						url="voucher/common-ajaxLoadBankAccFromBranch.action" />
					<td class="greybox"><s:text name="bankaccount" />:</td>
					<td class="greybox"><s:select name="bankaccount.id"
							id="bankaccount" list="dropdownData.accNumList" listKey="id"
							listValue="accountnumber" headerKey="-1"
							headerValue="----Choose----" /></td>
					<td class="greybox"><s:text name="report.pexnumber" />:</td>
					<td class="greybox"><input type="text" name="instrumentnumber"
						id="instrumentnumber" autocomplete="off"
						 /></td>

				</tr>
			</table>
			<div class="buttonbottom">
				<s:submit method="exportHtml" value="Search" cssClass="buttonsubmit"
					onclick="return submitForm('exportHtml')" />
				<%-- <s:submit method="exportPdf" value="Save As Pdf"
					cssClass="buttonsubmit" onclick="return submitForm('exportPdf')" />
				<s:submit method="exportXls" value="Save As Xls"
					cssClass="buttonsubmit" onclick="return submitForm('exportXls')" /> --%>
				<input type="button" value="Close"
					onclick="javascript:window.close()" class="button" />

			</div>
			
			<%-- <s:if test="%{searchResult}">
				<logic:empty name="rtgsDisplayList">
					<blink>Nothing found to display.</blink>
				</logic:empty>
			</s:if>
			<div id="codescontainer" /> --%>
			
			</div>
		</div>
		
	<c:if test="${not empty rtgsDisplayList}">
	<div class="panel panel-primary" data-collapsed="0">
		<div class="panel-heading">
			<div class="panel-title">
				<spring:message code="asset-search-result" text="Search Result" />
			</div>
		</div>
		
		<div class="panel-body">
		<table class="table table-bordered" id="resultHeader">
			<thead>
				<%-- <tr> 
					<td colspan="8"></td>
					<td><spring:message code="lbl.total.amount" text="Total Amount" /></td>
					<td><span id="totalVal"/></td>
				</tr> --%>
				<tr>
					<th><spring:message code="lbl-sl-no" text="Sr. No." /></th>
					<th><spring:message code="lbl.bank" text="Bank Name" /></th>
					<th><spring:message code="lbl.account.number" text="Account Number" /></th>
					<th><spring:message code="lbl.bank.branch" text="Branch" /></th>
					<th><spring:message code="lbl.pex.date" text="PEX Date" /></th>
					<th><spring:message code="lbl.pex.number" text="PEX Number" /></th>
					<th><spring:message code="lbl.department" text="Department" /></th>
					<th><spring:message code="lbl.party.name" text="Party Name" /></th>
					<th><spring:message code="lbl.bpv.number" text="BPV Number And Date" /></th>
					<th><spring:message code="lbl.amount" text="Amount(&#8377;)" /></th>
					<th><spring:message code="lbl.status" text="Status" /></th>
				</tr>
			</thead>
			<tbody>
				<c:choose>
					<c:when
						test="${rtgsDisplayList!=null && rtgsDisplayList.size() > 0}">
						<c:forEach items="${rtgsDisplayList}" var="rtgsResultObj"
							varStatus="item">

							<tr id="rtgsView">
								<td>${item.index + 1}</td>
								<td>${rtgsResultObj.bank}</td>
								<td>${rtgsResultObj.accountNumber}</td>
								<td>${rtgsResultObj.bankBranch}</td>
								<td>${rtgsResultObj.rtgsDate}</td>
								<td>${rtgsResultObj.rtgsNumber}</td>
								<td>${rtgsResultObj.department}</td>
								<td>${rtgsResultObj.partyName}</td>
								<td>${rtgsResultObj.paymentNumber},${rtgsResultObj.paymentDate}</td>
								<td>${rtgsResultObj.paymentAmount}</td>
								<td>${rtgsResultObj.status}</td>
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<td colspan="10">No Records Found..</td>
					</c:otherwise>
				</c:choose>
			</tbody>

		</table>
		</div>
		</div>
</c:if>
	</s:form>

</div>

	<script>
		function validateFund() {
			var fund = document.getElementById('fundId').value;
			var bank = document.getElementById('bank');
			if (fund == -1) {
				bootbox.alert("Please select a Fund");
				return false;
			}
			if (fund == -1 && bank.options.length == 1) {
				bootbox.alert("Please select a Fund");
				return false;
			}
			return true;
		}
		function submitForm(method) {
			var fund = document.getElementById('fundId').value;
			var bank = document.getElementById('bank');
			if (fund == -1) {
				bootbox.alert("Please select a Fund");
				return false;
			}
			if (fund == -1 && bank.options.length == 1) {
				bootbox.alert("Please select a Fund");
				return false;
			}
			document.rtgsIssueRegisterReport.action = '/services/EGF/report/pexIssueRegisterReport-'
					+ method + '.action';
			document.rtgsIssueRegisterReport.submit();
			return true;
		}
		
		function loadBank(fund) {
			if (fund.value != -1) {
				populatebank({
					fundId : fund.options[fund.selectedIndex].value
				})
			} else {
				populatebank()
			}
		}

		function exportPDF() {
			var bank = document.getElementById("bank.id").value;
			var bankbranch = document.getElementById("bankbranch").value;
			var bankaccount = document.getElementById("bankaccount").value;
			var instrumentnumber = document.getElementById("instrumentnumber").value;
			var url = "${pageContext.request.contextPath}/report/bankAdviceReport-exportPDF.action?bank.id="
					+ bank
					+ "&bankbranch.id="
					+ bankbranch
					+ "&bankaccount.id="
					+ bankaccount
					+ "&instrumentnumber.id=" + instrumentnumber;
			window
					.open(url, '',
							'height=650,width=980,scrollbars=yes,left=0,top=0,status=yes');
		}
		function exportExcel() {
			var bank = document.getElementById("bank.id").value;
			var bankbranch = document.getElementById("bankbranch").value;
			var bankaccount = document.getElementById("bankaccount").value;
			var instrumentnumber = document.getElementById("instrumentnumber").value;
			var url = "${pageContext.request.contextPath}/report/bankAdviceReport-exportExcel.action?bank.id="
					+ bank
					+ "&bankbranch.id="
					+ bankbranch
					+ "&bankaccount.id="
					+ bankaccount
					+ "&instrumentnumber.id=" + instrumentnumber;
			window
					.open(url, '',
							'height=650,width=980,scrollbars=yes,left=0,top=0,status=yes');
		}
		function exportHtml() {
			var bank = document.getElementById("bank.id").value;
			var bankbranch = document.getElementById("bankbranch").value;
			var bankaccount = document.getElementById("bankaccount").value;
			var instrumentnumber = document.getElementById("instrumentnumber").value;
			var url = "${pageContext.request.contextPath}/report/bankAdviceReport-exportHtml.action?bank.id="
					+ bank
					+ "&bankbranch.id="
					+ bankbranch
					+ "&bankaccount.id="
					+ bankaccount
					+ "&instrumentnumber.id=" + instrumentnumber;
			window
					.open(url, '',
							'height=650,width=980,scrollbars=yes,left=0,top=0,status=yes');
		}
	</script>
</body>
</html>
