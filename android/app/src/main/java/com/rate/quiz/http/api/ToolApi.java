package com.rate.quiz.http.api;

import com.rate.quiz.entity.AnswersBody;
import com.rate.quiz.entity.PageRequest;
import com.rate.quiz.entity.PinCheckResponse;
import com.rate.quiz.entity.PinList;
import com.rate.quiz.entity.PinNum;
import com.rate.quiz.entity.QuestionList;
import com.rate.quiz.entity.RedeemHistoryList;
import com.rate.quiz.entity.RedeemList;
import com.rate.quiz.entity.RedeemPointList;
import com.rate.quiz.entity.RedeemRequest;
import com.rate.quiz.entity.User;
import com.rate.quiz.entity.TResponse;
import com.rate.quiz.entity.TaskCheckRequest;
import com.rate.quiz.entity.TaskList;

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

    @POST("/rate/tool/redeem/history")
    Call<TResponse<RedeemHistoryList>> redeemHistory(@Body PageRequest body);

    @POST("/rate/tool/redeem/point/list")
    Call<TResponse<RedeemPointList>> redeemPointList(@Body PageRequest body);

    @POST("/rate/tool/redeem/redeem")
    Call<TResponse<Object>> redeem(@Body RedeemRequest body);

    @POST("/rate/tool/task/list")
    Call<TResponse<TaskList>> taskList();

    @POST("/rate/tool/task/check")
    Call<TResponse<PinNum>> taskCheck(@Body TaskCheckRequest body);

    @POST("/rate/tool/quiz/questions")
    Call<TResponse<QuestionList>> questionList();

    @POST("/rate/tool/quiz/submit_answer")
    Call<TResponse<Object>> submitAnswer(@Body AnswersBody body);

}
