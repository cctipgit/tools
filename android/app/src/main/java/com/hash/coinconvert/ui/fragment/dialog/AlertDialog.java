package com.hash.coinconvert.ui.fragment.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.duxl.baselib.ui.dialog.BaseDialogFragment;
import com.duxl.baselib.utils.DisplayUtil;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.Utils;
import com.hash.coinconvert.R;
import com.hash.coinconvert.databinding.DialogAlertBinding;
import com.hash.coinconvert.utils.FontUtil;

public class AlertDialog extends BaseDialogFragment {

    private DialogAlertBinding mBinding;

    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;

    public static AlertDialog newInstance() {
        Bundle args = new Bundle();
        AlertDialog fragment = new AlertDialog();
        fragment.setCancelable2(false);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getResId() {
        return R.layout.dialog_alert;
    }

    @Override
    public int getWidth() {
        int width = DisplayUtil.getScreenWidth(getContext()) - DisplayUtil.dip2px(getContext(), 32);
        return width;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = DialogAlertBinding.bind(view);

        String title = getArguments().getString("title");
        if (EmptyUtils.isNotEmpty(title)) {
            mBinding.tvTitle.setText(title);
            mBinding.tvTitle.setVisibility(View.VISIBLE);
        } else {
            mBinding.tvMsg.setPadding(0, DisplayUtil.dip2px(getContext(), 40), 0, 0);
        }

        mBinding.tvMsg.setText(getArguments().getString("message"));

        FontUtil.setType(mBinding.tvOptionLeft, FontUtil.FOUNT_TYPE.POPPINS_MEDIUM);
        String leftText = getArguments().getString("leftText");
        mBinding.tvOptionLeft.setText(leftText);
        mBinding.tvOptionLeft.setOnClickListener(v -> {
            dismissDialog();
            if (mLeftClickListener != null) {
                mLeftClickListener.onClick(v);
            }
        });

        FontUtil.setType(mBinding.tvOptionRight, FontUtil.FOUNT_TYPE.POPPINS_MEDIUM);
        String rightText = getArguments().getString("rightText");
        mBinding.tvOptionRight.setText(rightText);
        mBinding.tvOptionRight.setOnClickListener(v -> {
            dismissDialog();
            if (mRightClickListener != null) {
                mRightClickListener.onClick(v);
            }
        });

        if (EmptyUtils.isEmpty(leftText) || EmptyUtils.isEmpty(rightText)) {
            mBinding.vOptionSpace.setVisibility(View.GONE);
        }

        if(EmptyUtils.isEmpty(leftText)) {
            mBinding.tvOptionLeft.setVisibility(View.GONE);
        }

        if(EmptyUtils.isEmpty(rightText)) {
            mBinding.tvOptionRight.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean getCancelOutside() {
        return false;
    }

    public AlertDialog setTitle(int title) {
        return setTitle(Utils.getString(title));
    }

    public AlertDialog setTitle(String title) {
        getArguments().putString("title", title);
        return this;
    }

    public AlertDialog setMessage(int msg) {
        return setMessage(Utils.getString(msg));
    }

    public AlertDialog setMessage(String msg) {
        getArguments().putString("message", msg);
        return this;
    }

    public AlertDialog setLeftButton(int text, View.OnClickListener listener) {
        return setLeftButton(Utils.getString(text), listener);
    }

    public AlertDialog setLeftButton(String text, View.OnClickListener listener) {
        getArguments().putString("leftText", text);
        this.mLeftClickListener = listener;
        return this;
    }

    public AlertDialog setRightButton(int text, View.OnClickListener listener) {
        return setRightButton(Utils.getString(text), listener);
    }

    public AlertDialog setRightButton(String text, View.OnClickListener listener) {
        getArguments().putString("rightText", text);
        this.mRightClickListener = listener;
        return this;
    }
}
