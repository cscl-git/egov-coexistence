package org.egov.egf.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.egov.egf.contract.model.AuditDetails;

public class ScheduleReportRestData {
	
	private String glCode;
    // private String glCode;
    private String accountName;
    private String scheduleNo;
    private BigDecimal budgetAmount;
    private String majorCode;
    private  Map<String, BigDecimal> scheduleWiseTotal = new HashMap<String, BigDecimal>();
    private Map<String, BigDecimal> netAmount = new HashMap<String, BigDecimal>();
    private Map<String, BigDecimal> previousYearAmount = new HashMap<String, BigDecimal>();
    private String type;
    
    private String department_name;
    private AuditDetails auditDetails;
	public String getGlCode() {
		return glCode;
	}
	public void setGlCode(String glCode) {
		this.glCode = glCode;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getScheduleNo() {
		return scheduleNo;
	}
	public void setScheduleNo(String scheduleNo) {
		this.scheduleNo = scheduleNo;
	}
	public BigDecimal getBudgetAmount() {
		return budgetAmount;
	}
	public void setBudgetAmount(BigDecimal budgetAmount) {
		this.budgetAmount = budgetAmount;
	}
	public String getMajorCode() {
		return majorCode;
	}
	public void setMajorCode(String majorCode) {
		this.majorCode = majorCode;
	}
	public Map<String, BigDecimal> getNetAmount() {
		return netAmount;
	}
	public void setNetAmount(Map<String, BigDecimal> netAmount) {
		this.netAmount = netAmount;
	}
	public Map<String, BigDecimal> getPreviousYearAmount() {
		return previousYearAmount;
	}
	public void setPreviousYearAmount(Map<String, BigDecimal> previousYearAmount) {
		this.previousYearAmount = previousYearAmount;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Map<String, BigDecimal> getScheduleWiseTotal() {
		return scheduleWiseTotal;
	}
	public void setScheduleWiseTotal(Map<String, BigDecimal> scheduleWiseTotal) {
		this.scheduleWiseTotal = scheduleWiseTotal;
	}
	public String getDepartment_name() {
		return department_name;
	}
	public void setDepartment_name(String department_name) {
		this.department_name = department_name;
	}
	public AuditDetails getAuditDetails() {
		return auditDetails;
	}
	public void setAuditDetails(AuditDetails auditDetails) {
		this.auditDetails = auditDetails;
	}
	
	
    
}
