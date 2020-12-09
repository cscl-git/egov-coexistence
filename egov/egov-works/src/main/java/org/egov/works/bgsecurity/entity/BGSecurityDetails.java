package org.egov.works.bgsecurity.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.egov.model.bills.DocumentUpload;

@Entity
@Table(name = "txn_bgsecurity_details")
@SequenceGenerator(name = BGSecurityDetails.SEQ_BGSECURITY_DETAILS, sequenceName = BGSecurityDetails.SEQ_BGSECURITY_DETAILS, allocationSize = 1)
public class BGSecurityDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String SEQ_BGSECURITY_DETAILS = "SEQ_BGSECURITY_DETAILS";

	@Id
	@GeneratedValue(generator = SEQ_BGSECURITY_DETAILS, strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Long id;

	@Column(name = "security_validity")
	private String security_validity;
	
	@Column(name = "project_name")
	private String project_name;
	
	@Column(name = "security_tender_number")
	private String security_tender_number;
	
	@Column(name = "narration")
	private String narration;

	@Column(name = "security_amount")
	private Double security_amount;

	@Column(name = "security_start_date")
	private Date security_start_date;

	@Column(name = "security_end_date")
	private Date security_end_date;

	@Column(name = "loa_number")
	private String loaNumber;
	
	@Column(name = "security_number")
	private String security_number;

	@Transient
    private List<DocumentUpload> documentDetail = new ArrayList<>();
	
	@Transient
	private List<BGSecurityDetails> bgSecurityDetailsList=new ArrayList<BGSecurityDetails>();
	
	@Transient
	private Date fromDt;

	@Transient
	private Date toDt;
	
	@Transient
	private String bgStartDate;
	
	@Transient
	private String bgEndDate;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSecurity_validity() {
		return security_validity;
	}

	public void setSecurity_validity(String security_validity) {
		this.security_validity = security_validity;
	}

	public Double getSecurity_amount() {
		return security_amount;
	}

	public void setSecurity_amount(Double security_amount) {
		this.security_amount = security_amount;
	}

	public Date getSecurity_start_date() {
		return security_start_date;
	}

	public void setSecurity_start_date(Date security_start_date) {
		this.security_start_date = security_start_date;
	}

	public Date getSecurity_end_date() {
		return security_end_date;
	}

	public void setSecurity_end_date(Date security_end_date) {
		this.security_end_date = security_end_date;
	}

	public String getLoaNumber() {
		return loaNumber;
	}

	public void setLoaNumber(String loaNumber) {
		this.loaNumber = loaNumber;
	}

	

	public List<DocumentUpload> getDocumentDetail() {
		return documentDetail;
	}

	public void setDocumentDetail(List<DocumentUpload> documentDetail) {
		this.documentDetail = documentDetail;
	}

	public String getSecurity_number() {
		return security_number;
	}

	public void setSecurity_number(String security_number) {
		this.security_number = security_number;
	}

	public List<BGSecurityDetails> getBgSecurityDetailsList() {
		return bgSecurityDetailsList;
	}

	public void setBgSecurityDetailsList(List<BGSecurityDetails> bgSecurityDetailsList) {
		this.bgSecurityDetailsList = bgSecurityDetailsList;
	}

	public Date getFromDt() {
		return fromDt;
	}

	public void setFromDt(Date fromDt) {
		this.fromDt = fromDt;
	}

	public Date getToDt() {
		return toDt;
	}

	public void setToDt(Date toDt) {
		this.toDt = toDt;
	}

	public String getBgDate() {
		return bgStartDate;
	}

	public void setBgDate(String bgDate) {
		this.bgStartDate = bgDate;
	}

	public String getBgStartDate() {
		return bgStartDate;
	}

	public void setBgStartDate(String bgStartDate) {
		this.bgStartDate = bgStartDate;
	}

	public String getBgEndDate() {
		return bgEndDate;
	}

	public void setBgEndDate(String bgEndDate) {
		this.bgEndDate = bgEndDate;
	}

	public String getProject_name() {
		return project_name;
	}

	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}

	public String getSecurity_tender_number() {
		return security_tender_number;
	}

	public void setSecurity_tender_number(String security_tender_number) {
		this.security_tender_number = security_tender_number;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

}
