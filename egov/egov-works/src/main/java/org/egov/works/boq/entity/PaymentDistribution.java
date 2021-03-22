package org.egov.works.boq.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "txn_paymentdistribution")
@SequenceGenerator(name = PaymentDistribution.SEQ_PAY_DIST, sequenceName = PaymentDistribution.SEQ_PAY_DIST, allocationSize = 1)
public class PaymentDistribution implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5880334466832286487L;
	public static final String SEQ_PAY_DIST = "SEQ_PAY_DIST";
	
	
	@Id
	@GeneratedValue(generator = SEQ_PAY_DIST, strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "payment_desc")
	private String payment_desc;

	@Column(name = "payment_percent")
	private Integer payment_percent;

	@Column(name = "amount")
	private BigDecimal amount;
	
	
	@ManyToOne
	@JoinColumn(name = "work_id")
	private WorkOrderAgreement workOrderAgreement;
	
	@Column(name = "payment_completed")
	private boolean payment_completed;
	
	@Column(name = "completion_percent")
	private Integer completion_percent;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPayment_desc() {
		return payment_desc;
	}

	public void setPayment_desc(String payment_desc) {
		this.payment_desc = payment_desc;
	}

	public Integer getPayment_percent() {
		return payment_percent;
	}

	public void setPayment_percent(Integer payment_percent) {
		this.payment_percent = payment_percent;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public WorkOrderAgreement getWorkOrderAgreement() {
		return workOrderAgreement;
	}

	public void setWorkOrderAgreement(WorkOrderAgreement workOrderAgreement) {
		this.workOrderAgreement = workOrderAgreement;
	}

	public boolean isPayment_completed() {
		return payment_completed;
	}

	public void setPayment_completed(boolean payment_completed) {
		this.payment_completed = payment_completed;
	}

	public Integer getCompletion_percent() {
		return completion_percent;
	}

	public void setCompletion_percent(Integer completion_percent) {
		this.completion_percent = completion_percent;
	}

	
	
	
	
	

}
