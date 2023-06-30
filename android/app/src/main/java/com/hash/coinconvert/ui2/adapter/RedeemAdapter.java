package com.hash.coinconvert.ui2.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hash.coinconvert.R;
import com.hash.coinconvert.entity.RedeemItem;


public class RedeemAdapter extends BaseQuickAdapter<RedeemItem, com.chad.library.adapter.base.viewholder.BaseViewHolder> {

    public RedeemAdapter() {
        super(R.layout.item_redeem);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, RedeemItem redeemItem) {

    }
}
