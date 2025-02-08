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
<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld"%>
<%@ taglib prefix="egov" tagdir="/WEB-INF/tags"%>
<%@ page language="java"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ include file="/includes/taglibs.jsp"%>
<link
	href="<egov:url path='/resources/css/displaytagFormatted.css?rnd=${app_release_no}'/>"
	rel="stylesheet" type="text/css" />

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
<script type="text/javascript"
	src="https://cdn.datatables.net/buttons/2.1.0/js/buttons.print.min.js"></script>
<script type="text/javascript" src="/services/egi/resources/global/js/egov/patternvalidation.js?rnd=${app_release_no}"></script>
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/bootstrap/bootstrap-datepicker.css' context='/services/egi'/>"/>
<script src="<cdn:url value='/resources/global/js/bootstrap/bootstrap-datepicker.js' context='/services/egi'/>" ></script>

<script>
$(document).ready(function() {
	console.log("data table calling");
	   $('#resultHeader').DataTable( {
		   //"aLengthMenu": [ [2, 4, 8, -1], [2, 4, 8, "All"] ],
		   //"iDisplayLength": 4,
		   aaSorting : [],
	       dom: 'Bfrtip',
	       buttons: [
	           'copy', 'csv', 'excel', 'pdf', 'print'
	       ]
	   } );
	} );
</script>
<html>
<head>
<title><s:text name="bill.register.report" /></title>

</head>
<body>
	<s:form action="billRegisterReport" name="billRegisterReport"
		theme="simple" method="post" onsubmit="javascript:doAfterSubmit()">
		<span class="mandatory1"> <s:actionerror /> <s:fielderror />
			<s:actionmessage />
		</span>
		<font style='color: red; font-weight: bold'>
			<p class="error-block" id="lblError"></p>
		</font>
		<div class="formmainbox">
			<div class="subheadnew">
				<s:text name="bill.register.report" />
			</div>
		
		<table align="center" width="100%" cellpadding="0" cellspacing="0">
			<tr>
				<jsp:include page="../voucher/voucher-filter.jsp" />

			</tr>

			<tr>
			<%-- <td style="width: 5%"></td>
				<td class="greybox"><s:text name="voucher.fromdate" /></td>
				<td class="greybox"><s:date name="fromDate" var="fromDateId"
							format="dd/MM/yyyy" /> <s:textfield name="fromDate"
							id="fromDate" value="%{fromDateId}" maxlength="10"
						onkeyup="DateFormat(this,this.value,event,false,'3')" /> <a
					href="javascript:show_calendar('billRegisterReport.fromDate',null,null,'DD/MM/YYYY');"
					style="text-decoration: none">&nbsp;<img
							src="/services/egi/resources/erp2/images/calendaricon.gif"
							border="0" /></a>(dd/mm/yyyy)</td>
				<td class="greybox"><s:text name="voucher.todate" /></td>
				<td class="greybox"><s:date name="toDate" var="toDateId"
						format="dd/MM/yyyy" /> <s:textfield name="toDate" id="toDate"
						value="%{toDateId}" maxlength="10"
						onkeyup="DateFormat(this,this.value,event,false,'3')" /> <a
					href="javascript:show_calendar('billRegisterReport.toDate',null,null,'DD/MM/YYYY');"
					style="text-decoration: none">&nbsp;<img
							src="/services/egi/resources/erp2/images/calendaricon.gif"
							border="0" /></a>(dd/mm/yyyy)</td> --%>
			
			   <td style="width: 5%"></td>
				<td class="greybox"><s:text name="voucher.fromdate" /></td>			   								
				<td class="greybox"><s:date name="fromDate" var="fromDateId" format="dd/MM/yyyy" />
				<s:textfield id="fromDate" name="fromDate" value="%{fromDateId}" data-inputmask="'mask': 'd/m/y'" placeholder="DD/MM/YYYY" readonly="true"/></td>
				
				<td class="greybox"><s:text name="voucher.todate" /></td>											
				<td class="greybox"><s:date name="toDate" var="toDateId" format="dd/MM/yyyy" />
				<s:textfield id="toDate" name="toDate" value="%{toDateId}" data-inputmask="'mask': 'd/m/y'" placeholder="DD/MM/YYYY" readonly="true"/></td>
							
							
			</tr>
			<tr>
			<td style="width: 5%"></td>
				<td class="bluebox"><s:text name="bill.expenditure.type" /></td>
				<td class="bluebox"><s:select name="exptype" id="exptype"
						list="dropdownData.expenditureList" headerKey=""
						headerValue="----Choose----" /></td>
				<td class="bluebox"><s:text name="bill.type" /></td>
				<td class="bluebox"><s:select name="billType" id="billType"
						list="dropdownData.billTypeList" headerKey=""
						headerValue="----Choose----" /></td>
			</tr>
			<tr>
			<td style="width: 5%"></td>
				<td class="greybox"><s:text name="voucher.number" /></td>
				<td class="greybox"><s:textfield name="voucherNumber"
						id="voucherNumber" maxlength="30" value="%{voucherNumber}" class = "patternvalidation" data-pattern="alphanumerichyphenbackslash" /></td>

				<td class="greybox"><s:text name="bill.number" /></td>
				<td class="greybox"><s:textfield name="billNumber"
						id="billNumber" maxlength="30" value="%{billNumber}" class = "patternvalidation" data-pattern="alphanumerichyphenbackslash" /></td>
			</tr>

		</table>
		</div>
		
		<div class="buttonbottom">
			<s:submit method="billSearch" value="Search" cssClass="buttonsubmit"
				onclick="return validate();" />
			<s:submit method="searchform" value="Reset" cssClass="button"
				onclick="return resetAndSubmit();" />
			<input type="button" value="Close"
				onclick="javascript:window.parent.postMessage('close','*');"
				class="button" />

		</div>
		<br>
