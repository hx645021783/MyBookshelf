package com.jaredhuang.xiao.presenter.contract;

import android.content.SharedPreferences;

import com.jaredhuang.basemvplib.impl.IPresenter;
import com.jaredhuang.basemvplib.impl.IView;
import com.jaredhuang.xiao.bean.BookCollectBean;

import java.util.List;

public interface BookListContract {

    interface View extends IView {

        /**
         * 刷新书架书籍小说信息 更新UI
         *
         * @param bookCollectBeanList 书架
         */
        void refreshBookShelf(List<BookCollectBean> bookCollectBeanList);

        void refreshBook(String noteUrl);

        /**
         * 刷新错误
         *
         * @param error 错误
         */
        void refreshError(String error);

        SharedPreferences getPreferences();

        /**
         * 更新Group
         */
        void updateGroup(Integer group);

    }

    interface Presenter extends IPresenter {
        void queryBookShelf(Boolean needRefresh, int group);
    }

}
