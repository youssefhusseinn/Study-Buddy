package com.example.studybuddy;

public class MyTask {
    private String titleTask, dateTask, descTask, keyTask;
    public MyTask() {}
    public MyTask(String titleTask, String dateTask, String descTask, String keyTask) {
        this.titleTask = titleTask;
        this.dateTask = dateTask;
        this.descTask = descTask;
        this.keyTask = keyTask;
    }
    public String getKeyTask() {
        return keyTask;
    }
    public void setKeyTask(String keyTask) {
        this.keyTask = keyTask;
    }
    public String getTitleTask() {
        return titleTask;
    }
    public void setTitleTask(String titleTask) {
        this.titleTask = titleTask;
    }
    public String getDateTask() {
        return dateTask;
    }
    public void setDateTask(String dateTask) {
        this.dateTask = dateTask;
    }
    public String getDescTask() {
        return descTask;
    }
    public void setDescTask(String descTask) {
        this.descTask = descTask;
    }
}
