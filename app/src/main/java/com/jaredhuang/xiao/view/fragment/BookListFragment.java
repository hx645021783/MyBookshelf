package com.jaredhuang.xiao.view.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.jaredhuang.basemvplib.BitIntentDataManager;
import com.jaredhuang.xiao.MApplication;
import com.jaredhuang.xiao.bean.BookCollectBean;
import com.kunfei.bookshelf.R;
import com.jaredhuang.xiao.base.MBaseFragment;
import com.jaredhuang.xiao.base.observer.MySingleObserver;
import com.jaredhuang.xiao.help.BookCollectHelp;
import com.jaredhuang.xiao.help.ItemTouchCallback;
import com.jaredhuang.xiao.presenter.BookDetailPresenter;
import com.jaredhuang.xiao.presenter.BookListPresenter;
import com.jaredhuang.xiao.presenter.ReadBookPresenter;
import com.jaredhuang.xiao.presenter.contract.BookListContract;
import com.jaredhuang.xiao.utils.NetworkUtils;
import com.jaredhuang.xiao.utils.RxUtils;
import com.jaredhuang.xiao.utils.theme.ATH;
import com.jaredhuang.xiao.utils.theme.ThemeStore;
import com.jaredhuang.xiao.view.activity.BookDetailActivity;
import com.jaredhuang.xiao.view.activity.ReadBookActivity;
import com.jaredhuang.xiao.view.adapter.BookShelfAdapter;
import com.jaredhuang.xiao.view.adapter.BookShelfGridAdapter;
import com.jaredhuang.xiao.view.adapter.BookShelfListAdapter;
import com.jaredhuang.xiao.view.adapter.base.OnItemClickListenerTwo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;

