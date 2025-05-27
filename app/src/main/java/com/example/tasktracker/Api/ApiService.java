package com.example.tasktracker.Api;

import com.example.tasktracker.Answers.StatusAnswer;
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
    Single<StatusAnswer> singin(@Query("login") String login, @Query("password") String password);

    @POST("singup")
    Single<StatusAnswer> singup(@Query("login") String login, @Query("password") String password);

    @GET("user/task")
    Single<Task> getTask(@Query("login") String login, @Query("password") String password, @Query("id") long id);

    @GET("user/task/list")
    Single<ArrayList<Task>> getTaskList(@Query("login") String login, @Query("password") String password);

    @POST("user/task/add")
    Call<Void> addTask(@Query("login") String login, @Query("password") String password, @Body Task task);

    @POST("user/task/edit")
    Call<Void> editTask(@Query("login") String login, @Query("password") String password, @Body Task task);

    @POST("user/task/remove")
    Call<Void> removeTask(@Query("login") String login, @Query("password") String password, @Query("id") long ID);

    @POST("user/ChangePassword")
    Single<StatusAnswer> changePassword(@Query("oldPassword") String oldPassword, @Query("login") String login, @Query("newPassword") String newPassword);
}
