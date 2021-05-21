package com.aphex.minturassistent;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.aphex.minturassistent.Entities.Trip;
import com.aphex.minturassistent.databinding.FragmentTrackTourBinding;
import com.aphex.minturassistent.viewmodel.ViewModel;
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
    MapEventsOverlay mMapEventsOverlay;
    GeoPoint clickLocation;
    Road road;
    ArrayList<Marker> markers = new ArrayList<>();
    CompassOverlay mCompassOverlay;
    private ViewModel mViewModel;
    public int mTripID;
    FloatingActionButton btnCamera;
    ImageView imgKamera;
    String tourType;
    Double startLatitude;
    Double startLongitude;
    Double stoppLatitude;
    Double stoppLongitude;
    Trip currentTrip;

    static final int REQUEST_IMAGE_CAPTURE = 1;


    public TrackTourFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentTrackTourBinding binding = FragmentTrackTourBinding.inflate(inflater, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        mViewModel.getLastTourType().observe(getActivity(), trip -> {
            tourType = trip.get(0).mTourType;
        });
        SharedPreferences prefs = getContext().getSharedPreferences("tripID", 0);
        mTripID = prefs.getInt("tripID", -1);

        mMapView = binding.map;

        mMapView.setMultiTouchControls(true);

        mLocationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(inflater.getContext()), mMapView);
        mLocationOverlay.enableMyLocation();
        mMapView.getController().zoomTo(16.0);

        mMapView.getOverlays().add(mLocationOverlay);

        if (mLocationOverlay.getMyLocation() != null) {
            mMapView.getController().animateTo(mLocationOverlay.getMyLocation());
        } else {
            clickLocation = new GeoPoint(62.4577, 6.1305);
            mMapView.getController().animateTo(clickLocation);
        }

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