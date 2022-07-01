package com.example.performancemeasurement.customViews.StatsClasses.CustomChartObjects;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.performancemeasurement.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CustomChart extends ConstraintLayout {

    private TextView titleTV, tipTV, lockExplanationTV;
    private TabLayout chartPicker;
    private BarChart barChart;
    private HorizontalBarChart horizontalBarChart;
    private PieChart pieChart;
    private RadarChart spiderChart;
    private LineChart lineChart;
    private ConstraintLayout lockContainer;

    private String title;

    private boolean unlocked;

    public enum ChartType {barChart, horizontalBarChart, pieChart, spiderChart, lineChart}

    private ChartType chartType;

    private ArrayList<String> pickList = new ArrayList<>();

    private HashMap<String, String> tips = new HashMap<>();

    public enum TipType {Suggestive, Informative, NotEnoughData}

    private TipType tipType;


    public CustomChart(@NonNull Context context) {
        this(context, null);
    }

    public CustomChart(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomChart(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CustomChart(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(context, R.layout.custom_chart_view, this);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomChart, defStyleAttr, defStyleRes);

        title = a.getString(R.styleable.CustomChart_title);
        unlocked = a.getBoolean(R.styleable.CustomChart_unlocked, true);
        chartType = ChartType.values()[a.getInt(R.styleable.CustomChart_chart_type, 0)];
        CharSequence[] charSequencesPickList = a.getTextArray(R.styleable.CustomChart_android_entries);
        if (charSequencesPickList != null) {
            for (CharSequence pickItem : charSequencesPickList) {
                pickList.add(pickItem.toString());
            }
        }

        titleTV = findViewById(R.id.titleTV);
        chartPicker = findViewById(R.id.chart_picker);
        barChart = findViewById(R.id.bar_chart);
        horizontalBarChart = findViewById(R.id.horizontal_bar_chart);
        pieChart = findViewById(R.id.pie_chart);
        spiderChart = findViewById(R.id.spider_chart);
        lineChart = findViewById(R.id.line_chart);
        tipTV = findViewById(R.id.tip);

        lockContainer = findViewById(R.id.lock_container);
        lockExplanationTV = findViewById(R.id.lock_explanation);

        initTitle(title);
        initChartPicker(pickList);
        initChart();
        initTipList(null, null, true);
        initLock();

        a.recycle();
    }

    private void initTitle(String title) {
        titleTV.setText(title);
    }

    private void initChartPicker(ArrayList<String> pickList) {
        if (pickList.size() > 0) {
            for (String pickItem : pickList) {
                chartPicker.addTab(chartPicker.newTab());
                chartPicker.getTabAt(pickList.indexOf(pickItem))
                        .setText(pickItem);
            }
        } else {
            chartPicker.setVisibility(INVISIBLE);
        }
    }

    private void initChart() {
        switch (chartType) {
            case barChart:
                barChart.setVisibility(VISIBLE);
                horizontalBarChart.setVisibility(INVISIBLE);
                pieChart.setVisibility(INVISIBLE);
                spiderChart.setVisibility(INVISIBLE);
                lineChart.setVisibility(INVISIBLE);
                break;
            case horizontalBarChart:
                barChart.setVisibility(INVISIBLE);
                horizontalBarChart.setVisibility(VISIBLE);
                pieChart.setVisibility(INVISIBLE);
                spiderChart.setVisibility(INVISIBLE);
                lineChart.setVisibility(INVISIBLE);
                break;
            case pieChart:
                barChart.setVisibility(INVISIBLE);
                horizontalBarChart.setVisibility(INVISIBLE);
                pieChart.setVisibility(VISIBLE);
                spiderChart.setVisibility(INVISIBLE);
                lineChart.setVisibility(INVISIBLE);
                break;
            case spiderChart:
                barChart.setVisibility(INVISIBLE);
                horizontalBarChart.setVisibility(INVISIBLE);
                pieChart.setVisibility(INVISIBLE);
                spiderChart.setVisibility(VISIBLE);
                lineChart.setVisibility(INVISIBLE);
                break;
            case lineChart:
                barChart.setVisibility(INVISIBLE);
                horizontalBarChart.setVisibility(INVISIBLE);
                pieChart.setVisibility(INVISIBLE);
                spiderChart.setVisibility(INVISIBLE);
                lineChart.setVisibility(VISIBLE);
                break;
        }
    }

    public void initTipList(HashMap<String, String> tips, TipType tipType, boolean isAnalyzeGood) {
        if (tips == null) {
            tips = new HashMap<>();
        }
        this.tips = tips;

        if (tipType == null) {
            tipType = TipType.NotEnoughData;
        }
        this.tipType = tipType;

        if (!tips.containsKey("IMPROVE_TIP")) {
            this.tips.put("IMPROVE_TIP", "You can do better!");
        }
        if (!tips.containsKey("DOING_GREAT_TIP")) {
            this.tips.put("DOING_GREAT_TIP", "You're doing great!\nKeep up the hard work.");
        }
        if (!tips.containsKey("INFORMATIVE_TIP")) {
            this.tips.put("INFORMATIVE_TIP", "This is an informative tip.");
        }
        if (!tips.containsKey("NOT_ENOUGH_DATA_TIP")) {
            this.tips.put("NOT_ENOUGH_DATA_TIP", "Not enough data to analyze.\nCome back when you add more goals to your resume.");
        }
        initTip(getTipKey(isAnalyzeGood));
    }

    private void initTip(String tipKey) {
        tipTV.setText(tips.get(tipKey));
    }

    private String getTipKey(boolean isAnalyzeGood) {
        Chart chart;
        switch (chartType) {
            case horizontalBarChart:
                chart = horizontalBarChart;
                break;
            case pieChart:
                chart = pieChart;
                break;
            case spiderChart:
                chart = spiderChart;
                break;
            case lineChart:
                chart = lineChart;
                break;
            case barChart:
            default:
                chart = barChart;
                break;
        }
        if (chart.isEmpty() || chart.getData().getYMax() <= 0) {
            unlocked = false;
            lock("");
        } else {
            unlocked = true;
            unlock();
            if (tipType == TipType.Suggestive) {
                if (isAnalyzeGood) {
                    return "DOING_GREAT_TIP";
                } else {
                    return "IMPROVE_TIP";
                }
            } else if (tipType == TipType.Informative) {
                return "INFORMATIVE_TIP";
            }
        }
        return "NOT_ENOUGH_DATA_TIP";
    }

    private void initLock() {
        lockContainer.setVisibility(unlocked ? INVISIBLE : VISIBLE);
        generateExplanation();
    }

    public void initScrollableInScrollView(ScrollView scrollView) {
        Chart chart;
        switch (chartType) {
            case horizontalBarChart:
                chart = horizontalBarChart;
                break;
            case pieChart:
            case spiderChart:
            case lineChart:
                return;
            case barChart:
            default:
                chart = barChart;
                break;
        }
        chart.setOnTouchListener((view, motionEvent) -> {
            float lowestVisibleX = ((BarChart) chart).getLowestVisibleX();
            float highestVisibleX = ((BarChart) chart).getHighestVisibleX();
            if (!(lowestVisibleX == chart.getXAxis().getAxisMinimum() && highestVisibleX == chart.getXAxis().getAxisMaximum())) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        break;
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        break;
                }
            }
            return false;
        });
    }

    private void unlock() {
        lockContainer.setVisibility(INVISIBLE);
    }

    private void lock(String explanation) {
        lockContainer.setVisibility(VISIBLE);
        if (!explanation.equals("")) {
            lockExplanationTV.setText(explanation);
        } else {
            generateExplanation();
        }
    }

    public void setTitle(String title) {
        titleTV.setText(title);
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
        if (unlocked) {
            unlock();
        } else {
            lock("");
        }
    }

    public void setUnlocked(boolean unlocked, String explanation) {
        this.unlocked = unlocked;
        if (unlocked) {
            unlock();
        } else {
            lock(explanation);
        }
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public Chart getChart() {
        switch (chartType) {
            case horizontalBarChart:
                return horizontalBarChart;
            case pieChart:
                return pieChart;
            case spiderChart:
                return spiderChart;
            case lineChart:
                return lineChart;
            case barChart:
            default:
                return barChart;
        }
    }

    public TabLayout getChartPicker() {
        return chartPicker;
    }

    public String getPickerSelectedItem() {
        return Objects.requireNonNull(chartPicker.getTabAt(chartPicker.getSelectedTabPosition()).getText()).toString();
    }

    private void generateExplanation() {
        lockExplanationTV.setText(tips.get("NOT_ENOUGH_DATA_TIP"));
    }
}
