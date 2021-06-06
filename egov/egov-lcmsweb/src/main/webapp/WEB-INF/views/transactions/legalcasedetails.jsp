<%--
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) 2017  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~            Further, all user interfaces, including but not limited to citizen facing interfaces,
  ~            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
  ~            derived works should carry eGovernments Foundation logo on the top right corner.
  ~
  ~            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
  ~            For any further queries on attribution, including queries on brand guidelines,
  ~            please contact contact@egovernments.org
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  ~
  --%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>
<c:if test="${not empty message}">
	<div cssClass="add-margin error-msg" role="alert">${message}</div>
</c:if>
<input type="hidden" name="mode" value="${mode}" />
<spring:hasBindErrors name="legalCase">
	<c:forEach var="error" items="${errors.allErrors}">
		<form:errors path="caseNumber" cssClass="add-margin error-msg" />
		<div style="color: red;">
			<b><spring:message message="${error}" /></b> <br />
		</div>
	</c:forEach>
</spring:hasBindErrors>

<div class="form-group">
	<label class="col-sm-3 control-label text-right"><spring:message
			code="lbl.courttype" /><span class="mandatory"></span>:</label>
	<div class="col-sm-3 add-margin">
		<form:select path="courtMaster.courtType" data-first-option="false"
			cssClass="form-control" required="required" name="courtType"
			id="courtType">
			<form:option value="">
				<spring:message code="lbls.select" />
			</form:option>
			<form:options items="${courtTypeList}" itemValue="id" id="courtTypeDropdown" itemLabel="courtType" />
		</form:select>
		<form:errors path="courtMaster.courtType"
			cssClass="add-margin error-msg" />
	</div>
	<label class="col-sm-2 control-label text-right"><spring:message
			code="lbl.petitiontype" /><span class="mandatory"></span>:</label>
	<div class="col-sm-3 add-margin">
		<form:select path="petitionTypeMaster" id="petitionTypeMaster"
			data-first-option="false" cssClass="form-control" required="required">
			<form:option value="">
				<spring:message code="lbls.select" />
			</form:option>
			<!-- //Edited by me.. -->
			<form:options items="${petitiontypeList}" itemValue="id"
								itemLabel="petitionType" />
		</form:select>
		<form:errors path="petitionTypeMaster" cssClass="add-margin error-msg" />
	</div>
</div>
<div class="form-group">
	<label class="col-sm-3 control-label text-right"><spring:message
			code="lbl.court" /><span class="mandatory"></span>:</label>
	<div class="col-sm-3 add-margin">
		<form:select path="courtMaster" id="courtMaster"
			data-first-option="false" cssClass="form-control" required="required">
			<form:option value="">
				<spring:message code="lbls.select" />
			</form:option>
			<form:options items="${courtsList}" itemValue="id" id="courtDropdown"
				itemLabel="name" />
		</form:select>
		<form:errors path="courtMaster" cssClass="add-margin error-msg" />
	</div>	
</div>
<div class="form-group">
	<label class="col-sm-3 control-label text-right"><spring:message
			code="lbl.casenumber" /><span class="mandatory"></span>:</label>
	<div class="col-sm-3 add-margin text-left">
		<form:input class="form-control patternvalidation" data-pattern="alphanumeric" maxlength="40" id="caseNumber"
					required="required" path="caseNumber" style="width: 59%;display: inline;"/>
		<form:select path="wpYear" data-first-option="false" id="wpYear" cssClass="form-control"
					 style="width: 35%;display: inline;">
			<form:option value="">
				<spring:message code="lbls.select" />
			</form:option>
			<form:options items="${wPYearList}" />
		</form:select>
		<form:errors path="caseNumber" cssClass="add-margin error-msg" />
	</div>
	<c:if test="${mode != 'edit'}">
		<label class="col-sm-2 control-label text-right">
			<spring:message code="lbl.lcnumber" /><span class="mandatory"></span>:
		</label>
		<div class="col-sm-3 add-margin text-center">
			<form:input class="form-control patternvalidation" data-pattern="address" maxlength="50" id="lcNumber" path="lcNumber" required="required"/>
		</div>
		<form:errors path="lcNumber" cssClass="add-margin error-msg" />
	</c:if>
	<c:if test="${mode == 'edit'}">
		<label class="col-sm-2 control-label text-right">
			<spring:message code="lbl.lcnumber" /><span class="mandatory"></span>:
		</label>
		<div class="col-sm-3 add-margin text-center">
			<form:input class="form-control patternvalidation" data-pattern="address" maxlength="50" id="fileNumber" path="fileNumber" required="required"/>
		</div>
		<form:errors path="fileNumber" cssClass="add-margin error-msg" />
	</c:if>
