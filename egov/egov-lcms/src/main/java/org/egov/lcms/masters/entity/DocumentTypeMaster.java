package org.egov.lcms.masters.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.persistence.validator.annotation.Unique;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.SafeHtml;

@Entity
@Table(name = "EGLC_DOCUMENTTYPE_MASTER")
@Unique(id = "id", tableName = "EGLC_DOCUMENTTYPE_MASTER",columnName = { "code","documentType" },fields = { "code", "documentType" }, enableDfltMsg = true)
@SequenceGenerator(name = DocumentTypeMaster.SEQ_DOCUMENT_TYPE, sequenceName = DocumentTypeMaster.SEQ_DOCUMENT_TYPE, allocationSize = 1)
@AuditOverrides({ @AuditOverride(forClass = AbstractAuditable.class, name = "lastModifiedBy"),
        @AuditOverride(forClass = AbstractAuditable.class, name = "lastModifiedDate") })
public class DocumentTypeMaster extends AbstractAuditable{
	
	private static final long serialVersionUID = 1517694643078084884L;
	public static final String SEQ_DOCUMENT_TYPE = "SEQ_EGLC_DOCUMENTTYPE_MASTER";
	
	@Id
    @GeneratedValue(generator = SEQ_DOCUMENT_TYPE, strategy = GenerationType.SEQUENCE)
    private Long id;

    @Length(max = 25)
    @Audited
    @NotNull
    private String code;

    @NotNull
    @Length(max = 50)
    @Audited
    private String documentType;

    @Audited
    @NotNull
    private Boolean active;

    @Length(max = 256)
    @SafeHtml
    private String notes;
    
    @Min(1)
    @Max(1000)
    private Long ordernumber;
	
	@Override
	public Long getId() {
		 return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Long getOrdernumber() {
		return ordernumber;
	}

	public void setOrdernumber(Long ordernumber) {
		this.ordernumber = ordernumber;
	}   
}
