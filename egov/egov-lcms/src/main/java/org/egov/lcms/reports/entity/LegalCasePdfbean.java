package org.egov.lcms.reports.entity;

import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class LegalCasePdfbean {
	
	private String fileNumber;
	private String caseNumber;
	private String caseTitle;
	private String courtName;
	private String defendingCounsel;
	private String petitioners;
	private String respondents;
	private String concernedBranch;
	private String nextHearingDate;
	private String hearingOutcome;
	public String getFileNumber() {
		return fileNumber;
	}
	public void setFileNumber(String fileNumber) {
		this.fileNumber = fileNumber;
	}
	public String getCaseNumber() {
		return caseNumber;
	}
	public void setCaseNumber(String caseNumber) {
		this.caseNumber = caseNumber;
	}
	public String getCaseTitle() {
		return caseTitle;
	}
	public void setCaseTitle(String caseTitle) {
		this.caseTitle = caseTitle;
	}
	public String getCourtName() {
		return courtName;
	}
	public void setCourtName(String courtName) {
		this.courtName = courtName;
	}
	public String getDefendingCounsel() {
		return defendingCounsel;
	}
	public void setDefendingCounsel(String defendingCounsel) {
		this.defendingCounsel = defendingCounsel;
	}
	public String getPetitioners() {
		return petitioners;
	}
	public void setPetitioners(String petitioners) {
		this.petitioners = petitioners;
	}
	public String getRespondents() {
		return respondents;
	}
	public void setRespondents(String respondents) {
		this.respondents = respondents;
	}
	public String getConcernedBranch() {
		return concernedBranch;
	}
	public void setConcernedBranch(String concernedBranch) {
		this.concernedBranch = concernedBranch;
	}
	public String getNextHearingDate() {
		return nextHearingDate;
	}
	public void setNextHearingDate(String nextHearingDate) {
		this.nextHearingDate = nextHearingDate;
	}
	public String getHearingOutcome() {
		return hearingOutcome;
	}
	public void setHearingOutcome(String hearingOutcome) {
		this.hearingOutcome = hearingOutcome;
	}
	

}
