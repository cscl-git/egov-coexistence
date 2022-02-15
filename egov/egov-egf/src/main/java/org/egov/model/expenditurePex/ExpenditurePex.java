package org.egov.model.expenditurePex;

import java.util.Date;

public class ExpenditurePex {

	private String pexDate;

	private String pex;

	private String bvp;

	private String glcodeId;

	public String getGlcodeId() {
		return glcodeId;
	}

	public void setGlcodeId(String glcodeId) {
		this.glcodeId = glcodeId;
	}

	private String bvpDate;

	private String vNo;

	private String vDate;

	private String voucherType;

	private String partyName;

	private String budgetHead;

	private String narration;

	private String particulars;

	private String debitamt;

	private String creditamt;

	private String accNum;
	
	
	public String getAccNum() {
		return accNum;
	}

	public void setAccNum(String accNum) {
		this.accNum = accNum;
	}

	public String getPexDate() {
		return pexDate;
	}

	public void setPexDate(String pexDate) {
		this.pexDate = pexDate;
	}

	public String getBvpDate() {
		return bvpDate;
	}

	public void setBvpDate(String bvpDate) {
		this.bvpDate = bvpDate;
	}

	public String getvDate() {
		return vDate;
	}

	public void setvDate(String vDate) {
		this.vDate = vDate;
	}

	public String getPartyName() {
		return partyName;
	}

	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}

	public String getvNo() {
		return vNo;
	}

	public void setvNo(String vNo) {
		this.vNo = vNo;
	}

	public String getBvp() {
		return bvp;
	}

	public void setBvp(String bvp) {
		this.bvp = bvp;
	}

	public String getPex() {
		return pex;
	}

	public void setPex(String pex) {
		this.pex = pex;
	}

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	public String getParticulars() {
		return particulars;
	}

	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}

	public String getBudgetHead() {
		return budgetHead;
	}

	public void setBudgetHead(String budgetHead) {
		this.budgetHead = budgetHead;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public String getDebitamt() {
		return debitamt;
	}

	public void setDebitamt(String debitamt) {
		this.debitamt = debitamt;
	}

	public String getCreditamt() {
		return creditamt;
	}

	public void setCreditamt(String creditamt) {
		this.creditamt = creditamt;
	}

}
