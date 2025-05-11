package com.example.tasktracker.Answers;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginAnswer implements Serializable {
    @SerializedName("status")
    private String status;

    @SerializedName("token")
    private String token;

    public String getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }
}
