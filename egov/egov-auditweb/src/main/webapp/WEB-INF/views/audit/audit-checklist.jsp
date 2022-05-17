<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<script
	src="<cdn:url value='/resources/app/js/audit/audit.js?rnd=${app_release_no}' context='/services/audit'/>"></script>

<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading custom_form_panel_heading">
		<div class="panel-title">
			<spring:message code="lbl.checklist" text="CheckList" />
		</div>
	</div>

	<div style="padding: 0 15px;">
		<table class="table table-bordered" id="tblchecklist">
			<thead>
				<tr>
					<th><spring:message code="lbl.checklist.name" text="Name" /></th>
					<th><spring:message code="lbl.checklist.status" text="Status" /></th>
					<c:if
						test="${auditDetail.auditStatus == 'Pending with Department' }">
						<th style="display: none;"><spring:message
								code="lbl.checklist.date" text="Date" /></th>
						<th style="display: none;"><spring:message
								code="lbl.checklist.auditcomments" text="Auditor Comments" /></th>
						<th><spring:message code="lbl.checklist.usercomments"
								text="User Comments" /></th>
					</c:if>
					<c:if
						test="${auditDetail.auditStatus == 'Created' || auditDetail.auditStatus == 'Pending with Auditor'}">
						<th><spring:message code="lbl.checklist.date" text="Date" /></th>
						<th><spring:message code="lbl.checklist.auditcomments"
								text="Auditor Comments" /></th>
						<th style="display: none;"><spring:message
								code="lbl.checklist.usercomments" text="Department Comments" /></th>
					</c:if>
					<c:if
						test="${auditDetail.auditStatus == 'Pending with Section Officer' || auditDetail.auditStatus == 'Pending with Examiner'}">
						<th><spring:message code="lbl.checklist.date" text="Date" /></th>
						<th style="display: none;"><spring:message
								code="lbl.checklist.auditcomments" text="Auditor Comments" /></th>
						<th style="display: none;"><spring:message
								code="lbl.checklist.usercomments" text="Department Comments" /></th>
					</c:if>
					<th><spring:message code="lbl.checklist.auditHistory"
							text="History" /></th>
					<c:if test="${mode !='view' }">
						<c:if
							test="${auditDetail.auditStatus == 'Created' || auditDetail.auditStatus == 'Pending with Auditor'}">
							<th>Add Row</th>
						</c:if>
					</c:if>
				</tr>
			</thead>
			<tbody id="tbl_add_body">
				<c:forEach items="${auditDetail.checkList}" var="audit"
					varStatus="status">
					<tr id="rec-${status.index}">
						<td><c:if
								test="${auditDetail.auditStatus != 'Created' && auditDetail.auditStatus != 'Pending with Auditor'}">
								<form:hidden
									path="checkList[${status.index}].checklist_description"
									id="checkList[${status.index}].checklist_description"
									class="checklist_description" />
 			${audit.checklist_description}
 			</c:if> <c:if
								test="${auditDetail.auditStatus == 'Created' || auditDetail.auditStatus == 'Pending with Auditor'}">
								<form:input
									path="checkList[${status.index}].checklist_description"
									id="checkList[${status.index}].checklist_description"
									value="${audit.checklist_description}"
									class="form-control checklist_description" maxlength="200"></form:input>
							</c:if></td>
						<td><c:if
								test="${auditDetail.auditStatus == 'Created' || auditDetail.auditStatus == 'Pending with Auditor'}">
								<select name="checkList[${status.index}].status"
									id="checkList[${status.index}].status"
									class="form-control status">
									<option value="">-Select-</option>
									<option value="Seen/Checked">Seen/Checked</option>
									<option value="Incorrect">Incorrect</option>
									<option value="Not Required">Not Required</option>
								</select>
							</c:if> <c:if
								test="${auditDetail.auditStatus == 'Pending with Department' || auditDetail.auditStatus == 'Pending with Section Officer' || auditDetail.auditStatus == 'Pending with Examiner'}">
								<select name="checkList[${status.index}].status"
									id="checkList[${status.index}].status" disabled="disabled"
									class="form-control status">
									<option value="">-Select-</option>
									<option value="Seen/Checked">Seen/Checked</option>
									<option value="Incorrect">Incorrect</option>
									<option value="Not Required">Not Required</option>
								</select>
							</c:if></td>

						<c:if
							test="${auditDetail.auditStatus == 'Created' || auditDetail.auditStatus == 'Pending with Auditor'}">
							<c:if test="${audit.status == 'Seen/Checked' }">
								<td><form:input type="text"
										id="checkList[${status.index}].checklist_date"
										path="checkList[${status.index}].checklist_date"
										class="form-control datepicker checklist_date"
										data-date-end-date="0d" readonly="true" value=""
										placeholder="DD/MM/YYYY" /></td>
							</c:if>
							<c:if test="${audit.status != 'Seen/Checked' }">
								<td><form:input type="text"
										id="checkList[${status.index}].checklist_date"
										path="checkList[${status.index}].checklist_date"
										class="form-control datepicker checklist_date"
										data-date-end-date="0d" value="" placeholder="DD/MM/YYYY" /></td>
							</c:if>

						</c:if>
						<c:if
							test="${auditDetail.auditStatus == 'Pending with Section Officer' || auditDetail.auditStatus == 'Pending with Examiner'}">
							<td><form:input type="text"
									id="checkList[${status.index}].checklist_date"
									path="checkList[${status.index}].checklist_date"
									class="form-control datepicker checklist_date"
									data-date-end-date="0d" value="" readonly="true"
									placeholder="DD/MM/YYYY" /></td>
						</c:if>
						<c:if
							test="${auditDetail.auditStatus == 'Created' || auditDetail.auditStatus == 'Pending with Auditor'}">
							<c:if test="${audit.status == 'Seen/Checked' }">
								<td><form:input
										path="checkList[${status.index}].auditor_comments"
										id="checkList[${status.index}].auditor_comments"
										class="form-control auditor_comments" readonly="true"
										maxlength="200"></form:input></td>
							</c:if>
							<c:if test="${audit.status != 'Seen/Checked' }">
								<td><form:input
										path="checkList[${status.index}].auditor_comments"
										id="checkList[${status.index}].auditor_comments"
										class="form-control auditor_comments"
										maxlength="200"></form:input></td>
							</c:if>

							<td style="display: none;"><form:textarea
									path="checkList[${status.index}].user_comments"
									id="checkList[${status.index}].user_comments"
									class="form-control user_comments" maxlength="200"
									></form:textarea></td>
						</c:if>

						<c:if
							test="${auditDetail.auditStatus == 'Pending with Department' }">
							<td style="display: none;"><form:textarea
									path="checkList[${status.index}].auditor_comments"
									id="checkList[${status.index}].auditor_comments"
									 class="form-control auditor_comments" 
									maxlength="200"></form:textarea></td><!-- required="required" removed by abhishek on 11052022 -->
							<c:if test="${audit.status != 'Seen/Checked' }">
								<td><form:textarea
										path="checkList[${status.index}].user_comments"
										id="checkList[${status.index}].user_comments"
										class="form-control user_comments" 
										maxlength="200"></form:textarea></td><!-- required="required" removed by abhishek on 11052022 -->
							</c:if>
							<c:if test="${audit.status == 'Seen/Checked' }">
								<td><form:textarea
										path="checkList[${status.index}].user_comments"
										id="checkList[${status.index}].user_comments"
										class="form-control user_comments" readonly="true"
										maxlength="200"></form:textarea></td>
							</c:if>
						</c:if>
						<td><a href="#"
							onclick="return openHistory('${auditDetail.auditId}','${ audit.checkListId}');"
							style="text-decoration: none">&nbsp; <img
								src="/services/egi/resources/erp2/images/history.png" border="0" /></a>
						</td>
						<c:if test="${mode !='view' }">
							<c:if
								test="${auditDetail.auditStatus == 'Created' || auditDetail.auditStatus == 'Pending with Auditor'}">
								<td class="text-center">
									<!-- <span style="cursor:pointer;" onclick="addcheckListRow(this);" tabindex="0" id="tempSubLedger[0].addButton" data-toggle="tooltip" title="" data-original-title="" aria-hidden="true"><i class="fa fa-plus"></i></span> -->
									<%--  <span class="add-padding subledge-delete-row" data-id="${status.index}" onclick="deleteSubledgerRow(this);"><i class="fa fa-trash"  aria-hidden="true" data-toggle="tooltip" title="" data-original-title="Delete!"></i></span> --%>
									<span style="cursor: pointer;" onclick="addcheckListRowNew()"
									tabindex="0" class="Addrow" data-toggle="tooltip" title=""
									data-original-title="" aria-hidden="true"> <i
										class="fa fa-plus"></i>
								</span> <span class="add-padding subledge-delete-row"> <a
										class="fa fa-trash dataDelete" data-id="${status.index}"
										style="color: black;" href="#"
										data-href="${audit.checkListId},${status.index}"
										data-toggle="modal" data-target="#confirm-deleterow"
										data-original-title="Delete!"></a>
								</span>
								
						
								</td>

							</c:if>
						</c:if>
					</tr>

				</c:forEach>
			</tbody>
		</table>
		<div>
		<c:if
			test="${auditDetail.auditStatus == 'Created' || auditDetail.auditStatus == 'Pending with Auditor' || auditDetail.auditStatus == 'Pending with Section Officer' || auditDetail.auditStatus == 'Pending with Examiner'}">
		Pass Under Objection
		<form:checkbox path="passUnderobjection" id="passUnderobjection"
					onclick="myFunction()" value="${auditDetail.passUnderobjection}"></form:checkbox>
		</c:if>
		</div>
		<div class="form-group" >
			<div id="xxxx" style="display: none;">
				<label class="col-sm-3 control-label text-left-audit"><spring:message
						text="Comment" /> </label>
				<div class="col-sm-3 add-margin">
					<form:input class="form-control"
						data-pattern="alphanumericwithspecialcharacters"
						id="passunderobjectioncomment" path="passunderobjectioncomment"
						maxlength="500" />
				</div>
			</div>
		</div>
	<!-- added for retrachment -->
		<c:if
			test="${auditDetail.auditStatus == 'Created' || auditDetail.auditStatus == 'Pending with Auditor' || auditDetail.auditStatus == 'Pending with Section Officer' || auditDetail.auditStatus == 'Pending with Examiner'}">
		<div>
			Retrachment
			<c:if test="${auditDetail.retrachmentcheck == 'Y'}">
				<form:checkbox path="retrachmentcheck" id="retrachmentcheck"
					onclick="myFunctionRet()" value="${auditDetail.retrachmentcheck}" ></form:checkbox>
				<div style="text-align: right;">
					<div class="form-group">
						<div id="recomment" style="display: none;">
							<label class="col-sm-3 control-label text-left-audit"><spring:message
									text="Retrachment Comment" /> </label>
							<div class="col-sm-3 add-margin">
								<form:textarea class="form-control"
									data-pattern="alphanumericwithspecialcharacters"
									id="retrachmentcomment" path="retrachmentcomment" value="${auditDetail.retrachmentcomment}" 
									maxlength="2000" />
							</div>
						</div>
					</div>
				</div>
			</c:if>
			<c:if test="${auditDetail.retrachmentcheck != 'Y' || auditDetail.retrachmentcheck == ''}">
				<form:checkbox path="retrachmentcheck" id="retrachmentcheck"
					onclick="myFunctionRet()" value="Y"></form:checkbox>
				<div style="text-align: right;">
					<div class="form-group">
						<div id="recomment" style="display: none;">
							<label class="col-sm-3 control-label text-left-audit"><spring:message
									text="Retrachment Comment" /> </label>
							<div class="col-sm-3 add-margin">
								<form:textarea class="form-control"
									data-pattern="alphanumericwithspecialcharacters"
									id="retrachmentcomment" path="retrachmentcomment"
									maxlength="2000" />
							</div>
						</div>
					</div>
				</div>
			</c:if>
		
		</div>
		</c:if>

	</div>
