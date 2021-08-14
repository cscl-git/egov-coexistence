package org.egov.infra.microservice.models;

import java.math.BigDecimal;

public class MisRemittancePOJO {
	
	private Long id;
	private Long mis_receipt_id;
	private String voucherNumber;
	private String voucherDate;
	private String bankaccount;
	private String amount;
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
	public String getVoucherNumber() {
		return voucherNumber;
	}
	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}
	public String getVoucherDate() {
		return voucherDate;
	}
	public void setVoucherDate(String voucherDate) {
		this.voucherDate = voucherDate;
	}
	public String getBankaccount() {
		return bankaccount;
	}
	public void setBankaccount(String bankaccount) {
		this.bankaccount = bankaccount;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
}
