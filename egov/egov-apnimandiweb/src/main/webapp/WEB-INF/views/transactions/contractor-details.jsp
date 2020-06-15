<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>

<div class="row">
	<div class="col-md-12">
		<div class="panel panel-primary" data-collapsed="0">
			<div class="panel-heading">
				<div class="panel-title"><spring:message code="lbl.contractor.details" /></div>
			</div>
			<div class="panel-body custom">
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.name" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						${apnimandiContractor.salutation} ${apnimandiContractor.name}
					</div>
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.address" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						${apnimandiContractor.address}
					</div>				
				</div>
				
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.contract.signed.on" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						<fmt:formatDate pattern="dd/MM/yyyy" value="${apnimandiContractor.contractSignedOn}" var="contractSignedOn" />
						<c:out value="${contractSignedOn}" />
					</div>
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.aadhaar.no" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						${apnimandiContractor.aadhaarNo}
					</div>				
				</div>
				
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.valid.from.date" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						<fmt:formatDate pattern="dd/MM/yyyy" value="${apnimandiContractor.validFromDate}" var="validFromDate" />
						<c:out value="${validFromDate}" />
					</div>
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.valid.to.date" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						<fmt:formatDate pattern="dd/MM/yyyy" value="${apnimandiContractor.validToDate}" var="validToDate" />
						<c:out value="${validToDate}" />
					</div>				
				</div>
				
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.zone" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						${apnimandiContractor.zone.name}
					</div>
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.no.of.max.allowed.vendors" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						${apnimandiContractor.maxAllowedVendorsNo}
					</div>				
				</div>
				
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.rent.amount.per.day" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						<fmt:formatNumber var="rentAmountPerDay" type="number"
							minFractionDigits="2" maxFractionDigits="2"
							value="${apnimandiContractor.rentAmountPerDay}" />
						<c:out value="${rentAmountPerDay}" />
					</div>
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.security.fees" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						<fmt:formatNumber var="securityFees" type="number"
							minFractionDigits="2" maxFractionDigits="2"
							value="${apnimandiContractor.securityFees}" />
						<c:out value="${securityFees}" />
					</div>				
				</div>
				
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.due.day.of.collection.for.every.month" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						${apnimandiContractor.duedayOfCollectionForEveryMonth}
					</div>
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.penalty.amount.per.day" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						<fmt:formatNumber var="penaltyAmountPerDay" type="number"
							minFractionDigits="2" maxFractionDigits="2"
							value="${apnimandiContractor.penaltyAmountPerDay}" />
						<c:out value="${penaltyAmountPerDay}" />
					</div>				
				</div>
				
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.contractor.share" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						<fmt:formatNumber var="contractorSharePercentage" type="number"
							minFractionDigits="2" maxFractionDigits="2"
							value="${apnimandiContractor.contractorSharePercentage}" />
						<c:out value="${contractorSharePercentage}" />
					</div>									
				</div>
				
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.active" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						<c:choose>
							<c:when test="${apnimandiContractor.active == 'true'}">
								<c:out value="YES" />
							</c:when>
							<c:otherwise>
								<c:out value="NO" />
							</c:otherwise>
						</c:choose>
					</div>
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.status" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						${apnimandiContractor.status.description}
					</div>
				</div>
				
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.comments" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						${apnimandiContractor.comment}
					</div>									
				</div>
				
			</div>
		</div>
	</div>
</div>