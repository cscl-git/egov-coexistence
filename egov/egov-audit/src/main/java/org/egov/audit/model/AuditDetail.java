package org.egov.audit.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import org.egov.audit.entity.AuditCheckList;
import org.egov.audit.entity.AuditDetails;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.microservice.models.Department;
import org.egov.model.bills.DocumentUpload;

public class AuditDetail {
	
	private Date auditScheduledDate;
	private Date auditCompletedDate;
	private String leadAuditorName;
	private Long leadAuditorEmpNo;
	private String naration;
	private String auditNumber;
	private String auditType;
	private List<DocumentUpload> documentDetail = new ArrayList<>();
	private List<AuditCheckList> checkList=new ArrayList<AuditCheckList>();
	private String approvalDepartment;
    private String approvalComent;
    private User approver;
    private Date approvedOn;
    private Long billId;
    private Long stateId;
    private Long auditId;
    private String auditStatus;
    private String workFlowAction;
    private String expenditureType;
    private int fund;
    private Date billFrom;
    private Date billTo;
    private List<PostAuditResult> postAuditResultList=new ArrayList<PostAuditResult>();
    private int counter = 0;
    private List<AuditBillDetails> auditBillDetails=new ArrayList<AuditBillDetails>();
    private List<AuditDetails> auditSearchList= new ArrayList<AuditDetails>();
    private String department="";
    private List<Department> departments = new ArrayList<Department>();
    private List<AuditEmployee> auditEmployees= new ArrayList<AuditEmployee>();
	private String notes;
	private Integer passUnderobjection=0;
	private String type;
	
	public Date getAuditScheduledDate() {
		return auditScheduledDate;
	}
	public void setAuditScheduledDate(Date auditScheduledDate) {
		this.auditScheduledDate = auditScheduledDate;
	}
	public Date getAuditCompletedDate() {
		return auditCompletedDate;
	}
	public void setAuditCompletedDate(Date auditCompletedDate) {
		this.auditCompletedDate = auditCompletedDate;
	}
	public String getLeadAuditorName() {
		return leadAuditorName;
	}
	public void setLeadAuditorName(String leadAuditorName) {
		this.leadAuditorName = leadAuditorName;
	}
	public Long getLeadAuditorEmpNo() {
		return leadAuditorEmpNo;
	}
	public void setLeadAuditorEmpNo(Long leadAuditorEmpNo) {
		this.leadAuditorEmpNo = leadAuditorEmpNo;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getNaration() {
		return naration;
	}
	public void setNaration(String naration) {
		this.naration = naration;
	}
	public String getAuditNumber() {
		return auditNumber;
	}
	public void setAuditNumber(String auditNumber) {
		this.auditNumber = auditNumber;
	}
	public String getAuditType() {
		return auditType;
	}
	public void setAuditType(String auditType) {
		this.auditType = auditType;
	}
	public List<DocumentUpload> getDocumentDetail() {
		return documentDetail;
	}
	public void setDocumentDetail(List<DocumentUpload> documentDetail) {
		this.documentDetail = documentDetail;
	}
	public List<AuditCheckList> getCheckList() {
		return checkList;
	}
	public void setCheckList(List<AuditCheckList> checkList) {
		this.checkList = checkList;
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
	public Long getBillId() {
		return billId;
	}
	public void setBillId(Long billId) {
		this.billId = billId;
	}
	public Long getAuditId() {
		return auditId;
	}
	public void setAuditId(Long auditId) {
		this.auditId = auditId;
	}
	public String getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}
	public String getWorkFlowAction() {
		return workFlowAction;
	}
	public void setWorkFlowAction(String workFlowAction) {
		this.workFlowAction = workFlowAction;
	}
	public String getExpenditureType() {
		return expenditureType;
	}
	public void setExpenditureType(String expenditureType) {
		this.expenditureType = expenditureType;
	}
	public Date getBillFrom() {
		return billFrom;
	}
	public void setBillFrom(Date billFrom) {
		this.billFrom = billFrom;
	}
	public Date getBillTo() {
		return billTo;
	}
	public void setBillTo(Date billTo) {
		this.billTo = billTo;
	}
	public int getFund() {
		return fund;
	}
	public void setFund(int fund) {
		this.fund = fund;
	}
	public List<PostAuditResult> getPostAuditResultList() {
		return postAuditResultList;
	}
	public void setPostAuditResultList(List<PostAuditResult> postAuditResultList) {
		this.postAuditResultList = postAuditResultList;
	}
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	public List<AuditBillDetails> getAuditBillDetails() {
		return auditBillDetails;
	}
	public void setAuditBillDetails(List<AuditBillDetails> auditBillDetails) {
		this.auditBillDetails = auditBillDetails;
	}
	public List<AuditDetails> getAuditSearchList() {
		return auditSearchList;
	}
	public void setAuditSearchList(List<AuditDetails> auditSearchList) {
		this.auditSearchList = auditSearchList;
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
	public List<AuditEmployee> getAuditEmployees() {
		return auditEmployees;
	}
	public void setAuditEmployees(List<AuditEmployee> auditEmployees) {
		this.auditEmployees = auditEmployees;
	}
	public Long getStateId() {
		return stateId;
	}
	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}
	public Integer getPassUnderobjection() {
		return passUnderobjection;
	}
	public void setPassUnderobjection(Integer passUnderobjection) {
		this.passUnderobjection = passUnderobjection;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	

}
