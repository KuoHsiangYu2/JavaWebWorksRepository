package com.dao;

import java.util.List;

import com.model.PictureTable;

public interface IPictureTableDao {

	int getPageNo();

	void setPageNo(int pageNo);

	int getTotalPages();

	int savePicture(PictureTable pictureTable);

	List<PictureTable> getPagePictureNoBlob(int pageNo);

	List<PictureTable> getAllPictureNoBlob();

	PictureTable getPictureWithBlobById(int index);

	int getCount();

	PictureTable getFullPictureDataById(int index);

	int saveAndUpdatePictureById(int index, PictureTable newObj, boolean needSaveFile);

	int deletePictureById(int index);

}