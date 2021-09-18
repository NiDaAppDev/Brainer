package com.example.performancemeasurement.fragments;


import static android.content.ContentValues.TAG;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.azoft.carousellayoutmanager.CarouselLayoutManager;
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.azoft.carousellayoutmanager.CenterScrollListener;
import com.example.performancemeasurement.GoalAndDatabaseObjects.GoalDBHelper;
import com.example.performancemeasurement.GoalRecyclerViewAdapters.ActiveGoalsAdapter;
import com.example.performancemeasurement.GoalRecyclerViewAdapters.GoalsAdapter;
import com.example.performancemeasurement.R;
import com.example.performancemeasurement.brainAnimation.lightning.RandomLightning;
import com.example.performancemeasurement.customViews.CustomProgressBarButton.CustomProgressBarButton;
import com.example.performancemeasurement.publicClassesAndInterfaces.IOnFocusListenable;
import com.example.performancemeasurement.publicClassesAndInterfaces.PublicMethods;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class OpeningFragment extends Fragment implements IOnFocusListenable {

    private RandomLightning randomLightning1, randomLightning2;
    private ImageView brainImage;
    View v;
    GoalDBHelper db;
    FloatingActionButton playPauseBtn, addNewGoalBtn;
    ActiveGoalsAdapter activeGoalsAdapter;
    RelativeLayout currentGoalPickerContainer;
    RecyclerView currentGoalPicker;
    CustomProgressBarButton currentGoalProgressBarButton;
    int stop = 0, brainWidth = 0, brainHeight = 0;
    boolean playing = false;

    public OpeningFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_opening, container, false);

        db = new GoalDBHelper(getContext());

        initObjects(v);

        initButtonsClick();

        setupLightnings();

        return v;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        ArrayList<Integer> sizes = new ArrayList<>();
        if (randomLightning1.getWidth() != 0) {
            sizes.add(randomLightning1.getWidth());
            sizes.add(randomLightning1.getHeight());
            PublicMethods.addNewSharedPreferences(getString(R.string.brain_preferences_SharedPreferences_name), "lightningViewSizes", sizes);
        }
        randomLightning1.setBounds(0, randomLightning1.getWidth(), 0, randomLightning1.getHeight());
        randomLightning2.setBounds(0, randomLightning2.getWidth(), 0, randomLightning2.getHeight());
    }


    /**
     * setupLightnings method:
     * this method sets up the lightnings view
     */
    public void setupLightnings() {

        setupLightningsBounds();

        int rand1 = new Random().nextInt(10) + 65, rand2;
        brainWidth = brainImage.getMaxWidth();
        brainHeight = brainImage.getMaxHeight();
        randomLightning1.setMinimumWidth((int) (brainWidth - (40 * getResources().getDisplayMetrics().scaledDensity)));
        randomLightning2.setMinimumWidth((int) (brainWidth - (40 * getResources().getDisplayMetrics().scaledDensity)));
        randomLightning1.setMinimumHeight((int) (brainHeight - (40 * getResources().getDisplayMetrics().scaledDensity)));
        randomLightning2.setMinimumHeight((int) (brainHeight - (40 * getResources().getDisplayMetrics().scaledDensity)));
        rand2 = 120 - rand1;
        randomLightning1.startLightningsInFrequencyOf(rand1);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                randomLightning2.startLightningsInFrequencyOf(rand2);
            }
        }, 1000);
    }

    private void setupLightningsBounds(){
        ArrayList<Double> sizes1 = PublicMethods.getSharedPreferences(getString(R.string.brain_preferences_SharedPreferences_name), getString(R.string.lightning_view_sizes_SharedPreferences_value_name));
        ArrayList<Integer> sizes = new ArrayList<>();
    }

    private void initObjects(View v) {
        randomLightning1 = v.findViewById(R.id.lightning_canvas_1);
        randomLightning2 = v.findViewById(R.id.lightning_canvas_2);
        brainImage = v.findViewById(R.id.brain_image);
        playPauseBtn = v.findViewById(R.id.play_pause_btn);
        addNewGoalBtn = v.findViewById(R.id.add_new_goal_btn);
        initCurrentGoalPicker();
        initCurrentGoalBar();
    }

    private void initCurrentGoalBar(){
        currentGoalProgressBarButton = v.findViewById(R.id.goal_progress_bar);
        currentGoalProgressBarButton.enableDefaultGradient(true);
        currentGoalProgressBarButton.enableDefaultPress(true);
        currentGoalProgressBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked");
                openWheelPickerInPos();
            }
        });
    }

    private void initCurrentGoalPicker(){
        currentGoalPickerContainer = v.findViewById(R.id.goal_picker_container);
        currentGoalPicker = v.findViewById(R.id.main_goal_wheel_picker_recyclerview);
        GoalsAdapter adapter = new GoalsAdapter(getContext(), db.getActiveGoalsArrayList());
        final CarouselLayoutManager layoutManager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, false);
        layoutManager.setPostLayoutListener((CarouselLayoutManager.PostLayoutListener) new CarouselZoomPostLayoutListener());
        currentGoalPicker.setLayoutManager(layoutManager);
        currentGoalPicker.setHasFixedSize(true);
        currentGoalPicker.setAdapter(adapter);
        currentGoalPicker.addOnScrollListener(new CenterScrollListener());

        adapter.setOnItemClickListener(new GoalsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                selectNewCurrentGoal();
            }
        });
    }

    private void initButtonsClick() {
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                playPauseAction();
            }
        });

        addNewGoalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewGoalAction();
            }
        });
    }

    private void selectNewCurrentGoal(){
        closeWheelPicker();
    }

    private void openWheelPickerInPos(){
        TransitionManager.beginDelayedTransition((ViewGroup) currentGoalPickerContainer.getRootView(), new AutoTransition());
        currentGoalPickerContainer.setVisibility(View.VISIBLE);
    }

    private void closeWheelPicker(){
        TransitionManager.beginDelayedTransition((ViewGroup) currentGoalPickerContainer.getRootView(), new AutoTransition());
        currentGoalPickerContainer.setVisibility(View.INVISIBLE);
    }

    private void addNewGoalAction() {
        //TODO: Here Add New Goal Action
        currentGoalProgressBarButton.setProgress(new Random().nextInt(99) + 1);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void playPauseAction() {
        updatePlayPauseIcon();
        //TODO: Here Play Or Pause Action
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void updatePlayPauseIcon() {
        int res;
        if (playing) {
            res = R.drawable.pausetoplay_vector_anim;
            playPauseBtn.setForeground(getResources().getDrawable(R.drawable.fab_green_play_pause_opening_fragment_border));
            playPauseBtn.setRippleColor(getResources().getColorStateList(R.color.stop_red));
        } else {
            res = R.drawable.playtopause_vector_anim;
            playPauseBtn.setForeground(getResources().getDrawable(R.drawable.fab_red_play_pause_opening_fragment_border));
            playPauseBtn.setRippleColor(getResources().getColorStateList(R.color.start_green));
        }
        playPauseBtn.setImageResource(res);

        Drawable icon = playPauseBtn.getDrawable();
        playing = !playing;

        if (icon instanceof AnimatedVectorDrawable) {
            ((AnimatedVectorDrawable) icon).start();
        }
    }


}
