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

@Entity
@Table(name = "txn_bgsecurity_details")
@SequenceGenerator(name = BGSecurityDetails.SEQ_BGSECURITY_DETAILS, sequenceName = BGSecurityDetails.SEQ_BGSECURITY_DETAILS, allocationSize = 1)
public class BGSecurityDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String SEQ_BGSECURITY_DETAILS = "SEQ_BGSECURITY_DETAILS";

	@Id
	@GeneratedValue(generator = SEQ_BGSECURITY_DETAILS, strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Long id;

	@Column(name = "security_validity")
	private String security_validity;

	@Column(name = "security_amount")
	private Double security_amount;

	@Column(name = "security_start_date")
	private Date security_start_date;

	@Column(name = "security_end_date")
	private Date security_end_date;

	@Column(name = "loa_number")
	private String loa_number;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getLoa_number() {
		return loa_number;
	}

	public void setLoa_number(String loa_number) {
		this.loa_number = loa_number;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
