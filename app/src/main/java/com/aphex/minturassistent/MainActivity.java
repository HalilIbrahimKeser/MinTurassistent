package com.aphex.minturassistent;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.aphex.minturassistent.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.config.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FusedLocationProviderClient mFusedLocationClient;
    Location mLastLocation;
    private static BottomNavigationView bottomNav;
    //private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    //private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
    //       Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_MEDIA_LOCATION };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        ActivityMainBinding binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.getRoot());

        //checkPermissions();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        bottomNav = binding.bottomNav;
        assert navHostFragment != null;
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

    /**
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            for (int index = permissions.length - 1; index >= 0; --index) {
                if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Ønsket tillatelse '" + permissions[index]
                            + "' ikke gitt", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }
            Toast.makeText(this, "Ønsket tillatelse ikke gitt", Toast.LENGTH_LONG).show();
            getLocation();
        }
    }
     **/
    //Tatt fra: https://developer.android.com/codelabs/advanced-android-training-device-location?index=..%2F..advanced-android-training#3
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && permissions.length == grantResults.length) {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this,
                                R.string.location_permission_allowed,
                                Toast.LENGTH_SHORT).show();
                        getLocation();
                    } else {
                        Toast.makeText(this,
                                R.string.location_permission_denied,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    //Tatt fra: https://developer.android.com/codelabs/advanced-android-training-device-location?index=..%2F..advanced-android-training#3
    public void getLocation() {
        //Todo legg inn if SDK version høyere enn 29 så koden nedenfor, hvis ikke kun loacation permission

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_MEDIA_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(
                    (Location location) -> {
                        if (location != null) {
                            mLastLocation = location;
                        } else {
                            //mLocationTextView.setText(R.string.no_location);
                        }
                    });
        }
    }
}