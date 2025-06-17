<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn"%>

<form:form name="VoucherSearchForm" role="form" method="post"
	action="searchNewVoucherResult" modelAttribute="voucherHeader"
	id="voucherHeader" class="form-horizontal form-groups-bordered"
	enctype="multipart/form-data" style="margin-top:-20px;">
	<spring:hasBindErrors name="auditDetail">
	       <div class="alert alert-danger"
	            style="margin-top: 20px; margin-bottom: 10px;">
	           <form:errors path="*"/>
	           <br/>
	       </div>
	</spring:hasBindErrors>
	
    <div class="tab-content">
    	<div class="tab-pane fade in active" id="searcheader">
    	<h3>Search Voucher</h3>
    	<div class="panel panel-primary" data-collapsed="0">
    		<div class="form-group" style="padding : 50px 20px 0;">
    			
				
					<label class="col-sm-3 control-label text-left-audit">From
						Date <span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
						<form:input id="billFrom" required="required" path="billFrom"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
				</div>
					<label class="col-sm-3 control-label text-left-audit">To
						Date <span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
						<form:input id="billTo" path="billTo" required="required"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
				</div>
				
				
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.fund" text="Fund" /> <span class="mandatory"></span> </label>
				<div class="col-sm-3 add-margin">
						<form:select path="fundId" id="fundId" required="required"
							class="form-control">
						<form:option value="">-Select-</form:option>
						<form:option value="2">Earmarked Fund</form:option>
						<form:option value="1">Municipal (General) Fund</form:option>
					</form:select>
				</div>
				
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.department" text="Department" /> </label>
				<div class="col-sm-3 add-margin">
						<form:select path="vouchermis.departmentcode" id="department"
							class="form-control">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:options items="${approverDepartmentList}" itemValue="name"
								itemLabel="name" />
						</form:select>
				</div>
				
					<%-- <label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.vouchernumber" text="Voucher Number" /> </label>
				<div class="col-sm-3 add-margin">
						<form:input class="form-control patternvalidation"
							data-pattern="alphanumerichyphenbackslash"
							id="voucherNumber" path="voucherNumber" maxlength="50" />
				</div>
				
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.subdivision" text="Sub Division" /> </label>
				<div class="col-sm-3 add-margin">
						<form:select path="vouchermis.subdivision" id="subdivision"
							class="form-control">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:options items="${subdivisionList}"
								itemValue="subdivisionCode" itemLabel="subdivisionName" />
						</form:select>
				</div> --%>
				
				
				<%-- <label class="col-sm-3 control-label text-left-audit"><spring:message code="lbl.billnumber" text="Bill Number"/>
				</label>
				<div class="col-sm-3 add-margin">
					<form:input class="form-control patternvalidation" data-pattern="alphanumericwithspecialcharacters" id="billNumber" path="billNumber" maxlength="50" />
				</div> --%>
				
    		</div>
    	</div>
    	
    	</div>
    	
        <div class="buttonbottom" align="center">
			<input type="submit" id="search"
				class="btn btn-primary btn-wf-primary" name="search"
				onclick="searchCheck()" value="Search" />
        </div>
        
         
        
		<br> <br> <br>
        <div class="tab-pane fade in active" id="resultheader">
      
	        
			<c:if
				test="${billRegReportList != null &&  !billRegReportList.isEmpty()}">
	        	   <h3> Search Result</h3>
				<table class="table table-bordered" id="searchResult">
					<thead>
					<tr>
						<th><spring:message code="lbl.serial" text="SL.No."/></th>
						<th>Party Name</th>
						<th>Department Name</th>
						<th>Debit Side Account Head</th>
						<th>Debit Amount</th>
						<th>Credit Side Account Head</th>
						<th>Credit Amount</th>
						<th>Scheme</th>
						<th>Budget Bill Amount</th>
						<th>Paid Amount</th>
						<th>Journal Voucher No</th>
						<th>Journal Voucher Date</th>
						<th>Payment Voucher No</th>
						<th>Payment Voucher Date</th>
						<th>Pex Number</th>
						<th>Pex Date</th>
						<th>Pex Account No</th>
						<th>Status</th>						
					</tr>
					</thead>
`					
					<tbody>
						<c:forEach items="${billRegReportList}" var="result"
							varStatus="status">
						<tr>
								<td>${ status.index+1}</td>
								<td>${ result.partyName}</td>
								<td>${ result.departmentName}</td>
								<td>${ result.debitSideAccountHead}</td>
								<td>${ result.debitAmount}</td>
								<td>${ result.creditSideAccountHead}</td>
								<td>${ result.creditAmount}</td>
								<td>${ result.scheme}</td>
								<td>${ result.budgetBillAmount}</td>
								<td>${ result.paidAmount}</td>
								<td>${ result.voucherNo}</td>
								<td>${ result.voucherDate}</td>
								<td>${ result.paymentvoucherNo}</td>
								<td>${ result.paymentvoucherDate}</td>
								<td>${ result.pexNo}</td>
								<td>${ result.pexDate}</td>
								<td>${ result.pexaccountnumber}</td>
								<td>${ result.status}</td>
						</tr>
						</c:forEach>
					<tbody>
					
						<c:if
							test="${billRegReportList == null || billRegReportList.isEmpty()}">
					No records found
					</c:if>				
				</table>
				<div class="buttonbottom" align="center">
					<input type="submit" id="export"
						class="btn btn-primary btn-wf-primary" name="export"
						onclick="searchCheck()" value="Export" />
        </div>
				</c:if>	
				
        </div>
         
    </div>
    
</form:form>
<script>
	$('#search').click(function(e) {

		if ($('form').valid()) {

		} else {

		e.preventDefault();

	}  

});

	$('#export').click(function(e) {

		if ($('form').valid()) {

		} else {

		e.preventDefault();

	}  

});
</script>
<script
        src="<cdn:url value='/resources/app/js/audit/audit.js?rnd=${app_release_no}' context='/services/audit'/>"></script>
<script
	src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script type="text/javascript" src="/services/egi/resources/global/js/egov/patternvalidation.js?rnd=${app_release_no}"></script>
