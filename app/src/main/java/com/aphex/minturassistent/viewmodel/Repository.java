package com.aphex.minturassistent.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aphex.minturassistent.Entities.Images;
import com.aphex.minturassistent.Entities.MetData;
import com.aphex.minturassistent.Entities.Trip;
import com.aphex.minturassistent.Entities.Weather;
import com.aphex.minturassistent.db.API;
import com.aphex.minturassistent.db.Dao;
import com.aphex.minturassistent.db.RoomDatabase;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Repository {
    private static final String BASE_URL = "https://api.met.no/weatherapi/locationforecast/2.0/";
    private final Retrofit retrofit;
    private final com.aphex.minturassistent.db.API API;
    private final MutableLiveData<ArrayList<MetData>> mMetData;

    private final Dao mDao;
    private final LiveData<List<Trip>> mAllTrips;
    public LiveData<List<Trip>> singleTrip;

    Repository(Application application) {
        RoomDatabase db = RoomDatabase.getDatabase(application);
        mDao = db.Dao();
        mMetData = new MutableLiveData<>();
        mAllTrips = (LiveData<List<Trip>>) mDao.getTrips();

//        HttpUrl baseUrl = new HttpUrl.Builder()
//                .scheme("https")
//                .host("api.met.no")
//                .addPathSegment("weatherapi")
//                .addPathSegment("locationforecast")
//                .addPathSegment("2.0")
//                .build();
        retrofit = new Retrofit.Builder()
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
    public MutableLiveData<ArrayList<MetData>> downloadMetData(String lat, String lon) {
        Map<String, String> urlArguments = new HashMap<>();

        urlArguments.put("lat", lat);
        urlArguments.put("lon", lon);

        Call<MetData> call = API.downloadMetData(lat, lon);
        call.enqueue(new Callback<MetData>() {
            @Override
            public void onResponse(@NotNull Call<MetData> call, @NotNull Response<MetData> response) {
                if(response.isSuccessful()) {
                    MetData data = response.body();
                } else {
                    int statuscode = response.code();
                }
            }
            @Override
            public void onFailure(Call<MetData> call, Throwable t) {
            }
        });
        return mMetData;
    }
}