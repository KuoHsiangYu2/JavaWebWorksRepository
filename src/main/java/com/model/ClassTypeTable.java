package com.model;

import java.io.Serializable;

// 儲存分類資料
public class ClassTypeTable implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id; // primary key 主鍵
    private String typeName; // 圖片分類標籤

    public ClassTypeTable() {
        super();
    }

    @Override
    public String toString() {
        return "ClassTypeTable [id=" + id + ", typeName=" + typeName + "]";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
