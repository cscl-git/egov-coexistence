package org.egov.works.boq.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="BOQ_UPDATE_DATE")
@SequenceGenerator(name = BoqDateUpdate.SEQ_BOQUPDATE_ID, sequenceName = BoqDateUpdate.SEQ_BOQUPDATE_ID, allocationSize = 1)
public class BoqDateUpdate implements Serializable{

	private static final long serialVersionUID = 1L;

	public static final String SEQ_BOQUPDATE_ID = "SEQ_BOQUPDATE_ID";
	@Id
	@GeneratedValue(generator = SEQ_BOQUPDATE_ID, strategy = GenerationType.SEQUENCE)
	private long id;
	private long sl_no;
	private String actualEndDate;
	private String reason;
	private Date createdDate;
	private String createdby;
	
	
	
	
	public String getCreatedby() {
		return createdby;
	}
	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String getActualEndDate() {
		return actualEndDate;
	}
	public void setActualEndDate(String string) {
		this.actualEndDate = string;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public long getSl_no() {
		return sl_no;
	}
	public void setSl_no(long string) {
		this.sl_no = string;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	
	
	
	
	
}
