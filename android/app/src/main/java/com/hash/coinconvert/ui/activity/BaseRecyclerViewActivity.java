package com.hash.coinconvert.ui.activity;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.duxl.baselib.widget.SimpleOnLoadListener;
import com.duxl.baselib.widget.SmartRecyclerView;
import com.hash.coinconvert.R;

public abstract class BaseRecyclerViewActivity extends BaseActivity {
    SmartRecyclerView mSmartRecyclerView;

    private int mPageNum = 1;

    @Override
    protected int getLayoutResId() {
        return R.layout.srecyclerview;
    }

    @Override
    protected void initView(View v) {
        super.initView(v);
        initRecyclerView();
        initLoadData();
    }

    protected void initRecyclerView() {
        mSmartRecyclerView = findViewById(R.id.smart_recycler_view);
        resetRecyclerStatusView(mSmartRecyclerView);
        mSmartRecyclerView.getContentView().setAdapter(getAdapter());
        mSmartRecyclerView.getRefreshLayout().setOnLoadListener(new SimpleOnLoadListener() {
            @Override
            public void onRefresh() {
                mPageNum = 1;
                loadData(mPageNum);
            }

            @Override
            public void onLoadMore() {
                mPageNum++;
                loadData(mPageNum);
            }
        });
    }

    protected abstract BaseQuickAdapter getAdapter();

    protected void initLoadData() {
        loadData(mPageNum);
    }

    protected abstract void loadData(int pageNum);
}