<c:if test="${not empty CompleteBillRegisterReportDTO}">
		<table class="table table-bordered" id="resultHeader" width="100%"
			border="0" cellspacing="0" cellpadding="0" style="font-size:1rem;">
			<thead>
				<tr style="font-size:1rem;">
					<th><spring:message code="lbl-sl-no" text="Sr. No." /></th>
					<th><spring:message code="billnumber" text="Bill Number" /></th>
					<th><spring:message code="blldate" text="Bill Date" /></th>
					<th><spring:message code="vouchernumber" text="Voucher Number" /></th>
					<th><spring:message code="partyname" text="Party Name" /></th>
					<th><spring:message code="grossamount"
							text="Gross Amount(&#8377;)" /></th>
					<th><spring:message code="deduction" text="Deduction(&#8377;)" /></th>
					<th><spring:message code="netamount"
							text="Net Amount(&#8377;)" /></th>
					<th><spring:message code="paidamount"
							text="Paid Amount(&#8377;)" /></th>
					<th><spring:message code="paymentvouchernumber"
							text="Payment Voucher Number" /></th>
					<th><spring:message code="paymentpexnumber"
							text="Payment Pex number" /></th>
					<th><spring:message code="deductionvouchernumber"
							text="Deduction Voucher Number" /></th>
					<th><spring:message code="deductionpexnumber"
							text="Deduction Pex number" /></th>
					<th><spring:message code="status" text="Status" /></th>
				</tr>
			</thead>
			<tbody>
				<c:choose>
					<c:when
						test="${CompleteBillRegisterReportDTO!=null && CompleteBillRegisterReportDTO.size() > 0}">
						<c:forEach items="${CompleteBillRegisterReportDTO}" var="billRegReport"
							varStatus="item">

							<tr id="assetView" style="font-size:1rem;">

								<td>${item.index + 1}</td>
								<td>${billRegReport.billNumber}</td>
								<td>${billRegReport.billDate}</td>
								<td><a href="#"
									onclick="return openVoucher('${billRegReport.vhid}')">
										${billRegReport.voucherNumber } </a></td>
								<td>${billRegReport.payTo}</td>
								<td>${billRegReport.grossAmount}</td>
								<td>${billRegReport.deduction}</td>
								<td>${billRegReport.netPay}</td>
								<td>${billRegReport.paidAmount}</td>
								<td><a href="#"
									onclick="return openVoucher('${billRegReport.payvhid}')">
										${billRegReport.paymentVoucherNumber} </a></td>
								<td>${billRegReport.paymentPexNumber}</td>
								 <td><a href="#"
									onclick="return openVoucher('${billRegReport.deducVhId}')">
										${billRegReport.deductionvouchernumber} </a></td>
								<td>${billRegReport.deductionpexnumber}</td> 
								<td>${billRegReport.description}</td>
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<td colspan="6">No Records Found..</td>

					</c:otherwise>
				</c:choose>
			</tbody>

		</table>
