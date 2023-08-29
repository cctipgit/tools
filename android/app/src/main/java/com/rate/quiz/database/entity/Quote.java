package com.rate.quiz.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tb_quote")
public class Quote {
    @PrimaryKey
    private int _id;
    @NonNull
    private String mid;
    private String cmd;
    private String amount;
    private String priceTime;
    private String tokenFrom;
    private String tokenTo;
    private String dateUnit;
    private String data;
    private String priceFrom;
    private String childPriceTo;
    private String childPriceFrom;
    private String childPriceDate;
    private String priceTo;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    @NonNull
    public String getMid() {
        return mid;
    }

    public void setMid(@NonNull String mid) {
        this.mid = mid;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPriceTime() {
        return priceTime;
    }

    public void setPriceTime(String priceTime) {
        this.priceTime = priceTime;
    }

    public String getTokenFrom() {
        return tokenFrom;
    }

    public void setTokenFrom(String tokenFrom) {
        this.tokenFrom = tokenFrom;
    }

    public String getTokenTo() {
        return tokenTo;
    }

    public void setTokenTo(String tokenTo) {
        this.tokenTo = tokenTo;
    }

    public String getDateUnit() {
        return dateUnit;
    }

    public void setDateUnit(String dateUnit) {
        this.dateUnit = dateUnit;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(String priceFrom) {
        this.priceFrom = priceFrom;
    }

    public String getChildPriceTo() {
        return childPriceTo;
    }

    public void setChildPriceTo(String childPriceTo) {
        this.childPriceTo = childPriceTo;
    }

    public String getChildPriceFrom() {
        return childPriceFrom;
    }

    public void setChildPriceFrom(String childPriceFrom) {
        this.childPriceFrom = childPriceFrom;
    }

    public String getChildPriceDate() {
        return childPriceDate;
    }

    public void setChildPriceDate(String childPriceDate) {
        this.childPriceDate = childPriceDate;
    }

    public String getPriceTo() {
        return priceTo;
    }

    public void setPriceTo(String priceTo) {
        this.priceTo = priceTo;
    }
}
