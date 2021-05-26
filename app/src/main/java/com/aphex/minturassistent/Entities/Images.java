package com.aphex.minturassistent.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "image_table",
        indices = {@Index("FKtripID")},
        foreignKeys = {@ForeignKey(entity = Trip.class,
                parentColumns = "tripID",
                childColumns = "FKtripID",
                onDelete = CASCADE)})

public class Images implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "imageID") public int mImageID;

    @ColumnInfo(name = "FKtripID") public int mFKTripID;

    @ColumnInfo(name = "title") public String mTitle;

    @ColumnInfo(name = "imageURI") public String mImageURI;

    @ColumnInfo(name = "latitude") public double mLatitude;

    @ColumnInfo(name = "longitude") public double mLongitude;

    public Images(int mFKTripID, String mTitle, String mImageURI, double mLatitude, double mLongitude) {
        this.mFKTripID = mFKTripID;
        this.mTitle = mTitle;
        this.mImageURI = mImageURI;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
    }
    public double getmLatitude() { return mLatitude; }
    public void setmLatitude(double mLatitude) { this.mLatitude = mLatitude; }
    public double getmLongitude() { return mLongitude; }
    public void setmLongitude(double mLongitude) { this.mLongitude = mLongitude; }
    public int getmFKTripID() { return mFKTripID; }
    public void setmFKTripID(int mFKTripID) { this.mFKTripID = mFKTripID; }
    public String getmTitle() {
        return mTitle;
    }
    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }
    public String getmImageURI() { return mImageURI; }
    public void setmImageURI(String mImageURI) {
        this.mImageURI = mImageURI;
    }
}
