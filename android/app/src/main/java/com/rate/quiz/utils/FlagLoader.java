package com.rate.quiz.utils;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.Utils;
import com.rate.quiz.utils.glide.GlideUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * 国旗图标加载
 */
public class FlagLoader {

    @SuppressLint("NewApi")
    public static void load(ImageView iv, int errImg, String icon) {
        if (!isNetworkImage(icon)) {
            try {
                Optional<String> optional = Arrays.stream(Utils.getApp().getAssets().list("flag")).filter(it -> (icon + ".png").equalsIgnoreCase(it) || (icon + ".jpg").equalsIgnoreCase(it)).findFirst();
                if (optional.isPresent()) {
                    String fileName = String.format("flag/%s", optional.get());
                    iv.setImageDrawable(Drawable.createFromStream(Utils.getActContextOrApp().getAssets().open(fileName), null));
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            iv.setImageResource(errImg);
        } else {

            GlideUtils.showImageViewToRound(iv.getContext(), errImg, icon, iv, 4);
        }
    }

    public static boolean isNetworkImage(String icon) {
        if (EmptyUtils.isNotEmpty(icon)) {
            return icon.toLowerCase().startsWith("http")
                    || icon.toLowerCase().endsWith(".jpg")
                    || icon.toLowerCase().endsWith(".png");
        }
        return false;
    }
}
