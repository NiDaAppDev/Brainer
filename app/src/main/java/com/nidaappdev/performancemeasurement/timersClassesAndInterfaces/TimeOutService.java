package com.nidaappdev.performancemeasurement.timersClassesAndInterfaces;

import static com.nidaappdev.performancemeasurement.App.CHANNEL_ID;
import static com.nidaappdev.performancemeasurement.App.isRunning;
import static com.nidaappdev.performancemeasurement.util.Constants.GO_TO_OPENING_FRAGMENT;
import static com.nidaappdev.performancemeasurement.util.Constants.POMODORO_TIME_OUT_SERVICE_TIME_OUT_FINISHED_EXTRA_NAME;
import static com.nidaappdev.performancemeasurement.util.Constants.SAVE_GOAL_PROGRESS_INTENT_ACTION;
import static com.nidaappdev.performancemeasurement.util.Constants.TIMER_NOTIFICATION_ID;
import static com.nidaappdev.performancemeasurement.util.Constants.TIMER_NOTIFICATION_SERVICE_UPDATE_PLAY_PAUSE_BUTTON_EXTRA_NAME;

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

import com.nidaappdev.performancemeasurement.App;
import com.nidaappdev.performancemeasurement.R;
import com.nidaappdev.performancemeasurement.activities.MainActivity;
import com.nidaappdev.performancemeasurement.fragments.OpeningFragment;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.PublicMethods;
import com.nidaappdev.performancemeasurement.util.PrefUtil;

import java.util.ArrayList;

public class TimeOutService extends Service {

    private long millisRemaining;
    private CountDownTimer timer;
    boolean notificationJustStarted, isBatteryOptimizationActive;
    NotificationCompat.Builder timeOutNotificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(TIMER_NOTIFICATION_ID, timeOutNotificationBuilder.setSmallIcon(R.drawable.tomato).setContentTitle("Goal In Progress").build());
        isBatteryOptimizationActive = App.isBatteryOptimizationActive();
        notificationJustStarted = true;
        startTimeOut();
        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startTimeOut(){
        millisRemaining = PrefUtil.getPomodoroTimeOutLength() * 60000;
        PrefUtil.setTimerState(OpeningFragment.TimerState.Running);
        if(PrefUtil.getTimeMethod() == PrefUtil.TimeMethod.Pomodoro) {
            PrefUtil.setTimeMethod(PrefUtil.TimeMethod.TimeOut);
        }else if(PrefUtil.getTimeMethod() == PrefUtil.TimeMethod.Timer){
            PrefUtil.setTimeMethod(PrefUtil.TimeMethod.TimerTimeOut);
        }
        timer = new CountDownTimer(millisRemaining, 1000) {
            @Override
            public void onTick(long l) {
                millisRemaining = l;
                updateNotification();
            }

            @Override
            public void onFinish() {
                stopForeground(false);
                stopSelf();
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
            initAndStartTimeOutTimerNotification();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.notify(TIMER_NOTIFICATION_ID, timeOutNotificationBuilder.build());
        }

    }

    private void clearAllTimerNotificationActionButtons() {
        try {
            timeOutNotificationBuilder.getClass().getDeclaredField("mActions").setAccessible(true);
            timeOutNotificationBuilder.getClass().getDeclaredField("mActions").set(timeOutNotificationBuilder, new ArrayList<NotificationCompat.Action>());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private void initBatteryOptimizationActiveNotification() {
        Intent notificationIntent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        String disableBatteryOptimizationText = String.format("Background activity is restricted on this device.\nPlease allow it so we can post an active notification during work sessions.\n\nTo do so, click on the notification to go to\nApp management -> search for %s -> Battery Usage -> enable 'Allow background activity')", getString(R.string.app_name));
        timeOutNotificationBuilder.
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
        timeOutNotificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        timeOutNotificationBuilder
                .setContentTitle(getString(R.string.time_out_notification_background_allowed_title))
                .setOngoing(true)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.tomato)
                .setContentIntent(pendingIntent)
                .setNotificationSilent()
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationJustStarted = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initAndStartTimeOutTimerNotification() {
        timeOutNotificationBuilder
                .setContentText(PublicMethods.formatStopWatchTime(millisRemaining) + " until going back to work\nRest well!")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(String.format("Time to take a refreshing break! Take some air, let your mind rest, and prepare for the next session.\nYou have: %s until going back to work.\nRest well!", PublicMethods.formatStopWatchTime(millisRemaining))));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void timeOutOver(){
        PrefUtil.setTimerState(OpeningFragment.TimerState.Stopped);
        Intent showFinishTimeOutDialog = new Intent(SAVE_GOAL_PROGRESS_INTENT_ACTION);
        if (!isRunning()) {
            Intent openApp = new Intent(App.appContext, MainActivity.class);
            openApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            openApp.setAction(GO_TO_OPENING_FRAGMENT);
            startActivity(openApp);
        }
        showFinishTimeOutDialog.putExtra(TIMER_NOTIFICATION_SERVICE_UPDATE_PLAY_PAUSE_BUTTON_EXTRA_NAME, true);
        showFinishTimeOutDialog.putExtra(POMODORO_TIME_OUT_SERVICE_TIME_OUT_FINISHED_EXTRA_NAME, true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(showFinishTimeOutDialog);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void killService() {
        timer.cancel();
        timeOutOver();
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
