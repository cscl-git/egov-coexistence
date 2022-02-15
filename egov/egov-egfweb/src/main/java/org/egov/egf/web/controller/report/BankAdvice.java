package org.egov.egf.web.controller.report;

import java.util.List;

public class BankAdvice {

	private String fromDate;
	private String toDate;
	private String bankName;
	private String branchName;
	private String accountNumber;
	
	private List<String> bankNames;
	private List<String> branchNames;
	private List<String> accountNumbers;
	
	private List<PEX> pex;
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public List<String> getBankNames() {
		return bankNames;
	}
	public void setBankNames(List<String> bankNames) {
		this.bankNames = bankNames;
	}
	public List<String> getBranchNames() {
		return branchNames;
	}
	public void setBranchNames(List<String> branchNames) {
		this.branchNames = branchNames;
	}
	public List<String> getAccountNumbers() {
		return accountNumbers;
	}
	public void setAccountNumbers(List<String> accountNumbers) {
		this.accountNumbers = accountNumbers;
	}
	public List<PEX> getPex() {
		return pex;
	}
	public void setPex(List<PEX> pex) {
		this.pex = pex;
	}
	
	
	
	
}
