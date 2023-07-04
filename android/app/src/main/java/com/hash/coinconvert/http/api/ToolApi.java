package com.hash.coinconvert.http.api;

import com.google.gson.JsonObject;
import com.hash.coinconvert.entity.PageRequest;
import com.hash.coinconvert.entity.PinCheckResponse;
import com.hash.coinconvert.entity.PinList;
import com.hash.coinconvert.entity.PinNum;
import com.hash.coinconvert.entity.RedeemHistoryList;
import com.hash.coinconvert.entity.RedeemList;
import com.hash.coinconvert.entity.RedeemPointList;
import com.hash.coinconvert.entity.RedeemRequest;
import com.hash.coinconvert.entity.User;
import com.hash.coinconvert.entity.TResponse;
import com.hash.coinconvert.entity.TaskCheckRequest;
import com.hash.coinconvert.entity.TaskList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ToolApi {
    @POST("/rate/tool/user/profile")
    Call<TResponse<User>> userProfile();

    @POST("/rate/tool/pin/check")
    Call<TResponse<PinCheckResponse>> pinCheck();

    @POST("/rate/tool/pin/list")
    Call<TResponse<PinList>> pinList();

    @POST("/rate/tool/redeem/list")
    Call<TResponse<RedeemList>> redeemList();
    @POST("/rate/tool/redeem/list")
    Call<TResponse<JsonObject>> redeemListAsString();

    @POST("/rate/tool/redeem/history")
    Call<TResponse<RedeemHistoryList>> redeemHistory(@Body PageRequest body);

    @POST("/rate/tool/redeem/point/list")
    Call<TResponse<RedeemPointList>> redeemPointList(@Body PageRequest body);

    @POST("/rate/tool/redeem/redeem")
    Call<TResponse<String>> redeem(@Body RedeemRequest body);

    @POST("/rate/tool/task/list")
    Call<TResponse<TaskList>> taskList();

    @POST("/rate/tool/task/check")
    Call<TResponse<PinNum>> taskCheck(@Body TaskCheckRequest body);

}
