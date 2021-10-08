package org.egov.lcms.transactions.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang.StringUtils;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.lcms.masters.entity.GovernmentDepartment;
import org.egov.lcms.utils.constants.LcmsConstants;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

import com.google.gson.annotations.Expose;


@Entity
@Table(name = "EGLC_BIDEFENDINGCOUNSILDETAILS")
@SequenceGenerator(name = BidefendingCounsilDetails.SEQ_EGLC_BidefendingCounsilDetails, sequenceName = BidefendingCounsilDetails.SEQ_EGLC_BidefendingCounsilDetails, allocationSize = 1)
@AuditOverrides({@AuditOverride(forClass = AbstractAuditable.class, name = "lastModifiedBy"),
        @AuditOverride(forClass = AbstractAuditable.class, name = "lastModifiedDate")})
public class BidefendingCounsilDetails extends AbstractAuditable {


    public static final String SEQ_EGLC_BidefendingCounsilDetails = "SEQ_EGLC_BidefendingCounsilDetails";

    @Expose
    @Id
    @GeneratedValue(generator = SEQ_EGLC_BidefendingCounsilDetails, strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "legalcase")
    @Audited
    private LegalCase legalCase;
    
    @Column(name="defCounsilPrimary")
    private String defCounsilPrimary ;
    
    @Transient
    private String primaryCounsin;
    

    @NotNull
    @Length(max = 300)
    @Audited
    @Column(name="opppartyadvocate")
    private String oppPartyAdvocate;

    @Length(max = 256)
    @Audited
    private String counselphoneNo;

    @Pattern(regexp = LcmsConstants.numericiValForPhoneNo)
    @Audited
    private String counselEmail;

    @Column(name = "isrespondent")
    @Audited
    @Transient
    private Boolean isRepondent = false;


    @Column(name = "serialnumber")
    @Audited
    @Transient
    private Long serialNumber;


    

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }


    public Long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(final Long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public LegalCase getLegalCase() {
        return legalCase;
    }

    public void setLegalCase(final LegalCase legalCase) {
        this.legalCase = legalCase;
    }

    public String getOppPartyAdvocate() {
		return oppPartyAdvocate;
	}

	public String getCounselphoneNo() {
		return counselphoneNo;
	}

	public String getCounselEmail() {
		return counselEmail;
	}

	public void setOppPartyAdvocate(String oppPartyAdvocate) {
		this.oppPartyAdvocate = oppPartyAdvocate;
	}

	public void setCounselphoneNo(String counselphoneNo) {
		this.counselphoneNo = counselphoneNo;
	}

	public void setCounselEmail(String counselEmail) {
		this.counselEmail = counselEmail;
	}

	public Boolean getIsRepondent() {
        return isRepondent;
    }

    public void setIsRepondent(final Boolean isRepondent) {
        this.isRepondent = isRepondent;
    }


	public String getDefCounsilPrimary() {
		return defCounsilPrimary;
	}

	public void setDefCounsilPrimary(String defCounsilPrimary) {
		this.defCounsilPrimary = defCounsilPrimary;
	}

	public String getPrimaryCounsin() {
		return primaryCounsin;
	}

	public void setPrimaryCounsin(String primaryCounsin) {
		this.primaryCounsin = primaryCounsin;
	}
    
}
