package com.rate.quiz.http;

import android.text.TextUtils;

import com.rate.quiz.utils.UUIDUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private String token;

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (TextUtils.isEmpty(token)) {
            this.token = UUIDUtils.getUUID();
        }
        Request old = chain.request();
        Request.Builder builder = old.newBuilder().header("Authorization", token);
        if ("POST".equals(old.method())) {
            if(old.body() == null || old.body().contentLength() == 0){
                builder.post(RequestBody.create(MediaType.parse("application/json"), "{}"));
            }
        }
        return chain.proceed(builder.build());
    }
}
