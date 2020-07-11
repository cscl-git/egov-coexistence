package org.egov.audit.model;

public class AuditBillDetails {
	private String billNumber;
	private Long billId;
	private String voucherNumber;
	private Long voucherId;
	private String paymentVoucherNumber;
	private Long paymentVoucherId;
	
	public String getBillNumber() {
		return billNumber;
	}
	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}
	public Long getBillId() {
		return billId;
	}
	public void setBillId(Long billId) {
		this.billId = billId;
	}
	public String getVoucherNumber() {
		return voucherNumber;
	}
	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}
	public Long getVoucherId() {
		return voucherId;
	}
	public void setVoucherId(Long voucherId) {
		this.voucherId = voucherId;
	}
	public String getPaymentVoucherNumber() {
		return paymentVoucherNumber;
	}
	public void setPaymentVoucherNumber(String paymentVoucherNumber) {
		this.paymentVoucherNumber = paymentVoucherNumber;
	}
	public Long getPaymentVoucherId() {
		return paymentVoucherId;
	}
	public void setPaymentVoucherId(Long paymentVoucherId) {
		this.paymentVoucherId = paymentVoucherId;
	}

}
