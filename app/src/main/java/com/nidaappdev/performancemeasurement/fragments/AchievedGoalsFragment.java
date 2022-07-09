package com.nidaappdev.performancemeasurement.fragments;


import static android.content.ContentValues.TAG;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.adroitandroid.chipcloud.ChipCloud;
import com.adroitandroid.chipcloud.ChipListener;
import com.nidaappdev.performancemeasurement.GoalAndDatabaseObjects.Goal;
import com.nidaappdev.performancemeasurement.GoalAndDatabaseObjects.GoalDBHelper;
import com.nidaappdev.performancemeasurement.GoalRecyclerViewAdapters.AchievedGoalsAdapter;
import com.nidaappdev.performancemeasurement.R;
import com.nidaappdev.performancemeasurement.activities.MainActivity;
import com.nidaappdev.performancemeasurement.customViews.NestedRecyclerView.NestedRecyclerView;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.IOnBackPressed;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.PublicMethods;
import com.nidaappdev.performancemeasurement.util.PrefUtil;
import com.github.mmin18.widget.RealtimeBlurView;
import com.google.android.material.circularreveal.CircularRevealFrameLayout;

import java.util.ArrayList;
import java.util.Objects;

public class AchievedGoalsFragment extends Fragment implements IOnBackPressed {

    View v;
    NestedRecyclerView achievedGoalsList;
    AchievedGoalsAdapter mainAchievedGoalsAdapter;
    RealtimeBlurView blurBackground;
    CircularRevealFrameLayout sortGoalsDialog;
    RadioGroup sortByGroup, ascDescGroup;
    RadioButton byNameRadio, byFinishDateRadio, byDifficultyRadio, byEvolvingRadio, bySatisfactionRadio, ascRadio, descRadio;
    ChipCloud tagFilter;
    RelativeLayout sortGoalsButton;
    ArrayList<Goal> achievedGoalsArrayList;
    PrefUtil.AchievedSortMode sortMode;
    boolean ascending = true;
    ArrayList<Integer> filters = new ArrayList<>(), tempFilters = new ArrayList<>();
    GoalDBHelper db;

    public AchievedGoalsFragment() {
        // Required empty public constructor
    }


    /**
     * Defines all the objects that are used in the class.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        v = inflater.inflate(R.layout.fragment_achieved_goals, container, false);

        db = new GoalDBHelper(getContext());

        achievedGoalsArrayList = db.getAchievedGoalsArrayList();

        blurBackground = v.findViewById(R.id.achieved_goals_fragment_blur_view);
        sortGoalsDialog = v.findViewById(R.id.achieved_goals_fragment_sort_goals_dialog_container);
        sortByGroup = v.findViewById(R.id.achieved_goals_fragment_sort_by_radio_group);
        ascDescGroup = v.findViewById(R.id.achieved_goals_fragment_asc_desc_radio_group);
        byNameRadio = v.findViewById(R.id.achieved_goals_fragment_name_radio_btn);
        byFinishDateRadio = v.findViewById(R.id.achieved_goals_fragment_finish_date_radio_btn);
        byDifficultyRadio = v.findViewById(R.id.achieved_goals_fragment_difficulty_radio_btn);
        byEvolvingRadio = v.findViewById(R.id.achieved_goals_fragment_evolving_radio_btn);
        bySatisfactionRadio = v.findViewById(R.id.achieved_goals_fragment_satisfaction_radio_btn);
        ascRadio = v.findViewById(R.id.achieved_goals_fragment_asc_radio_btn);
        descRadio = v.findViewById(R.id.achieved_goals_fragment_desc_radio_btn);
        tagFilter = v.findViewById(R.id.achieved_goals_fragment_tag_filter);
        sortGoalsButton = v.findViewById(R.id.achieved_goals_fragment_sort_btn);


        initGoalsList(v);

        tagFilter.removeAllViews();
        ArrayList<String> allTags = db.getAllTags();
        tagFilter.addChips(allTags.toArray(new String[0]));
        for (int i = 0; i < allTags.size(); i++) {
            filters.add(i);
        }

        PublicMethods.sortAchievedGoals(requireContext(), PrefUtil.getAchievedSortMode(), PrefUtil.getAchievedGoalsAscending(), achievedGoalsArrayList, tagFilter, filters);



        tagFilter.setChipListener(new ChipListener() {
            @Override
            public void chipSelected(int index) {
                tempFilters.add(index);
            }

            @Override
            public void chipDeselected(int index) {
                tempFilters.remove((Object)index);
            }
        });

        blurBackground.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (sortGoalsDialog.getVisibility() == View.VISIBLE) {
                    closeSortGoalsDialog(false);
                }
            }
        });

        sortGoalsButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                closeSortGoalsDialog(true);
                mainAchievedGoalsAdapter.setExpandedItem(-1);
                mainAchievedGoalsAdapter.notifyDataSetChanged();
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.sort_goals_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.sort_goals_fragment_menu_sort_item) {
            openSortGoalsDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets up the RecyclerView.
     *
     * @param v is the one used in the onCreateView method.
     */
    public void initGoalsList(View v) {
        achievedGoalsList = v.findViewById(R.id.achieved_goals_recycler_view);

        mainAchievedGoalsAdapter = new AchievedGoalsAdapter(getContext(),
                achievedGoalsArrayList,
                db.getAchievedGoalsCursor(),
                achievedGoalsList,
                (MainActivity) getActivity());
        achievedGoalsList.setHasFixedSize(true);
        achievedGoalsList.setLayoutManager(new LinearLayoutManager(getContext()));
        achievedGoalsList.setAdapter(mainAchievedGoalsAdapter);
        ((SimpleItemAnimator) Objects.requireNonNull(achievedGoalsList.getItemAnimator())).setSupportsChangeAnimations(false);
    }

