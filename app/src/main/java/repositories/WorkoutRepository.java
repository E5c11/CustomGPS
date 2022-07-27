package repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import daos.WorkoutDao;
import databases.LocationDatabase;
import entities.Workout;

public class WorkoutRepository {

    private final WorkoutDao workoutDao;
    private final LiveData<List<Workout>> workouts;
    private final LiveData<Workout> lastWorkout;
    private final ExecutorService service;

    public WorkoutRepository(Application application) {
        LocationDatabase locationDatabase = LocationDatabase.getInstance(application);
        workoutDao = locationDatabase.workoutDao();
        workouts = workoutDao.getWorkouts();
        lastWorkout = workoutDao.getLastWorkout();
        service = Executors.newFixedThreadPool(2);
    }

    public void insert (Workout workout) {
        service.submit(() -> workoutDao.insert(workout));
    }

    public void update (Workout workout) {
        service.submit(() -> workoutDao.update(workout));
    }

    public void delete (Workout workout) {
        service.submit(() -> workoutDao.delete(workout));
    }

    public LiveData<Workout> getLastWorkout() {return lastWorkout;}

    public LiveData<Workout> getWorkout(String startTime) {return workoutDao.getWorkout(startTime);}

    public LiveData<List<Workout>> getWorkouts() {return workouts;}
}
