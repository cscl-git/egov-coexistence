package org.egov.lcms.transactions.entity;

import javax.persistence.CascadeType;
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
import javax.validation.constraints.NotNull;

import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.lcms.masters.entity.DocumentTypeMaster;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "EGLC_LIBRARY")
@SequenceGenerator(name = Library.SEQ_LIBRARY, sequenceName = Library.SEQ_LIBRARY, allocationSize = 1)
@AuditOverrides({ @AuditOverride(forClass = AbstractAuditable.class, name = "lastModifiedBy"),
        @AuditOverride(forClass = AbstractAuditable.class, name = "lastModifiedDate") })
public class Library extends AbstractAuditable{
	private static final long serialVersionUID = 1517694643078084884L;
	
	public static final String SEQ_LIBRARY = "SEQ_EGLC_LIBRARY";

    @Id
    @GeneratedValue(generator = SEQ_LIBRARY, strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "documenttype", nullable = false)
    @Audited
    private DocumentTypeMaster documentType;
    
    @NotNull
    @Length(max = 255)
    @Column(name = "title")
    @Audited
    private String title;    
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "filestoreid")
    @Fetch(FetchMode.JOIN)
    private FileStoreMapper filestoreid;
    
    @Column(name = "reffileid")
    @Audited
    private String reffileid;
    
    @Audited
    @NotNull
    private Boolean active;
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }
    
    public DocumentTypeMaster getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentTypeMaster documentType) {
		this.documentType = documentType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public FileStoreMapper getFilestoreid() {
		return filestoreid;
	}

	public void setFilestoreid(FileStoreMapper filestoreid) {
		this.filestoreid = filestoreid;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getReffileid() {
		return reffileid;
	}

	public void setReffileid(String reffileid) {
		this.reffileid = reffileid;
	}	
}
