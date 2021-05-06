package com.aphex.minturassistent.viewmodel;

import android.app.Application;
import android.app.DatePickerDialog;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aphex.minturassistent.Entities.Trip;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

public class ViewModel extends AndroidViewModel {

    private Repository mRepository;
    public MutableLiveData<String> date;
    public LiveData<List<Trip>> mAllTrips;

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

    public MutableLiveData<String> getDate() {
        return date;
    }

    public void setValue(int year, int month, int dayOfMonth) {
        Date yearDate = new Date(year, month, dayOfMonth);
    }
}
