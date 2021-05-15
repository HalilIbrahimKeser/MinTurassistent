package com.aphex.minturassistent;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aphex.minturassistent.Entities.Images;
import com.aphex.minturassistent.adapters.ImageAdapter;
import com.aphex.minturassistent.viewmodel.ViewModel;

import java.util.ArrayList;
import java.util.List;


public class StoredImagesFragment extends Fragment {
    private List<Images> mediaList = new ArrayList<>();
    private ViewModel mViewModel;
    private Cursor cursor;

    public StoredImagesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stored_images, container, false);
        final ImageAdapter imageAdapter = new ImageAdapter(getActivity(), new ImageAdapter.WordDiff());
        RecyclerView recyclerView = view.findViewById(R.id.imagerecycler);
        recyclerView.setAdapter(imageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mediaList = new ArrayList<>();
        parseAllImages();
        mViewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        mViewModel.getMediaData().observe(getViewLifecycleOwner(), medialist -> imageAdapter.submitList(medialist));

        return view;
    }

    private void parseAllImages() {
        try {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            cursor = getActivity()
                    .getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);

            int size = cursor.getCount();

            if (size == 0) {
                Toast.makeText(getActivity(), "Kunne ikke finne noen bilder på minnet.", Toast.LENGTH_SHORT).show();
            } else {
                while (cursor.moveToNext()) {
                    int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    String path = cursor.getString(file_ColumnIndex);
                    String fileName = path.substring(path.lastIndexOf("/") + 1, path.length());
                    Images imageData = new Images(fileName, path);
                    mediaList.add(imageData);
                }
                cursor.close();
            }
            mViewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
            mViewModel.mediaData.setValue(mediaList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}