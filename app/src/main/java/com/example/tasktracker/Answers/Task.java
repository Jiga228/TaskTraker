package com.example.tasktracker.Answers;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "task_room")
public class Task implements Serializable {
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    private long ID;

    @SerializedName("taskName")
    private String TaskName;


    @SerializedName("taskDescription")
    private String TaskDescription;

    @SerializedName("taskDate")
    private String TaskDate;

    public Task() {

    }

    @Ignore
    public Task(long ID, String TaskName, String TaskDescription, String TaskDate) {
        this.TaskName = TaskName;
        this.TaskDescription = TaskDescription;
        this.TaskDate = TaskDate;
        this.ID = ID;
    }

    public String getTaskName() {
        return TaskName;
    }

    public String getTaskDescription() {
        return TaskDescription;
    }

    public String getTaskDate() {
        return TaskDate;
    }

    public long getID() {
        return ID;
    }

    public void setTaskName(String taskName) {
        TaskName = taskName;
    }

    public void setTaskDescription(String taskDescription) {
        TaskDescription = taskDescription;
    }

    public void setTaskDate(String taskDate) {
        TaskDate = taskDate;
    }

    public void setID(long id) {
        ID = id;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return ID == task.ID && Objects.equals(TaskName, task.TaskName) && Objects.equals(TaskDescription, task.TaskDescription) && Objects.equals(TaskDate, task.TaskDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, TaskName, TaskDescription, TaskDate);
    }
}
