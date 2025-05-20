package com.example.tasktracker.Answers;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginAnswer implements Serializable {
    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }
}
