<%@ include file="/includes/taglibs.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<form:form name="bg-security-form" role="form" method="post"
	action="bgSecuritySave" modelAttribute="bgSecurityDetails"
	id="bgSecurityDetails" class="form-horizontal form-groups-bordered"
	style="margin-top:-20px;">

	<spring:hasBindErrors name="bgSecurityDetails">
		<div class="alert alert-danger"
			style="margin-top: 20px; margin-bottom: 10px;">
			<form:errors path="*" />
			<br>
		</div>
	</spring:hasBindErrors>

	<div class="tab-content">
		<div class="tab-pane fade in active" id="auditheader">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="form-group" style="padding: 50px 20px 0;">

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.bg.security.validity" /><span class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control txtRight"
							path="security_validity" required="required" />
						<form:errors path="security_validity"
							cssClass="add-margin error-msg" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.bg.security.amount" /><span class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control txtRight"
							path="security_amount" required="required" />
						<form:errors path="security_amount"
							cssClass="add-margin error-msg" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.bg.security.start.date" /><span class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
						<form:input id="security_start_date" path="security_start_date"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" required="required" />
						<form:errors path="security_start_date"
							cssClass="add-margin error-msg" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.bg.security.end.date" /><span class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
						<form:input id="security_end_date" path="security_end_date"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" required="required" />
						<form:errors path="security_end_date"
							cssClass="add-margin error-msg" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.bg.security.loa.number" /><span class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control txtRight"
							path="loaNumber" required="required" />
						<form:errors path="loaNumber" cssClass="add-margin error-msg" />
					</div>

				</div>
			</div>
		</div>

		<div align="center">
			<input type="submit" id="bgSecuritySave" class="btn btn-primary"
				name="bgSecuritySave" code="lbl.save.bg.security"
				value="Save Bg Security Creation" />
		</div>
	</div>

</form:form>
