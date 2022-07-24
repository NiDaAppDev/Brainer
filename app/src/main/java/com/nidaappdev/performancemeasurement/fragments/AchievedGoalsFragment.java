package com.nidaappdev.performancemeasurement.fragments;


import static com.nidaappdev.performancemeasurement.util.Constants.*;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.adroitandroid.chipcloud.ChipCloud;
import com.adroitandroid.chipcloud.ChipListener;
import com.nidaappdev.performancemeasurement.customObjects.Goal;
import com.nidaappdev.performancemeasurement.customViews.Tutorial.ButtonsLocation;
import com.nidaappdev.performancemeasurement.customViews.Tutorial.Shape.ShapeType;
import com.nidaappdev.performancemeasurement.customViews.Tutorial.TutorialConfiguration;
import com.nidaappdev.performancemeasurement.customViews.Tutorial.TutorialSequence;
import com.nidaappdev.performancemeasurement.customViews.Tutorial.TutorialSequenceListener;
import com.nidaappdev.performancemeasurement.customViews.Tutorial.TutorialView;
import com.nidaappdev.performancemeasurement.databaseObjects.GoalDBHelper;
import com.nidaappdev.performancemeasurement.RecyclerViewAdapters.AchievedGoalsAdapter;
import com.nidaappdev.performancemeasurement.R;
import com.nidaappdev.performancemeasurement.activities.MainActivity;
import com.nidaappdev.performancemeasurement.customViews.NestedRecyclerView.NestedRecyclerView;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.IOnBackPressed;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.PublicMethods;
import com.nidaappdev.performancemeasurement.util.PrefUtil;
import com.github.mmin18.widget.RealtimeBlurView;
import com.google.android.material.circularreveal.CircularRevealFrameLayout;

