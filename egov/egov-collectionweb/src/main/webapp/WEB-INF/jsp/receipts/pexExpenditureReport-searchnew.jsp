
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
<%@ taglib prefix="egov-authz" uri="/WEB-INF/taglib/egov-authz.tld" %> 
<link rel="stylesheet" type="text/css" href="<egov:url path='/yui/assets/skins/sam/autocomplete.css'/>" />
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><s:text name="Pex Expenditure Report" /></title>
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
.center {
  display: flex;
  justify-content: center;
  align-items: center;
} 
</style>
<script type="text/javascript">

function searchData(){
	if(jQuery("#fromDate").val()=="" && jQuery("#toDate").val()==""){
		bootbox.alert("Please enter from date and to date");
		return false;
	}
	
	if (document.getElementById("toDate") != null && document.getElementById("toDate").value == ""
			&& document.getElementById("fromDate") != null
			&& document.getElementById("fromDate").value != "") {
		bootbox.alert("Please Enter To Date");
		return false;
	}
	if (document.getElementById("fromDate") != null && document.getElementById("fromDate").value == ""
			&& document.getElementById("toDate") != null && document.getElementById("toDate").value != "") {
		bootbox.alert("Please Enter From Date");
		return false;
	}
	document.pexExpenditureForm.action = "pexExpenditure-listDataPex.action";
	return true;
}
function submitForm1() {
	var fromDate = document.getElementById("fromDate").value;
	var toDate = document.getElementById("toDate").value;
	
	var url = "/services/collection/remittanceBankdetail/exportXLS?fromDate="+ fromDate + "&toDate=" + toDate;
	window.location.href=url;
	//document.rtgsIssueRegisterReport.submit();
	return true;
}
</script>
</head>
<body>
	<s:form theme="simple" name="pexExpenditureForm" enctype = "multipart/form-data">
	
	 	<s:push value="model">
			<s:token />
			<div class="formmainbox">
				<div class="subheadnew">
					<s:text name="Pex Expenditure Report" />
				</div>
				<div align="center">
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
					<td class="blueboxcolspan="3">&nbsp;</td>
					<td class="greybox"><s:text name="From Date" />:</td>
					<td class="greybox"><s:date name="fromDate" var="fromDateId"
							format="dd/MM/yyyy" /> <s:textfield id="fromDate"
							name="fromDate" value="%{fromDateId}"
							data-date-end-date="0d"
							onkeyup="DateFormat(this,this.value,event,false,'3')"
							placeholder="DD/MM/YYYY" cssClass="form-control datepicker"
							data-inputmask="'mask': 'd/m/y'" /></td>
					<td class="greybox"><s:text name="To Date" />:</td>
					<td class="greybox"><s:date name="toDate" var="toDateId"
							format="dd/MM/yyyy" /> <s:textfield id="toDate"
							name="toDate" value="%{toDateId}"
							data-date-end-date="0d"
							onkeyup="DateFormat(this,this.value,event,false,'3')"
							placeholder="DD/MM/YYYY" cssClass="form-control datepicker"
							data-inputmask="'mask': 'd/m/y'" /></td>
				</tr>
					</table>
					</div>
					<!-- <div class="buttonbottom">
						<input type="button" value="Search" class="button" onclick=" getData()"/>
					</div> -->
					<div class="buttonbottom">
						<input name="search" type="submit" class="buttonsubmit" id="search" value="Search" onclick="return searchData()" />
					</div>
					<!-- <div class="center">
						<input type ="button" id="excel" method="exportXls" value="Export Excel" class="button" onclick="submitForm1()" />
					</div> -->
						
					<s:if test="%{!resultList.isEmpty()}">
						<display:table name="resultList" id="currentRow" class="table table-bordered" uid="currentRow" pagesize="${pageSize}" style="border:1px;width:100%" cellpadding="0" cellspacing="0" export="false" requestURI="">
							<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Pex No." style="width:10%;text-align: center" value="${currentRow.pex}" />
							<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Pex Date" style="width:10%;text-align: center" value="${currentRow.pexDate}" />
							<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="BPV No" style="width:20%;text-align: center" value="${currentRow.bvp}" />
							<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="BPV Date" style="width:10%;text-align: center" value="${currentRow.bvpDate}" />
							<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Voucher No" style="width:10%;text-align: center" value="${currentRow.vNo}" />
							<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Voucher Date" style="width:10%;text-align: center" value="${currentRow.vDate}" />
							<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Voucher Type" style="width:15%;text-align: center" value="${currentRow.voucherType}" />
							<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Party Name" style="width:10%;text-align: center" value="${currentRow.partyName}" />
							<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Budget Head" style="width:10%;text-align: center" value="${currentRow.budgetHead}" />
							<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Narration" style="width:10%;text-align: center" value="${currentRow.narration}" />
							<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="GlCode" style="width:10%;text-align: center" value="${currentRow.glcodeId}" />
							<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Account Number" style="width:10%;text-align: center" value="${currentRow.accNum}" />
							<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Particulars" style="width:10%;text-align: center" value="${currentRow.particulars}" />
							<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Debit Amount" style="width:10%;text-align: center" value="${currentRow.debitamt}" />
							<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Credit Amount" style="width:10%;text-align: center" value="${currentRow.creditamt}" />
							<display:column headerClass="bluebgheadtd" class="blueborderfortd" title="Paid Amount" style="width:10%;text-align: center" value="${currentRow.paidAmount}" />
							
						</display:table>
						<div class="center">
							<input type ="button" id="excel" method="exportXls" value="Export Excel" class="button" onclick="submitForm1()" />
						</div>			
				</s:if>
				<s:if test="%{isListData}">
					<s:if test="%{resultList.isEmpty()}">
						<div class="formmainbox">
							<table width="90%" border="0" align="center" cellpadding="0"
								cellspacing="0">
								<tr>
									<div>&nbsp;</div>
									<div class="billhead2">
										<b><s:text name="No Record Found" /></b>
									</div>
								</tr>
							</table>
							<br />
						</div>
						
					</s:if>
				</s:if>
				
				
			</div>
		</s:push> 
	</s:form> 
</body>
</html>