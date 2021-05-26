package com.aphex.minturassistent.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "location_table",
        indices = {@Index("locTripID")},
        foreignKeys = {@ForeignKey(entity = Trip.class,
                parentColumns = "tripID",
                childColumns = "locTripID",
                onDelete = CASCADE)})

public class Location implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "locationID") public int mLocationID;

    @ColumnInfo(name = "locTripID") public int mtripID;

    @ColumnInfo(name = "latitude") public double mLatitude;

    @ColumnInfo(name = "longitude") public double mLongitude;


    public Location(int mtripID, double mLatitude, double mLongitude) {
        this.mtripID = mtripID;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }
}
