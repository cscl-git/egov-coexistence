<%@ include file="/includes/taglibs.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<form:form name="tender-form" role="form" method="post"
	action="tenderSave" modelAttribute="tender" id="tender"
	enctype="multipart/form-data"
	class="form-horizontal form-groups-bordered" style="margin-top:-20px;">

	<spring:hasBindErrors name="tender">
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
					<div class="col-sm-9 add-margin">
						<form:textarea class="form-control" path="project_name" style="height: 100px;"
									maxlength="2000"  />
							</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.tender.loa.number" /><span class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="loaNumber" required="required"/>
						<form:errors path="loaNumber" cssClass="add-margin error-msg" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.tender.amount" /><span class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control"
							path="procurementAmount" required="required"/>
						<form:errors path="procurementAmount"
							cssClass="add-margin error-msg" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.tender.date" /><span class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
						<form:input id="procurementDate" path="procurementDate"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" required="required"/>
						<form:errors path="procurementDate"
							cssClass="add-margin error-msg" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.tender.vendor.details" /><span class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control"
							path="contractorDetails" required="required"/>
						<form:errors path="contractorDetails"
							cssClass="add-margin error-msg" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.tender.pro.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control"
							path="tenderProNumber" />
						<form:errors path="tenderProNumber"
							cssClass="add-margin error-msg" />
					</div>
				</div>
			</div>
		</div>

		<jsp:include page="fileupload.jsp" />

		<div align="center">
			<input type="submit" id="tenderSave" class="btn btn-primary"
				name="tenderSave" code="lbl.save.tender"
				value="Save" />
		</div>

	</div>
</form:form>
