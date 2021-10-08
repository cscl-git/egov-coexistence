package org.egov.infra.admin.master.service;

import java.util.List;

import org.egov.infra.admin.master.entity.Department;

public class GeneralLedgerPOJO {
	private String from_date;
	private String to_date;
	private String department ;
	
	
	public String getFrom_date() {
		return from_date;
	}
	public void setFrom_date(String from_date) {
		this.from_date = from_date;
	}
	public String getTo_date() {
		return to_date;
	}
	public void setTo_date(String to_date) {
		this.to_date = to_date;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	
	
	
	
	

}
