package org.egov.works.boq.entity;

public class WorkOrderAgreementRESTPOJO {
	private String id;
	private String agency_work_order;
	private String agreement_details;
	private String category;
	private String contractor_address;
	private String contractor_code;
	private String contractor_email;
	private String contractor_name;
	private String contractor_phone;
	private String date;
	private String estimated_cost;
	private String executing_department;
	private String fund;
	private String name_work_order;
	private String sector;
	private String tender_cost;
	private String time_limit;
	private String work_location;
	private String work_type;
	private String work_agreement_status;
	private String work_amount;
	private String work_details;
	private String work_end_date;
	private String work_intended_date;
	private String work_number;
	private String work_start_date;
	private String work_status;
	private String ward_number;
	private String statusid;
	private String version;
	private String createdby;
	private String createddate;
	private String lastmodifiedby;
	private String lastmodifieddate;
	private String state_id;
	private String work_agreement_number;
	private String project_closure_comments;
	private String contractor_performance_comments;
	private String actual_start_date;
	private String actual_end_date;
	private String approval_competent_authority;
	private String status;
	private String percentage_completion;
	private String subdivision;
	
	
	public WorkOrderAgreementRESTPOJO() {}
	
	
	
	
	public WorkOrderAgreementRESTPOJO(String id, String agency_work_order, String agreement_details, String category,
			String contractor_address, String contractor_code, String contractor_email, String contractor_name,
			String contractor_phone, String date, String estimated_cost, String executing_department, String fund,
			String name_work_order, String sector, String tender_cost, String time_limit, String work_location,
			String work_type, String work_agreement_status, String work_amount, String work_details,
			String work_end_date, String work_intended_date, String work_number, String work_start_date,
			String work_status, String ward_number, String statusid, String version, String createdby,
			String createddate, String lastmodifiedby, String lastmodifieddate, String state_id,
			String work_agreement_number, String project_closure_comments, String contractor_performance_comments,
			String actual_start_date, String actual_end_date, String approval_competent_authority, String status,
			String percentage_completion,String subdivision) {
		super();
		this.id = id;
		this.agency_work_order = agency_work_order;
		this.agreement_details = agreement_details;
		this.category = category;
		this.contractor_address = contractor_address;
		this.contractor_code = contractor_code;
		this.contractor_email = contractor_email;
		this.contractor_name = contractor_name;
		this.contractor_phone = contractor_phone;
		this.date = date;
		this.estimated_cost = estimated_cost;
		this.executing_department = executing_department;
		this.fund = fund;
		this.name_work_order = name_work_order;
		this.sector = sector;
		this.tender_cost = tender_cost;
		this.time_limit = time_limit;
		this.work_location = work_location;
		this.work_type = work_type;
		this.work_agreement_status = work_agreement_status;
		this.work_amount = work_amount;
		this.work_details = work_details;
		this.work_end_date = work_end_date;
		this.work_intended_date = work_intended_date;
		this.work_number = work_number;
		this.work_start_date = work_start_date;
		this.work_status = work_status;
		this.ward_number = ward_number;
		this.statusid = statusid;
		this.version = version;
		this.createdby = createdby;
		this.createddate = createddate;
		this.lastmodifiedby = lastmodifiedby;
		this.lastmodifieddate = lastmodifieddate;
		this.state_id = state_id;
		this.work_agreement_number = work_agreement_number;
		this.project_closure_comments = project_closure_comments;
		this.contractor_performance_comments = contractor_performance_comments;
		this.actual_start_date = actual_start_date;
		this.actual_end_date = actual_end_date;
		this.approval_competent_authority = approval_competent_authority;
		this.status = status;
		this.percentage_completion = percentage_completion;
		this.subdivision=subdivision;
	}




	public String getSubdivision() {
		return subdivision;
	}




