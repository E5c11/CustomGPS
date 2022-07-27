package entities;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "location_table")
public class LocationNow {

    @PrimaryKey(autoGenerate = true)
    private int id;

    //private String currentLocationNow;
    @Embedded
    private location location;
    //private double latitude;
    //private double longitude;
    private double altitude;
    private String time;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public LocationNow(location location, double altitude, String time) {
        this.location = location;
        this.altitude = altitude;
        this.time = time;
    }

    public LocationNow() {}

    /*public double getLatitude() {return latitude;}

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {return longitude;}

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }*/

    public double getAltitude() {return altitude;}

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getTime() {return time;}

    public void setTime(String time) {
        this.time = time;
    }

    public entities.location getLocation() {
        return location;
    }

    public void setLocation(entities.location location) {
        this.location = location;
    }
}
