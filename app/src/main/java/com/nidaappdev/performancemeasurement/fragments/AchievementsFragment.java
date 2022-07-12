package com.nidaappdev.performancemeasurement.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.nidaappdev.performancemeasurement.R;
import com.nidaappdev.performancemeasurement.RecyclerViewAdapters.AchievementsAdapter;
import com.nidaappdev.performancemeasurement.customObjects.Achievement;
import com.nidaappdev.performancemeasurement.customViews.NestedRecyclerView.NestedRecyclerView;
import com.nidaappdev.performancemeasurement.databaseObjects.GoalDBHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class AchievementsFragment extends Fragment {

    private View v;
    private NestedRecyclerView achievementsList;
    private AchievementsAdapter achievementsAdapter;
    private ArrayList<Achievement> achievementArrayList;
    private GoalDBHelper db;

    public AchievementsFragment() {
        // Required empty public constructor
    }

    /**
     * Defines all the objects that are used in the class.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_achievements, container, false);

        db = new GoalDBHelper(requireContext());

        achievementArrayList = db.getAchievementsArrayList();

        initAchievementsList();

        return v;
    }

    private void initAchievementsList() {
        achievementsList = v.findViewById(R.id.achievements_recycler_view);

        Collections.sort(achievementArrayList, (achievement1, achievement2) -> achievement1.isAchieved() ? (achievement2.isAchieved() ? 0 : -1) : (achievement2.isAchieved() ? 1 : 0));

        achievementsAdapter = new AchievementsAdapter(requireContext(), achievementArrayList);

        achievementsList.setHasFixedSize(true);
        achievementsList.setLayoutManager(new LinearLayoutManager(getContext()));
        achievementsList.setAdapter(achievementsAdapter);
        ((SimpleItemAnimator) Objects.requireNonNull(achievementsList.getItemAnimator())).setSupportsChangeAnimations(false);
    }
}