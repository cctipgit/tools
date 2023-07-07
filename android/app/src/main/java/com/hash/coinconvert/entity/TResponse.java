package com.hash.coinconvert.entity;

public class TResponse<T> {

    public static final int SUCCESS = 10000;
    public static final int EMPTY_BODY = -1;

    public int code;
    public String msg;
    public T data;

    public boolean isSuccess() {
        return code == SUCCESS;
    }
}
