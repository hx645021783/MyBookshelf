//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.jaredhuang.xiao.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jaredhuang.xiao.MApplication;
import com.jaredhuang.xiao.bean.BookCollectBean;
import com.kunfei.bookshelf.R;
import com.jaredhuang.xiao.base.observer.MyObserver;
import com.jaredhuang.xiao.bean.BookChapterBean;
import com.jaredhuang.xiao.help.BookCollectHelp;
import com.jaredhuang.xiao.utils.theme.ThemeStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ChapterListAdapter extends RecyclerView.Adapter<ChapterListAdapter.ThisViewHolder> {

    private BookCollectBean bookCollectBean;
    private OnItemClickListener itemClickListener;
    private List<BookChapterBean> allChapter;
    private List<BookChapterBean> bookChapterBeans = new ArrayList<>();
    private int index = 0;
    private boolean isSearch = false;
    private int normalColor;
    private int highlightColor;

    public ChapterListAdapter(BookCollectBean bookCollectBean, List<BookChapterBean> allChapter, @NonNull OnItemClickListener itemClickListener) {
        this.bookCollectBean = bookCollectBean;
        this.allChapter = allChapter;
        this.itemClickListener = itemClickListener;
        highlightColor = ThemeStore.accentColor(MApplication.getInstance());
    }

    public void upChapter(int index) {
        if (allChapter.size() > index) {
            notifyItemChanged(index, 0);
        }
    }

    public void search(final String key) {
        bookChapterBeans.clear();
        if (Objects.equals(key, "")) {
            isSearch = false;
            notifyDataSetChanged();
        } else {
            Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
                for (BookChapterBean bookChapterBean : allChapter) {
                    if (bookChapterBean.getDurChapterName().contains(key)) {
                        bookChapterBeans.add(bookChapterBean);
                    }
                }
                emitter.onNext(true);
                emitter.onComplete();
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyObserver<Boolean>() {
                        @Override
                        public void onNext(Boolean aBoolean) {
                            isSearch = true;
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        }
    }

    @NonNull
    @Override
    public ThisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        normalColor = ThemeStore.textColorSecondary(parent.getContext());
        return new ThisViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ThisViewHolder holder, final int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull ThisViewHolder holder, int position, @NonNull List<Object> payloads) {
        int realPosition = holder.getLayoutPosition();
        if (realPosition == getItemCount() - 1) {
            holder.line.setVisibility(View.GONE);
        } else {
            holder.line.setVisibility(View.VISIBLE);
        }
        if (payloads.size() > 0) {
            holder.tvName.setSelected(true);
            holder.tvName.getPaint().setFakeBoldText(true);
            return;
        }
        BookChapterBean bookChapterBean = isSearch ? bookChapterBeans.get(realPosition) : allChapter.get(realPosition);
        if (bookChapterBean.getDurChapterIndex() == index) {
            holder.tvName.setTextColor(highlightColor);
        } else {
            holder.tvName.setTextColor(normalColor);
        }

        holder.tvName.setText(bookChapterBean.getDurChapterName());

        boolean chapterCached = BookCollectHelp.isChapterCached(bookCollectBean.getBookInfoBean().getName(), bookChapterBean.getDomain(), bookChapterBean, bookCollectBean.getBookInfoBean().isAudio());

        if (Objects.equals(bookCollectBean.getDomain(), BookCollectBean.LOCAL_TAG) || chapterCached) {
            holder.tvName.setSelected(true);
            holder.tvName.getPaint().setFakeBoldText(true);
        } else {
            holder.tvName.setSelected(false);
            holder.tvName.getPaint().setFakeBoldText(false);
        }

        holder.llName.setOnClickListener(v -> {
            setIndex(realPosition);
            itemClickListener.itemClick(bookChapterBean.getDurChapterIndex(), 0);
        });
    }

    @Override
    public int getItemCount() {
        if (bookCollectBean == null)
            return 0;
        else {
            if (isSearch) {
                return bookChapterBeans.size();
            }
            return allChapter.size();
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
        notifyItemChanged(this.index, 0);
    }

    static class ThisViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private View line;
        private View llName;

        ThisViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            line = itemView.findViewById(R.id.v_line);
            llName = itemView.findViewById(R.id.ll_name);
        }
    }

    public interface OnItemClickListener {
        void itemClick(int index, int page);
    }
}
