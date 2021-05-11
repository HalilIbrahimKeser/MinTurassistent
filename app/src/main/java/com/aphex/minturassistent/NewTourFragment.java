package com.aphex.minturassistent;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.aphex.minturassistent.viewmodel.ViewModel;

import java.util.Calendar;

public class NewTourFragment extends Fragment {
    public EditText etDate;
    public Button btNewTour;
    private View view;
    final Calendar myCalendar = Calendar.getInstance();
    ViewModel viewmodel;

    public NewTourFragment() {
        // Required empty public constructor
    }

    public static NewTourFragment newInstance() {
        NewTourFragment fragment = new NewTourFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_tour, container, false);
        etDate = view.findViewById(R.id.etDate);
        btNewTour = view.findViewById(R.id.btnNewTour);

        Calendar choosenDate = Calendar.getInstance();
        int cday = choosenDate.get(Calendar.DAY_OF_MONTH);
        int cmonth = choosenDate.get(Calendar.MONTH) + 1;
        int cyear = choosenDate.get(Calendar.YEAR);
        String dateString = cday + "." + cmonth + "." + cyear;
        etDate.setText(dateString);

        btNewTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                Navigation.findNavController(getView()).navigate(R.id.planTourFragment);
            }
        });

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                Navigation.findNavController(getView()).navigate(R.id.datePickerFragment);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewmodel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        viewmodel.getDate().observe(getActivity(), date -> {
            etDate.setText(date);
        });
    }
}
