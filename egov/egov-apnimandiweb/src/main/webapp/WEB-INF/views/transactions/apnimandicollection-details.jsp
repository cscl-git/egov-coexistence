<div class="row">
	<div class="col-md-12">
		<div class="panel panel-primary" data-collapsed="0">
			<div class="panel-heading">
				<div class="panel-title"><spring:message code="lbl.collection.details" /></div>
			</div>
			<div class="panel-body custom">
				<div class="col-xs-3 add-margin">
					<spring:message code="lbl.collection.type" />
				</div>
				<div class="col-sm-3 add-margin view-content">
					${apnimandiCollectionDetails.collectiontype.name}
				</div>
				<div class="col-xs-3 add-margin">
					<spring:message code="lbl.zone" />
				</div>
				<div class="col-sm-3 add-margin view-content">
					${apnimandiCollectionDetails.zone.name}
				</div>
			</div>
			<div class="panel-body custom">
				<div class="col-xs-3 add-margin">
					<spring:message code="lbl.collection.month" />
				</div>
				<div class="col-sm-3 add-margin view-content">
					${apnimandiCollectionDetails.collectionMonthName}
				</div>
				<div class="col-xs-3 add-margin">
					<spring:message code="lbl.collection.year" />
				</div>
				<div class="col-sm-3 add-margin view-content">
					${apnimandiCollectionDetails.collectionForYear}
				</div>
			</div>		
			
			<c:if test="${apnimandiCollectionDetails.collectiontype.code eq 'DAY_MARKET'}">
				<div class="panel-body custom">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.contractor" />
					</div>
					<div class="col-sm-9 add-margin view-content">
						${apnimandiCollectionDetails.payeeName}
					</div>
				</div>
			</c:if>
			
			<div class="panel-body custom">
				<div class="col-xs-3 add-margin">
					<spring:message code="lbl.receipt.date" />
				</div>
				<div class="col-sm-9 add-margin view-content">
					<fmt:formatDate pattern="dd/MM/yyyy" value="${apnimandiCollectionDetails.receiptDate}" var="receiptDate" />
					<c:out value="${receiptDate}" />
				</div>
			</div>
			<div class="panel-body custom">
				<div class="col-xs-3 add-margin">
					<spring:message code="lbl.narration" />
				</div>
				<div class="col-sm-9 add-margin view-content">
					${apnimandiCollectionDetails.comment}
				</div>
			</div>
			
			<%@ include file="apnimandicollection-amountdetails.jsp"%>
			
			<div class="panel-body custom">
				<div class="col-xs-3 add-margin">
					<spring:message code="lbl.payment.mode" />
				</div>
				<div class="col-sm-9 add-margin view-content">
					${apnimandiCollectionDetails.paymentMode}
				</div>
				<div class="col-xs-3 add-margin">
					<spring:message code="lbl.amount" />
				</div>
				<div class="col-sm-9 add-margin view-content">
					${apnimandiCollectionDetails.amount}
				</div>
			</div>
			<c:if test="${apnimandiCollectionDetails.paymentMode != 'cash'}">
				<div class="panel-body custom">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.ddcheque.number" />
					</div>
					<div class="col-sm-9 add-margin view-content">
						${apnimandiCollectionDetails.ddOrChequeNo}
					</div>
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.ddcheque.date" />
					</div>
					<div class="col-sm-9 add-margin view-content">
						<fmt:formatDate pattern="dd/MM/yyyy" value="${apnimandiCollectionDetails.ddOrChequeDate}" var="ddOrChequeDate" />
						<c:out value="${ddOrChequeDate}" />
					</div>
				</div>
				<div class="panel-body custom">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.ifsc.code" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						${apnimandiCollectionDetails.ifscCode}
					</div>
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.bank.name" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						${apnimandiCollectionDetails.bankName}
					</div>
				</div>
				<div class="panel-body custom">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.branch.name" />
					</div>
					<div class="col-sm-9 add-margin view-content">
						${apnimandiCollectionDetails.branchName}
					</div>
				</div>
			</c:if>			
		</div>
	</div>
</div>