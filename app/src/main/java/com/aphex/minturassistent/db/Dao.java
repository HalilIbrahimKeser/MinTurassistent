package com.aphex.minturassistent.db;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RewriteQueriesToDropUnusedColumns;

import com.aphex.minturassistent.Entities.Images;
import com.aphex.minturassistent.Entities.Location;
import com.aphex.minturassistent.Entities.Trip;
import com.aphex.minturassistent.Entities.TripImages;

import java.util.ArrayList;
import java.util.List;

@androidx.room.Dao
public interface Dao {

    //TRIP - - - - - - - - - - - - - - - - - - - - -  -
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void tripInsert(Trip trip);

    @Query("SELECT * FROM trip_table ORDER BY tripID")
    LiveData<List<Trip>> getTrips();

    @Query("SELECT * FROM trip_table WHERE tripID = :mTripID")
    LiveData<List<Trip>> getTripData(int mTripID);

    @Query("SELECT * FROM trip_table WHERE tripID = :mTripID")
    LiveData<Trip> getTrip(int mTripID);

    @Query("DELETE FROM trip_table WHERE tripID = :mTripID")
    void deleteTrip(int mTripID);

    @Query("SELECT * FROM trip_table WHERE tripID = :mTripID")
    LiveData<List<Trip>> getSingleTrip(int mTripID);

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM trip_table LEFT JOIN image_table ON  tripID = FKtripID  WHERE tripID = :mTripID")
    LiveData<List<TripImages>> getTripWithImages(int mTripID);

    @Query("UPDATE trip_table SET comment=:mComment WHERE tripID =:mTripID")
    void setComment(int mTripID, String mComment);

    @Query("UPDATE trip_table SET isFinished = 1 WHERE tripID =:mTripID")
    void updateIsFinished(int mTripID);


    //IMAGE - - - - - - - - - - - - - - - - - - - - -  -
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void imageInsert(Images image);

    @Query("SELECT * FROM image_table ORDER BY imageID")
    LiveData<List<Images>> getImages();

    @Query("SELECT * FROM image_table WHERE FKtripID =:mFKTripID")
    LiveData<List<Images>> getImagesForTrip(int mFKTripID);

    @Query("SELECT * FROM image_table WHERE imageID = :mImageID")
    LiveData<List<Images>> getImage(int mImageID);

    @Query("DELETE FROM image_table WHERE imageID = :mImageID")
    void deleteImage(int mImageID);

    //LOCATION - - - - - - - - - - - - - - - - - - - - -  -
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void locationInsert(Location location);

    @Query("SELECT * FROM location_table ORDER BY locTripID")
    LiveData<List<Location>> getLocations();

    @Query("DELETE FROM trip_table")
    void deleteAlltrips();

    //MAPS - - - - - - - - - -
    @Query("SELECT * FROM trip_table ORDER BY tripID DESC LIMIT 1")
    LiveData<List<Trip>> getLastTourType();

    @Query("SELECT * FROM location_table WHERE locTripID = :mTripID")
    LiveData<List<Location>> getLocationPath(int mTripID);
}
