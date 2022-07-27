package adaptersandmore;

import java.util.Date;

public class LocationDetails {

    private double latitude, longitude, altitude, speed;
    private Date time;

    public LocationDetails(double latitude, double longitude, double altitude, double speed, Date time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.speed = speed;
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getSpeed() {
        return speed;
    }

    public Date getTime() {
        return time;
    }
}
