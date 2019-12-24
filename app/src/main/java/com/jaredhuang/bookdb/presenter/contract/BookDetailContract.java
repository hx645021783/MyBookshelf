package com.jaredhuang.bookdb.presenter.contract;

import android.content.Intent;

import com.jaredhuang.basemvplib.impl.IPresenter;
import com.jaredhuang.basemvplib.impl.IView;
import com.jaredhuang.bookdb.bean.BookChapterBean;
import com.jaredhuang.bookdb.bean.BookShelfBean;
import com.jaredhuang.bookdb.bean.SearchBookBean;

import java.util.List;

public interface BookDetailContract {
    interface Presenter extends IPresenter {
        void initData(Intent intent);

        int getOpenFrom();

        SearchBookBean getSearchBook();

        BookShelfBean getBookShelf();

        List<BookChapterBean> getChapterList();

        Boolean getInBookShelf();

        void initBookFormSearch(SearchBookBean searchBookBean);

        void getBookShelfInfo();

        void addToBookShelf();

        void removeFromBookShelf();

        void changeBookSource(SearchBookBean searchBookBean);
    }

    interface View extends IView {
        /**
         * 更新书籍详情UI
         */
        void updateView();

        /**
         * 数据获取失败
         */
        void getBookShelfError();

        void finish();

        void toast(String msg);
    }
}
