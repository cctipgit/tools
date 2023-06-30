package com.hash.coinconvert.ui.activity;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.duxl.baselib.utils.SPUtils;
import com.gw.swipeback.SwipeBackLayout;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.R;
import com.hash.coinconvert.ui.adapter.SettingListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置货币默认（数量）值
 */
public class SettingCurrencyValueActivity extends BaseRecyclerViewActivity implements OnItemClickListener {

    private SettingListAdapter mAdapter;
    private int[] items = null;

    @Override
    protected void initParams(Intent args) {
        super.initParams(args);
        items = getResources().getIntArray(R.array.default_currency_value);
    }

    @Override
    protected void initView(View v) {
        super.initView(v);
        setDialogStyle();
        setSlideEnable(SwipeBackLayout.FROM_TOP);
        setObserveOtherSlideFinish();
        setTitle(R.string.default_currency_value);
        mSmartRecyclerView.setEnableRefresh(false);
        mSmartRecyclerView.setEnableLoadMore(false);
    }

    @Override
    protected void onClickActionBack(View v) {
        super.onClickActionBack(v);
        slideOutRight();
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        if(mAdapter == null) {
            mAdapter = new SettingListAdapter();
            int defaultValue = SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE, Constants.SP.DEFAULT.DEFAULT_CURRENCY_VALUE);
            List<SettingListAdapter.ListItem> listData = new ArrayList();
            for (int item : items) {
                listData.add(new SettingListAdapter.ListItem(String.valueOf(item), defaultValue == item));
            }
            mAdapter.setNewInstance(listData);
            mAdapter.setOnItemClickListener(this);
        }
        return mAdapter;
    }

    @Override
    protected void loadData(int pageNum) {

    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        int value = items[position];
        SPUtils.getInstance().put(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE, value);
        setResult2(RESULT_OK);
        finish();
    }
}