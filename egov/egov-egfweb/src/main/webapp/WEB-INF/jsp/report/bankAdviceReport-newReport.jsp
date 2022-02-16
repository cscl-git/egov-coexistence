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
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
<style>
table.its th {
	text-align: left;
}
</style>
<title><s:text name="bank.advice.report" /></title>

</head>

<link
	href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/themes/base/jquery-ui.css"
	rel="stylesheet" />
<script type="text/javascript"
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script type="text/javascript"
	src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/jquery-ui.min.js"></script>
<script>
	function populateBranchNames() {
		var bankValue = document.getElementById("bankname").value;
		jQuery("#branchname").empty();
		jQuery("#accountnumber").empty();
		jQuery("#tableRow").empty();
		jQuery
				.ajax({
					url : '/services/EGF/bankAdvice/bankAdviceReports?bankValue='
							+ bankValue,
					type : "GET",
					contentType : "application/json",
					dataType : "json",
					success : function(r) {

						data = r;
						var options = '<option value=""><strong>Select</strong></option>';

						for (var i = 0; i < data.length; i++) {

							console.log(":::name  :::: " + data[i].id
									+ "::::Value:: " + data[i].branchname);
							options += '<option value="'+data[i].id +'">'
									+ data[i].branchname + '</option>';
							$('#branchname').html(options);

						}
						document.getElementById("branchname").scrollIntoView();
					}
				})

	}

	function populateAccountNumber() {
		var branchname = document.getElementById("branchname").value;
		jQuery("#accountnumber").empty();
		jQuery
				.ajax({
					url : '/services/EGF/bankAdvice/bankAdviceReportss?branchname='
							+ branchname,
					type : "GET",
					contentType : "application/json",
					dataType : "json",
					success : function(r) {
						data = r;
						var options = '<option value=""><strong>Select</strong></option>';
						for (var i = 0; i < data.length; i++) {

							console.log(":::name  :::: " + data[i].id
									+ "::::Value:: " + data[i].accountnumber);

							options += '<option value="'+data[i].id +'">'
									+ data[i].accountnumber + '</option>';
							$('#accountnumber').html(options);
						}
						document.getElementById("accountnumber")
								.scrollIntoView();
					}
				})
	}

	function populatePexNumberOR() {
		var accountnumber = document.getElementById("accountnumber").value;
		var fromdate = document.getElementById("fromdate").value.split('/');
		var todate = document.getElementById("todate").value.split('/');
		var branchname = document.getElementById("branchname").value;
		var bankValue = document.getElementById("bankname").value;
		fromdate = fromdate[1] + "/" + fromdate[0] + "/" + fromdate[2];
		todate = todate[1] + "/" + todate[0] + "/" + todate[2];
		fromdte = new Date(fromdate)
		todte = new Date(todate)
		if (fromdate === 'undefined//undefined') {
			alert("From Date Should not be empty");
			return false;
		}

		if (todate === 'undefined//undefined') {
			alert("To Date Should not be empty");
			return false;
		}
		if (bankValue === '-1') {
			alert("Bank Should not be empty");
			return false;
		}

		if (!branchname) {
			alert("Branch Name Should not be empty");
			return false;
		}
		if (!accountnumber) {
			alert("Account Number Should not be empty");
			return false;
		}

		if (fromdte >= todte) {
			alert("From Date should be lower than To Date");
			return false;
		}

	}

	function populatePexNumber() {
		var accountnumber = document.getElementById("accountnumber").value;
		var fromdate = document.getElementById("fromdate").value.split('/');
		var todate = document.getElementById("todate").value.split('/');
		var branchname = document.getElementById("branchname").value;
		var bankValue = document.getElementById("bankname").value;
		fromdate = fromdate[2] + "-" + fromdate[1] + "-" + fromdate[0];
		todate = todate[2] + "-" + todate[1] + "-" + todate[0];
		fromdte = new Date(fromdate)
		todte = new Date(todate)
		if (fromdate === 'undefined//undefined') {
			alert("From Date Should not be empty");
			return false;
		}

		if (todate === 'undefined//undefined') {
			alert("To Date Should not be empty");
			return false;
		}
		if (bankValue === '-1') {
			alert("Bank Should not be empty");
			return false;
		}

		if (!branchname) {
			alert("Branch Name Should not be empty");
			return false;
		}
		if (!accountnumber) {
			alert("Account Number Should not be empty");
			return false;
		}

		if (fromdte >= todte) {
			alert("From Date should be lower than To Date");
			return false;
		}

		jQuery("#tableRow").empty();
		jQuery
				.ajax({
					url : '/services/EGF/bankAdvice/bankAdviceReportPexx?accountnumber='
							+ accountnumber
							+ "&fromdate="
							+ fromdate
							+ "&todate=" + todate,
					type : "GET",
					contentType : "application/json",
					dataType : "json",
					success : function(r) {
						data = r;
						$("#tableRow")
								.append(
										"<thead><tr><th>Pex Number</th><th>Realization Date</th></thead>");

						for (var i = 0; i < data.length; i++) {

							console.log(data[i].transactionNumber);

							$("#tableRow")
									.append(
											"<tr><td class='greybox' width='10%' class='form-control' id = 'transactionNumber["
													+ i
													+ "].pex' name = 'transactionNumber["
													+ i
													+ "].pex' value = transactionNumber["
													+ i
													+ "].pex>"
													+ data[i].transactionNumber
													+ "</td><td class='greybox' width='10%' ><input type='date' class='form-control' id = 'realDate["
														+i
														+"].pex' name = 'realDate["+i+"].pex' >"

													+ "</td><tr>");

						}

					}
				})
	}

	function populatePexNumberEXCEL() {
		var accountnumber = document.getElementById("accountnumber").value;
		var fromdate = document.getElementById("fromdate").value.split('/');
		var todate = document.getElementById("todate").value.split('/');
		var branchname = document.getElementById("branchname").value;
		var bankValue = document.getElementById("bankname").value;
		fromdate = fromdate[2] + "-" + fromdate[1] + "-" + fromdate[0];
		todate = todate[2] + "-" + todate[1] + "-" + todate[0];
		fromdte = new Date(fromdate)
		todte = new Date(todate)
		if (fromdate === 'undefined//undefined') {
			alert("From Date Should not be empty");
			return false;
		}

		if (todate === 'undefined//undefined') {
			alert("To Date Should not be empty");
			return false;
		}
		if (bankValue === '-1') {
			alert("Bank Should not be empty");
			return false;
		}

		if (!branchname) {
			alert("Branch Name Should not be empty");
			return false;
		}
		if (!accountnumber) {
			alert("Account Number Should not be empty");
			return false;
		}

		if (fromdte >= todte) {
			alert("From Date should be lower than To Date");
			return false;
		}

		var url = '/services/EGF/bankAdvice/bankAdviceReportPexEXCEL?accountnumber='
				+ accountnumber + "&fromdate=" + fromdate + "&todate=" + todate;

		window.location.href = url;
		// document.rtgsIssueRegisterReport.submit();
		return true;

	}
	/*  */
