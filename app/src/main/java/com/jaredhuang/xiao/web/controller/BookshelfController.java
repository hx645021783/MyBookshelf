package com.jaredhuang.xiao.web.controller;

import android.text.TextUtils;

import com.jaredhuang.xiao.DbHelper;
import com.jaredhuang.xiao.bean.BookChapterBean;
import com.jaredhuang.xiao.bean.BookCollectBean;
import com.jaredhuang.xiao.bean.BookContentBean;
import com.jaredhuang.xiao.help.BookCollectHelp;
import com.jaredhuang.xiao.model.WebBookModel;
import com.jaredhuang.xiao.utils.GsonUtils;
import com.jaredhuang.xiao.web.utils.ReturnData;

import java.util.List;
import java.util.Map;

public class BookshelfController {

    public ReturnData getBookshelf() {
        List<BookCollectBean> shelfBeans = BookCollectHelp.getAllBook();
        ReturnData returnData = new ReturnData();
        if (shelfBeans.isEmpty()) {
            return returnData.setErrorMsg("还没有添加小说");
        }
        return returnData.setData(shelfBeans);
    }

    public ReturnData getChapterList(Map<String, List<String>> parameters) {
        List<String> strings = parameters.get("url");
        ReturnData returnData = new ReturnData();
        if (strings == null) {
            return returnData.setErrorMsg("参数url不能为空，请指定书籍地址");
        }
        List<BookChapterBean> chapterList = BookCollectHelp.getChapterList(strings.get(0));
        return returnData.setData(chapterList);
    }

    public ReturnData getBookContent(Map<String, List<String>> parameters) {
        List<String> strings = parameters.get("url");
        ReturnData returnData = new ReturnData();
        if (strings == null) {
            return returnData.setErrorMsg("参数url不能为空，请指定内容地址");
        }
        BookChapterBean chapter = DbHelper.getDaoSession().getBookChapterBeanDao().load(strings.get(0));
        if (chapter == null) {
            return returnData.setErrorMsg("未找到");
        }
        BookCollectBean bookCollectBean = BookCollectHelp.getBook(chapter.getNoteUrl());
        if (bookCollectBean == null) {
            return returnData.setErrorMsg("未找到");
        }
        String content = BookCollectHelp.getChapterCache(bookCollectBean, chapter);
        if (!TextUtils.isEmpty(content)) {
            return returnData.setData(content);
        }
        try {
            BookContentBean bookContentBean = WebBookModel.getInstance().getBookContent(bookCollectBean, chapter, null).blockingFirst();
            return returnData.setData(bookContentBean.getDurChapterContent());
        } catch (Exception e) {
            return returnData.setErrorMsg(e.getMessage());
        }
    }

    public ReturnData saveBook(String postData) {
        BookCollectBean bookCollectBean = GsonUtils.parseJObject(postData, BookCollectBean.class);
        ReturnData returnData = new ReturnData();
        if (bookCollectBean != null) {
            DbHelper.getDaoSession().getBookCollectBeanDao().insertOrReplace(bookCollectBean);
            return returnData.setData("");
        }
        return returnData.setErrorMsg("格式不对");
    }

}
