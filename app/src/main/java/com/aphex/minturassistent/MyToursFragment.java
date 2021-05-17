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
import com.aphex.minturassistent.databinding.FragmentMyToursBinding;
import com.aphex.minturassistent.viewmodel.ViewModel;

import org.jetbrains.annotations.NotNull;

public class MyToursFragment extends Fragment {

    public MyToursFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentMyToursBinding binding = FragmentMyToursBinding.inflate(inflater, container, false);

        final Adapter adapter = new Adapter(requireActivity(), new Adapter.WordDiff());

        RecyclerView recyclerView = binding.recyclerview;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ViewModel mViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(ViewModel.class);
        mViewModel.getAllTrips().observe(getViewLifecycleOwner(),
                adapter::submitList);

        return binding.getRoot();
    }
}