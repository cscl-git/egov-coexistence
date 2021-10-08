<%@page import="com.kenai.jffi.Array"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<form:form name="glsearchform" role="form" method="post" action=""  modelAttribute="capitalRevenueRequestPojo"
	id="glreport" class="form-horizontal form-groups-bordered"
	style="margin-top:-20px;">

	<div class="tab-content">
		<div class="tab-pane fade in active">
			<div class="panel panel-primary" data-collapsed="0">
			
			<div class="col-md-12">
				    <div class="col-md-5"></div>
				    <div class="col-md-6">
				    <h4>Receipt Report</h4>
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


			
					<div class="buttonbottom" align="center">
						<input type="submit" id="estimateSearch" class="btn btn-primary"
							name="estimateSearch" 
						value="Search" onclick="return generateReport()" />
				</div>
				</div>

		</form:form>
				
				<c:if test="${error}">
					<div class="col-sm-12 add-margin" style="font-size: large; text-align: center;"><b>${msg}</b></div>
				</c:if>
				<%-- <c:if test="${data_success}">	
				<c:if test="${data!=null && data.size()>0}">	
				 <div class="col-sm-12 add-margin" style="font-size: large; text-align: center;"><b>${headertag}</b></div>
				
				<input type="text" value="${capitalRevenueRequestPojo.from_date}" id="from_date_hid" hidden>
				<input type="text" value="${capitalRevenueRequestPojo.to_date}" id="to_date_hid" hidden>
				<input type="text" value="${capitalRevenueRequestPojo.department}" id="department_hid" hidden>
				<input type="text" value="${capitalRevenueRequestPojo.fund}" id="fund_hid" hidden>
				<input type="text" value="${capitalRevenueRequestPojo.expense_type}" id="expense_type_hid" hidden>
				<input type="text" value="${capitalRevenueRequestPojo.function}" id="function_hid" hidden>
				<div class="buttonbottom" align="center">
						<input type="button" id="estimateSearch2" class="btn btn-primary"
							name="estimateSearch2" 
						value="Download Report" onclick="generateReportXls()" />
				</div>
				</c:if>
				
				<c:if test="${data==null||data.size()<=0}">	No Data Found</c:if>
				</c:if> --%>
				
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

	document.forms[0].action="../generalledgerreport/capitaldivisionresultsXLS";
 	document.forms[0].submit();

	
	}
	function validateDates(){
	
	var fromDate = document.getElementById('from_date').value;
	var toDate = document.getElementById('to_date').value;
	//var fund = document.getElementById('fund').value;
	//var expensetype= document.getElementById('expense_type').value;
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
	/*if(fund==null ||fund==''){
		bootbox.alert("Please Select Fund ")
		return false;
	}*/
	
/*	if(expensetype==null ||expensetype==''){
		bootbox.alert("Please Select Expense Type ")
		return false;
	}*/

	return true;	
}
	
	function generateReportXls(){
		
		isValid = validateDates();

		var fromDate = document.getElementById('from_date_hid').value;
		var toDate = document.getElementById('to_date_hid').value;
		var dept = '';//document.getElementById('department_hid').value;

		var fund='';//document.getElementById('fund_hid').value;
		var functiontype='';//document.getElementById('function_hid').value;
		var expense_type='';//document.getElementById('expense_type_hid').value;

		if( fromDate==''|| toDate==''){
			bootbox.alert("Some Mandatory Fields Data Not Populated Please try again ");
		return false;
		}

var url = "../generalledgerreport/capitaldivisionresultsXLS"+"?from_date="+fromDate+"&to_date="+toDate+"&department="+dept+"&fund="+fund+"&function="+functiontype+"&expense_type="+expense_type;
	
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

	
