package com.aphex.minturassistent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.aphex.minturassistent.Entities.Images;
import com.aphex.minturassistent.adapters.ImageAdapter;
import com.aphex.minturassistent.databinding.FragmentStoredSingleImageShowBinding;
import com.aphex.minturassistent.viewmodel.ViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class storedSingleImageShowFragment extends Fragment {
    private final List<Images> mediaList = new ArrayList<>();
    private ViewModel mViewModel;

    public storedSingleImageShowFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentStoredSingleImageShowBinding binding = FragmentStoredSingleImageShowBinding.inflate(inflater, container, false);
        final ImageAdapter imageAdapter = new ImageAdapter(requireActivity(), new ImageAdapter.ImageDiff());

        mViewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        mViewModel.getMediaData().observe(getViewLifecycleOwner(), imageAdapter::submitList);

        return binding.getRoot();
    }


    private void getImageToShow() {
        //Må fikses ikke ferdig Halil
        try {
            mViewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
            mViewModel.mediaData.setValue(mediaList);

            //hente imageid
            //mViewModel.getImage(imageID)
            //sette image på imSinglePhotoShow
            //Denne må muligens gjøres via en adapter

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}