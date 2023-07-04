package com.hash.coinconvert.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class PinItem implements Parcelable {
    @SerializedName("pin_id")
    public String id;
    @SerializedName("pin_pic")
    public String pic;
    @SerializedName("pin_reward")
    public String reward;
    @SerializedName("pin_reward_desc")
    public String desc;

    public transient int picResId;

    public PinItem(){
        reward = "80 PTS";
    }

    public PinItem(String id, String reward) {
        this.id = id;
        this.reward = reward;
    }

    protected PinItem(Parcel in) {
        id = in.readString();
        pic = in.readString();
        reward = in.readString();
        desc = in.readString();
        picResId = in.readInt();
    }

    public static final Creator<PinItem> CREATOR = new Creator<PinItem>() {
        @Override
        public PinItem createFromParcel(Parcel in) {
            return new PinItem(in);
        }

        @Override
        public PinItem[] newArray(int size) {
            return new PinItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(pic);
        dest.writeString(reward);
        dest.writeString(desc);
        dest.writeInt(picResId);
    }
}
