package com.nidaappdev.performancemeasurement.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.nidaappdev.performancemeasurement.R;

public class AchievementsFragment extends Fragment {
    public AchievementsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_achievements, container, false);

        return v;
    }
}