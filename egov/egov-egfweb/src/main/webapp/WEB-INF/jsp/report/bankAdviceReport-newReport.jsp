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
						console.log(data.length);
						//$("#tableRow").append("<thead><tr><th>Pex Number</th><th>Realization Date</th></thead>");
						if(data.length > 0){
							for (var i = 0; i < data.length; i++) {
								var j = i+1;
								//console.log(data[i].transactionNumber);
								$("#tableRow").append(
									"<tr><td>"+j+"</td>"
									+"<td id = 'transactionNumber["+ i+ "].pex' name = 'transactionNumber["+ i+ "].pex' value = 'transactionNumber["+ i+ "].pex'>"
									+ data[i].transactionNumber+"</td>"
									+ "<td ><input type='date' class='form-control' id = 'realDate["+i+"].pex' name = 'realDate["+i+"].pex' >"
									+ "</td><tr>");
							}
						}else{
							$("#tableRow").append("<tr><td colspan='3'>No Records Found..</td></tr>");
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
	<div class="container">
		<form:form name="bankAdvice" role="form" method="post" modelAttribute="bankAdvice" id="bankAdvice"
			action="bankAdviceReportPex" enctype="multipart/form-data" class="form-horizontal form-groups-bordered">
			
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
					<div class="panel-title" style="text-align: center;">
						Bank Advice Report for Realization
					</div>
				</div>
				<div class="panel-body">
					<label class="col-sm-3 control-label text-right"> <spring:message
							code="asset-dept" text="From Date" /> <span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:input id="fromdate" path="fromDate"
							class="form-control datepicker" data-date-end-date="0d"
							required="required" placeholder="MM/DD/YYYY" />
					</div>
					<label class="col-sm-3 control-label text-right"> <spring:message
							code="asset-dept" text="To Date" /> <span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:input id="todate" path="toDate"
							class="form-control datepicker" data-date-end-date="0d"
							required="required" placeholder="MM/DD/YYYY" />
					</div>
					<label class="col-sm-3 control-label text-right"> <spring:message
							code="asset-dept" text="Bank Name" /> <span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="bankName" id="bankname" required="required"
							class="form-control" onchange="populateBranchNames()">
							<form:option value="-1">-Select-</form:option>
							<form:options items="${bankNames}" itemValue="id"
								itemLabel="name" />
						</form:select>
					</div>
					<label class="col-sm-3 control-label text-right"> <spring:message
							code="asset-dept" text="Branch Name" /> <span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="branchName" id="branchname" required="required"
							class="form-control" onchange="populateAccountNumber()">
							<form:option value="">
								<spring:message code="lbl.select" text="Select" />
							</form:option>
						</form:select>
					</div>
					<label class="col-sm-3 control-label text-right"> <spring:message
							code="asset-dept" text="Account Number" /> <span
						class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="accountNumber" id="accountnumber"
							required="required" class="form-control">
							<form:option value="">
								<spring:message code="lbl.select" text="Select" />
							</form:option>
						</form:select>
					</div>
				</div>
				<div align="center" class="buttonbottom">
					<div class="row text-center">
						<input type="button" name="bankAdviceSubmit" id="bankAdviceSubmit"
							class="btn btn-primary" value="Search" onclick="populatePexNumber()" /> 
							
						<input type="button" name="button2" id="button2" value="Close" class="btn btn-default"
							onclick="window.parent.postMessage('close','*');window.close();" />
					</div>
				</div>
			</div>
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
					<div class="panel-title">
						<spring:message code="asset-search-result" text="Search Result" />
					</div>
				</div>
				<div class="panel-body">
					<table class="table">
						<thead>
							<tr>
								<th><spring:message code="lbl-sl-no" text="Sr. No." /></th>
								<th><spring:message code="lbl-sl-no" text="PEX Number" /></th>
								<th><spring:message code="code" text="Realization Date" /></th>
							</tr>
						</thead>
						<tbody id="tableRow">
		
						</tbody>
					</table>
				</div>
				<div align="center" class="buttonbottom">
					<div class="row text-center">
						<input type="submit" name="bankAdviceSubmit" id="bankAdviceSubmit" class="btn btn-primary" value="Submit" /> 
						<input type="button" name="bankAdviceSubmit" id="bankAdviceSubmit"
							class="btn btn-primary" value="Export to Excel" onclick="populatePexNumberEXCEL()" />
					</div>
				</div>
			</div>
		</form:form>
	</div>

</body>

