package com.hash.coinconvert.ui.activity;

import android.content.Intent;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.duxl.baselib.utils.DisplayUtil;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.ToastUtils;
import com.duxl.baselib.widget.SimpleTextWatcher;
import com.duxl.baselib.widget.SmartRecyclerView;
import com.hash.coinconvert.R;
import com.hash.coinconvert.databinding.ActivitySwitchCurrencySearchBinding;
import com.hash.coinconvert.ui.adapter.SearchCurrencyAdapter;
import com.hash.coinconvert.utils.DataUtil;
import com.hash.coinconvert.widget.AppStatusView;

/**
 * 切换货币 - 搜索
 */
public class SwitchCurrencySearchActivity extends BaseRecyclerViewActivity implements TextView.OnEditorActionListener {

    protected ActivitySwitchCurrencySearchBinding mBinding;
    private SearchCurrencyAdapter mAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_switch_currency_search;
    }

    @Override
    protected void initView(View v) {
        super.initView(v);
        hideActionBar();
        mBinding = ActivitySwitchCurrencySearchBinding.bind(v);
        mBinding.ivActionBack.setOnClickListener(this::onClickActionBack);
        mBinding.etSearch.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mBinding.ivSearchIcon.setVisibility(s.length() > 0 ? View.GONE : View.VISIBLE);
            }
        });
        mBinding.etSearch.setOnEditorActionListener(this);
    }

    @Override
    protected AppStatusView resetRecyclerStatusView(SmartRecyclerView recyclerView) {
        AppStatusView statusView = super.resetRecyclerStatusView(recyclerView);
        statusView.setEmptyImgRes(R.mipmap.empty_search);
        statusView.setEmptyText(getString(R.string.search_currency_empty_msg));
        return statusView;
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new SearchCurrencyAdapter();
            mAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    setResult2(RESULT_OK, new Intent().putExtra("data", position));
                    finish();
                }
            });
        }
        return mAdapter;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH && (event == null || event.getRepeatCount() == 0)) {
            String keywords = mBinding.etSearch.getText().toString().trim();
            if (EmptyUtils.isNotEmpty(keywords)) {
                ToastUtils.show("搜索:" + keywords);
                DisplayUtil.hideKeyboard(this);
                loadData(1);
            }

        }
        return true;
    }

    @Override
    protected void initLoadData() {
    }

    @Override
    protected void loadData(int pageNum) {
        if (System.currentTimeMillis() % 2 == 0) {
            mSmartRecyclerView.getStatusView().showEmpty();
        } else {
            mSmartRecyclerView.getStatusView().showContent();
            getAdapter().setNewInstance(DataUtil.getList(5));
            mSmartRecyclerView.finishLoadMore();
            mSmartRecyclerView.finishRefresh();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_none, R.anim.slide_out_left);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_none, R.anim.slide_out_left);
    }
}
