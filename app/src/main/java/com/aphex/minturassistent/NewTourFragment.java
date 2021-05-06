package com.aphex.minturassistent;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
    final Calendar myCalendar = Calendar.getInstance();
    EditText etDate;
    NewTourFragment context = this;
    ViewModel viewModel;

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
        View view = inflater.inflate(R.layout.fragment_new_tour, container, false);
        etDate = view.findViewById(R.id.etDate);

        Calendar choosenDate = Calendar.getInstance();
        int cday = choosenDate.get(Calendar.DAY_OF_MONTH);
        int cmonth = choosenDate.get(Calendar.MONTH) + 1;
        int cyear = choosenDate.get(Calendar.YEAR);
        String dateString = cday + "." + cmonth  + "." + cyear;
        etDate.setText(dateString);

            etDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavController navController = Navigation.findNavController(view);
                    Navigation.findNavController(getView()).navigate(R.id.datePickerFragment);

            }});
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(ViewModel.class);
//        viewModel.getDate().observe(getViewLifecycleOwner(), date -> {
//            etDate.setText(date);
//        });
    }
}






//      KODE FJERNET FRA ONCREATE WIEW, Kommentert ut i tilfelle vi skal initialisere viewmodel
//                    NavBackStackEntry backStackEntry = navController.getBackStackEntry(R.id.nav_graph);
//
//                    viewModel = new ViewModelProvider(backStackEntry).get(ViewModel.class);
//                    viewModel.getDate().observe(getViewLifecycleOwner(), list -> {
//
//                    });