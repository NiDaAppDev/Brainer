package com.nidaappdev.performancemeasurement.customViews.Tutorial;


import static com.nidaappdev.performancemeasurement.util.Constants.*;

import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.nidaappdev.performancemeasurement.databaseObjects.GoalDBHelper;
import com.nidaappdev.performancemeasurement.util.PrefUtil;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.M)
public class TutorialSequence {

    private Context context;

    private GoalDBHelper goalDB;

    private List<TutorialView.Builder> tutorialStations;

    private boolean isRunning = false;

    private String pageName;

    private TutorialSequenceListener listener;

    public TutorialSequence(Context context, String pageName, List<TutorialView.Builder> tutorialStations) {
        this.context = context;
        this.pageName = pageName;
        this.tutorialStations = tutorialStations;

        goalDB = new GoalDBHelper(context);

        initStationsPassing();
    }

    private void initStationsPassing() {
        for (TutorialView.Builder station : tutorialStations) {
            station
                    .setListener(new TutorialViewListener() {

                        @Override
                        public void onShow() {
                        }

                        @Override
                        public void onUserClicked() {
                            station.build().dismissWithoutClick(false);
                            if (PrefUtil.getTutorialStationIndex(pageName) < tutorialStations.size() - 1) {
                                if (listener != null)
                                    listener.onNext(PrefUtil.getTutorialStationIndex(pageName), PrefUtil.getTutorialStationIndex(pageName) + 1);
                                PrefUtil.setTutorialStationIndex(pageName, PrefUtil.getTutorialStationIndex(pageName) + 1);
                                if(!isRunning)
                                    return;
                                Handler handler = new Handler();
                                handler.postDelayed(() -> tutorialStations.get(PrefUtil.getTutorialStationIndex(pageName)).show(false), tutorialStations.get(PrefUtil.getTutorialStationIndex(pageName)).getDelayMillis());
                                return;
                            }
                            PrefUtil.setSkippedTutorial(pageName, false);
                            PrefUtil.setFinishedTutorial(pageName, true);
                            goalDB.removeGoal(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME);
                            goalDB.removeGoal(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME);
                            if (listener != null)
                                listener.onFinish();
                        }

                        @Override
                        public void onSkipClicked() {
                            station.build().dismissWithoutClick(false);
                            PrefUtil.setSkippedTutorial(pageName, true);
                            goalDB.removeGoal(TUTORIAL_FIRST_EXAMPLE_GOAL_NAME);
                            goalDB.removeGoal(TUTORIAL_SECOND_EXAMPLE_GOAL_NAME);
                            if (listener != null)
                                listener.onSkip();
                            //TODO: Maybe first ask the user if he really wants to skip, by using a
                            // dialog, or first dismissing the tutorial and then showing some kind
                            // of toast message saying the tutorial has skipped.
                        }

                        @Override
                        public void onBackClicked() {
                            try {
                                station.build().dismissWithoutClick(true);
                                PrefUtil.setTutorialStationIndex(pageName, PrefUtil.getTutorialStationIndex(pageName) - 1);
                                if (listener != null)
                                    listener.onBack(PrefUtil.getTutorialStationIndex(pageName) + 1, PrefUtil.getTutorialStationIndex(pageName));
                            } finally {
                                Handler handler = new Handler();
                                handler.postDelayed(() -> tutorialStations.get(PrefUtil.getTutorialStationIndex(pageName)).show(false), tutorialStations.get(PrefUtil.getTutorialStationIndex(pageName)).getDelayMillis() + tutorialStations.get(PrefUtil.getTutorialStationIndex(pageName)).getBackOnlyDelayMillisAddition());
                            }
                        }

                        @Override
                        public void onRestartClicked() {
                            if (listener != null)
                                listener.onRestart();
                            station.build().dismissWithoutClick(false);
                            PrefUtil.setTutorialStationIndex(pageName, 0);
                            tutorialStations.get(PrefUtil.getTutorialStationIndex(pageName)).show(false);
                        }
                    });
        }
    }

    public void startTutorial(String pageName) {
        run();
        tutorialStations.get(0).show(false);
        if (listener != null)
            listener.onStart();
    }

    public void resumeTutorial() {
        run();
        if (listener != null)
            listener.onResume(PrefUtil.getTutorialStationIndex(pageName));
        Handler handler = new Handler();
        handler.postDelayed(() -> tutorialStations.get(PrefUtil.getTutorialStationIndex(pageName)).show(false),
                tutorialStations.get(PrefUtil.getTutorialStationIndex(pageName)).getDelayMillis());
    }

    public TutorialSequence setTutorialStations(List<TutorialView.Builder> tutorialStations) {
        this.tutorialStations.clear();
        this.tutorialStations.addAll(tutorialStations);
        initStationsPassing();
        return this;
    }

    private void run() {
        isRunning = true;
    }

    public void pause() {
        isRunning = false;
    }

    public TutorialSequence enableSkipButton(boolean isSkipButtonEnabled) {
        for (TutorialView.Builder station : tutorialStations) {
            if (tutorialStations.indexOf(station) != tutorialStations.size() - 1)
                station.enableSkipButton(isSkipButtonEnabled);
        }
        return this;
    }

    public TutorialSequence enableBackButton(boolean isBackButtonEnabled) {
        for (TutorialView.Builder station : tutorialStations) {
            if (tutorialStations.indexOf(station) != 0) {
                station.enableBackButton(isBackButtonEnabled);
            }
        }
        return this;
    }

    public TutorialSequence enableRestartButton(boolean isRestartButtonEnabled) {
        for (TutorialView.Builder station : tutorialStations) {
            if (tutorialStations.indexOf(station) > 0) {
                station.enableRestartButton(isRestartButtonEnabled);
            }
        }
        return this;
    }

    public TutorialSequence setListener(TutorialSequenceListener listener) {
        this.listener = listener;
        return this;
    }
}