</div>


<div class="modal fade" id="confirm-deleterow" tabindex="-1"
	role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">Confirm Delete</h4>
			</div>
			<div class="modal-body">
				<input type="hidden" name="rowindex" class="rowindex" />
				<p>Do you want to proceed?</p>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
				<a id="deletebtnok" herf="" style="display: none">Delete</a>
				<button type="button" class="btn btn-danger btn-okrow" value="">Delete</button>
			</div>
		</div>
	</div>
</div>


<div style="display: none;">
	<table id="blank_table">
		<tr id="rec-0">
			<td><form:input path="checkList[0].checklist_description"
					id="checkList[0].checklist_description" value=""
					class="form-control checklist_description" maxlength="200"></form:input>
			</td>
			<td><select name="checkList[0].status" id="checkList[0].status"
				class="form-control status">
					<option value="">-Select-</option>
					<option value="Seen/Checked">Seen/Checked</option>
					<option value="Incorrect">Incorrect</option>
					<option value="Not Required">Not Required</option>
			</select></td>
			<td><form:input id="checkList[0].checklist_date"
					path="checkList[0].checklist_date"
					class="form-control datepicker checklist_date"
					data-date-end-date="0d" placeholder="DD/MM/YYYY" /></td>
			<td><form:input path="checkList[0].auditor_comments"
					id="checkList[0].auditor_comments"
					class="form-control auditor_comments" readonly="false"
					maxlength="200"></form:input></td>
			<td><a href="#" 0 style="text-decoration: none">&nbsp; <img
					src="/services/egi/resources/erp2/images/history.png" border="0" /></a>
			</td>

			<td class="text-center"><span style="cursor: pointer;"
				onclick="addcheckListRowNew()" tabindex="0" class="Addrow"
				data-toggle="tooltip" title="" data-original-title=""
				aria-hidden="true"> <i class="fa fa-plus"></i>
			</span> <span class="add-padding subledge-delete-row"> <a
					class="fa fa-trash dataDelete" data-id="0" style="color: black;"
					href="#" data-href="" data-toggle="modal"
					data-target="#confirm-deleterow" data-original-title="Delete!"></a>
			</span></td>
		</tr>
	</table>
</div>
<script>
/* function myFunctionRet() {
alert("called");
	var checkBox = document.getElementsByName("retrachmentcheck");
	// Get the output text
	var text = document.getElementById("recomment");

	// If the checkbox is checked, display the output text
	if (checkBox.checked == true) {
		text.style.display = "block";
	} else {
		text.style.display = "none";
	}
} */
</script>
