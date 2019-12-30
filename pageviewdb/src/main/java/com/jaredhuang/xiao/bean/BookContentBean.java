//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.jaredhuang.xiao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 书本缓存内容
 */
@Entity
public class BookContentBean {
    private String noteUrl; //对应BookInfoBean noteUrl;
    @Id
    private String durChapterUrl;

    private Integer durChapterIndex;   //当前章节  （包括番外）

    private String durChapterContent; //当前章节内容

    private String domain;   //来源  某个网站/本地

    private Long timeMillis;

    public BookContentBean() {

    }

    @Generated(hash = 2138814505)
    public BookContentBean(String noteUrl, String durChapterUrl,
            Integer durChapterIndex, String durChapterContent, String domain,
            Long timeMillis) {
        this.noteUrl = noteUrl;
        this.durChapterUrl = durChapterUrl;
        this.durChapterIndex = durChapterIndex;
        this.durChapterContent = durChapterContent;
        this.domain = domain;
        this.timeMillis = timeMillis;
    }

    public String getDurChapterUrl() {
        return durChapterUrl;
    }

    public void setDurChapterUrl(String durChapterUrl) {
        this.durChapterUrl = durChapterUrl;
    }

    public int getDurChapterIndex() {
        return durChapterIndex;
    }

    public void setDurChapterIndex(int durChapterIndex) {
        this.durChapterIndex = durChapterIndex;
    }

    public String getDurChapterContent() {
        return durChapterContent;
    }

    public void setDurChapterContent(String durChapterContent) {
        this.durChapterContent = durChapterContent;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getNoteUrl() {
        return this.noteUrl;
    }

    public void setNoteUrl(String noteUrl) {
        this.noteUrl = noteUrl;
    }

    public void setDurChapterIndex(Integer durChapterIndex) {
        this.durChapterIndex = durChapterIndex;
    }

    public Long getTimeMillis() {
        return this.timeMillis;
    }

    public void setTimeMillis(Long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public boolean outTime() {
        if (timeMillis == null) {
            return true;
        }
        return timeMillis < System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "BookContentBean{" +
                "noteUrl='" + noteUrl + '\'' +
                ", durChapterUrl='" + durChapterUrl + '\'' +
                ", durChapterIndex=" + durChapterIndex +
                ", durChapterContent='" + durChapterContent + '\'' +
                ", domain='" + domain + '\'' +
                ", timeMillis=" + timeMillis +
                '}';
    }
}
