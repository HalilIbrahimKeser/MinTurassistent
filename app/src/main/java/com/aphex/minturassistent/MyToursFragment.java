package com.aphex.minturassistent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
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

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction == 4) {
                    int position = viewHolder.getAdapterPosition();
                    mViewModel.deleteTrip(position + 1);
                    Toast.makeText(getContext(), "Venstre", Toast.LENGTH_SHORT).show();
                } else if (direction == 8){
                    Toast.makeText(getContext(), "Høyre", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), "Noe annet", Toast.LENGTH_SHORT).show();
                }
            }
        }).attachToRecyclerView(recyclerView);

        return binding.getRoot();
    }
}