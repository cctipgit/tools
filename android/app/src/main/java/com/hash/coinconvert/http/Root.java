package com.hash.coinconvert.http;

import com.duxl.baselib.http.BaseRoot;

public class Root<T> implements BaseRoot {

    public int code;

    public String message;

    public T data;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return message;
    }

    @Override
    public boolean isSuccess() {
        return code == 0;
    }
}
