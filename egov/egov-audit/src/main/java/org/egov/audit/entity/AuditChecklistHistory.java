package org.egov.audit.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.egov.infra.persistence.entity.AbstractPersistable;

@Entity
@Table(name = "AUDIT_CHECKLIST_HISTORY")
@SequenceGenerator(name = AuditChecklistHistory.SEQ_AUDIT_CHECKLIST_HISTORY, sequenceName = AuditChecklistHistory.SEQ_AUDIT_CHECKLIST_HISTORY, allocationSize = 1)
public class AuditChecklistHistory extends AbstractPersistable<Integer> implements java.io.Serializable{

	public static final String SEQ_AUDIT_CHECKLIST_HISTORY = "SEQ_AUDIT_CHECKLIST_HISTORY";
	
	@Id
    @GeneratedValue(generator = SEQ_AUDIT_CHECKLIST_HISTORY, strategy = GenerationType.SEQUENCE)
    private Integer id;
	
	@OneToOne
    @JoinColumn(name = "checklist_id")
    private AuditCheckList auditCheckList;
	
	private String checklist_description;
	
	private String user_comments;
    
    private String auditor_comments;
    private String severity;
    
    private String status;
    @ManyToOne
    @JoinColumn(name = "audit_id")
    private AuditDetails auditDetails;
	
	@Override
	public Integer getId() {
		return id;
	}

	@Override
	protected void setId(Integer id) {
		this.id = id;
		
	}

	public AuditCheckList getAuditCheckList() {
		return auditCheckList;
	}

	public void setAuditCheckList(AuditCheckList auditCheckList) {
		this.auditCheckList = auditCheckList;
	}

	public String getChecklist_description() {
		return checklist_description;
	}

	public void setChecklist_description(String checklist_description) {
		this.checklist_description = checklist_description;
	}

	public String getUser_comments() {
		return user_comments;
	}

	public void setUser_comments(String user_comments) {
		this.user_comments = user_comments;
	}

	public String getAuditor_comments() {
		return auditor_comments;
	}

	public void setAuditor_comments(String auditor_comments) {
		this.auditor_comments = auditor_comments;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public AuditDetails getAuditDetails() {
		return auditDetails;
	}

	public void setAuditDetails(AuditDetails auditDetails) {
		this.auditDetails = auditDetails;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

}
