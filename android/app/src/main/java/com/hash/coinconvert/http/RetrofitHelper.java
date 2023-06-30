package com.hash.coinconvert.http;

import android.annotation.SuppressLint;

import com.duxl.baselib.http.interceptor.LogInterceptor;
import com.hash.coinconvert.utils.GsonHelper;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitHelper {

    private static final int DEFAULT_TIME_OUT = 30;//超时时间 5s
    private static final int DEFAULT_READ_TIME_OUT = 30;
    private OkHttpClient client;
    private Retrofit retrofit;

    private RetrofitHelper() throws Exception {
        client = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(new AuthInterceptor())
                .addInterceptor(new LogInterceptor())
                .sslSocketFactory(createEasySSLContext().getSocketFactory(), (X509TrustManager) trustALl[0])
                .build();
        retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(GsonHelper.getGsonInstance()))
                .baseUrl("https://7697d200.cwallet.com").build();
    }

    private static class Holder {
        private static final RetrofitHelper INSTANCE;

        static {
            try {
                INSTANCE = new RetrofitHelper();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public <T> T create_(Class<T> clz) {
        return retrofit.create(clz);
    }

    public static <T> T create(Class<T> clz) {
        return Holder.INSTANCE.create_(clz);
    }


    @SuppressLint("CustomX509TrustManager")
    private static final TrustManager[] trustALl = new TrustManager[]{
            new X509TrustManager() {
                @SuppressLint("TrustAllX509TrustManager")
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {

                }

                @SuppressLint("TrustAllX509TrustManager")
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            }
    };

    private static SSLContext createEasySSLContext() throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, trustALl, new SecureRandom());
        return context;
    }
}
