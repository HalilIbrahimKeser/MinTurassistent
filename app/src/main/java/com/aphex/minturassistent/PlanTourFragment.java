package com.aphex.minturassistent;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

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

import static androidx.core.content.res.ResourcesCompat.getDrawable;


public class PlanTourFragment extends Fragment implements MapEventsReceiver {
    FusedLocationProviderClient mFusedLocationClient;
    Location mLastLocation;
    MapView mMapView;
    MyLocationNewOverlay mLocationOverlay;
    MapEventsOverlay mMapEventsOverlay;
    GeoPoint clickLocation;
    RoadManager roadManager;
    Button buttonPop;
    ArrayList<Marker> markers = new ArrayList<>();
    ArrayList<Polyline> polys = new ArrayList<>();

    public PlanTourFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_plan_tour, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mMapView = view.findViewById(R.id.map);
        buttonPop = view.findViewById(R.id.buttonPopupMenu);
        roadManager = new OSRMRoadManager(inflater.getContext(), "MinTurassistent");

        mMapView.setMultiTouchControls(true);

        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(inflater.getContext()),mMapView);
        mLocationOverlay.enableMyLocation();
        mMapView.getOverlays().add(mLocationOverlay);

        getAddress();

        if (mLocationOverlay.getMyLocation() != null) {
            mMapView.getController().animateTo(mLocationOverlay.getMyLocation());
        }
        else {
            clickLocation = new GeoPoint(62.4577, 6.1305);
            mMapView.getController().animateTo(clickLocation);
            mMapView.getController().zoomTo(18.0);
        }

        CompassOverlay mCompassOverlay = new CompassOverlay(inflater.getContext(), new InternalCompassOrientationProvider(inflater.getContext()),mMapView);
        mCompassOverlay.enableCompass();
        mMapView.getOverlays().add(mCompassOverlay);

        //Add "listener" to map, so you can set a marker where you want..
        mMapEventsOverlay = new MapEventsOverlay(this);
        mMapView.getOverlays().add(0, mMapEventsOverlay);

        SearchView test = view.findViewById(R.id.searchField);
        test.setBackgroundColor(ContextCompat.getColor(inflater.getContext(), R.color.white));
        return view;
    }

    private void setMarker(GeoPoint clickedPoint) {
        Marker startMarker = new Marker(mMapView);
        startMarker.setIcon(getDrawable(getResources(), R.drawable.placeholder, null));
        //startMarker.setTitle("Start point");

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
        ArrayList<GeoPoint> temp = new ArrayList<>();
        for (Marker m : markers) {
            temp.add(new GeoPoint(m.getPosition().getLatitude(), m.getPosition().getLongitude()));
        }
        Road road = roadManager.getRoad(temp);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        polys.add(roadOverlay);
        mMapView.getOverlays().add(roadOverlay);
        mMapView.invalidate();
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
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        for (Marker m : markers) {
                            mMapView.getOverlays().remove(m);
                            mMapView.invalidate();
                        }
                        markers.remove(1);
                        markers.remove(0);
                        mMapView.getOverlays().remove(polys.get(0));
                        polys.remove(0);
                        mMapView.invalidate();
                        return true;
                    case R.id.menu_viapoint:
                        
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