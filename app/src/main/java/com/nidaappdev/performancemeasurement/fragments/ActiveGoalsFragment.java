package com.nidaappdev.performancemeasurement.fragments;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.nidaappdev.performancemeasurement.GoalAndDatabaseObjects.Goal;
import com.nidaappdev.performancemeasurement.GoalAndDatabaseObjects.GoalDBHelper;
import com.nidaappdev.performancemeasurement.GoalRecyclerViewAdapters.ActiveGoalsAdapter;
import com.nidaappdev.performancemeasurement.R;
import com.nidaappdev.performancemeasurement.activities.MainActivity;
import com.nidaappdev.performancemeasurement.customViews.NestedRecyclerView.NestedRecyclerView;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.IOnBackPressed;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.PublicMethods;
import com.nidaappdev.performancemeasurement.util.PrefUtil;
import com.github.mmin18.widget.RealtimeBlurView;
import com.google.android.material.chip.Chip;
import com.google.android.material.circularreveal.CircularRevealFrameLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hootsuite.nachos.NachoTextView;
import com.warkiz.tickseekbar.OnSeekChangeListener;
import com.warkiz.tickseekbar.SeekParams;
import com.warkiz.tickseekbar.TickSeekBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import br.com.sapereaude.maskedEditText.MaskedEditText;

public class ActiveGoalsFragment extends Fragment implements IOnBackPressed {

    View v;
    ExtendedFloatingActionButton fab;
    FloatingActionButton addAsSubgoalsFab, deleteFab;
    TextView addAsSubgoalsLabel, deleteLabel;
    RealtimeBlurView blurBackground;
    CircularRevealFrameLayout addNewGoalDialog, setAsSubgoalOfDialog, finishGoalDialog, sortGoalsDialog;
    RadioGroup sortByGroup, ascDescGroup;
    RadioButton byNameRadio, byProgressRadio, ascRadio, descRadio;
    RelativeLayout cancelAddingNewGoal, addNewGoal, cancelSetAsSubgoalOf, setAsSubgoalOf, cancelFinishGoal, finishGoalButton, sortGoalsButton;
    EditText newGoalsName, newGoalsDescription;
    MaskedEditText newGoalsTimeEstimated;
    TickSeekBar difficultySeekBar, evolvingSeekBar, satisfactionSeekBar;
    NachoTextView tagPickerEditText;
    Chip defaultTag;
    NestedRecyclerView activeGoalsList, setAsSubgoalOfGoalsList;
    ActiveGoalsAdapter mainActiveGoalsAdapter, setAsSubgoalOfGoalsAdapter;
    GoalDBHelper db;
    ArrayList<Goal> activeGoalsArrayList, setAsSubgoalOfGoalsArrayList;
    PrefUtil.ActiveSortMode sortMode;
    boolean ascending = true;
    boolean areAllFabsVisible;


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

        db = new GoalDBHelper(getContext());

//        db.clearDatabase();

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
        cancelFinishGoal = v.findViewById(R.id.finish_goal_dialog_cancel_button);
        difficultySeekBar = v.findViewById(R.id.difficulty_picker);
        evolvingSeekBar = v.findViewById(R.id.evolving_picker);
        satisfactionSeekBar = v.findViewById(R.id.satisfaction_picker);
        tagPickerEditText = v.findViewById(R.id.tag_picker_edit_text);
        finishGoalButton = v.findViewById(R.id.finish_goal_dialog_finish_button);
        newGoalsName = v.findViewById(R.id.name_et_active_goals_fragment);
        newGoalsDescription = v.findViewById(R.id.description_et_active_goals_fragment);
        newGoalsTimeEstimated = v.findViewById(R.id.time_estimation_et_active_goals_fragment);

        shrinkFabActions();

        activeGoalsArrayList = db.getActiveGoalsArrayList();

        PublicMethods.sortActiveGoals(requireContext(), PrefUtil.getActiveSortMode(), PrefUtil.getActiveGoalsAscending(), activeGoalsArrayList);

