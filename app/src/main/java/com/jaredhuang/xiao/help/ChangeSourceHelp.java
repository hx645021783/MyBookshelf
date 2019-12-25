package com.jaredhuang.xiao.help;

import com.jaredhuang.xiao.DbHelper;
import com.jaredhuang.xiao.base.observer.MyObserver;
import com.jaredhuang.xiao.bean.BookChapterBean;
import com.jaredhuang.xiao.bean.BookCollectBean;
import com.jaredhuang.xiao.bean.SearchBookBean;
import com.jaredhuang.xiao.bean.TwoDataBean;
import com.jaredhuang.xiao.model.SearchBookModel;
import com.jaredhuang.xiao.model.WebBookModel;
import com.jaredhuang.xiao.utils.RxUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;

public class ChangeSourceHelp {
    private SearchBookModel searchBookModel;
    private BookCollectBean bookCollectBean;
    private ChangeSourceListener changeSourceListener;
    private boolean finish;

    public ChangeSourceHelp() {
        SearchBookModel.OnSearchListener searchListener = new SearchBookModel.OnSearchListener() {
            @Override
            public void refreshSearchBook() {

            }

            @Override
            public void refreshFinish(Boolean value) {

            }

            @Override
            public void loadMoreFinish(Boolean value) {

            }

            @Override
            public void loadMoreSearchBook(List<SearchBookBean> value) {
                selectBook(value);
            }

            @Override
            public void searchBookError(Throwable throwable) {
                if (!finish && changeSourceListener != null) {
                    changeSourceListener.error(throwable);
                    searchBookModel.onDestroy();
                }
            }

            @Override
            public int getItemCount() {
                return 0;
            }
        };
        searchBookModel = new SearchBookModel(searchListener);
    }

    public void autoChange(BookCollectBean bookCollectBean, ChangeSourceListener changeSourceListener) {
        this.bookCollectBean = bookCollectBean;
        this.changeSourceListener = changeSourceListener;
        long searchTime = System.currentTimeMillis();
        finish = false;
        searchBookModel.setSearchTime(searchTime);
        searchBookModel.search(bookCollectBean.getBookInfoBean().getName(), searchTime, new ArrayList<>(), false);
    }

    private synchronized void selectBook(List<SearchBookBean> value) {
        if (finish) return;
        for (SearchBookBean searchBookBean : value) {
            if (Objects.equals(searchBookBean.getName(), bookCollectBean.getBookInfoBean().getName())) {
                if (Objects.equals(searchBookBean.getAuthor(), bookCollectBean.getBookInfoBean().getAuthor())) {
                    if (changeSourceListener != null) {
                        finish = true;
                        changeBookSource(searchBookBean, bookCollectBean)
                                .subscribe(new MyObserver<TwoDataBean<BookCollectBean, List<BookChapterBean>>>() {
                                    @Override
                                    public void onNext(TwoDataBean<BookCollectBean, List<BookChapterBean>> twoData) {
                                        changeSourceListener.finish(twoData.getData1(), twoData.getData2());
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        changeSourceListener.error(e);
                                    }
                                });
                    }
                    searchBookModel.onDestroy();
                    break;
                }
            } else {
                break;
            }
        }
    }

    public void stopSearch() {
        if (searchBookModel != null) {
            searchBookModel.onDestroy();
        }
    }

    public static Observable<TwoDataBean<BookCollectBean, List<BookChapterBean>>> changeBookSource(SearchBookBean searchBook, BookCollectBean oldBook) {
        BookCollectBean bookCollectBean = BookCollectHelp.getBookFromSearchBook(searchBook);
        bookCollectBean.setSerialNumber(oldBook.getSerialNumber());
        bookCollectBean.setLastChapterName(oldBook.getLastChapterName());
        bookCollectBean.setDurChapterName(oldBook.getDurChapterName());
        bookCollectBean.setDurChapter(oldBook.getDurChapter());
        bookCollectBean.setDurChapterPage(oldBook.getDurChapterPage());
        bookCollectBean.setReplaceEnable(oldBook.getReplaceEnable());
        return WebBookModel.getInstance().getBookInfo(bookCollectBean)
                .flatMap(book -> WebBookModel.getInstance().getChapterList(book))
                .flatMap(chapterBeanList -> saveChangedBook(bookCollectBean, oldBook, chapterBeanList))
                .compose(RxUtils::toSimpleSingle);
    }

    private static Observable<TwoDataBean<BookCollectBean, List<BookChapterBean>>> saveChangedBook(BookCollectBean newBook, BookCollectBean oldBook, List<BookChapterBean> chapterBeanList) {
        return Observable.create(e -> {
            if (newBook.getChapterListSize() <= oldBook.getChapterListSize()) {
                newBook.setHasUpdate(false);
            }
            newBook.setCustomCoverPath(oldBook.getCustomCoverPath());
            newBook.setDurChapter(BookCollectHelp.getDurChapter(oldBook.getDurChapter(), oldBook.getChapterListSize(), oldBook.getDurChapterName(), chapterBeanList));
            newBook.setDurChapterName(chapterBeanList.get(newBook.getDurChapter()).getDurChapterName());
            newBook.setGroup(oldBook.getGroup());
            newBook.getBookInfoBean().setName(oldBook.getBookInfoBean().getName());
            newBook.getBookInfoBean().setAuthor(oldBook.getBookInfoBean().getAuthor());
            BookCollectHelp.removeFromBookShelf(oldBook);
            BookCollectHelp.saveBookToShelf(newBook);
            DbHelper.getDaoSession().getBookChapterBeanDao().insertOrReplaceInTx(chapterBeanList);
            e.onNext(new TwoDataBean<>(newBook, chapterBeanList));
            e.onComplete();
        });
    }

    public interface ChangeSourceListener {
        void finish(BookCollectBean bookCollectBean, List<BookChapterBean> chapterBeanList);

        void error(Throwable throwable);
    }

}
