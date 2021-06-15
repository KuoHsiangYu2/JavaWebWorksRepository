package com.model;

import java.io.Serializable;

/* 儲存圖片相關資料 */
public class PictureTableTwo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id; /* primary key 主鍵 */
    private String title; /* 圖片標題 */
    private String pictureName; /* 圖片檔案名稱 */
    private String typeName; /* 圖片分類 */

    public PictureTableTwo() {
        super();
    }

    @Override
    public String toString() {
        return "PictureTableTwo [id=" + id + ", title=" + title + ", pictureName=" + pictureName + ", typeName="
                + typeName + "]";
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

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

}