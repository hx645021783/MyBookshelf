package com.jaredhuang.xiao;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.jaredhuang.xiao.help.FileHelp;

import java.io.File;

/**
 * Copyright (C), 2019, JIGUANG极光
 *
 * @Author: huangxiao
 * @CreateDate: 2019-12-24 15:37
 * @Description:
 */
public class ReadViewExt {
    private static ReadViewExt readViewExt = null;
    private String bookCachePath;
    private boolean isReadAloudRunning = false;
    private ReadViewExt() {
    }

    public static ReadViewExt getInstance() {
        if (readViewExt == null) readViewExt = new ReadViewExt();
        return readViewExt;
    }

    private Context mContext = null;

    public void init(Context mContext) {
        this.mContext = mContext;
        String downloadPath = FileHelp.getFilesPath(mContext);
        bookCachePath = downloadPath + File.separator + "book_cache" + File.separator;
    }

    public String getBookCachePath() {
        return bookCachePath;
    }

    public boolean isReadAloudRunning() {
        return isReadAloudRunning;
    }

    public void setReadAloudRunning(boolean readAloudRunning) {
        isReadAloudRunning = readAloudRunning;
    }

    /**
     * 设置下载地址
     */
    public void setBookCachePath(String bookCachePath) {
        this.bookCachePath = bookCachePath;
    }

    public Context getContext() {
        return mContext;
    }

    public void initNightTheme() {
        if (isNightTheme()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public boolean isNightTheme() {
        return ReadDataExt.getInstance().getConfigPreferences().getBoolean("nightTheme", false);
    }
}
