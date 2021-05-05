package com.aphex.minturassistent.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.aphex.minturassistent.Entities.Images;
import com.aphex.minturassistent.Entities.Location;
import com.aphex.minturassistent.Entities.Trip;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Trip.class, Location.class, Images.class}, version = 1, exportSchema = false)
public abstract class RoomDatabase extends androidx.room.RoomDatabase {
    public abstract Dao Dao();
    private static volatile RoomDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static RoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context,
                            RoomDatabase.class, "main_db")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    private static Callback sRoomDatabaseCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {

                Dao dao = INSTANCE.Dao();
                dao.deleteAlltrips();

                //Dummy User
                Trip trip1 = new Trip( "Nydelig dag på Mt.Everest!", "05.05.2021", true,"24", "Mt.Everest");
                Trip trip2 = new Trip( "Fett på K2!", "06.06.2021", true,"12", "K2");
                Trip trip3 = new Trip( "Bestiget Besseggen!", "19.07.2021", true,"10", "Besseggen");
                dao.tripInsert(trip1);
                dao.tripInsert(trip2);
                dao.tripInsert(trip3);

                //Dummy User
                Location location1 = new Location( 1, 71.33, 54.22,22.22);
                dao.locationInsert(location1);
            });
        }
    };
}
