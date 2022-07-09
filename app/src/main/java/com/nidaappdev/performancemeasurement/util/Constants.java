package com.nidaappdev.performancemeasurement.util;

public class Constants {
    /**
     * SharedPreferences Files Names
     */
    public static final String BRAIN_PREFERENCES_SHAREDPREFERENCES_NAME = "brainPreferences";
    public static final String TIMER_PREFERENCES_SHAREDPREFERENCES_NAME = "timerPreferences";
    public static final String CURRENT_ACTIVE_GOAL_SHAREDPREFERENCES_NAME = "currentGoalPreferences";

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

}
