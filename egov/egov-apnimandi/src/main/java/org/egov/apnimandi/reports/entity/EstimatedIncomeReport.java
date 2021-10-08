package org.egov.apnimandi.reports.entity;

import java.io.Serializable;

public class EstimatedIncomeReport  implements Serializable{	
	private static final long serialVersionUID = -227816736287986314L;
	private String zoneName;
	private String siteName;	
	private double totalAmount;
		
	public EstimatedIncomeReport() {}

	public EstimatedIncomeReport(String zoneName, String siteName, double totalAmount) {
		this.zoneName = zoneName;
		this.siteName = siteName;
		this.totalAmount = totalAmount;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}	
}
