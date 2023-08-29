package com.rate.quiz.http.api;

import com.rate.quiz.entity.VersionInfo;
import com.rate.quiz.http.Root;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface VersionAPI {

    @GET("appVersion/get?type=2")
    Observable<Root<VersionInfo>> get();
}
