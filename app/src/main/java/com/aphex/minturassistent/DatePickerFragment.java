package com.aphex.minturassistent;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.aphex.minturassistent.viewmodel.ViewModel;

import java.util.Calendar;


/**
 * Hentet fra exemplet dialogTest på Modul6
 *
 * Created by Werner on 30.03.2017.
 *
 * FRA: https://developer.android.com/guide/topics/ui/controls/pickers.html
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Starter med dagens dato
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getContext(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String dateString = dayOfMonth + "." + (month + 1) + "." + year;
        ViewModel viewmodel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        viewmodel.dateData.setValue(dateString);
    }

}
