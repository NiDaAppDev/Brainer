package com.nidaappdev.performancemeasurement.fragments;


import static com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.PublicMethods.tutorialConfig;
import static com.nidaappdev.performancemeasurement.util.Constants.*;

import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.github.mmin18.widget.RealtimeBlurView;
import com.google.android.material.circularreveal.CircularRevealFrameLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.chip.Chip;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;
import com.hootsuite.nachos.validator.ChipifyingNachoValidator;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.nidaappdev.congratulator.CongratulationView;
import com.nidaappdev.performancemeasurement.Lottie.DialogHandler;
import com.nidaappdev.performancemeasurement.R;
import com.nidaappdev.performancemeasurement.RecyclerViewAdapters.ActiveGoalsAdapter;
import com.nidaappdev.performancemeasurement.activities.MainActivity;
import com.nidaappdev.performancemeasurement.customObjects.Goal;
import com.nidaappdev.performancemeasurement.customViews.NestedRecyclerView.NestedRecyclerView;
import com.nidaappdev.performancemeasurement.customViews.Tutorial.ButtonsLocation;
import com.nidaappdev.performancemeasurement.customViews.Tutorial.Shape.ShapeType;
import com.nidaappdev.performancemeasurement.customViews.Tutorial.TutorialConfiguration;
import com.nidaappdev.performancemeasurement.customViews.Tutorial.TutorialSequence;
import com.nidaappdev.performancemeasurement.customViews.Tutorial.TutorialSequenceListener;
import com.nidaappdev.performancemeasurement.customViews.Tutorial.TutorialView;
import com.nidaappdev.performancemeasurement.databaseObjects.GoalDBHelper;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.IOnBackPressed;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.PublicMethods;
import com.nidaappdev.performancemeasurement.util.PrefUtil;
import com.warkiz.tickseekbar.OnSeekChangeListener;
import com.warkiz.tickseekbar.SeekParams;
import com.warkiz.tickseekbar.TickSeekBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import br.com.sapereaude.maskedEditText.MaskedEditText;

public class ActiveGoalsFragment extends Fragment implements IOnBackPressed {

    private View v, tutorialNoTarget;
    private ExtendedFloatingActionButton fab;
    private FloatingActionButton addAsSubgoalsFab, deleteFab;
    private TextView addAsSubgoalsLabel, deleteLabel;
    private RealtimeBlurView blurBackground;
    private CircularRevealFrameLayout addNewGoalDialog, setAsSubgoalOfDialog, finishGoalDialog, sortGoalsDialog;
    private ScrollView finishGoalDialogScrollView;
    private RadioGroup sortByGroup, ascDescGroup;
    private RadioButton byNameRadio, byProgressRadio, ascRadio, descRadio;
    private RelativeLayout cancelAddingNewGoal, addNewGoal, cancelSetAsSubgoalOf, setAsSubgoalOf, cancelFinishGoal, finishGoalButton, sortGoalsButton;
    private EditText newGoalsName, newGoalsDescription;
    private MaskedEditText newGoalsTimeEstimated;
    private TickSeekBar difficultySeekBar, evolvingSeekBar, satisfactionSeekBar;
    private NachoTextView tagPickerEditText;
    private TextInputLayout tagPickerContainer;
    private NestedRecyclerView activeGoalsList, setAsSubgoalOfGoalsList;
    private ActiveGoalsAdapter mainActiveGoalsAdapter, setAsSubgoalOfGoalsAdapter;
    private GoalDBHelper goalDB;
    private ArrayList<Goal> activeGoalsArrayList, setAsSubgoalOfGoalsArrayList;
    private TutorialView.Builder tutorialStation;
    private TutorialSequence tutorialSequence;
    private PrefUtil.ActiveSortMode sortMode;
    private boolean ascending = true;
    private boolean areAllFabsVisible;


    public ActiveGoalsFragment() {
        // Required empty public constructor
    }


