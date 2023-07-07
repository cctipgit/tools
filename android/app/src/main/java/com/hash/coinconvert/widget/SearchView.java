package com.hash.coinconvert.widget;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;

import com.duxl.baselib.utils.DisplayUtil;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.widget.SimpleTextWatcher;
import com.hash.coinconvert.R;
import com.hash.coinconvert.databinding.LayoutSearchViewBinding;

/**
 * 搜索View
 */
public class SearchView extends FrameLayout {

    private LayoutSearchViewBinding mBinding;

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_search_view, this, false);
        mBinding = LayoutSearchViewBinding.bind(view);
        addView(view);
        mBinding.etSearch.setCursorVisible(false);
        mBinding.etSearch.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                mBinding.etSearch.setCursorVisible(true);
                mBinding.ivClearIcon.setVisibility(View.VISIBLE);
                mBinding.ivSearchIcon.setVisibility(View.GONE);
                if(mOnActionChangedListener != null) {
                    mOnActionChangedListener.onKeywordsChanged(mBinding.etSearch.getText().toString());
                }
            }
            return false;
        });
        mBinding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH && (event == null || event.getRepeatCount() == 0)) {
                String keywords = mBinding.etSearch.getText().toString().trim();
                if (EmptyUtils.isNotEmpty(keywords)) {
                    DisplayUtil.hideKeyboard(v, context);
                }

            }
            return true;
        });
        mBinding.etSearch.addTextChangedListener(new SimpleTextWatcher(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if(mOnActionChangedListener != null) {
                    mOnActionChangedListener.onKeywordsChanged(s.toString());
                }
            }
        });

        mBinding.ivClearIcon.setOnClickListener(v->{
            mBinding.etSearch.getText().clear();
            mBinding.etSearch.setCursorVisible(false);
            mBinding.ivClearIcon.setVisibility(View.GONE);
            mBinding.ivSearchIcon.setVisibility(View.VISIBLE);
            DisplayUtil.hideKeyboard(v, context);
            if(mOnActionChangedListener != null) {
                mOnActionChangedListener.onCloseSearch();
            }
        });
    }

    /**
     * 是否是编辑输入模式
     * @return
     */
    public boolean isEditing() {
        return mBinding.ivClearIcon.getVisibility() == View.VISIBLE;
    }

    /**
     * 重置到最初状态
     */
    public void reset() {
        mBinding.ivClearIcon.callOnClick();
    }

    public interface OnActionChangedListener {
        void onKeywordsChanged(String keywords);
        void onCloseSearch();
    }

    private OnActionChangedListener mOnActionChangedListener;

    public void setOnActionChangedListener(OnActionChangedListener listener) {
        this.mOnActionChangedListener = listener;
    }
}
