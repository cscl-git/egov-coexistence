package com.exilant.eGov.src.common;

public class SubDivision {

	private Long id=0L;
	
	private String subdivisionCode;
	
	private String subdivisionName;

	public String getSubdivisionCode() {
		return subdivisionCode;
	}

	public String getSubdivisionName() {
		return subdivisionName;
	}

	public void setSubdivisionCode(String subdivisionCode) {
		this.subdivisionCode = subdivisionCode;
	}

	public void setSubdivisionName(String subdivisionName) {
		this.subdivisionName = subdivisionName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
}
