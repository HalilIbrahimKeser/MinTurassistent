package com.aphex.minturassistent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aphex.minturassistent.adapters.Adapter;
import com.aphex.minturassistent.viewmodel.ViewModel;

public class MyToursFragment extends Fragment {

    private ViewModel mViewModel;

    public MyToursFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_tours, container, false);

        final Adapter adapter = new Adapter(getActivity(), new Adapter.WordDiff());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(ViewModel.class);
        mViewModel.getAllTrips().observe(getViewLifecycleOwner(), list -> adapter.submitList(list));

        //mViewModel = new ViewModelProvider(view.get(ViewModel.class));
        //mViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(ViewModel.class);
        //mViewModel.getAllTrips().observe(getActivity(), Adapter::submitList);

        /*
        final Adapter adapter = new Adapter(getActivity(), new Adapter.WordDiff());
        RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mViewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        //mViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(ViewModel.class);
        mViewModel.getAllTrips().observe(this, Adapter::submitList);
         */
        return view;
    }
}