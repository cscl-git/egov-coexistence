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


<%@ include file="/includes/taglibs.jsp"%>
<%@ page contentType="text/html; charset=UTF-8"%>
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
<title><s:text name="PEX Expenditure Report" /></title>

</head>
<script>
	
</script>
<script type="text/javascript"
	src="/services/EGF/resources/javascript/autocomplete-debug.js"></script>
<body>
	<s:form action="rtgsIssueRegisterReport" name="rtgsIssueRegisterReport"
		theme="simple" method="post">
		<span class="mandatory1"> <s:actionerror /> <s:fielderror />
			<s:actionmessage />
		</span>
		<font style='color: red; font-weight: bold'>
			<p class="error-block" id="lblError"></p>
		</font>
		<div class="formmainbox">
			<div class="subheadnew">
				<s:text name="pex.issue.report" />
			</div>

			<table align="center" width="100%" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluebox"></td>
					<td class="greybox"><s:text name="report.pexassignedfromdate" />:</td>
					<td class="greybox"><s:date name="fromDate" var="fromDateId"
							format="dd/MM/yyyy" /> <s:textfield id="rtgsAssignedFromDate"
							name="rtgsAssignedFromDate" value="%{fromDateId}"
							data-date-end-date="0d"
							onkeyup="DateFormat(this,this.value,event,false,'3')"
							placeholder="DD/MM/YYYY" cssClass="form-control datepicker"
							data-inputmask="'mask': 'd/m/y'" /></td>
					<td class="greybox"><s:text name="report.pexassignedtodate" />:</td>
					<td class="greybox"><s:date name="toDate" var="toDateId"
							format="dd/MM/yyyy" /> <s:textfield id="rtgsAssignedToDate"
							name="rtgsAssignedToDate" value="%{toDateId}"
							data-date-end-date="0d"
							onkeyup="DateFormat(this,this.value,event,false,'3')"
							placeholder="DD/MM/YYYY" cssClass="form-control datepicker"
							data-inputmask="'mask': 'd/m/y'" /></td>
				</tr>
			</table>
			<div class="buttonbottom">
				<input type="button" value="Search" class="button"
					onclick="getData()" />
				
				<%-- <s:submit method="exportPdf" value="Save As Pdf"
					cssClass="buttonsubmit" onclick="return submitForm('exportPdf')" /> --%>
				<input type ="button" method="exportXls" value="Export Excel"
					class="button" onclick="submitForm1()" />
				<input type="button" value="Close"
					onclick="javascript:window.close()" class="button" />

			</div>
			<br>

			<div id="DetailTable">
				<table width="80%" border="0" align="center" cellpadding="0"
					cellspacing="0" class="table table-bordered">
					<thead>
						<tr>
							<th class="bluebgheadtd">Pex Date</th>
							<th class="bluebgheadtd">Pex No</th>
							<th class="bluebgheadtd">BPV No</th>
							<th class="bluebgheadtd">BPV Date</th>
							<th class="bluebgheadtd">Voucher NO</th>
							<th class="bluebgheadtd">Voucher Date</th>
							<th class="bluebgheadtd">Voucher Type</th>
							<th class="bluebgheadtd">Party Name</th>
							<th class="bluebgheadtd">Budget Head</th>
							<th class="bluebgheadtd">Narration</th>
							<th class="bluebgheadtd">GlCode</th>
							<th class="bluebgheadtd">Particulars</th>
							<th class="bluebgheadtd">Debit Amt(Rs.)</th>
							<th class="bluebgheadtd">Credit Amt(Rs.)</th>
						</tr>
					</thead>

					<tbody></tbody>
					<c:set var="trclass" value="greybox" />
					<s:hidden name="targetvalue" value="%{target}" id="targetvalue" />
				</table>
				<div id="codescontainer" />
			</div>
	</s:form>

	<script>
		function getData() {
			//alert("getdata");
			var fromDate = document.getElementById("rtgsAssignedFromDate").value;
			var toDate = document.getElementById("rtgsAssignedToDate").value;
			jQuery("#DetailTable tbody").empty();
			jQuery
					.ajax({
						url : '/services/collection/remittanceBankdetail/getdata?fromDate='
								+ fromDate + "&toDate=" + toDate,
						contentType : "application/json",
						dataType : "json",
						success : function(r) {
							data = r;

							for (var i = 0; i < data.length; i++) {
								jQuery("#DetailTable tbody").append(
										'<tr>' + '<td>' + data[i].pexDate
												+ '</td>' + '<td >'
												+ data[i].pex + '</td>'
												+ '<td >' + data[i].bvp
												+ '</td>' + '<td >'
												+ data[i].bvpDate + '</td>'
												+ '<td >' + data[i].vNo
												+ '</td>' + '<td >'
												+ data[i].vDate + '</td>'
												+ '<td >' + data[i].voucherType
												+ '</td>' + '<td >'
												+ data[i].partyName + '</td>'
												+ '<td >' + data[i].budgetHead
												+ '</td>' + '<td >'
												+ data[i].narration + '</td>'
												+ '<td >' + data[i].glcodeId
												+ '</td>' + '<td >'
												+ data[i].particulars + '</td>'
												+ '<td >' + data[i].debitamt
												+ '</td>' + '<td >'
												+ data[i].creditamt + '</td>'
												+ '</tr>');
						}
					}
					})
		}

		function submitForm1() {
			var fromDate = document.getElementById("rtgsAssignedFromDate").value;
			var toDate = document.getElementById("rtgsAssignedToDate").value;

			var url = "/services/collection/remittanceBankdetail/exportXLS?fromDate="+ fromDate + "&toDate=" + toDate;
			window.location.href=url;
			//document.rtgsIssueRegisterReport.submit();
			return true;
		}

		function submitForm(method) {
			document.rtgsIssueRegisterReport.action = '/services/EGF/report/pexExpenditureReport-'
				+ method + '.action';
		
			document.rtgsIssueRegisterReport.submit();
			return true;
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
			var url = "${pageContext.request.contextPath}/report/bankAdviceReport-exportHtml.action";
			window
					.open(url, '',
							'height=650,width=980,scrollbars=yes,left=0,top=0,status=yes');
		}
	</script>
</body>
</html>