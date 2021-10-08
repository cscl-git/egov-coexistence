package org.egov.apnimandi.transactions.entity;

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
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "EGAM_COLLECTION_DOCUMENT")
@SequenceGenerator(name = ApnimandiCollectionDocument.SEQ_COLLECTION_DOCUMENT, sequenceName = ApnimandiCollectionDocument.SEQ_COLLECTION_DOCUMENT, allocationSize = 1)
@AuditOverrides({ @AuditOverride(forClass = AbstractAuditable.class, name = "lastModifiedBy"),
        @AuditOverride(forClass = AbstractAuditable.class, name = "lastModifiedDate") })
public class ApnimandiCollectionDocument extends AbstractAuditable{
	private static final long serialVersionUID = 1517694643078084884L;	
	public static final String SEQ_COLLECTION_DOCUMENT = "SEQ_EGAM_COLLECTION_DOCUMENT";

    @Id
    @GeneratedValue(generator = SEQ_COLLECTION_DOCUMENT, strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "collectiondetails", nullable = false)
    @Audited
    private ApnimandiCollectionDetails apnimandicollectiondetails;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "filestoreid")
    @Fetch(FetchMode.JOIN)
    private FileStoreMapper filestoreid;
    
    @Column(name = "reffileid")
    @Audited
    private String reffileid;
    
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

	public FileStoreMapper getFilestoreid() {
		return filestoreid;
	}

	public void setFilestoreid(FileStoreMapper filestoreid) {
		this.filestoreid = filestoreid;
	}

	public String getReffileid() {
		return reffileid;
	}

	public void setReffileid(String reffileid) {
		this.reffileid = reffileid;
	}
}
