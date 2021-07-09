package org.egov.model.report;

import java.math.BigDecimal;

public class DedicatedExpense {
	
	private String id;
	private String vouchernumber;
	private String voucherdate;
	private String department;
	private String glcode;
	private BigDecimal creditamount;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getVouchernumber() {
		return vouchernumber;
	}
	public void setVouchernumber(String vouchernumber) {
		this.vouchernumber = vouchernumber;
	}
	public String getVoucherdate() {
		return voucherdate;
	}
	public void setVoucherdate(String voucherdate) {
		this.voucherdate = voucherdate;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getGlcode() {
		return glcode;
	}
	public void setGlcode(String glcode) {
		this.glcode = glcode;
	}
	public BigDecimal getCreditamount() {
		return creditamount;
	}
	public void setCreditamount(BigDecimal creditamount) {
		this.creditamount = creditamount;
	}
	
	
	
	

}
