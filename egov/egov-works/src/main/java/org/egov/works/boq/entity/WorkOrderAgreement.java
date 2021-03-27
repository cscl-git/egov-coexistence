package org.egov.works.boq.entity;

import java.io.Serializable;
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
import org.egov.infra.workflow.entity.StateAware;
import org.egov.model.bills.DocumentUpload;
import org.egov.model.masters.Contractor;


@SqlResultSetMapping(name = "AllWorkOrderAgreementresultset",classes = {
		@ConstructorResult(
				targetClass = WorkOrderAgreementRESTPOJO.class, columns = {

				@ColumnResult(name="id",type=String.class),@ColumnResult(name="agency_work_order",type=String.class),
				@ColumnResult(name="agreement_details",type=String.class),@ColumnResult(name="category",type=String.class),@ColumnResult(name="contractor_address",type=String.class),@ColumnResult(name="contractor_code",type=String.class),
				@ColumnResult(name="contractor_email",type=String.class),@ColumnResult(name="contractor_name",type=String.class),@ColumnResult(name="contractor_phone",type=String.class),
				@ColumnResult(name="date",type=String.class),@ColumnResult(name="estimated_cost",type=String.class),@ColumnResult(name="executing_department",type=String.class),@ColumnResult(name="fund",type=String.class),
				@ColumnResult(name="name_work_order",type=String.class),@ColumnResult(name="sector",type=String.class),@ColumnResult(name="tender_cost",type=String.class),
				@ColumnResult(name="time_limit",type=String.class),@ColumnResult(name="work_location",type=String.class),@ColumnResult(name="work_type",type=String.class),
				@ColumnResult(name="work_agreement_status",type=String.class),@ColumnResult(name="work_amount",type=String.class),@ColumnResult(name="work_details",type=String.class),
				@ColumnResult(name="work_end_date",type=String.class),@ColumnResult(name="work_intended_date",type=String.class),@ColumnResult(name="work_number",type=String.class),
				@ColumnResult(name="work_start_date",type=String.class),@ColumnResult(name="work_status",type=String.class),@ColumnResult(name="ward_number",type=String.class),
				@ColumnResult(name="statusid",type=String.class),@ColumnResult(name="version",type=String.class),@ColumnResult(name="createdby",type=String.class),
				@ColumnResult(name="createddate",type=String.class),@ColumnResult(name="lastmodifiedby",type=String.class),@ColumnResult(name="lastmodifieddate",type=String.class),
				@ColumnResult(name="state_id",type=String.class),@ColumnResult(name="work_agreement_number",type=String.class),@ColumnResult(name="project_closure_comments",type=String.class),
				@ColumnResult(name="contractor_performance_comments",type=String.class),@ColumnResult(name="actual_start_date",type=String.class),@ColumnResult(name="actual_end_date",type=String.class),
				@ColumnResult(name="approval_competent_authority",type=String.class),@ColumnResult(name="status",type=String.class),@ColumnResult(name="percentage_completion",type=String.class)}
				)
	})


	@NamedNativeQuery(name="WorkOrderAgreement.getAllWorkOrderAgreement", query = "select aw.id,aw.agency_work_order,aw.agreement_details,aw.category,aw.contractor_address, aw.contractor_code,aw.contractor_email,aw.contractor_name, aw.contractor_phone,aw.date,aw.estimated_cost,dep.name as executing_department,aw.fund,aw.name_work_order,aw.sector, aw.tender_cost,aw.time_limit,aw.work_location,aw.work_type, aw.work_agreement_status,aw.work_amount,aw.work_details,aw.work_end_date, aw.work_intended_date,aw.work_number,aw.work_start_date,aw.work_status, aw.ward_number,aw.statusid,aw.version,aw.createdby,aw.createddate, aw.lastmodifiedby,aw.lastmodifieddate,aw.state_id,aw.work_agreement_number, aw.project_closure_comments,aw.contractor_performance_comments,aw.actual_start_date, aw.actual_end_date,aw.approval_competent_authority, es.code as status, ceil (sum(tb.measured_amount)/(sum(tep.estimate_amount)/count(tep.estimate_amount))*100) as percentage_completion from txn_work_agreement aw, eg_department dep,egw_status es , txn_boqdetails tb,txn_estimate_preparation tep where aw.statusid =es.id and cast (aw.executing_department as Integer) = dep.id and aw.id=tb.work_id and tep.id = tb.work_id group by aw.id,dep.name ,es.code",
		resultClass = WorkOrderAgreementRESTPOJO.class,resultSetMapping = "AllWorkOrderAgreementresultset")

