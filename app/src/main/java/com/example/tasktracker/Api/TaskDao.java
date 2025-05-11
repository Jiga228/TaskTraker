package com.example.tasktracker.Api;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.tasktracker.Answers.Task;

import java.util.List;

import retrofit2.Call;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM task_room")
    LiveData<List<Task>> getTaskList();

    @Query("DELETE FROM task_room")
    void DropTable();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addTask(Task... task);

    @Delete
    void delete(Task task);
}
