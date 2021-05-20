package com.aphex.minturassistent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aphex.minturassistent.Entities.Trip;
import com.aphex.minturassistent.adapters.Adapter;
import com.aphex.minturassistent.databinding.FragmentMyToursBinding;
import com.aphex.minturassistent.viewmodel.ViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MyToursFragment extends Fragment {
    Trip currentTrip;

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
        mViewModel.getAllTrips().observe(getViewLifecycleOwner(), adapter::submitList);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                List<Trip> trips = adapter.getCurrentList();
                currentTrip = trips.get(position);
                mViewModel.getCurrentTrip().setValue(currentTrip);

                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        mViewModel.deleteTrip(currentTrip.mTripID);
                        adapter.notifyItemRemoved(currentTrip.mTripID);
                        Toast.makeText(getContext(), "Tur " + currentTrip.mTripName + " slettet", Toast.LENGTH_LONG).show();
                        break;
                    case ItemTouchHelper.RIGHT:
                        SharedPreferences prefs = getView().getContext().getSharedPreferences("tripID", 0);
                        SharedPreferences.Editor editor = prefs.edit();
                        prefs.edit().remove("tripID").apply();
                        editor.putInt("tripID", currentTrip.mTripID);
                        editor.apply();
                        Navigation.findNavController(requireView()).navigate(R.id.detailsFragment);
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return binding.getRoot();
    }
}