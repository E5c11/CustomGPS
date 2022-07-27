package services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.esc.test.customgps.BaseActivity;
import com.esc.test.customgps.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;

import calculations.WorkoutInfo;
import calculations.Stopwatch;
import entities.LocationNow;
import entities.Workout;
import entities.location;
import repositories.LocationRepository;
import repositories.WorkoutRepository;

import static com.esc.test.customgps.AppClass.CHANNEL_ID;

public class ActivityService extends Service {

    private static final int notID = 1;
    private PowerManager.WakeLock wakeLock;
    private static final String TAG = "myT";
    private final Stopwatch stopwatch = new Stopwatch();
    private volatile String locationInactive;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private double distance, previousDistance, elevation, lat, lng;
    private LocationRepository locationRepository;
    private WorkoutRepository workoutRepository;
    private Handler handler;
    private DecimalFormat df;

    @Override
    public void onCreate() {
        locationRepository = new LocationRepository(getApplication());
        workoutRepository = new WorkoutRepository(getApplication());
    }

    private final BroadcastReceiver pauseBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Log.d(TAG, "onReceive: ");
            if (action != null) {
                if (action.equals(getResources().getString(R.string.pause))) pauseLocationUpdates();
                else if (action.equals(getResources().getString(R.string.resume))){
                    resumeLocationUpdates();
                } else {
                    workoutRepository.insert(new Workout(stopwatch.getMilliSeconds(), distance));
                    Log.d(TAG, "onReceive: " + stopwatch.getMilliSeconds() + " " +  distance);
                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String service = intent.getStringExtra(getResources().getString(R.string.service_msg));
        Log.d(TAG, "onHandleIntent: " + service);
        if (service.equals(getResources().getString(R.string.start_service))) {
            init();
            locationInactive = getResources().getString(R.string.pause);
            startForeground(1, getNotification("00:00:00"));
            registerBroadcast();
            startLocationUpdates();
        } else if (service.equals(getResources().getString(R.string.pause_service))) pauseLocationUpdates();
        else if (service.equals(getResources().getString(R.string.resume_service))) resumeLocationUpdates();
        else stopLocationUpdates();
        return START_NOT_STICKY;
    }

    private void init() {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);
        wakeLock = Objects.requireNonNull(pm).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationService:WakeLock");
    }

