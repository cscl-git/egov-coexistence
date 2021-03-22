package org.egov.works.estimatepreparationapproval.entity;

import java.math.BigDecimal;
import java.util.Date;

public class EstimatePreparationApprovalRESTPOJO {
	private Long id;
	private String agency_work_order;
	private Date date;
	private Double estimate_amount;
	private String estimate_date;
	private String estimate_number;
	private String estimate_percentage;
	private String estimate_prepared_by;
	private Long executing_division;
	private String financial_year;
	private Long financing_details;
	private String fund_source;
	private String necessity;
	private Long preparation_designation;
	private String sector_number;
	private String tender_cost;
	private String time_limit;
	private String ward_number;
	private String work_category;
	private String work_name;
	private String work_scope;
	private String work_type;
	private Long state_id;
	private Long version;
	private Long statusid;
	private String createddate;
	private String lastmodifieddate;
	private String aanumber;
	private Date aadate;
	private Double contingent_percentage;
	private BigDecimal contingent_amount;
	private BigDecimal consultant_fee;
	private BigDecimal unforseen_charges;
	private String expenditure_head;
	private String expenditure_sub_category;
	private String expenditure_category;
	private String meetnumber;
	private String meetcategory;
	private Date meetdate;
	private String expenditure_head_est;
	private String status;
	public EstimatePreparationApprovalRESTPOJO(Long id, String agency_work_order, Date date, Double estimate_amount,
			String estimate_date, String estimate_number, String estimate_percentage, String estimate_prepared_by,
			Long executing_division, String financial_year, Long financing_details, String fund_source,
			String necessity, Long preparation_designation, String sector_number, String tender_cost, String time_limit,
			String ward_number, String work_category, String work_name, String work_scope, String work_type,
			Long state_id, Long version, Long statusid, String createddate, String lastmodifieddate, String aanumber,
			Date aadate, Double contingent_percentage, BigDecimal contingent_amount, BigDecimal consultant_fee,
			BigDecimal unforseen_charges, String expenditure_head, String expenditure_sub_category,
			String expenditure_category, String meetnumber, String meetcategory, Date meetdate,
			String expenditure_head_est, String status) {
		super();
		this.id = id;
		this.agency_work_order = agency_work_order;
		this.date = date;
		this.estimate_amount = estimate_amount;
		this.estimate_date = estimate_date;
		this.estimate_number = estimate_number;
		this.estimate_percentage = estimate_percentage;
		this.estimate_prepared_by = estimate_prepared_by;
		this.executing_division = executing_division;
		this.financial_year = financial_year;
		this.financing_details = financing_details;
		this.fund_source = fund_source;
		this.necessity = necessity;
		this.preparation_designation = preparation_designation;
		this.sector_number = sector_number;
		this.tender_cost = tender_cost;
		this.time_limit = time_limit;
		this.ward_number = ward_number;
		this.work_category = work_category;
		this.work_name = work_name;
		this.work_scope = work_scope;
		this.work_type = work_type;
		this.state_id = state_id;
		this.version = version;
		this.statusid = statusid;
		this.createddate = createddate;
		this.lastmodifieddate = lastmodifieddate;
		this.aanumber = aanumber;
		this.aadate = aadate;
		this.contingent_percentage = contingent_percentage;
		this.contingent_amount = contingent_amount;
		this.consultant_fee = consultant_fee;
		this.unforseen_charges = unforseen_charges;
		this.expenditure_head = expenditure_head;
		this.expenditure_sub_category = expenditure_sub_category;
		this.expenditure_category = expenditure_category;
		this.meetnumber = meetnumber;
		this.meetcategory = meetcategory;
		this.meetdate = meetdate;
		this.expenditure_head_est = expenditure_head_est;
		this.status = status;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAgency_work_order() {
		return agency_work_order;
	}
	public void setAgency_work_order(String agency_work_order) {
		this.agency_work_order = agency_work_order;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Double getEstimate_amount() {
		return estimate_amount;
	}
	public void setEstimate_amount(Double estimate_amount) {
		this.estimate_amount = estimate_amount;
	}
	public String getEstimate_date() {
		return estimate_date;
	}
	public void setEstimate_date(String estimate_date) {
		this.estimate_date = estimate_date;
	}
	public String getEstimate_number() {
		return estimate_number;
	}
	public void setEstimate_number(String estimate_number) {
		this.estimate_number = estimate_number;
	}
	public String getEstimate_percentage() {
		return estimate_percentage;
	}
	public void setEstimate_percentage(String estimate_percentage) {
		this.estimate_percentage = estimate_percentage;
	}
	public String getEstimate_prepared_by() {
		return estimate_prepared_by;
	}
	public void setEstimate_prepared_by(String estimate_prepared_by) {
		this.estimate_prepared_by = estimate_prepared_by;
	}
	public Long getExecuting_division() {
		return executing_division;
	}
	public void setExecuting_division(Long executing_division) {
		this.executing_division = executing_division;
	}
	public String getFinancial_year() {
		return financial_year;
	}
	public void setFinancial_year(String financial_year) {
		this.financial_year = financial_year;
	}
	public Long getFinancing_details() {
		return financing_details;
	}
	public void setFinancing_details(Long financing_details) {
		this.financing_details = financing_details;
	}
	public String getFund_source() {
		return fund_source;
	}
	public void setFund_source(String fund_source) {
		this.fund_source = fund_source;
	}
	public String getNecessity() {
		return necessity;
	}
	public void setNecessity(String necessity) {
		this.necessity = necessity;
	}
	public Long getPreparation_designation() {
		return preparation_designation;
	}
	public void setPreparation_designation(Long preparation_designation) {
		this.preparation_designation = preparation_designation;
	}
	public String getSector_number() {
		return sector_number;
	}
	public void setSector_number(String sector_number) {
		this.sector_number = sector_number;
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
	public String getWard_number() {
		return ward_number;
	}
	public void setWard_number(String ward_number) {
		this.ward_number = ward_number;
	}
	public String getWork_category() {
		return work_category;
	}
	public void setWork_category(String work_category) {
		this.work_category = work_category;
	}
	public String getWork_name() {
		return work_name;
	}
	public void setWork_name(String work_name) {
		this.work_name = work_name;
	}
	public String getWork_scope() {
		return work_scope;
	}
	public void setWork_scope(String work_scope) {
		this.work_scope = work_scope;
	}
	public String getWork_type() {
		return work_type;
	}
	public void setWork_type(String work_type) {
		this.work_type = work_type;
	}
	public Long getState_id() {
		return state_id;
	}
	public void setState_id(Long state_id) {
		this.state_id = state_id;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	public Long getStatusid() {
		return statusid;
	}
	public void setStatusid(Long statusid) {
		this.statusid = statusid;
	}
	public String getCreateddate() {
		return createddate;
	}
	public void setCreateddate(String createddate) {
		this.createddate = createddate;
	}
	public String getLastmodifieddate() {
		return lastmodifieddate;
	}
	public void setLastmodifieddate(String lastmodifieddate) {
		this.lastmodifieddate = lastmodifieddate;
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
	public Double getContingent_percentage() {
		return contingent_percentage;
	}
	public void setContingent_percentage(Double contingent_percentage) {
		this.contingent_percentage = contingent_percentage;
	}
	public BigDecimal getContingent_amount() {
		return contingent_amount;
	}
	public void setContingent_amount(BigDecimal contingent_amount) {
		this.contingent_amount = contingent_amount;
	}
	public BigDecimal getConsultant_fee() {
		return consultant_fee;
	}
	public void setConsultant_fee(BigDecimal consultant_fee) {
		this.consultant_fee = consultant_fee;
	}
	public BigDecimal getUnforseen_charges() {
		return unforseen_charges;
	}
	public void setUnforseen_charges(BigDecimal unforseen_charges) {
		this.unforseen_charges = unforseen_charges;
	}
	public String getExpenditure_head() {
		return expenditure_head;
	}
	public void setExpenditure_head(String expenditure_head) {
		this.expenditure_head = expenditure_head;
	}
	public String getExpenditure_sub_category() {
		return expenditure_sub_category;
	}
	public void setExpenditure_sub_category(String expenditure_sub_category) {
		this.expenditure_sub_category = expenditure_sub_category;
	}
	public String getExpenditure_category() {
		return expenditure_category;
	}
	public void setExpenditure_category(String expenditure_category) {
		this.expenditure_category = expenditure_category;
	}
	public String getMeetnumber() {
		return meetnumber;
	}
	public void setMeetnumber(String meetnumber) {
		this.meetnumber = meetnumber;
	}
	public String getMeetcategory() {
		return meetcategory;
	}
	public void setMeetcategory(String meetcategory) {
		this.meetcategory = meetcategory;
	}
	public Date getMeetdate() {
		return meetdate;
	}
	public void setMeetdate(Date meetdate) {
		this.meetdate = meetdate;
	}
	public String getExpenditure_head_est() {
		return expenditure_head_est;
	}
	public void setExpenditure_head_est(String expenditure_head_est) {
		this.expenditure_head_est = expenditure_head_est;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
	

}
