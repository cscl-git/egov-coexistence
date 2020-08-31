package org.egov.apnimandi.transactions.entity;

import org.egov.apnimandi.masters.entity.ZoneMaster;
import org.egov.commons.EgwStatus;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.utils.DateUtils;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.workflow.entity.StateAware;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.Length;

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

@Entity
@Table(name = "EGAM_CONTRACTOR")
@SequenceGenerator(name = ApnimandiContractor.SEQ_CONTRACTOR, sequenceName = ApnimandiContractor.SEQ_CONTRACTOR, allocationSize = 1)
@AuditOverrides({ @AuditOverride(forClass = AbstractAuditable.class, name = "lastModifiedBy"),
        @AuditOverride(forClass = AbstractAuditable.class, name = "lastModifiedDate") })
public class ApnimandiContractor extends StateAware{
	private static final long serialVersionUID = 796823780349590496L;
    public static final String SEQ_CONTRACTOR = "SEQ_EGAM_CONTRACTOR";
    
    @Id
    @GeneratedValue(generator = SEQ_CONTRACTOR, strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @NotNull
    @Audited
    @Length(max = 10)
    private String salutation;
    
    @NotNull
    @Length(max = 100)
    @Audited
    private String name;
    
    @NotNull
    @Length(max = 256)
    @Audited
    private String address;
    
    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "contractsignedon")
    @Audited
    private Date contractSignedOn;
    
    @NotNull
    @Length(max = 4)
    @Audited
    private String aadhaarNo;
    
    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "validfromdate")
    @Audited
    private Date validFromDate;
    
    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "validtodate")
    @Audited
    private Date validToDate;

    @ManyToOne
    @JoinColumn(name = "zone", nullable = false)
    @Audited
    private ZoneMaster zone;
    
    @NotNull
    @Audited
    private Integer maxAllowedVendorsNo;
    
    @NotNull
    @Audited
    private double rentAmountPerDay;
    
    @NotNull
    @Audited
    private double securityFees;
    
    @NotNull
    @Audited
    private Integer duedayOfCollectionForEveryMonth;
    
    @NotNull
    @Audited
    private double penaltyAmountPerDay;
    
    @NotNull
    @Audited
    private double contractorSharePercentage;
    
    @Length(max = 1024)
    @Audited
    private String comment;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "terminateon")
    @Audited
    private Date terminateOn;
    
    @Length(max = 1024)
    @Audited
    private String terminateRemarks;
    
    @Audited
    private Boolean active;
    
    @ManyToOne
    @JoinColumn(name = "status", nullable = false)
    @NotAudited
    private EgwStatus status;
    
    @OneToMany(mappedBy = "contractor", fetch = FetchType.LAZY)
    @NotAudited
    private List<ContractorDocument> contractorDocuments = new ArrayList<ContractorDocument>(0);
    
    @Transient
    private String statusCode;
    
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
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    protected void setId(final Long id) {
        this.id = id;
    }

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getContractSignedOn() {
		return contractSignedOn;
	}

	public void setContractSignedOn(Date contractSignedOn) {
		this.contractSignedOn = contractSignedOn;
	}

	public String getAadhaarNo() {
		return aadhaarNo;
	}

	public void setAadhaarNo(String aadhaarNo) {
		this.aadhaarNo = aadhaarNo;
	}

	public Date getValidFromDate() {
		return validFromDate;
	}

	public void setValidFromDate(Date validFromDate) {
		this.validFromDate = validFromDate;
	}

	public Date getValidToDate() {
		return validToDate;
	}

	public void setValidToDate(Date validToDate) {
		this.validToDate = validToDate;
	}

	public ZoneMaster getZone() {
		return zone;
	}

	public void setZone(ZoneMaster zone) {
		this.zone = zone;
	}

	public Integer getMaxAllowedVendorsNo() {
		return maxAllowedVendorsNo;
	}

	public void setMaxAllowedVendorsNo(Integer maxAllowedVendorsNo) {
		this.maxAllowedVendorsNo = maxAllowedVendorsNo;
	}

	public double getRentAmountPerDay() {
		return rentAmountPerDay;
	}

	public void setRentAmountPerDay(double rentAmountPerDay) {
		this.rentAmountPerDay = rentAmountPerDay;
	}

	public double getSecurityFees() {
		return securityFees;
	}

	public void setSecurityFees(double securityFees) {
		this.securityFees = securityFees;
	}

	public Integer getDuedayOfCollectionForEveryMonth() {
		return duedayOfCollectionForEveryMonth;
	}

	public void setDuedayOfCollectionForEveryMonth(Integer duedayOfCollectionForEveryMonth) {
		this.duedayOfCollectionForEveryMonth = duedayOfCollectionForEveryMonth;
	}

	public double getPenaltyAmountPerDay() {
		return penaltyAmountPerDay;
	}

	public void setPenaltyAmountPerDay(double penaltyAmountPerDay) {
		this.penaltyAmountPerDay = penaltyAmountPerDay;
	}

	public double getContractorSharePercentage() {
		return contractorSharePercentage;
	}

	public void setContractorSharePercentage(double contractorSharePercentage) {
		this.contractorSharePercentage = contractorSharePercentage;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public List<ContractorDocument> getContractorDocuments() {
		return contractorDocuments;
	}

	public void setContractorDocuments(final List<ContractorDocument> contractorDocuments) {
		this.contractorDocuments = contractorDocuments;
	}

	public EgwStatus getStatus() {
		return status;
	}

	public void setStatus(EgwStatus status) {
		this.status = status;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	
	public List<ValidationError> validate() {
		 final List<ValidationError> errors = new ArrayList<>();
		 if (!DateUtils.compareDates(getValidToDate(), getValidFromDate()))
	           errors.add(new ValidationError("validToDate", "todate.less.fromdate"));
		 return errors;  
	}

	@Override
    public String getStateDetails() {
        return String.format("Contractor Name %s ", salutation + " " + name);
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

	public Date getTerminateOn() {
		return terminateOn;
	}

	public void setTerminateOn(Date terminateOn) {
		this.terminateOn = terminateOn;
	}

	public String getTerminateRemarks() {
		return terminateRemarks;
	}

	public void setTerminateRemarks(String terminateRemarks) {
		this.terminateRemarks = terminateRemarks;
	}
}
