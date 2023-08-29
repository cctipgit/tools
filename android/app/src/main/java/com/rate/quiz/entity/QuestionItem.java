package com.rate.quiz.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionItem {
    public String title;
    public String id;
    public List<String> options;
    @SerializedName("q_type")
    public int qType;
}
