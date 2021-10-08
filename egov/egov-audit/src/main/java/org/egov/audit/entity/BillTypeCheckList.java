package org.egov.audit.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;







@Entity
@Table(name = "AUDIT_CHECKLIST_MASTER")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(name = BillTypeCheckList.SEQ_AUDIT_CHECKLIST_MASTER, sequenceName = BillTypeCheckList.SEQ_AUDIT_CHECKLIST_MASTER, allocationSize = 1)
public class BillTypeCheckList implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3045553368342070332L;
	
    public static final String SEQ_AUDIT_CHECKLIST_MASTER = "SEQ_AUDIT_CHECKLIST_MASTER";
	
	@Id
    @GeneratedValue(generator = SEQ_AUDIT_CHECKLIST_MASTER, strategy = GenerationType.SEQUENCE)
    private Long id;
	
	private String billType;
	
	private String billtypedescrip;
	
	
	

	public BillTypeCheckList() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BillTypeCheckList(Long id, String billType, String billtypedescrip) {
		super();
		this.id = id;
		this.billType = billType;
		this.billtypedescrip = billtypedescrip;
	}

	public Long getId() {
		return id;
	}

	public String getBillType() {
		return billType;
	}

	public String getBilltypedescrip() {
		return billtypedescrip;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public void setBilltypedescrip(String billtypedescrip) {
		this.billtypedescrip = billtypedescrip;
	}
	
	

}
