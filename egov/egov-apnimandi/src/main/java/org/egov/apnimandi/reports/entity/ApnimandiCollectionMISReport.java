package org.egov.apnimandi.reports.entity;

import java.io.Serializable;
import java.util.Date;

public class ApnimandiCollectionMISReport implements Serializable{
	private static final long serialVersionUID = -1607516086019249575L;
	private Long id;
	private Date collectionDate;	
	private Integer month;	
	private Integer year;
	private String amountType;
	private double totalAmount;
	private String zoneName;
	private String siteName;	
	private String status;
	private String collectionType;
	private String contractorName;
	
	public ApnimandiCollectionMISReport() {}
	
	public ApnimandiCollectionMISReport(Long id, int month, int year, String amountType, double totalAmount,
			String zoneName, String siteName, String status, String collectionType, String contractorName) {
		this.id = id;
		this.month = month;
		this.year = year;
		this.amountType = amountType;
		this.totalAmount = totalAmount;
		this.zoneName = zoneName;
		this.siteName = siteName;
		this.status = status;
		this.collectionType = collectionType;
		this.contractorName = contractorName;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getContractorName() {
		return contractorName;
	}
	public void setContractorName(String contractorName) {
		this.contractorName = contractorName;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}	
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCollectionType() {
		return collectionType;
	}
	public void setCollectionType(String collectionType) {
		this.collectionType = collectionType;
	}
	public String getAmountType() {
		return amountType;
	}
	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Date getCollectionDate() {
		return collectionDate;
	}
	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}	
}
