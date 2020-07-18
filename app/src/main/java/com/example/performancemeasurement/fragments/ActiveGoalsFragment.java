package com.example.performancemeasurement.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.example.performancemeasurement.GoalAndDatabaseObjects.Goal;
import com.example.performancemeasurement.GoalAndDatabaseObjects.GoalDBHelper;
import com.example.performancemeasurement.GoalRecyclerViewAdapters.ActiveGoalsAdapter;
import com.example.performancemeasurement.R;
import com.example.performancemeasurement.activities.MainActivity;
import com.example.performancemeasurement.customViews.NestedRecyclerView.NestedRecyclerView;
import com.example.performancemeasurement.publicClassesAndInterfaces.IOnBackPressed;
import com.google.android.material.circularreveal.CircularRevealFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;
import java.util.Random;

public class ActiveGoalsFragment extends Fragment implements IOnBackPressed {

    View v;
    FloatingActionButton fab;
    View blurBackground;
    CircularRevealFrameLayout dialog;
    NestedRecyclerView activeGoalsList;
    ActiveGoalsAdapter activeGoalsAdapter;
    GoalDBHelper db;

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

        db = new GoalDBHelper(getContext());

        fab = v.findViewById(R.id.fab);
        blurBackground = v.findViewById(R.id.blur_view);
        dialog = v.findViewById(R.id.add_new_goal_dialog_container);

        initGoalsList(v);

        //db.clearDatabase();
        initGoals(50);

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

    private void initGoals(int numOfGoals) {
        Goal goal;
        int progress;
        String parent;
        for(int i = 1; i <= numOfGoals; i ++){
            parent = new Random().nextBoolean() ? "1" : "" ;
            progress = new Random().nextInt(100) + 1;
            goal = new Goal(Integer.toString(i), i + "" + i, parent, progress, 100, false);
            db.addGoal(goal);
        }
        activeGoalsAdapter.swapCursor(db.getActiveGoalsCursor());
    }

    /**
     * Sets up the RecyclerView.
     * @param v is the one used in the onCreateView method.
     */
    public void initGoalsList(View v){
        activeGoalsList = v.findViewById(R.id.recycler_view);
        activeGoalsAdapter = new ActiveGoalsAdapter(getContext(), db.getActiveGoalsArrayList(), db.getActiveGoalsCursor(), activeGoalsList, (MainActivity) getActivity());
        activeGoalsList.setLayoutManager(new LinearLayoutManager(getContext()));
        activeGoalsList.setAdapter(activeGoalsAdapter);
        ((SimpleItemAnimator) Objects.requireNonNull(activeGoalsList.getItemAnimator())).setSupportsChangeAnimations(false);
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
        blurBackground.setVisibility(View.VISIBLE);
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

