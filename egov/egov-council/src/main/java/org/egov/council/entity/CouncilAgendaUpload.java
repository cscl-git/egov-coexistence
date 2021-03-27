package org.egov.council.entity;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.egov.infra.filestore.entity.FileStoreMapper;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name="EGCNCL_AGENDA_UPLOAD")
@SequenceGenerator(name = CouncilAgendaUpload.SEQ_COUNCIALAGENDAUPLOAD, sequenceName = CouncilAgendaUpload.SEQ_COUNCIALAGENDAUPLOAD, allocationSize = 1)
public class CouncilAgendaUpload {
	 public static final String SEQ_COUNCIALAGENDAUPLOAD = "seq_CouncilAgendaUpload";
	 private static final long serialVersionUID = -2561739732877438517L;
	@Id
	@GeneratedValue(generator = SEQ_COUNCIALAGENDAUPLOAD, strategy = GenerationType.SEQUENCE)
	private long id;
	private String department;
	private String departmentName;
	
	private Date createdDate ;
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "filestoreid")
	private FileStoreMapper filestoreid;
	@Transient
	private transient MultipartFile attachments;
    
    private transient MultipartFile[] files;
    
	@Transient
	private Date fromDate;
	@Transient
	private Date toDate;

	

	




	

	


	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public MultipartFile getAttachments() {
		return attachments;
	}

	public void setAttachments(MultipartFile attachments) {
		this.attachments = attachments;
	}

	public FileStoreMapper getFilestoreid() {
		return filestoreid;
	}

	public void setFilestoreid(FileStoreMapper filestoreid) {
		this.filestoreid = filestoreid;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public MultipartFile[] getFiles() {
		return files;
	}

	public void setFiles(MultipartFile[] files) {
		this.files = files;
	}
	
	
}
