package com.aphex.minturassistent;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aphex.minturassistent.adapters.Adapter;
import com.aphex.minturassistent.databinding.FragmentDetailsBinding;
import com.aphex.minturassistent.databinding.FragmentNewTourBinding;
import com.aphex.minturassistent.viewmodel.ViewModel;

public class DetailsFragment extends Fragment {
    FragmentDetailsBinding binding;
    private ViewModel mViewModel;
    public int tripID;

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


        ViewModel viewmodel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        viewmodel.tripID.setValue(tripID);
        return view;
    }
}