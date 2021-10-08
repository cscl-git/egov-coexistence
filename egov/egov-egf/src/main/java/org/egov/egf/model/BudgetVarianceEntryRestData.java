package org.egov.egf.model;

import java.math.BigDecimal;

public class BudgetVarianceEntryRestData {
	
	private String departmentCode;
	private String departmentName;
    private String functionCode;
    private String fundCode;
    private String budgetCode;
    private String budgetHead;
    private BigDecimal estimate;
    private  BigDecimal additionalAppropriation;
    private BigDecimal ae;
    private BigDecimal variance;
    private Long detailId;
    private BigDecimal be;
	public String getDepartmentCode() {
		return departmentCode;
	}
	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public String getFunctionCode() {
		return functionCode;
	}
	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}
	public String getFundCode() {
		return fundCode;
	}
	public void setFundCode(String fundCode) {
		this.fundCode = fundCode;
	}
	public String getBudgetCode() {
		return budgetCode;
	}
	public void setBudgetCode(String budgetCode) {
		this.budgetCode = budgetCode;
	}
	public String getBudgetHead() {
		return budgetHead;
	}
	public void setBudgetHead(String budgetHead) {
		this.budgetHead = budgetHead;
	}
	public BigDecimal getEstimate() {
		return estimate;
	}
	public void setEstimate(BigDecimal estimate) {
		this.estimate = estimate;
	}
	public BigDecimal getAdditionalAppropriation() {
		return additionalAppropriation;
	}
	public void setAdditionalAppropriation(BigDecimal additionalAppropriation) {
		this.additionalAppropriation = additionalAppropriation;
	}
	public BigDecimal getAe() {
		return ae;
	}
	public void setAe(BigDecimal ae) {
		this.ae = ae;
	}
	public BigDecimal getVariance() {
		return variance;
	}
	public void setVariance(BigDecimal variance) {
		this.variance = variance;
	}
	public Long getDetailId() {
		return detailId;
	}
	public void setDetailId(Long detailId) {
		this.detailId = detailId;
	}
	public BigDecimal getBe() {
		return be;
	}
	public void setBe(BigDecimal be) {
		this.be = be;
	}

}
