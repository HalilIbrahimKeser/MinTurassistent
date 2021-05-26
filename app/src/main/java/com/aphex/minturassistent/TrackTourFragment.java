package com.aphex.minturassistent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.aphex.minturassistent.Entities.Images;
import com.aphex.minturassistent.databinding.FragmentTrackTourBinding;
import com.aphex.minturassistent.viewmodel.ViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.res.ResourcesCompat.getDrawable;

public class TrackTourFragment extends Fragment {
    //Trackingen er basert på location 4 eksempelet til Werner.
    MapView mMapView;
    private FusedLocationProviderClient fusedLocationClient;
    private ViewModel mViewModel;

    private int tripID;
    private double startPosLat;
    private double startPosLon;
    private double stopPosLat;
    private double stopPosLon;
    private double imageLat;
    private double imageLon;
    private Polyline mPolyline;

    ImageView imgKamera;
    FloatingActionButton btnCamera;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;
    Uri photoURI;
    String imageUri;
    String imageFileName;

    private Location currentLocation;
    String currentPhotoPath;
    private MyLocationNewOverlay mLocationOverlay;
    private LocationManager lm;
    private List<Images> mediaList = new ArrayList<Images>();
    private File photoFile = null;

    FragmentTrackTourBinding binding;

    public TrackTourFragment() {
    }

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
        tripID = prefs.getInt("tripID", 0);
        startPosLat = prefs.getFloat("startgeolat", 0);
        startPosLon = prefs.getFloat("startgeolon", 0);
        stopPosLon = prefs.getFloat("stopgeolon", 0);
        stopPosLat = prefs.getFloat("stopgeolat", 0);

        SharedPreferences prefs1 = requireContext().getSharedPreferences("positionForImage", 0);
        imageLat = prefs1.getFloat("lat", -1);
        imageLon = prefs1.getFloat("lon", -1);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTrackTourBinding.inflate(inflater, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);

        mMapView = binding.trackmap;

        mMapView.setMultiTouchControls(true);

        mMapView.setMinZoomLevel(3.0);
        mMapView.setMaxZoomLevel(21.0);
        mMapView.getController().zoomTo(17.0);
        mMapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);

        mediaList = new ArrayList<Images>();

        startMap();

        btnCamera = binding.btnCamera;
        btnCamera.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageUri = String.valueOf(photoURI);
            Images imageData = new Images(tripID, imageFileName, imageUri, imageLat, imageLon);
            mediaList.add(imageData);
            mViewModel.mediaData.setValue(mediaList);
            mViewModel.insertImage(imageData);
        }
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        String currentDateandTime = sdf.format(new Date());
        imageFileName = "MinTur_" + currentDateandTime;
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getActivity()), mMapView);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mMapView.getOverlays().add(this.mLocationOverlay);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mediaList.clear();
    }
}