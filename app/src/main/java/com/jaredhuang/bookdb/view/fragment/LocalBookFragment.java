package com.jaredhuang.bookdb.view.fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaredhuang.basemvplib.impl.IPresenter;
import com.kunfei.bookshelf.R;
import com.jaredhuang.bookdb.help.BookshelfHelp;
import com.jaredhuang.bookdb.help.media.MediaStoreHelper;
import com.jaredhuang.bookdb.view.adapter.FileSystemAdapter;
import com.jaredhuang.bookdb.widget.itemdecoration.DividerItemDecoration;
import com.jaredhuang.bookdb.widget.recycler.refresh.RefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by newbiechen on 17-5-27.
 * 本地书籍
 */

public class LocalBookFragment extends BaseFileFragment {
    @BindView(R.id.refresh_layout)
    RefreshLayout mRlRefresh;
    @BindView(R.id.local_book_rv_content)
    RecyclerView mRvContent;

    private Unbinder unbinder;

    @Override
    public int createLayoutId() {
        return R.layout.fragment_local_book;
    }

    /**
     * P层绑定   若无则返回null;
     */
    @Override
    protected IPresenter initInjector() {
        return null;
    }

    @Override
    protected void bindView() {
        super.bindView();
        unbinder = ButterKnife.bind(this, view);
        setUpAdapter();
    }

    private void setUpAdapter() {
        mAdapter = new FileSystemAdapter();
        if (getContext() != null) {
            mRvContent.setLayoutManager(new LinearLayoutManager(getContext()));
            mRvContent.addItemDecoration(new DividerItemDecoration(getContext()));
            mRvContent.setAdapter(mAdapter);
        }
    }

    @Override
    protected void bindEvent() {
        super.bindEvent();
        mAdapter.setOnItemClickListener(
                (view, pos) -> {
                    //如果是已加载的文件，则点击事件无效。
                    String id = mAdapter.getItem(pos).getAbsolutePath();
                    if (BookshelfHelp.getBook(id) != null) {
                        return;
                    }

                    //点击选中
                    mAdapter.setCheckedItem(pos);

                    //反馈
                    if (mListener != null) {
                        mListener.onItemCheckedChange(mAdapter.getItemIsChecked(pos));
                    }
                }
        );
    }

    @Override
    protected void firstRequest() {
        super.firstRequest();
        if (getActivity() != null) {
            MediaStoreHelper.getAllBookFile(getActivity(),
                    (files) -> {
                        if (files.isEmpty()) {
                            mRlRefresh.showEmpty();
                        } else {
                            mAdapter.refreshItems(files);
                            mRlRefresh.showFinish();
                            //反馈
                            if (mListener != null) {
                                mListener.onCategoryChanged();
                            }
                        }
                    });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
