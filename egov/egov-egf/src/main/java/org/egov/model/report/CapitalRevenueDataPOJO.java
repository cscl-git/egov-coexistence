package org.egov.model.report;

import java.math.BigDecimal;

public class CapitalRevenueDataPOJO {
	
	private String id;
	private String vouchernumber;
	private String voucherdate;
	private String department;
	private String glcode;
	private BigDecimal creditamount;
	private BigDecimal debitamount;
	private String deptcode;
	private String fcode;
	private String fname;
	private Long fundid;
	private BigDecimal totalSum;
	
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
	public BigDecimal getDebitamount() {
		return debitamount;
	}
	public void setDebitamount(BigDecimal debitamount) {
		this.debitamount = debitamount;
	}
	public String getDeptcode() {
		return deptcode;
	}
	public void setDeptcode(String deptcode) {
		this.deptcode = deptcode;
	}
	public String getFcode() {
		return fcode;
	}
	public void setFcode(String fcode) {
		this.fcode = fcode;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public Long getFundid() {
		return fundid;
	}
	public void setFundid(Long fundid) {
		this.fundid = fundid;
	}
	public BigDecimal getTotalSum() {
		return totalSum;
	}
	public void setTotalSum(BigDecimal totalSum) {
		this.totalSum = totalSum;
	}
	
}
