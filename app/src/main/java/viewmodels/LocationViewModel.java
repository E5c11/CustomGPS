package viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.esc.test.customgps.R;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.text.ParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import adaptersandmore.ResourceProvider;
import calculations.WorkoutInfo;
import entities.LocationNow;
import entities.Workout;
import repositories.LocationRepository;
import repositories.WorkoutRepository;

public class LocationViewModel extends AndroidViewModel {

    private final LocationRepository locationRepository;
    private final WorkoutRepository workoutRepository;
    private final LiveData<LocationNow> locationDetails;
    private final LiveData<Workout> workout;
    public MutableLiveData<Double> distance = new MutableLiveData<>();
    public MutableLiveData<String> avgSpeed = new MutableLiveData<>(), time = new MutableLiveData<>(), alt = new MutableLiveData<>(), speed = new MutableLiveData<>();
    public MutableLiveData<String> runningState = new MutableLiveData<>();
    public LiveData<Boolean> setObservers = new MutableLiveData<>();
    private String startTime, previousTime, newTime, oldTime, elapsedTime;
    private double oldLat, oldLng, oldEle, oldDistance = 0;
    private double currentDistance;
    private final ResourceProvider rp;
    private final ExecutorService service;
    private static final String TAG = "myT";

    public LocationViewModel(@NonNull Application application) {
        super(application);
        locationRepository = new LocationRepository(application);
        workoutRepository = new WorkoutRepository(application);
        locationDetails = locationRepository.getCurrentLocation();
        rp = new ResourceProvider(application);
        service = Executors.newSingleThreadExecutor();
        previousTime = "00:00:00";
        workout = workoutRepository.getLastWorkout();
    }

    public void insert(LocationNow locationNow) {
        locationRepository.insert(locationNow);
    }

    public void update(LocationNow locationNow) {
        locationRepository.update(locationNow);
    }

    public void delete(LocationNow locationNow) {
        locationRepository.delete(locationNow);
    }

    public LiveData<LocationNow> getCurrentLocation() {
        return locationDetails;
    }

    public void updateMapPath() {

    }

    public void getWorkoutInfo(LocationNow location) {
        //Log.d(TAG, "Locale: " + Locale.getDefault().getCountry());
        service.submit(() -> {if (startTime == null) startTime = location.getTime();});
        service.submit(() -> {newTime = location.getTime();});
        service.submit(() -> {alt.postValue(WorkoutInfo.formatTime(location.getAltitude()));});
        service.submit(() -> elapsedTime = WorkoutInfo.addTime(previousTime, WorkoutInfo.getTime(startTime, newTime)));
        service.submit(() -> {time.postValue(elapsedTime);});
        service.submit(() -> {currentDistance = WorkoutInfo.getDistance(location.getLocation().getLatitude(), oldLat, location.getLocation().getLongitude(), oldLng, location.getAltitude(), oldEle, oldDistance);});
        //Log.d(TAG, "getWorkoutInfo: " + currentDistance);
        service.submit(() -> {distance.postValue(currentDistance);});
        service.submit(() -> {
            try {
                speed.postValue(WorkoutInfo.getSpeed(currentDistance, oldDistance, newTime, oldTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        service.submit(() -> {
            try {
                //Log.d(TAG, "getavgSpeed: " + elapsedTime);
                avgSpeed.postValue(WorkoutInfo.getAvgSpeed(currentDistance, elapsedTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        service.submit(() -> {
            setOldValues(newTime, currentDistance, location.getLocation().getLatitude(), location.getLocation().getLongitude(), location.getAltitude());
        });
        //service.shutdown();
    }

    public void reopenedWorkout(String activeWorkout) {
        if (activeWorkout != null) {
            runningState.setValue(activeWorkout);
            setObservers = Transformations.map(workout, s-> {
                if (s != null) {
                    oldDistance = s.getDistance_km();
                    distance.setValue(oldDistance);
                    oldTime = WorkoutInfo.convertTimeToString((long) s.getDuration_s());
                    previousTime = oldTime;
                    time.setValue(oldTime);
                    Log.d(TAG, "reopenedWorkout: " + oldTime + " " + oldDistance);
                    return true;
                } else return false;
            });
        }
    }

    public void pauseWorkout() {
        previousTime = elapsedTime;
        Log.d(TAG, "pauseWorkout: " + previousTime);
    }

    public void resumeWorkout() {
        startTime = WorkoutInfo.getDate();
    }

    private void setOldValues(String time, double ... old) {
        oldTime = time;
        oldDistance = old[0];
        oldLat = old[1];
        oldLng = old[2];
        oldEle = old[3];
    }
    public void clearWorkout() {
        locationRepository.clearWorkout();
        double clear = 0.0;
        startTime = null;
        setOldValues(null, clear, clear, clear, clear);
    }

    public MutableLiveData<String> getSpeed() {return speed;}
    public MutableLiveData<String> getTime() {return time;}
    public MutableLiveData<Double> getDistance() {return distance;}
    public MutableLiveData<String> getAvgSpeed() {return avgSpeed;}
    public MutableLiveData<String> getAlt() {return alt;}
    public MutableLiveData<String> getRunningState() {return runningState;}
    public LiveData<Boolean> getSetObservers() {return setObservers;}
}
