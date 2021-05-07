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

import com.aphex.minturassistent.viewmodel.ViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewTourFragment extends Fragment {
    public static EditText etDate;
    private static View view;
    final Calendar myCalendar = Calendar.getInstance();
    NewTourFragment context = this;
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

        Calendar choosenDate = Calendar.getInstance();
        int cday = choosenDate.get(Calendar.DAY_OF_MONTH);
        int cmonth = choosenDate.get(Calendar.MONTH) + 1;
        int cyear = choosenDate.get(Calendar.YEAR);
        String dateString = cday + "." + cmonth + "." + cyear;
        etDate.setText(dateString);

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
