package org.egov.infra.microservice.models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "MIS_REMITTANCE_DETAILS")
@SequenceGenerator(name = MisRemittanceDetails.SEQ_MIS_REMITTANCE_DETAILS, sequenceName = MisRemittanceDetails.SEQ_MIS_REMITTANCE_DETAILS, allocationSize = 1)
	public class MisRemittanceDetails {
	
	public static final String SEQ_MIS_REMITTANCE_DETAILS = "SEQ_MIS_REMITTANCE_DETAILS";
	
	@Id
	    @GeneratedValue(generator = SEQ_MIS_REMITTANCE_DETAILS, strategy = GenerationType.SEQUENCE)
	private Long id;
	private Long mis_receipt_id;
	private String voucher_number;
	private Date voucher_date;
	private String bankaccount;
	private BigDecimal amount;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMis_receipt_id() {
		return mis_receipt_id;
	}
	public void setMis_receipt_id(Long mis_receipt_id) {
		this.mis_receipt_id = mis_receipt_id;
	}
	
	public String getVoucher_number() {
		return voucher_number;
	}
	public void setVoucher_number(String voucher_number) {
		this.voucher_number = voucher_number;
	}
	public Date getVoucher_date() {
		return voucher_date;
	}
	public void setVoucher_date(Date voucher_date) {
		this.voucher_date = voucher_date;
	}
	public String getBankaccount() {
		return bankaccount;
	}
	public void setBankaccount(String bankaccount) {
		this.bankaccount = bankaccount;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	
}
