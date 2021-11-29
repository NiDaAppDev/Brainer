package com.example.performancemeasurement.fragments;


import static android.content.ContentValues.TAG;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.adroitandroid.chipcloud.Chip;
import com.adroitandroid.chipcloud.ChipCloud;
import com.adroitandroid.chipcloud.ChipListener;
import com.example.performancemeasurement.GoalAndDatabaseObjects.Goal;
import com.example.performancemeasurement.GoalAndDatabaseObjects.GoalDBHelper;
import com.example.performancemeasurement.GoalRecyclerViewAdapters.AchievedGoalsAdapter;
import com.example.performancemeasurement.R;
import com.example.performancemeasurement.activities.MainActivity;
import com.example.performancemeasurement.customViews.NestedRecyclerView.NestedRecyclerView;
import com.example.performancemeasurement.publicClassesAndInterfaces.IOnBackPressed;
import com.google.android.material.circularreveal.CircularRevealFrameLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class AchievedGoalsFragment extends Fragment implements IOnBackPressed {

    View v;
    NestedRecyclerView achievedGoalsList;
    AchievedGoalsAdapter mainAchievedGoalsAdapter;
    View blurBackground;
    CircularRevealFrameLayout sortGoalsDialog;
    RadioGroup sortByGroup, ascDescGroup;
    RadioButton byNameRadio, byFinishDateRadio, byDifficultyRadio, byEvolvingRadio, bySatisfactionRadio, ascRadio, descRadio;
    ChipCloud tagFilter;
    RelativeLayout sortGoalsButton;
    ArrayList<Goal> achievedGoalsArrayList;
    int sortMode;
    boolean ascending = true;
    ArrayList<Integer> filters = new ArrayList<>(), tempFilters = new ArrayList<>();
    GoalDBHelper db;

    public AchievedGoalsFragment() {
        // Required empty public constructor
    }


    /**
     * Defines all the objects that are used in the class.
     */
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

        ArrayList<String> allTags = db.getAllTags();
        Log.d(TAG, "onCreateView: " + allTags);
        for (int i = 0; i < allTags.size(); i++) {
            filters.add(i);
        }


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
            @Override
            public void onClick(View v) {
                if (sortGoalsDialog.getVisibility() == View.VISIBLE) {
                    closeSortGoalsDialog(false);
                }
            }
        });

        sortGoalsButton.setOnClickListener(new View.OnClickListener() {
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
    public void openSortGoalsDialog() {

        sortGoalsDialog.setVisibility(View.VISIBLE);
        fadeBlurIn();
        sortGoalsDialog.setClickable(true);
        switch (sortMode) {
            case 1:
                sortByGroup.check(R.id.achieved_goals_fragment_name_radio_btn);
                break;
            case 2:
                sortByGroup.check(R.id.achieved_goals_fragment_finish_date_radio_btn);
                break;
            case 3:
                sortByGroup.check(R.id.achieved_goals_fragment_difficulty_radio_btn);
                break;
            case 4:
                sortByGroup.check(R.id.achieved_goals_fragment_evolving_radio_btn);
                break;
            case 5:
                sortByGroup.check(R.id.achieved_goals_fragment_satisfaction_radio_btn);
                break;
            default:
                sortByGroup.check(R.id.achieved_goals_fragment_name_radio_btn);
                break;
        }

        if (!ascending) {
            ascDescGroup.check(R.id.achieved_goals_fragment_desc_radio_btn);
        } else {
            ascDescGroup.check(R.id.achieved_goals_fragment_asc_radio_btn);
        }

        tagFilter.removeAllViews();
        ArrayList<String> allTags = db.getAllTags();
        tagFilter.addChips(allTags.toArray(new String[0]));
        for (int filter : filters) {
            tagFilter.setSelectedChip(filter);
        }

        tempFilters = new ArrayList<>(filters);
    }

    /**
     * Closes the sort-goals dialog.
     */
    public void closeSortGoalsDialog(boolean sort) {

        if (sort) {
            if (sortByGroup.getCheckedRadioButtonId() == R.id.achieved_goals_fragment_name_radio_btn) {
                sortMode = 1;
            } else if (sortByGroup.getCheckedRadioButtonId() == R.id.achieved_goals_fragment_finish_date_radio_btn) {
                sortMode = 2;
            } else if (sortByGroup.getCheckedRadioButtonId() == R.id.achieved_goals_fragment_difficulty_radio_btn) {
                sortMode = 3;
            } else if (sortByGroup.getCheckedRadioButtonId() == R.id.achieved_goals_fragment_evolving_radio_btn) {
                sortMode = 4;
            } else if (sortByGroup.getCheckedRadioButtonId() == R.id.achieved_goals_fragment_satisfaction_radio_btn) {
                sortMode = 5;
            }

            if (ascDescGroup.getCheckedRadioButtonId() == R.id.achieved_goals_fragment_asc_radio_btn) {
                ascending = true;
            } else if (ascDescGroup.getCheckedRadioButtonId() == R.id.achieved_goals_fragment_desc_radio_btn) {
                ascending = false;
            }

            filters = new ArrayList<>(tempFilters);

            sort(sortMode, ascending, achievedGoalsArrayList);
        }

        sortGoalsDialog.setVisibility(View.INVISIBLE);
        fadeBlurOut();
        sortGoalsDialog.setClickable(false);
    }

    /**
     * Sort the cards.
     */
    public void sort(int sortMode, boolean ascending, ArrayList<Goal> arrayListToSort) {

        this.sortMode = sortMode;
        this.ascending = ascending;

        ArrayList<Goal> filteredArrayListToRemoveFromOriginal = new ArrayList<>();

        arrayListToSort.clear();
        arrayListToSort.addAll(db.getAchievedGoalsArrayList());

        for (Goal goal : arrayListToSort) {
            boolean goalHasOneOfTheTags = false;
            for (int filterTag : filters) {
                if (goal.getTagsAsArrayList().contains(((Chip) tagFilter.getChildAt(filterTag)).getText().toString())) {
                    goalHasOneOfTheTags = true;
                }
            }
            if (!goalHasOneOfTheTags) {
                filteredArrayListToRemoveFromOriginal.add(goal);
            }
        }

        arrayListToSort.removeAll(filteredArrayListToRemoveFromOriginal);


        switch (sortMode) {
            case 1:
                Collections.sort(arrayListToSort, new Comparator<Goal>() {
                    @Override
                    public int compare(Goal o1, Goal o2) {
                        try {
                            int numericO1 = Integer.parseInt(o1.getName()),
                                    numericO2 = Integer.parseInt(o2.getName());
                            return numericO1 - numericO2;
                        } catch (Exception e) {
                            return o1.getName().compareToIgnoreCase(o2.getName());
                        }
                    }
                });
                break;
            case 2:
                Collections.sort(arrayListToSort, new Comparator<Goal>() {
                    @Override
                    public int compare(Goal o1, Goal o2) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            Date d1 = sdf.parse(o1.getFinishDate()),
                                    d2 = sdf.parse(o2.getFinishDate());
                            return d1.compareTo(d2);
                        } catch (ParseException e) {
                            return 0;
                        }
                    }
                });
                break;
            case 3:
                Collections.sort(arrayListToSort, new Comparator<Goal>() {
                    @Override
                    public int compare(Goal o1, Goal o2) {
                        return o1.getDifficulty() - o2.getDifficulty();
                    }
                });
                break;
            case 4:
                Collections.sort(arrayListToSort, new Comparator<Goal>() {
                    @Override
                    public int compare(Goal o1, Goal o2) {
                        return o1.getEvolving() - o2.getEvolving();
                    }
                });
                break;
            case 5:
                Collections.sort(arrayListToSort, new Comparator<Goal>() {
                    @Override
                    public int compare(Goal o1, Goal o2) {
                        return o1.getSatisfaction() - o2.getSatisfaction();
                    }
                });
                break;
            default:
                break;
        }

        if (!ascending) {
            Collections.reverse(arrayListToSort);
        }

    }


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
