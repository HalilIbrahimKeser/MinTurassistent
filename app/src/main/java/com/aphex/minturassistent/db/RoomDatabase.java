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
    private static final Callback sRoomDatabaseCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {

                Dao dao = INSTANCE.Dao();
                dao.deleteAlltrips();

                //Dummy StartGeo
                Trip.StartGeo startGeo = new Trip.StartGeo(68.43,17.43);

                //Dummy StopGeo
                Trip.StopGeo stopGeo = new Trip.StopGeo(68.442,17.41);

                //Dummy Trip
                Trip trip1 = new Trip( "Nydelig dag på Mt.Everest!", "05.05.2021", 0, 5,
                        true,"24", "61.44, -17.41", "Sykkeltur", startGeo, stopGeo);

                Trip trip2 = new Trip( "Fett på K2!", "06.06.2021", 0, 5,
                        true,"12", "70.42, 10.41", "Gåtur", startGeo, stopGeo);

                Trip trip3 = new Trip( "Bestiget Besseggen!", "19.07.2021", 0, 5,
                        true, "10", "55.42, 0.41", "Gåtur", startGeo, stopGeo);

                Trip trip4 = new Trip( "Mormorparken", "16.05.2021", 0, 5,
                        false, "10", "68.43, 17.41", "Gåtur", startGeo, stopGeo);
                dao.tripInsert(trip1);
                dao.tripInsert(trip2);
                dao.tripInsert(trip3);
                dao.tripInsert(trip4);

                //Dummy Location
                Location location1 = new Location(1, 71.33, 54.22);
                dao.locationInsert(location1);

            });
        }
    };
}
