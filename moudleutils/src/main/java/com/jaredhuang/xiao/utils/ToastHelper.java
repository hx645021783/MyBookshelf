package com.jaredhuang.xiao.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Copyright (C), 2019, JIGUANG极光
 *
 * @Author: huangxiao
 * @CreateDate: 2019-12-31 11:21
 * @Description:
 */
public class ToastHelper {
    private static ToastHelper mToastHelper = new ToastHelper();

    public static ToastHelper getInstance() {
        return mToastHelper;
    }

    private Toast mToast;

    public ToastHelper init(Context mContext) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, "", 0);
        }
        return mToastHelper;
    }

    public void show(String msg) {
        mToast.setText(msg);
        mToast.show();
    }
}
