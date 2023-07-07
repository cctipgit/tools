package com.hash.coinconvert.entity;

import com.google.gson.annotations.SerializedName;
import com.hash.coinconvert.database.entity.Token;

public class RedeemItem {
    public String id;
    public String pic;
    @SerializedName("point_require")
    public long pointRequire;
    @SerializedName("reward")
    public String reward;
    public long total;
    public long left;
    public String symbol;

    public transient String currencyType = Token.TOKEN_TYPE_DIGITAL;
    public transient String unit = "$";

}
