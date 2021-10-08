package org.egov.apnimandi.transactions.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.egov.infra.persistence.entity.AbstractAuditable;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "EGAM_COLLECTION_AMOUNT_DETAILS")
@SequenceGenerator(name = ApnimandiCollectionAmountDetails.SEQ_COLLECTION_AMOUNT_DETAILS, sequenceName = ApnimandiCollectionAmountDetails.SEQ_COLLECTION_AMOUNT_DETAILS, allocationSize = 1)
@AuditOverrides({ @AuditOverride(forClass = AbstractAuditable.class, name = "lastModifiedBy"),
        @AuditOverride(forClass = AbstractAuditable.class, name = "lastModifiedDate") })
public class ApnimandiCollectionAmountDetails extends AbstractAuditable{
	private static final long serialVersionUID = 796823780349590496L;
    public static final String SEQ_COLLECTION_AMOUNT_DETAILS = "SEQ_EGAM_COLLECTION_AMOUNT_DETAILS";
    
    @Id
    @GeneratedValue(generator = SEQ_COLLECTION_AMOUNT_DETAILS, strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "collectiondetails", nullable = false)
    @Audited
    private ApnimandiCollectionDetails apnimandicollectiondetails;
    
    @NotNull
    @Audited
    private String glCodeIdDetail;
    
    @NotNull
    @Audited
    private String accountHead;
    
    @NotNull
    @Audited
    private String amountType;
    
    @NotNull
    @Audited
    private double creditAmountDetail;
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    protected void setId(final Long id) {
        this.id = id;
    }

    public ApnimandiCollectionDetails getApnimandicollectiondetails() {
		return apnimandicollectiondetails;
	}

	public void setApnimandicollectiondetails(ApnimandiCollectionDetails apnimandicollectiondetails) {
		this.apnimandicollectiondetails = apnimandicollectiondetails;
	}

	public String getGlCodeIdDetail() {
		return glCodeIdDetail;
	}

	public void setGlCodeIdDetail(String glCodeIdDetail) {
		this.glCodeIdDetail = glCodeIdDetail;
	}

	public String getAccountHead() {
		return accountHead;
	}

	public void setAccountHead(String accountHead) {
		this.accountHead = accountHead;
	}

	public String getAmountType() {
		return amountType;
	}

	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}

	public double getCreditAmountDetail() {
		return creditAmountDetail;
	}

	public void setCreditAmountDetail(double creditAmountDetail) {
		this.creditAmountDetail = creditAmountDetail;
	}
}
