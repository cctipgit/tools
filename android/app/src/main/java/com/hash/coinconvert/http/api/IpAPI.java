package com.hash.coinconvert.http.api;

import com.hash.coinconvert.entity.IpInfo;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IpAPI {

    /*@GET("http://ip-api.com/json/{ip}")
    Observable<IpInfo> get(@Path("ip") String ip);*/

    @GET("http://ip-api.com/json")
    Observable<IpInfo> get();
}
