//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.jaredhuang.xiao.bean;

import com.google.gson.Gson;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Objects;

/**
 * 章节列表
 */
@Entity
public class BookChapterBean implements Cloneable, BaseChapterBean {
    private String domain;
    private String noteUrl; //对应BookInfoBean noteUrl;

    private int durChapterIndex;  //当前章节数
    @Id
    private String durChapterUrl;  //当前章节对应的文章地址
    private String durChapterName;  //当前章节名称

    //章节内容在文章中的起始位置(本地)
    private Long start;
    //章节内容在文章中的终止位置(本地)
    private Long end;

    public BookChapterBean() {
    }



    public BookChapterBean(String domain, String durChapterName, String durChapterUrl) {
        this.domain = domain;
        this.durChapterName = durChapterName;
        this.durChapterUrl = durChapterUrl;
    }



    @Generated(hash = 589871795)
    public BookChapterBean(String domain, String noteUrl, int durChapterIndex,
            String durChapterUrl, String durChapterName, Long start, Long end) {
        this.domain = domain;
        this.noteUrl = noteUrl;
        this.durChapterIndex = durChapterIndex;
        this.durChapterUrl = durChapterUrl;
        this.durChapterName = durChapterName;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Object clone() {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(this);
            return gson.fromJson(json, BookChapterBean.class);
        } catch (Exception ignored) {
        }
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BookChapterBean) {
            BookChapterBean bookChapterBean = (BookChapterBean) obj;
            return Objects.equals(bookChapterBean.durChapterUrl, durChapterUrl);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (durChapterUrl == null) {
            return 0;
        }
        return durChapterUrl.hashCode();
    }

    @Override
    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String getDurChapterName() {
        return this.durChapterName;
    }

    public void setDurChapterName(String durChapterName) {
        this.durChapterName = durChapterName;
    }

    @Override
    public String getDurChapterUrl() {
        return this.durChapterUrl;
    }

    public void setDurChapterUrl(String durChapterUrl) {
        this.durChapterUrl = durChapterUrl;
    }

    @Override
    public int getDurChapterIndex() {
        return this.durChapterIndex;
    }

    public void setDurChapterIndex(int durChapterIndex) {
        this.durChapterIndex = durChapterIndex;
    }

    @Override
    public String getNoteUrl() {
        return this.noteUrl;
    }

    public void setNoteUrl(String noteUrl) {
        this.noteUrl = noteUrl;
    }

    public Long getStart() {
        return this.start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return this.end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "BookChapterBean{" +
                "domain='" + domain + '\'' +
                ", noteUrl='" + noteUrl + '\'' +
                ", durChapterIndex=" + durChapterIndex +
                ", durChapterUrl='" + durChapterUrl + '\'' +
                ", durChapterName='" + durChapterName + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
