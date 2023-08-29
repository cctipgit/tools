package com.rate.quiz.http.api;

import com.rate.quiz.entity.IpInfo;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface IpAPI {

    /*@GET("http://ip-api.com/json/{ip}")
    Observable<IpInfo> get(@Path("ip") String ip);*/

    @GET("http://ip-api.com/json")
    Observable<IpInfo> get();
}
