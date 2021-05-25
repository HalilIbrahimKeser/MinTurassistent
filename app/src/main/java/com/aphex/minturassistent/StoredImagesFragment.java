package com.aphex.minturassistent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aphex.minturassistent.Entities.Images;
import com.aphex.minturassistent.Entities.Trip;
import com.aphex.minturassistent.adapters.ImageAdapter;
import com.aphex.minturassistent.databinding.FragmentStoredImagesBinding;
import com.aphex.minturassistent.viewmodel.ViewModel;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;


public class StoredImagesFragment extends Fragment {
//    private List<Trip> mediaList = new ArrayList<>();
    private ViewModel mViewModel;
    int mTripID;

    public StoredImagesFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.hideTopNav();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentStoredImagesBinding binding = FragmentStoredImagesBinding.inflate(inflater, container, false);

        final ImageAdapter imageAdapter = new ImageAdapter(requireActivity(), new ImageAdapter.ImageDiff());

        RecyclerView recyclerView = binding.imagerecycler;
        recyclerView.setAdapter(imageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        SharedPreferences prefs = getContext().getSharedPreferences("tripID", 0);
        mTripID = prefs.getInt("tripID", -1);

//        mediaList = new ArrayList<>();

        mViewModel.getTripWithImages(mTripID).observe(getViewLifecycleOwner(),
                list -> imageAdapter.submitList(list));

        return binding.getRoot();
    }

//    private void parseAllImages() {
//        try {
//            String[] projection = {
//                    MediaStore.Images.Media.DATA
//            };
//            Cursor cursor = requireActivity()
//                    .getContentResolver()
//                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
//
//            int size = cursor.getCount();
//
//            if (size == 0) {
//                Toast.makeText(getActivity(), "Det er ingen bilder på minnet.", Toast.LENGTH_SHORT).show();
//            } else {
//                while (cursor.moveToNext()) {
//                    int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                    String path = cursor.getString(file_ColumnIndex);
//                    String fileName = path.substring(path.lastIndexOf("/") + 1, path.length());
//                    Images imageData = new Images(1, fileName, path, 68.43580,17.43666);
//                    mediaList.add(imageData);
//                }
//                cursor.close();
//            }
//            mViewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
//            mViewModel.mediaData.setValue(mediaList);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}