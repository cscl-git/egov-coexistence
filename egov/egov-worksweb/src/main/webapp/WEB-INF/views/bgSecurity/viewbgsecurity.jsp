<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<form:form name="view-bg-security-form" role="form" method="post"
	action="bgSecurityget" modelAttribute="bgSecurityDetails"
	id="bgSecurityDetails" class="form-horizontal form-groups-bordered"
	style="margin-top:-20px;">

	<div class="tab-content">
		<div class="tab-pane fade in active" id="auditheader">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="form-group" style="padding: 50px 20px 0;">

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.bg.security.validity" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control txtRight"
							path="security_validity" readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.bg.security.amount" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control txtRight"
							path="security_amount" readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.bg.security.start.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="security_start_date" path="security_start_date"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.bg.security.end.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="security_end_date" path="security_end_date"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" readonly="true" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.bg.security.loa.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control txtRight"
							path="loaNumber" readonly="true" />
					</div>
				</div>
			</div>
		</div>
	</div>

</form:form>
