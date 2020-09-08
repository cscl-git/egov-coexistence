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
<title>tender</title>
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

	<form:form name="view-tender-form" role="form" method="post"
		action="tenderSave" modelAttribute="tender" id="tender">
		<div class="card">
			<div class="container">
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.tender.amount" /></label>
							<div class="col-md-6 block-colm">

								${tender.procurementAmount}</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.tender.date" /></label>
							<div class="col-md-6 block-colm">${tender.procurementDate}
							</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.tender.vendor.details" /></label>
							<div class="col-md-6 block-colm">

								${tender.contractorDetails}</div>
							<div class="clearfix"></div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="form-group">
							<label for="inputPassword"
								class="col-md-6 col-form-label block-colm"><spring:message
									code="lbl.tender.loa.number" /></label>
							<div class="col-md-6 block-colm">${tender.loaNumber}</div>
							<div class="clearfix"></div>

						</div>
					</div>
				</div>

			</div>
		</div>
	</form:form>
</body>
</html>