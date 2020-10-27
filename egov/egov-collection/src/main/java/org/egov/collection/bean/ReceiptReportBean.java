package org.egov.collection.bean;

import java.math.BigDecimal;

public class ReceiptReportBean {

	private String slNo;
	private String paramDate;
	private String receiptNo;
	private String collectedBy;
	private String payeeName;
	private String serviceType;
	private String modeOfPayment;
	private String particulars;
	private BigDecimal totalReceiptAmount;
	private String dateOfDeposite;
	private String remitanceNo;
	private String bankAccountNo;
	private BigDecimal depositAmount;
	public String getSlNo() {
		return slNo;
	}
	public void setSlNo(String slNo) {
		this.slNo = slNo;
	}
	public String getParamDate() {
		return paramDate;
	}
	public void setParamDate(String paramDate) {
		this.paramDate = paramDate;
	}
	public String getReceiptNo() {
		return receiptNo;
	}
	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
	public String getCollectedBy() {
		return collectedBy;
	}
	public void setCollectedBy(String collectedBy) {
		this.collectedBy = collectedBy;
	}
	public String getPayeeName() {
		return payeeName;
	}
	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getModeOfPayment() {
		return modeOfPayment;
	}
	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}
	public String getParticulars() {
		return particulars;
	}
	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}
	public BigDecimal getTotalReceiptAmount() {
		return totalReceiptAmount;
	}
	public void setTotalReceiptAmount(BigDecimal totalReceiptAmount) {
		this.totalReceiptAmount = totalReceiptAmount;
	}
	public String getDateOfDeposite() {
		return dateOfDeposite;
	}
	public void setDateOfDeposite(String dateOfDeposite) {
		this.dateOfDeposite = dateOfDeposite;
	}
	public String getRemitanceNo() {
		return remitanceNo;
	}
	public void setRemitanceNo(String remitanceNo) {
		this.remitanceNo = remitanceNo;
	}
	public String getBankAccountNo() {
		return bankAccountNo;
	}
	public void setBankAccountNo(String bankAccountNo) {
		this.bankAccountNo = bankAccountNo;
	}
	public BigDecimal getDepositAmount() {
		return depositAmount;
	}
	public void setDepositAmount(BigDecimal depositAmount) {
		this.depositAmount = depositAmount;
	}
	@Override
	public String toString() {
		return "ReceiptReportBean [slNo=" + slNo + ", paramDate=" + paramDate + ", receiptNo=" + receiptNo
				+ ", collectedBy=" + collectedBy + ", payeeName=" + payeeName + ", serviceType=" + serviceType
				+ ", modeOfPayment=" + modeOfPayment + ", particulars=" + particulars + ", totalReceiptAmount="
				+ totalReceiptAmount + ", dateOfDeposite=" + dateOfDeposite + ", remitanceNo=" + remitanceNo
				+ ", bankAccountNo=" + bankAccountNo + ", depositAmount=" + depositAmount + "]";
	}
	
	
	
}
