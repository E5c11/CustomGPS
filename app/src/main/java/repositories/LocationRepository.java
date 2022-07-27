package repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import daos.LocationDao;
import databases.LocationDatabase;
import entities.LocationNow;

public class LocationRepository {

    private final LocationDao locationDao;
    private final LiveData<LocationNow> currentLocation;
    private final ExecutorService service;

    public LocationRepository(Application application) {
        LocationDatabase database = LocationDatabase.getInstance(application);
        locationDao = database.locationDao();
        currentLocation = locationDao.getLocation();
        service = Executors.newFixedThreadPool(2);
    }

    public void insert (LocationNow locationNow) {
        service.submit(() -> locationDao.insert(locationNow));
    }

    public void update (LocationNow locationNow) {
        service.submit(() -> locationDao.update(locationNow));
    }

    public void delete (LocationNow locationNow) {
        service.submit(() -> locationDao.delete(locationNow));
    }

    public void clearWorkout() {
        service.submit(locationDao::clearTable);
    }

    public LiveData<LocationNow> getCurrentLocation() {return currentLocation;}
}
