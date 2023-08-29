package com.rate.quiz.repository;

import android.text.TextUtils;

import com.duxl.baselib.utils.SPUtils;
import com.rate.quiz.entity.User;
import com.rate.quiz.entity.TResponse;
import com.rate.quiz.http.api.ToolApi;
import com.rate.quiz.utils.GsonHelper;

import retrofit2.Call;

public class UserRepository {
    private static final String KEY = "user";
    public User getFromLocal(){
        User info = null;
        String s = SPUtils.getInstance().getString(KEY);
        if(!TextUtils.isEmpty(s)){
            try{
                info = GsonHelper.fromJsonString(s, User.class);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return info;
    }

    public void save(User info){
        SPUtils.getInstance().put(KEY,GsonHelper.toJsonString(info));
    }

    public Call<TResponse<User>> fetch(ToolApi api){
        return api.userProfile();
    }
}
