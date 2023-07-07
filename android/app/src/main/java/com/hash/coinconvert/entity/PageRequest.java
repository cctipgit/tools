package com.hash.coinconvert.entity;

public class PageRequest {
    int page;

    public static PageRequest of(int page) {
        PageRequest request = new PageRequest();
        request.page = page;
        return request;
    }
}
