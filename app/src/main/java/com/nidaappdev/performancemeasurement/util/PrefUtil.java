package com.nidaappdev.performancemeasurement.util;

import static android.content.Context.MODE_PRIVATE;
import static com.nidaappdev.performancemeasurement.App.settingsReference;
import static com.nidaappdev.performancemeasurement.util.Constants.ACHIEVED_GOALS_ASCENDING_PREFERENCE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.ACHIEVED_GOALS_FILTERS_PREFERENCE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.ACHIEVED_GOALS_PAGE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.ACHIEVED_GOALS_SORT_PREFERENCE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.ACHIEVEMENTS_PAGE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.ACTIVE_GOALS_PAGE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.BRAIN_PREFERENCES_SHAREDPREFERENCES_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.MAIN_PAGE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.SETTINGS_PAGE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.SKIPPED_TUTORIAL_PREFERENCE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.STATS_PAGE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.TUTORIAL_PAGE_PREFERENCE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.TUTORIAL_SHAREDPREFERENCES_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.ACTIVE_GOALS_ASCENDING_PREFERENCE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.ACTIVE_GOALS_SORT_PREFERENCE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.CURRENT_ACTIVE_GOAL_SHAREDPREFERENCES_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.CURRENT_GOAL_PREFERENCE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.FINISHED_TUTORIAL_PREFERENCE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.POMODORO_LENGTH_IN_MINUTES_PREFERENCE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.POMODORO_TIME_OUT_LENGTH_IN_MINUTES_PREFERENCE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.STARTED_BEFORE_ESTIMATION;
import static com.nidaappdev.performancemeasurement.util.Constants.SUGGEST_BREAK_PREFERENCE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.TIMER_PREFERENCES_SHAREDPREFERENCES_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.TIMER_STATE_PREFERENCE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.TIME_METHOD_PREFERENCE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.TUTORIAL_STATION_INDEX_PREFERENCE_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nidaappdev.performancemeasurement.fragments.OpeningFragment;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.PublicMethods;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.M)
public class PrefUtil {

    public static Context appContext = PublicMethods.getAppContext();

    public enum TimeMethod {
        Pomodoro, Timer, TimeOut, TimerTimeOut
    }

    public enum ActiveSortMode {
        Date, Name, Progress
    }

    public enum AchievedSortMode {
        Name, FinishDate, Difficulty, Evolving, Satisfaction
    }

