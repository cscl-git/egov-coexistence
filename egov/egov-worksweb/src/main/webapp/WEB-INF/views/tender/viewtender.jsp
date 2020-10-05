<%@ include file="/includes/taglibs.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<form:form name="view-tender-form" role="form" method="post"
	 modelAttribute="tender" id="tender"
	class="form-horizontal form-groups-bordered" style="margin-top:-20px;">

	<div class="tab-content">
		<div class="tab-pane fade in active" id="auditheader">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="form-group" style="padding: 50px 20px 0;">

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.tender.amount" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control"
							path="procurementAmount" readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.tender.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="procurementDate" path="procurementDate"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.tender.vendor.details" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control"
							path="contractorDetails" readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.tender.loa.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control" path="loaNumber"
							readonly="true" />
					</div>
				</div>
			</div>
		</div>
	</div>
</form:form>
