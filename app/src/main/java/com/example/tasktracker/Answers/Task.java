package com.example.tasktracker.Answers;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;

@Entity(tableName = "task_room")
public class Task {

    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    private long id;
    @SerializedName("taskDate")
    private String taskDate;
    @SerializedName("taskName")
    private String taskName;
    @SerializedName("taskDescription")
    private String taskDescription;
    @SerializedName("taskTime")
    private String taskTime;

    public Task(long id, String taskDate, String taskName, String taskDescription, String taskTime) {
        this.id = id;
        this.taskDate = taskDate;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskTime = taskTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

}
