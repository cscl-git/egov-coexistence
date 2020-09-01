package org.egov.audit.model;

public class PostAuditResult {
	
	private String expendituretype;
	private String billtype;
	private String billnumber;
	private String billdate;
	private String billamount;
	private String passedamount;
	private String billstatus;
	private String sourcepath;
	private String ownerName;
	private boolean checked;
	private String voucherId;
	
	public String getExpendituretype() {
		return expendituretype;
	}
	public void setExpendituretype(String expendituretype) {
		this.expendituretype = expendituretype;
	}
	public String getBilltype() {
		return billtype;
	}
	public void setBilltype(String billtype) {
		this.billtype = billtype;
	}
	public String getBillnumber() {
		return billnumber;
	}
	public void setBillnumber(String billnumber) {
		this.billnumber = billnumber;
	}
	public String getBilldate() {
		return billdate;
	}
	public void setBilldate(String billdate) {
		this.billdate = billdate;
	}
	public String getBillamount() {
		return billamount;
	}
	public void setBillamount(String billamount) {
		this.billamount = billamount;
	}
	public String getPassedamount() {
		return passedamount;
	}
	public void setPassedamount(String passedamount) {
		this.passedamount = passedamount;
	}
	public String getBillstatus() {
		return billstatus;
	}
	public void setBillstatus(String billstatus) {
		this.billstatus = billstatus;
	}
	public String getSourcepath() {
		return sourcepath;
	}
	public void setSourcepath(String sourcepath) {
		this.sourcepath = sourcepath;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public boolean getChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public String getVoucherId() {
		return voucherId;
	}
	public void setVoucherId(String voucherId) {
		this.voucherId = voucherId;
	}
	
}
