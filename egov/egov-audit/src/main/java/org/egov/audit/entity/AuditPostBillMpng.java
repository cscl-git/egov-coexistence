package org.egov.audit.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.model.bills.EgBillregister;

@Entity
@Table(name = "AUDIT_POST_BILL_MPNG")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(name = AuditPostBillMpng.SEQ_AUDIT_POST_BILL_MPNG, sequenceName = AuditPostBillMpng.SEQ_AUDIT_POST_BILL_MPNG, allocationSize = 1)
public class AuditPostBillMpng extends AbstractAuditable implements java.io.Serializable{
	
/**
	 * 
	 */
	private static final long serialVersionUID = 4015597262881375991L;

public static final String SEQ_AUDIT_POST_BILL_MPNG = "SEQ_AUDIT_POST_BILL_MPNG";
	
	@Id
    @GeneratedValue(generator = SEQ_AUDIT_POST_BILL_MPNG, strategy = GenerationType.SEQUENCE)
    private Long id;
	
	@ManyToOne
    @JoinColumn(name = "audit_id")
    private AuditDetails auditDetails;
	
	@ManyToOne
    @JoinColumn(name = "bill_id")
    private EgBillregister egBillregister;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	protected void setId(Long id) {
		this.id=id;
		
	}

	public AuditDetails getAuditDetails() {
		return auditDetails;
	}

	public void setAuditDetails(AuditDetails auditDetails) {
		this.auditDetails = auditDetails;
	}

	public EgBillregister getEgBillregister() {
		return egBillregister;
	}

	public void setEgBillregister(EgBillregister egBillregister) {
		this.egBillregister = egBillregister;
	}

}
