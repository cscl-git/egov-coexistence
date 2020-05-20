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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<div class="row">
	<div class="col-md-12">
		<div class="panel panel-primary" data-collapsed="0">
			<div class="panel-heading">
				<div class="panel-title">
					<spring:message code="lbl.agenda.details" />
				</div>
			</div>
			<div class="panel-body">
				<table class="table table-bordered">
					<thead>
						<tr>
							<th align="center"><spring:message code="lbl.serial.no" /></th>
							<th width="7%"><spring:message code="lbl.agenda.number" /></th>
							<th><spring:message code="lbl.gistofpreamble" /></th>
							<%-- <th width="9%"><spring:message code="lbl.preamble.number" /></th> --%>
							<th width="14%"><spring:message code="lbl.department" /></th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<c:choose>
								<c:when test="${!councilMeeting.meetingMOMs.isEmpty()}">
									<c:forEach items="${councilMeeting.meetingMOMs}" var="mom"
										varStatus="counter">
										<tr>
											<td align="center">
												<%-- 	<input type="hidden" value="${mom.id}" id="meetingMOMs${counter.index}" name="meetingMOMs[${counter.index}].id" />
								<input type="hidden" value="${mom.preamble}" id="meetingMOMspreamble${counter.index}" name="meetingMOMs[${counter.index}].preamble" />
									<input type="hidden" value="${mom.agenda}" id="meetingMOMsagenda${counter.index}" name="meetingMOMs[${counter.index}].agenda" />
						 --%> <%-- 		<form:hidden path="meetingMOMs[${counter.index}]" id="meetingMOMs[${counter.index}].id" value="${mom.id}" />
							 --%> <form:hidden path="meetingMOMs[${counter.index}].preamble"
													id="meetingMOMspreamble${counter.index}"
													value="${mom.preamble.id}" /> <form:hidden
													path="meetingMOMs[${counter.index}].agenda"
													id="meetingMOMsagenda${counter.index}"
													value="${mom.agenda.id}" /> <form:hidden
													path="meetingMOMs[${counter.index}].itemNumber"
													id="meetingMOMsitemNumber${counter.index}"
													value="${mom.itemNumber}" /> <%-- 			<form:hidden path="meetingMOMs[${counter.index}].meeting.id" id="meetingMOMmeeting${counter.index}" value="${mom.meeting.id}" />
						 --%> ${counter.count}
											</td>
											<td class="text-center"><c:out
													value="${mom.agenda.agendaNumber}" /></td>
											<td><span class="more"><c:out
														value="${mom.preamble.gistOfPreamble}" /></span></td>
											<%-- <td><c:out value="${mom.preamble.preambleNumber}" /></td> --%>
											<td><c:out value="${mom.preamble.department}" /></td>
										</tr>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<div class="col-md-3 col-xs-6 add-margin">
										<spring:message code="lbl.noAgenda.Detail" />
									</div>
								</c:otherwise>
							</c:choose>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
