package org.egov.works.boq.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "txn_work_agreement")
@SequenceGenerator(name = WorkOrderAgreement.SEQ_WORK_ORDER_AGREEMENT, sequenceName = WorkOrderAgreement.SEQ_WORK_ORDER_AGREEMENT, allocationSize = 1)
public class WorkOrderAgreement implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String SEQ_WORK_ORDER_AGREEMENT = "SEQ_WORK_ORDER_AGREEMENT";

	@Id
	@GeneratedValue(generator = SEQ_WORK_ORDER_AGREEMENT, strategy = GenerationType.SEQUENCE)
	@Column(name = "work_id")
	private Long work_id;

	@Column(name = "name_work_order")
	private String name_work_order;

	@Column(name = "work_number")
	private String work_number;

	@Column(name = "work_start_date")
	private String work_start_date;

	@Column(name = "work_intended_date")
	private String work_intended_date;

	@Column(name = "work_end_date")
	private String work_end_date;

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
	private String date;

	@Column(name = "time_limit")
	private String timeLimit;

	@Column(name = "category")
	private String category;

	@Transient
	private List<BoQDetails> boQDetailsList;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "workOrderAgreement", targetEntity = BoQDetails.class)
	private List<BoQDetails> newBoQDetailsList;

	public Long getWork_id() {
		return work_id;
	}

	public void setWork_id(Long work_id) {
		this.work_id = work_id;
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

	public String getWork_start_date() {
		return work_start_date;
	}

	public void setWork_start_date(String work_start_date) {
		this.work_start_date = work_start_date;
	}

	public String getWork_intended_date() {
		return work_intended_date;
	}

	public void setWork_intended_date(String work_intended_date) {
		this.work_intended_date = work_intended_date;
	}

	public String getWork_end_date() {
		return work_end_date;
	}

	public void setWork_end_date(String work_end_date) {
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

	public List<BoQDetails> getBoQDetailsList() {
		return boQDetailsList;
	}

	public void setBoQDetailsList(List<BoQDetails> boQDetailsList) {
		this.boQDetailsList = boQDetailsList;
	}

	public List<BoQDetails> getNewBoQDetailsList() {
		return newBoQDetailsList;
	}

	public void setNewBoQDetailsList(List<BoQDetails> newBoQDetailsList) {
		this.newBoQDetailsList = newBoQDetailsList;
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