</div>
<div class="form-group">
	<label class="col-sm-3 control-label text-right" id="persons"><spring:message
			code="lbl.reappealofcase" /> ?:</label>
	<div class="col-sm-3 add-margin">
		<form:checkbox id="reappealOfCase" path="isReappealOfCase" value="isReappealOfCase" />
		<form:errors path="isReappealOfCase" />
	</div>
	<c:if test="${isReappealCase}">
		<div id="previousCaseNo" style="display: block;" class="reappealcase">
	</c:if>
	<c:if test="${!isReappealCase}">
		<div id="previousCaseNo" style="display: none;" class="reappealcase">
	</c:if>
		<label class="col-sm-2 control-label text-right" id="persons">
			<spring:message code="lbl.previouscaseNumber" /><span class="mandatory"></span>:
		</label>
		<div class="col-sm-3 add-margin" id="personsdiv">
			<form:input class="form-control patternvalidation" data-pattern="alphanumeric" maxlength="40" 
						id="appealNum" path="appealNum" style="width: 59%;display: inline;"/>
			<form:select path="prevCaseYear" data-first-option="false" cssClass="form-control"
						 style="width: 35%;display: inline;" id="prevCaseYear" name="prevCaseYear">
				<form:option value="">
					<spring:message code="lbls.select" />
				</form:option>
				<form:options items="${wPYearList}" />
			</form:select>
			<form:errors path="appealNum" cssClass="add-margin error-msg" />
		</div>
	</div>
</div>
<c:if test="${isReappealCase}">
	<div class="form-group reappealcase" style="display: block;">
</c:if>
<c:if test="${!isReappealCase}">
	<div class="form-group reappealcase" style="display: none;">
</c:if>
	<label class="col-sm-3 control-label text-right">
		<spring:message code="lbl.reappl.courttype" /><span class="mandatory"></span>:
	</label>
	<div class="col-sm-3 add-margin">
		<form:select path="prevCourtType" data-first-option="false" cssClass="form-control" 
					 name="prevCourtType" id="prevCourtType">
			<form:option value="">
				<spring:message code="lbls.select" />
			</form:option>
			<form:options items="${courtTypeList}" itemValue="id" id="prevCourtTypeDropdown" itemLabel="courtType" />
		</form:select>
		<form:errors path="prevCourtType" cssClass="add-margin error-msg" />
	</div>
	<label class="col-sm-2 control-label text-right">
		<spring:message code="lbl.reappl.petitiontype" /><span class="mandatory"></span>:
	</label>
	<div class="col-sm-3 add-margin">
		<form:select path="prevPetitionType" id="prevPetitionType"
					 data-first-option="false" cssClass="form-control">
			<form:option value="">
				<spring:message code="lbls.select" />
			</form:option>
		</form:select>
		<form:errors path="prevPetitionType" cssClass="add-margin error-msg" />
	</div>
</div>
<div class="form-group">
	<label class="col-sm-3 control-label text-right" id="persons"><spring:message
			code="lbl.title" /><span class="mandatory"></span>:</label>
	<div class="col-sm-3 add-margin">
		<form:textarea class="form-control" path="caseTitle" id="caseTitle" name="caseTitle" />
		<form:errors path="caseTitle" cssClass="add-margin error-msg" />
	</div>
	<label class="col-sm-2 control-label text-right" id="persons"><spring:message
			code="lbl.prayer" /><span class="mandatory"></span>:</label>
	<div class="col-sm-3 add-margin" id="personsdiv">
		<form:textarea class="form-control" maxlength="10000" id="prayer" path="prayer" />
		<form:errors path="prayer" cssClass="add-margin error-msg" />
	</div>
</div>
<div class="form-group">
	<label class="col-sm-3 control-label text-left"><spring:message
			code="lbl.casedate" /><span class="mandatory"></span>:</label>
	<div class="col-sm-3 add-margin text-center">
		<form:input path="caseDate" class="form-control datepicker"
			title="Please enter a valid date" pattern="\d{1,2}/\d{1,2}/\d{4}"
			data-date-end-date="-1d" id="caseDate"
			data-inputmask="'mask': 'd/m/y'" required="required" />
		<form:errors path="caseDate" cssClass="add-margin error-msg" />
	</div>
