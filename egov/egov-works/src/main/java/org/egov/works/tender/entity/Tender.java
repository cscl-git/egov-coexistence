package org.egov.works.tender.entity;

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

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "txn_tender")
@SequenceGenerator(name = Tender.SEQ_TENDER, sequenceName = Tender.SEQ_TENDER, allocationSize = 1)

public class Tender implements java.io.Serializable {

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

	@Column(name = "loa_number")
	private Double loaNumber;

	@Transient
	private Date fromDt;

	@Transient
	private Date toDt;

	@Transient
	private List<Tender> tenderList;

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

	public Double getLoaNumber() {
		return loaNumber;
	}

	public void setLoaNumber(Double loaNumber) {
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

}
