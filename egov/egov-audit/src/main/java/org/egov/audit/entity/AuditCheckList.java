package org.egov.audit.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.egov.infra.persistence.entity.AbstractAuditable;

@Entity
@Table(name = "AUDIT_CHECKLIST")
@SequenceGenerator(name = AuditCheckList.SEQ_AUDIT_CHECKLIST, sequenceName = AuditCheckList.SEQ_AUDIT_CHECKLIST, allocationSize = 1)
public class AuditCheckList extends AbstractAuditable implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1396632085994354439L;

	public static final String SEQ_AUDIT_CHECKLIST = "SEQ_AUDIT_CHECKLIST";

    @Id
    @GeneratedValue(generator = SEQ_AUDIT_CHECKLIST, strategy = GenerationType.SEQUENCE)
    private Long id;
    
    private String checklist_description;
    
    private String user_comments;
    
    private String auditor_comments;
    
    private String status;
    
    private String severity;
    
    private Date checklist_date;
    
    @ManyToOne
    @JoinColumn(name = "audit_id")
    private AuditDetails auditDetails;
    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "auditCheckList", targetEntity = AuditChecklistHistory.class)
	private List<AuditChecklistHistory> checkList_history;
    
    @Transient
    private String checkListId;
    

    public AuditCheckList() {}


	public AuditCheckList(AuditCheckList auditCheckList) {
		super();
		this.id = auditCheckList.id;
		this.checklist_description = auditCheckList.checklist_description;
		this.user_comments = auditCheckList.user_comments;
		this.auditor_comments = auditCheckList.auditor_comments;
		this.status = auditCheckList.status;
		this.severity = auditCheckList.severity;
		this.checklist_date = auditCheckList.checklist_date;
		this.checkListId = auditCheckList.checkListId;
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


	@Override
	public Long getId() {
		return id;
	}

	@Override
	protected void setId(Long id) {
		this.id=id;
		
	}

	public List<AuditChecklistHistory> getCheckList_history() {
		return checkList_history;
	}

	public void setCheckList_history(List<AuditChecklistHistory> checkList_history) {
		this.checkList_history = checkList_history;
	}

	public String getCheckListId() {
		return checkListId;
	}

	public void setCheckListId(String checkListId) {
		this.checkListId = checkListId;
	}

	public Date getChecklist_date() {
		return checklist_date;
	}

	public void setChecklist_date(Date checklist_date) {
		this.checklist_date = checklist_date;
	}

}
