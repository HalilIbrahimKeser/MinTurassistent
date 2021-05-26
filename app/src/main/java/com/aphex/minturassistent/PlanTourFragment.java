package com.aphex.minturassistent;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.aphex.minturassistent.Entities.Location;
import com.aphex.minturassistent.Entities.Trip;
import com.aphex.minturassistent.databinding.FragmentPlanTourBinding;
import com.aphex.minturassistent.viewmodel.ViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;


import org.jetbrains.annotations.NotNull;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.location.OverpassAPIProvider;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.TileSystem;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Objects;

import static androidx.core.content.res.ResourcesCompat.getDrawable;


public class PlanTourFragment extends Fragment implements MapEventsReceiver {
    MapView mMapView;
    MyLocationNewOverlay mLocationOverlay;
    MapEventsOverlay mMapEventsOverlay;
    GeoPoint clickLocation;
    Road road;
    Button buttonPop;
    FloatingActionButton btSaveTour;
    ArrayList<Marker> markers = new ArrayList<>();
    CompassOverlay mCompassOverlay;
    ViewModel viewModel;
    private FusedLocationProviderClient fusedLocationClient;

    String tourType;
    private double startPosLat, startPosLon, stopPosLat, stopPosLon;
    GeoPoint startLocation;

    public PlanTourFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
        Configuration.getInstance().setUserAgentValue("MinturAssistent");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentPlanTourBinding binding = FragmentPlanTourBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        viewModel.getLastTourType().observe(getActivity(), trip -> {
            tourType = trip.get(0).mTourType;
        });

        Toast.makeText(getContext(), "Velg rute, så lagre", Toast.LENGTH_SHORT).show();