import org.joda.time.format.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AchievedGoalsFragment extends Fragment implements IOnBackPressed {

    private View v, tutorialNoTarget;
    private TutorialSequence tutorialSequence;
    private TutorialView.Builder tutorialStation;
    private NestedRecyclerView achievedGoalsList;
    private AchievedGoalsAdapter mainAchievedGoalsAdapter;
    private RealtimeBlurView blurBackground;
    private CircularRevealFrameLayout sortGoalsDialog;
    private RadioGroup sortByGroup, ascDescGroup;
    private ChipCloud tagFilter;
    private RelativeLayout sortGoalsButton;
    private ArrayList<Goal> achievedGoalsArrayList;
    private PrefUtil.AchievedSortMode sortMode;
    private boolean ascending = true;
    private ArrayList<Integer> filters = new ArrayList<>(), tempFilters = new ArrayList<>();
    private GoalDBHelper goalDB;

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

        goalDB = new GoalDBHelper(getContext());

        achievedGoalsArrayList = goalDB.getAchievedGoalsArrayList();

        tutorialNoTarget = v.findViewById(R.id.tutorialNoTarget);
        blurBackground = v.findViewById(R.id.achieved_goals_fragment_blur_view);
        sortGoalsDialog = v.findViewById(R.id.achieved_goals_fragment_sort_goals_dialog_container);
        sortByGroup = v.findViewById(R.id.achieved_goals_fragment_sort_by_radio_group);
        ascDescGroup = v.findViewById(R.id.achieved_goals_fragment_asc_desc_radio_group);
        tagFilter = v.findViewById(R.id.achieved_goals_fragment_tag_filter);
        sortGoalsButton = v.findViewById(R.id.achieved_goals_fragment_sort_btn);


        initGoalsList(v);

        tagFilter.removeAllViews();
        ArrayList<String> allTags = goalDB.getAllTags();
        tagFilter.addChips(allTags.toArray(new String[0]));
        for (int i = 0; i < allTags.size(); i++) {
            filters.add(i);
        }

        PublicMethods.sortAchievedGoals(requireContext(), PrefUtil.getAchievedSortMode(), PrefUtil.getAchievedGoalsAscending(), achievedGoalsArrayList, tagFilter, filters);

        initListeners();

        if (!PrefUtil.finishedTutorial(ACHIEVED_GOALS_PAGE_NAME) && !PrefUtil.skippedTutorial(ACHIEVED_GOALS_PAGE_NAME)) {
            Handler handler = new Handler();
            handler.postDelayed(() -> showTutorial(), 5);
        }

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initListeners() {
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

        blurBackground.setOnClickListener(v -> {
            if (sortGoalsDialog.getVisibility() == View.VISIBLE) {
                closeSortGoalsDialog(false);
            }
        });

        sortGoalsButton.setOnClickListener(v -> {
            closeSortGoalsDialog(true);
            mainAchievedGoalsAdapter.setExpandedItem(-1);
            mainAchievedGoalsAdapter.notifyDataSetChanged();
        });
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
                achievedGoalsList,
                (MainActivity) getActivity());
        achievedGoalsList.setHasFixedSize(true);
        achievedGoalsList.setLayoutManager(new LinearLayoutManager(getContext()));
        achievedGoalsList.setAdapter(mainAchievedGoalsAdapter);
        ((SimpleItemAnimator) Objects.requireNonNull(achievedGoalsList.getItemAnimator())).setSupportsChangeAnimations(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private List<TutorialView.Builder> getTutorialStations() {
        int firstGoalIndex = mainAchievedGoalsAdapter.getGoalIndex(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME);

        View firstGoal = achievedGoalsList.getLayoutManager().findViewByPosition(firstGoalIndex);

        List<TutorialView.Builder> tutorialStations = new ArrayList<>();
        TutorialConfiguration config = PublicMethods.tutorialConfig();
        MainActivity activity = (MainActivity) getActivity();

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .enableDotAnimation(false)
                .dismissOnTouch(true)
                .enableIcon(false)
                .setTargetPadding(0)
                .setTarget(tutorialNoTarget)
                .setInfoText("Here, in the Achieved Goals page, all of the goals you have created and " +
                        "finished will appear.\n" +
                        "I'll create a couple of example finished goals for this tutorial, and after " +
                        "We finish it I'll remove them.\n" +
                        "Lets start reviewing what you can do here!\n\n" +
                        "(Click anywhere to continue)");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .setTarget(firstGoal.findViewById(R.id.achievedCardView))
                .setShape(ShapeType.RECTANGLE)
                .setBackOnlyDelayMillisAddition(700)
                .performClick(true, false, false)
                .setInfoText("Here's one of the goals I've created.\n" +
                        "Click on it to see more information about it.");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .enableDotAnimation(false)
                .dismissOnTouch(true)
                .enableIcon(false)
                .setTargetPadding(0)
                .setTarget(tutorialNoTarget)
                .setInfoText("This is how a finished goal looks.\n" +
                        "When finishing a goal, you'll have to insert some information about your " +
                        "experience doing it.\n" +
                        "Each field that you fill will be represented here by something.\n" +
                        "Let's understand what's the meaning of everything in here...");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .enableDotAnimation(false)
                .dismissOnTouch(true)
                .enableIcon(false)
                .setTargetPadding(0)
                .setTarget(tutorialNoTarget)
                .setInfoText("First, lets have a look at the background color.\n" +
                        "The color of the finished goals card represents the difficulty of " +
                        "finishing the goal that you've inserted (from 1 to 5).\n\n" +
                        "Here's a legend for the colors:\n" +
                        "Bronze = 1\n" +
                        "Silver = 2\n" +
                        "Gold = 3\n" +
                        "Purple = 4\n" +
                        "Black = 5");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .setShape(ShapeType.RECTANGLE)
                .setTarget(firstGoal.findViewById(R.id.achieved_goal_medal_holder))
                .setInfoText("Second, lets have a look at the medals.\n" +
                        "The number of medals represents the level of how evolved you felt from " +
                        "finishing the goal that you've inserted (from 1 to 5).");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .setShape(ShapeType.RECTANGLE)
                .setTarget(firstGoal.findViewById(R.id.achieved_goal_satisfaction_smiley))
                .setInfoText("Third, lets have a look at the Smiley.\n" +
                        "The smiley of the finished goals represents the satisfaction you got from " +
                        "finishing the goal that you've inserted (from 1 to 5).\n\n" +
                        "Here's a legend for the smileys:\n" +
                        "Red/Sad = 1\n" +
                        "Orange/Slightly Sad = 2\n" +
                        "Yellow/Neutral = 3\n" +
                        "Light Green/Slightly Smiling = 4\n" +
                        "Dark Green/Smiling = 5");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .setShape(ShapeType.RECTANGLE)
                .setTarget(firstGoal.findViewById(R.id.expanded_achieved_goal_tags_parent))
                .setInfoText("And last, lets have a look at the tags.\n" +
                        "When you finish a goal, you can add tags which it belongs to.\n" +
                        "If you add none, automatically it'll get the tag \"Other\".\n" +
                        "What good does the tag offer, you ask?");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .enableDotAnimation(false)
                .dismissOnTouch(true)
                .enableIcon(false)
                .setTargetPadding(0)
                .setTarget(tutorialNoTarget)
                .setInfoText("When you have a lot of goals things can get messy.\n" +
                        "When it gets to a real mess, you can sort the list by clicking the three " +
                        "lines in the top right.\n" +
                        "One of the sorting filters is the tags we've talked about.\n" +
                        "I'll leave this functionality to your curiosity for later.");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .enableDotAnimation(false)
                .dismissOnTouch(true)
                .enableIcon(false)
                .setTargetPadding(0)
                .setTarget(tutorialNoTarget)
                .setInfoText("That's it for the \"Achieved Goals\" page.\n" +
                        "The example goals that I've created will now get deleted.\n" +
                        "If you forget something, you can always go to settings and set this tutorial " +
                        "as unfinished.\n" +
                        "If you've went through the pages in the order suggested, you're through " +
                        "the tutorial!\n" +
                        "Go on and start using the app.\n\n" +
                        "Good luck!");
        tutorialStations.add(tutorialStation);

        return tutorialStations;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void buildTutorial() {
        int firstGoalIndex = mainAchievedGoalsAdapter.getGoalIndex(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME),
                secondGoalIndex = mainAchievedGoalsAdapter.getGoalIndex(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME);
        try {
            if (firstGoalIndex == -1 || secondGoalIndex == -1 ||
                    !goalDB.isAchieved(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME) ||
                    !goalDB.isAchieved(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME)) {
                goalDB.removeGoal(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME);
                goalDB.removeGoal(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME);

                ArrayList<String> tags = new ArrayList<>();
                tags.add("Other");

                Goal firstGoal = new Goal(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME,
                        TUTORIAL_FIRST_EXAMPLE_GOAL_DESCRIPTION,
                        "",
                        0,
                        3600,
                        0,
                        0,
                        5,
                        5,
                        5,
                        true,
                        tags,
                        new SimpleDateFormat("dd/MM/yyyy").format(new Date())),
                secondGoal = new Goal(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME,
                        TUTORIAL_SECOND_EXAMPLE_GOAL_DESCRIPTION,
                        TUTORIAL_FIRST_EXAMPLE_GOAL_NAME,
                        0,
                        1800,
                        0,
                        0,
                        1,
                        5,
                        5,
                        true,
                        tags,
                        new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

                goalDB.addGoal(firstGoal);
                mainAchievedGoalsAdapter.updateAddedActiveGoal(firstGoal.getName());
                goalDB.addGoal(secondGoal);
                mainAchievedGoalsAdapter.updateAddedActiveGoal(secondGoal.getName());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                List<TutorialView.Builder> tutorialStations = getTutorialStations();

                tutorialSequence = new TutorialSequence(requireContext(), ACHIEVED_GOALS_PAGE_NAME, tutorialStations)
                        .enableSkipButton(true)
                        .enableBackButton(true)
                        .enableRestartButton(true)
                        .setListener(new TutorialSequenceListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onResume(int index) {

                            }

                            @Override
                            public void onNext(int fromIndex, int toIndex) {
                                View currentTargetView = tutorialStations.get(fromIndex).getTargetView(),
                                        nextTargetView = tutorialStations.get(toIndex).getTargetView();

                                int firstGoalIndex = mainAchievedGoalsAdapter.getGoalIndex(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME);
                                View firstGoal = achievedGoalsList.getLayoutManager().findViewByPosition(firstGoalIndex);

                                if(currentTargetView.equals(tutorialNoTarget) &&
                                        nextTargetView.equals(firstGoal.findViewById(R.id.achievedCardView))) {
                                    mainAchievedGoalsAdapter.scrollToPositionInRecyclerView(firstGoalIndex, achievedGoalsList.getLayoutManager());
                                }
                            }

                            @Override
                            public void onBack(int fromIndex, int toIndex) {
                                View currentTargetView = tutorialStations.get(fromIndex).getTargetView(),
                                        previousTargetView = tutorialStations.get(toIndex).getTargetView();

                                int firstGoalIndex = mainAchievedGoalsAdapter.getGoalIndex(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME);
                                View firstGoal = achievedGoalsList.getLayoutManager().findViewByPosition(firstGoalIndex);

                                if(currentTargetView.equals(tutorialNoTarget) &&
                                        previousTargetView.equals(firstGoal.findViewById(R.id.achievedCardView))) {
                                    firstGoal.callOnClick();
                                }
                            }

                            @Override
                            public void onRestart() {

                            }

                            @Override
                            public void onSkip() {
                                int goalIndex = goalDB.getActiveGoalsArrayList().indexOf(goalDB.getGoalByName(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME));
                                if (goalIndex != -1) {
                                    mainAchievedGoalsAdapter.notifyItemRemoved(goalIndex);
                                }
                                goalIndex = goalDB.getActiveGoalsArrayList().indexOf(goalDB.getGoalByName(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME));
                                if (goalIndex != -1) {
                                    mainAchievedGoalsAdapter.notifyItemRemoved(goalIndex);
                                }
                                mainAchievedGoalsAdapter.updateGoalsList();
                            }

                            @Override
                            public void onFinish() {
                                int goalIndex = goalDB.getActiveGoalsArrayList().indexOf(goalDB.getGoalByName(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME));
                                if (goalIndex != -1) {
                                    mainAchievedGoalsAdapter.notifyItemRemoved(goalIndex);
                                }
                                goalIndex = goalDB.getActiveGoalsArrayList().indexOf(goalDB.getGoalByName(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME));
                                if (goalIndex != -1) {
                                    mainAchievedGoalsAdapter.notifyItemRemoved(goalIndex);
                                }
                                mainAchievedGoalsAdapter.updateGoalsList();
                                mainAchievedGoalsAdapter.notifyDataSetChanged();
                                PrefUtil.setTutorialStationIndex(ACHIEVED_GOALS_PAGE_NAME, 0);
                            }
                        });
            }, 1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showTutorial() {
        buildTutorial();
        Handler handler = new Handler();
        handler.postDelayed(() -> tutorialSequence.resumeTutorial(), 1);
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
        ArrayList<String> allTags = goalDB.getAllTags();
        tagFilter.addChips(allTags.toArray(new String[0]));
        filters = PrefUtil.getAchievedGoalsFilters();
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
