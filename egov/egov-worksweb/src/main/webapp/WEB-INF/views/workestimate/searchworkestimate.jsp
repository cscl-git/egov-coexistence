<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

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

					<div class="buttonbottom" align="center">
						<input type="submit" id="workEstimateSearch"
							class="btn btn-primary" name="workEstimateSearch"
							code="lbl.search.work.estimate" value="Search" />
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
							<div class="buttonbottom" align="center">
								<input type="submit" id="save" class="btn btn-primary"
									name="save" value="Submit" />
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
							<div class="buttonbottom" align="center">
								<input type="submit" id="saveboq" class="btn btn-primary"
									name="saveboq" value="Submit" />
						</div>
					</c:if>
				</div>
			</div>
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


