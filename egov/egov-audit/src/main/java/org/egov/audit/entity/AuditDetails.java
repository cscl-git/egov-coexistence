package org.egov.audit.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.egov.commons.EgwStatus;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.model.bills.DocumentUpload;
import org.egov.model.bills.EgBillregister;
import org.egov.model.bills.EgBillregistermis;

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
	private String auditno;
	private Long lead_auditor;
	private String notes;
	@Transient
    private List<DocumentUpload> documentDetail = new ArrayList<>();
	@ManyToOne
    @JoinColumn(name = "status_id")
    private EgwStatus status;
	@ManyToOne
    @JoinColumn(name = "bill_id")
    private EgBillregister egBillregister;
	
	private Integer passUnderobjection;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "auditDetails", targetEntity = AuditCheckList.class)
	private List<AuditCheckList> checkList=new ArrayList<AuditCheckList>();
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "auditDetails", targetEntity = AuditPostBillMpng.class)
	private List<AuditPostBillMpng> postBillMpng=new ArrayList<AuditPostBillMpng>();
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "auditDetails", targetEntity = AuditPostVoucherMpng.class)
	private List<AuditPostVoucherMpng> postVoucherMpng=new ArrayList<AuditPostVoucherMpng>();
	
	private String department;
	@Transient
    private String approvalComent;
	@Transient
    private User approver;
	@Transient
    private Date approvedOn;
	@Transient
	private String schdDate;
	@Transient
	private String statusDescription;
	
	@Transient
	private String employeeName;
	
	@Transient
	private String pendingWith;
	
	@Transient
	private String billNumber;
	
	@Column(name="auditor_name",nullable = true)
	private String auditor_name;
	
	@Column(name="rsa_name",nullable = true)
	private String rsa_name;
	
	private Long rsa_id;
	
	@Override
	public String getStateDetails() {
		String billNumber="";
		if(egBillregister != null)
		{
			billNumber=egBillregister.getBillnumber();
		}
		
		 return getState().getComments().isEmpty() ? auditno+" ("+billNumber+") " : auditno + "-" + getState().getComments()+" ("+billNumber+") ";
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

	public List<AuditCheckList> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<AuditCheckList> checkList) {
		this.checkList = checkList;
	}

	public String getApprovalComent() {
		return approvalComent;
	}

	public void setApprovalComent(String approvalComent) {
		this.approvalComent = approvalComent;
	}

	public User getApprover() {
		return approver;
	}

	public void setApprover(User approver) {
		this.approver = approver;
	}

	public Date getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Date approvedOn) {
		this.approvedOn = approvedOn;
	}

	public Long getLead_auditor() {
		return lead_auditor;
	}

	public void setLead_auditor(Long lead_auditor) {
		this.lead_auditor = lead_auditor;
	}

	public String getAuditno() {
		return auditno;
	}

	public void setAuditno(String auditno) {
		this.auditno = auditno;
	}

	public List<AuditPostBillMpng> getPostBillMpng() {
		return postBillMpng;
	}

	public void setPostBillMpng(List<AuditPostBillMpng> postBillMpng) {
		this.postBillMpng = postBillMpng;
	}

	public String getSchdDate() {
		return schdDate;
	}

	public void setSchdDate(String schdDate) {
		this.schdDate = schdDate;
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public List<AuditPostVoucherMpng> getPostVoucherMpng() {
		return postVoucherMpng;
	}

	public void setPostVoucherMpng(List<AuditPostVoucherMpng> postVoucherMpng) {
		this.postVoucherMpng = postVoucherMpng;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Integer getPassUnderobjection() {
		return passUnderobjection;
	}

	public void setPassUnderobjection(Integer passUnderobjection) {
		this.passUnderobjection = passUnderobjection;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getPendingWith() {
		return pendingWith;
	}

	public void setPendingWith(String pendingWith) {
		this.pendingWith = pendingWith;
	}

	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
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

	public Long getRsa_id() {
		return rsa_id;
	}

	public void setRsa_id(Long rsa_id) {
		this.rsa_id = rsa_id;
	}


	

}
