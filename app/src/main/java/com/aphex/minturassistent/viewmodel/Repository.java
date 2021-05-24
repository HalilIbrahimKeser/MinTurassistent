package com.aphex.minturassistent.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.aphex.minturassistent.Entities.Images;
import com.aphex.minturassistent.Entities.MetData;
import com.aphex.minturassistent.Entities.Trip;
import com.aphex.minturassistent.MainActivity;
import com.aphex.minturassistent.db.API;
import com.aphex.minturassistent.db.Dao;
import com.aphex.minturassistent.db.RoomDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Repository {
    private static final String BASE_URL = "https://api.met.no/weatherapi/locationforecast/2.0/";
    private final Retrofit retrofit;
    private final com.aphex.minturassistent.db.API API;

    private MutableLiveData<MetData> metData;

    private final Dao mDao;
    private final LiveData<List<Trip>> mAllTrips;
    public LiveData<List<Trip>> singleTrip;

    Repository(Application application) {
        RoomDatabase db = RoomDatabase.getDatabase(application);
        mDao = db.Dao();
        metData = new MutableLiveData<>();
        mAllTrips = (LiveData<List<Trip>>) mDao.getTrips();
        metData = new MutableLiveData<>();

        retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        API = retrofit.create(API.class);
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

    LiveData<List<Trip>> getSingleTrip(int mTripID) {
        singleTrip = mDao.getSingleTrip(mTripID);
        return singleTrip;
    }

    LiveData<Trip> getTrip(int mTripID) {
        return mDao.getTrip(mTripID);
    }

    void deleteTrip(int trip) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            mDao.deleteTrip(trip);
        });
    }

    //IMAGES--------------------
    void imageInsert(Images image) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            mDao.imageInsert(image);
        });
    }

    LiveData<List<Images>> getImage(int mImageID) {
        return mDao.getImage(mImageID);
    }

    //MAPS ------------------------
    LiveData<List<Trip>> getLastTourType() {
        return mDao.getLastTourType();
    }

    //METDATA -----------------------------------------

    //LAST NED METDATA
    public MutableLiveData<MetData> downloadMetData(String lat, String lon) {
        Call<MetData> call = API.downloadMetData(lat, lon);
        call.enqueue(new Callback<MetData>() {
            @Override
            public void onResponse(@NotNull Call<MetData> call, @NotNull Response<MetData> response) {
                if(response.isSuccessful()) {
                    MetData data = response.body();
                    metData.setValue(data);
                } else {
                    int statuscode = response.code();
                }
            }
            @Override
            public void onFailure(Call<MetData> call, Throwable t) {
            }
        });
        return metData;
    }
}