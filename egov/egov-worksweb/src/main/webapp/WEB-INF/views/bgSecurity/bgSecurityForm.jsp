<%@ include file="/includes/taglibs.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<form:form name="bg-security-form" role="form" method="post"
	action="bgSecuritySave" modelAttribute="bgSecurityDetails"
	enctype="multipart/form-data"
	id="bgSecurityDetails" class="form-horizontal form-groups-bordered"
	style="margin-top:-20px;">

	<spring:hasBindErrors name="bgSecurityDetails">
		<div class="alert alert-danger"
			style="margin-top: 20px; margin-bottom: 10px;">
			<form:errors path="*" />
			<br>
		</div>
	</spring:hasBindErrors>
	<c:if test="${not empty message}">
						<div id="message" class="success" style="color: green;margin-top:45px;">
							${message }
						</div>
					</c:if>
	<div class="tab-content">
		<div class="tab-pane fade in active" id="auditheader">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="form-group" style="padding: 50px 20px 0;">
					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.tender.preparation.name.work" /></label>
					<div class="col-sm-9 block-colm">
						<form:textarea class="form-control txtRight" path="project_name" style="height: 100px;"
									maxlength="2000"  />
							</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.bg.tender.number" /><span class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control txtRight"
							path="security_tender_number" required="required" />
						<form:errors path="security_tender_number"
							cssClass="add-margin error-msg" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.bg.security.number" /><span class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control txtRight"
							path="security_number" required="required" />
						<form:errors path="security_number"
							cssClass="add-margin error-msg" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.bg.security.validity" /><span class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control txtRight"
							path="security_validity" required="required" />
						<form:errors path="security_validity"
							cssClass="add-margin error-msg" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.bg.security.amount" /></label>
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
							code="lbl.bg.security.loa.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control txtRight"
							path="loaNumber"  />
						<form:errors path="loaNumber" cssClass="add-margin error-msg" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"></label>
			<div class="col-sm-3 add-margin">
				<input type="text" class="form-control" style="visibility:hidden"
									 />
							</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.tender.preparation.naration" /></label>
					<div class="col-sm-9 block-colm">
						<form:textarea class="form-control-works" path="narration" style="height: 100px;"
									maxlength="2000"  />
							</div>

				</div>
			</div>
		</div>
	
	<jsp:include page="fileupload.jsp" />
		<div align="center">
			<input type="submit" id="bgSecuritySave" class="btn btn-primary"
				name="bgSecuritySave" code="lbl.save.bg.security"
				value="Save" />
		</div>
	</div>

</form:form>
