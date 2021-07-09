package org.egov.works.estimatepreparationapproval.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.egov.commons.EgwStatus;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.Designation;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.model.bills.DocumentUpload;
import org.egov.works.boq.entity.BoQDetails;
import org.egov.works.boq.entity.BoqUploadDocument;


//Added Bikash Dhal
@SqlResultSetMapping(name = "AllEstimatePreparationApprovalresultset",classes = {
		@ConstructorResult(
				targetClass = EstimatePreparationApprovalRESTPOJO.class, columns = {
				@ColumnResult (name="id",type=String.class),@ColumnResult (name="agency_work_order",type=String.class),@ColumnResult (name="date",type=String.class),
				@ColumnResult (name="estimate_amount",type=String.class),@ColumnResult (name="estimate_date",type=String.class),
				@ColumnResult (name="estimate_number",type=String.class),@ColumnResult (name="estimate_percentage",type=String.class),
				@ColumnResult (name="estimate_prepared_by",type=String.class),@ColumnResult (name="executing_division",type=String.class),@ColumnResult (name="financial_year",type=String.class),
				@ColumnResult (name="financing_details",type=String.class),@ColumnResult (name="fund_source",type=String.class),@ColumnResult (name="necessity",type=String.class),
				@ColumnResult (name="preparation_designation",type=String.class),@ColumnResult (name="sector_number",type=String.class),@ColumnResult (name="tender_cost",type=String.class),
				@ColumnResult (name="time_limit",type=String.class),@ColumnResult (name="ward_number",type=String.class),@ColumnResult (name="work_category",type=String.class),
				@ColumnResult (name="work_location",type=String.class),@ColumnResult (name="work_name",type=String.class),@ColumnResult (name="work_scope",type=String.class),
				@ColumnResult (name="work_status",type=String.class),@ColumnResult (name="work_type",type=String.class),@ColumnResult (name="works_wing",type=String.class),
				@ColumnResult (name="state_id",type=String.class),@ColumnResult (name="version",type=String.class),@ColumnResult (name="statusid",type=String.class),
				@ColumnResult (name="createdby",type=String.class),@ColumnResult (name="createddate",type=String.class),@ColumnResult (name="lastmodifiedby",type=String.class),
				@ColumnResult (name="lastmodifieddate",type=String.class),@ColumnResult (name="aanumber",type=String.class),@ColumnResult (name="aadate",type=String.class),
				@ColumnResult (name="contingent_percentage",type=String.class),@ColumnResult (name="contingent_amount",type=String.class),@ColumnResult (name="consultant_fee",type=String.class),
				@ColumnResult (name="unforseen_charges",type=String.class),@ColumnResult (name="expenditure_head",type=String.class),@ColumnResult (name="expenditure_sub_category",type=String.class),
				@ColumnResult (name="expenditure_category",type=String.class),@ColumnResult (name="meetnumber",type=String.class),@ColumnResult (name="meetcategory",type=String.class),
				@ColumnResult (name="meetdate",type=String.class),@ColumnResult (name="expenditure_head_est",type=String.class),@ColumnResult (name="status",type=String.class),@ColumnResult (name="subdivision",type=String.class) }
				)
})
@NamedNativeQuery(name="EstimatePreparationApproval.getEstimatePreparationApprovalRESTPOJO", query = " select tep.id, tep.agency_work_order ,tep.date, tep.estimate_amount,tep.estimate_date,tep.estimate_number, tep.estimate_percentage ,tep.estimate_prepared_by, dep.name as executing_division,tep.financial_year ,tep.financing_details, tep.fund_source ,tep.necessity,tep.preparation_designation ,tep.sector_number, tep.tender_cost ,tep.time_limit ,tep.ward_number ,tep.work_category , tep.work_location ,tep.work_name,tep.work_scope ,tep.work_status , tep.work_type ,tep.works_wing ,tep.state_id,tep.version ,tep.statusid, tep.createdby ,tep.createddate ,tep.lastmodifiedby ,tep.lastmodifieddate, tep.aanumber,tep.aadate ,tep.contingent_percentage ,tep.contingent_amount, tep.consultant_fee ,tep.unforseen_charges ,tep.expenditure_head, tep.expenditure_sub_category ,tep.expenditure_category,tep.meetnumber, tep.meetcategory ,tep.meetdate ,tep.expenditure_head_est,es.code as status,sub.subdivision from txn_estimate_preparation tep , egw_status es, eg_department dep,eg_subdivision sub where tep.statusid =es.id and tep.executing_division = dep.id and tep.subdivision=sub.id",
			resultClass = EstimatePreparationApprovalRESTPOJO.class,resultSetMapping = "AllEstimatePreparationApprovalresultset")



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
	private String worksWing;

	@Column(name = "executing_division")
	private Long executingDivision;

	@Column(name = "estimate_date")
	private Date estimateDate;

	@Column(name = "estimate_number")
	private String estimateNumber;

	@Column(name = "work_location")
	private String workLocation;

	@Column(name = "sector_number")
	private String sectorNumber;

	@Column(name = "ward_number")
	private String wardNumber;

	@Column(name = "work_name")
	private String workName;

	@Column(name = "work_category")
	private String workCategory;

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
	
	@Column(name = "meetNumber")
	private String meetNumber;
	
	@Column(name = "meetCategory")
	private String meetCategory;
	
	@Column(name = "meetDate")
	private Date meetDate;
	
	@Column(name="subdivision")
	private Long subdivision;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "estimatePreparationApproval", targetEntity = BoQDetails.class)
	private List<BoQDetails> newBoQDetailsList=new ArrayList<BoQDetails>();

	/*@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "estimatePreparationApproval", targetEntity = BoQDetails.class)
	private List<BoqUploadDocument> uploadDocument=new ArrayList<BoqUploadDocument>();*/

	@ManyToOne
    @JoinColumn(name = "statusid")
    private EgwStatus status;

	
	@Column(name = "estimate_percentage")
	private String estimatePercentage;
	
	@Column(name = "contingent_percentage")
	private Double contingentPercentage;
	
	@Column(name = "contingent_amount")
	private BigDecimal contingentAmount;
	@Column(name = "consultant_fee")
	private BigDecimal consultantFee;
	@Column(name = "unforseen_charges")
	private BigDecimal unforseenCharges;
	
	@Column(name = "expenditure_head")
	private String expHead;
	
	@Column(name = "expenditure_head_est")
	private String expHead_est;
	
	@Column(name = "expenditure_sub_category")
	private String expSubCategory;
	
	@Column(name = "expenditure_category")
	private String expCategory;
	
	@Transient
	private String workStatusSearch;
	
	@Transient
	private String pendingWith;

	public Double getContingentPercentage() {
		return contingentPercentage;
	}

	public void setContingentPercentage(Double contingentPercentage) {
		this.contingentPercentage = contingentPercentage;
	}

	public BigDecimal getContingentAmount() {
		return contingentAmount;
	}

	public void setContingentAmount(BigDecimal contingentAmount) {
		this.contingentAmount = contingentAmount;
	}

	public BigDecimal getConsultantFee() {
		return consultantFee;
	}

	public void setConsultantFee(BigDecimal consultantFee) {
		this.consultantFee = consultantFee;
	}

	public BigDecimal getUnforseenCharges() {
		return unforseenCharges;
	}

	public void setUnforseenCharges(BigDecimal unforseenCharges) {
		this.unforseenCharges = unforseenCharges;
	}

	@Transient
	private List<EstimatePreparationApproval> estimateList;

	@Transient
	private boolean checked;

	@Transient
	private String estimateDt;

	@Transient
	private String createdDt;
	
	@Transient
	private String executeDiv;
	
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
	private List<BoqUploadDocument> docUpload;
	
	/*@Transient
	private List<String> UploadId;*/

	@Transient
	private String department = "";

	@Transient
	private List<Department> departments = new ArrayList<Department>();
	
	@Transient
	private List<Designation> designations = new ArrayList<Designation>();
	
	@Transient
	private List<Workswing> workswings = new ArrayList<Workswing>();
	
	@Transient
	private List<Subdivisionworks> subdivisions = new ArrayList<Subdivisionworks>();
	
	@Transient
	private List<org.egov.infra.admin.master.entity.Department> newdepartments = new ArrayList<org.egov.infra.admin.master.entity.Department>();
	
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
    private List<DocumentUpload> roughCostdocumentDetail = new ArrayList<>();
	
	@Transient
	private String workCategry;
	
	@Column(name="comments")
    private String comments;
	
	@Transient
	private Long uploadfileStoreId;
	@Transient
	private Long uploadId;
	
	


	public Long getUploadId() {
		return uploadId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Long getUploadfileStoreId() {
		return uploadfileStoreId;
	}

	public void setUploadfileStoreId(Long uploadfileStoreId) {
		this.uploadfileStoreId = uploadfileStoreId;
	}

	public void setUploadId(Long uploadId) {
		this.uploadId = uploadId;
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





	public String getWorkName() {
		return workName;
	}

	public void setWorkName(String workName) {
		this.workName = workName;
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

	public String getWorksWing() {
		return worksWing;
	}

	public void setWorksWing(String worksWing) {
		this.worksWing = worksWing;
	}

	public String getSectorNumber() {
		return sectorNumber;
	}

	public void setSectorNumber(String sectorNumber) {
		this.sectorNumber = sectorNumber;
	}

	public String getWardNumber() {
		return wardNumber;
	}

	public void setWardNumber(String wardNumber) {
		this.wardNumber = wardNumber;
	}

	public String getWorkCategory() {
		return workCategory;
	}

	public void setWorkCategory(String workCategory) {
		this.workCategory = workCategory;
	}

	public String getExpHead() {
		return expHead;
	}

	public void setExpHead(String expHead) {
		this.expHead = expHead;
	}

	public String getExpSubCategory() {
		return expSubCategory;
	}

	public void setExpSubCategory(String expSubCategory) {
		this.expSubCategory = expSubCategory;
	}

	public String getExpCategory() {
		return expCategory;
	}

	public void setExpCategory(String expCategory) {
		this.expCategory = expCategory;
	}

	public List<DocumentUpload> getRoughCostdocumentDetail() {
		return roughCostdocumentDetail;
	}

	public void setRoughCostdocumentDetail(List<DocumentUpload> roughCostdocumentDetail) {
		this.roughCostdocumentDetail = roughCostdocumentDetail;
	}

	public String getMeetNumber() {
		return meetNumber;
	}

	public void setMeetNumber(String meetNumber) {
		this.meetNumber = meetNumber;
	}

	public String getMeetCategory() {
		return meetCategory;
	}

	public void setMeetCategory(String meetCategory) {
		this.meetCategory = meetCategory;
	}

	public Date getMeetDate() {
		return meetDate;
	}

	public void setMeetDate(Date meetDate) {
		this.meetDate = meetDate;
	}

	public String getExpHead_est() {
		return expHead_est;
	}

	public void setExpHead_est(String expHead_est) {
		this.expHead_est = expHead_est;
	}

	public String getWorkStatusSearch() {
		return workStatusSearch;
	}

	public void setWorkStatusSearch(String workStatusSearch) {
		this.workStatusSearch = workStatusSearch;
	}

	public String getPendingWith() {
		return pendingWith;
	}

	public void setPendingWith(String pendingWith) {
		this.pendingWith = pendingWith;
	}

	public String getCreatedDt() {
		return createdDt;
	}

	public void setCreatedDt(String createdDt) {
		this.createdDt = createdDt;
	}

	public String getExecuteDiv() {
		return executeDiv;
	}

	public void setExecuteDiv(String executeDiv) {
		this.executeDiv = executeDiv;
	}



	public List<BoqUploadDocument> getDocUpload() {
		return docUpload;
	}

	public void setDocUpload(List<BoqUploadDocument> docUpload) {
		this.docUpload = docUpload;
	}

	public Long getSubdivision() {
		return subdivision;
	}

	public void setSubdivision(Long subdivision) {
		this.subdivision = subdivision;
	}

	public List<Workswing> getWorkswings() {
		return workswings;
	}
	
	public void setWorkswings(List<Workswing> workswings) {
		this.workswings = workswings;
	}
	
	public List<Subdivisionworks> getSubdivisions() {
		return subdivisions;
	}
	
	public void setSubdivisions(List<Subdivisionworks> subdivisions) {
		this.subdivisions = subdivisions;
	}

	public List<org.egov.infra.admin.master.entity.Department> getNewdepartments() {
		return newdepartments;
	}

	public void setNewdepartments(List<org.egov.infra.admin.master.entity.Department> newdepartments) {
		this.newdepartments = newdepartments;
}

	
	
	

}