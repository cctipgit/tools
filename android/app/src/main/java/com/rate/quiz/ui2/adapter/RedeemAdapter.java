package com.rate.quiz.ui2.adapter;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.rate.quiz.R;
import com.rate.quiz.entity.RedeemItem;
import com.rate.quiz.utils.FlagLoader;


public class RedeemAdapter extends BaseQuickAdapter<RedeemItem, com.chad.library.adapter.base.viewholder.BaseViewHolder> {

    public RedeemAdapter() {
        super(R.layout.item_redeem);
        addChildClickViewIds(R.id.btn_redeem);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, RedeemItem item) {
        holder.setText(R.id.tv_value, item.unit + "" + item.reward + " " + item.symbol);
        holder.setText(R.id.tv_rate, getProgressText(item.left, item.total));
        holder.setText(R.id.tv_rate_desc,getContext().getString(R.string.redeem_for,item.pointRequire));

        ProgressBar progressBar = holder.getView(R.id.progress);
        progressBar.setMax((int) item.pointRequire);
        progressBar.setProgress((int) item.left);
        FlagLoader.load(holder.getView(R.id.img_logo),0,item.pic);
    }

    private SpannableString getProgressText(long left, long total) {
        String requireS = String.valueOf(total);
        SpannableString ss = new SpannableString(left + "/" + requireS);
        int end = ss.length();
        int start = end - requireS.length();

        if (left >= total) {
            start = 0;
        }
        ss.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(getContext().getColor(R.color.theme_text_color)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }
}
