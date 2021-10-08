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
@Table(name = "eg_workswing")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(name = Workswing.SEQ_WORKSWING, sequenceName = Workswing.SEQ_WORKSWING, allocationSize = 1)
public class Workswing implements Serializable{

	private static final long serialVersionUID = -4312140421386028968L;

	public static final String SEQ_WORKSWING = "SEQ_WORKSWING";
	
	@Id
	@GeneratedValue(generator = SEQ_WORKSWING, strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Long id;
	
	@Column(name="workswingname")
	private String workswingname;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWorkswingname() {
		return workswingname;
	}

	public void setWorkswingname(String workswingname) {
		this.workswingname = workswingname;
	}
	
}
