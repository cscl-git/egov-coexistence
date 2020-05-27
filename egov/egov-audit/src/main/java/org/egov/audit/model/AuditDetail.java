package org.egov.audit.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.egov.model.bills.DocumentUpload;

public class AuditDetail {
	
	private Date auditScheduledDate;
	private Date auditCompletedDate;
	private String leadAuditorName;
	private Long leadAuditorEmpNo;
	private String naration;
	private String auditNumber;
	private String auditType;
	private List<DocumentUpload> documentDetail = new ArrayList<>();
	
	private String notes;
	public Date getAuditScheduledDate() {
		return auditScheduledDate;
	}
	public void setAuditScheduledDate(Date auditScheduledDate) {
		this.auditScheduledDate = auditScheduledDate;
	}
	public Date getAuditCompletedDate() {
		return auditCompletedDate;
	}
	public void setAuditCompletedDate(Date auditCompletedDate) {
		this.auditCompletedDate = auditCompletedDate;
	}
	public String getLeadAuditorName() {
		return leadAuditorName;
	}
	public void setLeadAuditorName(String leadAuditorName) {
		this.leadAuditorName = leadAuditorName;
	}
	public Long getLeadAuditorEmpNo() {
		return leadAuditorEmpNo;
	}
	public void setLeadAuditorEmpNo(Long leadAuditorEmpNo) {
		this.leadAuditorEmpNo = leadAuditorEmpNo;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getNaration() {
		return naration;
	}
	public void setNaration(String naration) {
		this.naration = naration;
	}
	public String getAuditNumber() {
		return auditNumber;
	}
	public void setAuditNumber(String auditNumber) {
		this.auditNumber = auditNumber;
	}
	public String getAuditType() {
		return auditType;
	}
	public void setAuditType(String auditType) {
		this.auditType = auditType;
	}
	public List<DocumentUpload> getDocumentDetail() {
		return documentDetail;
	}
	public void setDocumentDetail(List<DocumentUpload> documentDetail) {
		this.documentDetail = documentDetail;
	}

}
