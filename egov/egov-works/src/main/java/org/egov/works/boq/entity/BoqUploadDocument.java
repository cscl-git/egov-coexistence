package org.egov.works.boq.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "EGF_BoqUpload")
@SequenceGenerator(name = BoqUploadDocument.SEQ_EGF_BOQUPLOAD, sequenceName = BoqUploadDocument.SEQ_EGF_BOQUPLOAD, allocationSize = 1)
public class BoqUploadDocument implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final String SEQ_EGF_BOQUPLOAD = "SEQ_EGF_BOQUPLOAD";

    @Id
    @GeneratedValue(generator = SEQ_EGF_BOQUPLOAD, strategy = GenerationType.SEQUENCE)
    @Column(name="id")
    private Long id;

    @Column(name = "objectid")
    private Long objectId;
    
    @Column(name = "filestoreid")
    private Long filestoreid;
    
    @Column(name="objectType")
    private String objectType;
    
    @Column(name="comments")
    private String comments;
    
    @Column(name="username")
    private String username;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public Long getFilestoreid() {
		return filestoreid;
	}

	public void setFilestoreid(Long filestoreid) {
		this.filestoreid = filestoreid;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
