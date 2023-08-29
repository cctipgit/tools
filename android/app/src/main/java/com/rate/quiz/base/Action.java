package com.rate.quiz.base;

public interface Action<T> {
    void invoke(T t);
}
