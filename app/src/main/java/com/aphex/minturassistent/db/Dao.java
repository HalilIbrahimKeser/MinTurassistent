package com.aphex.minturassistent.db;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.aphex.minturassistent.Entities.Location;
import com.aphex.minturassistent.Entities.Trip;
import java.util.List;

@androidx.room.Dao
public interface Dao {

    //TRIP - - - - - - - - - - - - - - - - - - - - -  -
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void tripInsert(Trip trip);

    @Query("SELECT * FROM trip_table ORDER BY tripID")
    LiveData<List<Trip>> getTrips();

    //LOCATION - - - - - - - - - - - - - - - - - - - - -  -
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void locationInsert(Location location);

    @Query("SELECT * FROM location_table ORDER BY tripID")
    LiveData<List<Location>> getLocations();

    @Query("DELETE FROM trip_table")
    void deleteAlltrips();
}
