package com.nidaappdev.performancemeasurement.fragments;


import static android.content.Context.MODE_PRIVATE;
import static com.nidaappdev.performancemeasurement.activities.MainActivity.pomodoroServiceIntent;
import static com.nidaappdev.performancemeasurement.activities.MainActivity.timeOutServiceIntent;
import static com.nidaappdev.performancemeasurement.activities.MainActivity.timerServiceIntent;
import static com.nidaappdev.performancemeasurement.util.Constants.ACTION_STOP_SERVICE;
import static com.nidaappdev.performancemeasurement.util.Constants.BRAIN_PREFERENCES_SHAREDPREFERENCES_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.LIGHTNING_VIEW_SIZES_PREFERENCE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.POMODORO_SERVICE_POMODORO_FINISHED_EXTRA_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.POMODORO_SERVICE_STOP_FOR_REAL_EXTRA_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.POMODORO_TIME_OUT_SERVICE_TIME_OUT_FINISHED_EXTRA_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.SAVE_GOAL_PROGRESS_INTENT_ACTION;
import static com.nidaappdev.performancemeasurement.util.Constants.SUGGEST_BREAK_EXTRA_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.TIMER_NOTIFICATION_SERVICE_CURRENT_GOAL_EXTRA_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.TIMER_NOTIFICATION_SERVICE_TIME_IN_MILLIS_EXTRA_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.TIMER_NOTIFICATION_SERVICE_UPDATE_PLAY_PAUSE_BUTTON_EXTRA_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.TIMER_PREFERENCES_SHAREDPREFERENCES_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.TIMER_STATE_PREFERENCE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.TIME_METHOD_PREFERENCE_NAME;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.azoft.carousellayoutmanager.CarouselLayoutManager;
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.azoft.carousellayoutmanager.CenterScrollListener;
import com.nidaappdev.performancemeasurement.customObjects.Goal;
import com.nidaappdev.performancemeasurement.databaseObjects.GoalDBHelper;
import com.nidaappdev.performancemeasurement.RecyclerViewAdapters.GoalsAdapter;
import com.nidaappdev.performancemeasurement.Lottie.DialogHandler;
import com.nidaappdev.performancemeasurement.R;
import com.nidaappdev.performancemeasurement.activities.MainActivity;
import com.nidaappdev.performancemeasurement.brainAnimation.lightning.RandomLightning;
import com.nidaappdev.performancemeasurement.customViews.CustomProgressBarButton.CustomProgressBarButton;
import com.nidaappdev.performancemeasurement.databaseObjects.StatisticsDBHelper;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.IOnBackPressed;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.IOnFocusListenable;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.PublicMethods;
import com.nidaappdev.performancemeasurement.util.PrefUtil;
import com.github.mmin18.widget.RealtimeBlurView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.circularreveal.CircularRevealFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.polyak.iconswitch.IconSwitch;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import br.com.sapereaude.maskedEditText.MaskedEditText;

public class OpeningFragment extends Fragment implements IOnFocusListenable, IOnBackPressed {

    public enum TimerState {
        Stopped, Running
    }

    private ArrayList<RandomLightning> randomLightnings;
    private ImageView brainImage;
    private View v;
    private static GoalDBHelper goalDB;
    private static StatisticsDBHelper statsDB;
    private static MainActivity activity;
    private static IconSwitch timeMethodSwitch;
    private static FloatingActionButton playPauseBtn, addNewGoalBtn;
    private static GoalsAdapter activeGoalsAdapter;
    private RelativeLayout addBtn, cancelAddBtn;
    private static RealtimeBlurView blurBackground;
    private static CircularRevealFrameLayout addNewGoalDialog;
    private EditText newGoalsName, newGoalsDescription;
    private MaskedEditText newGoalsTimeEstimated;
    private BottomSheetDialog currentGoalPickerDialog;
    private RecyclerView currentGoalPicker;
    private static CustomProgressBarButton currentGoalProgressBarButton;
    private static Ringtone ringtone;
    private static Uri path;
    private int brainWidth = 0, brainHeight = 0;
    private static DialogHandler dialogHandler;

