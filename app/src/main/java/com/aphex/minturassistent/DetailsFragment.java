package com.aphex.minturassistent;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.aphex.minturassistent.Entities.Trip;
import com.aphex.minturassistent.databinding.FragmentDetailsBinding;
import com.aphex.minturassistent.viewmodel.ViewModel;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static androidx.core.content.res.ResourcesCompat.getDrawable;

public class DetailsFragment extends Fragment {
    private ViewModel mViewModel;
    public int mTripID;
    File imagePath;
    FragmentDetailsBinding binding;
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
        binding = FragmentDetailsBinding.inflate(inflater, container, false);

        mMapView = binding.mapDetails;
        mMapView.getTileProvider().getTileCache().getProtectedTileComputers().clear();

        SharedPreferences prefs = getContext().getSharedPreferences("tripID", 0);
        mTripID = prefs.getInt("tripID", -1);

        mViewModel = new ViewModelProvider(this).get(ViewModel.class);
        mViewModel.getSingleTrip(mTripID).observe(getViewLifecycleOwner(), tripData -> {
            binding.tvTourTittel.setText(tripData.get(0).getmTripName());
            binding.tvTimeSpent.setText(tripData.get(0).getmTimeSpent());
            binding.tvTripDate.setText(tripData.get(0).getmDate());
            startPoint = new GeoPoint(tripData.get(0).startGeo.latitude, tripData.get(0).startGeo.longitude);
            stopPoint = new GeoPoint(tripData.get(0).stopGeo.latitude1, tripData.get(0).stopGeo.longitude1);
            mapWorks();
        });
        Button btnDelete = binding.btnDeleteTrip;
        btnDelete.setOnClickListener(this::onDeleteTripButton);

        Button btnImages = binding.btnImages;
        btnImages.setOnClickListener(view ->
                Navigation.findNavController(getView()).navigate(R.id.storedImagesFragment));

        Button btnShared = binding.btnShare;
        btnShared.setOnClickListener(v -> {
            share(screenShot(getView()));
        });

        return binding.getRoot();
    }
    // share og screenshot fra
    // https://stackoverflow.com/questions/30196965/how-to-take-a-screenshot-of-current-activity-and-then-share-it/30212385#answer-30212385
    private Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void share(Bitmap bitmap){
        String pathofBmp=
                MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
                        bitmap,"MinTurassistent", null);
        Uri uri = Uri.parse(pathofBmp);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Min Turassistent");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Jeg har delt en tur med deg!");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        getContext().startActivity(Intent.createChooser(shareIntent, "Min Turassistent"));
    }

    //Viser en standard AlertDialog.. Tilpasset fra Werners dialogTest
    public void onDeleteTripButton(View view){
        DetailsFragment context = this;
        String title = "Bekreft sletting";
        String message = "Slette turen?";
        String button1String = "Ja";
        String button2String = "Nei, gå tilbake!";

        AlertDialog.Builder ad = new AlertDialog.Builder(context.getContext());
        ad.setTitle(title);
        ad.setMessage(message);

        ad.setPositiveButton(button1String,
                (dialog, arg1) -> {
                    mViewModel.deleteTrip(mTripID);
                    Navigation.findNavController(getView()).navigate(R.id.myToursFragment);
                    Toast.makeText(context.getContext(), "Tur slettet..", Toast.LENGTH_LONG).show();
                });

        ad.setNegativeButton(button2String,
                (dialog, arg1) ->
                        Toast.makeText(context.getContext(), "Avbryter..", Toast.LENGTH_LONG).show());

        ad.setCancelable(false);

        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            }
        });

        ad.show();
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