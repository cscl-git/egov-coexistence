<%@page import="java.util.ArrayList"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html lang="en">
<head>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"
	integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm"
	crossorigin="anonymous">
<script type="text/javascript">
	
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
	
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
	
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
	
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
	
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>
<!-- JS, Popper.js, and jQuery -->
	<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js" integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj" crossorigin="anonymous">
</script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"
	integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
	crossorigin="anonymous"></script>
<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"
	integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN"
	crossorigin="anonymous"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"
	integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
	crossorigin="anonymous"></script>
<meta charset="UTF-8">
<title>Search work Estimate</title>
<link href="/css/main.css" rel="stylesheet">
<style>
.tab {
	float: left;
}

.tab-2 {
	margin-left: 50px
}

.tab-2 input {
	display: block;
	margin-bottom: 10px
}

tr {
	transition: all .25s ease-in-out
}

tr:hover {
	background-color: #EEE;
	cursor: pointer
}

.btn-info {
	background: #f0794f;
	border: none;
	border-radius: 3px;
	font-size: 15px;
	padding: 10px 20px;
	color: white;
}

.btn-info:hover {
	background: #fdd3b6;
	transition: 0.5s background;
	cursor: pointer;
}

.container {
	padding: 10px 50px 20px;
}

.card {
	/* Add shadows to create the "card" effect */
	box-shadow: 0 5px 9px 0 rgba(0, 0, 0, 0.7);
	padding: 20px 0;
}

* {
	box-sizing: border-box;
}

.row:after {
	content: "";
	display: table;
	clear: both;
}

.container {
	padding: 10px 50px 20px;
}

.card {
	/* Add shadows to create the "card" effect */
	box-shadow: 0 5px 9px 0 rgba(0, 0, 0, 0.7);
	padding: 20px 0;
}

.btn-info {
	background: #f0794f;
	border: none;
	border-radius: 3px;
	font-size: 18px;
	padding: 10px 20px;
	color: white;
}

.btn-info:hover {
	background: #fdd3b6;
	transition: 0.5s background;
	cursor: pointer;
}

.txtRight {
	float: right
}

.block-colm {
	display: inline-block;
	float: left;
	text-align: right;
}

.vertical-center {
	text-align: center;
}
</style>

