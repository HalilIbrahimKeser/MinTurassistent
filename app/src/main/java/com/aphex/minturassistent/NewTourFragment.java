package com.aphex.minturassistent;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.aphex.minturassistent.Entities.Trip;
import com.aphex.minturassistent.databinding.FragmentNewTourBinding;
import com.aphex.minturassistent.viewmodel.ViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewTourFragment extends Fragment {
    public EditText etDate;
    public Button btnNewTour;
    public String tourType;

    Calendar choosenDate = Calendar.getInstance();
    ViewModel viewmodel;
    FragmentNewTourBinding binding;

    public NewTourFragment() {
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
        binding = FragmentNewTourBinding.inflate(inflater, container, false);
        etDate = binding.etDate;
        btnNewTour = binding.btnNewTour;


        int day = choosenDate.get(Calendar.DAY_OF_MONTH);
        int month = choosenDate.get(Calendar.MONTH) + 1;
        int year = choosenDate.get(Calendar.YEAR);
        String dateString = day + "." + month + "." + year;
        etDate.setText(dateString);

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(R.id.datePickerFragment);
            }
        });

        btnNewTour = binding.btnNewTour;
        btnNewTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //OM VERDIENE ER TOMME, LEGG INN VERDIER
                EditText tourName = binding.etTourName;
                String tourName1;
                try {
                    tourName1 = tourName.getText().toString();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    tourName1 = "Uten navn";
                }

                EditText date = binding.etDate;

                EditText estimatedDays = binding.etEstimatedDays;
                int estimatedDays1;
                try {
                    estimatedDays1 = Integer.parseInt(estimatedDays.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    estimatedDays1 = 0;
                }

                EditText estimatedHours = binding.etEstimatedHours;
                int estimatedHours1;
                try {
                    estimatedHours1 = Integer.parseInt(estimatedHours.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    estimatedHours1 = 0;
                }

                RadioButton radioButton1 = binding.rbTrailhiking;
                RadioButton radioButton2 = binding.rbBicycleTour;
                RadioButton radioButton3 = binding.rbSkitour;

                if (radioButton1.isChecked()){
                    tourType = "Gåtur";
                }else if(radioButton2.isChecked()) {
                    tourType = "Sykkeltur";
                }else if(radioButton3.isChecked()) {
                    tourType = "Skitur";
                }else {
                    tourType = "Ikke valgt";
                }
                if(tourName1.isEmpty()){
                    tourName1 = "Uten navn";
                }

                Trip newTrip = new Trip(tourName1, String.valueOf(date.getText()), estimatedDays1, estimatedHours1,
                        false, "null", "null");
                insertTrip(newTrip);

                NavController navController = Navigation.findNavController(view);
                Navigation.findNavController(getView()).navigate(R.id.planTourFragment);
            }
        });
        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewmodel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        viewmodel.getDate().observe(getActivity(), date -> {
            etDate.setText(date);
        });
    }

    public void insertTrip(Trip trip) {
        Trip tripToAdd = new Trip(trip.mTripName, trip.mDate, trip.mEstimatedHours, trip.mEstimatedHDays, trip.getmIsFinished(), trip.getmTimeSpent(), trip.getmPlace());
        try {
            viewmodel.insertTrip(tripToAdd);
            Toast.makeText(getContext(), "Tur lagt inn", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Kunne ikke legge inn", Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(),  String.valueOf(e), Toast.LENGTH_LONG).show();
        }
    }
}
