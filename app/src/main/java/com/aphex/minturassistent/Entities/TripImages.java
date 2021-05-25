package com.aphex.minturassistent.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

public class TripImages {
    @ColumnInfo(name = "tripID") public int mTripID;

    @ColumnInfo(name = "tripName") public String mTripName;

    @ColumnInfo(name = "date") public String mDate;

    @ColumnInfo(name = "estimatedHDays") public int mEstimatedHDays;

    @ColumnInfo(name = "estimatedHours") public int mEstimatedHours;

    @ColumnInfo(name = "tourType") public String mTourType;

    @ColumnInfo(name = "isFinished") public Boolean mIsFinished;

    @ColumnInfo(name = "timeSpent") public String mTimeSpent;

    @ColumnInfo(name = "place") public String mPlace;

    @ColumnInfo(name = "imageID") public int mImageID;

    @ColumnInfo(name = "FKtripID") public int mFKTripID;

    @ColumnInfo(name = "title") public String mTitle;

    @ColumnInfo(name = "imageURI") public String mImageURI;

    @ColumnInfo(name = "latitude") public double mLatitude;

    @ColumnInfo(name = "longitude") public double mLongitude;

    public TripImages(String mTripName, String mDate, int mEstimatedHours, int mEstimatedHDays,
                      Boolean mIsFinished, String mTimeSpent, String mPlace, String mTourType,
                      int mFKTripID, String mTitle, String mImageURI, double mLatitude, double mLongitude ) {
        this.mTripName = mTripName;
        this.mDate = mDate;
        this.mEstimatedHours = mEstimatedHours;
        this.mEstimatedHDays = mEstimatedHDays;
        this.mIsFinished = mIsFinished;
        this.mTimeSpent = mTimeSpent;
        this.mPlace = mPlace;
        this.mTourType = mTourType;
        this.mFKTripID = mFKTripID;
        this.mTitle = mTitle;
        this.mImageURI = mImageURI;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
    }

}
