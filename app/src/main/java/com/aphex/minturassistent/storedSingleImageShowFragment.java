package com.aphex.minturassistent;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aphex.minturassistent.Entities.Images;
import com.aphex.minturassistent.adapters.ImageAdapter;
import com.aphex.minturassistent.viewmodel.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class storedSingleImageShowFragment extends Fragment {
    private List<Images> mediaList = new ArrayList<>();
    private ViewModel mViewModel;

    public storedSingleImageShowFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stored_images, container, false);
        final ImageAdapter imageAdapter = new ImageAdapter(requireActivity(), new ImageAdapter.ImageDiff());

        mViewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        mViewModel.getMediaData().observe(getViewLifecycleOwner(), imageAdapter::submitList);

        return view;
    }


    private void getImageToShow() {
        //Må fikses ikke ferdig Halil
        try {
            mViewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
            mViewModel.mediaData.setValue(mediaList);

            //hente imageid
            //mViewModel.getImage(imageID)
            //sette image på imSinglePhotoShow
            //Denne må muligens gjøres vi en adapter

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}