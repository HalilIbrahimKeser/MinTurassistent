package com.aphex.minturassistent;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.aphex.minturassistent.databinding.ActivityMainBinding;
import com.aphex.minturassistent.viewmodel.ViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.config.Configuration;

public class MainActivity extends AppCompatActivity {
    FusedLocationProviderClient mFusedLocationClient;
    Location mLastLocation;
    private ActivityMainBinding binding;
    public final int REQUEST_LOCATION_PERMISSION = 1;
    private final int REQUEST_ACCESS_MEDIA_LOCATION = 2;
    private static BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        //BUTTOM NAVIGATION
        bottomNav = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, navHostFragment.getNavController());
    }

    public static void hideBottomNav() {
        bottomNav.setVisibility(View.GONE);
    }

    public static void showBottomNav() {
        bottomNav.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    //Tatt fra: https://developer.android.com/codelabs/advanced-android-training-device-location?index=..%2F..advanced-android-training#3
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(this,
                            R.string.location_permission_denied,
                            Toast.LENGTH_SHORT).show();
                }
            case REQUEST_ACCESS_MEDIA_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                            "Tilgang akseptert",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,
                            "Ingen tilgang til filer",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //Tatt fra: https://developer.android.com/codelabs/advanced-android-training-device-location?index=..%2F..advanced-android-training#3
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_MEDIA_LOCATION},
                    REQUEST_ACCESS_MEDIA_LOCATION);
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mLastLocation = location;

                            } else {
                                //mLocationTextView.setText(R.string.no_location);
                            }
                        }
                    });
        }
    }
}