package com.hash.coinconvert.entity;

public class KLineEntity {
    public String price;
    public String time;
    public String priceTime;

    public String getPriceTime() {
        return priceTime;
    }

    public void setPriceTime(String priceTime) {
        this.priceTime = priceTime;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
