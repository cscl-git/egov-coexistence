package org.egov.audit.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "EG_MANAGEAUDITOR")
@SequenceGenerator(name = ManageAuditor.SEQ_EG_MANAGEAUDITOR, sequenceName = ManageAuditor.SEQ_EG_MANAGEAUDITOR, allocationSize = 1)
public class ManageAuditor implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4566245689079138636L;
	public static final String SEQ_EG_MANAGEAUDITOR ="SEQ_EG_MANAGEAUDITOR";
	
	
	@Id
    @GeneratedValue(generator = SEQ_EG_MANAGEAUDITOR, strategy = GenerationType.SEQUENCE)
    private Long id;
	
	private Integer deptid;
	
	@Transient
	private String deptName;
	@Transient
	private String employeeName;
	
	private Integer employeeid;
	
	private String type;
	
	 private Date lastUpdatedtime;
	 
	 private Date createdtime;
	 
	 private Integer lastupdatedby;
	 
	
	 
	 
	 
	 
	
	

	

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Integer getDeptid() {
		return deptid;
	}

	public void setDeptid(Integer deptid) {
		this.deptid = deptid;
	}

	public Integer getEmployeeid() {
		return employeeid;
	}

	public void setEmployeeid(Integer employeeid) {
		this.employeeid = employeeid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getLastUpdatedtime() {
		return lastUpdatedtime;
	}

	public void setLastUpdatedtime(Date lastUpdatedtime) {
		this.lastUpdatedtime = lastUpdatedtime;
	}

	public Date getCreatedtime() {
		return createdtime;
	}

	public void setCreatedtime(Date createdtime) {
		this.createdtime = createdtime;
	}

	public Integer getLastupdatedby() {
		return lastupdatedby;
	}

	public void setLastupdatedby(Integer lastupdatedby) {
		this.lastupdatedby = lastupdatedby;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	

	
	

}
