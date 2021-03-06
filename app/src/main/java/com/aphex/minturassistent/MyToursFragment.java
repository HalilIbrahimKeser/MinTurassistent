package com.aphex.minturassistent;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aphex.minturassistent.Entities.Trip;
import com.aphex.minturassistent.adapters.Adapter;
import com.aphex.minturassistent.databinding.FragmentMyToursBinding;
import com.aphex.minturassistent.viewmodel.ViewModel;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MyToursFragment extends Fragment {
    Trip currentTrip;
    ViewModel mViewModel;

    public MyToursFragment() {
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
        mViewModel = new ViewModelProvider(this).get(ViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentMyToursBinding binding = FragmentMyToursBinding.inflate(inflater, container, false);

        final Adapter adapter = new Adapter(requireActivity(), new Adapter.WordDiff());

        RecyclerView recyclerView = binding.recyclerview;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mViewModel.getAllTrips().observe(getViewLifecycleOwner(),
                list -> adapter.submitList(list));

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
                mViewModel.getCurrentTrip().postValue(currentTrip);

                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        mViewModel.deleteTrip(currentTrip.mTripID);
                        adapter.notifyItemRemoved(currentTrip.mTripID);
                        Snackbar snackbar = Snackbar
                                .make(binding.rootLayout, "Tur '" + currentTrip.mTripName + "' slettet", Snackbar.LENGTH_LONG);
                        snackbar.setAction("ANGRE", view -> {
                            mViewModel.insertTrip(currentTrip);
                            recyclerView.scrollToPosition(position);
                        });
                        snackbar.setActionTextColor(Color.YELLOW);
                        snackbar.show();
                        break;

                    case ItemTouchHelper.RIGHT:
                        mViewModel.getCurrentTrip().setValue(currentTrip);
                        SharedPreferences prefs = requireActivity().getSharedPreferences("tripID", 0);
                        SharedPreferences.Editor editor = prefs.edit();
                        prefs.edit().remove("tripID").apply();
                        editor.putInt("tripID", currentTrip.mTripID);
                        editor.apply();
                        Navigation.findNavController(requireView()).navigate(R.id.detailsFragment);
                }
            }
            //https://stackoverflow.com/questions/30820806/adding-a-colored-background-with-text-icon-under-swiped-row-when-using-androids
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    Paint p = new Paint();
                    if (dX > 0) {
                        p.setARGB(255, 0, 255, 0);
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                (float) itemView.getBottom() - 40, p);
                    } else {
                        p.setARGB(255, 255, 0, 0);
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom() - 40, p);
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return binding.getRoot();
    }
}