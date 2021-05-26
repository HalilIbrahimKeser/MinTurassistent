package com.aphex.minturassistent.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.aphex.minturassistent.MainActivity;
import com.aphex.minturassistent.R;
import com.aphex.minturassistent.viewmodel.Repository;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.jetbrains.annotations.NotNull;

public class MyLocationService extends Service {

    private final Handler handler = new Handler();
    private static final int LOCATION_NOTIFICATION_ID = 1010;
    private LocationCallback locationCallback;
    private Location previousLocation=null;
    private NotificationManager notificationManager;
    private String channelId;

    // NB! Bruker repositoryklassen direkte - går ikke via ViewModel.
    private Repository mRepository;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        Notification notification = createNotification("0.0 0.0");
        startForeground(LOCATION_NOTIFICATION_ID, notification);
        // NB! Bruker repositoryklassen direkte - går ikke via ViewModel.
        mRepository = new Repository(getApplication());
    }

    private Notification createNotification(String notificationText){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification =  new Notification.Builder(this, channelId)
                .setContentTitle("Lokasjon")
                .setOnlyAlertOnce(false)
                .setContentText("Din posisjon: " + notificationText)
                .setSmallIcon(R.drawable.ic_menu_mylocation)
                .setContentIntent(pendingIntent)
                .setTicker("Tracker din lokasjon ...")
                .build();
        return notification;
    }

    private String createNotificationChannel(NotificationManager notificationManager) {
        String channelId = "my_location_channelid";
        String channelName = "MyLocationService";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context context = this.getApplicationContext();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NotNull LocationResult locationResult) {
                StringBuilder locationBuffer = new StringBuilder();
                for (Location location : locationResult.getLocations()) {
                    if (previousLocation == null)
                        previousLocation = location;

                    float distance = previousLocation.distanceTo(location);
                    Log.d("MY-LOCATION-DISTANCE", String.valueOf(distance));
                    Log.d("MY-LOCATION", location.toString());

                    locationBuffer.append(location.getLatitude() + ", " + location.getLongitude() + "\n");
                    previousLocation = location;

                    com.aphex.minturassistent.Entities.Location currentLocation = new com.aphex.minturassistent.Entities.Location(1,location.getLatitude(), location.getLongitude());
                    mRepository.insert(currentLocation);

                    SharedPreferences prefs = getApplication().getSharedPreferences("positionForImage", 0);
                    SharedPreferences.Editor editor = prefs.edit();
                    prefs.edit().remove("positionForImage").apply();
                    editor.putFloat("lat", (float) previousLocation.getLatitude());
                    editor.putFloat("lon", (float) previousLocation.getLongitude());
                    editor.apply();
                }

                // Viser/oppdaterer varsel:
                Notification notification = createNotification(locationBuffer.toString());
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(LOCATION_NOTIFICATION_ID, notification);

                // Sender lokal broadcast:
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        };

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        final LocationRequest locationRequest = MainActivity.createLocationRequest();
        startLocationUpdates(locationRequest);

        //START_STICKY sørger for at onStartCommand() kjører ved omstart (dvs. når service terminerer og starter på nytt ved ressursbehov):
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        this.stopLocationUpdates();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final Runnable doBackgroundThreadProcessing = new Runnable() {
        public void run() {
            backgroundThreadProcessing();
        }
    };

    private void backgroundThreadProcessing() {
        int res = 0;
        for (int i = 0; i < 10; i++) {
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        handler.post(doShowResult);

        this.stopSelf();
    }

    private Runnable doShowResult = new Runnable() {
        public void run() {
            showResult();
        }
    };

    private void showResult() {
        Context context = this.getApplicationContext();
        Toast.makeText(context, "Oppdrag fullført!!!!", Toast.LENGTH_SHORT).show();

        //Send beskjed til Activity vha. en broadcast / BroadcastReceiver.
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates(LocationRequest locationRequest) {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */);
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null)
            fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}