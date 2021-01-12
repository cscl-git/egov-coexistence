<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>

<form:form name="auditForm" role="form" method="post" action="search" modelAttribute="auditDetail" id="auditDetail" 
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
    	<h3>Post Audit</h3>
    	<div class="panel panel-primary" data-collapsed="0">
    		<div class="form-group" style="padding : 50px 20px 0;">
    			<label class="col-sm-3 control-label text-left-audit"><spring:message code="lbl.expType" text=" Type"/>
					<span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="expenditureType" id="expenditureType"  required="required" class="form-control">
						<form:option value="">-Select-</form:option>
						<form:option value="Expense">Expense</form:option>
						<form:option value="Works">Works</form:option>
						<form:option value="Purchase">Purchase</form:option>
						<form:option value="Receipt">Receipt</form:option>
					</form:select>
				</div>
				
				<label class="col-sm-3 control-label text-left-audit"><spring:message code="lbl.fund" text="Fund"/>
					<span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="fund" id="fund"  required="required" items="${fundList}" itemValue="id" itemLabel="name" class="form-control">
					</form:select>
				</div>
				
				<label class="col-sm-3 control-label text-left-audit"><spring:message code="lbl.dateFrom"  text="Bill Date From"/>
				<span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:input id="billFrom" path="billFrom" class="form-control datepicker" data-date-end-date="0d" required="required" placeholder="DD/MM/YYYY"/>
				</div>
				<label class="col-sm-3 control-label text-left-audit"><spring:message code="lbl.dateTo"  text="Bill Date To"/>
				<span class="mandatory"></span>
				</label>
				<div class="col-sm-3 add-margin">
					<form:input id="billTo" path="billTo" class="form-control datepicker" data-date-end-date="0d" required="required" placeholder="DD/MM/YYYY"/>
				</div>
				
    		</div>
    	</div>
    	
    	</div>
    	
        <div class="buttonbottom" align="center">
        <input type="submit" id="search" class="btn btn-primary btn-wf-primary" name="search"  value="Search"/>
        </div>
        <br>
        <br>
        <br>
        <div class="tab-pane fade in active" id="resultheader">
        <h3> Search Result</h3>
	        <div class="panel panel-primary" data-collapsed="0">
	        <form:hidden path="counter" id="counter" />
	        <div style="padding: 0 15px;">
				<table class="table table-bordered" id="searchResult">
					<thead>
					<tr>
						<th><spring:message code="lbl.selectAll" text="Select All"/><input type="checkbox" id="selectAll" name="selectAll" onclick="checkAll(this)"></th>
						<th><spring:message code="lbl.expenditure.type" text="Expenditure Type"/></th>
						<th><spring:message code="llbl.bill.type" text="Bill Type"/></th>
						<th><spring:message code="bill.search.billnumber" text="Bill Number"/></th>
						<th><spring:message code="lbl.bill.date" text="Bill Date"/></th>
						<th><spring:message code="lbl.bill.amount" text="Bill Amount"/></th>
						<th><spring:message code="lbl.passed.amount" text="Passed Amount"/></th>
						<th><spring:message code="lbl.bill.status" text="Bill Status"/></th>
					</tr>
					</thead>
`					 <c:if test="${auditDetail.postAuditResultList != null &&  !auditDetail.postAuditResultList.isEmpty()}">
					<tbody>
					<c:forEach items="${auditDetail.postAuditResultList}" var="result" varStatus="status">
						<tr>
							<td>
								<form:checkbox path="postAuditResultList[${status.index}].checked"/>
								<form:hidden path="postAuditResultList[${status.index}].voucherId" id="postAuditResultList[${status.index}].voucherId"/>
						    </td>
							<td>
								<form:hidden path="postAuditResultList[${status.index}].expendituretype" id="postAuditResultList[${status.index}].expendituretype"/>
								${result.expendituretype }
							</td>
							<td>
							<form:hidden path="postAuditResultList[${status.index}].billtype" id="postAuditResultList[${status.index}].billtype"/>
							${result.billtype }
							</td>
							<td>
							<form:hidden path="postAuditResultList[${status.index}].billnumber" id="postAuditResultList[${status.index}].billnumber"/>
							<a href="#" onclick="openBill('${result.sourcepath}')" >
							${result.billnumber }</a>
							</td>
							<td>
							<form:hidden path="postAuditResultList[${status.index}].billdate" id="postAuditResultList[${status.index}].billdate"/>
							${result.billdate }
							</td>
							<td>
							<form:hidden path="postAuditResultList[${status.index}].billamount" id="postAuditResultList[${status.index}].billamount"/>
							${result.billamount }
							</td>
							<td>
							<form:hidden path="postAuditResultList[${status.index}].passedamount" id="postAuditResultList[${status.index}].passedamount"/>
							${result.passedamount }
							</td>
							<td>
							<form:hidden path="postAuditResultList[${status.index}].billstatus" id="postAuditResultList[${status.index}].billstatus"/>
							${result.billstatus }
							</td>
						</tr>
						</c:forEach>
					<tbody>
					</c:if>	
					<c:if test="${auditDetail.postAuditResultList == null ||  auditDetail.postAuditResultList.isEmpty()}">
					No records found
					</c:if>				
				</table>
				</div>
			<br>
			<br>
			<c:if test="${auditDetail.postAuditResultList != null &&  !auditDetail.postAuditResultList.isEmpty()}">
			<br>
			<jsp:include page="billdocument-upload.jsp"/>
			<br>
				<div class="buttonbottom" align="center">
		        	<input type="submit" id="save" class="btn btn-primary btn-wf-primary" name="save"  value="Submit"/>
		        </div>
	        </c:if>
	        </div>
        </div>
    </div>
    
</form:form>
<script
        src="<cdn:url value='/resources/app/js/audit/audit.js?rnd=${app_release_no}' context='/services/audit'/>"></script>
<script
        src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
        