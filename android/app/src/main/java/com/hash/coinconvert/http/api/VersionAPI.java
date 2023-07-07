package com.hash.coinconvert.http.api;

import com.hash.coinconvert.entity.VersionInfo;
import com.hash.coinconvert.http.Root;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface VersionAPI {

    @GET("appVersion/get?type=2")
    Observable<Root<VersionInfo>> get();
}
