<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn"%>

<form:form name="VoucherSearchForm" role="form" method="post" action="searchVoucherResult" modelAttribute="voucherHeader" id="voucherHeader" 
class="form-horizontal form-groups-bordered" enctype="multipart/form-data" style="margin-top:-20px;">
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
    			
				
				<label class="col-sm-3 control-label text-left-audit">From Date
				</label>
				<div class="col-sm-3 add-margin">
					<form:input id="billFrom" path="billFrom" class="form-control datepicker"  data-date-end-date="0d" placeholder="DD/MM/YYYY"/>
				</div>
				<label class="col-sm-3 control-label text-left-audit">To Date
				</label>
				<div class="col-sm-3 add-margin">
					<form:input id="billTo" path="billTo" class="form-control datepicker"  data-date-end-date="0d"  placeholder="DD/MM/YYYY"/>
				</div>
				
				
    			<label class="col-sm-3 control-label text-left-audit"><spring:message code="lbl.fund" text="Fund"/>
					<span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="fundId" id="fundId"  required="required" class="form-control">
						<form:option value="">-Select-</form:option>
						<form:option value="2">Earmarked Fund</form:option>
						<form:option value="1">Municipal (General) Fund</form:option>
					</form:select>
				</div>
				
				<label class="col-sm-3 control-label text-left-audit"><spring:message code="lbl.department"  text="Department"/>
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="vouchermis.departmentcode" id="department" class="form-control">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${approverDepartmentList}" itemValue="code" itemLabel="name" />
						</form:select>
				</div>
				
				<label class="col-sm-3 control-label text-left-audit"><spring:message code="lbl.vouchernumber" text="Voucher Number"/>
				</label>
				<div class="col-sm-3 add-margin">
					<form:input class="form-control patternvalidation" data-pattern="alphanumericwithspecialcharacters" id="voucherNumber" path="voucherNumber" maxlength="50" />
				</div>
				
				<%-- <label class="col-sm-3 control-label text-left-audit"><spring:message code="lbl.billnumber" text="Bill Number"/>
				</label>
				<div class="col-sm-3 add-margin">
					<form:input class="form-control patternvalidation" data-pattern="alphanumericwithspecialcharacters" id="billNumber" path="billNumber" maxlength="50" />
				</div> --%>
				
    		</div>
    	</div>
    	
    	</div>
    	
        <div class="buttonbottom" align="center">
        <input type="submit" id="search" class="btn btn-primary btn-wf-primary" name="search"  onclick="searchCheck()" value="Search"/>
        </div>
        
         
        
        <br>
        <br>
        <br>
        <div class="tab-pane fade in active" id="resultheader">
      
	        <!-- <div class="panel panel-primary" data-collapsed="0">
	        	<div style="padding: 0 15px;"> -->
	        	 <c:if test="${billRegReportList != null &&  !billRegReportList.isEmpty()}">
	        	   <h3> Search Result</h3>
				<table class="table table-bordered" id="searchResult">
					<thead>
					<tr>
						<th><spring:message code="lbl.serial" text="SL.No."/></th>
						<th>Party Name</th>
						<th>Division</th>
						<th>Budget Head</th>
						<th>Gross Amount</th>
						<th>TDS/I. Tax</th>
						<th>TDS ON IGST</th><th>TDS ON CGST/UTGST</th><th>Labour Cess</th>
						<th>Collection charges</th><th>Water charges</th><th>Quality Cess</th>
						<th>Penalty/Fine</th><th>Security/Amt withheld</th><th>Any other deduction</th>
						<th>Net Amount</th>
						<th>Paid Amount</th>
						<th>Journal Voucher number </th>
						<th> Payment voucher number</th>
						<th>PEX NUMBER</th>
						<th>PEX DATE </th>
						<th>Status</th>
					</tr>
					</thead>
