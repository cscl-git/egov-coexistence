<div class="row">
	<div class="col-md-12">
		<div class="panel panel-primary" data-collapsed="0">
			<div class="panel-heading">
				<div class="panel-title">
					<spring:message code="lbl.document.details" />
				</div>
			</div>
			<div class="panel-body">
				<div class="form-group">
					<label class="col-sm-2 control-label text-right"><spring:message
							code="lbl.title" /> :<span class="mandatory"></span> </label>
					<div class="col-sm-10 add-margin">
						<form:input class="form-control patternvalidation" maxlength="255" id="title"
							path="title" data-pattern="address" required="required"/>
						<form:errors path="title" cssClass="add-margin error-msg" />
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label text-right">
						<spring:message code="lbl.document.type" /> :<span class="mandatory"></span> 
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="documentType" id="documentType"
							cssClass="form-control" required="required"
							cssErrorClass="form-control error">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:options items="${documentTypeMasters}" itemValue="id" itemLabel="documentType" />
						</form:select>
						<form:errors path="documentType" cssClass="error-msg" />
					</div>
					<label class="col-sm-2 control-label text-right"><spring:message
							code="lbl.active" /> :<span class="mandatory"></span></label>
					<div class="col-sm-2 add-margin">
						<form:select path="active" id="active" cssClass="form-control"
							cssErrorClass="form-control error" required="required">
							<form:option value="true">YES</form:option>
							<form:option value="false">NO</form:option>
							<form:errors path="active" cssClass="error-msg" />
						</form:select>
					</div>					
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label text-right"><spring:message
							code="lbl.attachment" /> :<span class="mandatory"></span> </label>
					<div class="col-sm-10 add-margin">
						<c:if test="${mode == 'edit'}">
							<a href="/services/egi/downloadfile?fileStoreId=${library.filestoreid.fileStoreId}&moduleName=LCMS">${library.filestoreid.fileName }</a><br />
							<input type="file" name="file" id="file1" onchange="isValidFile(this.id)">
						</c:if>
						<c:if test="${mode == 'new'}">
							<input type="file" name="file" id="file1" onchange="isValidFile(this.id)" required="required">
						</c:if>						
						<div class="add-margin error-msg text-left">
							<font size="2">
								<spring:message code="lbl.mesg.document"/>	
							</font>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>	