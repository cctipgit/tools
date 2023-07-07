package com.hash.coinconvert.utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.widget.TextView;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 设置字体文件
 */
public class FontUtil {

    private static Map<FOUNT_TYPE, SoftReference<Typeface>> cache = new HashMap<>();

    public static void setType(TextView view, FOUNT_TYPE type) {
    }

    public enum FOUNT_TYPE {
        POPPINS_BOLD("Poppins-Bold.ttf"),
        POPPINS_EXTRA_LIGHT("Poppins-ExtraLight.ttf"),
        POPPINS_LIGHT("Poppins-Light.ttf"),
        POPPINS_MEDIUM("Poppins-Medium.ttf"),
        POPPINS_REGULAR("Poppins-Regular.ttf"),
        POPPINS_SEMI_BOLD("Poppins-SemiBold.ttf");

        public String fileName;

        FOUNT_TYPE(String fileName) {
            this.fileName = fileName;
        }
    }
}
