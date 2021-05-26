package com.aphex.minturassistent;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.aphex.minturassistent.databinding.ActivityMainBinding;
import com.aphex.minturassistent.service.MyLocationService;
import com.aphex.minturassistent.viewmodel.Repository;
import com.aphex.minturassistent.viewmodel.ViewModel;
import com.google.android.gms.common.api.ResolvableApiException;
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
    ActivityMainBinding binding;
    private static final int REQUEST_CHECK_SETTINGS = 10;
    private static final int CALLBACK_REQUEST_FOREGROUND_SERVICE_PERMISSION = 1;
    private static BottomNavigationView bottomNav;
    private static Toolbar myToolbar;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 2;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_MEDIA_LOCATION, Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    };


    private boolean requestingLocationUpdates = false;
    private static String[] requiredPermissions = {
            Manifest.permission.FOREGROUND_SERVICE
    };
    private ViewModel mViewModel;
    private NavController navController;
    private Repository mRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.getRoot());

        checkPermissions();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        myToolbar = binding.myToolbar;
        setSupportActionBar(myToolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        assert navHostFragment != null;
        NavigationUI.setupWithNavController(myToolbar, navHostFragment.getNavController());

        bottomNav = binding.bottomNav;
        NavigationUI.setupWithNavController(bottomNav, navHostFragment.getNavController());

        mRepository = new Repository(getApplication());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ved skjermrotasjon må vi starte servicen på nytt (siden vi stopper servicen i onDestroy()):
        if (this.requestingLocationUpdates) {
            verifyFineLocationPermissions();
        }
    }

    @Override
    protected void onDestroy() {
        //Sikrer at servicen stopper når appen avslutter.
        Intent myIntent = new Intent(MainActivity.this, MyLocationService.class);
        stopService(myIntent);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
                showWeatherDialog(this);
                break;
            case R.id.menu_track:
                Toast.makeText(this, "Starter tracking!", Toast.LENGTH_SHORT).show();
                verifyFineLocationPermissions();
                break;
            case R.id.menu_stop:
                Toast.makeText(this, "Stopper tracking!", Toast.LENGTH_SHORT).show();
                requestingLocationUpdates = false;
                Intent myIntent = new Intent(MainActivity.this, MyLocationService.class);
                stopService(myIntent);
                SharedPreferences prefs1 = this.getSharedPreferences("weather", Context.MODE_PRIVATE);
                int mTripID = prefs1.getInt("tripID", -1);
                mRepository.updateIsFinished(mTripID);
                if (!navController.popBackStack()) {
                    finish();
                }
                Toast.makeText(this, "Tur avsluttet!", Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_pause:
                Toast.makeText(this, "Pauser tracking!", Toast.LENGTH_SHORT).show();
                //TODO
                //requestingLocationUpdates = false;
                //Intent myIntent1 = new Intent(MainActivity.this, MyLocationService.class);
                //stopService(myIntent1);
                break;
        }
        return super.onOptionsItemSelected(item);
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
            final String[] permissions = missingPermissions.toArray(new String[0]);
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
                       //akseptert
                    } else {
                       //ikke akseptert
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

    @SuppressLint("SetTextI18n")
    private void showWeatherDialog(Context context) {
        //hentet mye kode fra https://www.youtube.com/watch?v=3cJ9eia49w4
        //alertDialog.dismiss() lukker ikke parent ordentlig. Når man åpner dialogen flere ganger må
        //man lukke dialogen tilsvarende mange ganger.

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.weather_dialog, (ConstraintLayout) findViewById(R.id.weatherDialog)
        );
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();

        int mTripID;
        float startLat;
        float startLon;
        SharedPreferences prefs1 = context.getSharedPreferences("weather", Context.MODE_PRIVATE);
        mTripID = prefs1.getInt("tripID", -1);
        startLat = prefs1.getFloat("startgeolat", 0);
        startLon = prefs1.getFloat("startgeolon", 0);
        String lat = String.valueOf(startLat);
        String lon = String.valueOf(startLon);

        mViewModel = new ViewModelProvider(this).get(ViewModel.class);
        mViewModel.downloadMetData(lat, lon).observe(this, MetData -> {
            double airTemp = MetData.properties.timeseries[0].data.instant.details.air_temperature;
            String symbolCode = MetData.properties.timeseries[0].data.next_12_hours.summary.symbol_code;
            String timeStamp = MetData.properties.timeseries[0].time;


        ((TextView) view.findViewById(R.id.tvWeatherTitle)).setText("Værmelding:");
        ((ImageView) view.findViewById(R.id.ivWeatherInfo)).setImageResource(getResources().getIdentifier(symbolCode, "drawable", getPackageName()));
        ((TextView) view.findViewById(R.id.tvTemp)).setText("Temp: " + airTemp + "ºC");
        ((TextView) view.findViewById(R.id.tvTimeStamp)).setText(timeStamp);

        view.findViewById(R.id.btnWeatherDsm).setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

            alertDialog.show();
        });
    }

    /**
     * Sjekker om kravene satt i locationRequest kan oppfylles.
     * Hvis ikke vises en dialog.
     *
     */
    public void verifyLocationUpdatesRequirements() {
        final LocationRequest locationRequest = createLocationRequest();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        // NB! Sjekker om kravene satt i locationRequest kan oppfylles:
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // Starter location dervice!
                Intent myIntent = new Intent(MainActivity.this, MyLocationService.class);
                startForegroundService(myIntent);
                requestingLocationUpdates = true;
            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Lokasjopnsinnstillinger er IKKE OK, men det kan fikses ved å vise brukeren en dialog!!
                    try {
                        // Viser dialogen ved å kalle startResolutionForResult() OG SJEKKE resultatet i onActivityResult()
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    private void verifyFineLocationPermissions() {
        // Kontrollerer om vi har tilgang til ACCESS_FINE_LOCATION:
        int locationPermissionFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (locationPermissionFine != PackageManager.PERMISSION_GRANTED) {
            // Dersom vi ikke har nødvendige tilganger spør bruker om tilgang.
            // Fortsetter i metoden onRequestPermissionsResult() ...\
            ActivityCompat.requestPermissions(this, requiredPermissions, CALLBACK_REQUEST_FOREGROUND_SERVICE_PERMISSION);
        } else {
            // Fortsetter dersom tilgang gitt fra før:
            verifyLocationUpdatesRequirements();
        }
    }
    // LocationRequest: Setter krav til posisjoneringa:
    // Merk: public static, brukes også fra MyLocationService.
    public static LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        // Hvor ofte ønskes lokasjonsoppdateringer (her: hvert 10.sekund)
        locationRequest.setInterval(3000);
        // Her settes intervallet for hvor raskt appen kan håndtere oppdateringer.
        locationRequest.setFastestInterval(2000);
        // Ulike verderi; Her: høyest mulig nøyaktighet som også normalt betyr bruk av GPS.
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
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

    public boolean isRequestingLocationUpdates() {
        return requestingLocationUpdates;
    }
}