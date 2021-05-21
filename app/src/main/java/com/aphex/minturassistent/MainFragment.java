package com.aphex.minturassistent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.aphex.minturassistent.databinding.FragmentMainBinding;

import org.jetbrains.annotations.NotNull;

public class MainFragment extends Fragment {

    public MainFragment() {
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
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentMainBinding binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}