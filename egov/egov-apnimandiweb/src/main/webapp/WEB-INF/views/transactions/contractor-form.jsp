<div class="row">
	<div class="col-md-12">			
		<div class="panel panel-primary" data-collapsed="0">
			<div class="panel-heading">
				<div class="panel-title">
					<spring:message code="lbl.contractor.details" />
				</div>
			</div>
			<div class="panel-body">
				<spring:hasBindErrors name="apnimandiContractor">
					<c:forEach var="error" items="${errors.allErrors}">
						<div style="color: red;">
							<b><spring:message message="${error}" /></b> <br />
						</div>
					</c:forEach>
				</spring:hasBindErrors>
				<c:if test="${fn:length(existingContractorList) > 0}">	
					<div class="col-md-12 form-group report-table-container">
						<table class="table table-bordered table-hover multiheadertbl" style="border: 1px solid red;">
							<thead>
								<tr>
									<th><spring:message code="lbl.zone" /></th>
									<th><spring:message code="lbl.name" /></th>
									<th><spring:message code="lbl.valid.from.date" /></th>
									<th><spring:message code="lbl.valid.to.date" /></th>
									<th><spring:message code="lbl.active" /></th>
									<th><spring:message code="lbl.status" /></th>
								</tr>
							</thead>
							<tbody>			
								<c:forEach items="${existingContractorList}" var="existingContractor">
									<tr>
										<td>${existingContractor.zoneName}</td>
										<td>${existingContractor.apnimandiContractor.name}</td>
										<td>${existingContractor.apnimandiContractor.validFromDate}</td>
										<td>${existingContractor.apnimandiContractor.validToDate}</td>
										<td>
											<c:choose>
												<c:when test="${existingContractor.apnimandiContractor.active == 'true'}">
													<c:out value="YES" />
												</c:when>
												<c:otherwise>
													<c:out value="NO" />
												</c:otherwise>
											</c:choose>
										</td>
										<td>${existingContractor.statusName}</td>
									</tr>									
								</c:forEach>
							</tbody>
						</table>
					</div>
				</c:if>
				<div class="form-group">
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.zone" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="zone" id="zone" cssClass="form-control" required="required" cssErrorClass="form-control error">
							<form:options items="${zoneMasters}" itemValue="id" itemLabel="name" />
						</form:select>
						<form:errors path="zone" cssClass="error-msg" />
					</div>
				</div>
				
				<div class="form-group">
					<label for="field-1" class="col-sm-3 control-label">
						<spring:message code="lbl.name" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-1 col-md-1 add-margin">
						<form:select path="salutation" id="salutation" cssClass="form-control" cssErrorClass="form-control error"
									 required="required">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:option value="Mr.">Mr.</form:option>
							<form:option value="Ms.">Ms.</form:option>
							<form:option value="Mrs.">Mrs.</form:option>
						</form:select>
						<form:errors path="salutation" cssClass="error-msg" />
					</div>
					<div class="col-sm-2 col-md-2 add-margin">
						<form:input type="text" path="name" id="name" cssClass="form-control text-left patternvalidation"
									data-pattern="alphabetwithspace" maxlength="100" required="required" />
						<form:errors path="name" cssClass="error-msg" />
					</div>
					<label class="col-sm-3 control-label text-right">
						<spring:message code="lbl.address" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:textarea class="form-control text-left patternvalidation" path="address" id="address" name="address"
									   data-pattern="address" maxlength="250" required="required"/>
						<form:errors path="address" cssClass="error-msg" />
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.contract.signed.on" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:input path="contractSignedOn" class="form-control datepicker"
									title="Please enter a valid date" pattern="\d{1,2}/\d{1,2}/\d{4}" id="contractSignedOn"
									data-inputmask="'mask': 'd/m/y'" required="required" />
						<form:errors path="contractSignedOn" cssClass="add-margin error-msg" />
					</div>
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.aadhaar.no" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" path="aadhaarNo" id="aadhaarNo" cssClass="form-control text-left patternvalidation"
									data-pattern="number" maxlength="4" required="required"/>
						<form:errors path="aadhaarNo" cssClass="error-msg" />
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.valid.from.date" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:input path="validFromDate" class="form-control datepicker"
									title="Please enter a valid date" pattern="\d{1,2}/\d{1,2}/\d{4}" id="validFromDate"
									data-inputmask="'mask': 'd/m/y'" required="required" />
						<form:errors path="validFromDate" cssClass="add-margin error-msg" />
					</div>
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.valid.to.date" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:input path="validToDate" class="form-control datepicker"
									title="Please enter a valid date" pattern="\d{1,2}/\d{1,2}/\d{4}" id="validToDate"
									data-inputmask="'mask': 'd/m/y'" required="required" />
						<form:errors path="validToDate" cssClass="add-margin error-msg" />
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.contractor.share" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:input path="contractorSharePercentage" maxlength="10"
							id="contractorSharePercentage" class="form-control patternvalidation"
							data-pattern="decimalvalues" required="required"
							pattern="\d{0,2}(\.\d{1,2})?"
							title="It will allow upto 3 digits and 2 decimal points"
							data-first-option="false&true" />
						<form:errors path="contractorSharePercentage" cssClass="error-msg" />
					</div>
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.no.of.max.allowed.vendors" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" path="maxAllowedVendorsNo" id="maxAllowedVendorsNo" cssClass="form-control text-left patternvalidation"
									data-pattern="number" maxlength="5" required="required"/>
						<form:errors path="maxAllowedVendorsNo" cssClass="error-msg" />
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.rent.amount.per.day" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:input path="rentAmountPerDay" maxlength="10"
							id="rentAmountPerDay" class="form-control patternvalidation"
							data-pattern="decimalvalues" required="required"
							pattern="\d{0,9}(\.\d{1,2})?"
							title="It will allow upto 11 digits and 2 decimal points"
							data-first-option="false&true" />
						<form:errors path="rentAmountPerDay" cssClass="error-msg" />
					</div>
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.security.fees" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:input path="securityFees" maxlength="10"
							id="securityFees" class="form-control patternvalidation"
							data-pattern="decimalvalues" required="required"
							pattern="\d{0,9}(\.\d{1,2})?"
							title="It will allow upto 11 digits and 2 decimal points"
							data-first-option="false&true" />
						<form:errors path="securityFees" cssClass="error-msg" />
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.due.day.of.collection.for.every.month" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" path="duedayOfCollectionForEveryMonth" id="duedayOfCollectionForEveryMonth" cssClass="form-control text-left patternvalidation"
									data-pattern="number" maxlength="2"/>
						<form:errors path="duedayOfCollectionForEveryMonth" cssClass="error-msg" />
					</div>
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.penalty.amount.per.day" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:input path="penaltyAmountPerDay" maxlength="10"
							id="penaltyAmountPerDay" class="form-control patternvalidation"
							data-pattern="decimalvalues" required="required"
							pattern="\d{0,9}(\.\d{1,2})?"
							title="It will allow upto 11 digits and 2 decimal points"
							data-first-option="false&true" />
						<form:errors path="penaltyAmountPerDay" cssClass="error-msg" />
					</div>
				</div>
				<div class="form-group">					
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.comments" /> :
					</label>
					<div class="col-sm-3 add-margin">
						<form:textarea class="form-control text-left patternvalidation" path="comment" id="comment" name="comment"
									   data-pattern="address" maxlength="1000"/>
						<form:errors path="comment" cssClass="error-msg" />
					</div>
				</div>
			</div>
		</div>
	</div>
</div>