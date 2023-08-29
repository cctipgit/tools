package com.rate.quiz.ui2.adapter;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.duxl.baselib.utils.DisplayUtil;
import com.rate.quiz.R;
import com.rate.quiz.entity.RedeemPointItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PointsDetailAdapter extends BaseQuickAdapter<RedeemPointItem, BaseViewHolder> implements LoadMoreModule {

    private SimpleDateFormat format;
    private int drawableSize;

    public PointsDetailAdapter() {
        super(R.layout.item_point_detail);
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, RedeemPointItem item) {
        holder.setText(R.id.tv_name,item.title);
        holder.setText(R.id.tv_time,format.format(new Date(item.time)));
        Drawable d = AppCompatResources.getDrawable(getContext(),R.drawable.ic_points);
        if(drawableSize == 0){
            drawableSize = DisplayUtil.dip2px(getContext(),24);
        }
        if(d != null) {
            d.setBounds(0, 0, drawableSize, drawableSize);
        }
        TextView textView = holder.getView(R.id.tv_value);
        textView.setCompoundDrawables(d,null,null,null);
        textView.setText(item.pointChange);
    }
}
