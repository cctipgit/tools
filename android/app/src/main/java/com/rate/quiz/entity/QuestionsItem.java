package com.rate.quiz.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionsItem {
    @SerializedName("son_question")
    public List<QuestionItem> questions;
    public String title;
}
