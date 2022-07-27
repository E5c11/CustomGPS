package services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.esc.test.customgps.R;

import java.time.format.DateTimeFormatter;

import fragments.WorkoutFragment;
import adaptersandmore.ShareVariables;
import calculations.Stopwatch;

import static com.esc.test.customgps.AppClass.CHANNEL_ID;

public class LocationService extends IntentService {

    private PendingIntent pendingIntent;
    private int notID = 1;
    private PowerManager.WakeLock wakeLock;
    private static final String TAG = "myT";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private ShareVariables sv;
    private Stopwatch stopwatch = new Stopwatch();

    public LocationService() {
        super("LocationService");
        setIntentRedelivery(false);

    }

    @SuppressLint("WakelockTimeout")
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationService:WakeLock");
        wakeLock.acquire();
        boolean service = intent.getBooleanExtra("start_service", false);
        Log.d(TAG, "onHandleIntent: " + service);
        if (service) {
            Intent notificationIntent = new Intent(this, WorkoutFragment.class);
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForeground(notID, getNotification("00:00:00"));
            updateNotification();
        } else {
            stopwatch.stop();
            wakeLock.release();
        }
    }

    private Notification getNotification(String activityInfo) {
        Log.d(TAG, "getNotification: " + activityInfo);
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Tracking your activity")
                .setContentText(activityInfo)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();
    }

    private void updateNotification() {
        sv = new ShareVariables(this);
        stopwatch.start();
        while (wakeLock.isHeld()) {
            if (!sv.getActiveActivity()) wakeLock.release();
            else {
                SystemClock.sleep(100);
                setNewNotification(stopwatch.formatTime());
            }
        }
    }

    private void setNewNotification(String activityInfo) {
        Notification newNotification = getNotification(activityInfo);
        NotificationManager mNotificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        mNotificationManager.notify(notID, newNotification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service onDestroy");

        if (wakeLock.isHeld()) wakeLock.release();
    }
}
