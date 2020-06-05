package org.egov.apnimandi.reports.entity;

import org.egov.apnimandi.transactions.entity.ApnimandiContractor;

public class ApnimandiContractorSearchResult {
	private ApnimandiContractor apnimandiContractor;
	private String zoneName;
	private String statusName;	
	public ApnimandiContractor getApnimandiContractor() {
		return apnimandiContractor;
	}
	public void setApnimandiContractor(ApnimandiContractor apnimandiContractor) {
		this.apnimandiContractor = apnimandiContractor;
	}
	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
}
