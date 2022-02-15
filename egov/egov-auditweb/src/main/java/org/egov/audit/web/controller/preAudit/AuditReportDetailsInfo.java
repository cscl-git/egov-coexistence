package org.egov.audit.web.controller.preAudit;

public class AuditReportDetailsInfo {

	private int slNo;
	private String auditType;
	private String auditNumber;
	private String billNumber;
	private String auditScheduleDate;
	private String status;
	private String pendingWith;
	public int getSlNo() {
		return slNo;
	}
	public void setSlNo(int slNo) {
		this.slNo = slNo;
	}
	public String getAuditType() {
		return auditType;
	}
	public void setAuditType(String auditType) {
		this.auditType = auditType;
	}
	public String getAuditNumber() {
		return auditNumber;
	}
	public void setAuditNumber(String auditNumber) {
		this.auditNumber = auditNumber;
	}
	public String getBillNumber() {
		return billNumber;
	}
	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}
	public String getAuditScheduleDate() {
		return auditScheduleDate;
	}
	public void setAuditScheduleDate(String auditScheduleDate) {
		this.auditScheduleDate = auditScheduleDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPendingWith() {
		return pendingWith;
	}
	public void setPendingWith(String pendingWith) {
		this.pendingWith = pendingWith;
	}

}
