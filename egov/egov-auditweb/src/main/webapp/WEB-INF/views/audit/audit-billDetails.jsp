<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading custom_form_panel_heading">
		<div class="panel-title">
			<spring:message code="lbl.billDetails" text="Bill Details"/>
		</div>
	</div>
	
	<div style="padding: 0 15px;">
		<table class="table table-bordered" id="tblbilldetails">
			<thead>
				<tr>
					<th><spring:message code="lbl.serial" text="Sl.No."/></th>
					<th><spring:message code="lbl.billNumber" text="Bill Number"/></th>
					<th><spring:message code="lbl.voucher" text="Bill Voucher Number"/></th>
					<th><spring:message code="lbl.payment.voucher" text="Payment Voucher Number"/></th>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${auditDetail.auditBillDetails}" var="audit" varStatus="status">
			<tr>
			<td>
			${status.index+1}
			</td>
			<td>
			<a href="#" onclick="return openSource('/services/EGF/expensebill/view/'+'${audit.billId}');" >${audit.billNumber }</a>
			</td>
			<td>
			<a href="#" onclick="return openVoucher('${audit.voucherId}');" >${audit.voucherNumber }</a>
			</td>
			<td>
			<a href="#" onclick="return openVoucher('${audit.paymentVoucherId}');" >${audit.paymentVoucherNumber }</a>
			</td>
			</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>
</div>