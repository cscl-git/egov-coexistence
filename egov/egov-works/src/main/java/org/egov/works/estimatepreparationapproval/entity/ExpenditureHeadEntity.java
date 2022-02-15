package org.egov.works.estimatepreparationapproval.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "eg_expenditure_head")
public class ExpenditureHeadEntity implements Serializable {
	@Id
	@Column(name="head_id")
	private long headId;
	@Column(name="expenditure_head")
	private String expenditureHead;
	
	public long getHeadId() {
		return headId;
	}
	public void setHeadId(long headId) {
		this.headId = headId;
	}
	public String getExpenditureHead() {
		return expenditureHead;
	}
	public void setExpenditureHead(String expenditureHead) {
		this.expenditureHead = expenditureHead;
	}
	
	
}
