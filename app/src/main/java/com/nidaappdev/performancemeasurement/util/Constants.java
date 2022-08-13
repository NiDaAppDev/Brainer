package com.nidaappdev.performancemeasurement.util;

import com.nidaappdev.performancemeasurement.App;
import com.nidaappdev.performancemeasurement.R;

public class Constants {
    /**
     * Pages Names
     */
    public static final String MAIN_PAGE_NAME = "Main";
    public static final String ACTIVE_GOALS_PAGE_NAME = "ActiveGoals";
    public static final String ACHIEVED_GOALS_PAGE_NAME = "AchievedGoals";
    public static final String STATS_PAGE_NAME = "Stats";
    public static final String ACHIEVEMENTS_PAGE_NAME = "Achievements";
    public static final String SETTINGS_PAGE_NAME = "Settings";

    /**
     * SharedPreferences Files Names
     */
    public static final String BRAIN_PREFERENCES_SHAREDPREFERENCES_NAME = "brainPreferences";
    public static final String TIMER_PREFERENCES_SHAREDPREFERENCES_NAME = "timerPreferences";
    public static final String CURRENT_ACTIVE_GOAL_SHAREDPREFERENCES_NAME = "currentGoalPreferences";
    public static final String TUTORIAL_SHAREDPREFERENCES_NAME = "tutorialPreferences";

    /**
     * Measures Preferences
     */
    public static final String LIGHTNING_VIEW_SIZES_PREFERENCE_NAME = "lightningViewSizes";

    /**
     * Regular Timer Related
     */
    public static final String TIME_METHOD_PREFERENCE_NAME = "timeMethod";
    public static final String TIMER_STATE_PREFERENCE_NAME = "timerStatePreference";
    public static final String SUGGEST_BREAK_PREFERENCE_NAME = "suggestBreak";

    /**
     * Pomodoro Related
     */
    public static final String POMODORO_LENGTH_IN_MINUTES_PREFERENCE_NAME = "pomodoroLengthInMinutes";
    public static final String POMODORO_TIME_OUT_LENGTH_IN_MINUTES_PREFERENCE_NAME = "pomodoroTimeOutLengthInMinutes";
    public static final String POMODORO_SERVICE_STOP_FOR_REAL_EXTRA_NAME = "stopForReal";
    public static final String POMODORO_SERVICE_POMODORO_FINISHED_EXTRA_NAME = "pomodoroFinished";
    public static final String POMODORO_TIME_OUT_SERVICE_TIME_OUT_FINISHED_EXTRA_NAME = "timeOutFinished";

    /**
     * Notification and Broadcast Related
     */
    public static final int TIMER_NOTIFICATION_ID = 1;
    public static final String TIMER_NOTIFICATION_SERVICE_CURRENT_GOAL_EXTRA_NAME = "currentGoalName";
    public static final String TIMER_NOTIFICATION_SERVICE_TIME_IN_MILLIS_EXTRA_NAME = "timeInMillis";
    public static final String TIMER_NOTIFICATION_SERVICE_UPDATE_PLAY_PAUSE_BUTTON_EXTRA_NAME = "updatePlayPauseButton";
    public static final String ACTION_STOP_SERVICE = "stopService";
    public static final String SAVE_GOAL_PROGRESS_INTENT_ACTION = "saveGoalProgress";
    public static final String GO_TO_OPENING_FRAGMENT = "endCurrentProgress";
    public static final String SUGGEST_BREAK_EXTRA_NAME = "suggestBreak";
    public static final String STARTED_BEFORE_ESTIMATION = "startedBeforeEstimation";

    /**
     * Active Goals Related
     */
    public static final String ACTIVE_GOALS_SORT_PREFERENCE_NAME = "activeGoalsSort";
    public static final String ACTIVE_GOALS_ASCENDING_PREFERENCE_NAME = "activeGoalsAscending";

    /**
     * Achieved Goals Related
     */
    public static final String ACHIEVED_GOALS_SORT_PREFERENCE_NAME = "achievedGoalsSort";
    public static final String ACHIEVED_GOALS_ASCENDING_PREFERENCE_NAME = "achievedGoalsAscending";
    public static final String ACHIEVED_GOALS_FILTERS_PREFERENCE_NAME = "achievedGoalsFilters";

    /**
     * Current Goal Preferences
     */
    public static final String CURRENT_GOAL_PREFERENCE_NAME = "currentGoal";

    /**
     * FireStore Related
     */
    public static final String USERS_COLLECTION_NAME = "users";
    public static final String SETTINGS_COLLECTION_NAME = "settings";
    public static final String GOALS_DB_COLLECTION_NAME = "goalsDB";
    public static final String STATISTICS_DB_COLLECTION_NAME = "statisticsDB";

