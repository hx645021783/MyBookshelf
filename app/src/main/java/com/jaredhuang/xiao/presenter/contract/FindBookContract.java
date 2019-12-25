package com.jaredhuang.xiao.presenter.contract;

import com.jaredhuang.basemvplib.impl.IPresenter;
import com.jaredhuang.basemvplib.impl.IView;
import com.jaredhuang.xiao.widget.recycler.expandable.bean.RecyclerViewData;

import java.util.List;

public interface FindBookContract {
    interface Presenter extends IPresenter {

        void initData();

    }

    interface View extends IView {

        void upData(List<RecyclerViewData> group);

    }
}
