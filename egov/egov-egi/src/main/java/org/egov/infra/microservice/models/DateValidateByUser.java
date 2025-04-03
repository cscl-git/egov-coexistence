package org.egov.infra.microservice.models;

public class DateValidateByUser {
	
	private String username;
	private Integer validDay;
	
	 public DateValidateByUser(String username, int validDay) {
	        this.username = username;
	        this.validDay = validDay;
	    }
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Integer getValidDay() {
		return validDay;
	}
	public void setValidDay(Integer validDay) {
		this.validDay = validDay;
	}
	
	 @Override
	    public String toString() {
	        return "Username: " + username + ", Valid Day: " + validDay;
	    }
	
	

}
