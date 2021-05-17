package com.aphex.minturassistent.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aphex.minturassistent.Entities.Images;
import com.aphex.minturassistent.Entities.Trip;

import java.util.List;

public class ViewModel extends AndroidViewModel {

    private Repository mRepository;
    public LiveData<List<Trip>> mAllTrips;

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

    public LiveData<List<Trip>> getAllTrips() {
        return mAllTrips;
    }

    public LiveData<List<Trip>> getTripData(int mTripID) {
        return mRepository.getTripData(mTripID);
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

}