public class BookListFragment extends MBaseFragment<BookListContract.Presenter> implements BookListContract.View {

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.local_book_rv_content)
    RecyclerView rvBookshelf;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    @BindView(R.id.rl_empty_view)
    RelativeLayout rlEmptyView;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.action_bar)
    LinearLayout actionBar;
    @BindView(R.id.tv_select_count)
    TextView tvSelectCount;
    @BindView(R.id.iv_del)
    ImageView ivDel;
    @BindView(R.id.iv_select_all)
    ImageView ivSelectAll;

    private CallbackValue callbackValue;
    private Unbinder unbinder;
    private String bookPx;
    private boolean resumed = false;
    private boolean isRecreate;
    private int group;

    private BookShelfAdapter bookShelfAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            resumed = savedInstanceState.getBoolean("resumed");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public int createLayoutId() {
        return R.layout.fragment_book_list;
    }

    @Override
    protected BookListContract.Presenter initInjector() {
        return new BookListPresenter();
    }

    @Override
    protected void initData() {
        callbackValue = (CallbackValue) getActivity();
        bookPx = preferences.getString(getString(R.string.pk_bookshelf_px), "0");
        isRecreate = callbackValue != null && callbackValue.isRecreate();
    }

    @Override
    protected void bindView() {
        super.bindView();
        unbinder = ButterKnife.bind(this, view);
        int bookshelfLayout = preferences.getInt("bookshelfLayout", 0);
        if (bookshelfLayout == 0) {
            rvBookshelf.setLayoutManager(new LinearLayoutManager(getContext()));
            bookShelfAdapter = new BookShelfListAdapter(getActivity());
        } else {
            rvBookshelf.setLayoutManager(new GridLayoutManager(getContext(), bookshelfLayout + 2));
            bookShelfAdapter = new BookShelfGridAdapter(getActivity());
        }
        rvBookshelf.setAdapter((RecyclerView.Adapter) bookShelfAdapter);
        refreshLayout.setColorSchemeColors(ThemeStore.accentColor(MApplication.getInstance()));
    }

    @Override
    protected void firstRequest() {
        group = preferences.getInt("bookshelfGroup", 0);
        if (preferences.getBoolean(getString(R.string.pk_auto_refresh), false)
                && !isRecreate && NetworkUtils.isNetWorkAvailable() && group != 2) {
            mPresenter.queryBookShelf(true, group);
        } else {
            mPresenter.queryBookShelf(false, group);
        }
    }

    @Override
    protected void bindEvent() {
        refreshLayout.setOnRefreshListener(() -> {
            mPresenter.queryBookShelf(NetworkUtils.isNetWorkAvailable(), group);
            if (!NetworkUtils.isNetWorkAvailable()) {
                Toast.makeText(getContext(), R.string.network_connection_unavailable, Toast.LENGTH_SHORT).show();
            }
            refreshLayout.setRefreshing(false);
        });
        ItemTouchCallback itemTouchCallback = new ItemTouchCallback();
        itemTouchCallback.setSwipeRefreshLayout(refreshLayout);
        itemTouchCallback.setViewPager(callbackValue.getViewPager());
        if (bookPx.equals("2")) {
            itemTouchCallback.setDragEnable(true);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
            itemTouchHelper.attachToRecyclerView(rvBookshelf);
        } else {
            itemTouchCallback.setDragEnable(false);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
            itemTouchHelper.attachToRecyclerView(rvBookshelf);
        }
        bookShelfAdapter.setItemClickListener(getAdapterListener());
        itemTouchCallback.setOnItemTouchCallbackListener(bookShelfAdapter.getItemTouchCallbackListener());
        ivBack.setOnClickListener(v -> setArrange(false));
        ivDel.setOnClickListener(v -> {
            if (bookShelfAdapter.getSelected().size() == bookShelfAdapter.getBooks().size()) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.delete)
                        .setMessage(getString(R.string.sure_del_all_book))
                        .setPositiveButton(R.string.yes, (dialog, which) -> delSelect())
                        .setNegativeButton(R.string.no, null)
                        .show();
                ATH.setAlertDialogTint(alertDialog);
            } else {
                delSelect();
            }
        });
        ivSelectAll.setOnClickListener(v -> bookShelfAdapter.selectAll());
    }

    private OnItemClickListenerTwo getAdapterListener() {
        return new OnItemClickListenerTwo() {
            @Override
            public void onClick(View view, int index) {
                if (actionBar.getVisibility() == View.VISIBLE) {
                    upSelectCount();
                    return;
                }
                BookCollectBean bookCollectBean = bookShelfAdapter.getBooks().get(index);
                Intent intent = new Intent(getContext(), ReadBookActivity.class);
                intent.putExtra("openFrom", ReadBookPresenter.OPEN_FROM_APP);
                String key = String.valueOf(System.currentTimeMillis());
                String bookKey = "mBookCollectBean" + key;
                intent.putExtra("bookKey", bookKey);
                BitIntentDataManager.getInstance().putData(bookKey, bookCollectBean.clone());
                startActivityByAnim(intent, android.R.anim.fade_in, android.R.anim.fade_out);
            }

            @Override
            public void onLongClick(View view, int index) {
                BookCollectBean bookCollectBean = bookShelfAdapter.getBooks().get(index);
                String key = String.valueOf(System.currentTimeMillis());
                BitIntentDataManager.getInstance().putData(key, bookCollectBean.clone());
                Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                intent.putExtra("openFrom", BookDetailPresenter.FROM_BOOKSHELF);
                intent.putExtra("data_key", key);
                intent.putExtra("noteUrl", bookCollectBean.getNoteUrl());
                startActivityByAnim(intent, android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        if (resumed) {
            resumed = false;
            stopBookShelfRefreshAnim();
        }
    }

    @Override
    public void onPause() {
        resumed = true;
        super.onPause();
    }

    private void stopBookShelfRefreshAnim() {
        if (bookShelfAdapter.getBooks() != null && bookShelfAdapter.getBooks().size() > 0) {
            for (BookCollectBean bookCollectBean : bookShelfAdapter.getBooks()) {
                if (bookCollectBean.isLoading()) {
                    bookCollectBean.setLoading(false);
                    refreshBook(bookCollectBean.getNoteUrl());
                }
            }
        }
    }

    @Override
    public void refreshBookShelf(List<BookCollectBean> bookCollectBeanList) {
        bookShelfAdapter.replaceAll(bookCollectBeanList, bookPx);
        if (rlEmptyView == null) return;
        if (bookCollectBeanList.size() > 0) {
            rlEmptyView.setVisibility(View.GONE);
        } else {
            tvEmpty.setText(R.string.bookshelf_empty);
            rlEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void refreshBook(String noteUrl) {
        bookShelfAdapter.refreshBook(noteUrl);
    }

    @Override
    public void updateGroup(Integer group) {
        this.group = group;
    }

    @Override
    public void refreshError(String error) {
        toast(error);
    }

    @Override
    public SharedPreferences getPreferences() {
        return preferences;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setArrange(boolean isArrange) {
        if (bookShelfAdapter != null) {
            bookShelfAdapter.setArrange(isArrange);
            if (isArrange) {
                actionBar.setVisibility(View.VISIBLE);
                upSelectCount();
            } else {
                actionBar.setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void upSelectCount() {
        tvSelectCount.setText(String.format("%d/%d", bookShelfAdapter.getSelected().size(), bookShelfAdapter.getBooks().size()));
    }

    private void delSelect() {
        Single.create((SingleOnSubscribe<Boolean>) emitter -> {
            for (String noteUrl : bookShelfAdapter.getSelected()) {
                BookCollectHelp.removeFromBookShelf(BookCollectHelp.getBook(noteUrl));
            }
            bookShelfAdapter.getSelected().clear();
            emitter.onSuccess(true);
        }).compose(RxUtils::toSimpleSingle)
                .subscribe(new MySingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        mPresenter.queryBookShelf(false, group);
                    }
                });
    }

    public interface CallbackValue {
        boolean isRecreate();

        int getGroup();

        ViewPager getViewPager();
    }

}
