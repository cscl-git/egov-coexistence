<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading">
		<div class="panel-title">
			<spring:message code="lbl.termination.details" />
		</div>
	</div>
	<div class="panel-body">
		<div class="form-group">
			<label class="col-sm-3 control-label text-left">
				<spring:message code="lbl.termination.on" /> <span class="mandatory"></span>:
			</label>
			<div class="col-sm-3 add-margin">
				<form:input path="terminateOn" class="form-control datepicker"
							title="Please enter a valid date" pattern="\d{1,2}/\d{1,2}/\d{4}" id="terminateOn"
							data-inputmask="'mask': 'd/m/y'" required="required" />
				<form:errors path="terminateOn" cssClass="add-margin error-msg" />
			</div>
			<label class="col-sm-3 control-label text-left">
				<spring:message code="lbl.termination.remarks" /> :
			</label>
			<div class="col-sm-3 add-margin">
				<form:textarea class="form-control text-left patternvalidation" path="terminateRemarks" id="terminateRemarks" name="terminateRemarks"
							   data-pattern="address" maxlength="1000"/>
				<form:errors path="terminateRemarks" cssClass="error-msg" />
			</div>
		</div>
	</div>
</div>