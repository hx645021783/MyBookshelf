package com.jaredhuang.xiao.model.content;

import android.text.TextUtils;

import com.jaredhuang.xiao.base.BaseModelImpl;
import com.jaredhuang.xiao.bean.BaseChapterBean;
import com.jaredhuang.xiao.bean.BookChapterBean;
import com.jaredhuang.xiao.bean.BookContentBean;
import com.jaredhuang.xiao.bean.BookCollectBean;
import com.jaredhuang.xiao.bean.BookSourceBean;
import com.jaredhuang.xiao.bean.SearchBookBean;
import com.jaredhuang.xiao.model.BookSourceManager;
import com.jaredhuang.xiao.model.analyzeRule.AnalyzeHeaders;
import com.jaredhuang.xiao.model.analyzeRule.AnalyzeUrl;
import com.jaredhuang.xiao.throwable.NoSourceThrowable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;

import io.reactivex.Observable;

import static android.text.TextUtils.isEmpty;
import static com.jaredhuang.xiao.constant.AppConstant.JS_PATTERN;

/**
 * 默认检索规则
 */
public class WebBook extends BaseModelImpl {
    private String tag;
    private String name;
    private BookSourceBean bookSourceBean;
    private Map<String, String> headerMap;

    public static WebBook getInstance(String tag) {
        return new WebBook(tag);
    }

    private WebBook(String tag) {
        this.tag = tag;
        try {
            URL url = new URL(tag);
            name = url.getHost();
        } catch (MalformedURLException e) {
            name = tag;
        }
        bookSourceBean = BookSourceManager.getBookSourceByUrl(tag);
        if (bookSourceBean != null) {
            name = bookSourceBean.getBookSourceName();
            headerMap = AnalyzeHeaders.getMap(bookSourceBean);
        }
    }

    /**
     * 发现
     */
    public Observable<List<SearchBookBean>> findBook(String url, int page) {
        if (bookSourceBean == null) {
            return Observable.error(new NoSourceThrowable(tag));
        }
        BookList bookList = new BookList(tag, name, bookSourceBean, true);
        try {
            AnalyzeUrl analyzeUrl = new AnalyzeUrl(url, null, page, headerMap, tag);
            return getResponseO(analyzeUrl)
                    .flatMap(bookList::analyzeSearchBook);
        } catch (Exception e) {
            return Observable.error(new Throwable(String.format("%s错误:%s", url, e.getLocalizedMessage())));
        }
    }

