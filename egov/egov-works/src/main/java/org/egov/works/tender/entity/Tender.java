package org.egov.works.tender.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "txn_tender")
@SequenceGenerator(name = Tender.SEQ_TENDER, sequenceName = Tender.SEQ_TENDER, allocationSize = 1)

public class Tender implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String SEQ_TENDER = "SEQ_TENDER";

	@Id
	@GeneratedValue(generator = SEQ_TENDER, strategy = GenerationType.SEQUENCE)
	@Column(name = "procurement_no")
	private Long procurement_no;

	@Column(name = "procurement_amount")
	private Double procurement_amount;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Kolkata")
	@Column(name = "procurement_date")
	private Date procurement_date;

	@Column(name = "contractor_details")
	private String contractor_details;

	@Column(name = "loa_number")
	private Double loa_number;

	@Transient
	private String procurement_dt;

	public Long getProcurement_no() {
		return procurement_no;
	}

	public void setProcurement_no(Long procurement_no) {
		this.procurement_no = procurement_no;
	}

	public Double getProcurement_amount() {
		return procurement_amount;
	}

	public void setProcurement_amount(Double procurement_amount) {
		this.procurement_amount = procurement_amount;
	}

	public Date getProcurement_date() {
		return procurement_date;
	}

	public void setProcurement_date(Date procurement_date) {
		this.procurement_date = procurement_date;
	}

	public String getContractor_details() {
		return contractor_details;
	}

	public void setContractor_details(String contractor_details) {
		this.contractor_details = contractor_details;
	}

	public Double getLoa_number() {
		return loa_number;
	}

	public void setLoa_number(Double loa_number) {
		this.loa_number = loa_number;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getProcurement_dt() {
		return procurement_dt;
	}

	public void setProcurement_dt(String procurement_dt) {
		this.procurement_dt = procurement_dt;
	}

}
