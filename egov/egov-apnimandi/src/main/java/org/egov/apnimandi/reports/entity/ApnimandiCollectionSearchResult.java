package org.egov.apnimandi.reports.entity;

import org.egov.apnimandi.transactions.entity.ApnimandiCollectionDetails;

public class ApnimandiCollectionSearchResult {
	private ApnimandiCollectionDetails apnimandiCollections;
	private String zoneName;
	private String statusName;	
	
	public ApnimandiCollectionDetails getApnimandiCollections() {
		return apnimandiCollections;
	}
	public void setApnimandiCollections(ApnimandiCollectionDetails apnimandiCollections) {
		this.apnimandiCollections = apnimandiCollections;
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
