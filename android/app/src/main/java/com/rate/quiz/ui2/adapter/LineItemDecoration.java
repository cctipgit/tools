package com.rate.quiz.ui2.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.duxl.baselib.utils.DisplayUtil;
import com.rate.quiz.R;

public class LineItemDecoration  extends RecyclerView.ItemDecoration {

    private final int gap;
    private final Paint paint;

    private boolean drawLast;

    public LineItemDecoration(Context context,boolean drawLast){
        this(context);
        this.drawLast = drawLast;
    }

    public LineItemDecoration(Context context) {
        this.gap = DisplayUtil.dip2px(context, 8f);
        paint = new Paint();
        paint.setStrokeWidth(gap / 8f);
        paint.setColor(ContextCompat.getColor(context, R.color.list_divider_line));
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.bottom = gap;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        int paddingStart = parent.getPaddingStart();
        int paddingEnd = parent.getPaddingEnd();
        int drawCount = drawLast ? childCount : childCount-1;
        for (int i = 0; i < drawCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + gap;
            int y = (top + bottom) / 2;
            c.drawLine(paddingStart, y, parent.getWidth() - paddingEnd, y, paint);
        }
    }
}
