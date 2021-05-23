package com.aphex.minturassistent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.aphex.minturassistent.Entities.Trip;
import com.aphex.minturassistent.databinding.FragmentDetailsBinding;
import com.aphex.minturassistent.viewmodel.ViewModel;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

import static androidx.core.content.res.ResourcesCompat.getDrawable;

public class DetailsFragment extends Fragment {
    private ViewModel mViewModel;
    public int mTripID;
    public TextView tvTourTittel;
    public TextView tvTimeSpent;
    public TextView tvTripDate;

    MapView mMapView;
    private GeoPoint startPoint;
    private GeoPoint stopPoint;

    public DetailsFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.hideTopNav();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentDetailsBinding binding = FragmentDetailsBinding.inflate(inflater, container, false);

        mMapView = binding.mapDetails;
        mMapView.getTileProvider().getTileCache().getProtectedTileComputers().clear();

        tvTourTittel = binding.tvTourTittel;
        tvTimeSpent = binding.tvTimeSpent;
        tvTripDate = binding.tvTripDate;

        SharedPreferences prefs = getContext().getSharedPreferences("tripID", 0);
        mTripID = prefs.getInt("tripID", -1);

        mViewModel = new ViewModelProvider(this).get(ViewModel.class);
        mViewModel.getSingleTrip(mTripID).observe(getViewLifecycleOwner(), new Observer<List<Trip>>() {
            @Override
            public void onChanged(@Nullable final List<Trip> tripData) {
                tvTourTittel.setText(tripData.get(0).getmTripName());
                tvTimeSpent.setText(tripData.get(0).getmTimeSpent());
                tvTripDate.setText(tripData.get(0).getmDate());
                startPoint = new GeoPoint(tripData.get(0).startGeo.latitude, tripData.get(0).startGeo.longitude);
                stopPoint = new GeoPoint(tripData.get(0).stopGeo.latitude1, tripData.get(0).stopGeo.longitude1);
                mapWorks();
            }
        });

        Button btnImages = binding.btnImages;
        btnImages.setOnClickListener(view ->
                Navigation.findNavController(getView()).navigate(R.id.storedImagesFragment));

        Button btnDeleteTrip = binding.btnDeleteTrip;
        btnDeleteTrip.setOnClickListener(view -> {
            mViewModel.deleteTrip(mTripID);
            Navigation.findNavController(getView()).navigate(R.id.myToursFragment);
        });

        return binding.getRoot();
    }

    private void mapWorks() {
        mMapView.setMinZoomLevel(3.0);
        mMapView.setMaxZoomLevel(21.0);
        mMapView.getController().zoomTo(14.0);
        mMapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        mMapView.getController().animateTo(startPoint);

        Marker startMarker = new Marker(mMapView);
        startMarker.setIcon(getDrawable(getResources(), R.drawable.placeholder_green, null));
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlays().add(startMarker);

        Marker stopMarker = new Marker(mMapView);
        stopMarker.setIcon(getDrawable(getResources(), R.drawable.placeholder, null));
        stopMarker.setPosition(stopPoint);
        stopMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlays().add(stopMarker);

        mMapView.invalidate();
    }
}