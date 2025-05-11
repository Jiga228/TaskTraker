package com.example.tasktracker.Api;

import com.example.tasktracker.Answers.LoginAnswer;
import com.example.tasktracker.Answers.Task;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @GET("singin")
    Single<LoginAnswer> singin(@Query("login") String login, @Query("password") String password);

    @POST("singup")
    Single<LoginAnswer> singup(@Query("login") String login, @Query("password") String password);

    @GET("user/task")
    Single<ArrayList<Task>> getTaskList(@Query("token") String token);

    @POST("user/task/add")
    Call<Void> addTask(@Query("token") String token, @Body Task task);

    @POST("user/task/remove")
    Call<Void> removeTask(@Query("token") String token, @Query("id") long ID);
}
