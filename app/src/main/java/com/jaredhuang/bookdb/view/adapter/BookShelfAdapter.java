package com.jaredhuang.bookdb.view.adapter;

import com.jaredhuang.bookdb.bean.BookShelfBean;
import com.jaredhuang.bookdb.help.ItemTouchCallback;
import com.jaredhuang.bookdb.view.adapter.base.OnItemClickListenerTwo;

import java.util.HashSet;
import java.util.List;

public interface BookShelfAdapter {

    void setArrange(boolean isArrange);

    void selectAll();

    ItemTouchCallback.OnItemTouchCallbackListener getItemTouchCallbackListener();

    List<BookShelfBean> getBooks();

    void replaceAll(List<BookShelfBean> newDataS, String bookshelfPx);

    void refreshBook(String noteUrl);

    void setItemClickListener(OnItemClickListenerTwo itemClickListener);

    HashSet<String> getSelected();

}
