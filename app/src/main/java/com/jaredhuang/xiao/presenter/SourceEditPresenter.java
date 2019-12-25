package com.jaredhuang.xiao.presenter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.jaredhuang.basemvplib.BasePresenterImpl;
import com.jaredhuang.basemvplib.impl.IView;
import com.jaredhuang.xiao.DbHelper;
import com.jaredhuang.xiao.bean.BookSourceBean;
import com.jaredhuang.xiao.model.BookSourceManager;
import com.jaredhuang.xiao.presenter.contract.SourceEditContract;
import com.jaredhuang.xiao.utils.RxUtils;

import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by GKF on 2018/1/28.
 * 编辑书源
 */
public class SourceEditPresenter extends BasePresenterImpl<SourceEditContract.View> implements SourceEditContract.Presenter {

    @Override
    public Observable<Boolean> saveSource(BookSourceBean bookSource, BookSourceBean bookSourceOld) {
        return Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            if (!TextUtils.isEmpty(bookSourceOld.getBookSourceUrl()) && !Objects.equals(bookSource.getBookSourceUrl(), bookSourceOld.getBookSourceUrl())) {
                DbHelper.getDaoSession().getBookSourceBeanDao().delete(bookSourceOld);
            }
            BookSourceManager.addBookSource(bookSource);
            e.onNext(true);
        }).compose(RxUtils::toSimpleSingle);
    }

    @Override
    public void copySource(String bookSource) {
        ClipboardManager clipboard = (ClipboardManager) mView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(null, bookSource);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clipData);
        }
    }

    @Override
    public void pasteSource() {
        ClipboardManager clipboard = (ClipboardManager) mView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = clipboard != null ? clipboard.getPrimaryClip() : null;
        if (clipData != null && clipData.getItemCount() > 0) {
            setText(String.valueOf(clipData.getItemAt(0).getText()));
        }
    }

    @Override
    public void setText(String bookSourceStr) {
        try {
            Gson gson = new Gson();
            BookSourceBean bookSourceBean = gson.fromJson(bookSourceStr, BookSourceBean.class);
            mView.setText(bookSourceBean);
        } catch (Exception e) {
            mView.toast("数据格式不对");
        }
    }

    @Override
    public void attachView(@NonNull IView iView) {
        super.attachView(iView);
    }

    @Override
    public void detachView() {

    }
}
