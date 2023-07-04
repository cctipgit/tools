package com.hash.coinconvert.repository;

import android.text.TextUtils;

import com.duxl.baselib.utils.SPUtils;
import com.hash.coinconvert.entity.User;
import com.hash.coinconvert.entity.TResponse;
import com.hash.coinconvert.http.api.ToolApi;
import com.hash.coinconvert.utils.GsonHelper;

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
