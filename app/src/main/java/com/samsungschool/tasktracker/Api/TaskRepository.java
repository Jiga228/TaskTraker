package com.samsungschool.tasktracker.Api;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.samsungschool.tasktracker.Answers.Task;

@Database(entities = {Task.class}, version = 1)
public abstract class TaskRepository extends RoomDatabase {
    private static final String DATABASE_NAME = "task.db";
    private static TaskRepository instance = null;

    public static TaskRepository newInstance(Application app) {
        if(instance == null) {
            instance = Room.databaseBuilder(app,
                                            TaskRepository.class,
                                            DATABASE_NAME).build();
        }
        return instance;
    }

    public abstract TaskDao taskDao();
}
