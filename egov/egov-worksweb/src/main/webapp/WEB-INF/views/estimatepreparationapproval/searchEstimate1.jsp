<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<script
        src="<cdn:url value='/resources/js/estimateworks.js?rnd=${app_release_no}' context='/services/works'/>"></script>

	<form:form name="search-estimate-form" role="form" method="post"
		action="estimateApprovalSearchView" modelAttribute="workEstimateDetails"
	id="workEstimateDetails" class="form-horizontal form-groups-bordered"
	style="margin-top:-20px;">

	<div class="tab-content">
		<div class="tab-pane fade in active">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="form-group" style="padding: 50px 20px 0;">


      <label class="col-sm-3 control-label text-left-audit1"><spring:message
							code="lbl.estimate.search.work.name" /></label>
					<div class="col-sm-3 add-margin">
						<form:textarea class="form-control" path="workName"
							maxlength="2000" />
					</div>
			
			<label class="col-sm-3 control-label text-left-audit1"><spring:message
					code="lbl.estimate.preparation.exp.head" /></label>
			<div class="col-sm-3 add-margin">
							<form:select path="expHead_est"  id="expHead_est"
								cssClass="form-control" cssErrorClass="form-control error">
								<form:option value="">
									<spring:message code="lbl.select" />
								</form:option>
								<form:option value="Capital">Capital</form:option>
								<form:option value="Revenue">Revenue</form:option>
								<form:option value="Deposit Estimate works">Deposit Estimate works</form:option>
								<form:option value="Ward Development Funds">Ward Development Funds</form:option>
							</form:select>
						</div>			

					
					<%--  <label class="col-sm-3 control-label text-left-audit1"><spring:message
							code="lbl.estimate.preparation.Category" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="workCategory"  id="workCategory"/>
					</div>  --%>
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.works.wing" /></label>
					<div class="col-sm-3 add-margin">
								<form:select path="worksWing" id="worksWing"
									cssClass="form-control" cssErrorClass="form-control error">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:option value="Building & Roads">Building & Roads</form:option>
									<form:option value="Public Health">Public Health</form:option>
									<form:option value="Horticulture & Electrical">Horticulture & Electrical</form:option>
								</form:select>
					</div>
					
					<%-- <label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.estimate.preparation.Division" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="workLocation" />
							</div> --%>
					
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.work.estimate.from.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="fromDt" path="fromDt"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
					</div>
					
					<label class="col-sm-3 control-label text-left-audit1"><spring:message
									code="lbl.work.estimate.to.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="toDt" path="toDt" class="form-control datepicker"
							data-date-end-date="0d" placeholder="DD/MM/YYYY" />
					</div>
					
					
					
							<label class="col-sm-3 control-label text-left-audit1"><spring:message
							code="lbl.estimate.preparation.executing.division" /></label>
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
							
					


					
							
					
					
							
					<%-- <label class="col-sm-3 control-label text-left-audit1"><spring:message
							code="lbl.estimate.preparation.executing.division" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="department" id="department" 
							class="form-control">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
							<form:options items="${workEstimateDetails.departments}"
								itemValue="code" itemLabel="name" />
								</form:select>
							</div>	 --%>	
							</div>
						</div>
					</div>

		<div class="buttonbottom" align="center">
			<input type="submit" id="estimateApprovalSearchView" class="btn btn-primary"
						name="estimateApprovalSearchView" code="lbl.search.work.estimate"
						value="Search" />
				</div>

		<br> <br> <br>
		<div class="tab-pane fade in active" id="resultheader">
			<h3>Search Result</h3>
			<div class="panel panel-primary" data-collapsed="0">
				<div style="padding: 0 15px;">
					<table class="table table-bordered" id="table">
						<thead>
							<tr>
								<th><spring:message
										code="lbl.estimate.preparation.name.work" /></th>
								
								<%-- <th><spring:message
										code="lbl.estimate.preparation.Category" /></th> --%>
								<th><spring:message
										code="lbl.estimate.preparation.Division" /></th>
								<th><spring:message
										code="lbl.estimate.preparation.works.wing" /></th>
								<th><spring:message
										code="lbl.estimate.preparation.exp.head" /></th>
								<th><spring:message
										code="lbl.estimate.preparation.Estimate.Cost" /></th>
										<th><spring:message
										code="lbl.estimate.preparation.Estimate.Creation" /></th>
										<th><spring:message
										code="lbl.estimate.preparation.Estimate.Approval" /></th>

							</tr>
						</thead>
						`
						<c:if
							test="${workEstimateDetails.estimateList != null &&  !workEstimateDetails.estimateList.isEmpty()}">
							<tbody>
								<c:forEach items="${workEstimateDetails.estimateList}"
									var="result" varStatus="status">
									<tr>
										<td><form:hidden
												path="estimateList[${status.index}].workName"
												id="estimateList[${status.index}].workName" />
											${result.workName }</td>
										
										<%-- <td><form:hidden
												path="estimateList[${status.index}].workCategry"
												id="estimateList[${status.index}].workCategry" />
											${result.workCategry }</td>--%>
											
										<td> 
										<form:hidden
												path="estimateList[${status.index}].executeDiv"
												id="estimateList[${status.index}].executeDiv" />
											${result.executeDiv }</td>
										<td><form:hidden
												path="estimateList[${status.index}].worksWing"
												id="estimateList[${status.index}].worksWing" />
											${result.worksWing }</td>
										<td><form:hidden
												path="estimateList[${status.index}].expHead_est"
												id="estimateList[${status.index}].expHead_est" />
											${result.expHead_est }</td>
									<td><form:hidden
												path="estimateList[${status.index}].estimateAmount"
												id="estimateList[${status.index}].estimateAmount" />
											${result.estimateAmount }</td>
											<td><form:hidden
												path="estimateList[${status.index}].estimateDt"
												id="estimateList[${status.index}].estimateDt" />
											${result.estimateDt }</td>
											
											<td><form:hidden
												path="estimateList[${status.index}].createdDt"
												id="estimateList[${status.index}].createdDt" />
											${result.createdDt }</td>
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
				<c:if test="${workEstimateDetails.estimateList != null &&  !workEstimateDetails.estimateList.isEmpty()}">
				 <div class="buttonbottom" align="center">
			          <input type="submit" id="workEstimateSearchResult" class="btn btn-primary"
						name="workEstimateSearchResult" code="lbl.search.work.estimate"
						value="Export" />
				</div>
				</c:if>
               
			</div>
		</div>
	</div>

	</form:form>



