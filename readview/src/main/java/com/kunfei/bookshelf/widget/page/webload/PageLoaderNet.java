package com.kunfei.bookshelf.widget.page.webload;

import android.annotation.SuppressLint;
import android.widget.Toast;

import com.kunfei.bookshelf.bean.BaseChapterBean;
import com.kunfei.bookshelf.bean.BookChapterBean;
import com.kunfei.bookshelf.bean.BookContentBean;
import com.kunfei.bookshelf.bean.BookShelfBean;
import com.kunfei.bookshelf.help.BookshelfHelp;
import com.kunfei.bookshelf.throwable.NoSourceThrowable;
import com.kunfei.bookshelf.throwable.VipThrowable;
import com.kunfei.bookshelf.utils.NetworkUtils;
import com.kunfei.bookshelf.utils.RxUtils;
import com.kunfei.bookshelf.widget.page.PageLoader;
import com.kunfei.bookshelf.widget.page.PageView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 网络页面加载器
 */

public abstract class PageLoaderNet extends PageLoader {
    private static final String TAG = "PageLoaderNet";
    private List<String> downloadingChapterList = new ArrayList<>();
    private ExecutorService executorService;
    private Scheduler scheduler;

    public PageLoaderNet(PageView pageView, BookShelfBean bookShelfBean, Callback callback) {
        super(pageView, bookShelfBean, callback);
        executorService = Executors.newFixedThreadPool(20);
        scheduler = Schedulers.from(executorService);
    }

    @Override
    public void refreshChapterList() {
        if (!bookChapterBeanList.isEmpty()) {
            isChapterListPrepare = true;
            // 打开章节
            skipToChapter(book.getDurChapter(), book.getDurChapterPage());
        } else {
            loadNetChapterList();
        }
    }


