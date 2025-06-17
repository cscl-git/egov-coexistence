package org.egov.egf.model;

import java.math.BigDecimal;
import java.util.Date;

public class BillRegisterNewReportBean {
	
	private String billType;
	private String partyName;
	private String departmentName;
	private String functionName;
	private String scheme;
	private BigDecimal budgetBillAmount;
	private String debitSideAccountHead;
	private String creditSideAccountHead;
	private String billNo;
	private String billDate;
	private String voucherNo;
	private String voucherDate;
	private String vouchertype;
	private String vouchername;
	private BigDecimal voucherAmount;
	private String pexType;
	private String pexNo;
	private String pexDate;
	private BigDecimal pexAmount;
	private String pexaccountnumber;
	private String status;
	private Integer debitAmount;
	private Integer creditAmount;
	private BigDecimal paidAmount;
	private String paymentvoucherNo;
	private String paymentvoucherDate;
	
	public String getBillType() {
		return billType;
	}
	public void setBillType(String billType) {
		this.billType = billType;
	}
	public String getPartyName() {
		return partyName;
	}
	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getScheme() {
		return scheme;
	}
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	public BigDecimal getBudgetBillAmount() {
		return budgetBillAmount;
	}
	public void setBudgetBillAmount(BigDecimal budgetBillAmount) {
		this.budgetBillAmount = budgetBillAmount;
	}
	public String getDebitSideAccountHead() {
		return debitSideAccountHead;
	}
	public void setDebitSideAccountHead(String debitSideAccountHead) {
		this.debitSideAccountHead = debitSideAccountHead;
	}
	public String getCreditSideAccountHead() {
		return creditSideAccountHead;
	}
	public void setCreditSideAccountHead(String creditSideAccountHead) {
		this.creditSideAccountHead = creditSideAccountHead;
	}
	public String getBillNo() {
		return billNo;
	}
	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}
	
	public String getBillDate() {
		return billDate;
	}
	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}
	public String getVoucherNo() {
		return voucherNo;
	}
	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}
	public String getVoucherDate() {
		return voucherDate;
	}
	public void setVoucherDate(String voucherDate) {
		this.voucherDate = voucherDate;
	}	
	public String getVouchertype() {
		return vouchertype;
	}
	public void setVouchertype(String vouchertype) {
		this.vouchertype = vouchertype;
	}
	public String getVouchername() {
		return vouchername;
	}
	public void setVouchername(String vouchername) {
		this.vouchername = vouchername;
	}
	public BigDecimal getVoucherAmount() {
		return voucherAmount;
	}
	public void setVoucherAmount(BigDecimal voucherAmount) {
		this.voucherAmount = voucherAmount;
	}
	public String getPexType() {
		return pexType;
	}
	public void setPexType(String pexType) {
		this.pexType = pexType;
	}
	public String getPexNo() {
		return pexNo;
	}
	public void setPexNo(String pexNo) {
		this.pexNo = pexNo;
	}	
	public String getPexDate() {
		return pexDate;
	}
	public void setPexDate(String pexDate) {
		this.pexDate = pexDate;
	}
	public BigDecimal getPexAmount() {
		return pexAmount;
	}
	public void setPexAmount(BigDecimal pexAmount) {
		this.pexAmount = pexAmount;
	}	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPexaccountnumber() {
		return pexaccountnumber;
	}
	public void setPexaccountnumber(String pexaccountnumber) {
		this.pexaccountnumber = pexaccountnumber;
	}
	public Integer getDebitAmount() {
		return debitAmount;
	}
	public void setDebitAmount(Integer debitAmount) {
		this.debitAmount = debitAmount;
	}
	public Integer getCreditAmount() {
		return creditAmount;
	}
	public void setCreditAmount(Integer creditAmount) {
		this.creditAmount = creditAmount;
	}
	public BigDecimal getPaidAmount() {
		return paidAmount;
	}
	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}
	public String getPaymentvoucherNo() {
		return paymentvoucherNo;
	}
	public void setPaymentvoucherNo(String paymentvoucherNo) {
		this.paymentvoucherNo = paymentvoucherNo;
	}
	public String getPaymentvoucherDate() {
		return paymentvoucherDate;
	}
	public void setPaymentvoucherDate(String paymentvoucherDate) {
		this.paymentvoucherDate = paymentvoucherDate;
	}
}
