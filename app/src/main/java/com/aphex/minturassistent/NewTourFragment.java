package com.aphex.minturassistent;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.aphex.minturassistent.Entities.Trip;
import com.aphex.minturassistent.databinding.FragmentNewTourBinding;
import com.aphex.minturassistent.viewmodel.ViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class NewTourFragment extends Fragment {
    public EditText etDate;
    public Button btnNewTour;
    public String tourType;

    public String tourName1;
    public int estimatedDays1, estimatedHours1;

    Calendar choosenDate = Calendar.getInstance();
    public ViewModel viewModel;
    FragmentNewTourBinding binding;

    public NewTourFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNewTourBinding.inflate(inflater, container, false);
        etDate = binding.etDate;
        btnNewTour = binding.btnNewTour;

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);

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
                EditText date = binding.etDate;

                EditText tourName = binding.etTourName;
                if(!tourName.getText().toString().isEmpty()) {
                    tourName1 = tourName.getText().toString();
                } else {
                    tourName1 = "Uten navn";
                }

                EditText estimatedDays = binding.etEstimatedDays;
                if(!estimatedDays.getText().toString().isEmpty())
                    estimatedDays1 = Integer.parseInt(estimatedDays.getText().toString());

                EditText estimatedHours = binding.etEstimatedHours;
                if(!estimatedHours.getText().toString().isEmpty())
                    estimatedHours1 = Integer.parseInt(estimatedHours.getText().toString());

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

                Trip newTrip = new Trip(tourName1, String.valueOf(date.getText()), estimatedHours1, estimatedDays1,
                        false, "null", "null", tourType);
                viewModel.getCurrentTrip().postValue(newTrip);
                Navigation.findNavController(getView()).navigate(R.id.planTourFragment);
            }
        });
        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        viewModel.getDate().observe(getActivity(), date -> {
            etDate.setText(date);
        });
    }
}
