package entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity (tableName = "workout_table")
public class Workout {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String start_time;
    private String end_time;
    private double duration_s;
    private double distance_km;
    private double ascend_m;
    private double descend_m;
    private double speed_avg_kmh;
    private double speed_max_kmh;
    private double altitude_min_m;
    private double altitude_max_m;

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    @Ignore
    public Workout(String start_time, String end_time, double duration_s, double distance_km,
                   double ascend_m, double descend_m, double speed_avg_kmh, double speed_max_kmh,
                   double altitude_min_m, double altitude_max_m) {
        this.start_time = start_time;
        this.end_time = end_time;
        this.duration_s = duration_s;
        this.distance_km = distance_km;
        this.ascend_m = ascend_m;
        this.descend_m = descend_m;
        this.speed_avg_kmh = speed_avg_kmh;
        this.speed_max_kmh = speed_max_kmh;
        this.altitude_min_m = altitude_min_m;
        this.altitude_max_m = altitude_max_m;
    }

    public Workout(double duration_s, double distance_km) {
        this.duration_s = duration_s;
        this.distance_km = distance_km;
    }

    public String getStart_time() {
        return start_time;
    }
    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }
    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public double getDuration_s() {
        return duration_s;
    }
    public void setDuration_s(double duration_s) {
        this.duration_s = duration_s;
    }

    public double getDistance_km() {
        return distance_km;
    }
    public void setDistance_km(double distance_km) {
        this.distance_km = distance_km;
    }

    public double getAscend_m() {
        return ascend_m;
    }
    public void setAscend_m(double ascend_m) {
        this.ascend_m = ascend_m;
    }

    public double getDescend_m() {
        return descend_m;
    }
    public void setDescend_m(double descend_m) {
        this.descend_m = descend_m;
    }

    public double getSpeed_avg_kmh() {
        return speed_avg_kmh;
    }
    public void setSpeed_avg_kmh(double speed_avg_kmh) {
        this.speed_avg_kmh = speed_avg_kmh;
    }

    public double getSpeed_max_kmh() {
        return speed_max_kmh;
    }
    public void setSpeed_max_kmh(double speed_max_kmh) {
        this.speed_max_kmh = speed_max_kmh;
    }

    public double getAltitude_min_m() {
        return altitude_min_m;
    }
    public void setAltitude_min_m(double altitude_min_m) {
        this.altitude_min_m = altitude_min_m;
    }

    public double getAltitude_max_m() {
        return altitude_max_m;
    }
    public void setAltitude_max_m(double altitude_max_m) {
        this.altitude_max_m = altitude_max_m;
    }
}
