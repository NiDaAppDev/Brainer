package com.nidaappdev.performancemeasurement.customViews.StatsClasses.AxisValueFormatters;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class MonthAxisValueFormatter extends ValueFormatter {

    private final String[] mMonths = new String[]{
            "Jan.", "Feb.", "March", "April", "May", "June",
            "July", "Aug.", "Sept.", "Oct.", "Nov.", "Dec."
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mMonths[(int) (value % mMonths.length)];
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        try {
            return mMonths[(int) (value % mMonths.length)];
        } catch (Exception e) {
            e.printStackTrace();
            return mMonths[0];
        }
    }
}
