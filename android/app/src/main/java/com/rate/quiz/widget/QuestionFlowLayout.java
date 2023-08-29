package com.rate.quiz.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.duxl.baselib.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

public class QuestionFlowLayout extends ViewGroup {

    private int lineSpacing;
    private int itemSpacing;
    private int rowCount;

    private static final int TAG_LAYOUT_TAG = "QuestionFlowLayout_Child_Layout".hashCode();

    public QuestionFlowLayout(Context context) {
        super(context);
        init(context);
    }

    public QuestionFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public QuestionFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        int space = DisplayUtil.dip2px(context, 8);
        lineSpacing = space;
        itemSpacing = space;
    }

    private SparseArray<List<View>> map = new SparseArray<>();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            int row = (int) child.getTag(TAG_LAYOUT_TAG);
            if (map.get(row) == null) {
                List<View> list = new ArrayList<>();
                list.add(child);
                map.put(row, list);
            } else {
                List<View> list = map.get(row);
                list.add(child);
            }
        }
        int width = getMeasuredWidth();
        int layoutHeight = 0;
        int rowMaxHeight = 0;
        for (int i = 1; i < rowCount + 1; i++) {
            int left = 0;
            List<View> list = map.get(i);
            if (list.size() == 1) {
                View child = list.get(0);
                child.layout(left, layoutHeight, left + child.getMeasuredWidth(), layoutHeight + child.getMeasuredHeight());
                break;
            }
            int childrenTotalWidth = 0;
            for (View v : list) {
                childrenTotalWidth += v.getMeasuredWidth();
            }
            int empty = width - childrenTotalWidth - (list.size() - 1) * itemSpacing;
            int increaseWidthOfChild = empty / list.size();

            for (View child : list) {
                rowMaxHeight = Math.max(rowMaxHeight, child.getMeasuredHeight());
                child.layout(left, layoutHeight, left + child.getMeasuredWidth() + increaseWidthOfChild, rowMaxHeight);
                left += child.getMeasuredWidth() + increaseWidthOfChild + itemSpacing;
            }
            layoutHeight += rowMaxHeight + lineSpacing;
        }
        map.clear();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        final int childCount = getChildCount();

        int measureHeight = 0;
        int rowWidth = 0;
        rowCount = 1;
        int lineMaxHeight = 0;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) continue;
            rowWidth += child.getMeasuredWidth();
            lineMaxHeight = Math.max(child.getMeasuredHeight(), lineMaxHeight);
            if (rowWidth >= width) {
                rowCount++;
                measureHeight += lineMaxHeight;

                //move current child to next row
                rowCount++;
                rowWidth = child.getMeasuredWidth();
                measureHeight+=lineMaxHeight;
                lineMaxHeight = child.getMeasuredHeight();
            } else {
                rowWidth += itemSpacing;
            }
            child.setTag(TAG_LAYOUT_TAG, rowCount);
        }
        measureHeight += lineSpacing * (rowCount - 1);
        int finalWidth = MeasureSpec.makeMeasureSpec(width, widthMode);
        int finalHeight = MeasureSpec.makeMeasureSpec(measureHeight, MeasureSpec.AT_MOST);
        setMeasuredDimension(finalWidth, finalHeight);
    }
}
