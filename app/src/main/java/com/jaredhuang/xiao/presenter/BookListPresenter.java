//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.jaredhuang.xiao.presenter;

import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jaredhuang.basemvplib.BasePresenterImpl;
import com.jaredhuang.basemvplib.impl.IView;
import com.jaredhuang.xiao.DbHelper;
import com.jaredhuang.xiao.bean.BookCollectBean;
import com.kunfei.bookshelf.R;
import com.jaredhuang.xiao.base.observer.MyObserver;
import com.jaredhuang.xiao.bean.BookChapterBean;
import com.jaredhuang.xiao.bean.DownloadBookBean;
import com.jaredhuang.xiao.constant.RxBusTag;
import com.jaredhuang.xiao.help.BookCollectHelp;
import com.jaredhuang.xiao.model.WebBookModel;
import com.jaredhuang.xiao.presenter.contract.BookListContract;
import com.jaredhuang.xiao.service.DownloadService;
import com.jaredhuang.xiao.throwable.NoSourceThrowable;
import com.jaredhuang.xiao.utils.NetworkUtils;
import com.jaredhuang.xiao.utils.RxUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BookListPresenter extends BasePresenterImpl<BookListContract.View> implements BookListContract.Presenter {
    private int threadsNum = 6;
    private int refreshIndex;
    private List<BookCollectBean> bookCollectBeans;
    private int group;
    private boolean hasUpdate = false;
    private List<String> errBooks = new ArrayList<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void queryBookShelf(final Boolean needRefresh, final int group) {
        this.group = group;
        if (needRefresh) {
            hasUpdate = false;
            errBooks.clear();
        }
        Observable.create((ObservableOnSubscribe<List<BookCollectBean>>) e -> {
            List<BookCollectBean> bookShelfList;
            if (group == 0) {
                bookShelfList = BookCollectHelp.getAllBook();
            } else {
                bookShelfList = BookCollectHelp.getBooksByGroup(group - 1);
            }
            e.onNext(bookShelfList == null ? new ArrayList<>() : bookShelfList);
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<List<BookCollectBean>>() {
                    @Override
                    public void onNext(List<BookCollectBean> value) {
                        if (null != value) {
                            bookCollectBeans = value;
                            mView.refreshBookShelf(bookCollectBeans);
                            if (needRefresh && NetworkUtils.isNetWorkAvailable()) {
                                startRefreshBook();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.refreshError(NetworkUtils.getErrorTip(NetworkUtils.ERROR_CODE_ANALY));
                    }
                });
    }

    private void downloadAll(int downloadNum, boolean onlyNew) {
        if (bookCollectBeans == null || mView.getContext() == null) {
            return;
        }
        AsyncTask.execute(() -> {
            for (BookCollectBean bookCollectBean : new ArrayList<>(bookCollectBeans)) {
                if (!bookCollectBean.getDomain().equals(BookCollectBean.LOCAL_TAG) && (!onlyNew || bookCollectBean.getHasUpdate())) {
                    List<BookChapterBean> chapterBeanList = BookCollectHelp.getChapterList(bookCollectBean.getNoteUrl());
                    if (chapterBeanList.size() >= bookCollectBean.getDurChapter()) {
                        for (int start = bookCollectBean.getDurChapter(); start < chapterBeanList.size(); start++) {

                            boolean chapterCached = BookCollectHelp.isChapterCached(bookCollectBean.getBookInfoBean().getName(), chapterBeanList.get(start).getDomain(), chapterBeanList.get(start), bookCollectBean.getBookInfoBean().isAudio());
                            if (!chapterCached) {
                                DownloadBookBean downloadBook = new DownloadBookBean();
                                downloadBook.setName(bookCollectBean.getBookInfoBean().getName());
                                downloadBook.setNoteUrl(bookCollectBean.getNoteUrl());
                                downloadBook.setCoverUrl(bookCollectBean.getBookInfoBean().getCoverUrl());
                                downloadBook.setStart(start);
                                downloadBook.setEnd(downloadNum > 0 ? Math.min(chapterBeanList.size() - 1, start + downloadNum - 1) : chapterBeanList.size() - 1);
                                downloadBook.setFinalDate(System.currentTimeMillis());
                                DownloadService.addDownload(mView.getContext(), downloadBook);
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    private void startRefreshBook() {
        if (mView.getContext() != null) {
            threadsNum = mView.getPreferences().getInt(mView.getContext().getString(R.string.pk_threads_num), 6);
            if (bookCollectBeans != null && bookCollectBeans.size() > 0) {
                refreshIndex = -1;
                for (int i = 1; i <= threadsNum; i++) {
                    refreshBookshelf();
                }
            }
        }
    }

    private synchronized void refreshBookshelf() {
        refreshIndex++;
        if (refreshIndex < bookCollectBeans.size()) {
            BookCollectBean bookCollectBean = bookCollectBeans.get(refreshIndex);
            if (!bookCollectBean.getDomain().equals(BookCollectBean.LOCAL_TAG) && bookCollectBean.getAllowUpdate() && bookCollectBean.getGroup() != 3) {
                int chapterNum = bookCollectBean.getChapterListSize();
                bookCollectBean.setLoading(true);
                mView.refreshBook(bookCollectBean.getNoteUrl());
                WebBookModel.getInstance().getChapterList(bookCollectBean)
                        .flatMap(chapterBeanList -> saveBookToShelfO(bookCollectBean, chapterBeanList))
                        .compose(RxUtils::toSimpleSingle)
                        .subscribe(new Observer<BookCollectBean>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                compositeDisposable.add(d);
                            }

                            @Override
                            public void onNext(BookCollectBean value) {
                                if (value.getErrorMsg() != null) {
                                    mView.toast(value.getErrorMsg());
                                    value.setErrorMsg(null);
                                }
                                bookCollectBean.setLoading(false);
                                if (chapterNum < bookCollectBean.getChapterListSize())
                                    hasUpdate = true;
                                mView.refreshBook(bookCollectBean.getNoteUrl());
                                refreshBookshelf();
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (!(e instanceof NoSourceThrowable)) {
                                    errBooks.add(bookCollectBean.getBookInfoBean().getName());
                                    bookCollectBean.setLoading(false);
                                    mView.refreshBook(bookCollectBean.getNoteUrl());
                                    refreshBookshelf();
                                }
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            } else {
                refreshBookshelf();
            }
        } else if (refreshIndex >= bookCollectBeans.size() + threadsNum - 1) {
            if (errBooks.size() > 0) {
                mView.toast(TextUtils.join("、", errBooks) + " 更新失败！");
                errBooks.clear();
            }
            if (hasUpdate && mView.getPreferences().getBoolean(mView.getContext().getString(R.string.pk_auto_download), false)) {
                downloadAll(10, true);
                hasUpdate = false;
            }
            queryBookShelf(false, group);
        }
    }

    /**
     * 保存数据
     */
    private Observable<BookCollectBean> saveBookToShelfO(BookCollectBean bookCollectBean, List<BookChapterBean> chapterBeanList) {
        return Observable.create(e -> {
            if (!chapterBeanList.isEmpty()) {
                BookCollectHelp.delChapterList(bookCollectBean.getNoteUrl());
                BookCollectHelp.saveBookToShelf(bookCollectBean);
                DbHelper.getDaoSession().getBookChapterBeanDao().insertOrReplaceInTx(chapterBeanList);
            }
            e.onNext(bookCollectBean);
            e.onComplete();
        });
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void attachView(@NonNull IView iView) {
        super.attachView(iView);
        RxBus.get().register(this);
    }

    @Override
    public void detachView() {
        RxBus.get().unregister(this);
        compositeDisposable.dispose();
    }

    @Subscribe(thread = EventThread.MAIN_THREAD,
            tags = {@Tag(RxBusTag.HAD_ADD_BOOK), @Tag(RxBusTag.HAD_REMOVE_BOOK), @Tag(RxBusTag.UPDATE_BOOK_PROGRESS)})
    public void hadAddOrRemoveBook(BookCollectBean bookCollectBean) {
        queryBookShelf(false, group);
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusTag.UPDATE_GROUP)})
    public void updateGroup(Integer group) {
        this.group = group;
        mView.updateGroup(group);
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusTag.REFRESH_BOOK_LIST)})
    public void reFlashBookList(Boolean needRefresh) {
        queryBookShelf(needRefresh, group);
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusTag.DOWNLOAD_ALL)})
    public void downloadAll(Integer downloadNum) {
        downloadAll(downloadNum, false);
    }
}
