package com.rate.quiz.entity;

import java.util.List;

public class AnswerBody {
    public String id;
    public List<Integer> options;

    public AnswerBody() {
    }

    public AnswerBody(String id, List<Integer> options) {
        this.id = id;
        this.options = options;
    }
}