</script>

<body>
	<form:form name="bankAdvice" role="form" method="post"
		modelAttribute="bankAdvice" id="bankAdvice"
		action="bankAdviceReportPex" enctype="multipart/form-data"
		class="form-horizontal form-groups-bordered">
		<div class="formmainbox">
			<div class="page-container">
				<div class="main-content">

					<table align="center" width="100%" cellpadding="0" cellspacing="0">

						<center>Bank Advice Report for RTGS/PEX</center>
						<tr>
							<td class="greybox" width="10%">From Date:<span
								class="greybox"><span class="mandatory"></span></span></td>
							<td class="greybox"><form:input id="fromdate"
									path="fromDate" class="form-control datepicker"
									data-date-end-date="0d" required="required"
									placeholder="DD/MM/YYYY" /></td>
							<td class="greybox" width="10%">To Date:<span
								class="greybox"><span class="mandatory"></span></span></td>
							<td class="greybox"><form:input id="todate" path="toDate"
									class="form-control datepicker" data-date-end-date="0d"
									required="required" placeholder="DD/MM/YYYY" /></td>
						</tr>
						<br>

						<tr>
							<td class="greybox" width="10%">Bank Name:<span
								class="greybox"><span class="mandatory"></span></span></td>

							<td class="greybox"><form:select path="bankName"
									id="bankname" required="required" class="form-control"
									onchange="populateBranchNames()">
									<form:option value="-1">-Select-</form:option>
									<form:options items="${bankNames}" itemValue="id"
										itemLabel="name" />
								</form:select></td>

							<td class="greybox" width="10%">Branch Name:<span
								class="bluebox"><span class="mandatory"></span></span></td>
							<td class="greybox"><form:select path="branchName"
									id="branchname" required="required" class="form-control"
									onchange="populateAccountNumber()">

									<form:option value="">
										<spring:message code="lbl.select" text="Select" />
									</form:option>
								</form:select></td>
						</tr>
						<br>

						<tr>
							<td class="greybox" width="10%">Account Number:<span
								class="bluebox"><span class="mandatory"></span></span></td>
							<td class="greybox"><form:select path="accountNumber"
									id="accountnumber" required="required" class="form-control">
									<form:option value="">
										<spring:message code="lbl.select" text="Select" />
									</form:option>
								</form:select></td>
						</tr>

					</table>
				</div>
			</div>

		</div>

		<div align="center">
			<input type="button" name="bankAdviceSubmit" id="bankAdviceSubmit"
				class="btn btn-primary" value="Search" onclick="populatePexNumber()" />
			<input type="button" name="bankAdviceSubmit" id="bankAdviceSubmit"
				class="btn btn-primary" value="Export to Excel"
				onclick="populatePexNumberEXCEL()" />
		</div>

		<%-- <form:form name="bankAdvice" role="form" method="post"
		modelAttribute="bankAdvices" id="bankAdvices"
		action="bankAdviceReportController" enctype="multipart/form-data"
		class="form-horizontal form-groups-bordered"> --%>
		<div class="tab-pane fade in active" id="resultheader">
			<div class="panel panel-primary" data-collapsed="0">

				<table class="table table-bordered" align="center" id="tableRow">


				</table>
			</div>
			<div align="center">
				<input type="submit" name="bankAdviceSubmit" id="bankAdviceSubmit"
					class="btn btn-primary" value="Submit" />
			</div>


		</div>


	</form:form>

</body>