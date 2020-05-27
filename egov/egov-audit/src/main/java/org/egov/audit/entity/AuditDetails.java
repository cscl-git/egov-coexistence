package org.egov.audit.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.egov.commons.EgwStatus;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.model.bills.DocumentUpload;
import org.egov.model.bills.EgBillregister;

@Entity
@Table(name = "AUDIT_DETAILS")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(name = AuditDetails.SEQ_AUDIT_DETAILS, sequenceName = AuditDetails.SEQ_AUDIT_DETAILS, allocationSize = 1)
public class AuditDetails extends StateAware implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5768296515813072014L;
	public static final String SEQ_AUDIT_DETAILS = "SEQ_AUDIT_DETAILS";
	
	@Id
    @GeneratedValue(generator = SEQ_AUDIT_DETAILS, strategy = GenerationType.SEQUENCE)
    private Long id;
	private String type;
	private Date audit_sch_date;
	private Date audit_comp_date;
	private String audit_no;
	private Long leadAuditiorEmpId;
	private String notes;
	@Transient
    private List<DocumentUpload> documentDetail = new ArrayList<>();
	@ManyToOne
    @JoinColumn(name = "statusid")
    private EgwStatus status;
	@ManyToOne
    @JoinColumn(name = "billid")
    private EgBillregister egBillregister;
	
	@Override
	public String getStateDetails() {

		 return getState().getComments().isEmpty() ? audit_no : audit_no + "-" + getState().getComments();
	}

	@Override
	public Long getId() {
		
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id=id;
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getAudit_sch_date() {
		return audit_sch_date;
	}

	public void setAudit_sch_date(Date audit_sch_date) {
		this.audit_sch_date = audit_sch_date;
	}

	public Date getAudit_comp_date() {
		return audit_comp_date;
	}

	public void setAudit_comp_date(Date audit_comp_date) {
		this.audit_comp_date = audit_comp_date;
	}

	public String getAudit_no() {
		return audit_no;
	}

	public void setAudit_no(String audit_no) {
		this.audit_no = audit_no;
	}

	public Long getLeadAuditiorEmpId() {
		return leadAuditiorEmpId;
	}

	public void setLeadAuditiorEmpId(Long leadAuditiorEmpId) {
		this.leadAuditiorEmpId = leadAuditiorEmpId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public List<DocumentUpload> getDocumentDetail() {
		return documentDetail;
	}

	public void setDocumentDetail(List<DocumentUpload> documentDetail) {
		this.documentDetail = documentDetail;
	}

	public EgwStatus getStatus() {
		return status;
	}

	public void setStatus(EgwStatus status) {
		this.status = status;
	}

	public EgBillregister getEgBillregister() {
		return egBillregister;
	}

	public void setEgBillregister(EgBillregister egBillregister) {
		this.egBillregister = egBillregister;
	}

	

}
