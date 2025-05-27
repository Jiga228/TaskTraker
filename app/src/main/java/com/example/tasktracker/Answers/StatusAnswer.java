package com.example.tasktracker.Answers;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StatusAnswer implements Serializable {
    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }
}