    public static <T> void addNewOrEditSharedPreferences(String preferenceName, String valueName, T value) {

        SharedPreferences sharedPreferences = appContext.getSharedPreferences(preferenceName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        HashMap<String, Object> sharedPreferenceData = new HashMap<>();

        if (value instanceof String) {
            editor.putString(valueName, (String) value);
            sharedPreferenceData.put(valueName, (String) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(valueName, (Boolean) value);
            sharedPreferenceData.put(valueName, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(valueName, (Integer) value);
            sharedPreferenceData.put(valueName, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(valueName, (long) value);
            sharedPreferenceData.put(valueName, (long) value);
        } else if (value instanceof Float) {
            editor.putFloat(valueName, (Float) value);
            sharedPreferenceData.put(valueName, (Float) value);
        } else if (value instanceof ArrayList) {
            Gson gson = new Gson();
            String json = gson.toJson(value);
            editor.putString(valueName, json);
            sharedPreferenceData.put(valueName, json);
        }

        settingsReference.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> settingsPreferences = queryDocumentSnapshots.getDocuments();
                    if(!settingsPreferences.isEmpty()) {
                        for (DocumentSnapshot settingPreference : settingsPreferences) {
                            if(settingPreference.getId().equals(preferenceName)) {
                                Map<String, Object> settingData = settingPreference.getData();
                                if(settingData.containsKey(valueName)) {
                                    settingData.remove(valueName);
                                }
                                if (settingData != null) {
                                    sharedPreferenceData.putAll(settingData);
                                }
                            }
                        }
                    }
                    settingsReference.document(preferenceName).set(sharedPreferenceData);
                });

        editor.apply();
    }

    public static <T> ArrayList<T> getArrayListPreferences(String preferenceName, String valuesName) {

        SharedPreferences sharedPreferences = appContext.getSharedPreferences(preferenceName, MODE_PRIVATE);
        Gson gson = new Gson();
        ArrayList<T> out;
        String json = sharedPreferences.getString(valuesName, "");
        if (!json.isEmpty()) {
            Type type = new TypeToken<ArrayList<T>>() {
            }.getType();
            out = gson.fromJson(json, type);
        } else {
            out = null;
        }
        return out;

    }

    private static boolean getBooleanPreference(String preferenceName, String valueName) {
        SharedPreferences sharedPreferences = appContext.getSharedPreferences(preferenceName, MODE_PRIVATE);
        return sharedPreferences.getBoolean(valueName, false);
    }

    private static String getStringPreference(String preferenceName, String valueName) {
        SharedPreferences sharedPreferences = appContext.getSharedPreferences(preferenceName, MODE_PRIVATE);
        return sharedPreferences.getString(valueName, "");
    }

    private static int getIntPreference(String preferenceName, String valueName) {
        SharedPreferences sharedPreferences = appContext.getSharedPreferences(preferenceName, MODE_PRIVATE);
        return sharedPreferences.getInt(valueName, 0);
    }

    private static long getLongPreference(String preferenceName, String valueName) {
        SharedPreferences sharedPreferences = appContext.getSharedPreferences(preferenceName, MODE_PRIVATE);
        return sharedPreferences.getLong(valueName, 0L);
    }

    private static Float getFloatPreference(String preferenceName, String valueName) {
        SharedPreferences sharedPreferences = appContext.getSharedPreferences(preferenceName, MODE_PRIVATE);
        return sharedPreferences.getFloat(valueName, 0);
    }

    public static void setTimeMethod(PrefUtil.TimeMethod timeMethod) {
        addNewOrEditSharedPreferences(TIMER_PREFERENCES_SHAREDPREFERENCES_NAME, TIME_METHOD_PREFERENCE_NAME, timeMethod.ordinal());
    }

    public static TimeMethod getTimeMethod() {
        return TimeMethod.values()[getIntPreference(TIMER_PREFERENCES_SHAREDPREFERENCES_NAME, TIME_METHOD_PREFERENCE_NAME)];
    }

    public static void setTimerState(OpeningFragment.TimerState state) {
        addNewOrEditSharedPreferences(TIMER_PREFERENCES_SHAREDPREFERENCES_NAME, TIMER_STATE_PREFERENCE_NAME, state.ordinal());
    }

    public static OpeningFragment.TimerState getTimerState() {
        return OpeningFragment.TimerState.values()[getIntPreference(TIMER_PREFERENCES_SHAREDPREFERENCES_NAME, TIMER_STATE_PREFERENCE_NAME)];
    }

    public static void setActiveSortMode(ActiveSortMode sortMode) {
        addNewOrEditSharedPreferences(CURRENT_ACTIVE_GOAL_SHAREDPREFERENCES_NAME, ACTIVE_GOALS_SORT_PREFERENCE_NAME, sortMode.ordinal());
    }

    public static ActiveSortMode getActiveSortMode() {
        return ActiveSortMode.values()[getIntPreference(CURRENT_ACTIVE_GOAL_SHAREDPREFERENCES_NAME, ACTIVE_GOALS_SORT_PREFERENCE_NAME)];
    }

    public static void setActiveAscending(boolean ascending) {
        addNewOrEditSharedPreferences(CURRENT_ACTIVE_GOAL_SHAREDPREFERENCES_NAME, ACTIVE_GOALS_ASCENDING_PREFERENCE_NAME, ascending);
    }

    public static boolean getActiveGoalsAscending() {
        return getBooleanPreference(CURRENT_ACTIVE_GOAL_SHAREDPREFERENCES_NAME, ACTIVE_GOALS_ASCENDING_PREFERENCE_NAME);
    }

    public static void setAchievedSortMode(AchievedSortMode sortMode) {
        addNewOrEditSharedPreferences(CURRENT_ACTIVE_GOAL_SHAREDPREFERENCES_NAME, ACHIEVED_GOALS_SORT_PREFERENCE_NAME, sortMode.ordinal());
    }

    public static AchievedSortMode getAchievedSortMode() {
        return AchievedSortMode.values()[getIntPreference(CURRENT_ACTIVE_GOAL_SHAREDPREFERENCES_NAME, ACHIEVED_GOALS_SORT_PREFERENCE_NAME)];
    }

    public static void setAchievedAscending(boolean ascending) {
        addNewOrEditSharedPreferences(CURRENT_ACTIVE_GOAL_SHAREDPREFERENCES_NAME, ACHIEVED_GOALS_ASCENDING_PREFERENCE_NAME, ascending);
    }

    public static boolean getAchievedGoalsAscending() {
        return getBooleanPreference(CURRENT_ACTIVE_GOAL_SHAREDPREFERENCES_NAME, ACHIEVED_GOALS_ASCENDING_PREFERENCE_NAME);
    }

    public static void setAchievedGoalsFilters(ArrayList<Integer> filters) {
        addNewOrEditSharedPreferences(CURRENT_ACTIVE_GOAL_SHAREDPREFERENCES_NAME, ACHIEVED_GOALS_FILTERS_PREFERENCE_NAME, filters);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ArrayList<Integer> getAchievedGoalsFilters() {
        List<Double> dValues = new ArrayList<>();
        dValues = getArrayListPreferences(CURRENT_ACTIVE_GOAL_SHAREDPREFERENCES_NAME, ACHIEVED_GOALS_FILTERS_PREFERENCE_NAME);
        List<Integer> result = dValues.stream()
                .map(p -> p.intValue())
                .collect(Collectors.toList());
        return (ArrayList<Integer>) result;
    }

    public static void setPomodoroLength(long pomodoroLength) {
        addNewOrEditSharedPreferences(TIMER_PREFERENCES_SHAREDPREFERENCES_NAME, POMODORO_LENGTH_IN_MINUTES_PREFERENCE_NAME, pomodoroLength);
    }

    public static long getPomodoroLength() {
        return getLongPreference(TIMER_PREFERENCES_SHAREDPREFERENCES_NAME, POMODORO_LENGTH_IN_MINUTES_PREFERENCE_NAME);
    }

    public static void setPomodoroTimeOutLength(long pomodoroTimeOutLength) {
        addNewOrEditSharedPreferences(TIMER_PREFERENCES_SHAREDPREFERENCES_NAME, POMODORO_TIME_OUT_LENGTH_IN_MINUTES_PREFERENCE_NAME, pomodoroTimeOutLength);
    }

    public static long getPomodoroTimeOutLength() {
        return getLongPreference(TIMER_PREFERENCES_SHAREDPREFERENCES_NAME, POMODORO_TIME_OUT_LENGTH_IN_MINUTES_PREFERENCE_NAME);
    }

    public static void setCurrentGoal(String goalName) {
        addNewOrEditSharedPreferences(CURRENT_ACTIVE_GOAL_SHAREDPREFERENCES_NAME, CURRENT_GOAL_PREFERENCE_NAME, goalName);
    }

    public static String getCurrentGoal() {
        return getStringPreference(CURRENT_ACTIVE_GOAL_SHAREDPREFERENCES_NAME, CURRENT_GOAL_PREFERENCE_NAME);
    }

    public static void setSuggestBreak(boolean suggestBreak) {
        addNewOrEditSharedPreferences(CURRENT_ACTIVE_GOAL_SHAREDPREFERENCES_NAME, SUGGEST_BREAK_PREFERENCE_NAME, suggestBreak);
    }

    public static boolean getSuggestBreak() {
        return getBooleanPreference(CURRENT_ACTIVE_GOAL_SHAREDPREFERENCES_NAME, SUGGEST_BREAK_PREFERENCE_NAME);
    }

    public static void setStartedBeforeEstimation(boolean startedBeforeEstimation) {
        addNewOrEditSharedPreferences(CURRENT_ACTIVE_GOAL_SHAREDPREFERENCES_NAME, STARTED_BEFORE_ESTIMATION, startedBeforeEstimation);
    }

    public static boolean startedBeforeEstimation() {
        return getBooleanPreference(CURRENT_ACTIVE_GOAL_SHAREDPREFERENCES_NAME, STARTED_BEFORE_ESTIMATION);
    }

    public static void setFinishedTutorial(String pageName, boolean finishedTutorial) {
        addNewOrEditSharedPreferences(TUTORIAL_SHAREDPREFERENCES_NAME, FINISHED_TUTORIAL_PREFERENCE_NAME + pageName, finishedTutorial);
    }

    public static boolean finishedTutorial(String pageName) {
        return getBooleanPreference(TUTORIAL_SHAREDPREFERENCES_NAME, FINISHED_TUTORIAL_PREFERENCE_NAME + pageName);
    }

    public static boolean finishedTutorial() {
        return finishedTutorial(MAIN_PAGE_NAME) &&
                finishedTutorial(ACTIVE_GOALS_PAGE_NAME) &&
                finishedTutorial(ACHIEVED_GOALS_PAGE_NAME);
    }

    public static void setSkippedTutorial(String pageName, boolean skippedTutorial) {
        addNewOrEditSharedPreferences(TUTORIAL_SHAREDPREFERENCES_NAME, SKIPPED_TUTORIAL_PREFERENCE_NAME + pageName, skippedTutorial);
    }

    public static boolean skippedTutorial(String pageName) {
        return getBooleanPreference(TUTORIAL_SHAREDPREFERENCES_NAME, SKIPPED_TUTORIAL_PREFERENCE_NAME + pageName);
    }

    public static boolean skippedTutorial() {
        return skippedTutorial(MAIN_PAGE_NAME) &&
                skippedTutorial(ACTIVE_GOALS_PAGE_NAME) &&
                skippedTutorial(ACHIEVED_GOALS_PAGE_NAME);
    }

    public static void setTutorialStationIndex(String pageName, int tutorialIndex) {
        addNewOrEditSharedPreferences(TUTORIAL_SHAREDPREFERENCES_NAME, TUTORIAL_STATION_INDEX_PREFERENCE_NAME + pageName, tutorialIndex);
    }

    public static int getTutorialStationIndex(String pageName) {
        return getIntPreference(TUTORIAL_SHAREDPREFERENCES_NAME, TUTORIAL_STATION_INDEX_PREFERENCE_NAME + pageName);
    }

    public static void clearAllSharedPreferences() {
        SharedPreferences sharedPreferences = appContext.getSharedPreferences(BRAIN_PREFERENCES_SHAREDPREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();

        sharedPreferences = appContext.getSharedPreferences(TIMER_PREFERENCES_SHAREDPREFERENCES_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear().apply();

        sharedPreferences = appContext.getSharedPreferences(CURRENT_ACTIVE_GOAL_SHAREDPREFERENCES_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear().apply();

        sharedPreferences = appContext.getSharedPreferences(TUTORIAL_SHAREDPREFERENCES_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear().apply();
    }
}
