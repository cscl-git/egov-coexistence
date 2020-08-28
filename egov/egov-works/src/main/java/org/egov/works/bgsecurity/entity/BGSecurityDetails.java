package org.egov.works.bgsecurity.entity;

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
@Table(name = "txn_BGSecurityDetails")
@SequenceGenerator(name = BGSecurityDetails.SEQ_BGSECURITY, sequenceName = BGSecurityDetails.SEQ_BGSECURITY, allocationSize = 1)
public class BGSecurityDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String SEQ_BGSECURITY = "SEQ_BGSECURITY";

	@Id
	@GeneratedValue(generator = SEQ_BGSECURITY, strategy = GenerationType.SEQUENCE)
	@Column(name = "security_no")
	private Integer security_no;

	@Column(name = "security_validity")
	private String security_validity;

	@Column(name = "security_amount")
	private Double security_amount;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Kolkata")
	@Column(name = "security_start_date")
	private Date security_start_date;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Kolkata")
	@Column(name = "security_end_date")
	private Date security_end_date;

	@Column(name = "loa_number")
	private Integer loa_number;

	@Transient
	private String start_dt;

	@Transient
	private String end_dt;

	public Integer getSecurity_no() {
		return security_no;
	}

	public void setSecurity_no(Integer security_no) {
		this.security_no = security_no;
	}

	public String getSecurity_validity() {
		return security_validity;
	}

	public void setSecurity_validity(String security_validity) {
		this.security_validity = security_validity;
	}

	public Double getSecurity_amount() {
		return security_amount;
	}

	public void setSecurity_amount(Double security_amount) {
		this.security_amount = security_amount;
	}

	public Date getSecurity_start_date() {
		return security_start_date;
	}

	public void setSecurity_start_date(Date security_start_date) {
		this.security_start_date = security_start_date;
	}

	public Date getSecurity_end_date() {
		return security_end_date;
	}

	public void setSecurity_end_date(Date security_end_date) {
		this.security_end_date = security_end_date;
	}

	public Integer getLoa_number() {
		return loa_number;
	}

	public void setLoa_number(Integer loa_number) {
		this.loa_number = loa_number;
	}

	public String getStart_dt() {
		return start_dt;
	}

	public void setStart_dt(String start_dt) {
		this.start_dt = start_dt;
	}

	public String getEnd_dt() {
		return end_dt;
	}

	public void setEnd_dt(String end_dt) {
		this.end_dt = end_dt;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
