package com.hash.coinconvert.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable divider;

    public DividerItemDecoration(Context context, @DrawableRes int deliverResource) {
        // 从资源文件中获取分割线的Drawable
        divider = context.getResources().getDrawable(deliverResource);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (i != childCount - 1) {
                // 获取每个Item的底部位置
                int bottom = parent.getChildAt(i).getBottom();
                // 根据底部位置绘制分割线
                divider.setBounds(left, bottom, right, bottom + divider.getIntrinsicHeight());
                divider.draw(c);
            }
        }
    }
}