    /**
     * 搜索
     */
    public Observable<List<SearchBookBean>> searchBook(String content, int page) {
        if (bookSourceBean == null || isEmpty(bookSourceBean.getRuleSearchUrl())) {
            return Observable.create(emitter -> {
                emitter.onNext(new ArrayList<>());
                emitter.onComplete();
            });
        }
        BookList bookList = new BookList(tag, name, bookSourceBean, false);
        try {
            AnalyzeUrl analyzeUrl = new AnalyzeUrl(bookSourceBean.getRuleSearchUrl(), content, page, headerMap, tag);
            return getResponseO(analyzeUrl)
                    .flatMap(bookList::analyzeSearchBook);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    /**
     * 获取书籍信息
     */
    public Observable<BookCollectBean> getBookInfo(final BookCollectBean bookCollectBean) {
        if (bookSourceBean == null) {
            return Observable.error(new NoSourceThrowable(tag));
        }
        BookInfo bookInfo = new BookInfo(tag, name, bookSourceBean);
        if (!TextUtils.isEmpty(bookCollectBean.getBookInfoBean().getBookInfoHtml())) {
            return bookInfo.analyzeBookInfo(bookCollectBean.getBookInfoBean().getBookInfoHtml(), bookCollectBean);
        }
        try {
            AnalyzeUrl analyzeUrl = new AnalyzeUrl(bookCollectBean.getNoteUrl(), headerMap, tag);
            return getResponseO(analyzeUrl)
                    .flatMap(response -> setCookie(response, tag))
                    .flatMap(response -> bookInfo.analyzeBookInfo(response.body(), bookCollectBean));
        } catch (Exception e) {
            return Observable.error(new Throwable(String.format("url错误:%s", bookCollectBean.getNoteUrl())));
        }
    }

    /**
     * 获取目录
     */
    public Observable<List<BookChapterBean>> getChapterList(final BookCollectBean bookCollectBean) {
        if (bookSourceBean == null) {
            return Observable.error(new NoSourceThrowable(bookCollectBean.getBookInfoBean().getName()));
        }
        BookChapterList bookChapterList = new BookChapterList(tag, bookSourceBean, true);
        if (!TextUtils.isEmpty(bookCollectBean.getBookInfoBean().getChapterListHtml())) {
            return bookChapterList.analyzeChapterList(bookCollectBean.getBookInfoBean().getChapterListHtml(), bookCollectBean, headerMap);
        }
        try {
            AnalyzeUrl analyzeUrl = new AnalyzeUrl(bookCollectBean.getBookInfoBean().getChapterUrl(), headerMap, bookCollectBean.getNoteUrl());
            return getResponseO(analyzeUrl)

                    .flatMap(response -> setCookie(response, tag))
                    .flatMap(response -> bookChapterList.analyzeChapterList(response.body(), bookCollectBean, headerMap));
        } catch (Exception e) {
            return Observable.error(new Throwable(String.format("url错误:%s", bookCollectBean.getBookInfoBean().getChapterUrl())));
        }
    }

    /**
     * 获取正文
     */
    public Observable<BookContentBean> getBookContent(final BaseChapterBean chapterBean, final BaseChapterBean nextChapterBean, final BookCollectBean bookCollectBean) {
        if (bookSourceBean == null) {
            return Observable.error(new NoSourceThrowable(chapterBean.getTag()));
        }
        if (isEmpty(bookSourceBean.getRuleBookContent())) {
            return Observable.create(emitter -> {
                BookContentBean bookContentBean = new BookContentBean();
                bookContentBean.setDurChapterContent(chapterBean.getDurChapterUrl());
                bookContentBean.setDurChapterIndex(chapterBean.getDurChapterIndex());
                bookContentBean.setTag(bookCollectBean.getTag());
                bookContentBean.setDurChapterUrl(chapterBean.getDurChapterUrl());
                emitter.onNext(bookContentBean);
                emitter.onComplete();
            });
        }
        BookContent bookContent = new BookContent(tag, bookSourceBean);
        if (Objects.equals(chapterBean.getDurChapterUrl(), bookCollectBean.getBookInfoBean().getChapterUrl())
                && !TextUtils.isEmpty(bookCollectBean.getBookInfoBean().getChapterListHtml())) {
            return bookContent.analyzeBookContent(bookCollectBean.getBookInfoBean().getChapterListHtml(), chapterBean, nextChapterBean, bookCollectBean, headerMap);
        }
        try {
            AnalyzeUrl analyzeUrl = new AnalyzeUrl(chapterBean.getDurChapterUrl(), headerMap, bookCollectBean.getBookInfoBean().getChapterUrl());
            String contentRule = bookSourceBean.getRuleBookContent();
            if (contentRule.startsWith("$") && !contentRule.startsWith("$.")) {
                //动态网页第一个js放到webView里执行
                contentRule = contentRule.substring(1);
                String js = null;
                Matcher jsMatcher = JS_PATTERN.matcher(contentRule);
                if (jsMatcher.find()) {
                    js = jsMatcher.group();
                    if (js.startsWith("<js>")) {
                        js = js.substring(4, js.lastIndexOf("<"));
                    } else {
                        js = js.substring(4);
                    }
                }
                return getAjaxString(analyzeUrl, tag, js)
                        .flatMap(response -> bookContent.analyzeBookContent(response, chapterBean, nextChapterBean, bookCollectBean, headerMap));
            } else {
                return getResponseO(analyzeUrl)
                        .flatMap(response -> setCookie(response, tag))
                        .flatMap(response -> bookContent.analyzeBookContent(response, chapterBean, nextChapterBean, bookCollectBean, headerMap));
            }
        } catch (Exception e) {
            return Observable.error(new Throwable(String.format("url错误:%s", chapterBean.getDurChapterUrl())));
        }
    }
}
