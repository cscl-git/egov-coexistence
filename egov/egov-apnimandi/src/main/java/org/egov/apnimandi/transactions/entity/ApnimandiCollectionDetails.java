package org.egov.apnimandi.transactions.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.egov.apnimandi.masters.entity.ApnimandiCollectionType;
import org.egov.apnimandi.masters.entity.ZoneMaster;
import org.egov.commons.EgwStatus;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.workflow.entity.StateAware;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "EGAM_COLLECTION_DETAILS")
@SequenceGenerator(name = ApnimandiCollectionDetails.SEQ_COLLECTION_DETAILS, sequenceName = ApnimandiCollectionDetails.SEQ_COLLECTION_DETAILS, allocationSize = 1)
@AuditOverrides({ @AuditOverride(forClass = AbstractAuditable.class, name = "lastModifiedBy"),
        @AuditOverride(forClass = AbstractAuditable.class, name = "lastModifiedDate") })
public class ApnimandiCollectionDetails extends StateAware{
	private static final long serialVersionUID = 796823780349590496L;
    public static final String SEQ_COLLECTION_DETAILS = "SEQ_EGAM_COLLECTION_DETAILS";
    
    @Id
    @GeneratedValue(generator = SEQ_COLLECTION_DETAILS, strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "collectiontype", nullable = false)
    @Audited
    private ApnimandiCollectionType collectiontype;
    
    @ManyToOne
    @JoinColumn(name = "zone", nullable = false)
    @Audited
    private ZoneMaster zone;
    
    @NotNull
    @Audited
    private Integer collectionForMonth;
    
    @NotNull
    @Audited
    private Integer collectionForYear;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractor", nullable = true)
    @Audited
    private ApnimandiContractor contractor;
    
    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "receiptdate")
    @Audited
    private Date receiptDate;
    
    @NotNull
    @Audited
    private String serviceCategory;
    
    @NotNull
    @Audited
    private String serviceType;
    
    @NotNull
    @Audited
    private String payeeName;
    
    @NotNull
    @Audited
    private String paymentMode;
    
    @Audited
    private String ddOrChequeNo;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "ddorchequedate")
    @Audited
    private Date ddOrChequeDate;
    
    @Audited
    private String ifscCode;
    
    @Audited
    private String bankName;
    
    @Audited
    private String branchName;
    
    @Audited
    private String bankCode;
    
    @NotNull
    @Audited
    private double amount;
    
    @Length(max = 1024)
    @Audited
    private String comment;
    
    @NotNull
    @Audited
    private Long collectedBy;
    
    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "collectiondate")
    @Audited
    private Date collectionDate;
    
    @Audited
    private String receiptNo;

    @Audited
    private Boolean active;
    
    @ManyToOne
    @JoinColumn(name = "status", nullable = false)
    @NotAudited
    private EgwStatus status;
    
    @Audited
    private String paymentId;
    
    @Transient
    private String statusCode;
    
    @Transient
    private String collectionMonthName;
    
    @Transient
    private String approvalDepartment;
    
    @Transient
    private String approvalDesignation;

    @Transient
    private Long approvalPosition;
    
    @Transient
    private String approvalComent;
    
    @Transient
    private String departmentName;
    
	@OneToMany(mappedBy = "apnimandicollectiondetails", fetch = FetchType.LAZY)
    @NotAudited
    private List<ApnimandiCollectionAmountDetails> apnimandiCollectionAmountDetails = new ArrayList<ApnimandiCollectionAmountDetails>(0);
    
    @OneToMany(mappedBy = "apnimandicollectiondetails", fetch = FetchType.LAZY)
    @NotAudited
    private List<ApnimandiCollectionDocument> apnimandiCollectionDocuments = new ArrayList<ApnimandiCollectionDocument>(0);
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    protected void setId(final Long id) {
        this.id = id;
    }

	public ApnimandiCollectionType getCollectiontype() {
		return collectiontype;
	}

	public void setCollectiontype(ApnimandiCollectionType collectiontype) {
		this.collectiontype = collectiontype;
	}

	public ZoneMaster getZone() {
		return zone;
	}

	public void setZone(ZoneMaster zone) {
		this.zone = zone;
	}

	public Integer getCollectionForMonth() {
		return collectionForMonth;
	}

	public void setCollectionForMonth(Integer collectionForMonth) {
		this.collectionForMonth = collectionForMonth;
	}

	public Integer getCollectionForYear() {
		return collectionForYear;
	}

	public void setCollectionForYear(Integer collectionForYear) {
		this.collectionForYear = collectionForYear;
	}

	public ApnimandiContractor getContractor() {
		return contractor;
	}

	public void setContractor(ApnimandiContractor contractor) {
		this.contractor = contractor;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getServiceCategory() {
		return serviceCategory;
	}

	public void setServiceCategory(String serviceCategory) {
		this.serviceCategory = serviceCategory;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getPayeeName() {
		return payeeName;
	}

	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getDdOrChequeNo() {
		return ddOrChequeNo;
	}

	public void setDdOrChequeNo(String ddOrChequeNo) {
		this.ddOrChequeNo = ddOrChequeNo;
	}

	public Date getDdOrChequeDate() {
		return ddOrChequeDate;
	}

	public void setDdOrChequeDate(Date ddOrChequeDate) {
		this.ddOrChequeDate = ddOrChequeDate;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Long getCollectedBy() {
		return collectedBy;
	}

	public void setCollectedBy(Long collectedBy) {
		this.collectedBy = collectedBy;
	}

	public Date getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public EgwStatus getStatus() {
		return status;
	}

	public void setStatus(EgwStatus status) {
		this.status = status;
	}

	public List<ApnimandiCollectionAmountDetails> getApnimandiCollectionAmountDetails() {
		return apnimandiCollectionAmountDetails;
	}

	public void setApnimandiCollectionAmountDetails(List<ApnimandiCollectionAmountDetails> apnimandiCollectionAmountDetails) {
		this.apnimandiCollectionAmountDetails = apnimandiCollectionAmountDetails;
	}

	public List<ApnimandiCollectionDocument> getApnimandiCollectionDocuments() {
		return apnimandiCollectionDocuments;
	}

	public void setApnimandiCollectionDocuments(List<ApnimandiCollectionDocument> apnimandiCollectionDocuments) {
		this.apnimandiCollectionDocuments = apnimandiCollectionDocuments;
	}
	
	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getCollectionMonthName() {
		return collectionMonthName;
	}

	public void setCollectionMonthName(String collectionMonthName) {
		this.collectionMonthName = collectionMonthName;
	}

	public String getApprovalDepartment() {
		return approvalDepartment;
	}

	public void setApprovalDepartment(String approvalDepartment) {
		this.approvalDepartment = approvalDepartment;
	}

	public String getApprovalDesignation() {
		return approvalDesignation;
	}

	public void setApprovalDesignation(String approvalDesignation) {
		this.approvalDesignation = approvalDesignation;
	}

	public Long getApprovalPosition() {
		return approvalPosition;
	}

	public void setApprovalPosition(Long approvalPosition) {
		this.approvalPosition = approvalPosition;
	}

	public String getApprovalComent() {
		return approvalComent;
	}

	public void setApprovalComent(String approvalComent) {
		this.approvalComent = approvalComent;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	
	@Override
    public String getStateDetails() {
        return String.format("Collection Details");
    }

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}	
}
