package com.jaredhuang.bookdb.model.impl;

import com.jaredhuang.bookdb.bean.DownloadBookBean;
import com.jaredhuang.bookdb.bean.DownloadChapterBean;

import io.reactivex.Scheduler;

public interface IDownloadTask {

    int getId();

    void startDownload(Scheduler scheduler);

    void stopDownload();

    boolean isDownloading();

    boolean isFinishing();

    DownloadBookBean getDownloadBook();

    void onDownloadPrepared(DownloadBookBean downloadBook);

    void onDownloadProgress(DownloadChapterBean chapterBean);

    void onDownloadChange(DownloadBookBean downloadBook);

    void onDownloadError(DownloadBookBean downloadBook);

    void onDownloadComplete(DownloadBookBean downloadBook);
}
