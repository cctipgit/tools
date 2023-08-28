package com.hash.coinconvert.http.api;

import com.hash.coinconvert.entity.AResponse;
import com.hash.coinconvert.entity.ChartItem;
import com.hash.coinconvert.entity.ChartRequestBody;
import com.hash.coinconvert.entity.KLineResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface KLineApi {
    //http://46.51.243.83/coinconvert/charts
    @Headers("Auth-Token: 2y5iH44N87PKEYNHlyoqhTjIijAB6ACD")
    @POST("http://46.51.243.83/coinconvert/charts")
    Observable<AResponse<List<ChartItem>>> charts(@Body ChartRequestBody body);


    @GET("https://xcr.tratao.com/api/ver2/exchange/yahoo/history")
    Observable<KLineResponse> kLineData(@Query("range") String range, @Query("currency") String currency);
}