</div>
<div class="form-group">
	<label class="col-sm-3 control-label text-right"><spring:message
			code="lbl.nodalofficer.department" />:</label>
	<div class="col-sm-3 add-margin">
		<form:input class="form-control patternvalidation" maxlength="100" id="nodalofficerdepartment"
			path="nodalOfficerDepartment" data-pattern="address" />
		<form:errors path="nodalOfficerDepartment" cssClass="add-margin error-msg" />
	</div>
	<label class="col-sm-2 control-label text-right" id="persons"><spring:message
			code="lbl.nodalofficer" /><span class="mandatory"></span>:</label>
	<div class="col-sm-3 add-margin">
		<form:input class="form-control patternvalidation" maxlength="100" id="nodalofficer" path="nodalOfficer" data-pattern="address" required="required"/>
		<form:errors path="nodalOfficer" cssClass="add-margin error-msg" />
	</div>	
</div>
<div class="form-group">
	<label class="col-sm-3 control-label text-right"><spring:message
			code="lbl.concerned.branch" />:</label>
	<div class="col-sm-3 add-margin">
		<form:select path="concernedBranch" data-first-option="false"
			id="concernedBranch" cssClass="form-control">
			<form:option value="">
				<spring:message code="lbls.select" />
			</form:option>
			<form:options items="${ConcernedBranchList}" itemValue="id" itemLabel="concernedBranch" />
		</form:select>
		<form:errors path="concernedBranch" cssClass="add-margin error-msg" />
	</div>
	<label class="col-sm-2 control-label text-right" id="persons">
		<spring:message code="lbl.concerned.branch.email" />:
	</label>
	<div class="col-sm-3 add-margin">
		<form:input path="concernedBranchEmail" class="form-control text-left patternvalidation"
					data-pattern="alphanumericwithspecialcharacters" maxlength="64" placeholder="abc@xyz.com" />
		<form:errors path="concernedBranchEmail" cssClass="error-msg" />
	</div>	
</div>
<div class="form-group">
	<label class="col-sm-3 control-label text-right"><spring:message
			code="lbl.casecatagory" /><span class="mandatory"></span>:</label>
	<div class="col-sm-3 add-margin">
		<form:select path="caseTypeMaster" data-first-option="false"
			id="caseTypeMaster" cssClass="form-control" required="required">
			<form:option value="">
				<spring:message code="lbls.select" />
			</form:option>
			<form:options items="${caseTypeList}" itemValue="id"
				id="courtTypeDropdown" itemLabel="caseType" />
		</form:select>
		<form:errors path="caseTypeMaster" cssClass="add-margin error-msg" />
	</div>
	<label class="col-sm-2 control-label text-right" id="persons"><spring:message
			code="lbl.estimatepreparedby" />:</label>
	<div class="col-sm-3 add-margin">
		<form:input class="form-control patternvalidation" maxlength="100" id="estimatepreparedby"
			path="estimatePreparedBy" data-pattern="address" />
		<form:errors path="estimatePreparedBy" cssClass="add-margin error-msg" />
	</div>
</div>
<div class="form-group">
<label class="col-sm-3 control-label text-right">Is Case Important ?</label>
<div class="col-sm-3 add-margin">


        <input type="radio" name="caseImportant" id="caseyes" class="caseimp" value="Yes" <c:if test="${legalCase.caseImportant=='Yes'}">checked="checked"</c:if>><span>YES</span>
      
        <input type="radio" name="caseImportant" id="caseno"  class="caseimp" value="No" <c:if test="${legalCase.caseImportant=='No'}">checked="checked"</c:if>><span>NO</span>
</div>
</div>
 <div class="form-group">
	<label class="col-sm-3 control-label text-right" id="persons" style="display:none;"><spring:message
			code="lbl.fieldbycarp" /> ?:</label>
	<div class="col-sm-3 add-margin" style="display:none;">
		<form:checkbox id="activeid" path="isFiledByCorporation"
			value="isFiledByCorporation" />
		<form:errors path="isFiledByCorporation" />
	</div>
</div> 

<script
	src="<cdn:url value='/resources/global/js/bootstrap/bootstrap-datepicker.js' context='/services/egi'/>"
	type="text/javascript"></script>