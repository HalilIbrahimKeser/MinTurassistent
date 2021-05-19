package com.aphex.minturassistent.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

@Entity(tableName = "trip_table")
public class Trip implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "tripID") public int mTripID;

    @ColumnInfo(name = "tripName") public String mTripName;

    @ColumnInfo(name = "date") public String mDate;

    @ColumnInfo(name = "estimatedHDays") public int mEstimatedHDays;

    @ColumnInfo(name = "estimatedHours") public int mEstimatedHours;

    @ColumnInfo(name = "tourType") public String mTourType;

    @ColumnInfo(name = "isFinished") public Boolean mIsFinished;

    @ColumnInfo(name = "timeSpent") public String mTimeSpent;

    @ColumnInfo(name = "place") public String mPlace;

    @Embedded public StartGeo startGeo;

    @Embedded public StopGeo stopGeo;

    public Trip( String mTripName, String mDate, int mEstimatedHours, int mEstimatedHDays, Boolean mIsFinished, String mTimeSpent, String mPlace, String mTourType) {
        this.mTripName = mTripName;
        this.mDate = mDate;
        this.mEstimatedHours = mEstimatedHours;
        this.mEstimatedHDays = mEstimatedHDays;
        this.mIsFinished = mIsFinished;
        this.mTimeSpent = mTimeSpent;
        this.mPlace = mPlace;
        this.mTourType = mTourType;
    }

    public static class StartGeo {
        public double latitude;
        public double longitude;

        public StartGeo(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    public static class StopGeo {
        public double latitude1;
        public double longitude1;

        public StopGeo(double latitude1, double longitude1) {
            this.latitude1 = latitude1;
            this.longitude1 = longitude1;
        }
    }

    public StartGeo getStartGeo() { return startGeo; }
    public void setStartGeo(StartGeo startGeo) { this.startGeo = startGeo; }

    public StopGeo getStopGeo() { return stopGeo; }
    public void setStopGeo(StopGeo stopGeo) { this.stopGeo = stopGeo; }

    public int getmTripID() {
        return mTripID;
    }
    public void setmTripID(int mTripID) {
        this.mTripID = mTripID;
    }

    public String getmTripName() {
        return mTripName;
    }
    public void setmTripName(String mTripName) {
        this.mTripName = mTripName;
    }

    public String getmDate() {
        return mDate;
    }
    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public int getmEstimatedHDays() { return mEstimatedHDays; }
    public void setmEstimatedHDays(int mEstimatedHDays) { this.mEstimatedHDays = mEstimatedHDays; }

    public int getmEstimatedHours() { return mEstimatedHours; }
    public void setmEstimatedHours(int mEstimatedHours) { this.mEstimatedHours = mEstimatedHours; }

    public Boolean getmIsFinished() {
        return mIsFinished;
    }
    public void setmIsFinished(Boolean mIsFinished) {
        this.mIsFinished = mIsFinished;
    }

    public String getmTimeSpent() {
        return mTimeSpent;
    }
    public void setmTimeSpent(String mTimeSpent) {
        this.mTimeSpent = mTimeSpent;
    }

    public String getmPlace() {
        return mPlace;
    }
    public void setmPlace(String mPlace) {
        this.mPlace = mPlace;
    }

    public String getmTourType() {return mTourType;}

    public void setmTourType(String mTourType) {
        this.mTourType = mTourType;
    }
}
