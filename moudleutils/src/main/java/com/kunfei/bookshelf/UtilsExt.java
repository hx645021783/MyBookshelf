package com.kunfei.bookshelf;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Copyright (C), 2019, JIGUANG极光
 *
 * @Author: huangxiao
 * @CreateDate: 2019-12-24 14:56
 * @Description:
 */
public class UtilsExt {
    private static UtilsExt utilsExt=null;
    private UtilsExt() {
    }

    public static UtilsExt getInstance(){
        if(utilsExt==null)utilsExt=new UtilsExt();
        return utilsExt;
    }

    private Context mContext=null;

    public void init(Context mContext){
        this.mContext=mContext;
    }

    public Context getContext() {
        return mContext;
    }
}
