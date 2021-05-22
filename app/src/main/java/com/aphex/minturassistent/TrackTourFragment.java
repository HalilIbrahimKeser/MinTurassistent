package com.aphex.minturassistent;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.aphex.minturassistent.Entities.Trip;
import com.aphex.minturassistent.databinding.FragmentTrackTourBinding;
import com.aphex.minturassistent.db.Dao;
import com.aphex.minturassistent.db.RoomDatabase;
import com.aphex.minturassistent.viewmodel.Repository;
import com.aphex.minturassistent.viewmodel.ViewModel;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;

import static androidx.core.content.res.ResourcesCompat.getDrawable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class TrackTourFragment extends Fragment {
    MapView mMapView;
    MyLocationNewOverlay mLocationOverlay;
    private int mTripID;
    private double startPosLat;
    private double startPosLon;
    private double stopPosLat;
    private double stopPosLon;
    private static final int CALLBACK_ALL_PERMISSIONS = 1;
    private static final int REQUEST_CHECK_SETTINGS = 10;
    private Location previousLocation=null;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private Polyline mPolyline;
    // Indikerer om servicen er startet eller stoppet:
    private boolean requestingLocationUpdates = false;
    GeoPoint geoPoint;
    ImageView imgKamera;
    FloatingActionButton btnCamera;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    public TrackTourFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        MainActivity.showTopNav();
        MainActivity.hideBottomNav();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onStop() {
        super.onStop();
        MainActivity.hideTopNav();
        MainActivity.showBottomNav();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getContext().getSharedPreferences("tripID", 0);
        mTripID = prefs.getInt("tripID", -1);
        startPosLat = prefs.getFloat("startgeolat", 0);
        startPosLon = prefs.getFloat("startgeolon", 0);
        stopPosLon = prefs.getFloat("stopgeolon", 0);
        stopPosLat = prefs.getFloat("stopgeolat", 0);

        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        //Callback for å fange opp LOKASJONsendringer:
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NotNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                StringBuilder locationBuffer = new StringBuilder();
                for (Location location : locationResult.getLocations()) {
                    // Beregner avstand fra forrisge veipunkt:
                    if (previousLocation==null)
                        previousLocation = location;
                    float distance = previousLocation.distanceTo(location);
                    Log.d("MY-LOCATION-DISTANCE", String.valueOf(distance));
                    Log.d("MY-LOCATION", location.toString());
                    if (distance>50) {
                        Log.d("MY-LOCATION", "MER ENN 50 METER!!");
                    }
                    previousLocation = location;

                    locationBuffer.append(location.getLatitude()).append(", ").append(location.getLongitude()).append("\n");
                    geoPoint = new GeoPoint(location.getLatitude() , location.getLongitude());
                    mMapView.getController().setCenter(geoPoint);
                    //mPolyline.addPoint(geoPoint);
                    mMapView.invalidate();
                }
            }
        };
    }

    private void startMap() {
        GeoPoint geoPointStart = new GeoPoint(startPosLat, startPosLon);
        GeoPoint geoPointStop = new GeoPoint(stopPosLat, stopPosLon);
        //Markers
        Marker startMarker = new Marker(mMapView);
        startMarker.setPosition(geoPointStart);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        mMapView.getOverlays().add(startMarker);
        startMarker.setIcon(getDrawable(getResources(), R.drawable.placeholder, null));
        startMarker.setTitle("Start point");

        Marker stopMarker = new Marker(mMapView);
        stopMarker.setPosition(geoPointStop);
        stopMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        mMapView.getOverlays().add(stopMarker);
        stopMarker.setIcon(getDrawable(getResources(), R.drawable.placeholder, null));
        stopMarker.setTitle("Stop point");
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentTrackTourBinding binding = FragmentTrackTourBinding.inflate(inflater, container, false);
        mMapView = binding.trackmap;

        mMapView.setMultiTouchControls(true);

        mLocationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(inflater.getContext()), mMapView);
        mLocationOverlay.enableMyLocation();

        GeoPoint startLocation = new GeoPoint(startPosLat, startPosLon);
        mMapView.getController().animateTo(startLocation);

        mMapView.getController().zoomTo(18.0);
        startMap();


        // Polyline: tegner stien.
        //mPolyline.addPoint(geoPoint);
        btnCamera = binding.btnCamera;
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        imgKamera = binding.imgKamera;

        return binding.getRoot();
    }

    private void stopTracking() {
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = this.createLocationRequest();
        this.fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        requestingLocationUpdates = true;
    }

    // LocationRequest: Setter krav til posisjoneringa:
    public LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        // Hvor ofte ønskes lokasjonsoppdateringer (her: hvert 10.sekund)
        locationRequest.setInterval(5000);
        // Her settes intervallet for hvor raskt appen kan håndtere oppdateringer.
        locationRequest.setFastestInterval(3000);
        // Ulike verderi; Her: høyest mulig nøyaktighet som også normalt betyr bruk av GPS.
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }


    // Verifiserer kravene satt i locationRequest-objektet.
    //   Dersom OK verifiseres fine-location-tillatelse start av lokasjonsforespørsler.
    private void initLocationUpdates() {
        final LocationRequest locationRequest = this.createLocationRequest();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        // NB! Sjekker om kravene satt i locationRequest kan oppfylles:
        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // Alle lokasjopnsinnstillinger er OK, klienten kan nå initiere lokasjonsforespørsler her:
                startLocationUpdates();
            }
        });
        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Lokasjopnsinnstillinger er IKKE OK, men det kan fikses ved å vise brukeren en dialog!!
                    try {
                        // Viser dialogen ved å kalle startResolutionForResult() OG SJEKKE resultatet i onActivityResult()
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    private void dispatchTakePictureIntent() {
        //https://developer.android.com/training/camera/photobasics
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgKamera.setImageBitmap(imageBitmap);
        }
    }
}