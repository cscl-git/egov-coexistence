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

import org.egov.commons.CVoucherHeader;
import org.egov.infra.persistence.entity.AbstractAuditable;

@Entity
@Table(name = "AUDIT_POST_VOUCHER_MPNG")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(name = AuditPostVoucherMpng.SEQ_AUDIT_POST_VOUCHER_MPNG, sequenceName = AuditPostVoucherMpng.SEQ_AUDIT_POST_VOUCHER_MPNG, allocationSize = 1)
public class AuditPostVoucherMpng extends AbstractAuditable implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3837594197462206213L;
	public static final String SEQ_AUDIT_POST_VOUCHER_MPNG = "SEQ_AUDIT_POST_VOUCHER_MPNG";
	
	@Id
    @GeneratedValue(generator = SEQ_AUDIT_POST_VOUCHER_MPNG, strategy = GenerationType.SEQUENCE)
    private Long id;
	
	@ManyToOne
    @JoinColumn(name = "audit_id")
    private AuditDetails auditDetails;
	
	@ManyToOne
    @JoinColumn(name = "vh_id")
    private CVoucherHeader voucherheader;

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

	public CVoucherHeader getVoucherheader() {
		return voucherheader;
	}

	public void setVoucherheader(CVoucherHeader voucherheader) {
		this.voucherheader = voucherheader;
	}

}
