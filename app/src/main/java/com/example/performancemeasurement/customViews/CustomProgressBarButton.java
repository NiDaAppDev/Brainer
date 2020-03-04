package com.example.performancemeasurement.customViews;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import com.example.performancemeasurement.R;

public class CustomProgressBarButton extends LinearLayout {

    private CustomProgressBar mCustomProgressBar;
    private Drawable mShadowDrawable;//阴影
    private final int DEFAULT_COLOR = Color.GRAY;
    private TimeInterpolator mInterpolator;
    private ValueAnimator mLayoutDownAnimator;
    private ValueAnimator mLayoutUpAnimator;
    private float mDensity;
    private float mCenterX;
    private float mCenterY;
    private int mLayoutWidth;
    private int mLayoutHeight;
    private float mCanvasScale = 1f;
    private final String PROPERTY_CANVAS_SCALE = "canvasScale";
    private final long ANIM_DOWN_DURATION = 500;
    private final long ANIM_UP_DURATION = 500;
    private float mTargetScale = 1.0f;
    private float mMinScale = 0.95f;

    public CustomProgressBarButton(Context context) {
        super(context);
        mCustomProgressBar = new CustomProgressBar(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mCustomProgressBar.setLayoutParams(lp);
        this.addView(mCustomProgressBar);
        init(context, null);
    }

    public CustomProgressBarButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        mCustomProgressBar = new CustomProgressBar(context, attrs);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mCustomProgressBar.setLayoutParams(lp);
        this.addView(mCustomProgressBar);
    }


    private void init(Context context, AttributeSet attributeSet) {
        mInterpolator = new PathInterpolator(0.33f, 0f, 0.33f, 1);
        mShadowDrawable = getResources().getDrawable(R.drawable.gradient_layout_shadow);
        mDensity = getResources().getDisplayMetrics().density;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.scale(mCanvasScale, mCanvasScale, mCenterX, mCenterY);
        Log.w("tan", mCanvasScale + "");
        //drawShadow(canvas);
        super.dispatchDraw(canvas);
        canvas.restore();
    }


    private void drawShadow(Canvas canvas) {
        if (mShadowDrawable == null) {
            return;
        }
        canvas.save();
        float scale = 1 - (1 - mCanvasScale) * 6;
        canvas.scale(scale, scale, mCenterX, mCenterY);
        canvas.translate(0, (mCanvasScale - 1) * mLayoutHeight * 6 + mLayoutHeight * 0.4f + mDensity);
        mShadowDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (!isEnabled()) {
            return false;
        }
        if (!isClickable()) {
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                handleActionDown(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                handleActionUp(ev);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.w("tan", "onsize change");
        mLayoutWidth = w;
        mLayoutHeight = h;
        mCenterX = mLayoutWidth / 2;
        mCenterY = mLayoutHeight / 2;
        if (mShadowDrawable == null) {
            return;
        }
        mShadowDrawable.setColorFilter(DEFAULT_COLOR, PorterDuff.Mode.SRC_IN);
        mShadowDrawable.setBounds(0, 0, mLayoutWidth, mLayoutHeight);

        if (getParent() instanceof ViewGroup) {
            ((ViewGroup) getParent()).setClipChildren(false);
        }
    }


    private void handleActionDown(MotionEvent ev) {
        setupLayoutDownAnimator();
        mLayoutDownAnimator.start();
    }


    private void handleActionUp(MotionEvent ev) {
        setupLayoutUpAnimator();
        mLayoutUpAnimator.start();
    }


    private void setupLayoutDownAnimator() {

        mLayoutDownAnimator = ValueAnimator.ofFloat(1f, 0.95f);
        mLayoutDownAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCanvasScale = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mLayoutDownAnimator.setInterpolator(mInterpolator);
        mLayoutDownAnimator.setDuration(ANIM_DOWN_DURATION);
    }


    private void setupLayoutUpAnimator() {

        mLayoutUpAnimator = ValueAnimator.ofFloat(0.95f, 1f);
        mLayoutUpAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCanvasScale = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mLayoutUpAnimator.setInterpolator(mInterpolator);
        mLayoutUpAnimator.setDuration(ANIM_UP_DURATION);

    }


    @Override
    public void invalidate() {
        super.invalidate();
        mCustomProgressBar.invalidate();
    }



    public void setProgressText(String text) {
        mCustomProgressBar.setText(text);
    }

    public float getProgress() {
        return mCustomProgressBar.getProgress();
    }

    public void setProgress(float progress) {
        mCustomProgressBar.setProgress(progress);

    }


    public void removeAllAnim() {
        mCustomProgressBar.removeAllAnim();
    }

    public float getButtonRadius() {
        return mCustomProgressBar.getRadius();
    }

    public void setButtonRadius(float buttonRadius) {
        mCustomProgressBar.setRadius(buttonRadius);
    }


    public void enableDefaultPress(boolean enable) {
        mCustomProgressBar.enableDefaultPress(enable);
    }

    public void enableDefaultGradient(boolean enable) {
        mCustomProgressBar.enableDefaultGradient(enable);
    }

    public void setTextSize(float size) {
        mCustomProgressBar.setTextSize(size);
    }

    public float getTextSize() {
        return mCustomProgressBar.getTextSize();
    }

    public CustomProgressBar setCustomerController(ButtonController customerController) {
        return mCustomProgressBar.setCustomerController(customerController);
    }
}