    private void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getResources().getString(R.string.pause));
        intentFilter.addAction(getResources().getString(R.string.resume));
        intentFilter.addAction(getResources().getString(R.string.restart_app));
        registerReceiver(pauseBroadcast, intentFilter);
    }

    private Notification getNotification(String activityInfo) {
        //Log.d(TAG, "getNotification: " + activityInfo);
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Tracking your activity")
                .setContentText(activityInfo)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(getOpenAppIntent())
                .addAction(R.drawable.ic_launcher_foreground, locationInactive, getPauseIntent())
                .build();
    }

    private PendingIntent getOpenAppIntent() {
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.active_workout), locationInactive);
        return new NavDeepLinkBuilder(this)
                .setComponentName(BaseActivity.class)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.workoutFragment)
                .setArguments(bundle)
                .createPendingIntent();
    }

    private PendingIntent getPauseIntent() {
        String action;
        if (locationInactive.equals(getResources().getString(R.string.pause))) action = getResources().getString(R.string.pause);
        else action = getResources().getString(R.string.resume);
        //Log.d(TAG, "getPendingIntent: " + action);
        return PendingIntent.getBroadcast(this, 0, new Intent(action), PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private void updateNotification() {
        Handler UIHandler = new Handler(Looper.getMainLooper());
        Runnable notificationRunnable = new Runnable() {
            @Override
            public void run() {
                Notification newNotification = getNotification(getTimeDistance(stopwatch.formatTime(), df.format(distance)));
                //Log.d(TAG, "run: " + "notification");
                ((NotificationManager) Objects.requireNonNull(getSystemService(Context.NOTIFICATION_SERVICE))).notify(notID, newNotification);
                if (locationInactive.equals(getResources().getString(R.string.pause))) handler.postDelayed(this, 500);
            }
        };
        UIHandler.post(notificationRunnable);
    }

    private String getTimeDistance(String time, String distance) {
        return "Duration " + time + " - Distance " + distance + "km";
    }

    @SuppressLint("WakelockTimeout")
    private void startLocationUpdates() {
        locationInactive = getResources().getString(R.string.pause);
        ActivityService.LocationRunnable runnable = new ActivityService.LocationRunnable();
        new Thread(runnable).start();
        wakeLock.acquire();
        stopwatch.start();
        Log.d(TAG, "start");
        updateNotification();
    }

    private void resumeLocationUpdates() {
        stopwatch.resume();
        locationInactive = getResources().getString(R.string.pause);
//        ActivityService.LocationRunnable runnable = new ActivityService.LocationRunnable();
//        new Thread(runnable).start();
        Log.d(TAG, "resume");
        updateNotification();
    }

    private void stopLocationUpdates() {
        LocationServices.getFusedLocationProviderClient(getApplication())
                .removeLocationUpdates(locationCallback);
        //locationInactive = true;
        stopwatch.stop();
        locationInactive = getResources().getString(R.string.resume);
        if (wakeLock.isHeld()) wakeLock.release();
        Log.d(TAG, "stop");
        //stopForeground(true);
        stopSelf();
    }

    private void pauseLocationUpdates() {
        stopwatch.pause();
        Log.d(TAG, "pause");
        locationInactive = getResources().getString(R.string.resume);
        updateNotification();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(500);
        //locationRequest.setSmallestDisplacement(10);
        //Log.d(TAG, "setLocationRequest: ");
    }

    class LocationRunnable implements Runnable {
        public LocationRunnable() {}

        @SuppressLint("MissingPermission")
        @Override
        public void run() {
            Looper.prepare();
            handler = new Handler();
            if (locationRequest == null) setLocationRequest();
            LocationServices.getFusedLocationProviderClient(getApplication())
                    .requestLocationUpdates(locationRequest, locationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            if (locationInactive.equals(getResources().getString(R.string.pause))) {
                                if (locationResult.getLocations().size() > 0) {
                                    if (!stopwatch.isRunning()) stopwatch.start();
                                    int latestLocationIndex = locationResult.getLocations().size() - 1;
                                    double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                                    double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                                    double altitude = locationResult.getLocations().get(latestLocationIndex).getAltitude();
                                    LocationNow ld = new LocationNow(new location(latitude, longitude), altitude, WorkoutInfo.getDate());
                                    //String temp = String.format("Latitude: %s\nLongitude: %s", latitude, longitude);
                                    if (latitude != 0.0) {
                                        //Log.d(TAG, "latitude is: " + latitude);
                                        calcDistance(latitude, longitude, altitude);
                                        locationRepository.insert(ld);
                                    }
                                    //Log.d("myT", "Location is: " + temp + " elevation: " + altitude + "m");
                                }
                            }
                        }
                    }, handler.getLooper());
            Looper.loop();
        }
    }

    public void calcDistance(double newLat, double newLng1, double newEl) {
        if (lat != 0.0) {
            final int R = 6371; // Radius of the earth
            double latDistance = Math.toRadians(newLat - lat);
            double lonDistance = Math.toRadians(newLng1 - lng);
            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(newLat))
                    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double d = R * c * 1000; //m

            double height = elevation - newEl;

            d = (Math.pow(d, 2) + Math.pow(height, 2));

            distance = ((Math.sqrt(d) / 1000) + previousDistance);
        }
        //Log.d(TAG, "Distance: " + distance + " previous: " + previousDistance);
        lat = newLat;
        lng = newLng1;
        elevation = newEl;
        previousDistance = distance;
        //calcSpeed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service onDestroy");
        unregisterReceiver(pauseBroadcast);
        if (wakeLock.isHeld()) wakeLock.release();
    }

}