</head>
<body>

	<form:form name="search-work-estimate-form" role="form" method="post"
		action="workEstimateSearch" modelAttribute="workEstimateDetails"
		id="workEstimateDetails">
		<input type="number" name="workEstimateDetails"
			value="${estimatePreparationApproval.id}" />
		<div class="card">
			<div class="container">
				<div class="row">

					<%-- <div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.work.estimate.status" /></label>
							<div class="col-md-6 block-colm">
								<form:select path="workCategory" id="workCategory"
									cssClass="form-control" cssErrorClass="form-control error"
									required="required">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:option value="1">Initiated</form:option>
									<form:option value="2">Under Verification</form:option>
									<form:option value="3">Approved</form:option>
									<form:option value="4">AA Approved</form:option>
									<form:option value="5">Detailed Estimate Approved</form:option>
								</form:select>
							</div>
							<div class="clearfix"></div>
						</div>
					</div>
 --%>
					<%-- 				<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.work.estimate.number" /></label>
							<div class="col-md-6 block-colm">
								<form:input type="number" class="form-control txtRight"
									path="estimateNumber" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div> --%>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.work.estimate.from.date" /></label>
							<div class="col-md-6 block-colm">
								<form:input id="fromDt" path="fromDt"
									class="form-control datepicker" data-date-end-date="0d"
									placeholder="DD/MM/YYYY" />
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.work.estimate.to.date" /></label>
							<div class="col-md-6 block-colm">
								<form:input id="toDt" path="toDt"
									class="form-control datepicker" data-date-end-date="0d"
									placeholder="DD/MM/YYYY" />
							</div>
							<div class="clearfix"></div>

						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.work.estimate.executing.department" /></label>
							<div class="col-md-6 block-colm">
								<form:select path="department" id="department"
									class="form-control">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:options items="${workEstimateDetails.departments}"
										itemValue="code" itemLabel="name" />
								</form:select>
							</div>
							<div class="clearfix"></div>
						</div>
					</div>
				</div>
				<div class="vertical-center">
					<input type="submit" id="workEstimateSearch" class="btn-info"
						name="workEstimateSearch" code="lbl.search.work.estimate"
						value="Search Work Estimate" />
				</div>

				<div style="padding: 0 15px;">
					<table class="table table-bordered" id="table">
						<thead>
							<tr>
								<th><spring:message code="lbl.selectonly" text="Select" /></th>
								<th><spring:message
										code="lbl.estimate.preparation.name.work" /></th>
								<th><spring:message
										code="lbl.estimate.preparation.work.category" /></th>
								<th><spring:message
										code="lbl.estimate.preparation.work.status" /></th>
								<th><spring:message
										code="lbl.estimate.preparation.estimate.amount" /></th>
								<th><spring:message
										code="lbl.estimate.preparation.estimate.amount" /></th>
							</tr>
						</thead>
						`
						<c:if
							test="${workEstimateDetails.estimateList != null &&  !workEstimateDetails.estimateList.isEmpty()}">
							<tbody>
								<c:forEach items="${workEstimateDetails.estimateList}"
									var="result" varStatus="status">
									<tr>
										<td><form:radiobutton
												path="estimateList[${status.index}].checked"
												id="estimateList[${status.index}].checked" name="radio1"
												value="true" onclick="radioSelection(this)" /></td>
										<td><form:hidden
												path="estimateList[${status.index}].workName"
												id="estimateList[${status.index}].workName" />
											${result.workName }</td>
										<td><form:hidden
												path="estimateList[${status.index}].workCategory"
												id="estimateList[${status.index}].workCategory" />
											${result.workCategory }</td>
										<td><form:hidden
												path="estimateList[${status.index}].workStatus"
												id="estimateList[${status.index}].workStatus" />
											${result.workStatus }</td>
										<td><form:hidden
												path="estimateList[${status.index}].estimateAmount"
												id="estimateList[${status.index}].estimateAmount" />
											${result.estimateAmount }</td>
										<td><form:hidden path="estimateList[${status.index}].id"
												id="estimateList[${status.index}].id" /> ${result.id }</td>

									</tr>
								</c:forEach>
							<tbody>
						</c:if>
						<c:if
							test="${workEstimateDetails.estimateList == null ||  workEstimateDetails.estimateList.isEmpty()}">
					No records found
					</c:if>
					</table>
				</div>

				<div>
					<c:if
						test="${workEstimateDetails.estimateList != null &&  !workEstimateDetails.estimateList.isEmpty()}">
				<div class="vertical-center">
					<input type="submit" id="save" class="btn-info" name="save"
						value="Submit" />
				</div>
					</c:if>
				</div>

				<div style="padding: 0 15px;">
					<%-- <input type="hidden" name="workEstimateDetails" value="${estimatePreparationApproval.id}" /> --%>
					<form:hidden path="id" id="id" value="${workEstimateDetails.id}" />
					<table class="table table-bordered" id="searchResult">
						<thead>
							<tr>
								<th><spring:message code="lbl.selectAll" text="Select All" />
									<input type="checkbox" id="selectAll" name="selectAll"
									onclick="checkAll(this)"></th>
								<th><spring:message code="lbl.item.description" /></th>
								<th><spring:message code="lbl.item.description" /></th>
								<th><spring:message code="lbl.ref.dsr" /></th>
								<th><spring:message code="lbl.unit" /></th>
								<th><spring:message code="lbl.rate" /></th>
								<th><spring:message code="lbl.quantity" /></th>
								<th><spring:message code="lbl.amount" /></th>
								<th><spring:message code="lbl.amount" /></th>
							</tr>
						</thead>
						`
						<c:if
							test="${workEstimateDetails.newBoQDetailsList != null &&  !workEstimateDetails.newBoQDetailsList.isEmpty()}">
							<tbody>
								<c:forEach items="${workEstimateDetails.newBoQDetailsList}"
									var="result" varStatus="status">
									<tr>
										<td><form:checkbox
												path="newBoQDetailsList[${status.index}].checkboxChecked"
												id="newBoQDetailsList[${status.index}].checkboxChecked" /></td>
										<td><form:hidden
												path="newBoQDetailsList[${status.index}].slNo"
												id="newBoQDetailsList[${status.index}].slNo" />
											${result.slNo }</td>

										<td><form:hidden
												path="newBoQDetailsList[${status.index}].item_description"
												id="newBoQDetailsList[${status.index}].item_description" />
											${result.item_description }</td>
										<td><form:hidden
												path="newBoQDetailsList[${status.index}].ref_dsr"
												id="newBoQDetailsList[${status.index}].ref_dsr" />
											${result.ref_dsr }</td>
										<td><form:hidden
												path="newBoQDetailsList[${status.index}].unit"
												id="newBoQDetailsList[${status.index}].unit" />
											${result.unit }</td>
										<td><form:hidden
												path="newBoQDetailsList[${status.index}].rate"
												id="newBoQDetailsList[${status.index}].rate" />
											${result.rate }</td>
										<td><form:hidden
												path="newBoQDetailsList[${status.index}].quantity"
												id="newBoQDetailsList[${status.index}].quantity" />
											${result.quantity }</td>
										<td><form:hidden
												path="newBoQDetailsList[${status.index}].amount"
												id="newBoQDetailsList[${status.index}].amount" />
											${result.amount }</td>

										<td><form:hidden
												path="newBoQDetailsList[${status.index}].estimatePreparationApproval.id"
												id="newBoQDetailsList[${status.index}].estimatePreparationApproval.id" />
											${result.estimatePreparationApproval.id }</td>

									</tr>
								</c:forEach>
							<tbody>
						</c:if>
						<c:if
							test="${workEstimateDetails.newBoQDetailsList == null ||  workEstimateDetails.newBoQDetailsList.isEmpty()}">
					No records found
					</c:if>
					</table>
				</div>
				<div>
					<c:if
						test="${workEstimateDetails.newBoQDetailsList != null &&  !workEstimateDetails.newBoQDetailsList.isEmpty()}">
						<div class="vertical-center">
							<input type="submit" id="saveboq" class="btn-info" name="saveboq"
								value="Submit" />
						</div>
					</c:if>
				</div>
			</div>
		</div>
	</form:form>

	<script type="text/javascript">
		function checkAll(ele) {
			var checkboxes = document.getElementsByTagName('input');
			if (ele.checked) {
				for (var i = 0; i < checkboxes.length; i++) {
					if (checkboxes[i].type == 'checkbox') {
						checkboxes[i].checked = true;
					}
				}
			} else {
				for (var i = 0; i < checkboxes.length; i++) {
					console.log(i)
					if (checkboxes[i].type == 'checkbox') {
						checkboxes[i].checked = false;
					}
				}
			}
		}

		function radioSelection(r) {
			var selectedRow = r.parentNode.parentNode.rowIndex;
			for (var i = 1; i < table.rows.length; i++) {
				rIndex = i;
				if (selectedRow == rIndex) {
					document.getElementById("estimateList[" + (rIndex - 1)
							+ "].checked").value = true;
					document.getElementById("estimateList[" + (rIndex - 1)
							+ "].checked").checked = true;
				} else {
					document.getElementById("estimateList[" + (rIndex - 1)
							+ "].checked").value = false;
					document.getElementById("estimateList[" + (rIndex - 1)
							+ "].checked").checked = false;
				}
			}
		}
	</script>
</body>
</html>

