package com.hash.coinconvert.ui2.adapter;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hash.coinconvert.R;
import com.hash.coinconvert.database.entity.Token;
import com.hash.coinconvert.entity.RedeemItem;
import com.hash.coinconvert.utils.FlagLoader;

import java.util.Objects;


public class RedeemAdapter extends BaseQuickAdapter<RedeemItem, com.chad.library.adapter.base.viewholder.BaseViewHolder> {

    public RedeemAdapter() {
        super(R.layout.item_redeem);
        addChildClickViewIds(R.id.btn_redeem);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, RedeemItem item) {
        holder.setText(R.id.tv_value, item.unit + "" + item.reward + " " + item.symbol);
        holder.setText(R.id.tv_symbol, item.symbol);
        holder.setText(R.id.tv_rate, getProgressText(item.left, item.total));
        holder.setText(R.id.tv_rate_desc,getContext().getString(R.string.redeem_for,item.pointRequire));

        ProgressBar progressBar = holder.getView(R.id.progress);
        progressBar.setMax((int) item.pointRequire);
        progressBar.setProgress((int) (item.left > item.pointRequire ? item.pointRequire : item.left));

        holder.setImageResource(R.id.img_banner, Objects.equals(item.currencyType, Token.TOKEN_TYPE_CURRENCY)
                ? R.mipmap.redeem_banner_currency : R.mipmap.redeem_banner_crypto);
        holder.setEnabled(R.id.btn_redeem, item.left >= item.pointRequire);
        FlagLoader.load(holder.getView(R.id.img_logo),0,item.pic);

    }

    private SpannableString getProgressText(long left, long require) {
        String requireS = String.valueOf(require);
        SpannableString ss = new SpannableString(left + "/" + requireS);
        int end = ss.length();
        int start = end - requireS.length();

        if (left >= require) {
            start = 0;
        }
        ss.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(getContext().getColor(R.color.title)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }
}
