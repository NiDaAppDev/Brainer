package com.nidaappdev.performancemeasurement.timersClassesAndInterfaces;

import static com.nidaappdev.performancemeasurement.App.CHANNEL_ID;
import static com.nidaappdev.performancemeasurement.App.isRunning;
import static com.nidaappdev.performancemeasurement.util.Constants.ACTION_STOP_SERVICE;
import static com.nidaappdev.performancemeasurement.util.Constants.GO_TO_OPENING_FRAGMENT;
import static com.nidaappdev.performancemeasurement.util.Constants.SAVE_GOAL_PROGRESS_INTENT_ACTION;
import static com.nidaappdev.performancemeasurement.util.Constants.SUGGEST_BREAK_EXTRA_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.TIMER_NOTIFICATION_ID;
import static com.nidaappdev.performancemeasurement.util.Constants.TIMER_NOTIFICATION_SERVICE_CURRENT_GOAL_EXTRA_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.TIMER_NOTIFICATION_SERVICE_TIME_IN_MILLIS_EXTRA_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.TIMER_NOTIFICATION_SERVICE_UPDATE_PLAY_PAUSE_BUTTON_EXTRA_NAME;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.nidaappdev.performancemeasurement.App;
import com.nidaappdev.performancemeasurement.R;
import com.nidaappdev.performancemeasurement.activities.MainActivity;
import com.nidaappdev.performancemeasurement.fragments.OpeningFragment;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.PublicMethods;
import com.nidaappdev.performancemeasurement.util.PrefUtil;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service {

    long startTime = 0L, millis = 0L, seconds = 0L;
    boolean notificationJustStarted, isBatteryOptimizationActive;
    String action, goalName;
    Timer timer = new Timer();
    TimerTask timerTask;
    NotificationCompat.Builder timerNotificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        action = intent.getAction();
        if (action != null && action.equals(ACTION_STOP_SERVICE)) {
            stopForeground(false);
            stopSelf();
        } else {
            startForeground(TIMER_NOTIFICATION_ID, timerNotificationBuilder.setSmallIcon(R.drawable.tomato).setContentTitle("Goal In Progress").build());
            goalName = intent.getStringExtra(TIMER_NOTIFICATION_SERVICE_CURRENT_GOAL_EXTRA_NAME);
            isBatteryOptimizationActive = App.isBatteryOptimizationActive();
            startTime = System.currentTimeMillis();
            notificationJustStarted = true;
            startTimer();
        }

        return START_STICKY;
    }

    private void startTimer() {
        timerTask = new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                millis = System.currentTimeMillis() - startTime;
                seconds = (millis / 1000);
                updateNotification(millis);
                if (seconds * 60 == PrefUtil.getPomodoroLength()) {
                    suggestBreak();
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void updateNotification(Long timeInMillis) {
        isBatteryOptimizationActive = App.isBatteryOptimizationActive();

        clearAllTimerNotificationActionButtons();
        if (isBatteryOptimizationActive) {
            initBatteryOptimizationActiveNotification();
        } else {
            if (notificationJustStarted) {
                initNotificationBase();
            }
            initAndStartRegularTimerNotification(timeInMillis);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.notify(TIMER_NOTIFICATION_ID, timerNotificationBuilder.build());
        }

    }

    private void clearAllTimerNotificationActionButtons() {
        try {
            timerNotificationBuilder.getClass().getDeclaredField("mActions").setAccessible(true);
            timerNotificationBuilder.getClass().getDeclaredField("mActions").set(timerNotificationBuilder, new ArrayList<NotificationCompat.Action>());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private void initBatteryOptimizationActiveNotification() {
        Intent notificationIntent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        String disableBatteryOptimizationText = String.format("Background activity is restricted on this device.\nPlease allow it so we can post an active notification during work sessions.\n\nTo do so, click on the notification to go to\nApp management -> search for %s -> Battery Usage -> enable 'Allow background activity')", getString(R.string.app_name));
        timerNotificationBuilder.
                setContentTitle(getString(R.string.timer_notification_background_restricted_title))
                .setOngoing(true)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.tomato)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentText("Background activity is restricted on this device.")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(disableBatteryOptimizationText));
        notificationJustStarted = true;
    }

    private void initNotificationBase() {
        timerNotificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        timerNotificationBuilder
                .setContentTitle(getString(R.string.timer_notification_background_allowed_title))
                .setOngoing(true)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.tomato)
                .setContentIntent(pendingIntent)
                .setSilent(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationJustStarted = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initAndStartRegularTimerNotification(Long timeInMillis) {
        String enableScreenOverlayText = "",
                actionButtonTitle;
        PendingIntent pendingIntent;
        if (!Settings.canDrawOverlays(this)) {
            /** Initialize the notification in case "draw over other apps" setting is disabled */
            Intent notificationIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
            actionButtonTitle = "Enable";
            enableScreenOverlayText = String.format("To be able stopping the timer from the notification:\nClick Enable -> Search for %s -> Allow Display over other apps", getString(R.string.app_name));
        } else {
            /** Initialize the notification in case "draw over other apps" setting is enabled */
            Intent stopActionIntent = new Intent(this, TimerService.class);
            stopActionIntent.setAction(ACTION_STOP_SERVICE);
            pendingIntent = PendingIntent.getService(this, 1, stopActionIntent, PendingIntent.FLAG_IMMUTABLE);
            actionButtonTitle = "Stop";
        }

        timerNotificationBuilder
                .setContentText(String.format("%s is in progress\nThis session length: %s", goalName, PublicMethods.formatStopWatchTime(timeInMillis)))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(enableScreenOverlayText))
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setColorized(true)
                .addAction(R.drawable.stop, actionButtonTitle, pendingIntent);
    }

    private void suggestBreak() {
        Intent suggestBreak = new Intent(SAVE_GOAL_PROGRESS_INTENT_ACTION);
        if (!isRunning()) {
            Intent openApp = new Intent(App.appContext, MainActivity.class);
            openApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            openApp.setAction(GO_TO_OPENING_FRAGMENT);
            startActivity(openApp);
        }
        suggestBreak.putExtra(SUGGEST_BREAK_EXTRA_NAME, true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(suggestBreak);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void killService() {
        timer.cancel();
        PrefUtil.setTimerState(OpeningFragment.TimerState.Stopped);

        Intent saveGoalProgress = new Intent(SAVE_GOAL_PROGRESS_INTENT_ACTION);
        if (!isRunning()) {
            Intent openApp = new Intent(App.appContext, MainActivity.class);
            openApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            openApp.setAction(GO_TO_OPENING_FRAGMENT);
            startActivity(openApp);
            saveGoalProgress.putExtra(TIMER_NOTIFICATION_SERVICE_UPDATE_PLAY_PAUSE_BUTTON_EXTRA_NAME, true);
        }
        saveGoalProgress.putExtra(TIMER_NOTIFICATION_SERVICE_TIME_IN_MILLIS_EXTRA_NAME, millis);
        LocalBroadcastManager.getInstance(this).sendBroadcast(saveGoalProgress);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDestroy() {
        killService();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
