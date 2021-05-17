package com.aphex.minturassistent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.aphex.minturassistent.databinding.FragmentDetailsBinding;
import com.aphex.minturassistent.viewmodel.ViewModel;

import org.jetbrains.annotations.NotNull;

public class DetailsFragment extends Fragment {
    private ViewModel mViewModel;
    public int mTripID;
    public TextView tvTourTittel;
    public TextView tvTimeSpent;
    public TextView tvTripDate;

    public DetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentDetailsBinding binding = FragmentDetailsBinding.inflate(inflater, container, false);

        tvTourTittel = binding.tvTourTittel;
        tvTimeSpent = binding.tvTimeSpent;
        tvTripDate = binding.tvTripDate;

        SharedPreferences prefs = getContext().getSharedPreferences("tripID", 0);
        mTripID = prefs.getInt("tripID", -1);

        mViewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        mViewModel.getTripData(mTripID).observe(getActivity(), tripData -> {
            if(!tripData.isEmpty()) {
                tvTourTittel.setText(tripData.get(0).getmTripName());
                tvTimeSpent.setText(tripData.get(0).getmTimeSpent());
                tvTripDate.setText(tripData.get(0).getmDate());
            }
        });

        Button btnImages = binding.btnImages;
        btnImages.setOnClickListener(view ->
                Navigation.findNavController(getView()).navigate(R.id.storedImagesFragment));

        Button btnDeleteTrip = binding.btnDeleteTrip;
        btnDeleteTrip.setOnClickListener(view -> {
            mViewModel.deleteTrip(mTripID);
            Navigation.findNavController(getView()).navigate(R.id.myToursFragment);
        });
        return binding.getRoot();
    }
}