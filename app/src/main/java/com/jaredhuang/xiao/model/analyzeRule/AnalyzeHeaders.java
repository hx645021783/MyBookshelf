package com.jaredhuang.xiao.model.analyzeRule;

import android.content.SharedPreferences;

import com.jaredhuang.xiao.DbHelper;
import com.jaredhuang.xiao.MApplication;
import com.kunfei.bookshelf.R;
import com.jaredhuang.xiao.bean.BookSourceBean;
import com.jaredhuang.xiao.bean.CookieBean;

import java.util.HashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;
import static com.jaredhuang.xiao.constant.AppConstant.DEFAULT_USER_AGENT;

/**
 * Created by GKF on 2018/3/2.
 * 解析Headers
 */

public class AnalyzeHeaders {
    private static SharedPreferences preferences = MApplication.getConfigPreferences();

    public static Map<String, String> getMap(BookSourceBean bookSourceBean) {
        Map<String, String> headerMap = new HashMap<>();
        if (bookSourceBean != null && !isEmpty(bookSourceBean.getHttpUserAgent())) {
            headerMap.put("User-Agent", bookSourceBean.getHttpUserAgent());
        } else {
            headerMap.put("User-Agent", getDefaultUserAgent());
        }
        if (bookSourceBean != null) {
            CookieBean cookie = DbHelper.getDaoSession().getCookieBeanDao().load(bookSourceBean.getBookSourceUrl());
            if (cookie != null) {
                headerMap.put("Cookie", cookie.getCookie());
            }
        }
        return headerMap;
    }

    private static String getDefaultUserAgent() {
        return preferences.getString(MApplication.getInstance().getString(R.string.pk_user_agent), DEFAULT_USER_AGENT);
    }
}
