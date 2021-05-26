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

import com.aphex.minturassistent.adapters.ImageAdapter;
import com.aphex.minturassistent.databinding.FragmentStoredImagesBinding;
import com.aphex.minturassistent.viewmodel.ViewModel;

import org.jetbrains.annotations.NotNull;


public class StoredImagesFragment extends Fragment {
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

        mViewModel.getTripWithImages(mTripID).observe(getViewLifecycleOwner(), imageAdapter::submitList);

        mViewModel.getSingleTrip(mTripID).observe(getViewLifecycleOwner(), tripData -> {
            binding.tvTitleImages.setText(tripData.get(0).getmTripName());
        });


        return binding.getRoot();
    }
}