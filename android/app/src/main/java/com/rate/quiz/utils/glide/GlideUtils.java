package com.rate.quiz.utils.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.Target;
import com.duxl.baselib.utils.DisplayUtil;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class GlideUtils {

    /**
     * 显示图片Imageview
     *
     * @param context   上下文
     * @param errorImg  错误的资源图片
     * @param url       图片链接
     * @param imageView 组件
     */
    public static void showImageView(Context context, int errorImg, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .error(errorImg)
                .placeholder(errorImg)
                .into(imageView);
    }

    /**
     * 显示图片Imageview
     *
     * @param context   上下文
     * @param errorImg  错误的资源图片
     * @param url       图片链接
     * @param imageView 组件
     */
    public static void showImageViewWithCenterCrop(Context context, int errorImg, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .error(errorImg)
                .placeholder(errorImg)
                .transform(new CenterCrop())
                .into(imageView);
    }

    /**
     * 显示图片Imageview
     *
     * @param context   上下文
     * @param url       图片链接
     * @param imageView 组件
     */
    public static void showImageView(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .into(imageView);
    }

    public static void showImageView2(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);
    }


    /**
     * 显示图片Imageview
     *
     * @param context    上下文
     * @param errorImg   错误的资源图片
     * @param resourceId 图片链接
     * @param imageView  组件
     */
    public static void showImageView(Context context, int errorImg, Integer resourceId, ImageView imageView) {
        Glide.with(context)
                .load(resourceId)
                .error(errorImg)
                .placeholder(errorImg)
                .into(imageView);
    }


    /**
     * 显示图片 圆形显示  ImageView
     *
     * @param context   上下文
     * @param errorImg  错误的资源图片
     * @param url       图片链接
     * @param imageView 组件
     */
    public static void showImageViewToCircle(Context context, int errorImg, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .error(errorImg)
                .placeholder(errorImg)
                .transform(new CircleCrop())
                .into(imageView);
    }


    /**
     * 显示图片 圆形显示  ImageView
     *
     * @param context   上下文
     * @param errorImg  错误的资源图片
     * @param url       图片链接
     * @param imageView 组件
     */
    public static void showImageViewToCircle(Context context, int errorImg, Integer url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .error(errorImg)
                .placeholder(errorImg)
                .transform(new GlideCircleTransform(context))
                .into(imageView);
    }

    /**
     * 圆角
     *
     * @param context
     * @param errorImg
     * @param url
     * @param imageView
     * @param radiusDP  半径
     */
    public static void showImageViewToRoundWidthCenterCrop(Context context, int errorImg, String url, ImageView imageView, int radiusDP) {
        Glide.with(context).load(url)
                .error(errorImg)
                .placeholder(errorImg)
                .transform(new CenterCrop(), new RoundedCornersTransformation(DisplayUtil.dip2px(context, radiusDP), 0, RoundedCornersTransformation.CornerType.ALL))
                .into(imageView);
    }

    /**
     * 圆角
     *
     * @param context
     * @param errorImg
     * @param url
     * @param imageView
     * @param radiusDP  半径
     */
    public static void showImageViewToRound(Context context, int errorImg, String url, ImageView imageView, int radiusDP) {
        Glide.with(context).load(url)
                .error(errorImg)
                .placeholder(errorImg)
                .transform(new RoundedCornersTransformation(DisplayUtil.dip2px(context, radiusDP), 0, RoundedCornersTransformation.CornerType.ALL))
                .into(imageView);

    }


    /**
     * 圆角
     *
     * @param context
     * @param errorImg
     * @param imgRes
     * @param imageView
     * @param radiusDP  半径
     */
    public static void showImageViewToRound(Context context, int errorImg, int imgRes, ImageView imageView, int radiusDP) {
        Glide.with(context).load(imgRes)
                .error(errorImg)
                .placeholder(errorImg)
                .transform(new RoundedCornersTransformation(DisplayUtil.dip2px(context, radiusDP), 0, RoundedCornersTransformation.CornerType.ALL))
                .into(imageView);

    }

    /**
     * 圆角
     *
     * @param context
     * @param errorImg
     * @param url
     * @param imageView
     * @param cornerType 圆角位置
     * @param radiusDP   圆角半径
     */
    public static void showImageViewToRound(Context context, int errorImg, String url, ImageView imageView, RoundedCornersTransformation.CornerType cornerType, int radiusDP) {
        Glide.with(context).load(url)
                .error(errorImg)
                .placeholder(errorImg)
                .transform(new RoundedCornersTransformation(DisplayUtil.dip2px(context, radiusDP), 0, cornerType))
                .into(imageView);

    }


    /**
     * 圆角
     *
     * @param context
     * @param errorImg
     * @param url
     * @param imageView
     * @param cornerType 圆角位置
     * @param radiusDP   圆角半径
     */
    public static void showImageViewToRoundWidthCenterCrop(Context context, int errorImg, String url, ImageView imageView, RoundedCornersTransformation.CornerType cornerType, int radiusDP) {
        Glide.with(context).load(url)
                .error(errorImg)
                .placeholder(errorImg)
                .transform(new CenterCrop(), new RoundedCornersTransformation(DisplayUtil.dip2px(context, radiusDP), 0, cornerType))
                .into(imageView);

    }

    /**
     * Gif
     *
     * @param context
     * @param drawable
     * @param imageView
     */
    public static void showImageViewToGif(Context context, int drawable, ImageView imageView) {
        Glide.with(context)
                .asGif()
                .load(drawable)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);

    }

    /**
     * Gif
     *
     * @param context
     * @param url
     * @param imageView
     */
    public static void showImageViewUrlToGif(Context context, String url, ImageView imageView, RoundedCornersTransformation.CornerType cornerType, int radiusDP) {
        Glide.with(context)
                .asGif()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transform(new RoundedCornersTransformation(DisplayUtil.dip2px(context, radiusDP), 0, cornerType))
                .into(imageView);

    }

    /**
     * Gif
     *
     * @param context
     * @param url
     * @param imageView
     */
    public static void showImageViewUrlToGifWidthCenterCrop(Context context, String url, ImageView imageView, RoundedCornersTransformation.CornerType cornerType, int radiusDP) {
        Glide.with(context)
                .asGif()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transform(new CenterCrop(), new RoundedCornersTransformation(DisplayUtil.dip2px(context, radiusDP), 0, cornerType))
                .into(imageView);

    }

    /**
     * path获取bitmap
     *
     * @param path
     * @return
     */
    public static Bitmap getBitmapForPath(Context context, String path) {
        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(context)
                    .asBitmap()
                    .load(path)
                    .centerCrop()
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}