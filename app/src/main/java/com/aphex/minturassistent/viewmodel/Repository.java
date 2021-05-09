package com.aphex.minturassistent.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.aphex.minturassistent.Entities.Trip;
import com.aphex.minturassistent.db.Dao;
import com.aphex.minturassistent.db.RoomDatabase;

import java.util.List;

public class Repository {

    private Dao mDao;
    private LiveData<List<Trip>> mAllTrips;


    Repository(Application application) {
        RoomDatabase db = RoomDatabase.getDatabase(application);
        mDao = db.Dao();
        mAllTrips = (LiveData<List<Trip>>) mDao.getTrips();

    }
    //TRIP--------------------
    void tripInsert(Trip trip) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            mDao.tripInsert(trip);
        });
    }

    LiveData<List<Trip>> getAllTrips() {
        return mAllTrips;
    }

    LiveData<List<Trip>> getTripData(int mTripID) {
        return mDao.getTripData(mTripID);
    }
}