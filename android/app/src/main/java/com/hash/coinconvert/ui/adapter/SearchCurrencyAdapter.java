package com.hash.coinconvert.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hash.coinconvert.R;

/**
 * 搜索货币列表适配器
 */
public class SearchCurrencyAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {

    public SearchCurrencyAdapter() {
        super(R.layout.adapter_search_currency_listitem);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Object o) {

    }
}
