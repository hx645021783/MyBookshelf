package com.jaredhuang.xiao.view.adapter;

import com.jaredhuang.xiao.bean.BookCollectBean;
import com.jaredhuang.xiao.help.ItemTouchCallback;
import com.jaredhuang.xiao.view.adapter.base.OnItemClickListenerTwo;

import java.util.HashSet;
import java.util.List;

public interface BookShelfAdapter {

    void setArrange(boolean isArrange);

    void selectAll();

    ItemTouchCallback.OnItemTouchCallbackListener getItemTouchCallbackListener();

    List<BookCollectBean> getBooks();

    void replaceAll(List<BookCollectBean> newDataS, String bookshelfPx);

    void refreshBook(String noteUrl);

    void setItemClickListener(OnItemClickListenerTwo itemClickListener);

    HashSet<String> getSelected();

}
