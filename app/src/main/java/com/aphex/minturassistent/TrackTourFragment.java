package com.aphex.minturassistent;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aphex.minturassistent.databinding.FragmentTrackTourBinding;
import com.aphex.minturassistent.viewmodel.ViewModel;

public class TrackTourFragment extends Fragment {
    private ViewModel mViewModel;
    public int mTripID;

    public TrackTourFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentTrackTourBinding binding = FragmentTrackTourBinding.inflate(inflater, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        SharedPreferences prefs = getContext().getSharedPreferences("tripID", 0);
        mTripID = prefs.getInt("tripID", -1);

        Button btnDeleteTrip = binding.btnDeleteTrip;
        btnDeleteTrip.setOnClickListener(view -> {
            mViewModel.deleteTrip(mTripID);
            Navigation.findNavController(getView()).navigate(R.id.myToursFragment);
        });

        return binding.getRoot();
    }
}