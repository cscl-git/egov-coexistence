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
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.SafeHtml;

@Entity
@Table(name = "eglc_concerned_branch_master")
@SequenceGenerator(name = ConcernedBranchMaster.SEQ_CONCRN_BRANCH, sequenceName = ConcernedBranchMaster.SEQ_CONCRN_BRANCH, allocationSize = 1)
@AuditOverrides({ @AuditOverride(forClass = AbstractAuditable.class, name = "lastModifiedBy"),
        @AuditOverride(forClass = AbstractAuditable.class, name = "lastModifiedDate") })
public class ConcernedBranchMaster extends AbstractAuditable {

    private static final long serialVersionUID = 796823780349590496L;
    public static final String SEQ_CONCRN_BRANCH = "SEQ_EGLC_CONCERNED_BRANCH_MASTER";

    @Id
    @GeneratedValue(generator = SEQ_CONCRN_BRANCH, strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @NotNull
    @SafeHtml
    @Length(max = 32)
    @Audited
    private String concernedBranch;

    @Length(max = 256)
    @SafeHtml
    private String notes;

    @Min(1)
    @Max(1000)
    private Long orderNumber;

    @Audited
    @NotNull
    private Boolean active;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getConcernedBranch() {
		return concernedBranch;
	}

	public void setConcernedBranch(String concernedBranch) {
		this.concernedBranch = concernedBranch;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}   
}
