package com.eec.data;

import java.io.Serializable;

public class FileUploadData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private byte[] file;
	
	private String userId;
	
	private String fileName;

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
