package com.aphex.minturassistent;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.aphex.minturassistent.Entities.Location;
import com.aphex.minturassistent.databinding.ActivityMainBinding;
import com.aphex.minturassistent.service.MyLocationService;
import com.aphex.minturassistent.viewmodel.ViewModel;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ViewModel mViewModel;
    FusedLocationProviderClient mFusedLocationClient;
    Location mLastLocation;
    Location mTestLastLocation;
    android.location.Location location;
    private double startPosLat;
    private double startPosLon;
    private ComponentName service;
    private boolean requestingLocationUpdates = false;
    private static BottomNavigationView bottomNav;
    private static Toolbar myToolbar;
    private static final int REQUEST_CHECK_SETTINGS = 10;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 2;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_MEDIA_LOCATION, Manifest.permission.CAMERA};
    //Manifest.permission.WRITE_EXTERNAL_STORAGE, bør fjernes fra ovenfor

    private class MyLocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            double latitude = intent.getDoubleExtra("LATITUDE", 0);
            double longitude = intent.getDoubleExtra("LONGITUDE", 0);
            if (latitude != 0) {
                mLastLocation.setmLatitude(latitude);
                mLastLocation.setmLongitude(longitude);

                mViewModel.getLastLocation().setValue(mLastLocation);

                SharedPreferences prefs = getSharedPreferences("POSITION", Context.MODE_PRIVATE);
                startPosLat = prefs.getFloat("startgeolat", 0);
                startPosLon = prefs.getFloat("startgeolon", 0);
            }
        }
    }

    private MyLocationReceiver myLocationReceiver = new MyLocationReceiver();

    public final static String LOCATION_FILTER_STRING = "com.wfamedia.location2.MY_LOCATION_RECEIVER";
    private IntentFilter myBroadcastFilter = new IntentFilter(LOCATION_FILTER_STRING);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        ActivityMainBinding binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.getRoot());

        checkPermissions();

        mViewModel = new ViewModelProvider(this).get(ViewModel.class);

        mViewModel.getLastLocation().observe(this, lastLocationData -> {
            if (lastLocationData != null) {
                double mLatitude = lastLocationData.mLatitude;
                double mLongitude = lastLocationData.mLongitude;
                mTestLastLocation = lastLocationData;
            }
        });

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        myToolbar = binding.myToolbar;
        setSupportActionBar(myToolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        assert navHostFragment != null;
        NavigationUI.setupWithNavController(myToolbar, navHostFragment.getNavController());

        bottomNav = binding.bottomNav;
        NavigationUI.setupWithNavController(bottomNav, navHostFragment.getNavController());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent myIntent = new Intent(this, MyLocationService.class);
        service = startService(myIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(myLocationReceiver, myBroadcastFilter);
        if (this.requestingLocationUpdates) {
            verifyFineLocationPermissions();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (myLocationReceiver!=null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(myLocationReceiver);
    }

    @Override
    protected void onDestroy() {
        Intent myIntent = new Intent(MainActivity.this, MyLocationService.class);
        stopService(myIntent);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(this, MyLocationService.class));
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("requestingLocationUpdates", requestingLocationUpdates);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.keySet().contains("requestingLocationUpdates")) {
            this.requestingLocationUpdates = savedInstanceState.getBoolean("requestingLocationUpdates");
        } else {
            this.requestingLocationUpdates = false;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_weather:
                showWeatherDialog();
                break;
            case R.id.menu_track:
                Toast.makeText(this, "Start tracking!", Toast.LENGTH_SHORT).show();
                verifyFineLocationPermissions();
                Intent myIntent3 = new Intent(this, MyLocationService.class);
                service = startService(myIntent3);
                break;
            case R.id.menu_stop:
                Toast.makeText(this, "Stop tracking!", Toast.LENGTH_SHORT).show();
                requestingLocationUpdates = false;
                Intent myIntent = new Intent(MainActivity.this, MyLocationService.class);
                stopService(myIntent);
                break;
            case R.id.menu_pause:
                Toast.makeText(this, "Pause tracking!", Toast.LENGTH_SHORT).show();
                requestingLocationUpdates = false;
                Intent myIntent1 = new Intent(MainActivity.this, MyLocationService.class);
                stopService(myIntent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void verifyFineLocationPermissions() {
        int locationPermissionFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (locationPermissionFine != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUIRED_SDK_PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            verifyLocationUpdatesRequirements();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //Kalles når bruker har akseptert og gitt tillatelse til bruk av posisjon:
            case REQUEST_CHECK_SETTINGS:
                verifyFineLocationPermissions();
                return;
        }
    }

    protected void checkPermissions() {
        //https://developer.here.com/documentation/android-premium/3.17/dev_guide/topics/request-android-permissions.html
        final List<String> missingPermissions = new ArrayList<String>();

        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Endret, https://developer.here.com/documentation/android-premium/3.17/dev_guide/topics/request-android-permissions.html
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults.length > 0 && permissions.length == grantResults.length) {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Ønsket tillatelse \n'" + permissions[i]
                                + "' \nakseptert", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Ønsket tillatelse \n'" + permissions[i]
                                + "' \nIKKE akseptert", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    public boolean hasPermissions(String... permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void showWeatherDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Light_DarkActionBar);
        View view = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.weather_dialog, (ConstraintLayout) findViewById(R.id.weatherDialog)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.tvWeatherTitle)).setText("Værmelding: " + "\n" + "sol sol sol");
        ((ImageView) view.findViewById(R.id.ivWeatherInfo)).setImageResource(R.drawable.addtourpin);
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.btnWeatherDsm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    public static void hideBottomNav() {
        bottomNav.setVisibility(View.GONE);
    }

    public static void hideTopNav() {
        myToolbar.setVisibility(View.GONE);
    }

    public static void showBottomNav() {
        bottomNav.setVisibility(View.VISIBLE);
    }

    public static void showTopNav() {
        myToolbar.setVisibility(View.VISIBLE);
    }

    private void verifyLocationUpdatesRequirements() {
        final LocationRequest locationRequest = createLocationRequest();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Intent myIntent = new Intent(MainActivity.this, MyLocationService.class);
                startForegroundService(myIntent);
                requestingLocationUpdates = true;
            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    public static LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
}