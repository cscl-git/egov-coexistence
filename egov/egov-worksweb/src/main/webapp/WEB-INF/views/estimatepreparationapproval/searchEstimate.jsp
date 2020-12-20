<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<script
        src="<cdn:url value='/resources/js/estimateworks.js?rnd=${app_release_no}' context='/services/works'/>"></script>

	<form:form name="search-estimate-form" role="form" method="post"
		action="workEstimateSearch" modelAttribute="workEstimateDetails"
	id="workEstimateDetails" class="form-horizontal form-groups-bordered"
	style="margin-top:-20px;">

	<div class="tab-content">
		<div class="tab-pane fade in active">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="form-group" style="padding: 50px 20px 0;">


<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.search.work.name" /></label>
					<div class="col-sm-3 add-margin">
						<form:textarea class="form-control" path="workName"
							maxlength="2000" />
							</div>
							
							
				<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.search.status" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="workStatusSearch" id="workStatusSearch"
							cssClass="form-control" cssErrorClass="form-control error">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
						<form:option value="Created">Created</form:option>
							<form:option value="Pending for Approval">Pending for Approval</form:option>
							<form:option value="AA Initiated">AA Initiated</form:option>
							<form:option value="AA Pending for Approval">AA Pending for Approval</form:option>
							<form:option value="TS Initiated">TS Initiated</form:option>
							<form:option value="TS Pending for Approval">TS Pending for Approval</form:option>
							<form:option value="Approved">Approved</form:option>
						</form:select>
					</div>			

					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.preparation.executing.division" /><span
						class="mandatory"></span></label>
					<div class="col-sm-3 add-margin">
						<form:select path="department" id="department" required="true"
							class="form-control">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
							<form:options items="${workEstimateDetails.departments}"
								itemValue="code" itemLabel="name" />
								</form:select>
							</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
							code="lbl.estimate.preparation.estimate.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="estimateNumber" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.works.wing" /></label>
					<div class="col-sm-3 add-margin">
								<form:select path="worksWing" id="worksWing"
									cssClass="form-control" cssErrorClass="form-control error">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:option value="Building & Roads">Building & Roads</form:option>
									<form:option value="Public Health">Public Health</form:option>
									<form:option value="Horticulture & Electrical">Horticulture & Electrical</form:option>
								</form:select>
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.estimate.from.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="fromDt" path="fromDt"
							class="form-control datepicker" data-date-end-date="0d"
							placeholder="DD/MM/YYYY" />
					</div>

					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.work.estimate.to.date" /></label>
					<div class="col-sm-3 add-margin">
						<form:input id="toDt" path="toDt" class="form-control datepicker"
							data-date-end-date="0d" placeholder="DD/MM/YYYY" />
					</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.work.location" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="workLocation" />
							</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.sector.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="sectorNumber" id="sectorNumber" multiple="multiple"
									cssClass="form-control" cssErrorClass="form-control error">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:option value="1">1 </form:option>
							<form:option value="2">2 </form:option>
							<form:option value="3">3 </form:option>
							<form:option value="4">4 </form:option>
							<form:option value="5">5 </form:option>
							<form:option value="6">6 </form:option>
							<form:option value="7">7 </form:option>
							<form:option value="8">8 </form:option>
							<form:option value="9">9 </form:option>
							<form:option value="10">10 </form:option>
							<form:option value="11">11 </form:option>
							<form:option value="12">12 </form:option>
							<form:option value="14">14 </form:option>
							<form:option value="15">15 </form:option>
							<form:option value="16">16 </form:option>
							<form:option value="17">17 </form:option>
							<form:option value="18">18 </form:option>
							<form:option value="19">19 </form:option>
							<form:option value="20">20 </form:option>
							<form:option value="21">21 </form:option>
							<form:option value="22">22 </form:option>
							<form:option value="23">23 </form:option>
							<form:option value="24">24 </form:option>
							<form:option value="25">25 </form:option>
							<form:option value="26">26 </form:option>
							<form:option value="26 (E)">26 (E) </form:option>
							<form:option value="27">27 </form:option>
							<form:option value="28">28 </form:option>
							<form:option value="29">29 </form:option>
							<form:option value="30">30 </form:option>
							<form:option value="31">31 </form:option>
							<form:option value="32">32 </form:option>
							<form:option value="33">33 </form:option>
							<form:option value="34">34 </form:option>
							<form:option value="35">35 </form:option>
							<form:option value="36">36 </form:option>
							<form:option value="37">37 </form:option>
							<form:option value="38">38 </form:option>
							<form:option value="38 West">38 West </form:option>
							<form:option value="39">39 </form:option>
							<form:option value="40">40 </form:option>
							<form:option value="41">41 </form:option>
							<form:option value="42">42 </form:option>
							<form:option value="43">43 </form:option>
							<form:option value="44">44 </form:option>
							<form:option value="45">45 </form:option>
							<form:option value="46">46 </form:option>
							<form:option value="47">47 </form:option>
							<form:option value="48">48 </form:option>
							<form:option value="49">49 </form:option>
							<form:option value="50">50 </form:option>
							<form:option value="51 (Nizam Pur Burail)">51 (Nizam Pur Burail) </form:option>
							<form:option value="51 (Colony number 5 Brick- Kilin)">51 (Colony number 5 Brick- Kilin) </form:option>
							<form:option value="51">51 </form:option>
							<form:option value="52">52 </form:option>
							<form:option value="53 (Nehru Colony)">53 (Nehru Colony) </form:option>
							<form:option value="54">54 </form:option>
							<form:option value="55 (Housing Board)">55 (Housing Board) </form:option>
							<form:option value="55 (Indira)">55 (Indira) </form:option>
							<form:option value="61">61 </form:option>
							<form:option value="63">63 </form:option>
							<form:option value="Bapudham Trangt Camp I Phase I)">Bapudham Trangt Camp I Phase I) </form:option>
							<form:option value="Bapudham Trangt Camp II Phase II)">Bapudham Trangt Camp II Phase II) </form:option>
							<form:option value="Bapudham Trangt Camp III Phase III)">Bapudham Trangt Camp III Phase III) </form:option>
							<form:option value="Sarangpur">Sarangpur </form:option>
							<form:option value="Dadu Majara">Dadu Majara </form:option>
							<form:option value="Kaimbala">Kaimbala </form:option>
							<form:option value="Kishangarh">Kishangarh </form:option>
							<form:option value="Malaya">Malaya </form:option>
							<form:option value="Khudda Lahora">Khudda Lahora </form:option>
							<form:option value="Khudda Jassu">Khudda Jassu </form:option>
							<form:option value="Khudda Alisher">Khudda Alisher </form:option>
							<form:option value="Dhanas">Dhanas </form:option>
							<form:option value="Palsora">Palsora </form:option>
							<form:option value="Badheri">Badheri </form:option>
							<form:option value="Baterla">Baterla </form:option>
							<form:option value="Attawa">Attawa </form:option>
							<form:option value="Faidan Burail">Faidan Burail </form:option>
							<form:option value="Char Taraf Burail">Char Taraf Burail </form:option>
							<form:option value="Kajheri Hallo Majra">Kajheri Hallo Majra </form:option>
							<form:option value="Bohlana">Bohlana </form:option>
							<form:option value="Raipur Khurd">Raipur Khurd </form:option>
							<form:option value="Raipur Kalan">Raipur Kalan </form:option>
							<form:option value="Makhan Majra">Makhan Majra </form:option>
							<form:option value="Mauli Jagran">Mauli Jagran </form:option>
							<form:option value="Daria">Daria </form:option>
							<form:option value="Mani Majara">Mani Majara </form:option>
							<form:option value="Indusrial Area Phase I">Indusrial Area Phase I </form:option>
							<form:option value="Indusrial Area Phase II">Indusrial Area Phase II </form:option>
								</form:select>
							</div>
					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.ward.number" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="wardNumber" id="wardNumber"  multiple="multiple"
									cssClass="form-control" cssErrorClass="form-control error">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<form:option value="Ward 1">Ward 1 </form:option>
									<form:option value="Ward 2">Ward 2</form:option>
									<form:option value="Ward 3">Ward 3</form:option>
								</form:select>
							</div>
					
					
					<label class="col-sm-3 control-label text-left-audit"><spring:message
					code="lbl.estimate.preparation.fund.source" /></label>
					<div class="col-sm-3 add-margin">
				<form:select path="fundSource" id="fundSource"
									cssClass="form-control" cssErrorClass="form-control error">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
								<form:option value="1">Municipal Fund</form:option>
								<form:option value="2">Earmarked Fund</form:option>
								</form:select>
							</div>
					
					
					<label class="col-sm-3 control-label text-left-audit"><spring:message
									code="lbl.estimate.preparation.estimate.amount" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control" id="estimatedCost" 
									path="estimateAmount" />
							</div>
					
							</div>
						</div>
					</div>

		<div class="buttonbottom" align="center">
			<input type="submit" id="workEstimateSearch" class="btn btn-primary"
						name="workEstimateSearch" code="lbl.search.work.estimate"
						value="Search" />
				</div>

		<br> <br> <br>
		<div class="tab-pane fade in active" id="resultheader">
			<h3>Search Result</h3>
			<div class="panel panel-primary" data-collapsed="0">
				<div style="padding: 0 15px;">
					<table class="table table-bordered" id="table">
						<thead>
							<tr>
								<th><spring:message
										code="lbl.estimate.preparation.name.work" /></th>
								
								<th><spring:message
										code="lbl.estimate.preparation.estimate.number" /></th>
								<th><spring:message
										code="lbl.estimate.preparation.estimate.date" /></th>
								<th><spring:message
										code="lbl.estimate.preparation.estimate.amount" /></th>
								<th><spring:message
										code="lbl.estimate.preparation.work.status" /></th>
								<th><spring:message
										code="lbl.estimate.preparation.work.pending" /></th>

							</tr>
						</thead>
						`
						<c:if
							test="${workEstimateDetails.estimateList != null &&  !workEstimateDetails.estimateList.isEmpty()}">
							<tbody>
								<c:forEach items="${workEstimateDetails.estimateList}"
									var="result" varStatus="status">
									<tr>
										<td><form:hidden
												path="estimateList[${status.index}].workName"
												id="estimateList[${status.index}].workName" />
											${result.workName }</td>
										
										<td><form:hidden
												path="estimateList[${status.index}].estimateNumber"
												id="estimateList[${status.index}].estimateNumber" />
											<a href="#" onclick="openEstimate('${result.id}')">${result.estimateNumber }</a></td>
										<td><form:hidden
												path="estimateList[${status.index}].estimateDt"
												id="estimateList[${status.index}].estimateDt" />
											${result.estimateDt }</td>
										<td><form:hidden
												path="estimateList[${status.index}].estimateAmount"
												id="estimateList[${status.index}].estimateAmount" />
											${result.estimateAmount }</td>
										<td><form:hidden
												path="estimateList[${status.index}].statusDescription"
												id="estimateList[${status.index}].statusDescription" />
											${result.statusDescription }</td>
									<td><form:hidden
												path="estimateList[${status.index}].pendingWith"
												id="estimateList[${status.index}].pendingWith" />
											${result.pendingWith }</td>
									</tr>
								</c:forEach>
							<tbody>
						</c:if>
						<c:if
							test="${workEstimateDetails.estimateList == null ||  workEstimateDetails.estimateList.isEmpty()}">
					No records found
					</c:if>
					</table>
				</div>

			</div>
		</div>
	</div>

	</form:form>



