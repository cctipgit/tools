package com.hash.coinconvert.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.duxl.baselib.utils.DisplayUtil;
import com.hash.coinconvert.databinding.LayoutLetterPopBinding;
import com.hash.coinconvert.utils.FontUtil;

public class LetterPopupWindow {

    private PopupWindow popWindow;
    private LayoutLetterPopBinding mBinding;
    private final int x;

    public LetterPopupWindow(Context context) {
        // 实例化PopupWindow
        mBinding = LayoutLetterPopBinding.inflate(LayoutInflater.from(context));
        popWindow = new PopupWindow(mBinding.getRoot(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popWindow.setBackgroundDrawable(new ColorDrawable());
        popWindow.setFocusable(true);
        x = DisplayUtil.dip2px(context, 30);
        FontUtil.setType(mBinding.tvLetter, FontUtil.FOUNT_TYPE.POPPINS_MEDIUM);
    }

    public void setLetter(String letter) {
        mBinding.tvLetter.setText(letter);
    }

    public void show(View parent) {
        popWindow.showAtLocation(parent, Gravity.RIGHT, x, 0);
    }

    public void update(int y) {
        popWindow.update(x, y - popWindow.getHeight() / 2, popWindow.getWidth(), popWindow.getHeight());
    }

    public void dismiss() {
        popWindow.dismiss();
    }
}
