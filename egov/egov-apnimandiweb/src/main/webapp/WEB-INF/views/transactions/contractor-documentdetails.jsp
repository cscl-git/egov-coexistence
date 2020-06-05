<script src="<cdn:url value='/resources/js/app/documentsupload.js?rnd=${app_release_no}'/>"></script>
<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading">
		<div class="panel-title">
			<c:choose>
				<c:when test="${mode != 'view'}">
					<spring:message code="lbl.documents" />
				</c:when>
				<c:otherwise>
					<spring:message code="lbl.upload.document" />
				</c:otherwise>
			</c:choose>
		</div>
	</div>
	<c:if test="${mode == 'view'}">
		<c:forEach items="${documentTypeMasters}" var="documentTypeMaster">
			<div class="row add-border add-margin">
				<div class="col-xs-3 add-margin">
					${documentTypeMaster.documentType}
				</div>
				<div class="col-sm-9 add-margin view-content">
					<c:set var="fileFound" value="0"/>
					<c:if test="${!apnimandiContractor.contractorDocuments.isEmpty()}">						
						<c:forEach items="${apnimandiContractor.contractorDocuments}" var="contractorDocument">
							<c:if test="${contractorDocument.documentType.code eq documentTypeMaster.code}">
								<a href="/services/egi/downloadfile?fileStoreId=${contractorDocument.filestoreid.fileStoreId}&moduleName=Apnimandi">${contractorDocument.filestoreid.fileName}</a><br />
								<c:set var="fileFound" value="1"/>
							</c:if>
						</c:forEach>
					</c:if>
					<c:if test="${fileFound eq '0'}">
						Not Uploaded
					</c:if>
				</div>
			</div>			
		</c:forEach>
	</c:if>
	<c:if test="${mode == 'edit'}">
		<c:forEach items="${documentTypeMasters}" var="documentTypeMaster">
			<div class="row add-border add-margin">
				<div class="col-xs-3 add-margin">
					${documentTypeMaster.documentType}
				</div>
				<div class="col-sm-9 add-margin view-content">
					<c:set var="fileFound1" value="0"/>
					<c:if test="${!apnimandiContractor.contractorDocuments.isEmpty()}">
						<c:forEach items="${apnimandiContractor.contractorDocuments}" var="contractorDocument">
							<c:if test="${contractorDocument.documentType.code eq documentTypeMaster.code}">
								<a href="/services/egi/downloadfile?fileStoreId=${contractorDocument.filestoreid.fileStoreId}&moduleName=Apnimandi">${contractorDocument.filestoreid.fileName}</a><br />
								<input type="file" name="file_${documentTypeMaster.code}" id="file_${documentTypeMaster.code}" onchange="isValidFile(this.id)">
								<c:set var="fileFound1" value="1"/>
							</c:if>
						</c:forEach>
					</c:if>
					<c:if test="${fileFound1 eq '0'}">
						<input type="file" name="file_${documentTypeMaster.code}" id="file_${documentTypeMaster.code}" onchange="isValidFile(this.id)">
					</c:if>
				</div>
			</div>			
		</c:forEach>
	</c:if>
	<c:if test="${mode == 'create'}">
		<c:forEach items="${documentTypeMasters}" var="documentTypeMaster">
			<div class="row add-border add-margin">
				<div class="col-xs-3 add-margin">
					${documentTypeMaster.documentType}
				</div>
				<div class="col-sm-9 add-margin view-content">
					<input type="file" name="file_${documentTypeMaster.code}" id="file_${documentTypeMaster.code}" onchange="isValidFile(this.id)">
				</div>
			</div>			
		</c:forEach>
	</c:if>
</div>