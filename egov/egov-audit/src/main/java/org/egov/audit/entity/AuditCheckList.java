package org.egov.audit.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "AUDIT_CHECKLIST")
@SequenceGenerator(name = AuditCheckList.SEQ_AUDIT_CHECKLIST, sequenceName = AuditCheckList.SEQ_AUDIT_CHECKLIST, allocationSize = 1)
public class AuditCheckList extends AbstractPersistable<Integer> implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1396632085994354439L;

	public static final String SEQ_AUDIT_CHECKLIST = "SEQ_AUDIT_CHECKLIST";

    @Id
    @GeneratedValue(generator = SEQ_AUDIT_CHECKLIST, strategy = GenerationType.SEQUENCE)
    private Integer id;
    
    private String checklist_description;
    
    private String user_comments;
    
    private String auditor_comments;
    
    private String status;
    
    private String severity;
    
    @ManyToOne
    @JoinColumn(name = "audit_id")
    private AuditDetails auditDetails;
    
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "auditCheckList", targetEntity = AuditChecklistHistory.class)
	private AuditChecklistHistory checkList_history;
    

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	protected void setId(Integer id) {
		this.id = id;
		
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

	public AuditChecklistHistory getCheckList_history() {
		return checkList_history;
	}

	public void setCheckList_history(AuditChecklistHistory checkList_history) {
		this.checkList_history = checkList_history;
	}

}
