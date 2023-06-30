package com.hash.coinconvert.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.duxl.baselib.utils.DisplayUtil;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.SPUtils;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.entity.CurrencyInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页换算
 */
public class ConvertGroupView extends LinearLayout implements ConvertItemView.OnInputCountChangedListener, DefaultLifecycleObserver {

    private static final String TAG = "ConvertGroupView";
    private List<ConvertItemView> mChildView = new ArrayList<>();
    private int mSelectItemPosition = 0; // 当前选中的item
    private LifecycleOwner mLifecycleOwner;
    private int mItemHeight;
    private int mSpace;

    public ConvertGroupView(@NonNull Context context) {
        this(context, null);
    }

    public ConvertGroupView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        mSpace = DisplayUtil.dip2px(getContext(), 10);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int groupHeight = getMeasuredHeight();
        if (groupHeight <= 0 || EmptyUtils.isNotEmpty(mChildView)) {
            return;
        }
        int rateItemCount = SPUtils.getInstance().getInt(Constants.SP.KEY.RATE_ITEM_COUNT, -1);
        if (rateItemCount < 0) {
            ConvertItemView childView = new ConvertItemView(getContext());
            int size = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            childView.measure(size, size);
            int childMinHeight = childView.getMeasuredHeight();
            rateItemCount = (groupHeight - mSpace) / childMinHeight;
            SPUtils.getInstance().put(Constants.SP.KEY.RATE_ITEM_COUNT, rateItemCount);
        }

        mItemHeight = (groupHeight - mSpace) / rateItemCount;
        mSpace = groupHeight - mItemHeight * rateItemCount;

        for (int i = 0; i < rateItemCount; i++) {
            ConvertItemView childView = new ConvertItemView(getContext());
            childView.setSelectHeight(mItemHeight, mSpace);
            int finalI = i;
            childView.setOnClickListener(v -> setSelectItemPosition(finalI));
            childView.setOnSlideListener((orientation, itemView) -> {
                if (mOnSlideListener != null) {
                    mOnSlideListener.onSlide(finalI, orientation, itemView.getCurrencyInfo());
                }
            });
            mChildView.add(childView);
            addView(childView, new LayoutParams(LayoutParams.MATCH_PARENT, i == mSelectItemPosition ? mItemHeight + mSpace : mItemHeight));
        }
        setSelectItemPosition(mSelectItemPosition);
        setLifecycleOwner(mLifecycleOwner);
        if (mOnConvertItemsAddedListener != null) {
            mOnConvertItemsAddedListener.onConvertItemsAdded();
        }

    }

    public void setLifecycleOwner(LifecycleOwner owner) {
        if (owner == null) {
            return;
        }
        this.mLifecycleOwner = owner;
        mLifecycleOwner.getLifecycle().addObserver(this);
        if (EmptyUtils.isNotEmpty(getConvertItemViews())) {
            for (ConvertItemView convertItemView : getConvertItemViews()) {
                convertItemView.setLifecycleOwner(owner);
            }
        }
    }

    public void closeSwipe() {
        for (int i = 0; i < mChildView.size(); i++) {
            getConvertItemView(i).closeSwipe();
        }
    }

    public ConvertItemView getConvertItemView(int position) {
        return mChildView.get(position);
    }

    public List<ConvertItemView> getConvertItemViews() {
        return mChildView;
    }

    public void setSelectItemPosition(int position) {
        if (position >= 0 && position < mChildView.size()) {
            this.mSelectItemPosition = position;
        }
        if (getConvertItemView(position).isSelected()) {
            return;
        }

        for (int i = 0; i < mChildView.size(); i++) {
            ConvertItemView convertItemView = getConvertItemView(i);
            convertItemView.setSelected(i == position);
            convertItemView.setOnInputCountChangedListener(i == position ? this : null);
        }
    }

    @Override
    public void onInputCountCountChanged(String count) {
        Log.i(TAG, "输入的内容已改变，重新计算汇率：" + count);
    }

    public void onKeyInput(int code) {
        // 有输入操作，将非当前选中项输入的内容清除
        for (int i = 0; i < getConvertItemViews().size(); i++) {
            if (i != mSelectItemPosition) {
                // 切换了选中项，将之前选中输入的内容清除
                getConvertItemView(i).clearInputKey();
            }
        }

        if (code == -99) {
            getConvertItemView(mSelectItemPosition).delKey();
            return;
        }

        if (code == -88) {
            getConvertItemView(mSelectItemPosition).longDelKey();
            return;
        }

        if (code == -30) {
            getConvertItemView(mSelectItemPosition).longTimesKey();
            return;
        }

        if (code == -40) {
            getConvertItemView(mSelectItemPosition).longDivKey();
            return;
        }

        String key = null;
        if (code >= 0 && code <= 9) {
            key = String.valueOf(code);
        }
        if (code == -1) {
            key = "+";
        } else if (code == -2) {
            key = "-";
        } else if (code == -3) {
            key = "*";
        } else if (code == -4) {
            key = "/";
        } else if (code == -5) {
            key = ".";
        }
        if (key != null) {
            getConvertItemView(mSelectItemPosition).appendKey(key);
        }
    }

    public interface OnSlideListener {
        /**
         * 滑动事件
         *
         * @param position    货币索引ß
         * @param orientation -1左滑动，1右滑动
         */
        void onSlide(int position, int orientation, CurrencyInfo currencyInfo);
    }

    private OnSlideListener mOnSlideListener;

    /**
     * 设置汇率item左右滑动监听
     *
     * @param listener
     */
    public void setOnSlideListener(OnSlideListener listener) {
        this.mOnSlideListener = listener;
    }

    /**
     * 设置汇率数据
     *
     * @param list
     */
    public void setCurrencyData(List<CurrencyInfo> list) {
        if (EmptyUtils.isEmpty(list)) {
            return;
        }
        for (int i = 0; i < list.size() && i < mChildView.size(); i++) {
            mChildView.get(i).setCurrencyInfo(list.get(i));
        }
    }

    /**
     * 汇率item已经添加回调
     */
    public interface OnConvertItemsAddedListener {
        void onConvertItemsAdded();
    }

    private OnConvertItemsAddedListener mOnConvertItemsAddedListener;

    /**
     * 汇率item已经添加回调
     *
     * @param listener
     */
    public void setOnConvertItemsAddedListener(OnConvertItemsAddedListener listener) {
        this.mOnConvertItemsAddedListener = listener;
    }
}
