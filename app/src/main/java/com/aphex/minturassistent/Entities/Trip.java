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

    @ColumnInfo(name = "isFinished") public Boolean mIsFinished;

    @ColumnInfo(name = "timeSpent") public String mTimeSpent;

//    @ColumnInfo(name = "images") public String mImages;

    public Trip( String mTripName, String mDate, Boolean mIsFinished, String mTimeSpent) {
        this.mTripName = mTripName;
        this.mDate = mDate;
        this.mIsFinished = mIsFinished;
        this.mTimeSpent = mTimeSpent;
//        this.mImages = mImages;
    }
}
