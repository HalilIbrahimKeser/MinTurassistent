package com.aphex.minturassistent;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.aphex.minturassistent.Entities.Trip;
import com.aphex.minturassistent.databinding.FragmentPlanTourBinding;
import com.aphex.minturassistent.viewmodel.ViewModel;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static androidx.core.content.res.ResourcesCompat.getDrawable;


public class PlanTourFragment extends Fragment implements MapEventsReceiver {
    Location mLastLocation;
    MapView mMapView;
    MyLocationNewOverlay mLocationOverlay;
    MapEventsOverlay mMapEventsOverlay;
    GeoPoint clickLocation;
    Road road;
    RoadManager roadManager;
    Button buttonPop;
    Button btSaveTour;
    ArrayList<Marker> markers = new ArrayList<>();
    ArrayList<Polyline> polys = new ArrayList<>();
    CompassOverlay mCompassOverlay;
    ViewModel viewModel;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    String tourType;
    Double startLatitude;
    Double startLongitude;
    Double stoppLatitude;
    Double stoppLongitude;
    Trip currentTrip;

    public PlanTourFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
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

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentPlanTourBinding binding = FragmentPlanTourBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        viewModel.getLastTourType().observe(getActivity(), trip -> {
            tourType = trip.get(0).mTourType;
        });

        mMapView = binding.map;
        buttonPop = binding.buttonPopupMenu;
        btSaveTour = binding.btSaveTour;

        btSaveTour.setOnClickListener(v -> {
            Trip tempTrip = viewModel.getCurrentTrip().getValue();
            assert tempTrip != null;
            if (tempTrip.getmTripID() == 0) {
                viewModel.insertTrip(tempTrip);
                updateStartStopGeo(tempTrip);
            } else {
                viewModel.getLastTourType().observe(getActivity(), trip -> {
                    currentTrip = trip.get(0);
                });
                Toast.makeText(getContext(), "Tour allerede lagret", Toast.LENGTH_SHORT).show();
                updateStartStopGeo(tempTrip);
            }
            Navigation.findNavController(requireView()).navigate(R.id.myToursFragment);
        });

        mMapView.setMultiTouchControls(true);

        mLocationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(inflater.getContext()), mMapView);
        mLocationOverlay.enableMyLocation();
        mMapView.getController().zoomTo(18.0);

        mMapView.getOverlays().add(mLocationOverlay);

        getAddress();
        if (mLocationOverlay.getMyLocation() != null) {
            mMapView.getController().animateTo(mLocationOverlay.getMyLocation());
        }
        else {
            clickLocation = new GeoPoint(62.4577, 6.1305);
            mMapView.getController().animateTo(clickLocation);
        }

        mCompassOverlay = new CompassOverlay(inflater.getContext(),
                new InternalCompassOrientationProvider(inflater.getContext()), mMapView);
        mCompassOverlay.enableCompass();
        mMapView.getOverlays().add(mCompassOverlay);

        //Add "listener" to map, so you can set a marker where you want..
        mMapEventsOverlay = new MapEventsOverlay(this);
        mMapView.getOverlays().add(0, mMapEventsOverlay);

        SearchView test = binding.searchField;
        test.setBackgroundColor(ContextCompat.getColor(inflater.getContext(), R.color.white));

        return binding.getRoot();
    }

    private void updateStartStopGeo (Trip tempTrip) {
        if(markers.get(0).getPosition().getLatitude() != 0 && markers.get(1).getPosition().getLatitude() != 0
                && markers.get(0).getPosition().getLongitude() != 0 && markers.get(1).getPosition().getLongitude() != 0) {
            startLatitude = markers.get(0).getPosition().getLatitude();
            startLongitude = markers.get(0).getPosition().getLongitude();
            stoppLatitude = markers.get(1).getPosition().getLatitude();
            stoppLongitude = markers.get(1).getPosition().getLongitude();
        }else {
            startLatitude = 0.0;
            startLongitude = 0.0;
            stoppLatitude = 0.0;
            stoppLongitude = 0.0;
        }
        Trip.StartGeo startGeo = new Trip.StartGeo(startLatitude, startLongitude);
        tempTrip.setStartGeo(startGeo);
        Trip.StopGeo stopGeo = new Trip.StopGeo(stoppLatitude, stoppLongitude);
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
    }

    private void getAddress() {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = null;
            addresses = geocoder.getFromLocation(62.457781, 6.130721,1);
            addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getRoute() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                roadManager = new OSRMRoadManager(getActivity(), "MinTurassistent");
                switch (tourType) {
                    case "Gåtur":
                    case "Skitur":
                        ((OSRMRoadManager) roadManager).setMean(OSRMRoadManager.MEAN_BY_FOOT);
                        break;
                    case "Sykkeltur":
                        ((OSRMRoadManager) roadManager).setMean(OSRMRoadManager.MEAN_BY_BIKE);
                        break;
                }
                ArrayList<GeoPoint> temp = new ArrayList<>();
                for (Marker m : markers) {
                    temp.add(new GeoPoint(m.getPosition().getLatitude(), m.getPosition().getLongitude()));
                }
                try {
                    road = roadManager.getRoad(temp);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (road.mStatus != Road.STATUS_OK) {
                            Toast.makeText(getActivity(), "Kunne ikke sette opp rute.", Toast.LENGTH_SHORT).show();
                        }
                        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
                        polys.add(roadOverlay);
                        mMapView.getOverlays().add(roadOverlay);
                        mMapView.invalidate();
                    }
                });
            }
        });
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        clickLocation = new GeoPoint(p.getLatitude(), p.getLongitude());
        if (markers.size() <= 1) {
            setMarker(clickLocation);
            if (markers.size() == 2) {
                getRoute();
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
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_delete:
                    for (Marker m : markers) {
                        mMapView.getOverlays().remove(m);
                        mMapView.invalidate();
                    }
                    try {
                        markers.remove(1);
                        markers.remove(0);
                        mMapView.getOverlays().remove(polys.get(0));
                        polys.remove(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mMapView.invalidate();
                    return true;
                case R.id.menu_viapoint:

                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
        return false;
    }

}

/*
      executor.execute(new Runnable() {
            @Override
            public void run() {

                //Background work here

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        //UI Thread work here
                    }
                });
            }
        });
 */