@Entity
@Table(name = "txn_work_agreement")
@SequenceGenerator(name = WorkOrderAgreement.SEQ_WORK_ORDER_AGREEMENT, sequenceName = WorkOrderAgreement.SEQ_WORK_ORDER_AGREEMENT, allocationSize = 1)
public class WorkOrderAgreement extends StateAware implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String SEQ_WORK_ORDER_AGREEMENT = "SEQ_WORK_ORDER_AGREEMENT";

	@Id
	@GeneratedValue(generator = SEQ_WORK_ORDER_AGREEMENT, strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Long id;

	@Column(name = "name_work_order")
	private String name_work_order;

	@Column(name = "work_number")
	private String work_number;

	@Column(name = "work_start_date")
	private Date work_start_date;

	@Column(name = "work_intended_date")
	private Date work_intended_date;

	@Column(name = "work_end_date")
	private Date work_end_date;

	@Column(name = "actual_start_date")
	private Date actual_start_date;
	
	@Column(name = "actual_end_date")
	private Date actual_end_date;

	@Column(name = "executing_department")
	private String executing_department;

	@Column(name = "work_amount")
	private String work_amount;

	@Column(name = "work_details")
	private String work_details;

	@Column(name = "agreement_details")
	private String agreement_details;

	@Column(name = "work_agreement_status")
	private String work_agreement_status;

	@Column(name = "work_status")
	private String work_status;

	@Column(name = "fund")
	private String fund;

	@Column(name = "estimated_cost")
	private String estimatedCost;

	@Column(name = "sector")
	private String sector;

	@Column(name = "work_location")
	private String workLocation;

	@Column(name = "contractor_name")
	private String contractor_name;

	@Column(name = "contractor_code")
	private String contractor_code;

	@Column(name = "contractor_address")
	private String contractor_address;

	@Column(name = "contractor_phone")
	private String contractor_phone;

	@Column(name = "contractor_email")
	private String contractor_email;

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

	@Column(name = "category")
	private String category;
	
	@Column(name = "approval_competent_authority")
	private String approval_competent_authority;

	//edited
	@Column(name="milestonestatus")
	private String milestonestatus;

	@Transient
	private List<BoQDetails> boQDetailsList;

	@Transient
	private String department = "";

	@Transient
	private List<Department> departments = new ArrayList<Department>();

	@Transient
	private List<WorkOrderAgreement> WorkOrderList;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "workOrderAgreement", targetEntity = BoQDetails.class)
	private List<BoQDetails> newBoQDetailsList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "workOrderAgreement", targetEntity = PaymentDistribution.class)
	private List<PaymentDistribution> paymentDistribution;
	
	@ManyToOne
    @JoinColumn(name = "statusid")
    private EgwStatus status;
	
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
	
	@Column(name = "ward_number")
	private String wardNumber;
	
	@Transient
	private Double progressCompletion;
	@Transient
    private List<DocumentUpload> documentDetail = new ArrayList<>();
	@Transient
    private List<DocumentUpload> documents = new ArrayList<>();
	@Transient
    private List<DocumentUpload> estDocuments = new ArrayList<>();
	@Transient
	private Long dnitId;
	@Transient
	private Long estId;
	@Transient
	private List<Contractor> contractors = new ArrayList<Contractor>();
	
	@Column(name = "work_agreement_number")
	private String work_agreement_number;
	
	
	//for search & modification
	@Transient
	private String name_work_order_search;
	
	@Transient
	private String work_number_search;
	
	@Transient
	private String work_agreement_number_search;
	
	@Transient
	private Date fromDate;
	
	@Transient
	private Date toDate;
	@Transient
	private String startDate;
	@Transient
	private String endDate;
	@Transient
	private String statusDescp;
	
	@Column(name = "project_closure_comments")
	private String project_closure_comments;
	
	@Column(name = "contractor_performance_comments")
	private String contractor_performance_comments;
	
	@Transient
	private String percentCompletion;
	
	@Transient
	private String pendingWith;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName_work_order() {
		return name_work_order;
	}

	public void setName_work_order(String name_work_order) {
		this.name_work_order = name_work_order;
	}

	public String getWork_number() {
		return work_number;
	}

	public void setWork_number(String work_number) {
		this.work_number = work_number;
	}

	public Date getWork_start_date() {
		return work_start_date;
	}

	public void setWork_start_date(Date work_start_date) {
		this.work_start_date = work_start_date;
	}

	public Date getWork_intended_date() {
		return work_intended_date;
	}

	public void setWork_intended_date(Date work_intended_date) {
		this.work_intended_date = work_intended_date;
	}

	public Date getWork_end_date() {
		return work_end_date;
	}

	public void setWork_end_date(Date work_end_date) {
		this.work_end_date = work_end_date;
	}

	public String getExecuting_department() {
		return executing_department;
	}

	public void setExecuting_department(String executing_department) {
		this.executing_department = executing_department;
	}

	public String getWork_amount() {
		return work_amount;
	}

	public void setWork_amount(String work_amount) {
		this.work_amount = work_amount;
	}

	public String getWork_details() {
		return work_details;
	}

	public void setWork_details(String work_details) {
		this.work_details = work_details;
	}

	public String getAgreement_details() {
		return agreement_details;
	}

	public void setAgreement_details(String agreement_details) {
		this.agreement_details = agreement_details;
	}

	public String getWork_agreement_status() {
		return work_agreement_status;
	}

	public void setWork_agreement_status(String work_agreement_status) {
		this.work_agreement_status = work_agreement_status;
	}

	public String getWork_status() {
		return work_status;
	}

	public void setWork_status(String work_status) {
		this.work_status = work_status;
	}

	public String getFund() {
		return fund;
	}

	public void setFund(String fund) {
		this.fund = fund;
	}

	public String getEstimatedCost() {
		return estimatedCost;
	}

	public void setEstimatedCost(String estimatedCost) {
		this.estimatedCost = estimatedCost;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getWorkLocation() {
		return workLocation;
	}

	public void setWorkLocation(String workLocation) {
		this.workLocation = workLocation;
	}

	public String getContractor_name() {
		return contractor_name;
	}

	public void setContractor_name(String contractor_name) {
		this.contractor_name = contractor_name;
	}

	public String getContractor_code() {
		return contractor_code;
	}

	public void setContractor_code(String contractor_code) {
		this.contractor_code = contractor_code;
	}

	public String getContractor_address() {
		return contractor_address;
	}

	public void setContractor_address(String contractor_address) {
		this.contractor_address = contractor_address;
	}

	public String getContractor_phone() {
		return contractor_phone;
	}

	public void setContractor_phone(String contractor_phone) {
		this.contractor_phone = contractor_phone;
	}

	public String getContractor_email() {
		return contractor_email;
	}

	public void setContractor_email(String contractor_email) {
		this.contractor_email = contractor_email;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<BoQDetails> getBoQDetailsList() {
		return boQDetailsList;
	}

	public void setBoQDetailsList(List<BoQDetails> boQDetailsList) {
		this.boQDetailsList = boQDetailsList;
	}

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

	public List<WorkOrderAgreement> getWorkOrderList() {
		return WorkOrderList;
	}

	public void setWorkOrderList(List<WorkOrderAgreement> workOrderList) {
		WorkOrderList = workOrderList;
	}

	public List<BoQDetails> getNewBoQDetailsList() {
		return newBoQDetailsList;
	}

	public void setNewBoQDetailsList(List<BoQDetails> newBoQDetailsList) {
		this.newBoQDetailsList = newBoQDetailsList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String getStateDetails() {
		return getState().getComments().isEmpty() ? work_agreement_number : work_agreement_number + "-" + getState().getComments();
	}

	public EgwStatus getStatus() {
		return status;
	}

	public void setStatus(EgwStatus status) {
		this.status = status;
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

	public String getWorkFlowAction() {
		return workFlowAction;
	}

	public void setWorkFlowAction(String workFlowAction) {
		this.workFlowAction = workFlowAction;
	}

	public String getWardNumber() {
		return wardNumber;
	}

	public void setWardNumber(String wardNumber) {
		this.wardNumber = wardNumber;
	}

	public Double getProgressCompletion() {
		return progressCompletion;
	}

	public void setProgressCompletion(Double progressCompletion) {
		this.progressCompletion = progressCompletion;
	}

	public List<DocumentUpload> getDocumentDetail() {
		return documentDetail;
	}

	public void setDocumentDetail(List<DocumentUpload> documentDetail) {
		this.documentDetail = documentDetail;
	}

	public List<Contractor> getContractors() {
		return contractors;
	}

	public void setContractors(List<Contractor> contractors) {
		this.contractors = contractors;
	}

	public String getWork_agreement_number() {
		return work_agreement_number;
	}

	public void setWork_agreement_number(String work_agreement_number) {
		this.work_agreement_number = work_agreement_number;
	}

	public String getName_work_order_search() {
		return name_work_order_search;
	}

	public void setName_work_order_search(String name_work_order_search) {
		this.name_work_order_search = name_work_order_search;
	}

	public String getWork_number_search() {
		return work_number_search;
	}

	public void setWork_number_search(String work_number_search) {
		this.work_number_search = work_number_search;
	}

	public String getWork_agreement_number_search() {
		return work_agreement_number_search;
	}

	public void setWork_agreement_number_search(String work_agreement_number_search) {
		this.work_agreement_number_search = work_agreement_number_search;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getStatusDescp() {
		return statusDescp;
	}

	public void setStatusDescp(String statusDescp) {
		this.statusDescp = statusDescp;
	}

	public String getProject_closure_comments() {
		return project_closure_comments;
	}

	public void setProject_closure_comments(String project_closure_comments) {
		this.project_closure_comments = project_closure_comments;
	}

	public String getContractor_performance_comments() {
		return contractor_performance_comments;
	}

	public void setContractor_performance_comments(String contractor_performance_comments) {
		this.contractor_performance_comments = contractor_performance_comments;
	}

	public String getPercentCompletion() {
		return percentCompletion;
	}

	public void setPercentCompletion(String percentCompletion) {
		this.percentCompletion = percentCompletion;
	}

	public List<PaymentDistribution> getPaymentDistribution() {
		return paymentDistribution;
	}

	public void setPaymentDistribution(List<PaymentDistribution> paymentDistribution) {
		this.paymentDistribution = paymentDistribution;
	}

	public Date getActual_end_date() {
		return actual_end_date;
	}

	public void setActual_end_date(Date actual_end_date) {
		this.actual_end_date = actual_end_date;
	}

	public Date getActual_start_date() {
		return actual_start_date;
	}

	public void setActual_start_date(Date actual_start_date) {
		this.actual_start_date = actual_start_date;
	}

	public String getPendingWith() {
		return pendingWith;
	}

	public void setPendingWith(String pendingWith) {
		this.pendingWith = pendingWith;
	}

	public String getApproval_competent_authority() {
		return approval_competent_authority;
	}

	public void setApproval_competent_authority(String approval_competent_authority) {
		this.approval_competent_authority = approval_competent_authority;
	}

	public String getMilestonestatus() {
		return milestonestatus;
	}

	public void setMilestonestatus(String milestonestatus) {
		this.milestonestatus = milestonestatus;
	}

	public List<DocumentUpload> getDocuments() {
		return documents;
	}

	public void setDocuments(List<DocumentUpload> documents) {
		this.documents = documents;
	}

	public Long getDnitId() {
		return dnitId;
	}

	public void setDnitId(Long dnitId) {
		this.dnitId = dnitId;
	}

	public List<DocumentUpload> getEstDocuments() {
		return estDocuments;
	}

	public void setEstDocuments(List<DocumentUpload> estDocuments) {
		this.estDocuments = estDocuments;
	}

	public Long getEstId() {
		return estId;
	}

	public void setEstId(Long estId) {
		this.estId = estId;
	}

}
