package org.egov.audit.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.egov.infra.persistence.entity.AbstractAuditable;

@Entity

@Table(name = "PASS_UNDER_OBJECTION_DETAILS")
@SequenceGenerator(name = PassUnderObjection.SEQ_PASS_UNDER_OBJECTION_DETAILS, sequenceName = PassUnderObjection.SEQ_PASS_UNDER_OBJECTION_DETAILS, allocationSize = 1)

public class PassUnderObjection extends AbstractAuditable implements java.io.Serializable {

	public PassUnderObjection() {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1396632085994354439L;

	public static final String SEQ_PASS_UNDER_OBJECTION_DETAILS = "SEQ_PASS_UNDER_OBJECTION_DETAILS";

	@Id
	@GeneratedValue(generator = SEQ_PASS_UNDER_OBJECTION_DETAILS, strategy = GenerationType.SEQUENCE)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "audit_id")
	private AuditDetails auditDetails;

	@Transient
	private Long temp_id;
	@Transient
	private List<PassUnderObjection> passUnderObjSearchList = new ArrayList<PassUnderObjection>();

	
	public List<PassUnderObjection> getPassUnderObjSearchList() {
		return passUnderObjSearchList;
	}

	public void setPassUnderObjSearchList(List<PassUnderObjection> passUnderObjSearchList) {
		this.passUnderObjSearchList = passUnderObjSearchList;
	}

	public Long getTemp_id() {
		return temp_id;
	}

	public void setTemp_id(Long temp_id) {
		this.temp_id = temp_id;
	}

	private Date resolutiondate;

	private String resolutioncomment;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AuditDetails getAuditDetails() {
		return auditDetails;
	}

	public void setAuditDetails(AuditDetails auditDetails) {
		this.auditDetails = auditDetails;
	}

	public Date getResolutiondate() {
		return resolutiondate;
	}

	public void setResolutiondate(Date resolutiondate) {
		this.resolutiondate = resolutiondate;
	}

	public String getResolutioncomment() {
		return resolutioncomment;
	}

	public void setResolutioncomment(String resolutioncomment) {
		this.resolutioncomment = resolutioncomment;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public static String getSeqPassUnderObjectionDetails() {
		return SEQ_PASS_UNDER_OBJECTION_DETAILS;
	}

}