</c:if>
											


		<!--  -->
	</s:form>

	<script>

		function validate(){
	
		 <s:if test="%{isFieldMandatory('fund')}"> 
				 if(null != document.getElementById('fundId') && document.getElementById('fundId').value == -1){

					document.getElementById('lblError').innerHTML = "Please Select a fund";
					return false;
				 }
			 </s:if>
			<s:if test="%{isFieldMandatory('department')}"> 
				 if(null!= document.getElementById('vouchermis.departmentid') && document.getElementById('vouchermis.departmentid').value == -1){

					document.getElementById('lblError').innerHTML = "Please select a department";
					return false;
				 }
			</s:if>
			<s:if test="%{isFieldMandatory('scheme')}"> 
				 if(null!=document.getElementById('schemeid') &&  document.getElementById('schemeid').value == -1){

					document.getElementById('lblError').innerHTML = "Please select a scheme";
					return false;
				 }
			</s:if>
			<s:if test="%{isFieldMandatory('subscheme')}"> 
				 if(null!= document.getElementById('subschemeid') && document.getElementById('subschemeid').value == -1){

					document.getElementById('lblError').innerHTML = "Please select a subscheme";
					return false;
				 }
			</s:if>
			<s:if test="%{isFieldMandatory('functionary')}"> 
				 if(null!=document.getElementById('vouchermis.functionary') &&  document.getElementById('vouchermis.functionary').value == -1){

					document.getElementById('lblError').innerHTML = "Please select a functionary";
					return false;
				 }
			</s:if>
			<s:if test="%{isFieldMandatory('fundsource')}"> 
				 if(null !=document.getElementById('fundsourceId') &&  document.getElementById('fundsourceId').value == -1){

					document.getElementById('lblError').innerHTML = "Please select a fundsource";
					return false;
				}
			</s:if>
			<s:if test="%{isFieldMandatory('field')}"> 
				 if(null!= document.getElementById('vouchermis.divisionid') && document.getElementById('vouchermis.divisionid').value == -1){

					document.getElementById('lblError').innerHTML = "Please select a field";
					return false;
				 }
			</s:if>
		

			document.forms[0].action='${pageContext.request.contextPath}/report/billRegisterReport-billSearchNew.action';
			document.forms[0].submit();	
			return  true;
}

		function resetAndSubmit()
		{

			document.forms[0].action='${pageContext.request.contextPath}/report/billRegisterReport-searchform.action';
			document.forms[0].submit();	
		

		}
	
		function openVoucher(vid)
		{
			var url = "/services/EGF/voucher/preApprovedVoucher-loadvoucherview.action?vhid="+ vid;
			window.open(url,'','width=900, height=700');
		}
		function test()
		{
			alert('Test in UAT');
		}

		jQuery(document).ready(function() {
		    // Initialize "voucherDate" date picker
		     console.log("onload:");
		    jQuery("#fromDate").datepicker({
		        format: 'dd/mm/yyyy',
		        autoclose: true,
		        // Restrict past dates if needed
		        onRender: function(date) {
		            return date.valueOf() < new Date().valueOf() ? 'disabled' : '';
		        }
		    }).on('changeDate', function(ev) {
			    console.log("onchange:"+ev);
		        var selectedVoucherDate = jQuery(this).datepicker('getDate');  // Get the selected date
		        console.log("selectedVoucherDate:"+selectedVoucherDate);
		        if (selectedVoucherDate) {
		            var maxDate = new Date(selectedVoucherDate);
		            maxDate.setFullYear(maxDate.getFullYear() + 1);  // Set max date to 1 year later
		            console.log("maxDate:"+maxDate);
		            // Set min and max date for "toDate"
		            jQuery("#toDate").datepicker("setStartDate", selectedVoucherDate);
		            jQuery("#toDate").datepicker("setEndDate", maxDate);
		        }

		    });

		    // Initialize "toDate" date picker
		    jQuery("#toDate").datepicker({
		        format: 'dd/mm/yyyy',
		        autoclose: true
		    });
		});
	</script>
</body>
</html>


