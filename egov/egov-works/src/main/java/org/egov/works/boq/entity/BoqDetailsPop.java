package org.egov.works.boq.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="txn_boqdetail_master")
@SequenceGenerator(name = BoqDetailsPop.SEQ_BOQDTL_MASTER, sequenceName = BoqDetailsPop.SEQ_BOQDTL_MASTER, allocationSize = 1)
public class BoqDetailsPop {

	private static final long serialVersionUID = 1L;

	public static final String SEQ_BOQDTL_MASTER = "SEQ_BOQ_TXN_MASTER";
	@Id
	@GeneratedValue(generator = SEQ_BOQDTL_MASTER, strategy = GenerationType.SEQUENCE)
	private Long id;
	private String item_description;
	private String ref_dsr;
	private String unit;
	private int rate;
	
	
	public Long getId() {
		return id;
	}
	public String getItem_description() {
		return item_description;
	}
	public String getRef_dsr() {
		return ref_dsr;
	}
	public String getUnit() {
		return unit;
	}
	public int getRate() {
		return rate;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setItem_description(String item_description) {
		this.item_description = item_description;
	}
	public void setRef_dsr(String ref_dsr) {
		this.ref_dsr = ref_dsr;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public void setRate(int rate) {
		this.rate = rate;
	}
	
	
	
	
}

