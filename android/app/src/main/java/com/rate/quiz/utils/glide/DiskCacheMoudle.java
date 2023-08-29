package com.rate.quiz.utils.glide;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.module.GlideModule;


public class DiskCacheMoudle implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDiskCache(
                new InternalCacheDiskCacheFactory(context, "glide_cache", 100 * 1024 * 1024));
        //builder.setDiskCache(
        //        new ExternalCacheDiskCacheFactory(context, "glide_cache", 100 * 1024 * 1024));
    }


    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {

    }
}