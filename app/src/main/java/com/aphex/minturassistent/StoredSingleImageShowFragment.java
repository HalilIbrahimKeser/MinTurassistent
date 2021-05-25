package com.aphex.minturassistent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aphex.minturassistent.databinding.FragmentStoredSingleImageShowBinding;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

public class StoredSingleImageShowFragment extends Fragment {

    public StoredSingleImageShowFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.hideTopNav();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentStoredSingleImageShowBinding binding = FragmentStoredSingleImageShowBinding.inflate(inflater, container, false);

        SharedPreferences prefs = getContext().getSharedPreferences("tripImage", 0);
        String mImageURI = prefs.getString("imgUrl", "0");

        Glide.with(binding.imSinglePhotoShow)
                .load(mImageURI)
                .thumbnail(0.33f)
                .centerCrop()
                .into(binding.imSinglePhotoShow);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(getView()).navigate(R.id.storedImagesFragment));

        return binding.getRoot();
    }
}