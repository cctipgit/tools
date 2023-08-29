package com.rate.quiz.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class GsonHelper {

    private final Gson gson;

    private GsonHelper() {
        gson = new GsonBuilder().create();
    }

    private static class Holder {
        public static final GsonHelper INSTANCE = new GsonHelper();
    }


    public static Gson getGsonInstance() {
        return Holder.INSTANCE.gson;
    }

    public static String toJsonString(Object obj) {
        return getGsonInstance().toJson(obj);
    }

    public static <T> T fromJsonString(String json, Class<T> clz) {
        return getGsonInstance().fromJson(json, clz);
    }
}
