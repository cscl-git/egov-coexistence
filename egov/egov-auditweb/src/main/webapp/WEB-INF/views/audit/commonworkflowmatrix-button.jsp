<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="buttonbottom" align="center">
	<table>
		<tr>
			<td id="actionButtons">
				<c:if test="${auditDetail.auditStatus == 'Created' || auditDetail.auditStatus == 'Pending with Auditor'}">
				<c:if test="${auditDetail.auditEmployees != null && !auditEmployees.isEmpty()}">
				 <form:select path = "leadAuditorEmpNo" id="leadAuditorEmpNo">
                     <form:option value = "-1" label = "Select RSA"/>
                     <form:options items = "${auditDetail.auditEmployees}" itemValue="empCode" itemLabel="empName" />
                  </form:select>   
				</c:if>
				<br>
				<br>
						<input type="submit" id="sendToDept" class="btn btn-primary btn-wf-primary"  onclick="setWorkFLowAction('department')" value="Send To Dept"/>
						<input type="submit" id="sendToSO" class="btn btn-primary btn-wf-primary"  onclick="setWorkFLowAction('sectionOfficer')" value="Send To RSA"/>
				</c:if>
				<c:if test="${auditDetail.auditStatus == 'Pending with Department' }">
						<c:forEach items="${validActionList}" var="validButtons">
						<input type="submit" id="${validButtons}" name="${validButtons}" class="btn btn-primary btn-wf-primary" onclick="setWorkFLowAction('${validButtons}')" value="${validButtons}"/>
					</c:forEach>
				</c:if>
				<c:if test="${auditDetail.auditStatus == 'Pending with Section Officer' }">
						<input type="submit" id="sendToDept" class="btn btn-primary btn-wf-primary"  onclick="setWorkFLowAction('department')" value="Send To Dept"/>
						<input type="submit" id="sendToAud" class="btn btn-primary btn-wf-primary"  onclick="setWorkFLowAction('auditor')" value="Send To Auditor"/>
						<input type="submit" id="sendToExaminer" class="btn btn-primary btn-wf-primary"  onclick="setWorkFLowAction('examiner')" value="Send To Examiner"/>
						<input type="submit" id="approve" class="btn btn-primary btn-wf-primary"  onclick="setWorkFLowAction('approve')" value="Approve"/>
						<c:if test="${auditDetail.auditType == 'Pre-Audit' }">
							<input type="submit" id="reject" class="btn btn-primary btn-wf-primary"  onclick="setWorkFLowAction('reject')" value="Reject"/>
						</c:if>
				</c:if>
				<c:if test="${auditDetail.auditStatus == 'Pending with Examiner' }">
					<c:if test="${auditDetail.auditEmployees != null && !auditEmployees.isEmpty()}">
				 <form:select path = "leadAuditorEmpNo" id="leadAuditorEmpNo">
                     <form:option value = "-1" label = "Select RSA"/>
                     <form:options items = "${auditDetail.auditEmployees}" itemValue="empCode" itemLabel="empName" />
                  </form:select>   
				</c:if>
						<input type="submit" id="sendToDept" class="btn btn-primary btn-wf-primary"  onclick="setWorkFLowAction('department')" value="Send To Dept"/>
						<input type="submit" id="sendToSO" class="btn btn-primary btn-wf-primary"  onclick="setWorkFLowAction('sectionOfficer')" value="Send To RSA"/>
						<input type="submit" id="approve" class="btn btn-primary btn-wf-primary"  onclick="setWorkFLowAction('approve')" value="Approve"/>
						<c:if test="${auditDetail.auditType == 'Pre-Audit' }">
							<input type="submit" id="reject" class="btn btn-primary btn-wf-primary"  onclick="setWorkFLowAction('reject')" value="Reject"/>
						</c:if>
				</c:if>
				<input type="button" name="button2" id="button2" value='<spring:message code="lbl.close" text="Close"/>' class="btn btn-default" onclick="window.parent.postMessage('close','*');window.close();" />
			</td>
		</tr>
	</table>
</div>