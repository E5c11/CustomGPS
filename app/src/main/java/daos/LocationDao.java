package daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import entities.LocationNow;

@Dao
public interface LocationDao {

    @Insert
    void insert(LocationNow locationNow);

    @Update
    void update(LocationNow location);

    @Delete
    void delete(LocationNow locationNow);

    @Query("SELECT * FROM location_table ORDER BY id DESC LIMIT 1")
    LiveData<LocationNow> getLocation();

    @Query("DELETE FROM location_table")
    void clearTable();

}
