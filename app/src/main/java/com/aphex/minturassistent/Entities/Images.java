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
        indices = {@Index("imageID")},
        foreignKeys = {@ForeignKey(entity = Trip.class,
                parentColumns = "tripID",
                childColumns = "imageID",
                onDelete = CASCADE)})

public class Images implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "imageID") public int mImageID;

    @ColumnInfo(name = "title") public String mTitle;

    @ColumnInfo(name = "imageURI") public String mImageURI;

    public Images(String mTitle, String mImageURI) {
        this.mTitle = mTitle;
        this.mImageURI = mImageURI;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmImageURI() {
        return mImageURI;
    }

    public void setmImageURI(String mImageURI) {
        this.mImageURI = mImageURI;
    }
}
