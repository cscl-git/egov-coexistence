<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>

<div class="row">
	<div class="col-md-12">
		<div class="panel panel-primary" data-collapsed="0">
			<div class="panel-heading">
				<div class="panel-title"><spring:message code="lbl.library.details" /></div>
			</div>
			<div class="panel-body custom">
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.title" />
					</div>
					<div class="col-sm-9 add-margin view-content">
						${library.title}
					</div>
				</div>
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.document.type" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						${library.documentType.documentType}
					</div>
					<div class="col-xs-2 add-margin">
						<spring:message code="lbl.active" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						<c:choose>
							<c:when test="${library.active == 'true'}">
								<c:out value="YES" />
							</c:when>
							<c:otherwise>
								<c:out value="NO" />
							</c:otherwise>
						</c:choose>
					</div>
				</div>
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.uploaded.document" />
					</div>
					<div class="col-sm-9 add-margin view-content">
						<a href="/services/egi/downloadfile?fileStoreId=${library.filestoreid.fileStoreId}&moduleName=LCMS">${library.filestoreid.fileName }</a><br />
					</div>
				</div>
			</div>
		</div>
	</div>
</div>