package com.jaredhuang.xiao;

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
    public static String NAME_SHAREDPREFERENCES ="READ_CONFIG";
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
        configPreferences = mContext.getSharedPreferences("CONFIG", 0);
    }
    public SharedPreferences getConfigPreferences() {
        return configPreferences;
    }

    public Context getContext() {
        return mContext;
    }
}
