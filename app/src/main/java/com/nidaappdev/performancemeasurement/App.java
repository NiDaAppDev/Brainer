package com.nidaappdev.performancemeasurement;

import static com.nidaappdev.performancemeasurement.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_ACHIEVED;
import static com.nidaappdev.performancemeasurement.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_COUNTED_POMODORO;
import static com.nidaappdev.performancemeasurement.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_COUNTED_POMODORO_TIME;
import static com.nidaappdev.performancemeasurement.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_COUNTED_TIME;
import static com.nidaappdev.performancemeasurement.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_DESCRIPTION;
import static com.nidaappdev.performancemeasurement.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_DIFFICULTY;
import static com.nidaappdev.performancemeasurement.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_ESTIMATED_TIME;
import static com.nidaappdev.performancemeasurement.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_EVOLVING;
import static com.nidaappdev.performancemeasurement.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_FINISH_DATE;
import static com.nidaappdev.performancemeasurement.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_NAME;
import static com.nidaappdev.performancemeasurement.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_PARENT;
import static com.nidaappdev.performancemeasurement.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_SATISFACTION;
import static com.nidaappdev.performancemeasurement.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_TAGS;
import static com.nidaappdev.performancemeasurement.databaseObjects.GoalDBHelper.GOALS_DATABASE_NAME;
import static com.nidaappdev.performancemeasurement.databaseObjects.StatisticsContract.StatisticsEntry.COLUMN_DATE;
import static com.nidaappdev.performancemeasurement.databaseObjects.StatisticsContract.StatisticsEntry.COLUMN_HOUR;
import static com.nidaappdev.performancemeasurement.databaseObjects.StatisticsContract.StatisticsEntry.COLUMN_HOURLY_NEURONS;
import static com.nidaappdev.performancemeasurement.databaseObjects.StatisticsContract.StatisticsEntry.COLUMN_HOURLY_SECONDS_OF_WORK;
import static com.nidaappdev.performancemeasurement.databaseObjects.StatisticsContract.StatisticsEntry.COLUMN_PAYED_MONTHLY_NEURONS;
import static com.nidaappdev.performancemeasurement.databaseObjects.StatisticsDBHelper.STATISTICS_DATABASE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.GOALS_DB_COLLECTION_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.POMODORO_LENGTH_IN_MINUTES_PREFERENCE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.POMODORO_TIME_OUT_LENGTH_IN_MINUTES_PREFERENCE_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.SETTINGS_COLLECTION_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.STATISTICS_DB_COLLECTION_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.USERS_COLLECTION_NAME;

import android.app.ActivityManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;

import androidx.annotation.RequiresApi;

