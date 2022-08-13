package com.nidaappdev.brainer.customViews.Tutorial;

public interface TutorialSequenceListener {
    void onStart();
    void onResume(int index);
    void onNext(int fromIndex, int toIndex);
    void onBack(int fromIndex, int toIndex);
    void onRestart();
    void onSkip();
    void onFinish();
}
