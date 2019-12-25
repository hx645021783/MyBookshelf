//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.jaredhuang.xiao.presenter;

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
import com.jaredhuang.xiao.bean.BookInfoBean;
import com.jaredhuang.xiao.bean.BookSourceBean;
import com.jaredhuang.xiao.constant.RxBusTag;
import com.jaredhuang.xiao.dao.BookSourceBeanDao;
import com.jaredhuang.xiao.help.BookCollectHelp;
import com.jaredhuang.xiao.help.DataBackup;
import com.jaredhuang.xiao.help.DataRestore;
import com.jaredhuang.xiao.model.WebBookModel;
import com.jaredhuang.xiao.presenter.contract.MainContract;
import com.jaredhuang.xiao.utils.RxUtils;
import com.jaredhuang.xiao.utils.StringUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter {

    @Override
    public void backupData() {
        DataBackup.getInstance().run();
    }

    @Override
    public void restoreData() {
        mView.onRestore(mView.getContext().getString(R.string.on_restore));
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            if (DataRestore.getInstance().run()) {
                e.onNext(true);
            } else {
                e.onNext(false);
            }
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean value) {
                        mView.dismissHUD();
                        mView.toast(R.string.restore_success);
                        //更新书架并刷新
                        mView.recreate();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.dismissHUD();
                        mView.toast(R.string.restore_fail);
                    }
                });
    }

    @Override
    public void addBookUrl(String bookUrls) {
        bookUrls = bookUrls.trim();
        if (TextUtils.isEmpty(bookUrls)) return;

        String[] urls = bookUrls.split("\\n");

        Observable.fromArray(urls)
                .flatMap(this::addBookUrlO)
                .compose(RxUtils::toSimpleSingle)
                .subscribe(new MyObserver<BookCollectBean>() {
                    @Override
                    public void onNext(BookCollectBean bookCollectBean) {
                        getBook(bookCollectBean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toast(e.getMessage());
                    }
                });
    }

    private Observable<BookCollectBean> addBookUrlO(String bookUrl) {
        return Observable.create(e -> {
            if (StringUtils.isTrimEmpty(bookUrl)) {
                e.onComplete();
                return;
            }
            BookInfoBean temp = DbHelper.getDaoSession().getBookInfoBeanDao().load(bookUrl);
            if (temp != null) {
                e.onError(new Throwable("已在书架中"));
                return;
            } else {
                String baseUrl = StringUtils.getBaseUrl(bookUrl);
                BookSourceBean bookSourceBean = DbHelper.getDaoSession().getBookSourceBeanDao().load(baseUrl);
                if (bookSourceBean == null) {
                    List<BookSourceBean> sourceBeans = DbHelper.getDaoSession().getBookSourceBeanDao().queryBuilder()
                            .where(BookSourceBeanDao.Properties.RuleBookUrlPattern.isNotNull(), BookSourceBeanDao.Properties.RuleBookUrlPattern.notEq("")).list();
                    for (BookSourceBean sourceBean : sourceBeans) {
                        if (bookUrl.matches(sourceBean.getRuleBookUrlPattern())) {
                            bookSourceBean = sourceBean;
                            break;
                        }
                    }
                }
                if (bookSourceBean != null) {
                    BookCollectBean bookCollectBean = new BookCollectBean();
                    bookCollectBean.setTag(bookSourceBean.getBookSourceUrl());
                    bookCollectBean.setNoteUrl(bookUrl);
                    bookCollectBean.setDurChapter(0);
                    bookCollectBean.setGroup(mView.getGroup() % 4);
                    bookCollectBean.setDurChapterPage(0);
                    bookCollectBean.setFinalDate(System.currentTimeMillis());
                    e.onNext(bookCollectBean);
                } else {
                    e.onError(new Throwable("未找到对应书源"));
                    return;
                }
            }
            e.onComplete();
        });
    }

    private void getBook(BookCollectBean bookCollectBean) {
        WebBookModel.getInstance()
                .getBookInfo(bookCollectBean)
                .flatMap(bookShelfBean1 -> WebBookModel.getInstance().getChapterList(bookShelfBean1))
                .flatMap(chapterBeanList -> saveBookToShelfO(bookCollectBean, chapterBeanList))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<BookCollectBean>() {
                    @Override
                    public void onNext(BookCollectBean value) {
                        if (value.getBookInfoBean().getChapterUrl() == null) {
                            mView.toast("添加书籍失败");
                        } else {
                            //成功   //发送RxBus
                            RxBus.get().post(RxBusTag.HAD_ADD_BOOK, bookCollectBean);
                            mView.toast("添加书籍成功");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toast("添加书籍失败" + e.getMessage());
                    }
                });
    }

    /**
     * 保存数据
     */
    private Observable<BookCollectBean> saveBookToShelfO(BookCollectBean bookCollectBean, List<BookChapterBean> chapterBeanList) {
        return Observable.create(e -> {
            BookCollectHelp.saveBookToShelf(bookCollectBean);
            DbHelper.getDaoSession().getBookChapterBeanDao().insertOrReplaceInTx(chapterBeanList);
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
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusTag.IMMERSION_CHANGE)})
    public void initImmersionBar(Boolean immersion) {
        mView.initImmersionBar();
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusTag.RECREATE)})
    public void recreate(Boolean recreate) {
        mView.recreate();
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusTag.AUTO_BACKUP)})
    public void autoBackup(Boolean backup) {
        DataBackup.getInstance().autoSave();
    }
}