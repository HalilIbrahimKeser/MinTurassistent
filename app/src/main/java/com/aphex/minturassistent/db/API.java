package com.aphex.minturassistent.db;

import androidx.lifecycle.MutableLiveData;

import com.aphex.minturassistent.Entities.MetData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface API {
    @Headers({
            "Accept: application/json",
            "User-Agent: MinTurassistent aab057@uit.no"
            })
    @GET("compact")
    Call<MetData> downloadMetData(@Query("lat") String lat, @Query("lon") String lon);
}
