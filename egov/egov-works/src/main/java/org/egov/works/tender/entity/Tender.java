package org.egov.works.tender.entity;

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

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "txn_tender")
@SequenceGenerator(name = Tender.SEQ_TENDER, sequenceName = Tender.SEQ_TENDER, allocationSize = 1)

public class Tender implements  java.io.Serializable {
	
	private static final long serialVersionUID = -4312140421386028968L;
	public static final String SEQ_TENDER = "SEQ_TENDER";

	@Id
	@GeneratedValue(generator = SEQ_TENDER, strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Long id;

	@Column(name = "procurement_amount")
	private Double procurementAmount;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Kolkata")
	@Column(name = "procurement_date")
	private Date procurementDate;

	@Column(name = "contractor_details")
	private String contractorDetails;
	
	@Column(name = "project_name")
	private String project_name;

	@Column(name = "tender_number")
	private String loaNumber;
	
	@Column(name = "loa_number")
	private String tenderProNumber;

	@Transient
	private Date fromDt;

	@Transient
	private Date toDt;

	@Transient
	private List<Tender> tenderList;
	
	@Transient
	private String tenderDate;
	@Transient
	private String project_name_search;
	
	@Transient
    private List<org.egov.model.bills.DocumentUpload> documentDetail = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getProcurementAmount() {
		return procurementAmount;
	}

	public void setProcurementAmount(Double procurementAmount) {
		this.procurementAmount = procurementAmount;
	}

	public Date getProcurementDate() {
		return procurementDate;
	}

	public void setProcurementDate(Date procurementDate) {
		this.procurementDate = procurementDate;
	}

	public String getContractorDetails() {
		return contractorDetails;
	}

	public void setContractorDetails(String contractorDetails) {
		this.contractorDetails = contractorDetails;
	}

	public String getLoaNumber() {
		return loaNumber;
	}

	public void setLoaNumber(String loaNumber) {
		this.loaNumber = loaNumber;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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

	public List<Tender> getTenderList() {
		return tenderList;
	}

	public void setTenderList(List<Tender> tenderList) {
		this.tenderList = tenderList;
	}

	public List<org.egov.model.bills.DocumentUpload> getDocumentDetail() {
		return documentDetail;
	}

	public void setDocumentDetail(List<org.egov.model.bills.DocumentUpload> documentDetail) {
		this.documentDetail = documentDetail;
	}

	public String getTenderDate() {
		return tenderDate;
	}

	public void setTenderDate(String tenderDate) {
		this.tenderDate = tenderDate;
	}

	public String getTenderProNumber() {
		return tenderProNumber;
	}

	public void setTenderProNumber(String tenderProNumber) {
		this.tenderProNumber = tenderProNumber;
	}

	public String getProject_name() {
		return project_name;
	}

	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}

	public String getProject_name_search() {
		return project_name_search;
	}

	public void setProject_name_search(String project_name_search) {
		this.project_name_search = project_name_search;
	}

}
