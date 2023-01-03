package com.dao;

import java.util.List;

import com.model.PictureTableSearchType;
import com.model.PictureTableTwo;

public interface IPictureTableDao {

    public abstract int getPageNo();

    public abstract void setPageNo(int pageNo);

    public abstract int getTotalPages();

    public abstract int savePicture(PictureTableTwo pictureTable);

    public abstract List<PictureTableTwo> getPagePicture(int pageNo);

    public abstract List<PictureTableTwo> getAllPicture();

    public abstract int getCount();

    public abstract PictureTableTwo getFullPictureDataById(int index);

    public abstract int saveAndUpdatePictureById(int index, PictureTableTwo newObj);

    public abstract int deletePictureById(int index);

    public abstract int resetTypeNameList(List<PictureTableTwo> updateTypeNameList);

    public abstract List<PictureTableTwo> getPagePicture(int pageNo, String targetString, PictureTableSearchType type);

    public abstract int getTotalPages(String targetString, PictureTableSearchType type);

}