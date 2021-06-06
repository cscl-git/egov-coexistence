package org.egov.audit.model;

import java.util.Date;
import java.util.List;

import org.egov.audit.entity.AuditCheckList;

public class AuditRestDataPOJO {
	private Long Id;
	private String audit_type;
	private String audit_status;
	private Date audit_schedule_date;
	private String statusDescription;
	private List<AuditCheckList> auditCheckList;
	private String state;
	private String audit_no;
	private Date audit_comp_date;
	private Integer passUnderobjection;
	private String billid;
	private String department;
	private String auditor_name;
	private String rsa_name;
	
	
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public String getAudit_type() {
		return audit_type;
	}
	public void setAudit_type(String audit_type) {
		this.audit_type = audit_type;
	}
	public String getAudit_status() {
		return audit_status;
	}
	public void setAudit_status(String audit_status) {
		this.audit_status = audit_status;
	}
	public Date getAudit_schedule_date() {
		return audit_schedule_date;
	}
	public void setAudit_schedule_date(Date audit_schedule_date) {
		this.audit_schedule_date = audit_schedule_date;
	}
	
	public String getStatusDescription() {
		return statusDescription;
	}
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}
	public List<AuditCheckList> getAuditCheckList() {
		return auditCheckList;
	}
	public void setAuditCheckList(List<AuditCheckList> auditCheckList) {
		this.auditCheckList = auditCheckList;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getAudit_no() {
		return audit_no;
	}
	public void setAudit_no(String audit_no) {
		this.audit_no = audit_no;
	}
	public Date getAudit_comp_date() {
		return audit_comp_date;
	}
	public void setAudit_comp_date(Date audit_comp_date) {
		this.audit_comp_date = audit_comp_date;
	}
	public Integer getPassUnderobjection() {
		return passUnderobjection;
	}
	public void setPassUnderobjection(Integer passUnderobjection) {
		this.passUnderobjection = passUnderobjection;
	}
	public String getBillid() {
		return billid;
	}
	public void setBillid(String billid) {
		this.billid = billid;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	
	public String getAuditor_name() {
		return auditor_name;
	}
	
	public void setAuditor_name(String auditor_name) {
		this.auditor_name = auditor_name;
	}

	public String getRsa_name() {
		return rsa_name;
	}

	public void setRsa_name(String rsa_name) {
		this.rsa_name = rsa_name;
	}
}