    /**
     * Tutorial Related
     */
    public static final String FINISHED_TUTORIAL_PREFERENCE_NAME = "finishedTutorial";
    public static final String SKIPPED_TUTORIAL_PREFERENCE_NAME = "skippedTutorial";
    public static final String TUTORIAL_PAGE_PREFERENCE_NAME = "tutorialPage";
    public static final String TUTORIAL_STATION_INDEX_PREFERENCE_NAME = "tutorialStationIndex";
    public static final String TUTORIAL_FIRST_EXAMPLE_GOAL_NAME = "Study for the math exam";
    public static final String TUTORIAL_FIRST_EXAMPLE_GOAL_DESCRIPTION = "Study as hard as I can so I won't fail.\n" +
            ".\n" +
            ".\n" +
            ".\n" +
            "Good luck to me!";
    public static final String TUTORIAL_FIRST_EXAMPLE_GOAL_TIME_ESTIMATED = "000200";
    public static final String TUTORIAL_SECOND_EXAMPLE_GOAL_NAME = "Study trigonometry for the math exam";
    public static final String TUTORIAL_SECOND_EXAMPLE_GOAL_DESCRIPTION = "Sin, Cos and Tan";
    public static final String TUTORIAL_SECOND_EXAMPLE_GOAL_TIME_ESTIMATED = "000100";
    public static int DEFAULT_MASK_COLOR = 0x70000000;
    public static long DEFAULT_DELAY_MILLIS = 0;
    public static long DEFAULT_FADE_DURATION = 700;
    public static int DEFAULT_TARGET_PADDING = 10;
    public static int DEFAULT_COLOR_TEXTVIEW_INFO = 0xFF000000;
    public static int DEFAULT_DOT_SIZE = 55;

    /**
     * Achievements Related
     */
    public static final String[] achievementsNames = new String[]{
            "Learner",
            "Planner",
            "Multi Tasker",
            "Smart Worker",
            "Committed To Success",
            "Rookie Achiever",
            "Expert Achiever",
            "Charter I",
            "Charter II",
            "Charter III",
            "Charter IV",
            "Zeus I",
            "Zeus II",
            "Zeus III",
            "Zeus IV",
            "God Of Gods"
    };
    public static final String[] achievementsDescriptions = new String[]{
            "You've finished the tutorial.",
            "You've created a goal at least once.",
            "You've created 50 goals or more.",
            "You've finished a Pomodoro session at least once.",
            "You've finished at least 10 Pomodoro sessions",
            "You've completed a goal at least once.",
            "You've completed at least 10 goals",
            "You've unlocked the \"" + App.appContext.getString(R.string.division_of_work_time_chart_title) + "\" chart.",
            "You've unlocked the \"" + App.appContext.getString(R.string.timer_mode_division_chart_title) + "\" chart.",
            "You've unlocked the \"" + App.appContext.getString(R.string.timer_mode_results_chart_title) + "\".",
            "You've unlocked the \"" + App.appContext.getString(R.string.neurons_progress_chart_title) + "\".",
            "You've got enough neurons to get at least one lightning per minute in the main page.",
            "You've got enough neurons to get at least 50 lightnings per minute in the main page.",
            "You've got enough neurons to get at least 100 lightnings per minute in the main page.",
            "You've got enough neurons to get at least 500 lightnings per minute in the main page.",
            "You've got enough neurons to get at least 1000 lightnings per minute in the main page."
    };
    public static final String[] achievementsRequirements = new String[]{
            "Finish the tutorial",
            "Create a goal",
            "Create at least 50 goals",
            "Finish a Pomodoro session once",
            "Finish 10 Pomodoro sessions",
            "Finish a goal",
            "Finish 10 goals",
            "Run one of the timer modes",
            "Finish a goal",
            "Finish a goal",
            "Get neurons",
            "Get neurons",
            "Get at least 50 neurons",
            "Get at least 100 neurons",
            "Get at least 500 neurons",
            "Get at least 1000 neurons"
    };
    public static final int[] achievementsIconsRes = new int[]{
            R.drawable.learner,
            R.drawable.planner,
            R.drawable.multi_tasker,
            R.drawable.smart_worker,
            R.drawable.committed_to_success,
            R.drawable.rookie_achiever,
            R.drawable.expert_achiever,
            R.drawable.charter_1,
            R.drawable.charter_2,
            R.drawable.charter_3,
            R.drawable.charter_4,
            R.drawable.zeus_1,
            R.drawable.zeus_2,
            R.drawable.zeus_3,
            R.drawable.zeus_4,
            R.drawable.god_of_gods
    };

}
