//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.jaredhuang.xiao.bean;

public class LocBookShelfBean {
    private Boolean isNew;
    private BookCollectBean bookCollectBean;

    public LocBookShelfBean(Boolean isNew, BookCollectBean bookCollectBean) {
        this.isNew = isNew;
        this.bookCollectBean = bookCollectBean;
    }

    public Boolean getNew() {
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }

    public BookCollectBean getBookCollectBean() {
        return bookCollectBean;
    }

    public void setBookCollectBean(BookCollectBean bookCollectBean) {
        this.bookCollectBean = bookCollectBean;
    }
}
