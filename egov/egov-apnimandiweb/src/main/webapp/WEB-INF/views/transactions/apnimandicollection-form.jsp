<div class="row">
	<div class="col-md-12">			
		<div class="panel panel-primary" data-collapsed="0">
			<div class="panel-heading">
				<div class="panel-title">
					<spring:message code="lbl.collection.details" />
				</div>
			</div>
			<div class="panel-body">
				<spring:hasBindErrors name="apnimandiCollectionDetails">
					<c:forEach var="error" items="${errors.allErrors}">
						<div style="color: red;">
							<b><spring:message message="${error}" /></b><br />
						</div>
					</c:forEach>
				</spring:hasBindErrors>
				<form:hidden id="payeeName" name="payeeName" path="payeeName"/>
				<form:hidden id="collectedBy" name="collectedBy" path="collectedBy"/>
				<form:hidden id="collectionDate" name="collectionDate" path="collectionDate"/>
				<c:if test="${ mode == 'edit'}">
					<div class="form-group">
						<div class="col-xs-3 add-margin text-right">
							<spring:message code="lbl.collection.type" /> :
						</div>
						<div class="col-sm-3 add-margin view-content">
							${apnimandiCollectionDetails.collectiontype.name}
						</div>
						<div class="col-xs-3 add-margin text-right">
							<spring:message code="lbl.zone" /> :
						</div>
						<div class="col-sm-3 add-margin view-content">
							${apnimandiCollectionDetails.zone.name}
						</div>
					</div>
					<div class="form-group">
						<div class="col-xs-3 add-margin text-right">
							<spring:message code="lbl.site" /> :
						</div>
						<div class="col-sm-3 add-margin view-content">
							${apnimandiCollectionDetails.site.name}
						</div>
					</div>
					<form:hidden id="collectiontype" name="collectiontype" path="collectiontype"/>
					<form:hidden id="zone" name="zone" path="zone"/>
					<form:hidden id="site" name="site" path="site"/>
					<input type="hidden" id="collectionTypeCode" name="collectionTypeCode" value="${collectionTypeCode}"/>
					<input type="hidden" id="zoneId" name="zoneId" value="${zoneId}"/>
				</c:if>
				<c:if test="${ mode != 'edit'}">
					<div class="form-group">
						<label class="col-sm-3 control-label text-left">
							<spring:message code="lbl.collection.type" /> <span class="mandatory"></span>:
						</label>
						<div class="col-sm-3 add-margin">
							<form:select path="collectiontype" id="collectiontype" cssClass="form-control" required="required" cssErrorClass="form-control error">
								<form:option value=""><spring:message code="lbl.select" /></form:option>
								<form:options items="${apnimandiCollectionTypes}" itemValue="id" itemLabel="name"/>
							</form:select>
							<form:errors path="collectiontype" cssClass="error-msg" />
						</div>
						<label class="col-sm-3 control-label text-left">
							<spring:message code="lbl.zone" /> <span class="mandatory"></span>:
						</label>
						<div class="col-sm-3 add-margin">
							<form:select path="zone" id="zone" cssClass="form-control" required="required" cssErrorClass="form-control error">
								<form:option value=""><spring:message code="lbl.select" /></form:option>
								<form:options items="${zoneMasters}" itemValue="id" itemLabel="name" />
							</form:select>
							<form:errors path="zone" cssClass="error-msg" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label text-left">
							<spring:message code="lbl.site" /> <span class="mandatory"></span>:
						</label>
						<div class="col-sm-3 add-margin">
							<form:select path="site" id="site" cssClass="form-control" cssErrorClass="form-control error">
								<form:option value=""><spring:message code="lbl.select" /></form:option>
							</form:select>
							<form:errors path="site" cssClass="error-msg" />
						</div>
					</div>
				</c:if>				
				<div class="form-group">
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.collection.month" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="collectionForMonth" id="collectionForMonth" cssClass="form-control" required="required" cssErrorClass="form-control error">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${months}" />
						</form:select>
						<form:errors path="collectionForMonth" cssClass="error-msg" />
					</div>
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.collection.year" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="collectionForYear" id="collectionForYear" cssClass="form-control" required="required" cssErrorClass="form-control error">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${years}" />
						</form:select>
						<form:errors path="collectionForYear" cssClass="error-msg" />
					</div>
				</div>	
				<c:if test="${ mode == 'edit'}">
					<div class="form-group" id="dvApnimandiContractor">
						<label class="col-sm-3 control-label text-left">
							<spring:message code="lbl.contractor" /> <span class="mandatory"></span>:
						</label>
						<div class="col-sm-3 add-margin">
							<form:select path="contractor" id="contractor" cssClass="form-control" cssErrorClass="form-control error">
								<form:option value=""><spring:message code="lbl.select" /></form:option>
								<form:options items="${apnimandiContractorList}" itemValue="id" itemLabel="name" />
							</form:select>
							<form:errors path="contractor" cssClass="error-msg" />
						</div>
					</div>
				</c:if>
				<c:if test="${ mode != 'edit'}">
					<div class="form-group" id="dvApnimandiContractor">
						<label class="col-sm-3 control-label text-left">
							<spring:message code="lbl.contractor" /> <span class="mandatory"></span>:
						</label>
						<div class="col-sm-3 add-margin">
							<form:select path="contractor" id="contractor" cssClass="form-control" cssErrorClass="form-control error">
								<form:option value=""><spring:message code="lbl.select" /></form:option>
							</form:select>
							<form:errors path="contractor" cssClass="error-msg" />
						</div>
					</div>
				</c:if>			
				
				<div class="form-group">
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.receipt.date" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:input path="receiptDate" class="form-control datepicker"
									title="Please enter a valid date" pattern="\d{1,2}/\d{1,2}/\d{4}" id="receiptDate"
									data-inputmask="'mask': 'd/m/y'" required="required" />
						<form:errors path="receiptDate" cssClass="add-margin error-msg" />
					</div>
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.narration" /> :
					</label>
					<div class="col-sm-3 add-margin">
						<form:textarea class="form-control text-left patternvalidation" path="comment" id="comment" name="comment"
									   data-pattern="address" cols="18" rows="1" maxlength="125"/>
						<form:errors path="comment" cssClass="error-msg" />
					</div>
				</div>	
				<c:if test="${ mode != 'edit'}">
					<div class="form-group" id="dvApnimandiPaymentType">
						<label class="col-sm-3 control-label text-left">
							<spring:message code="lbl.payment.type" /> <span class="mandatory"></span>:
						</label>
						<div class="col-sm-3 add-margin">
							<form:hidden id="serviceCategory" name="serviceCategory" path="serviceCategory"/>
							<form:select path="serviceType" id="serviceType" cssClass="form-control" cssErrorClass="form-control error">
								<form:option value=""><spring:message code="lbl.select" /></form:option>
							</form:select>
							<form:errors path="serviceType" cssClass="error-msg" />
						</div>
					</div>
				</c:if>
				<c:if test="${ mode == 'edit'}">
					<form:hidden id="serviceType" name="serviceType" path="serviceType"/>
					<div class="form-group">
						<label class="col-sm-3 control-label text-left">
							<spring:message code="lbl.active" />:
						</label>
						<div class="col-sm-3 add-margin">
							<form:select path="active" id="active" cssClass="form-control" cssErrorClass="form-control error">
								<form:option value="true">YES</form:option>
								<form:option value="false">NO</form:option>
								<form:errors path="active" cssClass="error-msg" />
							</form:select>
						</div>
					</div>
				</c:if>
				
				<%@ include file="apnimandicollection-amountdetails.jsp"%>
				
				<div>
					<div class="panel-heading">
						<div class="panel-title">
							<spring:message code="lbl.payment.details" />
							<div class="billhead2 text-center" style="font-size: 14px;color: #000000;background: #FFFFCC;padding: 8px;">
								<spring:message code="lbl.total.amount.received" /> : Rs. <span id="totalAmount"></span>
							</div>
						</div>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.payment.mode" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="paymentMode" id="paymentMode" cssClass="form-control" cssErrorClass="form-control error"
									 required="required">
							<form:option value="cash">Cash</form:option>
							<form:option value="cheque">Cheque</form:option>
							<form:option value="dd">DD</form:option>
						</form:select>
					</div>
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.amount" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:input path="amount" maxlength="10" id="amount" class="form-control patternvalidation"
									data-pattern="decimalvalues" required="required" readonly="true"
							pattern="\d{0,8}(\.\d{1,2})?"
							title="It will allow upto 8 digits and 2 decimal points"
							data-first-option="false&true" />
						<form:errors path="amount" cssClass="error-msg" />
					</div>
				</div>
				
				<div class="form-group" id="dvDDCheque">
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.ddcheque.number" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:input path="ddOrChequeNo" class="form-control text-left patternvalidation" id="ddOrChequeNo"
									data-pattern="alphanumeric" maxlength="20" required="required" />
						<form:errors path="ddOrChequeNo" cssClass="error-msg" />
					</div>
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.ddcheque.date" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:input path="ddOrChequeDate" class="form-control datepicker"
									title="Please enter a valid date" pattern="\d{1,2}/\d{1,2}/\d{4}" id="ddOrChequeDate"
									data-inputmask="'mask': 'd/m/y'" required="required" />
						<form:errors path="ddOrChequeDate" cssClass="add-margin error-msg" />
					</div>
				</div>	
				<div class="form-group" id="dvIFSC">
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.ifsc.code" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:input path="ifscCode" class="form-control text-left patternvalidation" id="ifscCode"
									data-pattern="alphanumeric" maxlength="12" required="required" style="width: 80%;display:inline;"/>
						<form:errors path="ifscCode" cssClass="error-msg" />
						<span class="searchBoxCls" style="border: 1px solid blue; padding: 5px 10px;">
							<button type="button" onclick='findBankDetailsByIfsc()'><i class="fa fa-search"></i></button>
						</span>
						<div id='bankExistenceResponseMessageId'></div>
					</div>
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.bank.name" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" path="bankName" class="form-control text-left patternvalidation" id="bankName"
									data-pattern="address" maxlength="50" required="required" readonly="true"/>
						<form:errors path="bankName" cssClass="error-msg" />
					</div>
				</div>			
				<div class="form-group" id="dvBranch">
					<label class="col-sm-3 control-label text-left">
						<spring:message code="lbl.branch.name" /> <span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" path="branchName" class="form-control text-left patternvalidation" id="branchName"
									data-pattern="address" maxlength="50" required="required" readonly="true"/>
						<form:errors path="branchName" cssClass="error-msg" />
						<form:hidden id="bankCode" name="bankCode" path="bankCode"/>
					</div>
				</div>				
			</div>
		</div>
	</div>
</div>