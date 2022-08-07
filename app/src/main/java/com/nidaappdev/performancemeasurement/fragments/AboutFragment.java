package com.nidaappdev.performancemeasurement.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.nidaappdev.performancemeasurement.BuildConfig;
import com.nidaappdev.performancemeasurement.R;


import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutFragment extends Fragment {


    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Element versionElement = new Element();
        versionElement.setTitle(BuildConfig.VERSION_NAME);
        return new AboutPage(getContext())
                .isRTL(false)
                .setDescription(getString(R.string.app_description))
                .setImage(R.drawable.app_icon)
                .addGroup("Contact and more")
                .addEmail("nidaappdev@gmail.com")
                .addGitHub("NiDaAppDev")
                .addGroup("App Info")
                .addItem(versionElement)
                .create();
    }

}
