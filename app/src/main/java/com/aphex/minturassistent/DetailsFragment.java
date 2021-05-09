package com.aphex.minturassistent;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aphex.minturassistent.databinding.FragmentDetailsBinding;
import com.aphex.minturassistent.viewmodel.ViewModel;

public class DetailsFragment extends Fragment {
    FragmentDetailsBinding binding;
    private ViewModel mViewModel;
    public int mTripID;
    public TextView tvTourTittel;
    public TextView tvTimeSpent;
    public TextView tvTripDate;

    public DetailsFragment() {
    }

    public static DetailsFragment newInstance() {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        tvTourTittel = view.findViewById(R.id.tvTourTittel);
        tvTimeSpent = view.findViewById(R.id.tvTimeSpent);
        tvTripDate = view.findViewById(R.id.tvTripDate);

        SharedPreferences prefs = view.getContext().getSharedPreferences("tripID", 0);
        mTripID = prefs.getInt("tripID", -1);

        mViewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        mViewModel.getTripData(mTripID).observe(getActivity(), tripData -> {

            tvTourTittel.setText(tripData.get(0).getmTripName());
            tvTimeSpent.setText(tripData.get(0).getmTimeSpent());
            tvTripDate.setText(tripData.get(0).getmDate());
        });
        return view;
    }
}