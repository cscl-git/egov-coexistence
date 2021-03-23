package org.egov.works.boq.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.egov.works.estimatepreparationapproval.entity.DNITCreation;

@Entity
@Table(name="txn_BoqDetail_Master")
@SequenceGenerator(name = BoqNewDetails.SEQ_BOQ_MASTER, sequenceName = BoqNewDetails.SEQ_BOQ_MASTER, allocationSize = 1)
public class BoqNewDetails implements Serializable {
	

	private static final long serialVersionUID = 1L;

	public static final String SEQ_BOQ_MASTER = "SEQ_BOQ_TXN_MASTER";
	@Id
	@GeneratedValue(generator = SEQ_BOQ_MASTER, strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Long id;

	@Column(name = "item_description")
	private String item_description;

	@Column(name = "ref_dsr")
	private String ref_dsr;

	@Column(name = "unit")
	private String unit;
	
	@Column(name = "rate")
	private Double rate;
	
	@Transient
	private List<BoqNewDetails> estimateList;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public List<BoqNewDetails> getEstimateList() {
		return estimateList;
	}

	public void setEstimateList(List<BoqNewDetails> estimateList) {
		this.estimateList = estimateList;
	}
}
