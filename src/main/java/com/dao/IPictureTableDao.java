package com.dao;

import java.util.List;

import com.model.PictureTableSearchType;
import com.model.PictureTableTwo;

public interface IPictureTableDao {

    int getPageNo();

    void setPageNo(int pageNo);

    int getTotalPages();

    int savePicture(PictureTableTwo pictureTable);

    List<PictureTableTwo> getPagePicture(int pageNo);

    List<PictureTableTwo> getAllPicture();

    int getCount();

    PictureTableTwo getFullPictureDataById(int index);

    int saveAndUpdatePictureById(int index, PictureTableTwo newObj);

    int deletePictureById(int index);

    int resetTypeNameList(List<PictureTableTwo> updateTypeNameList);

    List<PictureTableTwo> getPagePicture(int pageNo, String targetString, PictureTableSearchType type);

    int getTotalPages(String targetString, PictureTableSearchType type);

}