`					
					<tbody>
					<c:forEach items="${billRegReportList}" var="result" varStatus="status">
						<tr>
							<td>
								${ status.index+1}
						    </td>
							
							<td>
							<%-- <a href="#" onclick="openAudit('${result.id}')" ></a> --%>
							${result.partyName }
							</td>
							<td>
							${result.departmentCode }
							</td>
							<td>
							${result.budgetHead }
							</td>
							<td>
							${result.grossAmount }
							</td>
							<%-- <td>
							${result.deductionAmount }
							</td> --%>
							<td>
							 <c:if test="${result.billDetailList != null &&  !result.billDetailList.isEmpty()}">
							  <c:set var="continueExecuting" scope="request" value="true"/>
							 <c:forEach items="${result.billDetailList}" var="resultbilldetails" varStatus="resultbilldetailsstatus">
							 <c:if test="${resultbilldetails.consumerCode != null &&  resultbilldetails.consumerType =='Tax'}">
								
								<c:if test="${continueExecuting}">
								${resultbilldetails.totalAmount }
							</c:if>
								
								<c:set var="continueExecuting" scope="request" value="false"/>
								
							</c:if>
							 </c:forEach>
							 </c:if> 
							</td>
							<td>
							 <c:if test="${result.billDetailList != null &&  !result.billDetailList.isEmpty()}">
							 <c:forEach items="${result.billDetailList}" var="resultbilldetails" varStatus="resultbilldetailsstatus">
							 <c:if test="${resultbilldetails.consumerCode != null &&  resultbilldetails.consumerCode =='3502055'}">
								${resultbilldetails.amountPaid }
							</c:if>
							 </c:forEach>
							 </c:if> 
							</td>
							<td>
							 <c:if test="${result.billDetailList != null &&  !result.billDetailList.isEmpty()}">
							 <c:forEach items="${result.billDetailList}" var="resultbilldetails" varStatus="resultbilldetailsstatus">
							 <c:if test="${resultbilldetails.consumerCode != null &&  resultbilldetails.consumerCode =='3502054'}">
								${resultbilldetails.amountPaid }
							</c:if>
							 </c:forEach>
							 </c:if> 
							</td>
							<td>
							 <c:if test="${result.billDetailList != null &&  !result.billDetailList.isEmpty()}">
							 <c:forEach items="${result.billDetailList}" var="resultbilldetails" varStatus="resultbilldetailsstatus">
							 <c:if test="${resultbilldetails.consumerCode != null &&  resultbilldetails.consumerCode =='3502018'}">
								${resultbilldetails.amountPaid }
							</c:if>
							 </c:forEach>
							 </c:if> 
							</td>
							<td>
							 <c:if test="${result.billDetailList != null &&  !result.billDetailList.isEmpty()}">
							 <c:forEach items="${result.billDetailList}" var="resultbilldetails" varStatus="resultbilldetailsstatus">
							 <c:if test="${resultbilldetails.consumerCode != null &&  resultbilldetails.consumerCode =='1408055'}">
								${resultbilldetails.amountPaid }
							</c:if>
							 </c:forEach>
							 </c:if> 
							</td>
							<td>
							 <c:if test="${result.billDetailList != null &&  !result.billDetailList.isEmpty()}">
							 <c:forEach items="${result.billDetailList}" var="resultbilldetails" varStatus="resultbilldetailsstatus">
							 <c:if test="${resultbilldetails.consumerCode != null &&  resultbilldetails.consumerCode =='1405014'}">
								${resultbilldetails.amountPaid }
							</c:if>
							 </c:forEach>
							 </c:if> 
							</td>
							<td>
							 <c:if test="${result.billDetailList != null &&  !result.billDetailList.isEmpty()}">
							 <c:forEach items="${result.billDetailList}" var="resultbilldetails" varStatus="resultbilldetailsstatus">
							 <c:if test="${resultbilldetails.consumerCode != null &&  resultbilldetails.consumerCode =='3502058'}">
								${resultbilldetails.amountPaid }
							</c:if>
							 </c:forEach>
							 </c:if> 
							</td>
							<td>
							 <c:if test="${result.billDetailList != null &&  !result.billDetailList.isEmpty()}">
							 <c:forEach items="${result.billDetailList}" var="resultbilldetails" varStatus="resultbilldetailsstatus">
							 <c:if test="${resultbilldetails.consumerCode != null &&  resultbilldetails.consumerCode =='1402003'}">
								${resultbilldetails.amountPaid }
							</c:if>
							 </c:forEach>
							 </c:if> 
							</td>
							<td>
							 <c:if test="${result.billDetailList != null &&  !result.billDetailList.isEmpty()}">
							 <c:forEach items="${result.billDetailList}" var="resultbilldetails" varStatus="resultbilldetailsstatus">
							 <c:if test="${resultbilldetails.consumerCode != null &&  resultbilldetails.consumerCode =='3401004'}">
								${resultbilldetails.amountPaid }
							</c:if>
							 </c:forEach>
							 </c:if> 
							</td>
							<td>
							 <c:if test="${result.billDetailList != null &&  !result.billDetailList.isEmpty()}">
							 <c:set var="continueExecuting" scope="request" value="true"/>
							 <c:forEach items="${result.billDetailList}" var="resultbilldetails" varStatus="resultbilldetailsstatus">
							 <c:if test="${resultbilldetails.consumerCode != null &&  resultbilldetails.consumerType =='AnyOtherDeduction'}">
								
								<c:if test="${continueExecuting}">
								${resultbilldetails.amountPaid }
							</c:if>
								
								<c:set var="continueExecuting" scope="request" value="false"/>
								
							</c:if>
							 </c:forEach>
							 </c:if> 
							</td>
							
							<td>
							${result.netAmount }
							</td>
							<td>
							${result.paidAmount }
							</td>
							<td>
							${result.voucherNumber }
							</td>
							<td>
							${result.billNumber }
							</td>
							<td>
							${result.pexNo }
							</td>
							<td>
							<!-- add pex date -->
							${result.pexNodate }
							</td>
							<td>
							${result.status }
							</td>
						</tr>
						</c:forEach>
					<tbody>
					
					<c:if test="${billRegReportList == null || billRegReportList.isEmpty()}">
					No records found
					</c:if>				
				</table>
				<div class="buttonbottom" align="center">
        <input type="submit" id="export" class="btn btn-primary btn-wf-primary" name="export"  onclick="searchCheck()" value="Export"/>
        </div>
				</c:if>	
				<!-- </div>
			<br>
			<br>
	        </div> -->
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
        