    private static final BroadcastReceiver endTimerReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean updatePlayPauseButtonExtra = intent.getBooleanExtra(TIMER_NOTIFICATION_SERVICE_UPDATE_PLAY_PAUSE_BUTTON_EXTRA_NAME, false);
            if (updatePlayPauseButtonExtra) {
                updateRunning(PrefUtil.getTimerState() == TimerState.Running, context);
            }

            if (PrefUtil.getTimeMethod() == PrefUtil.TimeMethod.Timer) {
                boolean suggestBreak = intent.getBooleanExtra(SUGGEST_BREAK_EXTRA_NAME, false);
                if (suggestBreak && PrefUtil.getSuggestBreak()) {
                    openSuggestBreakDialog(context);
                } else {
                    stopTimerService(context);
                    Handler handler = new Handler();
                    Runnable runnable = () -> {
                        onTimerStopped(PrefUtil.getCurrentGoal(), intent.getLongExtra(TIMER_NOTIFICATION_SERVICE_TIME_IN_MILLIS_EXTRA_NAME, 0L), context);
                    };
                    handler.postDelayed(runnable, 10);
                }
            } else if (PrefUtil.getTimeMethod() == PrefUtil.TimeMethod.Pomodoro) {
                boolean pomodoroFinished = intent.getBooleanExtra(POMODORO_SERVICE_POMODORO_FINISHED_EXTRA_NAME, false);
                boolean losePomodoro = intent.getBooleanExtra(POMODORO_SERVICE_STOP_FOR_REAL_EXTRA_NAME, false);
                if (pomodoroFinished) {
                    stopPomodoro(context);
                    Handler handler = new Handler();
                    Runnable runnable = () -> {
                        onPomodoroFinished(context, PrefUtil.getCurrentGoal());
                    };
                    handler.postDelayed(runnable, 10);
                } else if (!losePomodoro) {
                    Handler handler = new Handler();
                    Runnable runnable = () -> tryStopPomodoro(context);
                    handler.postDelayed(runnable, 10);
                }
            } else if (PrefUtil.getTimeMethod() == PrefUtil.TimeMethod.TimeOut || PrefUtil.getTimeMethod() == PrefUtil.TimeMethod.TimerTimeOut) {
                boolean timeOutFinished = intent.getBooleanExtra(POMODORO_TIME_OUT_SERVICE_TIME_OUT_FINISHED_EXTRA_NAME, false);
                if (timeOutFinished) {
                    Handler handler = new Handler();
                    Runnable runnable = () -> onTimeOutOver(context);
                    handler.postDelayed(runnable, 10);
                }
            }

        }
    };

    public OpeningFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_opening, container, false);

        goalDB = new GoalDBHelper(getContext());

        statsDB = new StatisticsDBHelper(getContext());

        initObjects(v);

        initButtonsClick();

        setupLightnings();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(TIMER_PREFERENCES_SHAREDPREFERENCES_NAME, MODE_PRIVATE);

        activity = (MainActivity) getActivity();

        path = Uri.parse("android.resource://" + requireContext().getPackageName() + "/raw/pomodoro_finished");
        ringtone = RingtoneManager.getRingtone(requireContext(), path);

        if (!sharedPreferences.contains(TIMER_STATE_PREFERENCE_NAME)) {
            PrefUtil.setTimerState(TimerState.Stopped);
        }

        if (!sharedPreferences.contains(TIME_METHOD_PREFERENCE_NAME)) {
            PrefUtil.setTimeMethod(PrefUtil.TimeMethod.Pomodoro);
        }

        updateToggle();
        updateRunning(PrefUtil.getTimerState() == TimerState.Running, requireContext());

        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        updateCurrentGoalBar();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        ArrayList<Integer> sizes = new ArrayList<>();
        if (!randomLightnings.isEmpty() && randomLightnings.get(0).getWidth() != 0) {
            sizes.add(randomLightnings.get(0).getWidth());
            sizes.add(randomLightnings.get(0).getHeight());
            PrefUtil.addNewOrEditSharedPreferences(BRAIN_PREFERENCES_SHAREDPREFERENCES_NAME, "lightningViewSizes", sizes);
        }
        for (RandomLightning randomLightning : randomLightnings) {
            randomLightning.setBounds(0, randomLightning.getWidth(), 0, randomLightning.getHeight());
        }
    }


    /**
     * setupLightnings method:
     * this method sets up the lightnings view
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setupLightnings() {

        setupLightningsBounds();

        int lightningFrequency = (int) statsDB.getCurrentAllTimeNeurons();
        int lightningsAmount = (int) Math.ceil(lightningFrequency / 60.0);

        brainWidth = brainImage.getMaxWidth();
        brainHeight = brainImage.getMaxHeight();

        randomLightnings = new ArrayList<>();

        ConstraintLayout constraintLayout = v.findViewById(R.id.parent);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        Handler handler = new Handler();

        for (int i = 0; i < lightningsAmount; i++) {
            RandomLightning randomLightning = new RandomLightning(requireContext());
            randomLightning.setId(View.generateViewId());
            constraintLayout.addView(randomLightning, 0);
            randomLightning.getLayoutParams().width = 0;
            randomLightning.getLayoutParams().height = 0;
            ViewCompat.setElevation(randomLightning, 10);
            randomLightning.requestLayout();
            constraintSet.connect(randomLightning.getId(), ConstraintSet.START, brainImage.getId(), ConstraintSet.START, (int) (30 * getResources().getDisplayMetrics().scaledDensity));
            constraintSet.connect(randomLightning.getId(), ConstraintSet.END, brainImage.getId(), ConstraintSet.END, (int) (30 * getResources().getDisplayMetrics().scaledDensity));
            constraintSet.connect(randomLightning.getId(), ConstraintSet.TOP, brainImage.getId(), ConstraintSet.TOP, (int) (40 * getResources().getDisplayMetrics().scaledDensity));
            constraintSet.connect(randomLightning.getId(), ConstraintSet.BOTTOM, brainImage.getId(), ConstraintSet.BOTTOM, (int) (40 * getResources().getDisplayMetrics().scaledDensity));
            constraintSet.applyTo(constraintLayout);
            randomLightnings.add(randomLightning);

            int freq;
            if (i == lightningsAmount - 1) {
                freq = lightningFrequency;
            } else {
                freq = ThreadLocalRandom.current().nextInt(lightningFrequency / ((lightningsAmount - i) + 1), 2 * (lightningFrequency / ((lightningsAmount - i) + 1)));
                lightningFrequency -= freq;
            }
            if (lightningsAmount > 1) {
                handler.postDelayed(() -> randomLightning.startLightningsInFrequencyOf(freq), (long) (1000 / (lightningsAmount - 1)) * i);
            } else {
                randomLightning.startLightningsInFrequencyOf(freq);
            }
        }
    }

    /**
     * Sets up the lightning box's bounds.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setupLightningsBounds() {
        ArrayList<Double> sizes1 = PrefUtil.getArrayListPreferences(BRAIN_PREFERENCES_SHAREDPREFERENCES_NAME, LIGHTNING_VIEW_SIZES_PREFERENCE_NAME);
        ArrayList<Integer> sizes = new ArrayList<>();
    }

    /**
     * Initializes all the objects in this page.
     *
     * @param v is the fragments view (as the parent of all the objects inside of it).
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initObjects(View v) {
        brainImage = v.findViewById(R.id.brain_image);
        timeMethodSwitch = v.findViewById(R.id.time_method_switch);
        playPauseBtn = v.findViewById(R.id.play_pause_btn);
        addNewGoalBtn = v.findViewById(R.id.add_new_goal_btn);
        blurBackground = v.findViewById(R.id.blur_view_opening_fragment);
        addNewGoalDialog = v.findViewById(R.id.add_new_goal_dialog_container);
        addBtn = v.findViewById(R.id.add_btn_opening_fragment);
        cancelAddBtn = v.findViewById(R.id.cancel_btn_opening_fragment);
        newGoalsName = v.findViewById(R.id.name_et_opening_fragment);
        newGoalsDescription = v.findViewById(R.id.description_et_opening_fragment);
        newGoalsTimeEstimated = v.findViewById(R.id.time_estimation_et_opening_fragment);

        initCurrentGoalPicker();
        initCurrentGoalBar();
        updateCurrentGoalBar();
    }

    /**
     * Enables / disables the TimeMethod toggle switch.
     *
     * @param enabled is the boolean determines weather enabled (true) or disabled (false).
     * @param toggle  is the switch object itself that we want to enable / disable.
     */
    private static void setToggleEnabled(boolean enabled, ViewGroup toggle) {
        for (int i = 0; i < toggle.getChildCount(); i++) {
            View child = toggle.getChildAt(i);
            child.setClickable(!enabled);
            if (child instanceof ViewGroup) {
                setToggleEnabled(enabled, (ViewGroup) child);
            }
        }
    }

    /**
     * Initialize the current-goal progressbar under the brain drawing.
     * This bar lets the user select which goal he's currently working on.
     */
    private void initCurrentGoalBar() {
        currentGoalProgressBarButton = v.findViewById(R.id.goal_progress_bar);
        currentGoalProgressBarButton.enableDefaultGradient(true);
        currentGoalProgressBarButton.enableDefaultPress(true);
        currentGoalProgressBarButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                openWheelPickerInPos();
            }
        });
    }

    /**
     * Initializes the picker (RecyclerView) that pops when the user clicks the current-goal progressbar.
     * This picker lets the user select a new current goal which he'll work on.
     */
    private void initCurrentGoalPicker() {
        currentGoalPickerDialog = new BottomSheetDialog(requireContext());
        currentGoalPickerDialog.setContentView(R.layout.current_goal_bottom_sheet_dialog);
        currentGoalPickerDialog.setCancelable(true);
        currentGoalPickerDialog.setTitle(R.string.wheel_picker_title_text);
        currentGoalPicker = currentGoalPickerDialog.findViewById(R.id.wheel_picker_recyclerview);
        activeGoalsAdapter = new GoalsAdapter(getContext(), goalDB.getActiveGoalsArrayList());
        final CarouselLayoutManager layoutManager = new CarouselLayoutManager(CarouselLayoutManager.VERTICAL, false);
        layoutManager.setPostLayoutListener((CarouselLayoutManager.PostLayoutListener) new CarouselZoomPostLayoutListener());
        currentGoalPicker.setLayoutManager(layoutManager);
        currentGoalPicker.setHasFixedSize(true);
        currentGoalPicker.setAdapter(activeGoalsAdapter);
        currentGoalPicker.addOnScrollListener(new CenterScrollListener());

        activeGoalsAdapter.setOnItemClickListener(new GoalsAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(int position) {
                selectNewCurrentGoal(goalDB.getActiveGoalsArrayList().get(position).getName());
            }
        });
    }

    /**
     * Initializes all the Buttons' clicks (and similar to buttons and clicks).
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initButtonsClick() {
        timeMethodSwitch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked current) {
                switch (current) {
                    case LEFT:
                        PrefUtil.setTimeMethod(PrefUtil.TimeMethod.Pomodoro);
                        break;
                    case RIGHT:
                        PrefUtil.setTimeMethod(PrefUtil.TimeMethod.Timer);
                        break;
                }
            }
        });

        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPauseAction();
            }
        });

        blurBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addNewGoalDialog.getVisibility() == View.VISIBLE) {
                    closeAddNewGoalDialog();
                }
            }
        });

        addNewGoalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddNewGoalDialog();
            }
        });

        addBtn.setOnClickListener(view -> {
            Goal newGoal = new Goal(newGoalsName.getText().toString(), newGoalsDescription.getText().toString(), PublicMethods.getNewGoalsTimeEstimated(newGoalsTimeEstimated));
            ArrayList<String> allGoalsNames = new ArrayList<>();
            for (Goal goal : goalDB.getAllGoalsArrayList()) {
                allGoalsNames.add(goal.getName());
            }
            if (allGoalsNames.contains(newGoal.getName())) {
                PublicMethods.openIdenticalGoalNameErrorDialog(requireContext(), activity, newGoal.getName());
            } else if (newGoal.getName().trim().isEmpty()) {
                PublicMethods.openGoalNameNotValidErrorDialog(requireContext(), activity, newGoal.getName());
            } else if (newGoalsTimeEstimated.getText().toString().isEmpty() || PublicMethods.getNewGoalsTimeEstimated(newGoalsTimeEstimated) == 0) {
                PublicMethods.openGoalTimeEstimatedNotValidErrorDialog(requireContext(), activity);
            } else {
                goalDB.addGoal(newGoal);
                initCurrentGoalPicker();
                closeAddNewGoalDialog();
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        cancelAddBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                closeAddNewGoalDialog();
            }
        });
    }

    /**
     * The callback function that is called when the regular timer has stopped.
     *
     * @param goalName         is the name of the current goal in progress.
     * @param timerMillisExtra is the time that the timer has counted during the work session.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void onTimerStopped(String goalName, long timerMillisExtra, Context context) {
        goalDB.progressGoal(goalName, timerMillisExtra / 1000);
        updateCurrentGoalBar();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(endTimerReceiver);
        if (PrefUtil.startedBeforeEstimation() && goalDB.getGoalByName(PrefUtil.getCurrentGoal()).getProgress() > 100) {
            openEstimateBetterDialog(context);
        }
        activeGoalsAdapter.updateGoalsList(goalDB.getActiveGoalsArrayList());
    }


    /**
     * Calls a function that starts the regular timer service (sendCommandToStartTimerService()).
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void startTimerService(Context context) {
        updateRunning(true, context);
        LocalBroadcastManager.getInstance(context).registerReceiver(endTimerReceiver, new IntentFilter(SAVE_GOAL_PROGRESS_INTENT_ACTION));
        sendCommandToStartTimerService(context, PrefUtil.getCurrentGoal());
        PrefUtil.setStartedBeforeEstimation(goalDB.getGoalByName(PrefUtil.getCurrentGoal()).getProgress() < 100);
    }

    /**
     * Calls a function that stops the regular timer service (sendCommandToStopTimerService()).
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void stopTimerService(Context context) {
        updateRunning(false, context);
        sendCommandToStopTimerService(context);
    }

    /**
     * Starts the regular timer service (the background timer).
     *
     * @param goalName is the name of the current goal in progress.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void sendCommandToStartTimerService(Context context, String goalName) {
        timerServiceIntent.putExtra(TIMER_NOTIFICATION_SERVICE_CURRENT_GOAL_EXTRA_NAME, goalName);
        timerServiceIntent.setAction("startActivity");
        context.startForegroundService(timerServiceIntent);
    }

    /**
     * Stops the regular timer service (the background timer).
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void sendCommandToStopTimerService(Context context) {
        timerServiceIntent.setAction(ACTION_STOP_SERVICE);
        context.stopService(timerServiceIntent);
    }

    /**
     * Opens suggestBreak dialog.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void openSuggestBreakDialog(Context context) {
        dialogHandler = DialogHandler.getDialogHandler(context);
        Handler handler = new Handler();
        Runnable runnable = () -> ringtone.play();
        handler.postDelayed(runnable, 1);
        Runnable startBreak = () -> {
            stopTimerService(context);
            Runnable timeOutRunnable = () -> startTimeOutService(context);
            handler.postDelayed(timeOutRunnable, 50);
            ringtone.stop();
        };
        Runnable continueTimer = () -> {
            ringtone.stop();
        };

        dialogHandler.showDialog(activity,
                context,
                context.getString(R.string.suggest_break_dialog_label),
                "You've passed the pomodoro session length.\n" +
                        "We suggest you take a " + PublicMethods.formatStopWatchTime(PrefUtil.getPomodoroTimeOutLength() * 60000) + " long break before proceeding.\n" +
                        "Would you take our advice?",
                "I Will",
                startBreak,
                "I won't",
                continueTimer,
                DialogTypes.TYPE_QUESTION,
                null);
    }

    /**
     * Calls a function that starts the pomodoro timer service (sendCommandToStartTimerService()).
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void startPomodoro(Context context) {
        updateRunning(true, context);
        setToggleEnabled(false, timeMethodSwitch);
        currentGoalProgressBarButton.setEnabled(false);
        LocalBroadcastManager.getInstance(context).registerReceiver(endTimerReceiver, new IntentFilter(SAVE_GOAL_PROGRESS_INTENT_ACTION));
        sendCommandToStartPomodoroService(context, PrefUtil.getCurrentGoal());
        PrefUtil.setStartedBeforeEstimation(goalDB.getGoalByName(PrefUtil.getCurrentGoal()).getProgress() < 100);
    }

    /**
     * Calls a function (showLosePomodoroDialog()) to check if user really wants to stop the pomodoro session.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void tryStopPomodoro(Context context) {
        openLosePomodoroDialog(context, blurBackground);
    }

    /**
     * Calls a function that stops the pomodoro timer service (sendCommandToStartTimerService()).
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void stopPomodoro(Context context) {
        updateRunning(false, context);
        sendCommandToStopPomodoroService(context);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(endTimerReceiver);
    }

    /**
     * Starts the pomodoro timer service (the background timer).
     *
     * @param goalName is the name of the current goal in progress.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void sendCommandToStartPomodoroService(Context context, String goalName) {
        pomodoroServiceIntent.putExtra(TIMER_NOTIFICATION_SERVICE_CURRENT_GOAL_EXTRA_NAME, goalName);
        pomodoroServiceIntent.setAction("startActivity");
        context.startService(pomodoroServiceIntent);
    }

    /**
     * Stops the pomodoro timer service (the background timer).
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void sendCommandToStopPomodoroService(Context context) {
        pomodoroServiceIntent.setAction(ACTION_STOP_SERVICE);
        pomodoroServiceIntent.putExtra(POMODORO_SERVICE_STOP_FOR_REAL_EXTRA_NAME, true);
        context.startService(pomodoroServiceIntent);
    }

    /**
     * Opens losePomodoro dialog
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void openLosePomodoroDialog(Context context, RealtimeBlurView blurBackground) {

        dialogHandler = DialogHandler.getDialogHandler(context);
        Runnable stopPomodoroProcedure = () -> {
            stopPomodoro(context);
        };
        Runnable continuePomodoroProcedure = () -> {

        };
        dialogHandler.showDialog(activity,
                context,
                activity.getString(R.string.lose_pomodoro_dialog_label),
                activity.getString(R.string.lose_pomodoro_dialog_content_text),
                "Yes",
                stopPomodoroProcedure,
                "No",
                continuePomodoroProcedure,
                DialogTypes.TYPE_WARNING,
                null);
    }

    /**
     * Opens pomodoroFinishedDialog
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void openPomodoroFinishedDialog(Context context) {
        dialogHandler = DialogHandler.getDialogHandler(context);
        Handler handler = new Handler();
        Runnable runnable = () -> ringtone.play();
        handler.postDelayed(runnable, 1);
        Runnable proceedSessionProcedure = () -> {
            startTimeOutService(context);
            ringtone.stop();
        };
        Runnable finishSessionProcedure = () -> {
            ringtone.stop();
            //TODO: what happens when user wants to stop the session (maybe nothing)
        };

        dialogHandler.showDialog(activity,
                context,
                "Pomodoro Finished",
                "You've successfully finished this pomodoro session!" +
                        "\nWould you want to continue to the next one?" +
                        "\nIf you do, click yes and take a " + PublicMethods.formatStopWatchTime(PrefUtil.getPomodoroTimeOutLength() * 60000) + " long time-out",
                "Yes",
                proceedSessionProcedure,
                "No",
                finishSessionProcedure,
                DialogTypes.TYPE_CUSTOM,
                "pomodoro_finished.json");
    }

    /**
     * Handles what happens when pomodoro is finished (calls a function to open pomodoroFinishedDialog).
     *
     * @param context is the context passed from a non-static scope.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void onPomodoroFinished(Context context, String goalName) {
        goalDB.pomodoroProgressGoal(goalName);
        updateCurrentGoalBar();
        openPomodoroFinishedDialog(context);
        setToggleEnabled(true, timeMethodSwitch);
        currentGoalProgressBarButton.setEnabled(true);
        if (PrefUtil.startedBeforeEstimation() && goalDB.getGoalByName(PrefUtil.getCurrentGoal()).getProgress() > 100) {
            openEstimateBetterDialog(context);
        }
        activeGoalsAdapter.updateGoalsList(goalDB.getActiveGoalsArrayList());
    }

    private static void openEstimateBetterDialog(Context context) {
        dialogHandler = DialogHandler.getDialogHandler(context);
        Runnable thanksProcedure = () -> {

        };
        dialogHandler.showDialog(activity,
                context,
                "Estimate Better!",
                "You've passed the estimated time for this goal." +
                        "\nNext time, evaluate your goals better, so you can plan things ahead better." +
                        "\nGood luck!",
                "I'll Do My Best!",
                thanksProcedure,
                DialogTypes.TYPE_CUSTOM,
                "tip_star.json");
    }

    /**
     * Calls a function that starts the time-out timer service (sendCommandToStartTimeOutService()).
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void startTimeOutService(Context context) {
        updateRunning(true, context);
        setToggleEnabled(false, timeMethodSwitch);
        currentGoalProgressBarButton.setEnabled(false);
        LocalBroadcastManager.getInstance(context).registerReceiver(endTimerReceiver, new IntentFilter(SAVE_GOAL_PROGRESS_INTENT_ACTION));
        sendCommandToStartTimeOutService(context);
    }

    /**
     * Starts the time-out timer service (the background timer).
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void sendCommandToStartTimeOutService(Context context) {
        timeOutServiceIntent.setAction("startActivity");
        context.startForegroundService(timeOutServiceIntent);
    }

    /**
     * Handles what happens when time-out is over (alarms and shows a dialog to start a new pomodoro session).
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void onTimeOutOver(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(endTimerReceiver);
        Handler handler = new Handler();
        Runnable runnable = () -> ringtone.play();
        handler.postDelayed(runnable, 1);
        openTimeOutOverDialog(context);
    }

    /**
     * Opens a dialog to let the user start a new pomodoro session.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void openTimeOutOverDialog(Context context) {
        dialogHandler = DialogHandler.getDialogHandler(context);
        Handler handler = new Handler();
        Runnable runnable = () -> ringtone.play();
        handler.postDelayed(runnable, 1);
        String title, content, positiveBtnCap, negativeBtnCap, customAnimationName;
        Runnable positiveProcedure;
        Runnable negativeProcedure;
        int dialogType;
        title = "Time-Out Finished";
        if (PrefUtil.getTimeMethod() == PrefUtil.TimeMethod.TimeOut) {
            content = "The break is over! Time to work." +
                    "\nAre you sure that you're ready to commit to another pomodoro session?" +
                    "\nIf you are, click yes and start a new " + PublicMethods.formatStopWatchTime(PrefUtil.getPomodoroLength() * 60000) + " long pomodoro work session.";
            positiveBtnCap = "Yes";
            positiveProcedure = () -> {
                startPomodoro(context);
                ringtone.stop();
            };
            negativeBtnCap = "No";
            negativeProcedure = () -> {
                setToggleEnabled(true, timeMethodSwitch);
                currentGoalProgressBarButton.setEnabled(true);
                ringtone.stop();
            };
            dialogType = DialogTypes.TYPE_CUSTOM;
            customAnimationName = "start_work.json";
        } else {
            content = "The break is over! Time to work." +
                    "\nWe suggest this time you try the pomodoro timer mode!" +
                    "\nIt might require more commitment, but it'll pay off, by score and by your real-life achievements." +
                    "\nSo, what will you choose?";
            positiveBtnCap = "Pomodoro Mode";
            positiveProcedure = () -> {
                timeMethodSwitch.setChecked(IconSwitch.Checked.LEFT);
                startPomodoro(context);
                ringtone.stop();
            };
            negativeBtnCap = "Regular Mode";
            negativeProcedure = () -> {
                PrefUtil.setTimeMethod(PrefUtil.TimeMethod.Timer);
                startTimerService(context);
                ringtone.stop();
            };
            dialogType = DialogTypes.TYPE_QUESTION;
            customAnimationName = null;
        }

        dialogHandler.showDialog(activity,
                context,
                title,
                content,
                positiveBtnCap,
                positiveProcedure,
                negativeBtnCap,
                negativeProcedure,
                dialogType,
                customAnimationName);
    }

    /**
     * Shows the shadow appears when a dialog is opened.
     */
    public static void fadeBlurIn(RealtimeBlurView blurBackground) {
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
     * Updates the state of the timer(s) and the PlayPause Button.
     *
     * @param isRunning represents whether the timer is running or not.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void updateRunning(Boolean isRunning, Context context) {
        PrefUtil.setTimerState(isRunning ? TimerState.Running : TimerState.Stopped);
        updatePlayPauseIcon(playPauseBtn, context);
    }

    /**
     * Updates the toggle switch according to the current TimeMethod (in case it's not already updated).
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void updateToggle() {
        switch (PrefUtil.getTimeMethod()) {
            case Pomodoro:
            case TimeOut:
                timeMethodSwitch.setChecked(IconSwitch.Checked.LEFT);
                break;
            case Timer:
            case TimerTimeOut:
                timeMethodSwitch.setChecked(IconSwitch.Checked.RIGHT);
                break;
        }
    }

    /**
     * Selects the new current goal in progress.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void selectNewCurrentGoal(String newCurrentGoalName) {
        PrefUtil.setCurrentGoal(newCurrentGoalName);
        updateCurrentGoalBar();
        closeWheelPicker();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void updateCurrentGoalBar() {
        Goal goal = goalDB.getGoalByName(PrefUtil.getCurrentGoal());
        if (goal != null && !goal.isAchieved()) {
            currentGoalProgressBarButton.setText(goal.getName());
            currentGoalProgressBarButton.setProgress(goal.getProgress());
        } else {
            currentGoalProgressBarButton.setText("No Goal Selected");
            currentGoalProgressBarButton.setProgress(0);
        }
    }

    /**
     * Opens the picker (RecyclerView) of the active goals for the user to choose the new current
     * goal to work on.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void openWheelPickerInPos() {
        currentGoalPickerDialog.show();
        if (PublicMethods.positionOfGoalInGoalsArrayList(PrefUtil.getCurrentGoal(), goalDB.getActiveGoalsArrayList()) != -1) {
            currentGoalPicker.scrollToPosition(PublicMethods.positionOfGoalInGoalsArrayList(PrefUtil.getCurrentGoal(), goalDB.getActiveGoalsArrayList()));
        }
    }

    /**
     * Closes the picker (RecyclerView) of the active goals after the user had chosen the new
     * current goal to work on.
     */
    private void closeWheelPicker() {
        currentGoalPickerDialog.hide();
    }

    /**
     * Opens the add-new-active-goal dialog.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void openAddNewGoalDialog() {
        if (PrefUtil.getTimerState() == TimerState.Running) {
            PublicMethods.openTimerIsRunningErrorDialog(requireContext(), activity);
        } else {
            //fab.setExpanded(true);
            addNewGoalDialog.setVisibility(View.VISIBLE);
            fadeBlurIn(blurBackground);
            addNewGoalDialog.setClickable(true);
        }

    }


    /**
     * Closes the add-new-active-goal dialog.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
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

        updateCurrentGoalBar();
    }


    /**
     * Handles what happens when the PlayPause Button is clicked.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void playPauseAction() {
        if (PrefUtil.getTimerState() == TimerState.Running) {
            switch (PrefUtil.getTimeMethod()) {
                case Timer:
                    stopTimerService(requireContext());
                    break;
                case Pomodoro:
                    tryStopPomodoro(requireContext());
                    break;
            }
            setToggleEnabled(true, timeMethodSwitch);
            currentGoalProgressBarButton.setEnabled(true);
        } else {
            if (PrefUtil.getCurrentGoal().isEmpty()) {
                Toast.makeText(requireContext(), "No Goal Was Selected", Toast.LENGTH_SHORT).show();
            } else {
                switch (PrefUtil.getTimeMethod()) {
                    case Timer:
                        startTimerService(requireContext());
                        break;
                    case Pomodoro:
                        startPomodoro(requireContext());
                        break;
                }
                setToggleEnabled(false, timeMethodSwitch);
                currentGoalProgressBarButton.setEnabled(false);
            }
        }
    }

    /**
     * Updates the PlayPause button state.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void updatePlayPauseIcon(FloatingActionButton playPauseBtn, Context context) {
        int res;
        if (PrefUtil.getTimerState() != TimerState.Running) {
            res = R.drawable.pausetoplay_vector_anim;
            playPauseBtn.setForeground(context.getResources().getDrawable(R.drawable.fab_green_play_pause_opening_fragment_border));
            playPauseBtn.setRippleColor(context.getResources().getColorStateList(R.color.stop_red));
        } else {
            res = R.drawable.playtopause_vector_anim;
            playPauseBtn.setForeground(context.getResources().getDrawable(R.drawable.fab_red_play_pause_opening_fragment_border));
            playPauseBtn.setRippleColor(context.getResources().getColorStateList(R.color.start_green));
        }
        playPauseBtn.setImageResource(res);

        Drawable icon = playPauseBtn.getDrawable();

        if (icon instanceof AnimatedVectorDrawable) {
            ((AnimatedVectorDrawable) icon).start();
        }
    }

    /**
     * Handles back presses
     *
     * @return weather the back press was handled by the OpeningFragment or not.
     */
    @Override
    public boolean onBackPressed() {
        return false;
    }
}
