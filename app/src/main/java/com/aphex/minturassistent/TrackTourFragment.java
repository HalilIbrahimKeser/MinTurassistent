package com.aphex.minturassistent;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.annotation.Nullable;
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
import com.aphex.minturassistent.service.MyLocationService;
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
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.advancedpolyline.MonochromaticPaintList;
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
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class TrackTourFragment extends Fragment {
    //Trackingen er basert på location 4 eksempelet til Werner.
    MapView mMapView;
    private FusedLocationProviderClient fusedLocationClient;

    private double startPosLat;
    private double startPosLon;
    private double stopPosLat;
    private double stopPosLon;

    ImageView imgKamera;
    FloatingActionButton btnCamera;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private MyLocationNewOverlay mLocationOverlay;

    public TrackTourFragment() {
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (((MainActivity) getActivity()).isRequestingLocationUpdates()) {
            ((MainActivity) getActivity()).verifyLocationUpdatesRequirements();
        }
        MainActivity.showTopNav();
        MainActivity.hideBottomNav();
        requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        MainActivity.hideTopNav();
        MainActivity.showBottomNav();
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
        Configuration.getInstance().setUserAgentValue("MinturAssistent");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        SharedPreferences prefs = requireContext().getSharedPreferences("tripID", 0);
        startPosLat = prefs.getFloat("startgeolat", 0);
        startPosLon = prefs.getFloat("startgeolon", 0);
        stopPosLon = prefs.getFloat("stopgeolon", 0);
        stopPosLat = prefs.getFloat("stopgeolat", 0);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentTrackTourBinding binding = FragmentTrackTourBinding.inflate(inflater, container, false);
        mMapView = binding.trackmap;

        mMapView.setMultiTouchControls(true);

        mMapView.setMinZoomLevel(3.0);
        mMapView.setMaxZoomLevel(21.0);
        mMapView.getController().zoomTo(17.0);
        mMapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);

        startMap();

        btnCamera = binding.btnCamera;
        btnCamera.setOnClickListener(v -> dispatchTakePictureIntent());
        imgKamera = binding.imgKamera;

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getActivity()), mMapView);
        mMapView.getOverlays().add(this.mLocationOverlay);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();

    }

    @SuppressLint("MissingPermission")
    private void startMap() {
        GeoPoint geoPointStart = new GeoPoint(startPosLat, startPosLon);
        GeoPoint geoPointStop = new GeoPoint(stopPosLat, stopPosLon);

        mMapView.getController().setCenter(geoPointStart);
        mMapView.getController().animateTo(geoPointStart);

        //Markers
        Marker startMarker = new Marker(mMapView);
        startMarker.setPosition(geoPointStart);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        mMapView.getOverlays().add(startMarker);
        startMarker.setIcon(getDrawable(getResources(), R.drawable.placeholder_green, null));
        startMarker.setTitle("Start point");

        Marker stopMarker = new Marker(mMapView);
        stopMarker.setPosition(geoPointStop);
        stopMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        mMapView.getOverlays().add(stopMarker);
        stopMarker.setIcon(getDrawable(getResources(), R.drawable.placeholder, null));
        stopMarker.setTitle("Stop point");
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