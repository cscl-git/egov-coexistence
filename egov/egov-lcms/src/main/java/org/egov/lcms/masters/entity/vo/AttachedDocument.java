package org.egov.lcms.masters.entity.vo;

import java.io.InputStream;

public class AttachedDocument {
	private InputStream fileStream;
	private String fileName;
	private String mimeType;
	public InputStream getFileStream() {
		return fileStream;
	}
	public void setFileStream(InputStream fileStream) {
		this.fileStream = fileStream;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}	
}
