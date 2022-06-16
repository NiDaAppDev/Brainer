package com.example.performancemeasurement.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.performancemeasurement.R;
import com.example.performancemeasurement.util.PrefUtil;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.warkiz.tickseekbar.OnSeekChangeListener;
import com.warkiz.tickseekbar.SeekParams;
import com.warkiz.tickseekbar.TickSeekBar;

public class SettingsFragment extends Fragment {

    private TickSeekBar pomodoroLengthSeekbar, pomodoroTimeOutLengthSeekbar;
    private SwitchMaterial notifyPomodoroOnTimerSwitch;

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
        notifyPomodoroOnTimerSwitch = v.findViewById(R.id.timer_notify_pomodoro_switch);

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

    private void setRegularTimerObjectsEnabled(boolean enabled){
        notifyPomodoroOnTimerSwitch.setEnabled(enabled);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initSettingsObjectsValues() {
        pomodoroLengthSeekbar.setProgress(PrefUtil.getPomodoroLength());
        pomodoroTimeOutLengthSeekbar.setProgress(PrefUtil.getPomodoroTimeOutLength());
        notifyPomodoroOnTimerSwitch.setChecked(PrefUtil.getSuggestBreak());
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

        notifyPomodoroOnTimerSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            PrefUtil.setSuggestBreak(b);
        });
    }
}