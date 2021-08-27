<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<script
        src="<cdn:url value='/resources/js/estimateworks.js?rnd=${app_release_no}' context='/services/works'/>"></script>
        <script
	src="<cdn:url value='/resources/js/estimatepreparationapproval/estimationhelper.js?rnd=${app_release_no}'/>"></script>

	<form:form name="search-estimate-form" role="form" method="post"
		action="" modelAttribute="worksabstractliabilities"
	id="worksabstractliabilities" class="form-horizontal form-groups-bordered"
	style="margin-top:-20px;">

	<div class="tab-content">
	<h3 style="font-weight:bold;" align="center">ABSTRACT/LIABILITIES OF WORK </h3>
		<div class="tab-pane fade in active">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="form-group" style="padding: 50px 20px 0;">
                   <label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.estimate.from.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="fromDt" path="fromdate"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.estimate.to.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="toDt" path="todate" class="form-control datepicker"
							data-date-end-date="0d" placeholder="DD/MM/YYYY" />
					</div>
						</div>
						</div>
					</div>

		<div class="buttonbottom" align="center">
			<input type="button" id="worksabstractSearch" class="btn btn-primary"
						name="excel" code="lbl.search.work.estimate"
						value="Export Excel"  onclick="return validate();"/>
				</div>

	
		
	</div>

	</form:form>
	
	<script>
	function validate(){
		var fromDate = document.getElementById('fromDt').value;
		var toDate = document.getElementById('toDt').value;
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
		}else{
			var fromDate = document.getElementById('fromDt').value;
			var toDate = document.getElementById('toDt').value;
			
			var url = "/services/works/dnit/abstractliabilitiesexcel"+"?fromdate="+fromDate+"&todate="+toDate;
			window.location.href =url;
			return true;
		}
		
		
		
	}
	
	
	</script>



