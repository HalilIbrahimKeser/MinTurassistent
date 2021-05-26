package com.aphex.minturassistent;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.aphex.minturassistent.Entities.Images;
import com.aphex.minturassistent.Entities.Location;
import com.aphex.minturassistent.databinding.FragmentDetailsBinding;
import com.aphex.minturassistent.viewmodel.ViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.advancedpolyline.MonochromaticPaintList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.res.ResourcesCompat.getDrawable;

public class DetailsFragment extends Fragment {
    private ViewModel mViewModel;
    public int mTripID;
    FragmentDetailsBinding binding;
    MapView mMapView;
    private GeoPoint startPoint;
    private GeoPoint stopPoint;
    private Polyline mPolyline;
    private ArrayList<GeoPoint> pathPoints = new ArrayList<>();

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
        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
        Configuration.getInstance().setUserAgentValue("MinturAssistent");
        SharedPreferences prefs = getContext().getSharedPreferences("tripID", 0);
        mTripID = prefs.getInt("tripID", -1);

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetailsBinding.inflate(inflater, container, false);

        mMapView = binding.mapDetails;
        mViewModel = new ViewModelProvider(this).get(ViewModel.class);

        final Observer<List<Location>> nameObserver = new Observer<List<Location>>() {
            @Override
            public void onChanged(@Nullable final List<Location> locs) {
                for (int i = 0; i < locs.size(); i++) {
                    GeoPoint temp = new GeoPoint(locs.get(i).getmLatitude(), locs.get(i).getmLongitude());
                    pathPoints.add(temp);
                    mPolyline.setPoints(pathPoints);
                }
            }
        };
        mViewModel.getLocationPath(mTripID).observe(getViewLifecycleOwner(), nameObserver);

        mViewModel.getSingleTrip(mTripID).observe(getViewLifecycleOwner(), tripData -> {
            binding.tvTourTittel.setText(tripData.get(0).getmTripName());
            binding.tvTimeSpent.setText(tripData.get(0).getmTimeSpent());
            binding.tvTripDate.setText(tripData.get(0).getmDate());
            binding.tvLocDet.setText(tripData.get(0).getmPlace());
            binding.etComment.setText(tripData.get(0).getmComment());
            startPoint = new GeoPoint(tripData.get(0).startGeo.latitude, tripData.get(0).startGeo.longitude);
            stopPoint = new GeoPoint(tripData.get(0).stopGeo.latitude1, tripData.get(0).stopGeo.longitude1);
            mapWorks();
        });

        mViewModel.getImagesForTrip(mTripID).observe(getViewLifecycleOwner(), images -> {
            if (images.size() != 0) {
                for (int i = 0; i < images.size(); i++) {
                    Marker imageMarker = new Marker(mMapView);

                    Glide.with(this)
                            .asBitmap()
                            .load(images.get(i).getmImageURI())
                            .thumbnail(0.1f)
                            .centerCrop()
                            .apply(new RequestOptions().override(100, 100))
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    imageMarker.setIcon(new BitmapDrawable(resource));
                                }
                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });
                    imageMarker.setIcon(getDrawable(getResources(), R.drawable.placeholder, null));
                    imageMarker.setPosition(new GeoPoint(images.get(i).getmLatitude(), images.get(i).getmLongitude()));
                    imageMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    mMapView.getOverlays().add(imageMarker);
                }
            }
        });

        binding.etComment.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String comment = binding.etComment.getText().toString();
                    mViewModel.setComment(mTripID, comment);

                    handled = true;
                }
                return handled;
            }
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
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void share(Bitmap bitmap) {
        String pathofBmp =
                MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
                        bitmap, "MinTurassistent", null);
        Uri uri = Uri.parse(pathofBmp);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Min Turassistent");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Jeg har delt en tur med deg!");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        getContext().startActivity(Intent.createChooser(shareIntent, "Min Turassistent"));
    }

    //Viser en standard AlertDialog.. Tilpasset fra Werners dialogTest
    public void onDeleteTripButton(View view) {
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
        mMapView.setMultiTouchControls(true);
        mMapView.setClickable(true);
        mMapView.getFocusable();
        mMapView.getController().zoomTo(15.0);
        mMapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);

        //FOCUS
        GeoPoint centerGeo = new GeoPoint(((startPoint.getLatitude() + stopPoint.getLatitude()) / 2), ((startPoint.getLongitude() + stopPoint.getLongitude()) / 2));
        mMapView.getController().animateTo(centerGeo);

        //DRAW PATH
        mPolyline = new Polyline(mMapView);
        final Paint paintBorder = new Paint();
        paintBorder.setStrokeWidth(18);
        paintBorder.setStyle(Paint.Style.FILL_AND_STROKE);
        paintBorder.setColor(Color.BLACK);
        paintBorder.setStrokeCap(Paint.Cap.ROUND);
        paintBorder.setAntiAlias(true);

        final Paint paintInside = new Paint();
        paintInside.setStrokeWidth(8);
        paintInside.setStyle(Paint.Style.FILL);
        paintInside.setColor(Color.WHITE);
        paintInside.setStrokeCap(Paint.Cap.ROUND);
        paintInside.setAntiAlias(true);

        mPolyline.getOutlinePaintLists().add(new MonochromaticPaintList(paintBorder));
        mPolyline.getOutlinePaintLists().add(new MonochromaticPaintList(paintInside));

        mMapView.getOverlays().add(mPolyline);

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