//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.jaredhuang.xiao.presenter;

import com.hwangjr.rxbus.RxBus;
import com.jaredhuang.basemvplib.BasePresenterImpl;
import com.jaredhuang.xiao.bean.LocBookShelfBean;
import com.jaredhuang.xiao.constant.RxBusTag;
import com.jaredhuang.xiao.model.ImportBookModel;
import com.jaredhuang.xiao.presenter.contract.ImportBookContract;
import com.jaredhuang.xiao.utils.RxUtils;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class ImportBookPresenter extends BasePresenterImpl<ImportBookContract.View> implements ImportBookContract.Presenter {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void importBooks(List<File> books) {
        Observable.fromIterable(books)
                .flatMap(file -> ImportBookModel.getInstance().importBook(file))
                .compose(RxUtils::toSimpleSingle)
                .subscribe(new Observer<LocBookShelfBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(LocBookShelfBean value) {
                        if (value.getNew()) {
                            RxBus.get().post(RxBusTag.HAD_ADD_BOOK, value.getBookCollectBean());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.addError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mView.addSuccess();
                    }
                });
    }


    @Override
    public void detachView() {
        compositeDisposable.dispose();
    }
}
