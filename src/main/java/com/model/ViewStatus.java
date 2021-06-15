package com.model;

import java.io.Serializable;

public class ViewStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean viewEmpty;/* 畫面資料是否為空？ */

    public ViewStatus() {
        super();
    }

    public boolean isViewEmpty() {
        return viewEmpty;
    }

    public void setViewEmpty(boolean viewEmpty) {
        this.viewEmpty = viewEmpty;
    }
}