        mMapView = binding.map;
        buttonPop = binding.buttonPopupMenu;
        btSaveTour = binding.btSaveTour;

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        startLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                        mMapView.getController().animateTo(startLocation);
                    }
                });

        btSaveTour.setOnClickListener(v -> {
            Trip tempTrip = viewModel.getCurrentTrip().getValue();
            assert tempTrip != null;
            if (tempTrip.getmTripID() == 0) {
                @SuppressLint("DefaultLocale") String str = String.format("%.2f", startLocation.getLatitude()) + ", " + String.format("%.2f", startLocation.getLongitude());
                tempTrip.setmPlace(str);
                updateStartStopGeo(tempTrip);
                Toast.makeText(getContext(), "Tur lagret", Toast.LENGTH_SHORT).show();
                updateStartStopGeo(tempTrip);
                viewModel.insertTrip(tempTrip);
            }
            Navigation.findNavController(requireView()).navigate(R.id.myToursFragment);
        });

        mMapView.setMultiTouchControls(true);

        mLocationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(inflater.getContext()), mMapView);
        mLocationOverlay.enableMyLocation();
        mMapView.setMinZoomLevel(3.0);
        mMapView.setMaxZoomLevel(21.0);
        mMapView.getController().zoomTo(18.0);
        mMapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);

        mMapView.getOverlays().add(mLocationOverlay);
        mCompassOverlay = new CompassOverlay(inflater.getContext(),
                new InternalCompassOrientationProvider(inflater.getContext()), mMapView);
        mCompassOverlay.enableCompass();
        mMapView.getOverlays().add(mCompassOverlay);

        mMapEventsOverlay = new MapEventsOverlay(this);
        mMapView.getOverlays().add(0, mMapEventsOverlay);

        return binding.getRoot();
    }

    private void updateStartStopGeo(Trip tempTrip) {
        if (markers.get(0).getPosition().getLatitude() != 0 && markers.get(1).getPosition().getLatitude() != 0
                && markers.get(0).getPosition().getLongitude() != 0 && markers.get(1).getPosition().getLongitude() != 0) {
            startPosLat = markers.get(0).getPosition().getLatitude();
            startPosLon = markers.get(0).getPosition().getLongitude();
            stopPosLat = markers.get(1).getPosition().getLatitude();
            stopPosLon = markers.get(1).getPosition().getLongitude();
        } else {
            startPosLat = 0.0;
            startPosLon = 0.0;
            stopPosLat = 0.0;
            stopPosLon = 0.0;
        }
        Trip.StartGeo startGeo = new Trip.StartGeo(startPosLat, startPosLon);
        tempTrip.setStartGeo(startGeo);
        Trip.StopGeo stopGeo = new Trip.StopGeo(stopPosLat, stopPosLon);
        tempTrip.setStopGeo(stopGeo);
    }

    private void setMarker(GeoPoint clickedPoint) {
        Marker startMarker = new Marker(mMapView);
        startMarker.setIcon(getDrawable(getResources(), R.drawable.placeholder, null));
        startMarker.setPosition(clickedPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlays().add(startMarker);
        mMapView.invalidate();
        markers.add(startMarker);
        new UpdateRoadTask().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class UpdateRoadTask extends AsyncTask<Object, Void, Road> {
        protected Road doInBackground(Object... params) {
            @SuppressWarnings("unchecked")
            RoadManager roadManager = new OSRMRoadManager(getActivity(), "Roads");
            switch (tourType) {
                case "Gåtur":
                    ((OSRMRoadManager) roadManager).setMean(OSRMRoadManager.MEAN_BY_FOOT);
                    break;
                case "Sykkeltur":
                    ((OSRMRoadManager) roadManager).setMean(OSRMRoadManager.MEAN_BY_BIKE);
                    break;
                case "Skitur":
                    ((OSRMRoadManager) roadManager).setMean(OSRMRoadManager.MEAN_BY_CAR);
                    break;
            }
            ArrayList<GeoPoint> temp = new ArrayList<>();
            for (Marker m : markers) {
                temp.add(new GeoPoint(m.getPosition().getLatitude(), m.getPosition().getLongitude()));
            }
            return roadManager.getRoad(temp);
        }

        @Override
        protected void onPostExecute(Road result) {
            road = result;
            Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
            mMapView.getOverlays().add(0, roadOverlay);
            mMapView.invalidate();
        }
    }

    private class UpdateKMLTask extends AsyncTask<Object, Void, KmlDocument> {
        protected KmlDocument doInBackground(Object... params) {
            OverpassAPIProvider overpassProvider = new OverpassAPIProvider();
            String url = overpassProvider.urlForTagSearchKml("highway=path", mMapView.getBoundingBox(), 30, 30);
            KmlDocument kmlDocument = new KmlDocument();
            boolean ok = overpassProvider.addInKmlFolder(kmlDocument.mKmlRoot, url);
            return kmlDocument;
        }
        @Override
        protected void onPostExecute(KmlDocument result) {
            FolderOverlay kmlOverlay = (FolderOverlay) result.mKmlRoot.buildOverlay(mMapView, null, null, result);
            mMapView.getOverlays().add(kmlOverlay);
        }
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        clickLocation = new GeoPoint(p.getLatitude(), p.getLongitude());
        if (markers.size() <= 1) {
            setMarker(clickLocation);
            if (markers.size() == 2) {
                Toast.makeText(getActivity(), "Setter opp rute..", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Langklikk for å slette eller sette opp viapunkt.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), buttonPop);
        popupMenu.getMenuInflater().inflate(R.menu.map_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        for (Marker m : markers) {
                            mMapView.getOverlays().remove(m);
                            mMapView.invalidate();
                        }
                        try {
                            markers.remove(1);
                            markers.remove(0);
                            mMapView.getOverlays().remove(0);
                            mMapView.invalidate();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mMapView.invalidate();
                        return true;
                    case R.id.menu_viapoint:
                        return true;
                    case R.id.menu_getRoute:
                        new UpdateKMLTask().execute();
                        return true;

                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
        return false;
    }
}
//Eksempel på viapoints: https://github.com/MKergall/osmbonuspack/blob/master/OSMNavigator/src/main/java/com/osmnavigator/MapActivity.java