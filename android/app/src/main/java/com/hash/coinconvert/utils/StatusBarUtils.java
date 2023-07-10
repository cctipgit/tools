package com.hash.coinconvert.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 *  about status bar
 */
public class StatusBarUtils {

    /**
     * getStatusBar height
     * @param context context
     * @return  status bar height ,but note that the unit is pix
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        @SuppressLint({"InternalInsetResource", "DiscouragedApi"})
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void setViewHeightEqualsStatusBarHeight(View view){
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                StatusBarUtils.getStatusBarHeight(view.getContext())));
    }
    public static void setViewHeightEqualsStatusBarHeightLinear(View view){
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                StatusBarUtils.getStatusBarHeight(view.getContext())));
    }
}