	public void setSubdivision(String subdivision) {
		this.subdivision = subdivision;
	}




	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAgency_work_order() {
		return agency_work_order;
	}
	public void setAgency_work_order(String agency_work_order) {
		this.agency_work_order = agency_work_order;
	}
	public String getAgreement_details() {
		return agreement_details;
	}
	public void setAgreement_details(String agreement_details) {
		this.agreement_details = agreement_details;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getContractor_address() {
		return contractor_address;
	}
	public void setContractor_address(String contractor_address) {
		this.contractor_address = contractor_address;
	}
	public String getContractor_code() {
		return contractor_code;
	}
	public void setContractor_code(String contractor_code) {
		this.contractor_code = contractor_code;
	}
	public String getContractor_email() {
		return contractor_email;
	}
	public void setContractor_email(String contractor_email) {
		this.contractor_email = contractor_email;
	}
	public String getContractor_name() {
		return contractor_name;
	}
	public void setContractor_name(String contractor_name) {
		this.contractor_name = contractor_name;
	}
	public String getContractor_phone() {
		return contractor_phone;
	}
	public void setContractor_phone(String contractor_phone) {
		this.contractor_phone = contractor_phone;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getEstimated_cost() {
		return estimated_cost;
	}
	public void setEstimated_cost(String estimated_cost) {
		this.estimated_cost = estimated_cost;
	}
	public String getExecuting_department() {
		return executing_department;
	}
	public void setExecuting_department(String executing_department) {
		this.executing_department = executing_department;
	}
	public String getFund() {
		return fund;
	}
	public void setFund(String fund) {
		this.fund = fund;
	}
	public String getName_work_order() {
		return name_work_order;
	}
	public void setName_work_order(String name_work_order) {
		this.name_work_order = name_work_order;
	}
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}
	public String getTender_cost() {
		return tender_cost;
	}
	public void setTender_cost(String tender_cost) {
		this.tender_cost = tender_cost;
	}
	public String getTime_limit() {
		return time_limit;
	}
	public void setTime_limit(String time_limit) {
		this.time_limit = time_limit;
	}
	public String getWork_location() {
		return work_location;
	}
	public void setWork_location(String work_location) {
		this.work_location = work_location;
	}
	public String getWork_type() {
		return work_type;
	}
	public void setWork_type(String work_type) {
		this.work_type = work_type;
	}
	public String getWork_agreement_status() {
		return work_agreement_status;
	}
	public void setWork_agreement_status(String work_agreement_status) {
		this.work_agreement_status = work_agreement_status;
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
	public String getWork_end_date() {
		return work_end_date;
	}
	public void setWork_end_date(String work_end_date) {
		this.work_end_date = work_end_date;
	}
	public String getWork_intended_date() {
		return work_intended_date;
	}
	public void setWork_intended_date(String work_intended_date) {
		this.work_intended_date = work_intended_date;
	}
	public String getWork_number() {
		return work_number;
	}
	public void setWork_number(String work_number) {
		this.work_number = work_number;
	}
	public String getWork_start_date() {
		return work_start_date;
	}
	public void setWork_start_date(String work_start_date) {
		this.work_start_date = work_start_date;
	}
	public String getWork_status() {
		return work_status;
	}
	public void setWork_status(String work_status) {
		this.work_status = work_status;
	}
	public String getWard_number() {
		return ward_number;
	}
	public void setWard_number(String ward_number) {
		this.ward_number = ward_number;
	}
	public String getStatusid() {
		return statusid;
	}
	public void setStatusid(String statusid) {
		this.statusid = statusid;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getCreatedby() {
		return createdby;
	}
	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}
	public String getCreateddate() {
		return createddate;
	}
	public void setCreateddate(String createddate) {
		this.createddate = createddate;
	}
	public String getLastmodifiedby() {
		return lastmodifiedby;
	}
	public void setLastmodifiedby(String lastmodifiedby) {
		this.lastmodifiedby = lastmodifiedby;
	}
	public String getLastmodifieddate() {
		return lastmodifieddate;
	}
	public void setLastmodifieddate(String lastmodifieddate) {
		this.lastmodifieddate = lastmodifieddate;
	}
	public String getState_id() {
		return state_id;
	}
	public void setState_id(String state_id) {
		this.state_id = state_id;
	}
	public String getWork_agreement_number() {
		return work_agreement_number;
	}
	public void setWork_agreement_number(String work_agreement_number) {
		this.work_agreement_number = work_agreement_number;
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
	public String getActual_start_date() {
		return actual_start_date;
	}
	public void setActual_start_date(String actual_start_date) {
		this.actual_start_date = actual_start_date;
	}
	public String getActual_end_date() {
		return actual_end_date;
	}
	public void setActual_end_date(String actual_end_date) {
		this.actual_end_date = actual_end_date;
	}
	public String getApproval_competent_authority() {
		return approval_competent_authority;
	}
	public void setApproval_competent_authority(String approval_competent_authority) {
		this.approval_competent_authority = approval_competent_authority;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}




	public String getPercentage_completion() {
		return percentage_completion;
	}




	public void setPercentage_completion(String percentage_completion) {
		this.percentage_completion = percentage_completion;
	}
	
	
	
	

}
