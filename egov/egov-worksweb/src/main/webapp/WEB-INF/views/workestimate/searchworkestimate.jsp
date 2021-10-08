<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<script
        src="<cdn:url value='/resources/js/estimateworks.js?rnd=${app_release_no}' context='/services/works'/>"></script>

	<form:form name="search-work-estimate-form" role="form" method="post"
	action="workEstimateSearch" modelAttribute="workEstimateDetails"
	id="workEstimateDetails" class="form-horizontal form-groups-bordered"
	style="margin-top:-20px;">

	<div class="tab-content">
		<div class="tab-pane fade in active">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="form-group" style="padding: 50px 20px 0;">

					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.estimate.from.date" /></label>
					<div class="col-sm-3 add-margin">
								<form:input id="fromDt" path="fromDt"
									class="form-control datepicker" data-date-end-date="0d"
									placeholder="DD/MM/YYYY" />
							</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.estimate.to.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="toDt" path="toDt" class="form-control datepicker"
							data-date-end-date="0d" placeholder="DD/MM/YYYY" />
							</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.estimate.executing.department" /></label>
					<div class="col-sm-3 add-margin">
								<form:select path="department" id="department"
									class="form-control">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:options items="${workEstimateDetails.departments}"
										itemValue="code" itemLabel="name" />
								</form:select>
							</div>
					
							</div>
						</div>
					</div>

		<div class="buttonbottom" align="center">
			<input type="submit" id="workEstimateSearch"
							class="btn btn-primary" name="workEstimateSearch"
							code="lbl.search.work.estimate" value="Search" />
				</div>

		<br> <br> <br>
		<div class="tab-pane fade in active" id="resultheader">
			<h3>Search Result</h3>
			<div class="panel panel-primary" data-collapsed="0">
				<div style="padding: 0 15px;">
					<table class="table table-bordered" id="table">
					<c:if
							test="${workEstimateDetails.estimateList != null &&  !workEstimateDetails.estimateList.isEmpty()}">
						<thead>
							<tr>
								<th><spring:message code="lbl.selectonly" text="Select" /></th>
								<th><spring:message
										code="lbl.estimate.preparation.name.work" /></th>
								<th><spring:message
										code="lbl.dnit.number" /></th>
								
								<th><spring:message
										code="lbl.estimate.preparation.dnit.amount" /></th>
							</tr>
						</thead>
						`
						
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
												path="estimateList[${status.index}].estimateNumber"
												id="estimateList[${status.index}].estimateNumber" />
											${result.estimateNumber }</td>
										<td>
										<form:hidden path="estimateList[${status.index}].id"
												id="estimateList[${status.index}].id" />
										<form:hidden
												path="estimateList[${status.index}].estimateAmount"
												id="estimateList[${status.index}].estimateAmount" />
											${result.estimateAmount }</td>

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
							<div class="buttonbottom" align="center">
								<input type="submit" id="save" class="btn btn-primary"
									name="save" value="Search BOQ" />
				</div>
					</c:if>
				</div>
				<div style="padding: 0 15px;">
					<form:hidden path="id" id="id" value="${workEstimateDetails.id}" />
					
					
						<c:forEach var="mapboq" items="${milestoneList}" varStatus="mapstatus">
					<table id="boq${mapstatus.index}tableBoq" class="table table-bordered tableBoq">
				
				
				
						<thead>
							<tr>
							<th><c:out value="${mapboq.key}"/></th>
							</tr>
							<tr>
								<th><spring:message code="lbl.selectAll" text="Select All" />
									</th>
								<th><spring:message code="lbl.item.Milestone" /></th>	
								<th><spring:message code="lbl.item.description" /></th>
								<th><spring:message code="lbl.ref.dsr" /></th>
								<th><spring:message code="lbl.unit" /></th>
								<th><spring:message code="lbl.rate" /></th>
								<th><spring:message code="lbl.quantity" /></th>
								<th><spring:message code="lbl.amount" /></th>
								
							</tr>
						</thead>
						
						
							<tbody>
					
						
						<c:forEach var="boq" items="${mapboq.value}" varStatus="status">
						
						<%-- <c:if test="${mapboq.key == boq.milestone }"> --%>
								<tr id="boq${mapstatus.index}tableBoqrow" class="boq${status.index}repeat-address">
								
										<td><form:checkbox
												path="newBoQDetailsList[${boq.sizeIndex}].checkboxChecked"
												id="newBoQDetailsList[${boq.sizeIndex}].checkboxChecked" />
												<form:hidden
												path="newBoQDetailsList[${boq.sizeIndex}].slNo"
												id="newBoQDetailsList[${boq.sizeIndex}].slNo" />
												</td>
								<td><form:hidden
												path="newBoQDetailsList[${boq.sizeIndex}].milestone"
												id="newBoQDetailsList[${boq.sizeIndex}].milestone" />
											${boq.milestone }</td>

										<td><form:hidden
												path="newBoQDetailsList[${boq.sizeIndex}].item_description"
												id="newBoQDetailsList[${boq.sizeIndex}].item_description" />
											${boq.item_description }</td>
										<td><form:hidden
												path="newBoQDetailsList[${boq.sizeIndex}].ref_dsr"
												id="newBoQDetailsList[${boq.sizeIndex}].ref_dsr" />
											${boq.ref_dsr }</td>
										<td><form:hidden
												path="newBoQDetailsList[${boq.sizeIndex}].unit"
												id="newBoQDetailsList[${boq.sizeIndex}].unit" />
											${boq.unit }</td>
										<td><form:hidden
												path="newBoQDetailsList[${boq.sizeIndex}].rate"
												id="newBoQDetailsList[${boq.sizeIndex}].rate" />
											${boq.rate }</td>
										<td><form:hidden
												path="newBoQDetailsList[${boq.sizeIndex}].quantity"
												id="newBoQDetailsList[${boq.sizeIndex}].quantity" />
											${boq.quantity }</td>
										<td>
										<form:hidden
												path="newBoQDetailsList[${boq.sizeIndex}].estimatePreparationApproval.id"
												id="newBoQDetailsList[${boq.sizeIndex}].estimatePreparationApproval.id" />
										<form:hidden
												path="newBoQDetailsList[${boq.sizeIndex}].amount"
												id="newBoQDetailsList[${boq.sizeIndex}].amount" />
											${boq.amount }</td>

									</tr>
						<%-- 	</c:if>	 --%>
								</c:forEach>
							
						</tbody>
					</table>
				
					
					
				</c:forEach>
				</div>
				<div>
					<c:if
						test="${workEstimateDetails.newBoQDetailsList != null &&  !workEstimateDetails.newBoQDetailsList.isEmpty()}">
							<div class="buttonbottom" align="center">
								<input type="submit" id="saveboq" class="btn btn-primary"
									name="saveboq" value="Submit" />
						</div>
					</c:if>
				</div>
		</div>
	</div>

	</form:form>



