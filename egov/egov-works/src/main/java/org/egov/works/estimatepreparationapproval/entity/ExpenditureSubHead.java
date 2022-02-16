package org.egov.works.estimatepreparationapproval.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
@Entity
@Table(name = "eg_expenditure_sub_head")
public class ExpenditureSubHead implements Serializable{
@Id
@Column(name="exp_sub_head_id")
	private long subHeadId;
@Column(name="exp_sub_head")
	private String subHead;
@ManyToOne
@JoinColumn(name = "exp_head_id")
private ExpenditureHeadEntity exp_head_id;


public String getSubHead() {
	return subHead;
}
public void setSubHead(String subHead) {
	this.subHead = subHead;
}
public long getSubHeadId() {
	return subHeadId;
}
public void setSubHeadId(long subHeadId) {
	this.subHeadId = subHeadId;
}
public ExpenditureHeadEntity getExp_head_id() {
	return exp_head_id;
}
public void setExp_head_id(ExpenditureHeadEntity exp_head_id) {
	this.exp_head_id = exp_head_id;
}

}
