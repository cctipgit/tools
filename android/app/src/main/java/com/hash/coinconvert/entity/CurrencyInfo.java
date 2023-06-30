package com.hash.coinconvert.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class CurrencyInfo implements Parcelable , Comparable{

    public String token;
    public String icon;
    public String name;
    public String currencyType;
    public String unitName;
    public String countryCode;
    public String fChat;
    public String price;

    public CurrencyInfo() {
    }

    protected CurrencyInfo(Parcel in) {
        token = in.readString();
        icon = in.readString();
        name = in.readString();
        currencyType = in.readString();
        unitName = in.readString();
        countryCode = in.readString();
        fChat = in.readString();
        price = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(token);
        dest.writeString(icon);
        dest.writeString(name);
        dest.writeString(currencyType);
        dest.writeString(unitName);
        dest.writeString(countryCode);
        dest.writeString(fChat);
        dest.writeString(price);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CurrencyInfo> CREATOR = new Creator<CurrencyInfo>() {
        @Override
        public CurrencyInfo createFromParcel(Parcel in) {
            return new CurrencyInfo(in);
        }

        @Override
        public CurrencyInfo[] newArray(int size) {
            return new CurrencyInfo[size];
        }
    };

    @NonNull
    @Override
    public CurrencyInfo clone() {
        CurrencyInfo currency = new CurrencyInfo();
        currency.token = this.token;
        currency.icon = this.icon;
        currency.name = this.name;
        currency.currencyType = this.currencyType;
        currency.unitName = this.unitName;
        currency.countryCode = this.countryCode;
        currency.fChat = this.fChat;
        currency.price = this.price;
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyInfo that = (CurrencyInfo) o;
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, icon, name, currencyType, unitName, countryCode, fChat, price);
    }

    @Override
    public int compareTo(Object o) {
        if(o == null || !(o instanceof CurrencyInfo)) {
            return 0;
        }
        CurrencyInfo other = (CurrencyInfo)o;

        char[] chars1 = this.name.toCharArray();
        char[] chars2 = other.name.toCharArray();
        if(this.name.equals(other.name)) {
            return 0;
        } else {
            int len = chars1.length > chars2.length ? chars1.length : chars2.length;
            for(int i=0; i<len; i++) {
                if(i >= chars1.length) {
                    return -1;
                }

                if(i >= chars2.length) {
                    return 1;
                }

                if(chars1[i] > chars2[i]) {
                    return 1;
                }

                if(chars1[i] < chars2[i]) {
                    return -1;
                }
            }
        }
        return 0;
    }
}
