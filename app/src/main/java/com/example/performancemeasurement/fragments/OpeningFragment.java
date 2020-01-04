package com.example.performancemeasurement.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.performancemeasurement.R;

import java.util.zip.Inflater;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;

public class OpeningFragment extends Fragment {


    public OpeningFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_opening, container, false);
        return v;
    }

}
