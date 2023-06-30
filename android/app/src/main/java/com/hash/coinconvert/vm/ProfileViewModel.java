package com.hash.coinconvert.vm;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.duxl.baselib.utils.SPUtils;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.R;
import com.hash.coinconvert.base.Action;
import com.hash.coinconvert.base.BaseViewModel;
import com.hash.coinconvert.entity.ServerUserInfo;
import com.hash.coinconvert.entity.UserInfo;
import com.hash.coinconvert.http.RetrofitHelper;
import com.hash.coinconvert.http.api.ToolApi;
import com.hash.coinconvert.utils.GsonHelper;

import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class ProfileViewModel extends BaseViewModel {

    private static final String[] DIC = new String[]{
            "Sandra", "Sukey", "Leo", "Boris", "Neko", "Mao", "Lee", "Mayday", "Matt", "Tony", "Henry", "Snail",
            "Vivi", "Kitty", "Sounder", "Milly", "Caesar", "Jack", "Sina", "Faye", "Jade", "Long", "Johnny",
            "Flappy", "Mark", "Ken", "Zeke", "Vida", "Pend", "Gavin", "Cloud", "Nemo", "Lucas", "Lucas", "Maybe",
            "James", "Object", "Benson", "Bruin", "Lisa", "Shirley", "Ben", "Jingle", "Eric", "Jase", "Nellie",
            "Cathy", "Max", "Lin"
    };

    public static final int[] AVATARS = new int[]{R.mipmap.avator01, R.mipmap.avator02, R.mipmap.avator03, R.mipmap.avator04, R.mipmap.avator05, R.mipmap.avator06, R.mipmap.avator07, R.mipmap.avator08,};

    private MutableLiveData<UserInfo> userInfo = new MutableLiveData<>();

    public LiveData<UserInfo> getUserInfo() {
        return userInfo;
    }

    private ToolApi api;

    public ProfileViewModel() {
        api = RetrofitHelper.create(ToolApi.class);
    }

    public void fetchUserInfo() {
//        String json = SPUtils.getInstance().getString(Constants.SP.KEY.USER_INFO);
//        UserInfo info;
//        if (TextUtils.isEmpty(json)) {
//            info = randomUser();
//            SPUtils.getInstance().put(Constants.SP.KEY.USER_INFO, GsonHelper.toJsonString(info));
//        } else {
//            info = GsonHelper.fromJsonString(json, UserInfo.class);
//        }
//        userInfo.postValue(info);
        fetchUserFromServer();
    }

    public void fetchUserFromServer() {
        execute(api.userProfile(), new Action<ServerUserInfo>() {
            @Override
            public void invoke(ServerUserInfo serverUserInfo) {
                UserInfo info = UserInfo.getUserInfo();
                info.name = serverUserInfo.userName;
                info.avatarIndex = Math.abs(serverUserInfo.avatar.hashCode()) % AVATARS.length;
                userInfo.postValue(info);
            }
        });
    }

    private UserInfo randomUser() {
        Calendar calendar = Calendar.getInstance();
        int second = calendar.get(Calendar.SECOND);
        int mils = calendar.get(Calendar.MILLISECOND);
        int seed = second * mils;
        int nameIndex = seed % DIC.length;
        int avatarIndex = seed % AVATARS.length;
        UserInfo userInfo = new UserInfo();
        userInfo.avatarIndex = avatarIndex;
        userInfo.name = DIC[nameIndex];
        userInfo.id = UUID.randomUUID().toString().toLowerCase(Locale.ROOT);
        userInfo.createTime = calendar.getTimeInMillis();
        return userInfo;
    }
}
