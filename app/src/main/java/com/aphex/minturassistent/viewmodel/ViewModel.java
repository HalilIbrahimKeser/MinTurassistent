package com.aphex.minturassistent.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aphex.minturassistent.Entities.Images;
import com.aphex.minturassistent.Entities.MetData;
import com.aphex.minturassistent.Entities.Trip;

import java.util.ArrayList;
import java.util.List;

public class ViewModel extends AndroidViewModel {

    private final Repository mRepository;

    public LiveData<List<Trip>> mAllTrips;
    public MutableLiveData<Trip> mCurrentTrip;

    public MutableLiveData<String> dateData = new MutableLiveData<>();
    public MutableLiveData<List<Images>> mediaData = new MutableLiveData<>();

    public ViewModel(Application application) {
        super(application);
        mRepository = new Repository(application);
        mAllTrips = mRepository.getAllTrips();
    }

    //TRIP ------------------------------------------------------------
    public void insertTrip(Trip trip) {
        mRepository.tripInsert(trip);
    }

    public MutableLiveData<Trip> getCurrentTrip() {
        if (mCurrentTrip == null) {
            mCurrentTrip = new MutableLiveData<>();
        }
        return mCurrentTrip;
    }

    public LiveData<List<Trip>> getAllTrips() {
        return mAllTrips;
    }

    public LiveData<List<Trip>> getTripData(int mTripID) {
        return mRepository.getTripData(mTripID);
    }

    public LiveData<Trip> getTrip(int mTripID) {
        return mRepository.getTrip(mTripID);
    }

    public LiveData<List<Trip>> getSingleTrip(int mTripID) {
        return mRepository.getSingleTrip(mTripID);
    }

    public void deleteTrip(int mTripID) {
        mRepository.deleteTrip(mTripID);
    }


    //DATE ------------------------------------------------------
    public MutableLiveData<String> getDate() {
        return dateData;
    }


    //IMAGE ------------------------------------------------------------------
    public MutableLiveData<List<Images>> getMediaData() {
        return mediaData;
    }

    public LiveData<List<Images>> getImage(int mImageID) {
        return mRepository.getImage(mImageID);
    }

    //MAPS ------------------------------------------------------------------
    public LiveData<List<Trip>> getLastTourType() { return mRepository.getLastTourType(); }

    //METDATA -----------------------------------------------------------------
    public MutableLiveData<ArrayList<MetData>> getMetData(String lat, String lon) {
        return mRepository.downloadMetData(lat, lon);
    }

}
