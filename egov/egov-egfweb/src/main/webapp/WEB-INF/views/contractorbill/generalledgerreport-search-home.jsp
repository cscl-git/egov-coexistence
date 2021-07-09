<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<form:form name="glsearchform" role="form" method="post" action=""  modelAttribute="generalLedgerPOJO"
	id="glreport" class="form-horizontal form-groups-bordered"
	style="margin-top:-20px;">


	<div class="tab-content">
		<div class="tab-pane fade in active">
			<div class="panel panel-primary" data-collapsed="0">
			
			 <div class="col-md-12">
				    <div class="col-md-5"></div>
				    <div class="col-md-6">
				    <h4>Salary  Report</h4>
				    </div>
				</div>
			
				<div class="form-group" style="padding: 50px 20px 0;">

				
					<label class="col-sm-3 control-label text-left-audit">From Date: <span style="color:red;font-weight:bold">*</span></label>
					<div class="col-sm-3 add-margin">
						<form:input id="from_date" name="from_date"  path="from_date"  class="form-control datepicker"
							data-date-end-date="0d" placeholder="DD/MM/YYYY" />
					</div>
				

					<label class="col-sm-3 control-label text-left-audit">To Date: <span style="color:red;font-weight:bold">*</span></label>
					<div class="col-sm-3 add-margin">
						<form:input id="to_date" name="to_date" path="to_date"  class="form-control datepicker"
							data-date-end-date="0d" placeholder="DD/MM/YYYY" />
					</div>


					<label class="col-sm-3 control-label text-left-audit">Department : </label>
					<div class="col-sm-3 add-margin">
						<form:select path="department" id="department" class="form-control">
									<form:option value="" label="Select"></form:option>
							<form:options items="${departments}"
								itemValue="code" itemLabel="name" />
						</form:select>
					</div>

				<div class="col-md-12"></div>
				<div class="col-md-12">
					<div class="buttonbottom" align="center">
						<input type="submit" id="estimateSearch" class="btn btn-primary"
							name="estimateSearch" 
						value="Search" onclick="return generateReport()" />
				</div>
				</div>

				</div>

				</form:form>
				<!-- </div>
				</div> -->

				<c:if test="${data_success}">
				
				 <div class="col-sm-12 add-margin" style="font-size: large; text-align: center;"><b>${headertag}</b></div>
				<div style="padding: 0 15px;">
						<table class="table table-bordered" id="table" style="width: 100%">
						<thead>
							<tr>
							
							<th>Department/GLCODE</th>
							 <th>  GPF</th>
				             <th>  DEFIN  PEN. Contt.</th>
				            <th> PENS.Contt.10%</th>
				            <th> INCOME TAX</th>
				            <th> L.I.C.</th>
				            <th> HBA </th>
				            <th>COURT Recove</th>
				            <th>All L/Fee</th>
				            <th>GIS LIC</th>
				           <th> B/Loan</th>
				            <th>Total</th>
							<tr>
							
						</thead>
						`
						<c:if test="${data.size()>0 }">
							<tbody>
									<c:forEach items="${data}"
									var="d" varStatus="status">
									<tr>
										<td>${d.name }</td>
										<td>${d.glsum1 }</td>
										<td>${d.glsum2 }</td>
										<td>${d.glsum3 }</td>
										<td>${d.glsum4 }</td>
										<td>${d.glsum5 }</td>
										<td>${d.glsum6 }</td>
										<td>${d.glsum7 }</td>
										<td>${d.glsum8 }</td>
										<td>${d.glsum9 }</td>
										<td>${d.glsum10 }</td>
										<td>${d.sum}</td>
										
									</tr>
								</c:forEach>
							<tbody>
						</c:if>
						<c:if test="${data.size()<=0}">
										No records found
					</c:if>
					</table>
				</div>
				<input type="text" value="${generalLedgerPOJO.from_date}" id="from_date_hid" hidden>
				<input type="text" value="${generalLedgerPOJO.to_date}" id="to_date_hid" hidden>
				<input type="text" value="${generalLedgerPOJO.department}" id="department_hid" hidden>
				<div class="buttonbottom" align="center">
						<input type="button" id="estimateSearch2" class="btn btn-primary"
							name="estimateSearch2" 
						value="Download Report" onclick="generateReportXls()" />
				</div>
				</c:if>
			</div>
		</div>
		</div>
	</div>
	<script type="text/javascript"
	src="/services/EGF/resources/javascript/ajaxCommonFunctions.js?rnd=${app_release_no}"></script>
	<script>
		function generateReport(){
				isValid = validateDates();
			if(isValid == false)
		return false;

	document.forms[0].action="../generalledgerreport/results";
 	document.forms[0].submit();

	
	}
	function validateDates(){
	
	var fromDate = document.getElementById('from_date').value;
	var toDate = document.getElementById('to_date').value;
	if(fromDate == '' || toDate == ''){
		bootbox.alert("Please select the dates")
		return false;
	}
	var startDate= fromDate.split('/');
	fromDate=new Date(startDate[2],startDate[1]-1,startDate[0]);
    var endDate = toDate.split('/');
    toDate=new Date(endDate[2],endDate[1]-1,endDate[0]);
	
	
	if(fromDate > toDate ){
		bootbox.alert("From date should not be greater than To date  ")
		return false;
	}
	
	
	return true;	
}
	
	function generateReportXls(){
		
		var fromDate = document.getElementById('from_date_hid').value;
		var toDate = document.getElementById('to_date_hid').value;
		var dept = document.getElementById('department_hid').value;

var url = "../generalledgerreport/xls"+"?from_date="+fromDate+"&to_date="+toDate+"&department="+dept;
	
window.location.href =url;
	
//YAHOO.util.Connect.asyncRequest('POST', url, callback, null);


}
	
	var callback = {
			success: function(o){
				document.getElementById('results').innerHTML=o.responseText;
				undoLoadingMask();
				},
				failure: function(o) {
					undoLoadingMask();
			    }
			}
	</script>

	
