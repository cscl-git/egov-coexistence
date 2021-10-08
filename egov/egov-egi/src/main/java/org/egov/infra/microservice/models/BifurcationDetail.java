package org.egov.infra.microservice.models;

public class BifurcationDetail {

	private String receiptNumber;
	private String glcode;
	private String creditAmount;
	private String debitAMount;
	
	public String getReceiptNumber() {
		return receiptNumber;
	}
	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}
	public String getGlcode() {
		return glcode;
	}
	public void setGlcode(String glcode) {
		this.glcode = glcode;
	}
	public String getCreditAmount() {
		return creditAmount;
	}
	public void setCreditAmount(String creditAmount) {
		this.creditAmount = creditAmount;
	}
	public String getDebitAMount() {
		return debitAMount;
	}
	public void setDebitAMount(String debitAMount) {
		this.debitAMount = debitAMount;
	}
}