    /**
     * Defines all the objects that are used in the class.
     * Sets up FloatingActionButton and blur background's behavior when clicking them.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        v = inflater.inflate(R.layout.fragment_active_goals, container, false);

        goalDB = new GoalDBHelper(getContext());

//        db.clearDatabase();

        tutorialNoTarget = v.findViewById(R.id.tutorialNoTarget);
        fab = v.findViewById(R.id.fab);
        addAsSubgoalsFab = v.findViewById(R.id.fab_add_selected_goals_as_sub_goal_of);
        deleteFab = v.findViewById(R.id.fab_delete_selected_goals);
        addAsSubgoalsLabel = v.findViewById(R.id.add_as_subgoals_label);
        deleteLabel = v.findViewById(R.id.delete_selected_label);
        blurBackground = v.findViewById(R.id.blur_view_active_goals_fragment);
        sortGoalsDialog = v.findViewById(R.id.active_goals_fragment_sort_goals_dialog_container);
        sortByGroup = v.findViewById(R.id.active_goals_fragment_sort_by_radio_group);
        ascDescGroup = v.findViewById(R.id.active_goals_fragment_asc_desc_radio_group);
        byNameRadio = v.findViewById(R.id.active_goals_fragment_name_radio_btn);
        byProgressRadio = v.findViewById(R.id.active_goals_fragment_progress_radio_btn);
        ascRadio = v.findViewById(R.id.active_goals_fragment_asc_radio_btn);
        descRadio = v.findViewById(R.id.active_goals_fragment_desc_radio_btn);
        sortGoalsButton = v.findViewById(R.id.active_goals_fragment_sort_btn);
        addNewGoalDialog = v.findViewById(R.id.add_new_goal_dialog_container);
        cancelAddingNewGoal = v.findViewById(R.id.cancel_btn_active_goals_fragment);
        addNewGoal = v.findViewById(R.id.add_btn_active_goals_fragment);
        setAsSubgoalOfDialog = v.findViewById(R.id.set_as_subgoal_of_dialog_container);
        cancelSetAsSubgoalOf = v.findViewById(R.id.subgoal_dialog_cancel_button);
        setAsSubgoalOf = v.findViewById(R.id.subgoal_dialog_confirm_button);
        finishGoalDialog = v.findViewById(R.id.finish_goal_dialog);
        finishGoalDialogScrollView = v.findViewById(R.id.finish_goal_dialog_scroll_container);
        cancelFinishGoal = v.findViewById(R.id.finish_goal_dialog_cancel_button);
        difficultySeekBar = v.findViewById(R.id.difficulty_picker);
        evolvingSeekBar = v.findViewById(R.id.evolving_picker);
        satisfactionSeekBar = v.findViewById(R.id.satisfaction_picker);
        tagPickerContainer = v.findViewById(R.id.tag_picker_container);
        tagPickerEditText = v.findViewById(R.id.tag_picker_edit_text);
        finishGoalButton = v.findViewById(R.id.finish_goal_dialog_finish_button);
        newGoalsName = v.findViewById(R.id.name_et_active_goals_fragment);
        newGoalsDescription = v.findViewById(R.id.description_et_active_goals_fragment);
        newGoalsTimeEstimated = v.findViewById(R.id.time_estimation_et_active_goals_fragment);

        shrinkFabActions();

        activeGoalsArrayList = goalDB.getActiveGoalsArrayList();

        PublicMethods.sortActiveGoals(requireContext(), PrefUtil.getActiveSortMode(), PrefUtil.getActiveGoalsAscending(), activeGoalsArrayList);

        initGoalsList();
//        initGoals(50);

        initListeners();

        if (!PrefUtil.finishedTutorial(ACTIVE_GOALS_PAGE_NAME) && !PrefUtil.skippedTutorial(ACTIVE_GOALS_PAGE_NAME)) {
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.sort_goals_fragment_menu_sort_item) {
            openSortGoalsDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initGoals(int numOfGoals) {
        Goal goal;
        int progress;
        String parent, startDate;
        for (int i = 1; i <= numOfGoals; i++) {
            if (i != 1) {
                parent = new Random().nextBoolean() ? "1" : "";
            } else {
                parent = "";
            }
            progress = new Random().nextInt(100) + 1;

            goal = new Goal(Integer.toString(i), i + "" + i, parent, progress, 100, 0, 0, 0, 0, 0, false, new ArrayList<String>(), "");
            goalDB.addGoal(goal);
        }
    }

    /**
     * Sets up the RecyclerView.
     */
    public void initGoalsList() {
        activeGoalsList = v.findViewById(R.id.active_goals_recycler_view);
        mainActiveGoalsAdapter = new ActiveGoalsAdapter(getContext(),
                activeGoalsArrayList,
                activeGoalsList,
                (MainActivity) getActivity(),
                fab,
                blurBackground,
                finishGoalDialog,
                difficultySeekBar,
                evolvingSeekBar,
                satisfactionSeekBar,
                tagPickerEditText);
        activeGoalsList.setHasFixedSize(true);
        activeGoalsList.setLayoutManager(new LinearLayoutManager(getContext()));
        activeGoalsList.setAdapter(mainActiveGoalsAdapter);
        ((SimpleItemAnimator) Objects.requireNonNull(activeGoalsList.getItemAnimator())).setSupportsChangeAnimations(false);

        /**
         Enables one Card hovering another (the hovered one getting expanded and when it ends being
         hovered it collapses
         */
//        HoverItemDecoration itemDecoration = new HoverItemDecoration(
//                new HoveringCallback() {
//                    @Override
//                    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
//                        super.attachToRecyclerView(recyclerView);
//                        addOnDropListener(new HoveringCallback.OnDroppedListener() {
//                            @Override
//                            public void onDroppedOn(ActiveGoalsAdapter.ActiveGoalsViewHolder viewHolder, ActiveGoalsAdapter.ActiveGoalsViewHolder target) {
//
//                            }
//                        });
//                    }
//                },
//                new ItemBackgroundCallback() {
//                    private int hoverColor = Color.parseColor("#e9effb");
//
//                    @Override
//                    public int getDefaultBackgroundColor(@NonNull RecyclerView.ViewHolder viewHolder) {
//                        return Color.WHITE;
//                    }
//
//                    @Override
//                    public int getDraggingBackgroundColor(@NonNull RecyclerView.ViewHolder viewHolder) {
//                        return Color.WHITE;
//                    }
//
//                    @Override
//                    public int getHoverBackgroundColor(@NonNull RecyclerView.ViewHolder viewHolder) {
//                        return hoverColor;
//                    }
//                });
//
//        itemDecoration.attachToRecyclerView(activeGoalsList);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initListeners() {
        fab.setOnClickListener(view -> {
            if (mainActiveGoalsAdapter.getMultiSelectable()) {
                if (!areAllFabsVisible) {
                    expandFabActions();
                } else {
                    shrinkFabActions();
                }
            } else {
                openAddNewGoalDialog();
            }
        });

        deleteFab.setOnClickListener(v -> {
            if (mainActiveGoalsAdapter.getMultiSelected().size() > 0) {
                for (Goal selectedGoal : mainActiveGoalsAdapter.getMultiSelected()) {
                    goalDB.removeGoal(selectedGoal.getName());
                    activeGoalsArrayList = goalDB.getActiveGoalsArrayList();

                    PublicMethods.sortActiveGoals(requireContext(), PrefUtil.getActiveSortMode(), PrefUtil.getActiveGoalsAscending(), activeGoalsArrayList);
                    mainActiveGoalsAdapter.notifyItemRemoved(PublicMethods.positionOfGoalInGoalsArrayList(selectedGoal.getName(), activeGoalsArrayList));
                }
                mainActiveGoalsAdapter.emptyMultiSelected();
                mainActiveGoalsAdapter.updateGoalsList();
            }
            mainActiveGoalsAdapter.setMultiSelectable(false);

        });

        addAsSubgoalsFab.setOnClickListener(v -> openSetAsSubgoalOfDialog());

        blurBackground.setOnClickListener(view -> {
            if (addNewGoalDialog.getVisibility() == View.VISIBLE) {
                closeAddNewGoalDialog();
            } else if (setAsSubgoalOfDialog.getVisibility() == View.VISIBLE) {
                closeSetAsSubgoalOfDialog();
            } else if (finishGoalDialog.getVisibility() == View.VISIBLE) {
                closeFinishGoalDialog();
            } else if (sortGoalsDialog.getVisibility() == View.VISIBLE) {
                closeSortGoalsDialog(false);
            }
        });

        sortGoalsButton.setOnClickListener(v -> {
            closeSortGoalsDialog(true);
            mainActiveGoalsAdapter.setExpandedItem(-1);
            mainActiveGoalsAdapter.notifyDataSetChanged();
        });

        cancelAddingNewGoal.setOnClickListener(v -> closeAddNewGoalDialog());

        addNewGoal.setOnClickListener(v -> {
            Goal newGoal = new Goal(newGoalsName.getText().toString(), newGoalsDescription.getText().toString(), PublicMethods.getNewGoalsTimeEstimated(newGoalsTimeEstimated));
            ArrayList<String> allGoalsNames = new ArrayList<>();
            for (Goal goal : goalDB.getAllGoalsArrayList()) {
                allGoalsNames.add(goal.getName());
            }
            if (allGoalsNames.contains(newGoal.getName())) {
                PublicMethods.openIdenticalGoalNameErrorDialog(requireContext(), requireActivity(), newGoal.getName());
            } else if (newGoal.getName().trim().isEmpty()) {
                PublicMethods.openGoalNameNotValidErrorDialog(requireContext(), requireActivity(), newGoal.getName());
            } else if (newGoalsTimeEstimated.getText().toString().isEmpty() || PublicMethods.getNewGoalsTimeEstimated(newGoalsTimeEstimated) == 0) {
                PublicMethods.openGoalTimeEstimatedNotValidErrorDialog(requireContext(), requireActivity());
            } else {
                goalDB.addGoal(newGoal);
                closeAddNewGoalDialog();
                mainActiveGoalsAdapter.updateAddedActiveGoal(newGoal.getName());
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        cancelSetAsSubgoalOf.setOnClickListener(v -> closeSetAsSubgoalOfDialog());

        setAsSubgoalOf.setOnClickListener(v -> {
            if (setAsSubgoalOfGoalsAdapter.getSingleSelected() != null) {
                goalDB.setParentToGoals(setAsSubgoalOfGoalsAdapter.getSingleSelected(), mainActiveGoalsAdapter.getMultiSelected());
                for (Goal goal : mainActiveGoalsAdapter.getMultiSelected()) {
                    mainActiveGoalsAdapter.notifyItemChanged(PublicMethods.positionOfGoalInGoalsArrayList(goal.getName(), activeGoalsArrayList));
                    mainActiveGoalsAdapter.updateGoalsList();
                }
                closeSetAsSubgoalOfDialog();
            } else {
                DialogHandler dialogHandler = new DialogHandler();
                dialogHandler.showDialog(requireActivity(),
                        requireContext(),
                        "No Goal Was Selected",
                        "No goal was selected to be the parent goal of the selected " +
                                "subgoals.",
                        "OK",
                        () -> {
                        },
                        DialogTypes.TYPE_ERROR,
                        "");
            }
        });

        finishGoalDialog.setTag(finishGoalDialog.getVisibility());
        finishGoalDialog.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int newVis = finishGoalDialog.getVisibility();
            if ((int) finishGoalDialog.getTag() != newVis) {
                finishGoalDialog.setTag(finishGoalDialog.getVisibility());
                if (finishGoalDialog.getVisibility() == View.VISIBLE) {

                }
            }
        });

        satisfactionSeekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                switch (seekParams.progress) {
                    case 2:
                        satisfactionSeekBar.setThumbDrawable(getResources().getDrawable(R.drawable.smiley_2));
                        break;
                    case 3:
                        satisfactionSeekBar.setThumbDrawable(getResources().getDrawable(R.drawable.smiley_3));
                        break;
                    case 4:
                        satisfactionSeekBar.setThumbDrawable(getResources().getDrawable(R.drawable.smiley_4));
                        break;
                    case 5:
                        satisfactionSeekBar.setThumbDrawable(getResources().getDrawable(R.drawable.smiley_5));
                        break;
                    case 1:
                    default:
                        satisfactionSeekBar.setThumbDrawable(getResources().getDrawable(R.drawable.smiley_1));
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(TickSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(TickSeekBar seekBar) {

            }
        });

        tagPickerEditText.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN);
        tagPickerEditText.enableEditChipOnTouch(false, false);
        tagPickerEditText.setNachoValidator(new ChipifyingNachoValidator());
        tagPickerEditText.setThreshold(1);
        tagPickerContainer.setOnClickListener(v -> {
            String[] tags = goalDB.getAllTags().toArray(new String[0]);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, tags);
            tagPickerEditText.setAdapter(adapter);
            tagPickerEditText.showDropDown();
        });
        tagPickerEditText.setOnClickListener(v -> {
            String[] tags = goalDB.getAllTags().toArray(new String[0]);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, tags);
            tagPickerEditText.setAdapter(adapter);
            tagPickerEditText.showDropDown();
        });

        ArrayList<String> allTagsArrayList = goalDB.getAllTags();
        tagPickerEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<String> tagsArrayList = new ArrayList<>(allTagsArrayList);
                for (Chip chip : tagPickerEditText.getAllChips())
                    tagsArrayList.remove(chip.getText());
                String[] tags = tagsArrayList.toArray(new String[0]);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, tags);
                tagPickerEditText.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cancelFinishGoal.setOnClickListener(v -> closeFinishGoalDialog());

        finishGoalButton.setOnClickListener(v -> {
            finishGoal();
            //TODO: add a congratulations screen for finishing the goal.
            closeFinishGoalDialog();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private List<TutorialView.Builder> getTutorialStations() {
        int firstGoalIndex = mainActiveGoalsAdapter.getGoalIndex(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME),
                secondGoalIndex = mainActiveGoalsAdapter.getGoalIndex(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME);

        View firstGoal = activeGoalsList.getLayoutManager().findViewByPosition(firstGoalIndex),
                secondGoal = activeGoalsList.getLayoutManager().findViewByPosition(secondGoalIndex);

        List<TutorialView.Builder> tutorialStations = new ArrayList<>();
        TutorialConfiguration config = tutorialConfig();
        MainActivity activity = (MainActivity) getActivity();

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .enableDotAnimation(false)
                .dismissOnTouch(true)
                .enableIcon(false)
                .setTargetPadding(0)
                .setTarget(tutorialNoTarget)
                .setInfoText("Here, in the Active Goals page, all of the goals you have created and " +
                        "haven't finished yet will appear.\n" +
                        "Lets start reviewing what you can do here!\n\n" +
                        "(Click anywhere to continue)");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .setTarget(fab)
                .setButtonsLocation(ButtonsLocation.TOP)
                .performClick(true, false, false)
                .setInfoText("As we've learned, you can add new goals in the main page.\n" +
                        "Well, this page allows you to do so as well.\n\n" +
                        "Click this button to add a new goal");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .setTarget(addNewGoalDialog)
                .setShape(ShapeType.RECTANGLE)
                .performClick(true, false, false)
                .setInfoText("Here, again, you can customize your goal, as well as you did in the " +
                        "main page.\n" +
                        "This time I'll skip the fields filling, because you've seen it already.\n" +
                        "Ive already created a couple example goals for you.");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .setTarget(firstGoal.findViewById(R.id.activeCardView))
                .setBackOnlyDelayMillisAddition(650)
                .setShape(ShapeType.RECTANGLE)
                .performClick(true, false, false)
                .setInfoText("Here's one of the goals I've created.\n" +
                        "Click on it to see more information about it.");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .setTarget(firstGoal.findViewById(R.id.active_goal_edit_btn))
                .performClick(true, false, false)
                .setInfoText("As mentioned in the main page tutorial, you can edit the goals in this " +
                        "page.\n\n" +
                        "Click this button to edit the goal.");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .setShape(ShapeType.RECTANGLE)
                .setTarget(firstGoal.findViewById(R.id.activeCardView))
                .setBackOnlyDelayMillisAddition(650)
                .setInfoText("For now, we'll leave the goal as it is.");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .setShape(ShapeType.RECTANGLE)
                .setDelayMillis(650)
                .performClick(false, true, false)
                .setTarget(secondGoal.findViewById(R.id.activeCardView))
                .setInfoText("Another thing you can do here, is to set goals as sub-goals of others.\n" +
                        "To do that, first you'll have to long-click a goal (for now just click it normally)");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .enableDotAnimation(false)
                .dismissOnTouch(true)
                .enableIcon(false)
                .setBackOnlyDelayMillisAddition(100)
                .setTargetPadding(0)
                .setTarget(tutorialNoTarget)
                .setInfoText("Now every goal you'll click on will be selected.\n\n" +
                        "(Click anywhere to continue)");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .setButtonsLocation(ButtonsLocation.TOP)
                .setDelayMillis(100)
                .performClick(true, false, false)
                .setTarget(fab)
                .setInfoText("After you're finished selecting goals, click here to show options " +
                        "for the selected goals.");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .performClick(true, false, false)
                .setTarget(addAsSubgoalsFab)
                .setBackOnlyDelayMillisAddition(100)
                .setInfoText("Here, you can set this goal as a subgoal of other goals by clicking " +
                        "this button.\n");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .setShape(ShapeType.RECTANGLE)
                .setTarget(setAsSubgoalOfDialog)
                .setInfoText("Choose the parent goal from the list");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .setShape(ShapeType.RECTANGLE)
                .setTarget(setAsSubgoalOf)
                .performClick(true, false, false)
                .setBackOnlyDelayMillisAddition(650)
                .setInfoText("Then click confirm to set the selected goal(s) as a sub-goal of " +
                        "the selected parent-goal.");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .setTarget(firstGoal.findViewById(R.id.expanded_active_goal_sub_goals_recyclerview))
                .setShape(ShapeType.RECTANGLE)
                .setDelayMillis(750)
                .setInfoText("As you can see, subgoals appear under their parents.\n" +
                        "A goal can have multiple subgoals, and also multiple parent goals.\n" +
                        "A goal can't be its own parent\n" +
                        "Clicking a subgoal in this list will lead you to it in the active goals " +
                        "list, but you can examine it later.");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .setTarget(firstGoal.findViewById(R.id.finish_goal_button))
                .setShape(ShapeType.RECTANGLE)
                .setBackOnlyDelayMillisAddition(650)
                .setInfoText("When you finish working on a goal, click this button.\n" +
                        "It will set the goal as finished, and send it to the \"Achieved Goals\" page.\n" +
                        "You cannot finish a goal unless it has no sub-goals or all its subgoals" +
                        "are finished.\n" +
                        "Therefore, this time only I'll finish the sub-goal for you so you'll be " +
                        "able to finish this one, the we'll finish this one together.");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .setTarget(finishGoalDialog)
                .setShape(ShapeType.RECTANGLE)
                .setInfoText("Clicking the finish button leads you to this dialog, where you'll " +
                        "fill out some information about your experience doing the goal.");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .setDelayMillis(100)
                .setTarget(tagPickerEditText)
                .setShape(ShapeType.RECTANGLE)
                .setInfoText("The above are pretty much self explanatory, so I skipped explaining them.\n" +
                        "This field lets you customize which tag the goal you finish belongs to.\n" +
                        "If it exists already, it'll suggest it. If not, you can add new tags by typing " +
                        "them and hit the enter when you finish.\n" +
                        "If you insert only one new tag, or its the last one you insert, you don't " +
                        "have to hit the enter, it'll just convert to a tag automatically. " +
                        "(any goal can have multiple tags)\n" +
                        "If you don't insert any tag, the goal will automatically get the tag " +
                        "\"Other\".\n");
        tutorialStations.add(tutorialStation);


        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .setTarget(finishGoalButton)
                .performClick(true, false, false)
                .setShape(ShapeType.RECTANGLE)
                .setBackOnlyDelayMillisAddition(750)
                .setInfoText("When you're finished filling this dialog out, click this button to " +
                        "finish the goal.");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .enableDotAnimation(false)
                .dismissOnTouch(true)
                .enableIcon(false)
                .setTargetPadding(0)
                .setTarget(tutorialNoTarget)
                .setInfoText("When you have a lot of goals things can get messy.\n" +
                        "I suggest not to set too many goals without finishing them, but if it " +
                        "gets to a real mess, you can sort the list by clicking the three lines in the top right.\n" +
                        "I'll also leave this one to your curiosity for later.");
        tutorialStations.add(tutorialStation);

        tutorialStation = new TutorialView.Builder(activity)
                .setConfiguration(config)
                .enableDotAnimation(false)
                .dismissOnTouch(true)
                .enableIcon(false)
                .setTargetPadding(0)
                .setTarget(tutorialNoTarget)
                .setInfoText("That's it for the \"Active Goals\" page.\n" +
                        "The example goals that I've created will now get deleted.\n" +
                        "To learn about other pages and features in the app, click on the three lines" +
                        "in the top left, and click on the page you'd like to explore - A tutorial " +
                        "will wait for you there if you haven't finished it yet.\n" +
                        "Goals you finish will first appear in \"Achieved Goals\" page, there you'll be " +
                        "able to see all the information you need to know about them, so I suggest " +
                        "to go there next.\n\n" +
                        "If you forget something, you can always go to settings and set this tutorial " +
                        "as unfinished.\n\n" +
                        "Good luck!");
        tutorialStations.add(tutorialStation);

        return tutorialStations;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void buildTutorial() {

        int firstGoalIndex = mainActiveGoalsAdapter.getGoalIndex(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME),
                secondGoalIndex = mainActiveGoalsAdapter.getGoalIndex(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME);
        try {
            if (firstGoalIndex == -1 || secondGoalIndex == -1 ||
                    goalDB.isAchieved(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME) ||
                    goalDB.isAchieved(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME)) {
                goalDB.removeGoal(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME);
                goalDB.removeGoal(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME);
                newGoalsName.setText(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME);
                newGoalsDescription.setText(TUTORIAL_FIRST_EXAMPLE_GOAL_DESCRIPTION);
                newGoalsTimeEstimated.setText(TUTORIAL_FIRST_EXAMPLE_GOAL_TIME_ESTIMATED);
                addNewGoal.callOnClick();

                newGoalsName.setText(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME);
                newGoalsDescription.setText(TUTORIAL_SECOND_EXAMPLE_GOAL_DESCRIPTION);
                newGoalsTimeEstimated.setText(TUTORIAL_SECOND_EXAMPLE_GOAL_TIME_ESTIMATED);
                addNewGoal.callOnClick();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                List<TutorialView.Builder> tutorialStations = getTutorialStations();

                tutorialSequence = new TutorialSequence(requireContext(), ACTIVE_GOALS_PAGE_NAME, tutorialStations)
                        .enableSkipButton(true)
                        .enableBackButton(true)
                        .enableRestartButton(true)
                        .setListener(new TutorialSequenceListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onResume(int index) {
                                View currentTargetView = tutorialStations.get(index).getTargetView(),
                                        previousTargetView = index > 0 ? tutorialStations.get(index - 1).getTargetView() : currentTargetView,
                                        nextTargetView = tutorialStations.size() - 1 > index ? tutorialStations.get(index + 1).getTargetView() : currentTargetView;
                                final int[] firstGoalIndex = {mainActiveGoalsAdapter.getGoalIndex(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME)};
                                int secondGoalIndex = mainActiveGoalsAdapter.getGoalIndex(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME);
                                final View[] firstGoal = {activeGoalsList.getLayoutManager().findViewByPosition(firstGoalIndex[0])};
                                View secondGoal = activeGoalsList.getLayoutManager().findViewByPosition(secondGoalIndex);

                                if (currentTargetView.equals(addNewGoalDialog)) {
                                    fab.callOnClick();
                                } else if (firstGoal[0] != null && currentTargetView.equals(firstGoal[0].findViewById(R.id.active_goal_edit_btn))) {
                                    mainActiveGoalsAdapter.scrollToPositionInRecyclerView(firstGoalIndex[0], activeGoalsList.getLayoutManager());
                                    firstGoal[0].callOnClick();
                                } else if (firstGoal[0] != null && currentTargetView.equals(firstGoal[0].findViewById(R.id.activeCardView)) &&
                                        previousTargetView.equals(firstGoal[0].findViewById(R.id.active_goal_edit_btn))) {
                                    mainActiveGoalsAdapter.scrollToPositionInRecyclerView(firstGoalIndex[0], activeGoalsList.getLayoutManager());
                                    firstGoal[0].findViewById(R.id.active_goal_edit_btn).callOnClick();
                                } else if (secondGoal != null && firstGoal[0] != null &&
                                        ((currentTargetView.equals(tutorialNoTarget) && previousTargetView.equals(secondGoal.findViewById(R.id.activeCardView))) ||
                                                (currentTargetView.equals(fab) && nextTargetView.equals(addAsSubgoalsFab)))) {
                                    mainActiveGoalsAdapter.scrollToPositionInRecyclerView(secondGoalIndex, activeGoalsList.getLayoutManager());
                                    secondGoal.performLongClick();
                                } else if (secondGoal != null && firstGoal[0] != null &&
                                        currentTargetView.equals(addAsSubgoalsFab) &&
                                        previousTargetView.equals(fab)) {
                                    mainActiveGoalsAdapter.scrollToPositionInRecyclerView(secondGoalIndex, activeGoalsList.getLayoutManager());
                                    secondGoal.performLongClick();
                                    fab.callOnClick();
                                } else if (secondGoal != null &&
                                        currentTargetView.equals(setAsSubgoalOfDialog)) {
                                    mainActiveGoalsAdapter.scrollToPositionInRecyclerView(secondGoalIndex, activeGoalsList.getLayoutManager());
                                    secondGoal.performLongClick();
                                    fab.callOnClick();
                                    addAsSubgoalsFab.callOnClick();
                                } else if (secondGoal != null &&
                                        currentTargetView.equals(setAsSubgoalOf)) {
                                    mainActiveGoalsAdapter.scrollToPositionInRecyclerView(secondGoalIndex, activeGoalsList.getLayoutManager());
                                    secondGoal.performLongClick();
                                    fab.callOnClick();
                                    addAsSubgoalsFab.callOnClick();
                                    handler.postDelayed(() -> setAsSubgoalOfGoalsList.getChildAt(setAsSubgoalOfGoalsAdapter.getGoalIndex(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME)).callOnClick(), 10);
                                } else if (firstGoal[0] != null &&
                                        (currentTargetView.equals(firstGoal[0].findViewById(R.id.expanded_active_goal_sub_goals_recyclerview)) ||
                                                currentTargetView.equals(firstGoal[0].findViewById(R.id.finish_goal_button)))) {
                                    mainActiveGoalsAdapter.scrollToPositionInRecyclerView(firstGoalIndex[0], activeGoalsList.getLayoutManager());
                                    firstGoal[0].callOnClick();
                                } else if (currentTargetView.equals(finishGoalDialog) ||
                                        currentTargetView.equals(finishGoalButton)) {
                                    try {
                                        goalDB.removeGoal(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME);
                                        mainActiveGoalsAdapter.updateGoalsList();
                                    } finally {
                                        Handler handler = new Handler();
                                        handler.postDelayed(() -> {
                                            firstGoalIndex[0] = mainActiveGoalsAdapter.getGoalIndex(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME);
                                            firstGoal[0] = activeGoalsList.getLayoutManager().findViewByPosition(firstGoalIndex[0]);
                                            mainActiveGoalsAdapter.scrollToPositionInRecyclerView(firstGoalIndex[0], activeGoalsList.getLayoutManager());
                                            firstGoal[0].callOnClick();
                                            firstGoal[0].findViewById(R.id.finish_goal_button).callOnClick();
                                        }, 100);
                                    }
                                } else if (currentTargetView.equals(tagPickerEditText)) {
                                    try {
                                        goalDB.removeGoal(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME);
                                        mainActiveGoalsAdapter.updateGoalsList();
                                    } finally {
                                        Handler handler = new Handler();
                                        handler.postDelayed(() -> {
                                            firstGoalIndex[0] = mainActiveGoalsAdapter.getGoalIndex(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME);
                                            firstGoal[0] = activeGoalsList.getLayoutManager().findViewByPosition(firstGoalIndex[0]);
                                            mainActiveGoalsAdapter.scrollToPositionInRecyclerView(firstGoalIndex[0], activeGoalsList.getLayoutManager());
                                            firstGoal[0].callOnClick();
                                            firstGoal[0].findViewById(R.id.finish_goal_button).callOnClick();
                                        }, 100);
                                    }
                                    finishGoalDialogScrollView.post(() -> finishGoalDialogScrollView.fullScroll(View.FOCUS_DOWN));
                                }
                            }

                            @Override
                            public void onNext(int fromIndex, int toIndex) {
                                View currentTargetView = tutorialStations.get(fromIndex).getTargetView();
                                View nextTargetView = tutorialStations.get(toIndex).getTargetView();
                                int firstGoalIndex = mainActiveGoalsAdapter.getGoalIndex(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME);
                                View firstGoal = activeGoalsList.getLayoutManager().findViewByPosition(firstGoalIndex);
                                if (currentTargetView.equals(addNewGoalDialog)) {
                                    closeAddNewGoalDialog();
                                } else if (firstGoal != null && currentTargetView.equals(firstGoal.findViewById(R.id.activeCardView)) &&
                                        !nextTargetView.equals(firstGoal.findViewById(R.id.active_goal_edit_btn))) {
                                    firstGoal.findViewById(R.id.edit_cancel_button).callOnClick();
                                    firstGoal.callOnClick();
                                } else if (currentTargetView.equals(setAsSubgoalOfDialog)) {
                                    setAsSubgoalOfGoalsList.getChildAt(setAsSubgoalOfGoalsAdapter.getGoalIndex(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME)).callOnClick();
                                } else if (firstGoal != null && currentTargetView.equals(setAsSubgoalOf)) {
                                    firstGoal.callOnClick();
                                    mainActiveGoalsAdapter.scrollToPositionInRecyclerView(firstGoalIndex, activeGoalsList.getLayoutManager());
                                } else if (firstGoal != null && currentTargetView.equals(firstGoal.findViewById(R.id.finish_goal_button))) {
                                    try {
                                        goalDB.removeGoal(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME);
                                        mainActiveGoalsAdapter.updateGoalsList();
                                    } finally {
                                        handler.postDelayed(() -> {
                                            mainActiveGoalsAdapter.scrollToPositionInRecyclerView(firstGoalIndex, activeGoalsList.getLayoutManager());
                                            firstGoal.callOnClick();
                                            firstGoal.findViewById(R.id.finish_goal_button).callOnClick();
                                        }, 100);
                                    }
                                } else if (currentTargetView.equals(finishGoalDialog)) {
                                    finishGoalDialogScrollView.post(() -> finishGoalDialogScrollView.fullScroll(View.FOCUS_DOWN));
                                }
                            }

                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onBack(int fromIndex, int toIndex) {
                                View currentTargetView = tutorialStations.get(fromIndex).getTargetView();
                                View previousTargetView = tutorialStations.get(toIndex).getTargetView();
                                final int[] firstGoalIndex = {mainActiveGoalsAdapter.getGoalIndex(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME)};
                                int secondGoalIndex = mainActiveGoalsAdapter.getGoalIndex(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME);
                                final View[] firstGoal = {activeGoalsList.getLayoutManager().findViewByPosition(firstGoalIndex[0])};
                                View secondGoal = activeGoalsList.getLayoutManager().findViewByPosition(secondGoalIndex);
                                if (currentTargetView.equals(addNewGoalDialog)) {
                                    closeAddNewGoalDialog();
                                } else if (firstGoal[0] != null &&
                                        currentTargetView.equals(firstGoal[0].findViewById(R.id.activeCardView)) &&
                                        previousTargetView.equals(addNewGoalDialog)) {
                                    openAddNewGoalDialog();
                                } else if (firstGoal[0] != null &&
                                        currentTargetView.equals(firstGoal[0].findViewById(R.id.active_goal_edit_btn)) &&
                                        firstGoal[0].findViewById(R.id.expanded_active_goal_container).getVisibility() == View.VISIBLE) {
                                    firstGoal[0].callOnClick();
                                } else if (firstGoal[0] != null &&
                                        currentTargetView.equals(firstGoal[0].findViewById(R.id.activeCardView)) &&
                                        previousTargetView.equals(firstGoal[0].findViewById(R.id.active_goal_edit_btn))) {
                                    firstGoal[0].findViewById(R.id.edit_cancel_button).callOnClick();
                                } else if (firstGoal[0] != null &&
                                        secondGoal != null &&
                                        currentTargetView.equals(secondGoal.findViewById(R.id.activeCardView))) {
                                    firstGoal[0].findViewById(R.id.active_goal_edit_btn).callOnClick();
                                } else if (secondGoal != null &&
                                        currentTargetView.equals(tutorialNoTarget) &&
                                        previousTargetView.equals(secondGoal.findViewById(R.id.activeCardView))) {
                                    mainActiveGoalsAdapter.setMultiSelectable(false);
                                } else if (currentTargetView.equals(addAsSubgoalsFab)) {
                                    fab.callOnClick();
                                } else if (secondGoal != null &&
                                        currentTargetView.equals(setAsSubgoalOfDialog)) {
                                    closeSetAsSubgoalOfDialog();
                                    handler.postDelayed(() -> {
                                        secondGoal.performLongClick();
                                        fab.callOnClick();
                                    }, 5);
                                } else if (setAsSubgoalOfGoalsAdapter != null &&
                                        setAsSubgoalOfGoalsList.getChildAt(setAsSubgoalOfGoalsAdapter.getGoalIndex(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME)) != null &&
                                        currentTargetView.equals(setAsSubgoalOf)) {
                                    setAsSubgoalOfGoalsList.getChildAt(setAsSubgoalOfGoalsAdapter.getGoalIndex(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME)).findViewById(R.id.active_goal_multi_select_button).callOnClick();
                                } else if (firstGoal[0] != null &&
                                        secondGoal != null &&
                                        currentTargetView.equals(firstGoal[0].findViewById(R.id.expanded_active_goal_sub_goals_recyclerview))) {
                                    ArrayList<Goal> secondGoalHolder = new ArrayList<>();
                                    secondGoalHolder.add(goalDB.getGoalByName(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME));
                                    goalDB.removeSubGoalsFromGoal(secondGoalHolder, TUTORIAL_FIRST_EXAMPLE_GOAL_NAME);
                                    mainActiveGoalsAdapter.scrollToPositionInRecyclerView(secondGoalIndex, activeGoalsList.getLayoutManager());
                                    secondGoal.performLongClick();
                                    fab.callOnClick();
                                    addAsSubgoalsFab.callOnClick();
                                    handler.postDelayed(() -> setAsSubgoalOfGoalsList.getChildAt(setAsSubgoalOfGoalsAdapter.getGoalIndex(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME)).callOnClick(), 10);
                                } else if (firstGoal[0] != null &&
                                        currentTargetView.equals(finishGoalDialog)) {
                                    newGoalsName.setText(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME);
                                    newGoalsDescription.setText(TUTORIAL_SECOND_EXAMPLE_GOAL_DESCRIPTION);
                                    newGoalsTimeEstimated.setText(TUTORIAL_SECOND_EXAMPLE_GOAL_TIME_ESTIMATED);
                                    addNewGoal.callOnClick();
                                    ArrayList<Goal> secondGoalHolder = new ArrayList<>();
                                    secondGoalHolder.add(goalDB.getGoalByName(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME));
                                    goalDB.setParentToGoals(goalDB.getGoalByName(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME), secondGoalHolder);
                                    mainActiveGoalsAdapter.updateGoalsList();
                                    closeFinishGoalDialog();
                                    firstGoal[0].callOnClick();
                                } else if (currentTargetView.equals(tagPickerEditText)) {
                                    finishGoalDialogScrollView.post(() -> finishGoalDialogScrollView.fullScroll(View.FOCUS_UP));
                                } else if (currentTargetView.equals(tutorialNoTarget) &&
                                        previousTargetView.equals(finishGoalButton)) {
                                    try {
                                        if (firstGoalIndex[0] == -1 || secondGoalIndex == -1 ||
                                                goalDB.isAchieved(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME) ||
                                                goalDB.isAchieved(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME)) {
                                            goalDB.removeGoal(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME);
                                            goalDB.removeGoal(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME);
                                            newGoalsName.setText(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME);
                                            newGoalsDescription.setText(TUTORIAL_FIRST_EXAMPLE_GOAL_DESCRIPTION);
                                            newGoalsTimeEstimated.setText(TUTORIAL_FIRST_EXAMPLE_GOAL_TIME_ESTIMATED);
                                            addNewGoal.callOnClick();
                                        }
                                    } finally {
                                        handler.postDelayed(() -> {
                                            firstGoalIndex[0] = mainActiveGoalsAdapter.getGoalIndex(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME);
                                            firstGoal[0] = activeGoalsList.getLayoutManager().findViewByPosition(firstGoalIndex[0]);
                                            mainActiveGoalsAdapter.scrollToPositionInRecyclerView(firstGoalIndex[0], activeGoalsList.getLayoutManager());
                                            firstGoal[0].callOnClick();
                                            firstGoal[0].findViewById(R.id.finish_goal_button).callOnClick();
                                        }, 100);
                                    }
                                }
                            }

                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onRestart() {
                                if (firstGoalIndex == -1 || secondGoalIndex == -1 ||
                                        goalDB.isAchieved(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME) ||
                                        goalDB.isAchieved(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME)) {
                                    goalDB.removeGoal(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME);
                                    goalDB.removeGoal(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME);
                                    newGoalsName.setText(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME);
                                    newGoalsDescription.setText(TUTORIAL_FIRST_EXAMPLE_GOAL_DESCRIPTION);
                                    newGoalsTimeEstimated.setText(TUTORIAL_FIRST_EXAMPLE_GOAL_TIME_ESTIMATED);
                                    addNewGoal.callOnClick();

                                    newGoalsName.setText(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME);
                                    newGoalsDescription.setText(TUTORIAL_SECOND_EXAMPLE_GOAL_DESCRIPTION);
                                    newGoalsTimeEstimated.setText(TUTORIAL_SECOND_EXAMPLE_GOAL_TIME_ESTIMATED);
                                    addNewGoal.callOnClick();
                                } else {
                                    int firstGoalIndex = mainActiveGoalsAdapter.getGoalIndex(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME),
                                            secondGoalIndex = mainActiveGoalsAdapter.getGoalIndex(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME);
                                    View firstGoal = activeGoalsList.getLayoutManager().findViewByPosition(firstGoalIndex),
                                            secondGoal = activeGoalsList.getLayoutManager().findViewByPosition(secondGoalIndex);
                                    if (firstGoal.findViewById(R.id.expanded_active_goal_container).getVisibility() == View.VISIBLE) {
                                        if (firstGoal.findViewById(R.id.edit_panel).getVisibility() == View.VISIBLE) {
                                            firstGoal.findViewById(R.id.edit_cancel_button).callOnClick();
                                        }
                                        firstGoal.callOnClick();
                                    }
                                    if (secondGoal.findViewById(R.id.expanded_active_goal_container).getVisibility() == View.VISIBLE) {
                                        secondGoal.callOnClick();
                                    }
                                    if (mainActiveGoalsAdapter.getMultiSelectable()) {
                                        mainActiveGoalsAdapter.setMultiSelectable(false);
                                    }
                                    if (goalDB.isGoalSubGoalOf(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME, TUTORIAL_FIRST_EXAMPLE_GOAL_NAME)) {
                                        ArrayList<Goal> secondGoalHolder = new ArrayList<>();
                                        secondGoalHolder.add(goalDB.getGoalByName(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME));
                                        goalDB.removeSubGoalsFromGoal(secondGoalHolder, TUTORIAL_FIRST_EXAMPLE_GOAL_NAME);
                                    }
                                    tutorialSequence
                                            .setTutorialStations(getTutorialStations())
                                            .enableBackButton(true)
                                            .enableRestartButton(true)
                                            .enableSkipButton(true);
                                }
                                closeAddNewGoalDialog();
                                closeSetAsSubgoalOfDialog();
                                closeFinishGoalDialog();
                            }

                            @Override
                            public void onSkip() {
                                int goalIndex = goalDB.getActiveGoalsArrayList().indexOf(goalDB.getGoalByName(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME));
                                if (goalIndex != -1) {
                                    mainActiveGoalsAdapter.notifyItemRemoved(goalIndex);
                                }
                                goalIndex = goalDB.getActiveGoalsArrayList().indexOf(goalDB.getGoalByName(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME));
                                if (goalIndex != -1) {
                                    mainActiveGoalsAdapter.notifyItemRemoved(goalIndex);
                                }
                                mainActiveGoalsAdapter.updateGoalsList();
                                closeAddNewGoalDialog();
                                closeSetAsSubgoalOfDialog();
                                closeFinishGoalDialog();
                            }

                            @Override
                            public void onFinish() {
                                int goalIndex = goalDB.getActiveGoalsArrayList().indexOf(goalDB.getGoalByName(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME));
                                if (goalIndex != -1) {
                                    mainActiveGoalsAdapter.notifyItemRemoved(goalIndex);
                                }
                                goalIndex = goalDB.getActiveGoalsArrayList().indexOf(goalDB.getGoalByName(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME));
                                if (goalIndex != -1) {
                                    mainActiveGoalsAdapter.notifyItemRemoved(goalIndex);
                                }
                                mainActiveGoalsAdapter.updateGoalsList();
                                mainActiveGoalsAdapter.notifyDataSetChanged();
                                PrefUtil.setTutorialStationIndex(ACTIVE_GOALS_PAGE_NAME, 0);
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
     * Finishes The selected Goal (makes it achieved).
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void finishGoal() {
        if (!PrefUtil.getCurrentGoal().equals(PublicMethods.getFinishingGoal().toString()) || PrefUtil.getTimerState() != OpeningFragment.TimerState.Running) {
            ArrayList<String> tagsArray = new ArrayList<>();
            if (!tagPickerEditText.getText().toString().equals("")) {
                tagPickerEditText.chipifyAllUnterminatedTokens();
                List<com.hootsuite.nachos.chip.Chip> allTagsSelected = tagPickerEditText.getAllChips();
                for (com.hootsuite.nachos.chip.Chip chip : allTagsSelected) {
                    tagsArray.add(chip.getText().toString());
                }
            } else {
                tagsArray.add("Other");
            }

            StringBuilder tags = new StringBuilder();
            for (int i = 0; i < tagsArray.size(); i++) {
                if (i > 0) {
                    tags.append(",");
                }
                tags.append(tagsArray.get(i));
            }

            goalDB.finishGoal(PublicMethods.getFinishingGoal(), difficultySeekBar.getProgress(), evolvingSeekBar.getProgress(), satisfactionSeekBar.getProgress(), tags.toString());
            mainActiveGoalsAdapter.notifyItemRemoved(PublicMethods.positionOfGoalInGoalsArrayList(PublicMethods.getFinishingGoal().getName(), activeGoalsArrayList));
            mainActiveGoalsAdapter.updateGoalsList();
            if(PrefUtil.getCurrentGoal().equals(PublicMethods.getFinishingGoal().toString()))
                PrefUtil.setCurrentGoal("");
            new CongratulationView.Builder(requireActivity())
                    .setTitle("Congratulations!")
                    .setContent("You've finished " + PublicMethods.getFinishingGoal().toString() +
                            "!" + "\nWell done! keep up the great work.")
                    .enableImage(false, false)
                    .setConfettiColors(new int[]{getResources().getColor(R.color.brain1),
                            getResources().getColor(R.color.brain2),
                            getResources().getColor(R.color.brain3)})
                    .show();
        }
    }

    /**
     * Expands the FloatingActionButtons when in selection mode.
     */
    public void expandFabActions() {
        addAsSubgoalsFab.show();
        addAsSubgoalsLabel.setVisibility(View.VISIBLE);
        deleteFab.show();
        deleteLabel.setVisibility(View.VISIBLE);
        fab.extend();
        areAllFabsVisible = true;
    }

    /**
     * Shrinks the FloatingActionButtons expanded when in selection mode.
     */
    public void shrinkFabActions() {
        deleteFab.hide();
        deleteLabel.setVisibility(View.GONE);
        addAsSubgoalsFab.hide();
        addAsSubgoalsLabel.setVisibility(View.GONE);
        fab.shrink();
        areAllFabsVisible = false;
    }

    /**
     * Opens the sort-goals dialog.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void openSortGoalsDialog() {

        sortGoalsDialog.setVisibility(View.VISIBLE);
        fadeBlurIn();
        sortGoalsDialog.setClickable(true);
        sortMode = PrefUtil.getActiveSortMode();
        switch (sortMode) {
            case Name:
                sortByGroup.check(R.id.active_goals_fragment_name_radio_btn);
                break;
            case Progress:
                sortByGroup.check(R.id.active_goals_fragment_progress_radio_btn);
                break;
            case Date:
            default:
                sortByGroup.check(R.id.active_goals_fragment_start_date_radio_btn);
                break;
        }

        ascending = PrefUtil.getActiveGoalsAscending();
        if (!ascending) {
            ascDescGroup.check(R.id.active_goals_fragment_desc_radio_btn);
        } else {
            ascDescGroup.check(R.id.active_goals_fragment_asc_radio_btn);
        }

    }

    /**
     * Closes the sort-goals dialog.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void closeSortGoalsDialog(boolean sort) {

        if (sort) {
            if (sortByGroup.getCheckedRadioButtonId() == R.id.active_goals_fragment_start_date_radio_btn) {
                sortMode = PrefUtil.ActiveSortMode.Date;
            } else if (sortByGroup.getCheckedRadioButtonId() == R.id.active_goals_fragment_name_radio_btn) {
                sortMode = PrefUtil.ActiveSortMode.Name;
            } else if (sortByGroup.getCheckedRadioButtonId() == R.id.active_goals_fragment_progress_radio_btn) {
                sortMode = PrefUtil.ActiveSortMode.Progress;
            }

            if (ascDescGroup.getCheckedRadioButtonId() == R.id.active_goals_fragment_asc_radio_btn) {
                ascending = true;
            } else if (ascDescGroup.getCheckedRadioButtonId() == R.id.active_goals_fragment_desc_radio_btn) {
                ascending = false;
            }

            PublicMethods.sortActiveGoals(requireContext(), sortMode, ascending, activeGoalsArrayList);
        }

        sortGoalsDialog.setVisibility(View.INVISIBLE);
        fadeBlurOut();
        sortGoalsDialog.setClickable(false);
    }

    /**
     * Opens the add-new-active-goal dialog.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void openAddNewGoalDialog() {
        if (PrefUtil.getTimerState() == OpeningFragment.TimerState.Running) {
            PublicMethods.openTimerIsRunningErrorDialog(requireContext(), requireActivity());
        } else {
            //fab.setExpanded(true);
            addNewGoalDialog.setVisibility(View.VISIBLE);
            fadeBlurIn();
            addNewGoalDialog.setClickable(true);
        }
    }


    /**
     * Closes the add-new-active-goal dialog.
     */
    public void closeAddNewGoalDialog() {

        final InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        //fab.setExpanded(false);
        addNewGoalDialog.setVisibility(View.INVISIBLE);
        fadeBlurOut();
        addNewGoalDialog.setClickable(false);

        newGoalsName.setText("");
        newGoalsDescription.setText("");
        newGoalsTimeEstimated.setText("");

    }

    /**
     * Opens the set-as-subgoal-of dialog.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void openSetAsSubgoalOfDialog() {
        initSetAsSubgoalOfGoalsList(v);
        setAsSubgoalOfDialog.setVisibility(View.VISIBLE);
        fadeBlurIn();
        setAsSubgoalOfDialog.setClickable(true);
    }

    /**
     * Closes the set-as-subgoal-of dialog.
     */
    public void closeSetAsSubgoalOfDialog() {
        setAsSubgoalOfDialog.setVisibility(View.INVISIBLE);
        fadeBlurOut();
        setAsSubgoalOfDialog.setClickable(false);
        mainActiveGoalsAdapter.setMultiSelectable(false);
    }

    /**
     * Closes the finish-goal dialog.
     */
    public void closeFinishGoalDialog() {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        finishGoalDialog.setVisibility(View.INVISIBLE);
        fadeBlurOut();
        finishGoalDialog.setClickable(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void initSetAsSubgoalOfGoalsList(View v) {
        setAsSubgoalOfGoalsList = v.findViewById(R.id.set_as_subgoal_dialog_recycler_view);
        setAsSubgoalOfGoalsArrayList = goalDB.getPossibleParentGoalsArrayListOf(mainActiveGoalsAdapter.getMultiSelected());
        PublicMethods.sortActiveGoals(requireContext(), PublicMethods.getValueOrDefault(sortMode, PrefUtil.ActiveSortMode.Date), ascending, setAsSubgoalOfGoalsArrayList);
        setAsSubgoalOfGoalsAdapter = new ActiveGoalsAdapter(getContext(),
                goalDB.getPossibleParentGoalsArrayListOf(mainActiveGoalsAdapter.getMultiSelected()),
                setAsSubgoalOfGoalsList,
                (MainActivity) getActivity(),
                fab,
                blurBackground,
                finishGoalDialog,
                difficultySeekBar,
                evolvingSeekBar,
                satisfactionSeekBar,
                tagPickerEditText);
        setAsSubgoalOfGoalsAdapter.setMultiSelectable(false);
        setAsSubgoalOfGoalsAdapter.setSingleSelectable(true);
        setAsSubgoalOfGoalsList.setHasFixedSize(true);
        setAsSubgoalOfGoalsList.setLayoutManager(new LinearLayoutManager(getContext()));
        setAsSubgoalOfGoalsList.setAdapter(setAsSubgoalOfGoalsAdapter);
        ((SimpleItemAnimator) Objects.requireNonNull(setAsSubgoalOfGoalsList.getItemAnimator())).setSupportsChangeAnimations(false);
    }

    /**
     * Shows the shadow appears when a dialog is opened.
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
     * Hides the shadow appears when a dialog is opened.
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
     * Controls what happens when pressing back, whether it closes the dialog when it's opened or it closes the fragment / the whole app.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onBackPressed() {
        if (addNewGoalDialog.getVisibility() == View.VISIBLE) {
            closeAddNewGoalDialog();
            return true;
        } else if (setAsSubgoalOfDialog.getVisibility() == View.VISIBLE) {
            closeSetAsSubgoalOfDialog();
            return true;
        } else if (finishGoalDialog.getVisibility() == View.VISIBLE) {
            closeFinishGoalDialog();
            return true;
        } else if (sortGoalsDialog.getVisibility() == View.VISIBLE) {
            closeSortGoalsDialog(false);
            return true;
        } else if (mainActiveGoalsAdapter.getMultiSelectable()) {
            mainActiveGoalsAdapter.setMultiSelectable(false);
            return true;
        } else {
            return false;
        }
    }

}

