package org.egov.works.boq.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;

@Entity
@Table(name = "txn_BoQDetails")
@SequenceGenerator(name = BoQDetails.SEQ_BOQ_DETAILS, sequenceName = BoQDetails.SEQ_BOQ_DETAILS, allocationSize = 1)

public class BoQDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String SEQ_BOQ_DETAILS = "SEQ_BOQ_DETAILS";

	@Id
	@GeneratedValue(generator = SEQ_BOQ_DETAILS, strategy = GenerationType.SEQUENCE)
	@Column(name = "sl_no")
	private Long slNo;

	@Column(name = "item_description")
	private String item_description;

	@Column(name = "ref_dsr")
	private String ref_dsr;

	@Column(name = "unit")
	private Double unit;

	@Column(name = "rate")
	private Double rate;

	@Column(name = "quantity")
	private Double quantity;

	@Column(name = "amount")
	private Double amount;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "work_id", nullable = true, updatable = true, insertable = true)
	private WorkOrderAgreement workOrderAgreement;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "estimate_preparation_id", nullable = true, updatable = true, insertable = true)
	private EstimatePreparationApproval estimatePreparationApproval;

	@Transient
	private boolean checkboxChecked;

	public Long getSlNo() {
		return slNo;
	}

	public void setSlNo(Long slNo) {
		this.slNo = slNo;
	}

	public String getItem_description() {
		return item_description;
	}

	public void setItem_description(String item_description) {
		this.item_description = item_description;
	}

	public String getRef_dsr() {
		return ref_dsr;
	}

	public void setRef_dsr(String ref_dsr) {
		this.ref_dsr = ref_dsr;
	}

	public Double getUnit() {
		return unit;
	}

	public void setUnit(Double unit) {
		this.unit = unit;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public WorkOrderAgreement getWorkOrderAgreement() {
		return workOrderAgreement;
	}

	public void setWorkOrderAgreement(WorkOrderAgreement workOrderAgreement) {
		this.workOrderAgreement = workOrderAgreement;
	}

	public EstimatePreparationApproval getEstimatePreparationApproval() {
		return estimatePreparationApproval;
	}

	public void setEstimatePreparationApproval(EstimatePreparationApproval estimatePreparationApproval) {
		this.estimatePreparationApproval = estimatePreparationApproval;
	}

	public boolean isCheckboxChecked() {
		return checkboxChecked;
	}

	public void setCheckboxChecked(boolean checkboxChecked) {
		this.checkboxChecked = checkboxChecked;
	}

}
