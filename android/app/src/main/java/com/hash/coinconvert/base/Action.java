package com.hash.coinconvert.base;

public interface Action<T> {
    void invoke(T t);
}
