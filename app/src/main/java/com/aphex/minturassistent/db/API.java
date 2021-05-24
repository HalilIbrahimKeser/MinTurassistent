package com.aphex.minturassistent.db;

import com.aphex.minturassistent.Entities.MetData;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface API {
    @Headers({
            "Accept: application/json",
            "User-Agent: MinTurassistent aab057@uit.no"
            })
    @GET("compact")
    Call<MetData> downloadMetData(@Query("lat") String lat, @Query("lon") String lon);
}
