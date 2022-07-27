package calculations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class WorkoutInfo {

    private static final SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd KK:mm:ss zzz yyyy", Locale.getDefault());
    private static final SimpleDateFormat sdf = new SimpleDateFormat("KK:mm:ss", Locale.getDefault());
    private static final String TAG = "myT";

    public static String getTime(String startTime, String newTime) {
        //Log.d(TAG, "getnewTime: " + newTime);
        //Log.d(TAG, "getstartTime: " + startTime);
        long time = 0;
        try {
            time = (long) (convertDate(newTime) - convertDate(startTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertDateToString(time);
    }

    public static String addTime(String previousTime, String newTime) {
        long time = 0;
        try {
            time = (long) (convertTime(previousTime) + convertTime(newTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertDateToString(time);
    }

    public static double getDistance(double newLat, double oldLat, double newLng, double oldLng, double newEl, double oldEl, double oldDistance) {

        if (oldLat != 0.0) {
            final int R = 6371; // Radius of the earth
            double latDistance = Math.toRadians(newLat - oldLat);
            double lonDistance = Math.toRadians(newLng - oldLng);
            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(oldLat)) * Math.cos(Math.toRadians(newLat))
                    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double d = R * c * 1000; //m

            double height = oldEl - newEl;

            d = (Math.pow(d, 2) + Math.pow(height, 2));
            //Log.d("myT", "getDistance: " + d + " " + oldDistance);
            return ((Math.sqrt(d)/1000) + oldDistance);
        } else return 0;


    }

    public static String getSpeed(double newDistance, double oldDistance, String newTime, String oldTime) throws ParseException {
        double hours = (convertDate(oldTime) - convertDate(newTime))/(1000*60*60);
        return formatTime((oldDistance - newDistance)/(hours));
    }

    public static String getAvgSpeed(double distance, String time) throws ParseException {
        //Log.d(TAG, "gettime: " + time);
        double hours = (convertTime(time))/(1000*60*60);
        //Log.d(TAG, "getAvgSpeed: " + distance/hours + " " + distance + " " + hours);
        return formatTime(distance/hours);
    }

    public static String getDate() {
        return df.format(Calendar.getInstance().getTime());
    }

    public static double convertTime(String time) throws ParseException {
        Date date2 = sdf.parse(time);
        TimeZone tz = TimeZone.getDefault();
        long adjustedTime = date2.getTime() + tz.getOffset(new Date().getTime());
        //Log.d(TAG, "convertTime: " + date2.getTime());
        return adjustedTime;
    }

    public static double convertDate(String time) throws ParseException {
        Date date1 = df.parse(time);
        Date date2 = sdf.parse(sdf.format(date1));
        //Log.d(TAG, "convertTime: " + date2.getTime());
        return date2.getTime();
    }

    public static String convertDateToString(long time) {
        TimeZone tz = TimeZone.getDefault();
        long adjustedTime = time - tz.getOffset(new Date().getTime());
        Date dateTime = new Date(adjustedTime);
        return sdf.format(dateTime);
    }

    public static String convertTimeToString(long time) {
        TimeZone tz = TimeZone.getDefault();
        long adjustedTime = time + tz.getOffset(new Date().getTime());
        Date dateTime = new Date(time);
        return sdf.format(adjustedTime);
    }

    public static String formatTime(double value) {
        return String.format("%.2f", value);
    }
}
