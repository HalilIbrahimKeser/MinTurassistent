package com.aphex.minturassistent.viewmodel;

import android.app.Application;
import android.app.DatePickerDialog;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aphex.minturassistent.Entities.Trip;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ViewModel extends AndroidViewModel {

    private Repository mRepository;
    public LiveData<List<Trip>> mAllTrips;
    public MutableLiveData<String> dateData = new MutableLiveData<>();

    public ViewModel(Application application) {
        super(application);
        mRepository = new Repository(application);
        mAllTrips = mRepository.getAllTrips();
    }

    //TRIP ------------------
    public void insertTrip(Trip trip) {
        mRepository.tripInsert(trip);
    }

    public LiveData<List<Trip>> getAllTrips() {
        return mAllTrips;
    }

    //DATE VIEWMODEL
    public MutableLiveData<String> getDate() {
        return dateData;
    }

}
