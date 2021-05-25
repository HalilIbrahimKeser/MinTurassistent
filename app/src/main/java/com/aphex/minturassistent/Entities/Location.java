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
        indices = {@Index("locationID")},
        foreignKeys = {@ForeignKey(entity = Trip.class,
                parentColumns = "tripID",
                childColumns = "locationID",
                onDelete = CASCADE)})

public class Location implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "locationID") public int mLocationID;

    @ColumnInfo(name = "tripID") public int mtripID;

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

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }
}
