//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.jaredhuang.xiao.model;

import com.jaredhuang.xiao.DbHelper;
import com.jaredhuang.xiao.MApplication;
import com.kunfei.bookshelf.R;
import com.jaredhuang.xiao.base.BaseModelImpl;
import com.jaredhuang.xiao.bean.BookInfoBean;
import com.jaredhuang.xiao.bean.BookCollectBean;
import com.jaredhuang.xiao.bean.LocBookShelfBean;
import com.jaredhuang.xiao.help.BookCollectHelp;

import java.io.File;

import io.reactivex.Observable;


public class ImportBookModel extends BaseModelImpl {

    public static ImportBookModel getInstance() {
        return new ImportBookModel();
    }

    public Observable<LocBookShelfBean> importBook(final File file) {
        return Observable.create(e -> {
            //判断文件是否存在

            boolean isNew = false;

            BookCollectBean bookCollectBean = BookCollectHelp.getBook(file.getAbsolutePath());
            if (bookCollectBean == null) {
                isNew = true;
                bookCollectBean = new BookCollectBean();
                bookCollectBean.setHasUpdate(true);
                bookCollectBean.setFinalDate(System.currentTimeMillis());
                bookCollectBean.setDurChapter(0);
                bookCollectBean.setDurChapterPage(0);
                bookCollectBean.setGroup(3);
                bookCollectBean.setDomain(BookCollectBean.LOCAL_TAG);
                bookCollectBean.setNoteUrl(file.getAbsolutePath());
                bookCollectBean.setAllowUpdate(false);

                BookInfoBean bookInfoBean = bookCollectBean.getBookInfoBean();
                String fileName = file.getName();
                int lastDotIndex = file.getName().lastIndexOf(".");
                if (lastDotIndex > 0)
                    fileName = fileName.substring(0, lastDotIndex);
                int authorIndex = fileName.indexOf("作者");
                if (authorIndex != -1) {
                    bookInfoBean.setAuthor(fileName.substring(authorIndex));
                    fileName = fileName.substring(0, authorIndex).trim();
                } else {
                    bookInfoBean.setAuthor("");
                }
                int smhStart = fileName.indexOf("《");
                int smhEnd = fileName.indexOf("》");
                if (smhStart != -1 && smhEnd != -1) {
                    bookInfoBean.setName(fileName.substring(smhStart + 1, smhEnd));
                } else {
                    bookInfoBean.setName(fileName);
                }
                bookInfoBean.setFinalRefreshData(file.lastModified());
                bookInfoBean.setCoverUrl("");
                bookInfoBean.setNoteUrl(file.getAbsolutePath());
                bookInfoBean.setDomain(BookCollectBean.LOCAL_TAG);
                bookInfoBean.setOrigin(MApplication.getInstance().getString(R.string.local));

                DbHelper.getDaoSession().getBookInfoBeanDao().insertOrReplace(bookInfoBean);
                DbHelper.getDaoSession().getBookCollectBeanDao().insertOrReplace(bookCollectBean);
            }
            e.onNext(new LocBookShelfBean(isNew, bookCollectBean));
            e.onComplete();
        });
    }

}