    private void loadNetChapterList() {
        getChapterList(book)
                .compose(RxUtils::toSimpleSingle)
                .subscribe(new Observer<List<BookChapterBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(List<BookChapterBean> chapterBeanList) {
                        isChapterListPrepare = true;
                        addBookChapterBeanList(chapterBeanList);
                        // 目录加载完成
                        if (!chapterBeanList.isEmpty()) {
                            BookshelfHelp.delChapterList(book.getNoteUrl());
                            callback.onCategoryFinish(chapterBeanList);
                        }
                        // 加载并显示当前章节
                        skipToChapter(book.getDurChapter(), book.getDurChapterPage());
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof NoSourceThrowable) {
                            mPageView.autoChangeSource();
                        } else {
                            durDhapterError(e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public void changeSourceFinish(BookShelfBean bookShelfBean) {
        if (bookShelfBean == null) {
            openChapter(book.getDurChapter());
        } else {
            this.book = bookShelfBean;
            refreshChapterList();
        }
    }

    @SuppressLint("DefaultLocale")
    private synchronized void loadContent(final int chapterIndex) {
        if (downloadingChapterList.size() >= 20) return;
        if (chapterIndex >= bookChapterBeanList.size()
                || DownloadingList(listHandle.CHECK, bookChapterBeanList.get(chapterIndex).getDurChapterUrl()))
            return;
        if (null != book && bookChapterBeanList.size() > 0) {
            Observer observer = new Observer<BookContentBean>() {
                @Override
                public void onSubscribe(Disposable d) {
                    compositeDisposable.add(d);
                }

                @SuppressLint("DefaultLocale")
                @Override
                public void onNext(BookContentBean bookContentBean) {
                    DownloadingList(listHandle.REMOVE, bookContentBean.getDurChapterUrl());
                    finishContent(bookContentBean.getDurChapterIndex());
                }

                @Override
                public void onError(Throwable e) {
                    DownloadingList(listHandle.REMOVE, bookChapterBeanList.get(chapterIndex).getDurChapterUrl());
                    if (chapterIndex == book.getDurChapter()) {
                        if (e instanceof NoSourceThrowable) {
                            mPageView.autoChangeSource();
                        } else if (e instanceof VipThrowable) {
                            callback.vipPop();
                        } else {
                            durDhapterError(e.getMessage());
                        }
                    }
                }

                @Override
                public void onComplete() {

                }
            };
            Observable.create((ObservableOnSubscribe<Integer>) e -> {
                if (shouldRequestChapter(chapterIndex)) {
                    DownloadingList(listHandle.ADD, bookChapterBeanList.get(chapterIndex).getDurChapterUrl());
                    e.onNext(chapterIndex);
                }
                e.onComplete();
            })
//                    .flatMap(index -> WebBookModel.getInstance().getBookContent(book, bookChapterBeanList().get(chapterIndex), null))
                    .flatMap(index -> getBookContent(book, bookChapterBeanList.get(chapterIndex), null))
                    .subscribeOn(scheduler)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);

        }
    }

    /**
     * 编辑下载列表
     */
    private synchronized boolean DownloadingList(listHandle editType, String value) {
        if (editType == listHandle.ADD) {
            downloadingChapterList.add(value);
            return true;
        } else if (editType == listHandle.REMOVE) {
            downloadingChapterList.remove(value);
            return true;
        } else {
            return downloadingChapterList.indexOf(value) != -1;
        }
    }

    /**
     * 章节下载完成
     */
    private void finishContent(int chapterIndex) {
        if (chapterIndex == mCurChapterPos) {
            super.parseCurChapter();
        }
        if (chapterIndex == mCurChapterPos - 1) {
            super.parsePrevChapter();
        }
        if (chapterIndex == mCurChapterPos + 1) {
            super.parseNextChapter();
        }
    }

    /**
     * 刷新当前章节
     */
    @SuppressLint("DefaultLocale")
    public void refreshDurChapter() {
        if (bookChapterBeanList.isEmpty()) {
            updateChapter();
            return;
        }
        if (bookChapterBeanList.size() - 1 < mCurChapterPos) {
            mCurChapterPos = bookChapterBeanList.size() - 1;
        }
        BookshelfHelp.delChapter(getCachePathName(book.getBookInfoBean().getName(), book.getTag()),
                mCurChapterPos, bookChapterBeanList.get(mCurChapterPos).getDurChapterName());
        skipToChapter(mCurChapterPos, 0);
    }

    public static String getCachePathName(String bookName, String tag) {
        return formatFolderName(bookName + "-" + tag);
    }

    private static String formatFolderName(String folderName) {
        return folderName.replaceAll("[\\\\/:*?\"<>|.]", "");
    }

    @Override
    protected String getChapterContent(BookChapterBean chapter) {
        return BookshelfHelp.getChapterCache(book, chapter);
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected boolean noChapterData(BookChapterBean chapter) {
        return !BookshelfHelp.isChapterCached(book.getBookInfoBean().getName(), book.getTag(), chapter, book.isAudio());
    }

    private boolean shouldRequestChapter(Integer chapterIndex) {
        return NetworkUtils.isNetWorkAvailable() && noChapterData(bookChapterBeanList.get(chapterIndex));
    }

    // 装载上一章节的内容
    @Override
    protected void parsePrevChapter() {
        if (mCurChapterPos >= 1) {
            loadContent(mCurChapterPos - 1);
        }
        super.parsePrevChapter();
    }

    // 装载当前章内容。
    @Override
    protected void parseCurChapter() {
        for (int i = mCurChapterPos; i < Math.min(mCurChapterPos + 5, book.getChapterListSize()); i++) {
            loadContent(i);
        }
        super.parseCurChapter();
    }

    // 装载下一章节的内容
    @Override
    protected void parseNextChapter() {
        for (int i = mCurChapterPos; i < Math.min(mCurChapterPos + 5, book.getChapterListSize()); i++) {
            loadContent(i);
        }
        super.parseNextChapter();
    }

    @Override
    public void updateChapter() {
        Toast.makeText(mPageView.getContext(), "目录更新中", Toast.LENGTH_SHORT).show();
        getChapterList(book)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BookChapterBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(List<BookChapterBean> chapterBeanList) {
                        isChapterListPrepare = true;
                        addBookChapterBeanList(chapterBeanList);
                        if (chapterBeanList.size() > bookChapterBeanList.size()) {
                            Toast.makeText(mPageView.getContext(), "更新完成,有新章节", Toast.LENGTH_SHORT).show();
                            callback.onCategoryFinish(chapterBeanList);
                        } else {
                            Toast.makeText(mPageView.getContext(), "更新完成,无新章节", Toast.LENGTH_SHORT).show();
                        }

                        // 加载并显示当前章节
                        skipToChapter(book.getDurChapter(), book.getDurChapterPage());
                    }

                    @Override
                    public void onError(Throwable e) {
                        durDhapterError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public abstract ObservableSource<BookContentBean> getBookContent(BookShelfBean book, BookChapterBean bookChapterBean, BaseChapterBean nextChapterBean);

    public abstract Observable<List<BookChapterBean>> getChapterList(BookShelfBean book);

    @Override
    public void closeBook() {
        super.closeBook();
        executorService.shutdown();
    }

    public enum listHandle {
        ADD, REMOVE, CHECK
    }
}
