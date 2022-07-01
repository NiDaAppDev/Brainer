package com.example.performancemeasurement.customViews.StatsClasses.AxisValueFormatters;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class HourAxisValueFormatter extends ValueFormatter {

    public final String[] mHours = new String[] {
            "0am", "1am", "2am", "3am", "4am", "5am", "6am", "7am", "8am", "9am", "10am", "11am",
            "12am", "1pm", "2pm", "3pm", "4pm", "5pm", "6pm", "7pm", "8pm", "9pm", "10pm", "11pm"
    };

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mHours[(int) (value % mHours.length)];
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return mHours[(int) (value % mHours.length)];
    }
}
