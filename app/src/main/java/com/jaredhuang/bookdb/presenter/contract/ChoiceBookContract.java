package com.jaredhuang.bookdb.presenter.contract;

import com.jaredhuang.basemvplib.impl.IPresenter;
import com.jaredhuang.basemvplib.impl.IView;
import com.jaredhuang.bookdb.bean.SearchBookBean;

import java.util.List;

public interface ChoiceBookContract {
    interface Presenter extends IPresenter {

        int getPage();

        void initPage();

        void toSearchBooks(String key);

        String getTitle();
    }

    interface View extends IView {

        void refreshSearchBook(List<SearchBookBean> books);

        void loadMoreSearchBook(List<SearchBookBean> books);

        void refreshFinish(Boolean isAll);

        void loadMoreFinish(Boolean isAll);

        void searchBookError(String msg);

        void addBookShelfFailed(String massage);

        void startRefreshAnim();
    }


}
