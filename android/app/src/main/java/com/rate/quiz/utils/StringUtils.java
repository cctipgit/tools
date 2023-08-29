package com.rate.quiz.utils;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

public class StringUtils {
    public static String format(Context context, @StringRes int res, @Nullable Object... args){
        if (args != null && args.length > 0){
           return String.format(context.getString(res),args);
        }else{
            return context.getString(res);
        }
    }
}
