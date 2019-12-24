package com.jaredhuang.bookdb;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Copyright (C), 2019, JIGUANG极光
 *
 * @Author: huangxiao
 * @CreateDate: 2019-12-24 14:30
 * @Description:
 */
public class ReadDataExt {
    private static ReadDataExt readDataExt =null;
    private ReadDataExt() {
    }

    public static ReadDataExt getInstance(){
        if(readDataExt ==null) readDataExt =new ReadDataExt();
        return readDataExt;
    }


    private  Context mContext=null;
    private SharedPreferences configPreferences;

    public void init(Context mContext){
        this.mContext=mContext;
    }

    public void setConfigPreferences(SharedPreferences configPreferences) {
        this.configPreferences = configPreferences;
    }

    public SharedPreferences getConfigPreferences() {
        return configPreferences;
    }

    public Context getContext() {
        return mContext;
    }
}