    /**
     * Shows the shadow appears when dialog is opened.
     */
    public void fadeBlurIn() {
        blurBackground.setVisibility(View.INVISIBLE);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        blurBackground.startAnimation(alphaAnimation);
        blurBackground.setVisibility(View.VISIBLE);
        blurBackground.setClickable(true);
    }


    /**
     * Hides the shadow appears when dialog is opened.
     */
    public void fadeBlurOut() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        blurBackground.startAnimation(alphaAnimation);
        blurBackground.setVisibility(View.GONE);
        blurBackground.setClickable(false);
    }

    /**
     * Opens the sort-goals dialog.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void openSortGoalsDialog() {

        sortGoalsDialog.setVisibility(View.VISIBLE);
        fadeBlurIn();
        sortGoalsDialog.setClickable(true);
        sortMode = PrefUtil.getAchievedSortMode();
        switch (sortMode) {
            case FinishDate:
                sortByGroup.check(R.id.achieved_goals_fragment_finish_date_radio_btn);
                break;
            case Difficulty:
                sortByGroup.check(R.id.achieved_goals_fragment_difficulty_radio_btn);
                break;
            case Evolving:
                sortByGroup.check(R.id.achieved_goals_fragment_evolving_radio_btn);
                break;
            case Satisfaction:
                sortByGroup.check(R.id.achieved_goals_fragment_satisfaction_radio_btn);
                break;
            default:
                sortByGroup.check(R.id.achieved_goals_fragment_name_radio_btn);
                break;
        }

        ascending = PrefUtil.getAchievedGoalsAscending();
        if (!ascending) {
            ascDescGroup.check(R.id.achieved_goals_fragment_desc_radio_btn);
        } else {
            ascDescGroup.check(R.id.achieved_goals_fragment_asc_radio_btn);
        }

        tagFilter.removeAllViews();
        ArrayList<String> allTags = db.getAllTags();
        tagFilter.addChips(allTags.toArray(new String[0]));
        filters = PrefUtil.getAchievedGoalsFilters();
        Log.d(TAG, "openSortGoalsDialog: " + filters);
        for (int filter : filters) {
            tagFilter.setSelectedChip(filter);
        }

        tempFilters = new ArrayList<>(filters);
    }

    /**
     * Closes the sort-goals dialog.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void closeSortGoalsDialog(boolean sort) {

        if (sort) {
            if (sortByGroup.getCheckedRadioButtonId() == R.id.achieved_goals_fragment_name_radio_btn) {
                sortMode = PrefUtil.AchievedSortMode.Name;
            } else if (sortByGroup.getCheckedRadioButtonId() == R.id.achieved_goals_fragment_finish_date_radio_btn) {
                sortMode = PrefUtil.AchievedSortMode.FinishDate;
            } else if (sortByGroup.getCheckedRadioButtonId() == R.id.achieved_goals_fragment_difficulty_radio_btn) {
                sortMode = PrefUtil.AchievedSortMode.Difficulty;
            } else if (sortByGroup.getCheckedRadioButtonId() == R.id.achieved_goals_fragment_evolving_radio_btn) {
                sortMode = PrefUtil.AchievedSortMode.Evolving;
            } else if (sortByGroup.getCheckedRadioButtonId() == R.id.achieved_goals_fragment_satisfaction_radio_btn) {
                sortMode = PrefUtil.AchievedSortMode.Satisfaction;
            }

            if (ascDescGroup.getCheckedRadioButtonId() == R.id.achieved_goals_fragment_asc_radio_btn) {
                ascending = true;
            } else if (ascDescGroup.getCheckedRadioButtonId() == R.id.achieved_goals_fragment_desc_radio_btn) {
                ascending = false;
            }

            filters = new ArrayList<>(tempFilters);

            PublicMethods.sortAchievedGoals(requireContext(), sortMode, ascending, achievedGoalsArrayList, tagFilter, filters);
        }

        sortGoalsDialog.setVisibility(View.INVISIBLE);
        fadeBlurOut();
        sortGoalsDialog.setClickable(false);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onBackPressed() {
        if (sortGoalsDialog.getVisibility() == View.VISIBLE) {
            closeSortGoalsDialog(false);
            return true;
        } else {
            return false;
        }
    }
}
