package com.model;

import java.io.Serializable;
import java.sql.Blob;

public class PictureTable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String title;
	private String pictureName;
	private Blob file2;

	public PictureTable() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPictureName() {
		return pictureName;
	}

	public void setPictureName(String pictureName) {
		this.pictureName = pictureName;
	}

	public Blob getFile2() {
		return file2;
	}

	public void setFile2(Blob file2) {
		this.file2 = file2;
	}
}