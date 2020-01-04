package com.example.performancemeasurement.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import com.example.performancemeasurement.publicClassesAndInterfaces.IOnBackPressed;
import com.example.performancemeasurement.R;
import com.google.android.material.circularreveal.CircularRevealFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ActiveGoalsFragment extends Fragment implements IOnBackPressed {

    View v;
    FloatingActionButton fab;
    View blurBackground;
    CircularRevealFrameLayout dialog;

    public ActiveGoalsFragment() {
        // Required empty public constructor
    }


    /**
     * Defines all the objects that are used in the class.
     * Sets up FloatingActionButton and blur background's behavior when clicking them.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_active_goals, container, false);

        fab = v.findViewById(R.id.fab);
        blurBackground = v.findViewById(R.id.blur_background);
        dialog = v.findViewById(R.id.fragment_container);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        blurBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog.getVisibility() == View.VISIBLE){
                    closeDialog();
                }
            }
        });


        return v;
    }

    /**
     * Opens the add-new-active-goal dialog.
     */
    public void openDialog() {

        fab.setExpanded(true);
        fadeBlurIn();

    }


    /**
     * Closes the add-new-active-goal dialog.
     */
    public void closeDialog() {

        fab.setExpanded(false);
        fadeBlurOut();

    }

    /**
     * Shows the shadow appears when dialog is opened.
     */
    public void fadeBlurIn(){
        blurBackground.setVisibility(View.INVISIBLE);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        blurBackground.startAnimation(alphaAnimation);
    }


    /**
     * Hides the shadow appears when dialog is opened.
     */
    public void fadeBlurOut(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        blurBackground.startAnimation(alphaAnimation);
        blurBackground.setVisibility(View.GONE);
    }

    /**
     * Controls what happens when pressing back, whether it closes the dialog when it's opened or it closes the fragment / the whole app.
     */
    @Override
    public boolean onBackPressed() {
        if (dialog.getVisibility() == View.VISIBLE) {
            closeDialog();
            return true;
        } else {
            return false;
        }
    }

}

