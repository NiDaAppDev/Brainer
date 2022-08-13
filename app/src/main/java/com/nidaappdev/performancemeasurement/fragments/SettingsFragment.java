package com.nidaappdev.performancemeasurement.fragments;

import static com.nidaappdev.performancemeasurement.util.Constants.*;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.nidaappdev.performancemeasurement.R;
import com.nidaappdev.performancemeasurement.util.PrefUtil;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.warkiz.tickseekbar.OnSeekChangeListener;
import com.warkiz.tickseekbar.SeekParams;
import com.warkiz.tickseekbar.TickSeekBar;

public class SettingsFragment extends Fragment {

    private TickSeekBar pomodoroLengthSeekbar, pomodoroTimeOutLengthSeekbar;
    private SwitchMaterial notifyPomodoroOnTimerSwitch;
    private CheckBox mainCheckBox, activeGoalsCheckBox, achievedGoalsCheckBox;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        pomodoroLengthSeekbar = v.findViewById(R.id.settings_pomodoro_length_seekbar);
        pomodoroTimeOutLengthSeekbar = v.findViewById(R.id.settings_pomodoro_time_out_length_seekbar);
        notifyPomodoroOnTimerSwitch = v.findViewById(R.id.stopwatch_notify_pomodoro_switch);
        mainCheckBox = v.findViewById(R.id.main_page_tutorial_checkbox);
        activeGoalsCheckBox = v.findViewById(R.id.active_goals_page_tutorial_checkbox);
        achievedGoalsCheckBox = v.findViewById(R.id.achieved_goals_page_tutorial_checkbox);

        setPomodoroObjectsEnabled((PrefUtil.getTimeMethod() != PrefUtil.TimeMethod.Pomodoro && PrefUtil.getTimeMethod() != PrefUtil.TimeMethod.TimeOut)
                || PrefUtil.getTimerState() != OpeningFragment.TimerState.Running);

        setRegularTimerObjectsEnabled((PrefUtil.getTimeMethod() != PrefUtil.TimeMethod.Timer && PrefUtil.getTimeMethod() != PrefUtil.TimeMethod.TimerTimeOut)
                || PrefUtil.getTimerState() != OpeningFragment.TimerState.Running);

        initSettingsObjectsValues();

        initSettingsObjectsListeners();

        return v;
    }

    private void setPomodoroObjectsEnabled(boolean enabled) {
        pomodoroLengthSeekbar.setEnabled(enabled);
        pomodoroTimeOutLengthSeekbar.setEnabled(enabled);
    }

    private void setRegularTimerObjectsEnabled(boolean enabled) {
        notifyPomodoroOnTimerSwitch.setEnabled(enabled);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initSettingsObjectsValues() {
        pomodoroLengthSeekbar.setProgress(PrefUtil.getPomodoroLength());
        pomodoroTimeOutLengthSeekbar.setProgress(PrefUtil.getPomodoroTimeOutLength());
        notifyPomodoroOnTimerSwitch.setChecked(PrefUtil.getSuggestBreak());
        if (PrefUtil.finishedTutorial(MAIN_PAGE_NAME) ||
                PrefUtil.skippedTutorial(MAIN_PAGE_NAME))
            mainCheckBox.setChecked(true);
        if (PrefUtil.finishedTutorial(ACTIVE_GOALS_PAGE_NAME) ||
                PrefUtil.skippedTutorial(ACTIVE_GOALS_PAGE_NAME))
            activeGoalsCheckBox.setChecked(true);
        if (PrefUtil.finishedTutorial(ACHIEVED_GOALS_PAGE_NAME) ||
                PrefUtil.skippedTutorial(ACHIEVED_GOALS_PAGE_NAME))
            achievedGoalsCheckBox.setChecked(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initSettingsObjectsListeners() {
        pomodoroLengthSeekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {

            }

            @Override
            public void onStartTrackingTouch(TickSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(TickSeekBar seekBar) {
                PrefUtil.setPomodoroLength(seekBar.getProgress());
            }
        });

        pomodoroTimeOutLengthSeekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {

            }

            @Override
            public void onStartTrackingTouch(TickSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(TickSeekBar seekBar) {
                PrefUtil.setPomodoroTimeOutLength(seekBar.getProgress());
            }
        });

        notifyPomodoroOnTimerSwitch.setOnCheckedChangeListener((switchView, isOn) -> {
            PrefUtil.setSuggestBreak(isOn);
        });

        mainCheckBox.setOnCheckedChangeListener((checkBox, isChecked) -> {
            if (isChecked) {
                if (!PrefUtil.skippedTutorial(MAIN_PAGE_NAME)) {
                    PrefUtil.setSkippedTutorial(MAIN_PAGE_NAME, true);
                }
                return;
            }
            if (PrefUtil.finishedTutorial(MAIN_PAGE_NAME)) {
                PrefUtil.setFinishedTutorial(MAIN_PAGE_NAME, false);
            }
            if (PrefUtil.skippedTutorial(MAIN_PAGE_NAME)) {
                PrefUtil.setSkippedTutorial(MAIN_PAGE_NAME, false);
            }
        });

        activeGoalsCheckBox.setOnCheckedChangeListener((checkBox, isChecked) -> {
            if (isChecked) {
                if (!PrefUtil.skippedTutorial(ACTIVE_GOALS_PAGE_NAME)) {
                    PrefUtil.setSkippedTutorial(ACTIVE_GOALS_PAGE_NAME, true);
                }
                return;
            }
            if (PrefUtil.finishedTutorial(ACTIVE_GOALS_PAGE_NAME)) {
                PrefUtil.setFinishedTutorial(ACTIVE_GOALS_PAGE_NAME, false);
            }
            if (PrefUtil.skippedTutorial(ACTIVE_GOALS_PAGE_NAME)) {
                PrefUtil.setSkippedTutorial(ACTIVE_GOALS_PAGE_NAME, false);
            }
        });

        achievedGoalsCheckBox.setOnCheckedChangeListener((checkBox, isChecked) -> {
            if (isChecked) {
                if (!PrefUtil.skippedTutorial(ACHIEVED_GOALS_PAGE_NAME)) {
                    PrefUtil.setSkippedTutorial(ACHIEVED_GOALS_PAGE_NAME, true);
                }
                return;
            }
            if (PrefUtil.finishedTutorial(ACHIEVED_GOALS_PAGE_NAME)) {
                PrefUtil.setFinishedTutorial(ACHIEVED_GOALS_PAGE_NAME, false);
            }
            if (PrefUtil.skippedTutorial(ACHIEVED_GOALS_PAGE_NAME)) {
                PrefUtil.setSkippedTutorial(ACHIEVED_GOALS_PAGE_NAME, false);
            }
        });
    }
}