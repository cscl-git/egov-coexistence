package org.egov.audit.model;





import java.util.List;

import org.egov.audit.entity.BillTypeCheckList;





public class BillTypeCheckListData implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3142893893591917946L;
	private Long id;
    private String billType;
	private String billtypedescrip;
	private String messege;
	private List<BillTypeCheckList> billTypeCheckLists;
	
	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}
	
	

	public String getBilltypedescrip() {
		return billtypedescrip;
	}

	public void setBilltypedescrip(String billtypedescrip) {
		this.billtypedescrip = billtypedescrip;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessege() {
		return messege;
	}

	public void setMessege(String messege) {
		this.messege = messege;
	} 
		
	public List<BillTypeCheckList> getBillTypeCheckLists() {
		return billTypeCheckLists;
	}

	public void setBillTypeCheckLists(List<BillTypeCheckList> billTypeCheckLists) {
		this.billTypeCheckLists = billTypeCheckLists;
	}

	
	public boolean isStringNull(String str) 
    { 
        if (str == null) 
            return true; 
        else
            return false; 
    }	
}
