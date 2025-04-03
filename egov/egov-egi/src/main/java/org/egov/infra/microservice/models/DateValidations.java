package org.egov.infra.microservice.models;

import java.util.ArrayList;
import java.util.List;

public class DateValidations {
	
	private String code;
	
	 private List<DateValidateByUser> DateValidateByUser = new ArrayList<>();

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	  public void addUsernameRole(DateValidateByUser usernameRole) {
	        this.DateValidateByUser.add(usernameRole);
	    }
	
	public List<DateValidateByUser> getDateValidateByUser() {
		return DateValidateByUser;
	}

	public void setDateValidateByUser(List<DateValidateByUser> dateValidateByUser) {
		DateValidateByUser = dateValidateByUser;
	}
	 
	 
	 

}
