package daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.Workout;

@Dao
public interface WorkoutDao {

    @Insert
    void insert(Workout workout);

    @Update
    void update(Workout workout);

    @Delete
    void delete(Workout workout);

    @Query("SELECT * FROM workout_table ORDER BY id DESC")
    LiveData<List<Workout>> getWorkouts();

    @Query("SELECT * FROM workout_table ORDER BY id DESC LIMIT 1")
    LiveData<Workout> getLastWorkout();

    @Query("SELECT * FROM workout_table WHERE start_time = :startTime ORDER BY id DESC LIMIT 1")
    LiveData<Workout> getWorkout(String startTime);
}
