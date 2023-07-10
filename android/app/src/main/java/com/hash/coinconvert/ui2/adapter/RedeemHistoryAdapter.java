package com.hash.coinconvert.ui2.adapter;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hash.coinconvert.R;
import com.hash.coinconvert.entity.RedeemHistoryItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RedeemHistoryAdapter extends BaseQuickAdapter<RedeemHistoryItem, BaseViewHolder> implements LoadMoreModule {
    private final SimpleDateFormat format;

    public RedeemHistoryAdapter() {
        super(R.layout.item_point_detail);
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, RedeemHistoryItem item) {
        holder.setText(R.id.tv_name, item.title);
        holder.setText(R.id.tv_time, format.format(new Date(item.time)));
        setStateView(holder.getView(R.id.tv_value),item.status);
    }

    private void setStateView(TextView textView, int state) {
        switch (state) {
            case RedeemHistoryItem.STATUS_SUCCESS:
                textView.setText(R.string.redeem_history_state_success);
                textView.setTextColor(getContext().getColor(R.color.success));
                break;
            case RedeemHistoryItem.STATUS_FAIL:
                textView.setText(R.string.redeem_history_state_fail);
                textView.setTextColor(getContext().getColor(R.color.fail));
                break;
            default:
                textView.setText(R.string.redeem_history_state_pending);
                textView.setTextColor(getContext().getColor(R.color.pending));
                break;
        }
    }
}
