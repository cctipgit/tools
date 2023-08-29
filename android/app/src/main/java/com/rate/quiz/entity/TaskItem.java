package com.rate.quiz.entity;

import com.google.gson.annotations.SerializedName;

public class TaskItem {
    public boolean done;
    public String params;
    @SerializedName("spin_times")
    public int spinTimes;
    @SerializedName("task_id")
    public String id;
    @SerializedName("task_name")
    public String name;
    @SerializedName("task_type")
    public String type;

    public int getRequiredTimes() {
        /*
        ['sign in', 'share_to_facebook', 'share_to_twitter', 'share_to_telegram',
        'share_to_discord', 'share_to_1_register', 'share_to_3_register', 'share_to_5_register',
        'share_add_token_1', 'share_add_token_3', 'view_token_1', 'view_token_3', 'quiz_done',
        'visit_website', 'product_exchange', 'product_get_during_10_12', 'product_get_during_14_16',
        'product_get_app_star']
         */
        switch (type) {
            case "share_to_1_register":
            case "share_add_token_1":
            case "view_token_1":
                return 1;
            case "share_to_3_register":
            case "share_add_token_3":
            case "view_token_3":
                return 3;
            case "share_to_5_register":
                return 5;
        }
        return 0;
    }
}
