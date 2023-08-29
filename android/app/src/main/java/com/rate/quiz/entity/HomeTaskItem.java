package com.rate.quiz.entity;

public class HomeTaskItem {
    private String taskName;
    private int chance;

    public HomeTaskItem(String taskName, int chance) {
        this.taskName = taskName;
        this.chance = chance;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getChance() {
        return chance;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }
}