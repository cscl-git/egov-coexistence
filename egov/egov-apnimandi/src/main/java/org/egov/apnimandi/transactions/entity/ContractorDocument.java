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

import org.egov.apnimandi.masters.entity.DocumentsTypeMaster;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "EGAM_CONTRACTOR_DOCUMENTS")
@SequenceGenerator(name = ContractorDocument.SEQ_EGAM_CONTRACTOR_DOCUMENTS, sequenceName = ContractorDocument.SEQ_EGAM_CONTRACTOR_DOCUMENTS, allocationSize = 1)
@AuditOverrides({ @AuditOverride(forClass = AbstractAuditable.class, name = "lastModifiedBy"),
        @AuditOverride(forClass = AbstractAuditable.class, name = "lastModifiedDate") })
public class ContractorDocument extends AbstractAuditable{
	private static final long serialVersionUID = 1517694643078084884L;	
	public static final String SEQ_EGAM_CONTRACTOR_DOCUMENTS = "SEQ_EGAM_CONTRACTOR_DOCUMENTS";

    @Id
    @GeneratedValue(generator = SEQ_EGAM_CONTRACTOR_DOCUMENTS, strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "contractor", nullable = false)
    @Audited
    private ApnimandiContractor contractor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "documenttype", nullable = false)
    @Audited
    private DocumentsTypeMaster documentType;
    
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

	public ApnimandiContractor getContractor() {
		return contractor;
	}

	public void setContractor(ApnimandiContractor contractor) {
		this.contractor = contractor;
	}

	public DocumentsTypeMaster getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentsTypeMaster documentType) {
		this.documentType = documentType;
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
