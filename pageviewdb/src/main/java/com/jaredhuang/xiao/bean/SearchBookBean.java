//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.jaredhuang.xiao.bean;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.jaredhuang.xiao.DbHelper;
import com.jaredhuang.xiao.utils.StringUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
public class SearchBookBean implements BaseBookBean {
    private static Pattern chapterNamePattern = Pattern.compile("^(.*?第([\\d零〇一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟０-９\\s]+)[章节篇回集])[、，。　：:.\\s]*");


    @Id
    private String noteUrl;//目录url
    private String coverUrl;//封面URL
    private String name;//小说名称
    private String author;//作者
    private String domain;// 网站域名
    private String kind;//分类
    private String origin;//来源名称
    private String lastChapter;
    private String introduce; //简介
    private String chapterUrl;//目录URL
    private Long addTime = 0L;
    private Long upTime = 0L;
    private String variable;

    @Transient
    private Boolean isCurrentSource = false;
    @Transient
    private int originNum = 1;
    @Transient
    private int lastChapterNum = -2;
    @Transient
    private int searchTime = Integer.MAX_VALUE;
    @Transient
    private LinkedHashSet<String> originUrls;
    @Transient
    private Map<String, String> variableMap;
    @Transient
    private String bookInfoHtml;

    public SearchBookBean() {

    }

    /**
     * 手动新增
     * @param domain   网站域名
     * @param origin
     */
    public SearchBookBean(String domain, String origin) {
        this.domain = domain;
        this.origin = origin;
    }

    @Generated(hash = 887441386)
    public SearchBookBean(String noteUrl, String coverUrl, String name, String author, String domain, String kind, String origin,
            String lastChapter, String introduce, String chapterUrl, Long addTime, Long upTime, String variable) {
        this.noteUrl = noteUrl;
        this.coverUrl = coverUrl;
        this.name = name;
        this.author = author;
        this.domain = domain;
        this.kind = kind;
        this.origin = origin;
        this.lastChapter = lastChapter;
        this.introduce = introduce;
        this.chapterUrl = chapterUrl;
        this.addTime = addTime;
        this.upTime = upTime;
        this.variable = variable;
    }

    @Override
    public String getVariable() {
        return this.variable;
    }

    @Override
    public void setVariable(String variable) {
        this.variable = variable;
    }

    @Override
    public void putVariable(String key, String value) {
        if (variableMap == null) {
            variableMap = new HashMap<>();
        }
        variableMap.put(key, value);
        variable = new Gson().toJson(variableMap);
    }

    @Override
    public Map<String, String> getVariableMap() {
        if (variableMap == null) {
            return new Gson().fromJson(variable, new TypeToken<Map<String, String>>() {
            }.getType());
        }
        return variableMap;
    }

    @Override
    public String getNoteUrl() {
        return noteUrl;
    }

    @Override
    public void setNoteUrl(String noteUrl) {
        this.noteUrl = noteUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim().replaceAll("　", "") : null;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = formatAuthor(author);
    }

    public String getLastChapter() {
        return lastChapter == null ? "" : lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;

    }
    public  String formatAuthor(String author) {
        if (author == null) {
            return "";
        }
        return author.replaceAll("作\\s*者[\\s:：]*", "").replaceAll("\\s+", " ").trim();
    }
    public int getLastChapterNum() {
        if (lastChapterNum == -2)
            this.lastChapterNum = guessChapterNum(lastChapter);
        return lastChapterNum;
    }

    public  int guessChapterNum(String name) {
        if (TextUtils.isEmpty(name) || name.matches("第.*?卷.*?第.*[章节回]"))
            return -1;
        Matcher matcher = chapterNamePattern.matcher(name);
        if (matcher.find()) {
            return StringUtils.stringToInt(matcher.group(2));
        }
        return -1;
    }
    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Boolean getIsCurrentSource() {
        return this.isCurrentSource;
    }

    public void setIsCurrentSource(Boolean isCurrentSource) {
        this.isCurrentSource = isCurrentSource;
        if (isCurrentSource)
            this.addTime = System.currentTimeMillis();
    }

    public int getOriginNum() {
        return originNum;
    }

    public void addOriginUrl(String origin) {
        if (this.originUrls == null) {
            this.originUrls = new LinkedHashSet<>();
        }
        this.originUrls.add(origin);
        originNum = this.originUrls.size();
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getChapterUrl() {
        return this.chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    public long getAddTime() {
        return this.addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public int getWeight() {

        BookSourceBean source =DbHelper.getDaoSession().getBookSourceBeanDao().load(this.domain);
        if (source != null)
            return source.getWeight();
        else
            return 0;
    }

    public int getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(int searchTime) {
        this.searchTime = searchTime;
    }

    public Long getUpTime() {
        return this.upTime;
    }

    public void setUpTime(Long upTime) {
        this.upTime = upTime;
    }

    public String getBookInfoHtml() {
        return bookInfoHtml;
    }

    public void setBookInfoHtml(String bookInfoHtml) {
        this.bookInfoHtml = bookInfoHtml;
    }

    // 一次性存入搜索书籍节点信息
    public void setSearchInfo(String name, String author, String kind, String lastChapter,
                              String introduce, String coverUrl, String noteUrl) {
        this.name = name;
        this.author = author;
        this.kind = kind;
        this.lastChapter = lastChapter;
        this.introduce = introduce;
        this.coverUrl = coverUrl;
        this.noteUrl = noteUrl;
    }

}
