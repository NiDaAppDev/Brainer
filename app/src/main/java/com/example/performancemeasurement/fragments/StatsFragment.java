package com.example.performancemeasurement.fragments;


import android.content.ContentValues;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.performancemeasurement.GoalAndDatabaseObjects.GoalDBHelper;
import com.example.performancemeasurement.GoalAndDatabaseObjects.StatisticsDBHelper;
import com.example.performancemeasurement.R;
import com.example.performancemeasurement.customViews.StatsClasses.AxisValueFormatters.DayAxisValueFormatter;
import com.example.performancemeasurement.customViews.StatsClasses.AxisValueFormatters.HourAxisValueFormatter;
import com.example.performancemeasurement.customViews.StatsClasses.AxisValueFormatters.MonthAxisValueFormatter;
import com.example.performancemeasurement.customViews.StatsClasses.CustomChartObjects.CustomChart;
import com.example.performancemeasurement.customViews.StatsClasses.RadarMarkerView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.google.android.material.tabs.TabLayout;

import org.joda.time.DateTime;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatsFragment extends Fragment {

    private CustomChart workTimeDivisionChart, pomodoroVSRegularDivisionChart, pomodoroVSRegularResultsChart, neuronsProgressChart;
    private ScrollView scrollView;
    private static StatisticsDBHelper db;
    private static GoalDBHelper goalDB;

    private static HashMap<String, String> tips = new HashMap<>();

    public StatsFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stats, container, false);

        db = new StatisticsDBHelper(requireContext());
        goalDB = new GoalDBHelper(requireContext());

        scrollView = v.findViewById(R.id.scrollView);

        initCharts(v);
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initCharts(View v) {
        initChartObjects(v);
        initWorkTimeDivisionChart();
        initPomodoroVSRegularDivisionChart();
        initPomodoroVSRegularResultsChart();
        initNeuronsProgressChart();
    }

    private void initChartObjects(View v) {
        workTimeDivisionChart = v.findViewById(R.id.work_time_division_chart);
        pomodoroVSRegularDivisionChart = v.findViewById(R.id.pomodoro_vs_regular_division_chart);
        pomodoroVSRegularResultsChart = v.findViewById(R.id.pomodoro_vs_regular_results_chart);
        neuronsProgressChart = v.findViewById(R.id.neurons_progress_chart);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initWorkTimeDivisionChart() {
        initGeneralBarChart((BarChart) workTimeDivisionChart.getChart());
        initWorkTimeDivisionPickerListener();
        workTimeDivisionChart.initScrollableInScrollView(scrollView);

        ValueFormatter xAxisFormatter;
        switch (workTimeDivisionChart.getPickerSelectedItem()) {
            case "Daily":
                xAxisFormatter = new DayAxisValueFormatter();
                break;
            case "Monthly":
                xAxisFormatter = new MonthAxisValueFormatter();
                break;
            case "Hourly":
            default:
                xAxisFormatter = new HourAxisValueFormatter();
                break;

        }

        BarDataSet set = new BarDataSet(getWorkDivisionChartData(), "WorkTimeDivisionBarDataSet");
        set.setColor(getResources().getColor(R.color.brain1));
        set.setValueTextColor(getResources().getColor(R.color.brain1));
        BarData data = new BarData(set);
        ((HorizontalBarChart) workTimeDivisionChart.getChart()).setData(data);
        workTimeDivisionChart.initTipList(tips, CustomChart.TipType.Informative, false);

        ((HorizontalBarChart) workTimeDivisionChart.getChart()).animateY(1000, Easing.EaseInOutQuad);
        workTimeDivisionChart.invalidate();

        XAxis xl = ((HorizontalBarChart) workTimeDivisionChart.getChart()).getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setAxisLineColor(getResources().getColor(R.color.brain1));
        xl.setGranularity(1f);
        xl.setValueFormatter(xAxisFormatter);
        xl.setTextColor(getResources().getColor(R.color.brain1));
        xl.setLabelCount(data.getEntryCount());

        YAxis yl = ((HorizontalBarChart) workTimeDivisionChart.getChart()).getAxisLeft();
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisLineColor(getResources().getColor(R.color.brain1));
        yl.setGridColor(getResources().getColor(R.color.brain2));
        yl.setTextColor(getResources().getColor(R.color.brain1));
        yl.setAxisMinimum(0f);

        YAxis yr = ((HorizontalBarChart) workTimeDivisionChart.getChart()).getAxisRight();
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        yr.setAxisLineColor(getResources().getColor(R.color.brain1));
        yr.setTextColor(getResources().getColor(R.color.brain1));
        yr.setAxisMinimum(0f);
    }

    private void initPomodoroVSRegularDivisionChart() {
        initGeneralPieChart(pomodoroVSRegularDivisionChart);

        ArrayList<PieEntry> entries = new ArrayList<>();
        float pomodoroPercent, regularPercent;
        if( db.getAllTimeMinutesOfWork() <= 0) {
            pomodoroPercent = 0;
            regularPercent = 0;
        } else {
            pomodoroPercent = (((float) (goalDB.getAllTimePomodoroCount()) * 25f) / (float) db.getAllTimeMinutesOfWork()) * 100f;
            regularPercent = 100f - pomodoroPercent;
        }
        entries.add(new PieEntry(pomodoroPercent, "Pomodoro"));
        entries.add(new PieEntry(regularPercent, "Regular Timer"));
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.stop_red));
        colors.add(getResources().getColor(R.color.lightning_blur));

        dataSet.setColors(colors);


        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter((PieChart) pomodoroVSRegularDivisionChart.getChart()));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        ((PieChart) pomodoroVSRegularDivisionChart.getChart()).setData(data);

        Legend l = ((PieChart) pomodoroVSRegularDivisionChart.getChart()).getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        HashMap<String, String> tips = new HashMap<>();
        tips.put("IMPROVE_TIP", "We suggest you use the Pomodoro mode more often.");
        pomodoroVSRegularDivisionChart.initTipList(tips, CustomChart.TipType.Suggestive, pomodoroPercent > 70);

        ((PieChart) pomodoroVSRegularDivisionChart.getChart()).invalidate();
    }

    private void initPomodoroVSRegularResultsChart() {
        initGeneralSpiderChart(pomodoroVSRegularResultsChart);

        MarkerView mv = new RadarMarkerView(requireContext(), R.layout.radar_markerview);
        mv.setChartView((RadarChart) pomodoroVSRegularResultsChart.getChart());
        ((RadarChart) pomodoroVSRegularResultsChart.getChart()).setMarker(mv);

        ArrayList<RadarEntry> pomodoroEntries = new ArrayList<>(), regularEntries = new ArrayList<>();

        //Difficulties Average
        pomodoroEntries.add(new RadarEntry(goalDB.getPomodoroDifficultyAverage()));
        regularEntries.add(new RadarEntry(goalDB.getRegularDifficultyAverage()));

        //Satisfaction Average
        pomodoroEntries.add(new RadarEntry(goalDB.getPomodoroSatisfactionAverage()));
        regularEntries.add(new RadarEntry(goalDB.getRegularSatisfactionAverage()));

        //Evolving Average
        pomodoroEntries.add(new RadarEntry(goalDB.getPomodoroEvolvingAverage()));
        regularEntries.add(new RadarEntry(goalDB.getRegularEvolvingAverage()));

        //Time Evaluation Average
        pomodoroEntries.add(new RadarEntry(goalDB.getPomodoroEvaluationAverage()));
        regularEntries.add(new RadarEntry(goalDB.getRegularEvaluationAverage()));


        RadarDataSet pomodoroSet = new RadarDataSet(pomodoroEntries, "Pomodoro"),
                regularSet = new RadarDataSet(regularEntries, "Regular Timer");

        pomodoroSet.setColor(getResources().getColor(R.color.stop_red));
        pomodoroSet.setFillColor(getResources().getColor(R.color.stop_red));
        pomodoroSet.setDrawFilled(true);
        pomodoroSet.setFillAlpha(180);
        pomodoroSet.setLineWidth(2f);
        pomodoroSet.setDrawHighlightCircleEnabled(true);
        pomodoroSet.setDrawHighlightIndicators(false);

        regularSet.setColor(getResources().getColor(R.color.lightning_blur));
        regularSet.setFillColor(getResources().getColor(R.color.lightning_blur));
        regularSet.setDrawFilled(true);
        regularSet.setFillAlpha(180);
        regularSet.setLineWidth(2f);
        regularSet.setDrawHighlightCircleEnabled(true);
        regularSet.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        sets.add(pomodoroSet);
        sets.add(regularSet);

        RadarData data = new RadarData(sets);
        data.setDrawValues(false);

        ((RadarChart) pomodoroVSRegularResultsChart.getChart()).setData(data);
        ((RadarChart) pomodoroVSRegularResultsChart.getChart()).invalidate();

        XAxis xAxis = ((RadarChart) pomodoroVSRegularResultsChart.getChart()).getXAxis();
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new ValueFormatter() {

            private final String[] mResults = new String[]{
                    "Difficulty", "Satisfaction", "Evolving", "Evaluation"
            };

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mResults[(int) value % mResults.length];
            }

            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return mResults[(int) value % mResults.length];
            }
        });
        xAxis.setTextColor(getResources().getColor(R.color.brain1));

        YAxis yAxis = ((RadarChart) pomodoroVSRegularResultsChart.getChart()).getYAxis();
        yAxis.setLabelCount(4, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(4f);
        yAxis.setDrawLabels(false);

        Legend l = ((RadarChart) pomodoroVSRegularResultsChart.getChart()).getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);

        ArrayList<String> tipsArrayList = new ArrayList<>();

        if (goalDB.getPomodoroDifficultyAverage() < goalDB.getRegularDifficultyAverage()) {
            tipsArrayList.add("Try using Pomodoro mode for harder goals.");
        }

        if (goalDB.getPomodoroSatisfactionAverage() < goalDB.getRegularSatisfactionAverage()) {
            tipsArrayList.add("Your satisfaction will grow by getting used to the Pomodoro mode.");
        }

        if (goalDB.getPomodoroEvolvingAverage() < goalDB.getRegularEvolvingAverage()) {
            tipsArrayList.add("You could get even more evolved by using the Pomodoro mode more frequently.");
        }

        if (goalDB.getPomodoroEvaluationAverage() < goalDB.getRegularEvaluationAverage()) {
            tipsArrayList.add("The more you use the Pomodoro mode, the more you'll get used to evaluating with it.");
        }

        StringBuilder currentTip = new StringBuilder();
        if (tipsArrayList.size() > 1) {
            for (String tip : tipsArrayList) {
                if (tipsArrayList.indexOf(tip) != 0) {
                    currentTip.append("\n");
                }
                currentTip.append(tipsArrayList.indexOf(tip) + 1).append(". ").append(tip);
            }
        } else if (tipsArrayList.size() == 1) {
            currentTip.append(tipsArrayList.get(0));
        }

        HashMap<String, String> tips = new HashMap<>();
        tips.put("IMPROVE_TIP", currentTip.toString());

        pomodoroVSRegularResultsChart.initTipList(tips, CustomChart.TipType.Suggestive, tipsArrayList.isEmpty());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initNeuronsProgressChart() {
        initGeneralLineChart((LineChart) neuronsProgressChart.getChart());
        initNeuronsProgressPickerListener();
        neuronsProgressChart.initScrollableInScrollView(scrollView);

        ValueFormatter xAxisFormatter;
        switch (neuronsProgressChart.getPickerSelectedItem()) {
            case "This Week":
                xAxisFormatter = new DayAxisValueFormatter();
                break;
            case "This Month":
                xAxisFormatter = new DefaultAxisValueFormatter(0);
                break;
            case "This Year":
                xAxisFormatter = new MonthAxisValueFormatter();
                break;
            case "Today":
            default:
                xAxisFormatter = new HourAxisValueFormatter();
                break;

        }
        LineDataSet set;
        if (neuronsProgressChart.getChart().getData() != null && neuronsProgressChart.getChart().getData().getDataSetCount() > 0) {
            set = (LineDataSet) neuronsProgressChart.getChart().getData().getDataSetByIndex(0);
            set.setValues(getNeuronsProgressChartData());
            neuronsProgressChart.getChart().getData().notifyDataChanged();
            neuronsProgressChart.getChart().notifyDataSetChanged();
        } else {
            set = new LineDataSet(getNeuronsProgressChartData(), "NeuronsProgress");
            set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set.setCubicIntensity(0.1f);
            set.setDrawFilled(true);
            set.setDrawCircles(false);
            set.setLineWidth(1.8f);
            set.setCircleRadius(4f);
            set.setCircleColor(Color.WHITE);
            set.setColor(getResources().getColor(R.color.brain1));
            set.setFillColor(getResources().getColor(R.color.brain1));
            set.setFillAlpha(100);
            set.setDrawHighlightIndicators(false);
            set.setFillFormatter((dataSet, dataProvider) -> ((LineChart) neuronsProgressChart.getChart()).getAxisLeft().getAxisMinimum());
        }
        LineData data = new LineData(set);
        data.setDrawValues(false);
        Log.d(ContentValues.TAG, "initNeuronsProgressChart: " + data);
        ((LineChart) neuronsProgressChart.getChart()).setData(data);
        neuronsProgressChart.initTipList(tips, CustomChart.TipType.Informative, false);

        ((LineChart) neuronsProgressChart.getChart()).animateY(1000, Easing.EaseInOutQuad);
        neuronsProgressChart.invalidate();

        XAxis x = ((LineChart) neuronsProgressChart.getChart()).getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(false);
        x.setGranularity(1f);
        x.setValueFormatter(xAxisFormatter);
        x.setAxisLineColor(getResources().getColor(R.color.brain1));
        x.setTextColor(getResources().getColor(R.color.brain1));
        x.setAxisMaximum(data.getEntryCount() - 1);

        YAxis y = ((LineChart) neuronsProgressChart.getChart()).getAxisLeft();
        y.setDrawGridLines(false);
        y.setAxisLineColor(getResources().getColor(R.color.brain1));
        y.setTextColor(getResources().getColor(R.color.brain1));
        y.setAxisMinimum(0f);

    }

    private void initWorkTimeDivisionPickerListener() {
        workTimeDivisionChart.getChartPicker().setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                initWorkTimeDivisionChart();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initNeuronsProgressPickerListener() {
        neuronsProgressChart.getChartPicker().setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                initNeuronsProgressChart();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<BarEntry> getWorkDivisionChartData() {
        List<BarEntry> workEntries = new ArrayList<>();
        ArrayList<Integer> bestTimes = new ArrayList<>();
        int bestTime = 0;

        StringBuilder bestTimesStr = new StringBuilder();

        switch (workTimeDivisionChart.getPickerSelectedItem()) {
            case "Daily":
                for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                    long dailyMinutesOfWork = db.getDailyMinutesOfWork(dayOfWeek);
                    workEntries.add(new BarEntry((dayOfWeek.ordinal() + 1) % 7, (float) dailyMinutesOfWork));
                    if (dailyMinutesOfWork == bestTime) {
                        bestTimes.add((dayOfWeek.ordinal() + 1) % 7);
                    } else if (dailyMinutesOfWork > bestTime) {
                        bestTime = (int) dailyMinutesOfWork;
                        bestTimes.clear();
                        bestTimes.add((dayOfWeek.ordinal() + 1) % 7);
                    }
                }

                for (int bestDayUnit : bestTimes) {
                    if (bestTimes.indexOf(bestDayUnit) != 0) {
                        if (bestTimes.indexOf(bestDayUnit) != bestTimes.size() - 1) {
                            bestTimesStr.append(", ");
                        } else {
                            bestTimesStr.append(" and ");
                        }
                    }
                    bestTimesStr.append(DayOfWeek.of(bestDayUnit).name());
                }
                tips.put("INFORMATIVE_TIP", "You work best on " + bestTimesStr);

                workTimeDivisionChart.setTitle("Daily Division Of Work Time");
                break;
            case "Monthly":
                for (Month month : Month.values()) {
                    long monthlyMinutesOfWork = db.getMonthlyMinutesOfWork(month);
                    workEntries.add(new BarEntry(month.ordinal(), (float) monthlyMinutesOfWork));
                    if (monthlyMinutesOfWork == bestTime) {
                        bestTimes.add(month.ordinal());
                    } else if (monthlyMinutesOfWork > bestTime) {
                        bestTime = (int) monthlyMinutesOfWork;
                        bestTimes.clear();
                        bestTimes.add(month.ordinal());
                    }
                }

                for (int bestMonthUnit : bestTimes) {
                    if (bestTimes.indexOf(bestMonthUnit) != 0) {
                        if (bestTimes.indexOf(bestMonthUnit) != bestTimes.size() - 1) {
                            bestTimesStr.append(", ");
                        } else {
                            bestTimesStr.append(" and ");
                        }
                    }
                    bestTimesStr.append(Month.of(bestMonthUnit + 1).name());
                }
                tips.put("INFORMATIVE_TIP", "You work best on " + bestTimesStr);

                workTimeDivisionChart.setTitle("Monthly Division Of Work Time");
                break;
            case "Hourly":
            default:
                for (float i = 0f; i < 24f; i++) {
                    long hourlyMinutesOfWork = db.getHourlyMinutesOfWork((int) i);
                    workEntries.add(new BarEntry(i, (float) hourlyMinutesOfWork));
                    if (hourlyMinutesOfWork == bestTime) {
                        bestTimes.add((int) i);
                    } else if (hourlyMinutesOfWork > bestTime) {
                        bestTime = (int) hourlyMinutesOfWork;
                        bestTimes.clear();
                        bestTimes.add((int) i);
                    }
                }

                for (int bestHourUnit : bestTimes) {
                    if (bestTimes.indexOf(bestHourUnit) != 0) {
                        if (bestTimes.indexOf(bestHourUnit) != bestTimes.size() - 1) {
                            bestTimesStr.append(", ");
                        } else {
                            bestTimesStr.append(" and ");
                        }
                    }
                    bestTimesStr.append(bestHourUnit).append(":00");
                }
                tips.put("INFORMATIVE_TIP", "You work best on " + bestTimesStr);

                workTimeDivisionChart.setTitle("Hourly Division Of Work Time");
                break;
        }

        return workEntries;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<Entry> getNeuronsProgressChartData() {
        List<Entry> neuronsEntries = new ArrayList<>();
        ArrayList<Integer> mostNeurons = new ArrayList<>();
        int mostNeuronsHolder = 0;

        StringBuilder mostNeuronsStr = new StringBuilder();
        DateTime dt = new DateTime();
        int todayOfWeek = (dt.getDayOfWeek() + 1) % 7;
        int todayOfMonth = dt.getDayOfMonth();
        int nowMonth = dt.getMonthOfYear();
        float nowHour = dt.getHourOfDay();

        switch (neuronsProgressChart.getPickerSelectedItem()) {
            case "This Week":
                long weekDailyNeurons = 0;
                for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                    if((dayOfWeek.ordinal() + 1) % 7 < todayOfWeek) {
                        long todayNeurons = db.getDayOfThisWeekNeurons(dayOfWeek);
                        weekDailyNeurons += todayNeurons;
                        neuronsEntries.add(new Entry((dayOfWeek.ordinal() + 1) % 7, (float) weekDailyNeurons));
                        if (todayNeurons == mostNeuronsHolder) {
                            mostNeurons.add((dayOfWeek.ordinal() + 1) % 7);
                        } else if (todayNeurons > mostNeuronsHolder) {
                            mostNeuronsHolder = (int) todayNeurons;
                            mostNeurons.clear();
                            mostNeurons.add((dayOfWeek.ordinal() + 1) % 7);
                        }
                    }
                }

                for (int bestDayUnit : mostNeurons) {
                    if (mostNeurons.indexOf(bestDayUnit) != 0) {
                        if (mostNeurons.indexOf(bestDayUnit) != mostNeurons.size() - 1) {
                            mostNeuronsStr.append(", ");
                        } else {
                            mostNeuronsStr.append(" and ");
                        }
                    }
                    mostNeuronsStr.append(DayOfWeek.of(bestDayUnit).name());
                }
                tips.put("INFORMATIVE_TIP", "This week your strong day" + (mostNeurons.size() > 1 ? "s were " : " was ") + mostNeuronsStr);

                neuronsProgressChart.setTitle("This Week's Neurons");
                break;
            case "This Month":
                long monthDailyNeurons = 0;
                for (int dayOfMonth = 1; dayOfMonth <= todayOfMonth; dayOfMonth++) {
                    long todayNeurons = db.getDayOfThisMonthNeurons(dayOfMonth);
                    monthDailyNeurons += todayNeurons;
                    neuronsEntries.add(new Entry(dayOfMonth, (float) monthDailyNeurons));
                    if (todayNeurons == mostNeuronsHolder) {
                        mostNeurons.add(dayOfMonth);
                    } else if (todayNeurons > mostNeuronsHolder) {
                        mostNeuronsHolder = (int) todayNeurons;
                        mostNeurons.clear();
                        mostNeurons.add(dayOfMonth);
                    }
                }


                for (int bestDayUnit : mostNeurons) {
                    if (mostNeurons.indexOf(bestDayUnit) != 0) {
                        if (mostNeurons.indexOf(bestDayUnit) != mostNeurons.size() - 1) {
                            mostNeuronsStr.append(", ");
                        } else {
                            mostNeuronsStr.append(" and ");
                        }
                    }
                    mostNeuronsStr.append(bestDayUnit);
                }
                tips.put("INFORMATIVE_TIP", "This month your strong day" + (mostNeurons.size() > 1 ? "s were the " : " was the ") + mostNeuronsStr);

                neuronsProgressChart.setTitle("This Month's Neurons");
                break;
            case "This Year":
                long monthlyNeurons = 0;
                for (Month month : Month.values()) {
                    long thisMonthsNeurons = db.getMonthOfThisYearNeurons(month);
                    monthlyNeurons += thisMonthsNeurons;
                    neuronsEntries.add(new Entry(month.ordinal(), (float) monthlyNeurons));
                    if (thisMonthsNeurons == mostNeuronsHolder) {
                        mostNeurons.add(month.ordinal());
                    } else if (thisMonthsNeurons > mostNeuronsHolder) {
                        mostNeuronsHolder = (int) thisMonthsNeurons;
                        mostNeurons.clear();
                        mostNeurons.add(month.ordinal());
                    }

                    if(month.equals(Month.of(nowMonth))){
                        break;
                    }
                }

                for (int bestMonthUnit : mostNeurons) {
                    if (mostNeurons.indexOf(bestMonthUnit) != 0) {
                        if (mostNeurons.indexOf(bestMonthUnit) != mostNeurons.size() - 1) {
                            mostNeuronsStr.append(", ");
                        } else {
                            mostNeuronsStr.append(" and ");
                        }
                    }
                    mostNeuronsStr.append(Month.of(bestMonthUnit).name());
                }
                tips.put("INFORMATIVE_TIP", "This year your strong month" + (mostNeurons.size() > 1 ? "s were " : " was ") + mostNeuronsStr);

                neuronsProgressChart.setTitle("This Year's Neurons");
                break;
            case "Today":
            default:
                long hourlyNeurons = 0;
                for (float hour = 0f; hour <= nowHour; hour++) {
                    long currentHour = db.getHourOfTodayNeurons((int) hour);
                     hourlyNeurons += currentHour;
                    neuronsEntries.add(new Entry(hour, (float) hourlyNeurons));
                    if (currentHour == mostNeuronsHolder) {
                        mostNeurons.add((int) hour);
                    } else if (currentHour > mostNeuronsHolder) {
                        mostNeuronsHolder = (int) currentHour;
                        mostNeurons.clear();
                        mostNeurons.add((int) hour);
                    }
                }

                final String[] mHours = new String[]{
                        "0am", "1am", "2am", "3am", "4am", "5am", "6am", "7am", "8am", "9am", "10am", "11am",
                        "12am", "1pm", "2pm", "3pm", "4pm", "5pm", "6pm", "7pm", "8pm", "9pm", "10pm", "11pm"
                };
                for (int bestHourUnit : mostNeurons) {
                    if (mostNeurons.indexOf(bestHourUnit) != 0) {
                        if (mostNeurons.indexOf(bestHourUnit) != mostNeurons.size() - 1) {
                            mostNeuronsStr.append(", ");
                        } else {
                            mostNeuronsStr.append(" and ");
                        }
                    }
                    mostNeuronsStr.append(mHours[bestHourUnit - 1]);
                }
                tips.put("INFORMATIVE_TIP", "Today your strong hour" + (mostNeurons.size() > 1 ? "s were " : " was ") + mostNeuronsStr);

                neuronsProgressChart.setTitle("Today's Neurons");
                break;
        }
        return neuronsEntries;
    }

    private void initGeneralBarChart(BarChart chart) {
        chart.setDrawBarShadow(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setFitBars(true);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
    }

    private void initGeneralPieChart(CustomChart chart) {
        ((PieChart) chart.getChart()).setUsePercentValues(true);
        ((PieChart) chart.getChart()).setDrawEntryLabels(false);
        ((PieChart) chart.getChart()).setTransparentCircleColor(Color.BLACK);
        ((PieChart) chart.getChart()).setTransparentCircleAlpha(110);
        ((PieChart) chart.getChart()).setHoleRadius(58f);
        ((PieChart) chart.getChart()).setTransparentCircleRadius(61f);
        ((PieChart) chart.getChart()).getDescription().setEnabled(false);
        ((PieChart) chart.getChart()).setExtraOffsets(5, 10, 5, 5);
        ((PieChart) chart.getChart()).setDragDecelerationFrictionCoef(0.95f);
        ((PieChart) chart.getChart()).setRotationEnabled(false);
        ((PieChart) chart.getChart()).setHighlightPerTapEnabled(true);
        ((PieChart) chart.getChart()).animateY(1000, Easing.EaseInOutQuad);
    }

    private void initGeneralSpiderChart(CustomChart chart) {
        ((RadarChart) chart.getChart()).getDescription().setEnabled(false);
        ((RadarChart) chart.getChart()).setWebLineWidth(1f);
        ((RadarChart) chart.getChart()).setWebColor(getResources().getColor(R.color.brain1));
        ((RadarChart) chart.getChart()).setWebLineWidthInner(1f);
        ((RadarChart) chart.getChart()).setWebColorInner(getResources().getColor(R.color.brain1));
        ((RadarChart) chart.getChart()).setWebAlpha(100);
        ((RadarChart) chart.getChart()).animateXY(1000, 1000, Easing.EaseInOutQuad);
        ((RadarChart) chart.getChart()).setRotationEnabled(false);
    }

    private void initGeneralLineChart(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setDrawGridBackground(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
    }
}
