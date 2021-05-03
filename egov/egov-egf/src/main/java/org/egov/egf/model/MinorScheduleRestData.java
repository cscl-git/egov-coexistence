package org.egov.egf.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class MinorScheduleRestData {
	
	private String glCode;
    private String accountName;
    private String scheduleNo;
    // FundCode is added for RP report
    private String fundCode;
    private BigDecimal previousYearTotal = BigDecimal.ZERO;
    private BigDecimal currentYearTotal = BigDecimal.ZERO;
    private Map<String, BigDecimal> fundWiseAmount = new HashMap<String, BigDecimal>();
    private String type;
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
	public String getFundCode() {
		return fundCode;
	}
	public void setFundCode(String fundCode) {
		this.fundCode = fundCode;
	}
	public BigDecimal getPreviousYearTotal() {
		return previousYearTotal;
	}
	public void setPreviousYearTotal(BigDecimal previousYearTotal) {
		this.previousYearTotal = previousYearTotal;
	}
	public BigDecimal getCurrentYearTotal() {
		return currentYearTotal;
	}
	public void setCurrentYearTotal(BigDecimal currentYearTotal) {
		this.currentYearTotal = currentYearTotal;
	}
	public Map<String, BigDecimal> getFundWiseAmount() {
		return fundWiseAmount;
	}
	public void setFundWiseAmount(Map<String, BigDecimal> fundWiseAmount) {
		this.fundWiseAmount = fundWiseAmount;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
    
    

}