import com.dd.CircularProgressButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nidaappdev.performancemeasurement.customObjects.Goal;
import com.nidaappdev.performancemeasurement.databaseObjects.GoalDBHelper;
import com.nidaappdev.performancemeasurement.databaseObjects.StatisticsDBHelper;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.PublicMethods;
import com.nidaappdev.performancemeasurement.util.PrefUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class App extends Application {

    public static Context appContext;
    public static final String CHANNEL_ID = "timerServiceChannel";

    private static GoalDBHelper goalDB;
    private static StatisticsDBHelper statisticsDB;

    public static CollectionReference goalsDBReference, statisticsDBReference, settingsReference;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        createNotificationChannel();

        goalDB = new GoalDBHelper(appContext);
        statisticsDB = new StatisticsDBHelper(appContext);

        initCloudReferences();
    }

    private static void initCloudReferences() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser firebaseAccount = FirebaseAuth.getInstance().getCurrentUser();
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(App.appContext);
        if (firebaseAccount != null) {
            settingsReference = firestore.collection(USERS_COLLECTION_NAME + "/" + firebaseAccount.getUid() + "/" + SETTINGS_COLLECTION_NAME);
            goalsDBReference = firestore.collection(USERS_COLLECTION_NAME + "/" + firebaseAccount.getUid() + "/" + GOALS_DB_COLLECTION_NAME);
            statisticsDBReference = firestore.collection(USERS_COLLECTION_NAME + "/" + firebaseAccount.getUid() + "/" + STATISTICS_DB_COLLECTION_NAME);
        } else if (googleAccount != null) {
            settingsReference = firestore.collection(USERS_COLLECTION_NAME + "/" + googleAccount.getId() + "/" + SETTINGS_COLLECTION_NAME);
            goalsDBReference = firestore.collection(USERS_COLLECTION_NAME + "/" + googleAccount.getId() + "/" + GOALS_DB_COLLECTION_NAME);
            statisticsDBReference = firestore.collection(USERS_COLLECTION_NAME + "/" + googleAccount.getId() + "/" + STATISTICS_DB_COLLECTION_NAME);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel timerChannel = new NotificationChannel(
                CHANNEL_ID,
                "Timer Service Channel",
                NotificationManager.IMPORTANCE_LOW
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(timerChannel);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isBatteryOptimizationActive() {
        String packageName = appContext.getPackageName();
        PowerManager powerManager = (PowerManager) appContext.getSystemService(Context.POWER_SERVICE);
        return !powerManager.isIgnoringBatteryOptimizations(packageName);
    }

    public static boolean isRunning() {
        final ActivityManager activityManager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null) {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(appContext.getPackageName()) && processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                }
            }
        }
        return false;
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void loadUserDataFromCloud(CircularProgressButton progressButton, Runnable onSuccess) {
        Handler handler = new Handler();
        initCloudReferences();
        progressButton.setIndeterminateProgressMode(false);
        appContext.deleteDatabase(GOALS_DATABASE_NAME);
        goalDB = new GoalDBHelper(appContext);
        appContext.deleteDatabase(STATISTICS_DATABASE_NAME);
        statisticsDB = new StatisticsDBHelper(appContext);
        PrefUtil.clearAllSharedPreferences();
        settingsReference.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> settingsPreferences = queryDocumentSnapshots.getDocuments();
                    if (!settingsPreferences.isEmpty()) {
                        int settingPreferenceIndex = 0;
                        for (DocumentSnapshot settingPreference : settingsPreferences) {
                            settingPreferenceIndex++;
                            int settingIndex = 0;
                            Map<String, Object> settingData = settingPreference.getData();
                            for (Map.Entry<String, Object> setting : settingData.entrySet()) {
                                settingIndex++;
                                if (setting.getValue() instanceof Long && !(setting.getKey().equals(POMODORO_LENGTH_IN_MINUTES_PREFERENCE_NAME) ||
                                        setting.getKey().equals(POMODORO_TIME_OUT_LENGTH_IN_MINUTES_PREFERENCE_NAME))) {
                                    PrefUtil.addNewOrEditSharedPreferences(settingPreference.getId(), setting.getKey(), ((Long) setting.getValue()).intValue());
                                } else {
                                    PrefUtil.addNewOrEditSharedPreferences(settingPreference.getId(), setting.getKey(), setting.getValue());
                                }
                                int progress = (int) (((float) settingPreferenceIndex / (float) settingsPreferences.size()) * ((float) settingIndex / (float) settingData.size()) * 20f);
                                progressButton.setProgress(progress);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    progressButton.setProgress(-1);
                    handler.postDelayed(() -> progressButton.setProgress(0), 2000);
                });

        goalsDBReference.get().
                addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> goalsList = queryDocumentSnapshots.getDocuments();
                    if (!goalsList.isEmpty()) {
                        for (DocumentSnapshot goal : goalsList) {
                            Map<String, Object> goalData = goal.getData();
                            goalDB.addGoal(new Goal(
                                    PublicMethods.getValueOrDefault(String.valueOf(goalData.get(COLUMN_GOAL_NAME)), ""),
                                    PublicMethods.getValueOrDefault(String.valueOf(goalData.get(COLUMN_GOAL_DESCRIPTION)), ""),
                                    PublicMethods.getValueOrDefault(String.valueOf(goalData.get(COLUMN_GOAL_PARENT)), ""),
                                    PublicMethods.getValueOrDefault((Long) goalData.get(COLUMN_GOAL_COUNTED_TIME), 0).intValue(),
                                    PublicMethods.getValueOrDefault((Long) goalData.get(COLUMN_GOAL_ESTIMATED_TIME), 0).intValue(),
                                    PublicMethods.getValueOrDefault((Long) goalData.get(COLUMN_GOAL_COUNTED_POMODORO), 0).intValue(),
                                    PublicMethods.getValueOrDefault((Long) goalData.get(COLUMN_GOAL_COUNTED_POMODORO_TIME), 0).intValue(),
                                    PublicMethods.getValueOrDefault((Long) goalData.get(COLUMN_GOAL_DIFFICULTY), 0).intValue(),
                                    PublicMethods.getValueOrDefault((Long) goalData.get(COLUMN_GOAL_EVOLVING), 0).intValue(),
                                    PublicMethods.getValueOrDefault((Long) goalData.get(COLUMN_GOAL_SATISFACTION), 0).intValue(),
                                    PublicMethods.getValueOrDefault((Boolean) goalData.get(COLUMN_GOAL_ACHIEVED), false),
                                    PublicMethods.getValueOrDefault(new ArrayList<>(Arrays.asList(goalData.get(COLUMN_GOAL_TAGS).toString().split(","))), new ArrayList<>()),
                                    PublicMethods.getValueOrDefault(String.valueOf(goalData.get(COLUMN_GOAL_FINISH_DATE)), "")
                            ));
                            int progress = (int) (((float) goalsList.indexOf(goal) / (float) goalsList.size()) * 40f) + 20;
                            progressButton.setProgress(progress);
                        }
                    }
                })
                .addOnFailureListener(e ->

                {
                    progressButton.setProgress(-1);
                    handler.postDelayed(() -> progressButton.setProgress(0), 2000);
                });

        statisticsDBReference.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> statisticsList = queryDocumentSnapshots.getDocuments();
                    if (!statisticsList.isEmpty()) {
                        for (DocumentSnapshot statistics : statisticsList) {
                            Map<String, Object> statisticsData = statistics.getData();
                            statisticsDB.addRecordToDBOnly(
                                    statisticsData.get(COLUMN_DATE).toString(),
                                    ((Long) statisticsData.get(COLUMN_HOUR)).intValue(),
                                    ((Long) statisticsData.get(COLUMN_HOURLY_SECONDS_OF_WORK)).intValue(),
                                    Double.parseDouble(statisticsData.get(COLUMN_HOURLY_NEURONS).toString()),
                                    (Boolean) statisticsData.get(COLUMN_PAYED_MONTHLY_NEURONS)
                            );
                            int progress = (int) (((float) statisticsList.indexOf(statistics) / (float) statisticsList.size()) * 40f) + 60;
                            progressButton.setProgress(progress);
                        }
                    }
                    progressButton.setProgress(100);
                    handler.postDelayed(onSuccess, 2000);
                })
                .addOnFailureListener(e -> {
                    progressButton.setProgress(-1);
                    handler.postDelayed(() -> progressButton.setProgress(0), 2000);
                });
    }

}
