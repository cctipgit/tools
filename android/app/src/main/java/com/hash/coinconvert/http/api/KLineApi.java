package com.hash.coinconvert.http.api;

import com.hash.coinconvert.entity.AResponse;
import com.hash.coinconvert.entity.ChartItem;
import com.hash.coinconvert.entity.ChartRequestBody;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface KLineApi {
    //http://46.51.243.83/coinconvert/charts
    @POST("/coinconvert/charts")
    Observable<AResponse<List<ChartItem>>> charts(@Body ChartRequestBody body);
}
