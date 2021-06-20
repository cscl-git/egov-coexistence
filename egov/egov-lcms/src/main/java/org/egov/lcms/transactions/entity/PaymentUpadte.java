package org.egov.lcms.transactions.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="PaymentUpadte")
@SequenceGenerator(name = PaymentUpadte.SEQ_PAYMENT_UPDATE, sequenceName = PaymentUpadte.SEQ_PAYMENT_UPDATE, allocationSize = 1)
public class PaymentUpadte {
	
	 public static final String SEQ_PAYMENT_UPDATE = "SEQ_PAYMENT_UPDATE";

	@Id
	@GeneratedValue(generator = SEQ_PAYMENT_UPDATE, strategy = GenerationType.SEQUENCE)
	private Long id ;
	private String nameOfDefendingCounsil;
	private String caseFee;
	private String email;
	private String miscExpenses;
	private String phoneNo;
	private String status;
	private String modeOfPayment;
	private String amount;
	private String paymentOfIssuance;
	private String findingOfReplyCase;
	private String finalDisposal;
	private Long legalcaseid;
	private String legallcnumber;
	
	
	public String getLegallcnumber() {
		return legallcnumber;
	}
	public void setLegallcnumber(String legallcnumber) {
		this.legallcnumber = legallcnumber;
	}
	public String getNameOfDefendingCounsil() {
		return nameOfDefendingCounsil;
	}
	public String getCaseFee() {
		return caseFee;
	}
	public String getEmail() {
		return email;
	}
	public String getMiscExpenses() {
		return miscExpenses;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public String getStatus() {
		return status;
	}
	public String getModeOfPayment() {
		return modeOfPayment;
	}
	public String getAmount() {
		return amount;
	}
	public String getPaymentOfIssuance() {
		return paymentOfIssuance;
	}
	public String getFindingOfReplyCase() {
		return findingOfReplyCase;
	}
	public String getFinalDisposal() {
		return finalDisposal;
	}
	public void setNameOfDefendingCounsil(String nameOfDefendingCounsil) {
		this.nameOfDefendingCounsil = nameOfDefendingCounsil;
	}
	public void setCaseFee(String caseFee) {
		this.caseFee = caseFee;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setMiscExpenses(String miscExpenses) {
		this.miscExpenses = miscExpenses;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public void setPaymentOfIssuance(String paymentOfIssuance) {
		this.paymentOfIssuance = paymentOfIssuance;
	}
	public void setFindingOfReplyCase(String findingOfReplyCase) {
		this.findingOfReplyCase = findingOfReplyCase;
	}
	public void setFinalDisposal(String finalDisposal) {
		this.finalDisposal = finalDisposal;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getLegalcaseid() {
		return legalcaseid;
	}
	public void setLegalcaseid(Long legalcaseid) {
		this.legalcaseid = legalcaseid;
	}
	
	
	

}
