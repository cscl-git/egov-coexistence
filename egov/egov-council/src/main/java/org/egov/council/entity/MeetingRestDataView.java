package org.egov.council.entity;

public class MeetingRestDataView {
	

	private Long id;
	private String meetingCreatedDate;
	private String meetingDate;
	private String meetingLocation;
	private String meetingNumber;
	private String meetingStatus;
	private String meetingTime;
	private String meetingType;
	private int noOfMembersAbsent;
	private int noOfMembersPresent;
	private int totCommitteMemCount;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMeetingCreatedDate() {
		return meetingCreatedDate;
	}
	public void setMeetingCreatedDate(String meetingCreatedDate) {
		this.meetingCreatedDate = meetingCreatedDate;
	}
	public String getMeetingDate() {
		return meetingDate;
	}
	public void setMeetingDate(String meetingDate) {
		this.meetingDate = meetingDate;
	}
	public String getMeetingLocation() {
		return meetingLocation;
	}
	public void setMeetingLocation(String meetingLocation) {
		this.meetingLocation = meetingLocation;
	}
	public String getMeetingNumber() {
		return meetingNumber;
	}
	public void setMeetingNumber(String meetingNumber) {
		this.meetingNumber = meetingNumber;
	}
	public String getMeetingStatus() {
		return meetingStatus;
	}
	public void setMeetingStatus(String meetingStatus) {
		this.meetingStatus = meetingStatus;
	}
	public String getMeetingTime() {
		return meetingTime;
	}
	public void setMeetingTime(String meetingTime) {
		this.meetingTime = meetingTime;
	}
	public String getMeetingType() {
		return meetingType;
	}
	public void setMeetingType(String meetingType) {
		this.meetingType = meetingType;
	}
	public int getNoOfMembersAbsent() {
		return noOfMembersAbsent;
	}
	public void setNoOfMembersAbsent(int noOfMembersAbsent) {
		this.noOfMembersAbsent = noOfMembersAbsent;
	}
	public int getNoOfMembersPresent() {
		return noOfMembersPresent;
	}
	public void setNoOfMembersPresent(int noOfMembersPresent) {
		this.noOfMembersPresent = noOfMembersPresent;
	}
	public int getTotCommitteMemCount() {
		return totCommitteMemCount;
	}
	public void setTotCommitteMemCount(int totCommitteMemCount) {
		this.totCommitteMemCount = totCommitteMemCount;
	}
	
	
}
