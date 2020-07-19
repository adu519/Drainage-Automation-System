package com.solution.robotcleaner.model;

public class TaskModel {
    private String task, dateTime, progress, area;

    public TaskModel() {
    }

    public TaskModel(String task, String dateTime, String progress, String area) {
        this.task = task;
        this.dateTime = dateTime;
        this.progress = progress;
        this.area = area;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
