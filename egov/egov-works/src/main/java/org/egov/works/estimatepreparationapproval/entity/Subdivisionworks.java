package org.egov.works.estimatepreparationapproval.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "eg_subdivision")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(name = Subdivisionworks.SEQ_SUBDIVISION, sequenceName = Subdivisionworks.SEQ_SUBDIVISION, allocationSize = 1)
public class Subdivisionworks implements Serializable{
	private static final long serialVersionUID = -4312140421386028968L;

	public static final String SEQ_SUBDIVISION = "SEQ_SUBDIVISION";
	
	@Id
	@GeneratedValue(generator = SEQ_SUBDIVISION, strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Long id;
	
	@Column(name="subdivision")
	private String subdivision;
	
	@Column(name="division_id")
	private Long divisionid;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSubdivision() {
		return subdivision;
	}

	public void setSubdivision(String subdivision) {
		this.subdivision = subdivision;
	}

	public Long getDivisionid() {
		return divisionid;
	}

	public void setDivisionid(Long divisionid) {
		this.divisionid = divisionid;
	}
}
