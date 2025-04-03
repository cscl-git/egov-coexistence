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


<%@ include file="/includes/taglibs.jsp"%>
<%@ page language="java"%>
<script src="<cdn:url value='/resources/global/js/egov/patternvalidation.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
<s:if test="hasActionErrors() || hasFieldErrors()">
    <div style="color: red">
        <s:actionerror />
        <s:fielderror />
    </div>
</s:if>
<s:else>
<div class="panel-heading custom_form_panel_heading">
	<div class="panel-title">Reconciled/Unreconciled Items </div>
</div>
<div class="col-md-12 form-group report-table-container">
	<table class="table table-bordered table-hover multiheadertbl"
		id="resultTable">
		<thead>
			<tr>
				<th><s:text name="lbl.voucher.number"/></th>
				<th><s:text name="lbl.cheque.number"/></th>
				<th><s:text name="lbl.cheque.date"/></th>
				<th><s:text name="lbl.type"/></th>
				<th><s:text name="lbl.instrument.type"/></th>
				<th><s:text name="lbl.amount"/></th>
				<th><s:text name="lbl.reconciliation.date"/></th>
				<th><s:text name="lbl.reconciliation.comment"/></th>
			</tr>
		</thead>
		<s:if test="%{unReconciledCheques.size>0}">
			<s:iterator var="vh" value="unReconciledCheques" status="status">
				<tr>
					<input type="hidden"
						name="instrumentHeaders[<s:property value="#status.index"/>]"
						value='<s:property value="ihId"/>' />
					<%-- <td style="text-align: left"><s:property value="voucherNumber" /></td> --%>
					<td style="text-align: left"><a href="#"	onclick="openVoucher('<s:property value="vhId" />','<s:property value="voucherNumber" />');">
					<s:property	value="voucherNumber" /></td>
					<td style="text-align: left"><s:property value="chequeNumber" /></td>
					<td><s:property value="chequeDate" /></td>
					<td><s:property value="type" /></td>
					<td  style="text-align: center;"><s:property value="instrumentType" /></td>
					<td style="text-align: right"><s:property value="chequeAmount" /></td>
					<td>
					<%-- <input type="text" value="<s:property value="reconciledOn" />"/>
					
					<s:if test="#status.reconciledOn != null">
					<s:property value="reconciledOn" />
					</s:if>
					<s:else> --%>
					<input type="text"
						id="reconDates<s:property value="#status.index"/>"
						name="reconDates[<s:property value="#status.index"/>]"
						class="form-control datepicker" data-inputmask="'mask': 'd/m/y'" value="<s:property value="reconciledOn" />"/>
					<%-- </s:else> --%>
					</td>
					<%-- <td style="text-align: right"><s:property value="reconciledComment" /></td> --%>
					<td>
					<%-- <input type="text"
						id="reconComments<s:property value="#status.index"/>"
						name="reconComments[<s:property value="#status.index"/>]"
						class="form-control" value="<s:property value="reconciledComment" />"/> --%>
						
						<textarea cols="30" rows="2" id="reconComments<s:property value="#status.index"/>" 
							name="reconComments[<s:property value="#status.index"/>]" value="<s:property value="reconciledComment" />" class="patternvalidation" data-pattern="alphanumericwithspace"><s:property value="reconciledComment"/></textarea>
					</td>
				</tr>
			</s:iterator>
		</s:if>
		<s:else>
			<tr>
				<td colspan="8" style="text-align: center"><s:text name="msg.no.record.found"/></td>
			</tr>
		</s:else>
	</table>
</div>
</s:else>
<s:if test="%{unReconciledCheques.size>0}">
	<div class="buttonbottom" id="reconcileDiv" style="display: none">
		<table>
			<tr>
				<td><input type="button" class="buttonsubmit" value='<s:text name="lbl.reconcile.button"/>'
					name="Reconcile" method="reconcile"
					onclick="return validateReconcile();" /></td>
				<td><input type="button" value="<s:text name='lbl.close'/>"
					onclick="javascript:window.close()" class="buttonsubmit" /></td>
			</tr>
		</table>
	</div>
</s:if>





