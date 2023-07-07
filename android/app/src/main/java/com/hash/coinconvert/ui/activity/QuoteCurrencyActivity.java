package com.hash.coinconvert.ui.activity;

import android.content.Intent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.duxl.baselib.utils.EmptyUtils;
import com.hash.coinconvert.R;
import com.hash.coinconvert.database.repository.TokenRepository;
import com.hash.coinconvert.databinding.ActivityQuoteCurrencyBinding;
import com.hash.coinconvert.ui.adapter.SwitchCurrencyAdapter;
import com.hash.coinconvert.utils.Dispatch;
import com.hash.coinconvert.widget.SearchView;
import com.hash.coinconvert.widget.SideBar;

import java.util.List;

/**
 * 报价货币选择
 */
public class QuoteCurrencyActivity extends SearchCurrencyActivity {

    private ActivityQuoteCurrencyBinding mBinding;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_quote_currency;
    }

    @Override
    protected void initView(View v) {
        mBinding = ActivityQuoteCurrencyBinding.bind(v);
        super.initView(v);
        setTitle(R.string.quote_currency);
        setDialogStyle();
    }

    @Override
    protected SearchView getSearchView() {
        return mBinding.searchView;
    }

    @Override
    protected SideBar getSideBar() {
        return mBinding.sideBar;
    }

    @Override
    protected List<String> getCheckedTokens() {
        return null;
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        SwitchCurrencyAdapter adapter = (SwitchCurrencyAdapter) super.getAdapter();
        adapter.setOnItemClickListener((adp, view, position) -> {
            SwitchCurrencyAdapter.ItemEntity item = adapter.getItemOrNull(position);
            if (EmptyUtils.isNotNull(item) && item.itemType == SwitchCurrencyAdapter.ItemEntity.ItemType.CONTENT) {
                Intent intent = new Intent()
                        .putExtra("unit", item.token.unitName)
                        .putExtra("data", item.token.token);
                setResult2(RESULT_OK, intent);
                finish();
                slideOutBottom();
            }
        });
        return adapter;
    }
}
