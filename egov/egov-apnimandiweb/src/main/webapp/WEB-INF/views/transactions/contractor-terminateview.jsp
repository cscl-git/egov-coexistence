<c:if test="${apnimandiContractor.status.code == 'CONTRACTTERMINATED'}">	
	<div class="panel panel-primary" data-collapsed="0">
		<div class="panel-heading">
			<div class="panel-title">
				<spring:message code="lbl.termination.details" />
			</div>
		</div>
		<div class="panel-body custom">
			<div class="row add-border">
				<div class="col-xs-3 add-margin">
					<spring:message code="lbl.termination.on" />
				</div>
				<div class="col-sm-9 add-margin view-content">
					<fmt:formatDate pattern="dd/MM/yyyy" value="${apnimandiContractor.terminateOn}" var="terminateOn" />
					<c:out value="${terminateOn}" />
				</div>
			</div>
			<div class="row add-border">
				<div class="col-xs-3 add-margin">
					<spring:message code="lbl.address" />
				</div>
				<div class="col-sm-9 add-margin view-content">
					${apnimandiContractor.terminateRemarks}
				</div>				
			</div>
		</div>	
	</div>
</c:if>