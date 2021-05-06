package com.aphex.minturassistent;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.aphex.minturassistent.viewmodel.ViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * Hentet fra exemplet dialogTest på Modul6
 * <p>
 * Created by Werner on 30.03.2017.
 * <p>
 * FRA: https://developer.android.com/guide/topics/ui/controls/pickers.html
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    ViewModel viewModel;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Starter med dagens dato
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH) + 1;
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getContext(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//        viewModel.getDate().setValue(String.valueOf(year));
//        viewModel.setValue(year, month, dayOfMonth);

        int year1 = year;
        int month1 = month + 1;
        int day1 = dayOfMonth;

        String dateString = day1 + "." + month1  + "." + day1;
        EditText etDate = view.findViewById(R.id.etDate);
        etDate.setText(dateString);

        Toast.makeText(getActivity(), "Valgt dato:" + String.valueOf(dayOfMonth) + "." + String.valueOf(month), Toast.LENGTH_SHORT).show();
    }

}
