package org.egov.model.bills;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;


import org.egov.infra.microservice.models.Department;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.springframework.stereotype.Component;

@Entity
@Component
@Table(name = "RETRACHMENTDETAILS")
@SequenceGenerator(name = RetrachmentDetails.SEQ_RETRACHMENT, sequenceName = RetrachmentDetails.SEQ_RETRACHMENT, allocationSize = 1)
public class RetrachmentDetails extends AbstractAuditable implements java.io.Serializable  {
	private static final long serialVersionUID = 1396632085994354439L;

	public static final String SEQ_RETRACHMENT = "SEQ_RETRACHMENT";

	@Id
	@GeneratedValue(generator = SEQ_RETRACHMENT, strategy = GenerationType.SEQUENCE)
	private Long id;
	private Date retrachmentdate;
	@Transient
	private Date date;
	private String department_name;
	private BigDecimal amountofbill;
	private BigDecimal amountbyaudit;
	private BigDecimal amountretrached;
	private String billdetail;
	private String remarks;
	@Transient
	private String status;
	private String auditid;
	
	@Transient
	private String auditorSignature;

	@Transient
	private String rsa_examinerSignature;

	@Transient
	private Date billFrom;
	
	@Transient
	private List<Department> departments = new ArrayList<Department>();
	
	@Transient
	private List<RetrachmentDetails> retrachmentDetailList = new ArrayList<RetrachmentDetails>();

	@Transient
	private String department;
	
	@Transient
	private Date billTo;
	
	public Date getBillFrom() {
		return billFrom;
	}

	public void setBillFrom(Date billFrom) {
		this.billFrom = billFrom;
	}

	public Date getBillTo() {
		return billTo;
	}

	public void setBillTo(Date billTo) {
		this.billTo = billTo;
	}

	
	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public List<RetrachmentDetails> getRetrachmentDetailList() {
		return retrachmentDetailList;
	}

	public void setRetrachmentDetailList(List<RetrachmentDetails> retrachmentDetailList) {
		this.retrachmentDetailList = retrachmentDetailList;
	}

	public String getAuditorSignature() {
		return auditorSignature;
	}

	public List<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(Long id) {
		// TODO Auto-generated method stub
		
	}

	

	public String getDepartment_name() {
		return department_name;
	}

	public void setDepartment_name(String department_name) {
		this.department_name = department_name;
	}

	

	public String getBilldetail() {
		return billdetail;
	}

	public void setBilldetail(String billdetail) {
		this.billdetail = billdetail;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRsa_examinerSignature() {
		return rsa_examinerSignature;
	}

	public void setRsa_examinerSignature(String rsa_examinerSignature) {
		this.rsa_examinerSignature = rsa_examinerSignature;
	}

	public void setAuditorSignature(String auditorSignature) {
		this.auditorSignature = auditorSignature;
	}

	public BigDecimal getAmountofbill() {
		return amountofbill;
	}

	public void setAmountofbill(BigDecimal amountofbill) {
		this.amountofbill = amountofbill;
	}

	public BigDecimal getAmountbyaudit() {
		return amountbyaudit;
	}

	public void setAmountbyaudit(BigDecimal amountbyaudit) {
		this.amountbyaudit = amountbyaudit;
	}

	public BigDecimal getAmountretrached() {
		return amountretrached;
	}

	public void setAmountretrached(BigDecimal amountretrached) {
		this.amountretrached = amountretrached;
	}

	public String getAuditid() {
		return auditid;
	}

	public void setAuditid(String auditid) {
		this.auditid = auditid;
	}

	public Date getRetrachmentdate() {
		return retrachmentdate;
	}

	public void setRetrachmentdate(Date retrachmentdate) {
		this.retrachmentdate = retrachmentdate;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	@Transient
	private String retdate;

	public String getRetdate() {
		return retdate;
	}

	public void setRetdate(String retdate) {
		this.retdate = retdate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
