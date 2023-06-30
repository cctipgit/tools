package com.hash.coinconvert.entity;

import android.text.TextUtils;

import androidx.annotation.IntRange;

import com.duxl.baselib.utils.SPUtils;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.R;
import com.hash.coinconvert.utils.GsonHelper;

import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class UserInfo {
    private static final String[] DIC = new String[]{
            "Sandra", "Sukey", "Leo", "Boris", "Neko", "Mao", "Lee", "Mayday", "Matt", "Tony", "Henry", "Snail",
            "Vivi", "Kitty", "Sounder", "Milly", "Caesar", "Jack", "Sina", "Faye", "Jade", "Long", "Johnny",
            "Flappy", "Mark", "Ken", "Zeke", "Vida", "Pend", "Gavin", "Cloud", "Nemo", "Lucas", "Lucas", "Maybe",
            "James", "Object", "Benson", "Bruin", "Lisa", "Shirley", "Ben", "Jingle", "Eric", "Jase", "Nellie",
            "Cathy", "Max", "Lin"
    };

    public static final int[] AVATARS = new int[]{R.mipmap.avator01, R.mipmap.avator02, R.mipmap.avator03, R.mipmap.avator04, R.mipmap.avator05, R.mipmap.avator06, R.mipmap.avator07, R.mipmap.avator08,};


    public String name;
    public String id;
    @IntRange(from = 0, to = 7)
    public int avatarIndex;
    public long createTime;


    public static UserInfo randomUser() {
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

    public static UserInfo getUserInfo() {
        String json = SPUtils.getInstance().getString(Constants.SP.KEY.USER_INFO);
        UserInfo info;
        if (TextUtils.isEmpty(json)) {
            info = randomUser();
            SPUtils.getInstance().put(Constants.SP.KEY.USER_INFO, GsonHelper.toJsonString(info));
        } else {
            info = GsonHelper.fromJsonString(json, UserInfo.class);
        }
        return info;
    }
}
