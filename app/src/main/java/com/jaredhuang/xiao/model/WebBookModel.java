//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.jaredhuang.xiao.model;

import android.annotation.SuppressLint;

import com.hwangjr.rxbus.RxBus;
import com.jaredhuang.xiao.DbHelper;
import com.jaredhuang.xiao.bean.BaseChapterBean;
import com.jaredhuang.xiao.bean.BookChapterBean;
import com.jaredhuang.xiao.bean.BookCollectBean;
import com.jaredhuang.xiao.bean.BookContentBean;
import com.jaredhuang.xiao.bean.BookInfoBean;
import com.jaredhuang.xiao.bean.SearchBookBean;
import com.jaredhuang.xiao.constant.RxBusTag;
import com.jaredhuang.xiao.help.BookCollectHelp;
import com.jaredhuang.xiao.model.content.WebBook;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

import static android.text.TextUtils.isEmpty;
import static com.jaredhuang.xiao.constant.AppConstant.TIME_OUT;

public class WebBookModel {

    public static WebBookModel getInstance() {
        return new WebBookModel();
    }

    /**
     * 网络请求并解析书籍信息
     * return BookCollectBean
     */
    public Observable<BookCollectBean> getBookInfo(BookCollectBean bookCollectBean) {
        return WebBook.getInstance(bookCollectBean.getDomain())
                .getBookInfo(bookCollectBean)
                .timeout(TIME_OUT, TimeUnit.SECONDS);
    }

    /**
     * 网络解析图书目录
     * return BookCollectBean
     */
    public Observable<List<BookChapterBean>> getChapterList(final BookCollectBean bookCollectBean) {
        return WebBook.getInstance(bookCollectBean.getDomain())
                .getChapterList(bookCollectBean)
                .flatMap((chapterList) -> upChapterList(bookCollectBean, chapterList))
                .timeout(TIME_OUT, TimeUnit.SECONDS);
    }

    /**
     * 章节缓存
     */
    public Observable<BookContentBean> getBookContent(BookCollectBean bookCollectBean, BaseChapterBean chapterBean, BaseChapterBean nextChapterBean) {
        return WebBook.getInstance(chapterBean.getDomain())
                .getBookContent(chapterBean, nextChapterBean, bookCollectBean)
                .timeout(TIME_OUT, TimeUnit.SECONDS);
    }

    /**
     * 搜索
     */
    public Observable<List<SearchBookBean>> searchBook(String content, int page, String tag) {
        return WebBook.getInstance(tag)
                .searchBook(content, page)
                .timeout(TIME_OUT, TimeUnit.SECONDS);
    }

    /**
     * 发现页
     */
    public Observable<List<SearchBookBean>> findBook(String url, int page, String tag) {
        return WebBook.getInstance(tag)
                .findBook(url, page)
                .timeout(TIME_OUT, TimeUnit.SECONDS);
    }

    /**
     * 更新目录
     */
    private Observable<List<BookChapterBean>> upChapterList(BookCollectBean bookCollectBean, List<BookChapterBean> chapterList) {
        return Observable.create(e -> {
            for (int i = 0; i < chapterList.size(); i++) {
                BookChapterBean chapter = chapterList.get(i);
                chapter.setDurChapterIndex(i);
                chapter.setDomain(bookCollectBean.getDomain());
                chapter.setNoteUrl(bookCollectBean.getNoteUrl());
            }
            if (bookCollectBean.getChapterListSize() < chapterList.size()) {
                bookCollectBean.setHasUpdate(true);
                bookCollectBean.setFinalRefreshData(System.currentTimeMillis());
                bookCollectBean.getBookInfoBean().setFinalRefreshData(System.currentTimeMillis());
            }
            if (!chapterList.isEmpty()) {
                bookCollectBean.setChapterListSize(chapterList.size());
                bookCollectBean.setDurChapter(Math.min(bookCollectBean.getDurChapter(), bookCollectBean.getChapterListSize() - 1));
                bookCollectBean.setDurChapterName(chapterList.get(bookCollectBean.getDurChapter()).getDurChapterName());
                bookCollectBean.setLastChapterName(chapterList.get(chapterList.size() - 1).getDurChapterName());
            }
            e.onNext(chapterList);
            e.onComplete();
        });
    }

    /**
     * 保存章节
     */
    @SuppressLint("DefaultLocale")
    private Observable<BookContentBean> saveContent(BookInfoBean infoBean, BaseChapterBean chapterBean, BookContentBean bookContentBean) {
        return Observable.create(e -> {
            bookContentBean.setNoteUrl(chapterBean.getNoteUrl());
            if (isEmpty(bookContentBean.getDurChapterContent())) {
                e.onError(new Throwable("下载章节出错"));
            } else if (infoBean.isAudio()) {
                bookContentBean.setTimeMillis(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
                DbHelper.getDaoSession().getBookContentBeanDao().insertOrReplace(bookContentBean);
                e.onNext(bookContentBean);
            } else if (BookCollectHelp.saveChapterInfo(infoBean.getName() + "-" + chapterBean.getDomain(), chapterBean.getDurChapterIndex(),
                    chapterBean.getDurChapterName(), bookContentBean.getDurChapterContent())) {
                RxBus.get().post(RxBusTag.CHAPTER_CHANGE, chapterBean);
                e.onNext(bookContentBean);
            } else {
                e.onError(new Throwable("保存章节出错"));
            }
            e.onComplete();
        });
    }
}
