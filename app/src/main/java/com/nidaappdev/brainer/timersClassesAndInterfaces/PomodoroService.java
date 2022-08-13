package com.nidaappdev.brainer.timersClassesAndInterfaces;

import static com.nidaappdev.brainer.App.CHANNEL_ID;
import static com.nidaappdev.brainer.App.isRunning;
import static com.nidaappdev.brainer.util.Constants.GO_TO_OPENING_FRAGMENT;
import static com.nidaappdev.brainer.util.Constants.ACTION_STOP_SERVICE;
import static com.nidaappdev.brainer.util.Constants.POMODORO_SERVICE_POMODORO_FINISHED_EXTRA_NAME;
import static com.nidaappdev.brainer.util.Constants.POMODORO_SERVICE_STOP_FOR_REAL_EXTRA_NAME;
import static com.nidaappdev.brainer.util.Constants.SAVE_GOAL_PROGRESS_INTENT_ACTION;
import static com.nidaappdev.brainer.util.Constants.TIMER_NOTIFICATION_ID;
import static com.nidaappdev.brainer.util.Constants.TIMER_NOTIFICATION_SERVICE_CURRENT_GOAL_EXTRA_NAME;
import static com.nidaappdev.brainer.util.Constants.TIMER_NOTIFICATION_SERVICE_UPDATE_PLAY_PAUSE_BUTTON_EXTRA_NAME;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.nidaappdev.brainer.App;
import com.nidaappdev.brainer.R;
import com.nidaappdev.brainer.activities.MainActivity;
import com.nidaappdev.brainer.fragments.OpeningFragment;
import com.nidaappdev.brainer.publicClassesAndInterfaces.PublicMethods;
import com.nidaappdev.brainer.util.PrefUtil;

import java.util.ArrayList;

public class PomodoroService extends Service {

    private long millisRemaining;
    private CountDownTimer timer;
    private String action, goalName;
    boolean notificationJustStarted, isBatteryOptimizationActive, stopForReal;
    NotificationCompat.Builder pomodoroNotificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        action = intent.getAction();
        stopForReal = intent.getBooleanExtra(POMODORO_SERVICE_STOP_FOR_REAL_EXTRA_NAME, false);
        if (action != null && action.equals(ACTION_STOP_SERVICE)) {
            if(stopForReal) {
                stopForeground(false);
                stopSelf();
            } else {
                showLosePomodoroDialog();
            }
        } else {
            startForeground(TIMER_NOTIFICATION_ID, pomodoroNotificationBuilder.setSmallIcon(R.drawable.tomato).setContentTitle("Goal In Progress").build());
            goalName = intent.getStringExtra(TIMER_NOTIFICATION_SERVICE_CURRENT_GOAL_EXTRA_NAME);
            isBatteryOptimizationActive = App.isBatteryOptimizationActive();
            notificationJustStarted = true;
            startPomodoro();
        }
        return START_STICKY;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startPomodoro() {
        millisRemaining = PrefUtil.getPomodoroLength() * 60000;
        PrefUtil.setTimerState(OpeningFragment.TimerState.Running);
        timer = new CountDownTimer(millisRemaining, 1000) {
            @Override
            public void onTick(long l) {
                millisRemaining = l;
                updateNotification();
            }

            @Override
            public void onFinish() {
                finishPomodoro();
            }
        }.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void updateNotification() {
        isBatteryOptimizationActive = App.isBatteryOptimizationActive();

        clearAllTimerNotificationActionButtons();

        if (isBatteryOptimizationActive) {
            initBatteryOptimizationActiveNotification();
        } else {
            if (notificationJustStarted) {
                initNotificationBase();
            }
            initAndStartPomodoroTimerNotification();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.notify(TIMER_NOTIFICATION_ID, pomodoroNotificationBuilder.build());
        }

    }

    private void clearAllTimerNotificationActionButtons() {
        try {
            pomodoroNotificationBuilder.getClass().getDeclaredField("mActions").setAccessible(true);
            pomodoroNotificationBuilder.getClass().getDeclaredField("mActions").set(pomodoroNotificationBuilder, new ArrayList<NotificationCompat.Action>());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private void initBatteryOptimizationActiveNotification() {
        Intent notificationIntent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        String disableBatteryOptimizationText = String.format("Background activity is restricted on this device.\nPlease allow it so we can post an active notification during work sessions.\n\nTo do so, click on the notification to go to\nApp management -> search for %s -> Battery Usage -> enable 'Allow background activity')", getString(R.string.app_name));
        pomodoroNotificationBuilder.
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
        pomodoroNotificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        pomodoroNotificationBuilder
                .setContentTitle(getString(R.string.timer_notification_background_allowed_title))
                .setOngoing(true)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.tomato)
                .setContentIntent(pendingIntent)
                .setNotificationSilent()
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationJustStarted = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initAndStartPomodoroTimerNotification() {
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
            Intent stopActionIntent = new Intent(this, PomodoroService.class);
            stopActionIntent.setAction(ACTION_STOP_SERVICE);
            stopActionIntent.putExtra(POMODORO_SERVICE_STOP_FOR_REAL_EXTRA_NAME, false);
            pendingIntent = PendingIntent.getService(this, 1, stopActionIntent, PendingIntent.FLAG_IMMUTABLE);
            actionButtonTitle = "Stop";
        }


        pomodoroNotificationBuilder
                .setContentText(String.format("%s is in progress\nThis session remains: %s until finished", goalName, PublicMethods.formatStopWatchTime(millisRemaining)))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(enableScreenOverlayText))
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setColorized(true)
                .addAction(R.drawable.stop, actionButtonTitle, pendingIntent);
    }

    private void showLosePomodoroDialog(){
        Intent showLosePomodoroDialog = new Intent(SAVE_GOAL_PROGRESS_INTENT_ACTION);
        if (!isRunning()) {
            Intent openApp = new Intent(App.appContext, MainActivity.class);
            openApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            openApp.setAction(GO_TO_OPENING_FRAGMENT);
            startActivity(openApp);
            showLosePomodoroDialog.putExtra(TIMER_NOTIFICATION_SERVICE_UPDATE_PLAY_PAUSE_BUTTON_EXTRA_NAME, true);
        }
        showLosePomodoroDialog.putExtra(POMODORO_SERVICE_STOP_FOR_REAL_EXTRA_NAME, false);
        showLosePomodoroDialog.putExtra(POMODORO_SERVICE_POMODORO_FINISHED_EXTRA_NAME, false);
        LocalBroadcastManager.getInstance(this).sendBroadcast(showLosePomodoroDialog);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void finishPomodoro(){
        PrefUtil.setTimerState(OpeningFragment.TimerState.Stopped);
        Intent showFinishPomodoroDialog = new Intent(SAVE_GOAL_PROGRESS_INTENT_ACTION);
        if (!isRunning()) {
            Intent openApp = new Intent(App.appContext, MainActivity.class);
            openApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            openApp.setAction(GO_TO_OPENING_FRAGMENT);
            startActivity(openApp);
        }
        showFinishPomodoroDialog.putExtra(TIMER_NOTIFICATION_SERVICE_UPDATE_PLAY_PAUSE_BUTTON_EXTRA_NAME, true);
        showFinishPomodoroDialog.putExtra(POMODORO_SERVICE_POMODORO_FINISHED_EXTRA_NAME, true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(showFinishPomodoroDialog);
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
        saveGoalProgress.putExtra(POMODORO_SERVICE_POMODORO_FINISHED_EXTRA_NAME, false);
        saveGoalProgress.putExtra(POMODORO_SERVICE_STOP_FOR_REAL_EXTRA_NAME, true);
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
