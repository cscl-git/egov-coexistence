package org.egov.works.estimatepreparationapproval.entity;

import java.io.Serializable;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.egov.commons.EgwStatus;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.Designation;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.model.bills.DocumentUpload;
import org.egov.works.boq.entity.BoQDetails;

@Entity
@Table(name = "txn_estimate_preparation")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(name = EstimatePreparationApproval.SEQ_ESTIMATE_PREPARATION, sequenceName = EstimatePreparationApproval.SEQ_ESTIMATE_PREPARATION, allocationSize = 1)
public class EstimatePreparationApproval extends StateAware implements Serializable {

	private static final long serialVersionUID = -4312140421386028968L;

	public static final String SEQ_ESTIMATE_PREPARATION = "SEQ_ESTIMATE_PREPARATION";

	@Id
	@GeneratedValue(generator = SEQ_ESTIMATE_PREPARATION, strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Long id;

	@Column(name = "works_wing")
	private Long worksWing;

	@Column(name = "executing_division")
	private Long executingDivision;

	@Column(name = "estimate_date")
	private Date estimateDate;

	@Column(name = "estimate_number")
	private String estimateNumber;

	@Column(name = "work_location")
	private String workLocation;

	@Column(name = "sector_number")
	private Long sectorNumber;

	@Column(name = "ward_number")
	private Long wardNumber;

	@Column(name = "work_name")
	private String workName;

	@Column(name = "work_category")
	private Long workCategory;

	@Column(name = "necessity")
	private String necessity;

	@Column(name = "work_scope")
	private String workScope;

	@Column(name = "work_status")
	private Long workStatus;

	@Column(name = "estimate_amount")
	private Double estimateAmount;

	@Column(name = "estimate_prepared_by")
	private String estimatePreparedBy;

	@Column(name = "preparation_designation")
	private Long preparationDesignation;

	@Column(name = "financing_details")
	private Long financingDetails;

	@Column(name = "fund_source")
	private String fundSource;

	@Column(name = "financial_year")
	private String financialYear;

	@Column(name = "estimate_percentage")
	private String estimatePercentage;

	@Column(name = "work_type")
	private String workType;

	@Column(name = "tender_cost")
	private String tenderCost;

	@Column(name = "agency_work_order")
	private String agencyWorkOrder;

	@Column(name = "date")
	private Date date;

	@Column(name = "time_limit")
	private String timeLimit;
	
	@Column(name = "aanumber")
	private String aanumber;
	
	@Column(name = "aadate")
	private Date aadate;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "estimatePreparationApproval", targetEntity = BoQDetails.class)
	private List<BoQDetails> newBoQDetailsList=new ArrayList<BoQDetails>();

	@ManyToOne
    @JoinColumn(name = "statusid")
    private EgwStatus status;

	@Transient
	private List<EstimatePreparationApproval> estimateList;

	@Transient
	private boolean checked;

	@Transient
	private String estimateDt;

	@Transient
	private String fromDate;

	@Transient
	private String toDate;

	@Transient
	private Date fromDt;

	@Transient
	private Date toDt;

	@Transient
	private List<BoQDetails> boQDetailsList;

	@Transient
	private String department = "";

	@Transient
	private List<Department> departments = new ArrayList<Department>();
	
	@Transient
	private List<Designation> designations = new ArrayList<Designation>();
	
	@Transient
	private String statusDescription;

	@Transient
	private String dt;

	@Transient
	private String approvalDepartment;
	@Transient
	private String approvalComent;

	@Transient
	private User approver;
	@Transient
	private Date approvedOn;
	
	@Transient
	private String workFlowAction;
	
	@Transient
	private String objectType;
	
	@Transient
    private List<DocumentUpload> documentDetail = new ArrayList<>();
	
	@Transient
	private String workCategry;

	public Long getWorksWing() {
		return worksWing;
	}

	public void setWorksWing(Long worksWing) {
		this.worksWing = worksWing;
	}

	public Long getExecutingDivision() {
		return executingDivision;
	}

	public void setExecutingDivision(Long executingDivision) {
		this.executingDivision = executingDivision;
	}

	public Date getEstimateDate() {
		return estimateDate;
	}

	public void setEstimateDate(Date estimateDate) {
		this.estimateDate = estimateDate;
	}

	public String getEstimateNumber() {
		return estimateNumber;
	}

	public void setEstimateNumber(String estimateNumber) {
		this.estimateNumber = estimateNumber;
	}

	public String getWorkLocation() {
		return workLocation;
	}

	public void setWorkLocation(String workLocation) {
		this.workLocation = workLocation;
	}

	public Long getSectorNumber() {
		return sectorNumber;
	}

	public void setSectorNumber(Long sectorNumber) {
		this.sectorNumber = sectorNumber;
	}

	public Long getWardNumber() {
		return wardNumber;
	}

	public void setWardNumber(Long wardNumber) {
		this.wardNumber = wardNumber;
	}

	public String getWorkName() {
		return workName;
	}

