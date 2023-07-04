package com.hash.coinconvert.vm;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hash.coinconvert.R;
import com.hash.coinconvert.base.BaseViewModel;
import com.hash.coinconvert.entity.User;
import com.hash.coinconvert.http.RetrofitHelper;
import com.hash.coinconvert.http.api.ToolApi;
import com.hash.coinconvert.repository.UserRepository;

public class ProfileViewModel extends BaseViewModel {

    private static final String[] DIC = new String[]{
            "Sandra", "Sukey", "Leo", "Boris", "Neko", "Mao", "Lee", "Mayday", "Matt", "Tony", "Henry", "Snail",
            "Vivi", "Kitty", "Sounder", "Milly", "Caesar", "Jack", "Sina", "Faye", "Jade", "Long", "Johnny",
            "Flappy", "Mark", "Ken", "Zeke", "Vida", "Pend", "Gavin", "Cloud", "Nemo", "Lucas", "Lucas", "Maybe",
            "James", "Object", "Benson", "Bruin", "Lisa", "Shirley", "Ben", "Jingle", "Eric", "Jase", "Nellie",
            "Cathy", "Max", "Lin"
    };

    public static final int[] AVATARS = new int[]{R.mipmap.avator01, R.mipmap.avator02, R.mipmap.avator03, R.mipmap.avator04, R.mipmap.avator05, R.mipmap.avator06, R.mipmap.avator07, R.mipmap.avator08,};

    private MutableLiveData<User> userInfo = new MutableLiveData<>();
    private UserRepository repository;

    public LiveData<User> getUserInfo() {
        return userInfo;
    }

    private ToolApi api;

    public static int getAvatarResId(String uid) {
        return AVATARS[Math.abs(uid.hashCode()) % AVATARS.length];
    }

    public ProfileViewModel() {
        Log.d("ProfileViewModel", "newinstance");
        api = RetrofitHelper.create(ToolApi.class);
        repository = new UserRepository();
    }

    public void fetchUserInfo() {
        User local = repository.getFromLocal();
        if (local != null) {
            userInfo.postValue(local);
        }
        execute(repository.fetch(api), res -> {
            repository.save(res);
            userInfo.postValue(res);
        });
    }
}
