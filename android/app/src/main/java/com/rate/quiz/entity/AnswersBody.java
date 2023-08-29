package com.rate.quiz.entity;

import java.util.ArrayList;
import java.util.List;

public class AnswersBody {
    public List<AnswerBody> answers;

    public AnswersBody(List<AnswerBody> answers){
        this.answers = answers;
    }

    public void add(List<AnswerBody> list){
        if(answers == null){
            answers = new ArrayList<>();
        }
        answers.addAll(list);
    }
}
