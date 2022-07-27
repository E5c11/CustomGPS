package databases;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import daos.LocationDao;
import daos.WorkoutDao;
import entities.LocationNow;
import entities.Workout;

@Database(entities = {LocationNow.class, Workout.class}, version = 1)
public abstract class LocationDatabase extends RoomDatabase {

    private static LocationDatabase instance;

    public abstract LocationDao locationDao();

    public abstract WorkoutDao workoutDao();

    public static synchronized LocationDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    LocationDatabase.class, "location_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };

}