        initGoalsList(v);
//        initGoals(50);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainActiveGoalsAdapter.getMultiSelectable()) {
                    if (!areAllFabsVisible) {
                        expandFabActions();
                    } else {
                        shrinkFabActions();
                    }
                } else {
                    openAddNewGoalDialog();
                }
            }
        });

        deleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActiveGoalsAdapter.getMultiSelected().size() > 0) {
                    for (Goal selectedGoal : mainActiveGoalsAdapter.getMultiSelected()) {
                        db.removeGoal(selectedGoal);
                        activeGoalsArrayList = db.getActiveGoalsArrayList();

                        PublicMethods.sortActiveGoals(requireContext(), PrefUtil.getActiveSortMode(), PrefUtil.getActiveGoalsAscending(), activeGoalsArrayList);
                        mainActiveGoalsAdapter.notifyItemRemoved(PublicMethods.positionOfGoalInGoalsArrayList(selectedGoal.getName(), activeGoalsArrayList));
                    }
                    mainActiveGoalsAdapter.emptyMultiSelected();
                    mainActiveGoalsAdapter.updateGoalsList();
                }
                mainActiveGoalsAdapter.setMultiSelectable(false);

            }
        });

        addAsSubgoalsFab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                openSetAsSubgoalOfDialog();
            }
        });

        blurBackground.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (addNewGoalDialog.getVisibility() == View.VISIBLE) {
                    closeAddNewGoalDialog();
                } else if (setAsSubgoalOfDialog.getVisibility() == View.VISIBLE) {
                    closeSetAsSubgoalOfDialog();
                } else if (finishGoalDialog.getVisibility() == View.VISIBLE) {
                    closeFinishGoalDialog();
                } else if (sortGoalsDialog.getVisibility() == View.VISIBLE) {
                    closeSortGoalsDialog(false);
                }
            }
        });

        sortGoalsButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                closeSortGoalsDialog(true);
                mainActiveGoalsAdapter.setExpandedItem(-1);
                mainActiveGoalsAdapter.notifyDataSetChanged();
            }
        });

        cancelAddingNewGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAddNewGoalDialog();
            }
        });

        addNewGoal.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Goal newGoal = new Goal(newGoalsName.getText().toString(), newGoalsDescription.getText().toString(), PublicMethods.getNewGoalsTimeEstimated(newGoalsTimeEstimated));
                ArrayList<String> allGoalsNames = new ArrayList<>();
                for (Goal goal : db.getAllGoalsArrayList()) {
                    allGoalsNames.add(goal.getName());
                }
                if (allGoalsNames.contains(newGoal.getName())) {
                    PublicMethods.openIdenticalGoalNameErrorDialog(requireContext(), requireActivity(), newGoal.getName());
                } else if (newGoal.getName().trim().isEmpty()) {
                    PublicMethods.openGoalNameNotValidErrorDialog(requireContext(), requireActivity(), newGoal.getName());
                } else if (newGoalsTimeEstimated.getText().toString().isEmpty() || PublicMethods.getNewGoalsTimeEstimated(newGoalsTimeEstimated) == 0) {
                    PublicMethods.openGoalTimeEstimatedNotValidErrorDialog(requireContext(), requireActivity());
                } else {
                    db.addGoal(newGoal);
                    closeAddNewGoalDialog();
                    mainActiveGoalsAdapter.updateAddedActiveGoal(newGoal.getName());
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
            }
        });

        cancelSetAsSubgoalOf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSetAsSubgoalOfDialog();
            }
        });

        setAsSubgoalOf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.setParentToGoals(setAsSubgoalOfGoalsAdapter.getSingleSelected(), mainActiveGoalsAdapter.getMultiSelected());
                for (Goal goal : mainActiveGoalsAdapter.getMultiSelected()) {
                    mainActiveGoalsAdapter.notifyItemChanged(PublicMethods.positionOfGoalInGoalsArrayList(goal.getName(), activeGoalsArrayList));
                    mainActiveGoalsAdapter.updateGoalsList();
                }
                closeSetAsSubgoalOfDialog();
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
                    case 1:
                        satisfactionSeekBar.setThumbDrawable(getResources().getDrawable(R.drawable.smiley_1));
                        break;
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

        cancelFinishGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFinishGoalDialog();
            }
        });

        finishGoalButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                finishGoal();
                //TODO: add a congratulations screen for finishing the goal.
                closeFinishGoalDialog();
            }
        });

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

            goal = new Goal(Integer.toString(i), i + "" + i, parent, progress, 100, 0, 0, 0, 0, false, new ArrayList<String>(), "");
            db.addGoal(goal);
        }
        mainActiveGoalsAdapter.swapCursor(db.getActiveGoalsCursor());
    }

    /**
     * Sets up the RecyclerView.
     *
     * @param v is the one used in the onCreateView method.
     */
    public void initGoalsList(View v) {
        activeGoalsList = v.findViewById(R.id.active_goals_recycler_view);
        mainActiveGoalsAdapter = new ActiveGoalsAdapter(getContext(),
                activeGoalsArrayList,
                db.getActiveGoalsCursor(),
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

    /**
     * Finishes The selected Goal (makes it achieved).
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void finishGoal() {
        if (!PrefUtil.getCurrentGoal().equals(PublicMethods.getFinishingGoal().toString()) || PrefUtil.getTimerState() != OpeningFragment.TimerState.Running) {
            ArrayList<String> tagsArray = new ArrayList<>();
            if (!tagPickerEditText.getText().toString().equals("")) {
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

            db.finishGoal(PublicMethods.getFinishingGoal(), difficultySeekBar.getProgress(), evolvingSeekBar.getProgress(), satisfactionSeekBar.getProgress(), tags.toString());
            mainActiveGoalsAdapter.notifyItemRemoved(PublicMethods.positionOfGoalInGoalsArrayList(PublicMethods.getFinishingGoal().getName(), activeGoalsArrayList));
            mainActiveGoalsAdapter.updateGoalsList();
            PrefUtil.setCurrentGoal("");
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
        setAsSubgoalOfGoalsArrayList = db.getPossibleParentGoalsArrayListOf(mainActiveGoalsAdapter.getMultiSelected());
        PublicMethods.sortActiveGoals(requireContext(), sortMode, ascending, setAsSubgoalOfGoalsArrayList);
        setAsSubgoalOfGoalsAdapter = new ActiveGoalsAdapter(getContext(),
                db.getPossibleParentGoalsArrayListOf(mainActiveGoalsAdapter.getMultiSelected()),
                db.getPossibleParentGoalsCursor(mainActiveGoalsAdapter.getMultiSelected()),
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

