package org.egov.model.deduction;

import java.math.BigDecimal;

public class DeductionReportBean {
	
	private int slNo;
	private String recoveryCode;
	private String voucherNo;
	private String division;
	private String nameOfAgency;
	private String workDone;
	private BigDecimal amount;
	public int getSlNo() {
		return slNo;
	}
	public void setSlNo(int slNo) {
		this.slNo = slNo;
	}
	public String getRecoveryCode() {
		return recoveryCode;
	}
	public void setRecoveryCode(String recoveryCode) {
		this.recoveryCode = recoveryCode;
	}
	public String getVoucherNo() {
		return voucherNo;
	}
	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String divison) {
		this.division = divison;
	}
	public String getNameOfAgency() {
		return nameOfAgency;
	}
	public void setNameOfAgency(String nameOfAgency) {
		this.nameOfAgency = nameOfAgency;
	}
	public String getWorkDone() {
		return workDone;
	}
	public void setWorkDone(String workDone) {
		this.workDone = workDone;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
