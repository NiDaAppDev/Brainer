package com.nidaappdev.performancemeasurement.customViews.StatsClasses.AxisValueFormatters;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class DayAxisValueFormatter extends ValueFormatter {

    private final String[] mDays = new String[]{
            "Sun.", "Mon.", "Tue.", "Wed.", "Thu.", "Fri.", "Sat."
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mDays[(int) (value % mDays.length)];
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        try {
            return mDays[(int) (value % mDays.length)];
        } catch (Exception e) {
            e.printStackTrace();
            return mDays[0];
        }
    }
}
