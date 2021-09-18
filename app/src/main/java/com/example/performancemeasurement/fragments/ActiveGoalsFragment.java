package com.example.performancemeasurement.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.RelativeLayout;

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
    RelativeLayout cancel, addNewGoal;
    EditText newGoalsName, newGoalsDescription;
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
        cancel = v.findViewById(R.id.cancel_btn);
        addNewGoal = v.findViewById(R.id.add_btn);
        newGoalsName = v.findViewById(R.id.name_et);
        newGoalsDescription = v.findViewById(R.id.description_et);

        initGoalsList(v);

        //db.clearDatabase();
        //initGoals(50);

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

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });

        addNewGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Goal newGoal = new Goal(newGoalsName.getText().toString(), newGoalsDescription.getText().toString());
                //lottie_dialog_to_check_if_goal_already_exists
                db.addGoal(newGoal);
                closeDialog();
                activeGoalsAdapter.updateAddedActiveGoal();
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
        activeGoalsAdapter = new ActiveGoalsAdapter(getContext(),
                db.getActiveGoalsArrayList(),
                db.getActiveGoalsCursor(),
                activeGoalsList,
                (MainActivity) getActivity(),
                fab);
        activeGoalsList.setHasFixedSize(true);
        activeGoalsList.setLayoutManager(new LinearLayoutManager(getContext()));
        activeGoalsList.setAdapter(activeGoalsAdapter);
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

        newGoalsName.setText("");
        newGoalsDescription.setText("");

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
        blurBackground.setClickable(true);
        dialog.setClickable(true);
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
        blurBackground.setClickable(false);
        dialog.setClickable(false);
    }

    /**
     * Controls what happens when pressing back, whether it closes the dialog when it's opened or it closes the fragment / the whole app.
     */
    @Override
    public boolean onBackPressed() {
        if (dialog.getVisibility() == View.VISIBLE) {
            closeDialog();
            return true;
        } else if (activeGoalsAdapter.getSelectable()) {
            activeGoalsAdapter.setSelectable(false);
            return true;
        } else {
            return false;
        }
    }

}