	public void setWorkName(String workName) {
		this.workName = workName;
	}

	public Long getWorkCategory() {
		return workCategory;
	}

	public void setWorkCategory(Long workCategory) {
		this.workCategory = workCategory;
	}

	public String getNecessity() {
		return necessity;
	}

	public void setNecessity(String necessity) {
		this.necessity = necessity;
	}

	public String getWorkScope() {
		return workScope;
	}

	public void setWorkScope(String workScope) {
		this.workScope = workScope;
	}

	public Long getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(Long workStatus) {
		this.workStatus = workStatus;
	}

	public Double getEstimateAmount() {
		return estimateAmount;
	}

	public void setEstimateAmount(Double estimateAmount) {
		this.estimateAmount = estimateAmount;
	}


	public Long getPreparationDesignation() {
		return preparationDesignation;
	}

	public void setPreparationDesignation(Long preparationDesignation) {
		this.preparationDesignation = preparationDesignation;
	}

	public Long getFinancingDetails() {
		return financingDetails;
	}

	public void setFinancingDetails(Long financingDetails) {
		this.financingDetails = financingDetails;
	}

	public String getFundSource() {
		return fundSource;
	}

	public void setFundSource(String fundSource) {
		this.fundSource = fundSource;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getEstimatePercentage() {
		return estimatePercentage;
	}

	public void setEstimatePercentage(String estimatePercentage) {
		this.estimatePercentage = estimatePercentage;
	}

	public List<BoQDetails> getNewBoQDetailsList() {
		return newBoQDetailsList;
	}

	public void setNewBoQDetailsList(List<BoQDetails> newBoQDetailsList) {
		this.newBoQDetailsList = newBoQDetailsList;
	}

	public List<EstimatePreparationApproval> getEstimateList() {
		return estimateList;
	}

	public void setEstimateList(List<EstimatePreparationApproval> estimateList) {
		this.estimateList = estimateList;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getEstimateDt() {
		return estimateDt;
	}

	public void setEstimateDt(String estimateDt) {
		this.estimateDt = estimateDt;
	}

	public String getWorkType() {
		return workType;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}

	public String getTenderCost() {
		return tenderCost;
	}

	public void setTenderCost(String tenderCost) {
		this.tenderCost = tenderCost;
	}

	public String getAgencyWorkOrder() {
		return agencyWorkOrder;
	}

	public void setAgencyWorkOrder(String agencyWorkOrder) {
		this.agencyWorkOrder = agencyWorkOrder;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(String timeLimit) {
		this.timeLimit = timeLimit;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public Date getFromDt() {
		return fromDt;
	}

	public void setFromDt(Date fromDt) {
		this.fromDt = fromDt;
	}

	public Date getToDt() {
		return toDt;
	}

	public void setToDt(Date toDt) {
		this.toDt = toDt;
	}

	public List<BoQDetails> getBoQDetailsList() {
		return boQDetailsList;
	}

	public void setBoQDetailsList(List<BoQDetails> boQDetailsList) {
		this.boQDetailsList = boQDetailsList;
	}

	/*
	 * public EgwStatus getStatus() { return status; }
	 * 
	 * public void setStatus(EgwStatus status) { this.status = status; }
	 */

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public List<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}

	public String getDt() {
		return dt;
	}

	public void setDt(String dt) {
		this.dt = dt;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String getStateDetails() {
		return getState().getComments().isEmpty() ? estimateNumber : estimateNumber + "-" + getState().getComments();
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;

	}

	public String getApprovalDepartment() {
		return approvalDepartment;
	}

	public void setApprovalDepartment(String approvalDepartment) {
		this.approvalDepartment = approvalDepartment;
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

	public EgwStatus getStatus() {
		return status;
	}

	public void setStatus(EgwStatus status) {
		this.status = status;
	}

	public String getWorkFlowAction() {
		return workFlowAction;
	}

	public void setWorkFlowAction(String workFlowAction) {
		this.workFlowAction = workFlowAction;
	}

	public List<DocumentUpload> getDocumentDetail() {
		return documentDetail;
	}

	public void setDocumentDetail(List<DocumentUpload> documentDetail) {
		this.documentDetail = documentDetail;
	}

	public String getEstimatePreparedBy() {
		return estimatePreparedBy;
	}

	public void setEstimatePreparedBy(String estimatePreparedBy) {
		this.estimatePreparedBy = estimatePreparedBy;
	}

	public List<Designation> getDesignations() {
		return designations;
	}

	public void setDesignations(List<Designation> designations) {
		this.designations = designations;
	}

	public String getAanumber() {
		return aanumber;
	}

	public void setAanumber(String aanumber) {
		this.aanumber = aanumber;
	}

	public Date getAadate() {
		return aadate;
	}

	public void setAadate(Date aadate) {
		this.aadate = aadate;
	}

	public String getWorkCategry() {
		return workCategry;
	}

	public void setWorkCategry(String workCategry) {
		this.workCategry = workCategry;
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	
	

}
