package com.nidaappdev.performancemeasurement.customViews.Tutorial;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nidaappdev.performancemeasurement.R;
import com.nidaappdev.performancemeasurement.customViews.Tutorial.Animations.AnimationFactory;
import com.nidaappdev.performancemeasurement.customViews.Tutorial.Focus.Focus;
import com.nidaappdev.performancemeasurement.customViews.Tutorial.Focus.FocusGravity;
import com.nidaappdev.performancemeasurement.customViews.Tutorial.Shape.Circle;
import com.nidaappdev.performancemeasurement.customViews.Tutorial.Shape.Rect;
import com.nidaappdev.performancemeasurement.customViews.Tutorial.Shape.Shape;
import com.nidaappdev.performancemeasurement.customViews.Tutorial.Shape.ShapeType;
import com.nidaappdev.performancemeasurement.util.Constants;

public class TutorialView extends RelativeLayout {

    private int maskColor;

    /**
     * TutorialView will start
     * showing after delayMillis seconds
     * passed
     */
    private long delayMillis;

    /**
     * If targetView animates, this
     * is its animation duration
     */
    private long backOnlyDelayMillisAddition;

    /**
     * We don't draw TutorialView
     * until isReady field set to true
     */
    private boolean isReady;

    /**
     * Show TutorialView
     * with fade in animation if
     * this is enabled.
     */
    private boolean isFadeInAnimationEnabled;

    /**
     * Dismiss TutorialView
     * with fade out animation if
     * this is enabled.
     */
    private boolean isFadeOutAnimationEnabled;

    /**
     * Animation duration
     */
    private long fadeAnimationDuration;

    /**
     * targetShape focus on target
     * and clear circle to focus
     */
    private Shape targetShape;

    /**
     * Focus Type
     */
    private Focus focusType;

    /**
     * FocusGravity type
     */
    private FocusGravity focusGravity;

    /**
     * Target View
     */
    private Target targetView;

    /**
     * Eraser
     */
    private Paint eraser;

    /**
     * Handler will be used to
     * delay TutorialView
     */
    private Handler handler;

    /**
     * All views will be drawn to
     * this bitmap and canvas then
     * bitmap will be drawn to canvas
     */
    private Bitmap bitmap;
    private Canvas canvas;

    /**
     * Circle padding
     */
    private int padding;

    /**
     * Layout width/height
     */
    private int width;
    private int height;

    /**
     * Dismiss on touch any position
     */
    private boolean dismissOnTouch;

    /**
     * Info dialog view
     */
    private View infoView;

    /**
     * Info Dialog Text
     */
    private TextView textViewInfo;

    /**
     * Info dialog text color
     */
    private int colorTextViewInfo;

    /**
     * Info dialog will be shown
     * If this value true
     */
    private boolean isInfoEnabled;

    /**
     * Dot view will appear center of
     * cleared target area
     */
    private View dotView;

    /**
     * Skip button will appear where the
     * user choose it to, and will skip the tutorial
     */
    private Button skipButton;

    /**
     * Skip button location determines where the
     * skip button will appear
     */
    private ButtonLocation skipButtonLocation;

    /**
     * Skip button will be shown if
     * this is true
     */
    private boolean isSkipButtonEnabled;

    /**
     * Back button will appear where the
     * user choose it to, and will go back one
     * tutorial station
     */
    private Button backButton;

    /**
     * Back button location determines where the
     * skip button will appear
     */
    private ButtonLocation backButtonLocation;

    /**
     * Back button will be shown if
     * this is true
     */
    private boolean isBackButtonEnabled;

    /**
     * Restart button will appear where the
     * user choose it to, and will restart the tutorial
     */
    private Button restartButton;

    /**
     * Restart button location determines where the
     * restart button will appear
     */
    private ButtonLocation restartButtonLocation;

    /**
     * Restart Button will be shown if
     * this is true
     */
    private boolean isRestartButtonEnabled;

    /**
     * Dot View will be shown if
     * this is true
     */
    private boolean isDotViewEnabled;

    /**
     * Info Dialog Icon
     */
    private ImageView imageViewIcon;

    /**
     * Image View will be shown if
     * this is true
     */
    private boolean isImageViewEnabled;

    /**
     * When layout completed, we set this true
     * Otherwise onGlobalLayoutListener stuck on loop.
     */
    private boolean isLayoutCompleted;

    /**
     * When layout dismissing or showing, set to true;
     */
    private boolean isLayoutInProgress;

    /**
     * Notify user when TutorialView is dismissed
     */
    private TutorialViewListener tutorialListener;

    /**
     * Perform click operation to target
     * if this is true
     */
    private boolean isPerformClick;

    /**
     * Perform click operation to target
     * on back clicked if this is true
     */
    private boolean performClickAlsoInReverse;

    /**
     * Perform long click operation to target
     * if this is true
     */
    private boolean isPerformLongClick;

    /**
     * Shape of target
     */
    private ShapeType shapeType;

    /**
     * Use custom shape
     */
    private boolean usesCustomShape = false;

    public TutorialView(Context context) {
        super(context);
        init(context);
    }

    public TutorialView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TutorialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TutorialView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        setVisibility(INVISIBLE);

        /**
         * set default values
         */
        maskColor = Constants.DEFAULT_MASK_COLOR;
        delayMillis = Constants.DEFAULT_DELAY_MILLIS;
        backOnlyDelayMillisAddition = Constants.DEFAULT_DELAY_MILLIS;
        fadeAnimationDuration = Constants.DEFAULT_FADE_DURATION;
        padding = Constants.DEFAULT_TARGET_PADDING;
        colorTextViewInfo = getResources().getColor(R.color.brain1);
        focusType = Focus.ALL;
        focusGravity = FocusGravity.CENTER;
        shapeType = ShapeType.CIRCLE;
        skipButtonLocation = ButtonLocation.BOTTOM_RIGHT;
        backButtonLocation = ButtonLocation.BOTTOM_LEFT;
        restartButtonLocation = ButtonLocation.BOTTOM_CENTER;
        isReady = false;
        isFadeInAnimationEnabled = true;
        isFadeOutAnimationEnabled = true;
        dismissOnTouch = false;
        isLayoutCompleted = false;
        isInfoEnabled = false;
        isDotViewEnabled = false;
        isPerformClick = false;
        isPerformLongClick = false;
        performClickAlsoInReverse = false;
        isImageViewEnabled = true;
        isSkipButtonEnabled = false;
        isBackButtonEnabled = false;
        isRestartButtonEnabled = false;

        /**
         * initialize objects
         */
        handler = new Handler();

        eraser = new Paint();
        eraser.setColor(0xFFFFFFFF);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        eraser.setFlags(Paint.ANTI_ALIAS_FLAG);

        View layoutInfo = LayoutInflater.from(getContext()).inflate(R.layout.material_tutorial_card, null);

        infoView = layoutInfo.findViewById(R.id.info_layout);
        textViewInfo = (TextView) layoutInfo.findViewById(R.id.textview_info);
        textViewInfo.setTextColor(colorTextViewInfo);
        imageViewIcon = (ImageView) layoutInfo.findViewById(R.id.imageview_icon);

        dotView = LayoutInflater.from(getContext()).inflate(R.layout.tutorial_dotview, null);
        dotView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

        skipButton = (Button) LayoutInflater.from(getContext()).inflate(R.layout.tutorial_skip_button, null);
        skipButton.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        skipButton.setOnClickListener(view -> tutorialListener.onSkipClicked());

        backButton = (Button) LayoutInflater.from(getContext()).inflate(R.layout.tutorial_back_button, null);
        backButton.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        backButton.setOnClickListener(view -> {
            tutorialListener.onBackClicked();
            if (performClickAlsoInReverse)
                targetView.getView().performClick();
        });

        restartButton = (Button) LayoutInflater.from(getContext()).inflate(R.layout.tutorial_restart_button, null);
        restartButton.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        restartButton.setOnClickListener(view -> tutorialListener.onRestartClicked());

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                targetShape.reCalculateAll();
                if (targetShape != null && targetShape.getPoint().y != 0 && !isLayoutCompleted) {
                    if (isInfoEnabled)
                        setInfoLayout();
                    if (isDotViewEnabled)
                        setDotViewLayout();
                    if (isSkipButtonEnabled)
                        setSkipButtonLayout();
                    if (isBackButtonEnabled)
                        setBackButtonLayout();
                    if (isRestartButtonEnabled)
                        setRestartButtonLayout();
                    removeOnGlobalLayoutListener(TutorialView.this, this);
                }
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isReady) return;

        if (bitmap == null || canvas == null) {
            if (bitmap != null) bitmap.recycle();

            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            this.canvas = new Canvas(bitmap);
        }

        /**
         * Draw mask
         */
        if (this.canvas != null) {
            this.canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        } else {
            this.canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.DARKEN);
        }
        this.canvas.drawColor(maskColor);

        /**
         * Clear focus area
         */
        targetShape.draw(this.canvas, eraser, padding);

        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    /**
     * Perform click operation when user
     * touches on target circle.
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isLayoutInProgress) {
            float xT = event.getX();
            float yT = event.getY();

            boolean isTouchOnFocus = targetShape.isTouchOnFocus(xT, yT);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    if (isTouchOnFocus && isPerformClick || isTouchOnFocus && isPerformLongClick) {
                        targetView.getView().setPressed(true);
                        targetView.getView().invalidate();
                    }

                    return true;
                case MotionEvent.ACTION_UP:

                    if (isTouchOnFocus || dismissOnTouch)
                        dismiss();

                    if (isTouchOnFocus && isPerformClick) {
                        targetView.getView().performClick();
                        targetView.getView().setPressed(true);
                        targetView.getView().invalidate();
                        targetView.getView().setPressed(false);
                        targetView.getView().invalidate();
                    } else if (isTouchOnFocus && isPerformLongClick) {
                        targetView.getView().performLongClick();
                        targetView.getView().setPressed(true);
                        targetView.getView().invalidate();
                        targetView.getView().setPressed(false);
                        targetView.getView().invalidate();
                    }

                    return true;
                default:
                    break;
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     * Shows tutorial view with fade in
     * animation
     *
     * @param activity
     */
    private void show(Activity activity, boolean inReverse) {
        isLayoutInProgress = true;
        if (getParent() == null) {
            ((ViewGroup) activity.getWindow().getDecorView()).addView(this);
        }

        setReady(true);

        handler.postDelayed(() -> {
            AnimationFactory.animateFadeIn(TutorialView.this,
                    inReverse ?
                            (isFadeOutAnimationEnabled ? fadeAnimationDuration : 0)
                            :
                            (isFadeInAnimationEnabled ? fadeAnimationDuration : 0),
                    () -> {
                setVisibility(VISIBLE);
                isLayoutInProgress = false;
            });
        }, Constants.DEFAULT_DELAY_MILLIS);
    }

    /**
     * Dismiss Tutorial View
     */
    public void dismiss() {
        isLayoutInProgress = true;
        AnimationFactory.animateFadeOut(this, isFadeOutAnimationEnabled ? fadeAnimationDuration : 0, () -> {
            setVisibility(GONE);
            removeTutorialView();

            if (tutorialListener != null)
                tutorialListener.onUserClicked();
            isLayoutInProgress = false;
        });
    }

    /**
     * Dismiss Tutorial View without calling onClick method
     */
    public void dismissWithoutClick(boolean inReverse) {
        isLayoutInProgress = true;
        AnimationFactory.animateFadeOut(this,
                inReverse ?
                        (isFadeInAnimationEnabled ? fadeAnimationDuration : 0)
                        : (isFadeOutAnimationEnabled ? fadeAnimationDuration : 0),
                () -> {
                    setVisibility(GONE);
                    removeTutorialView();
                    isLayoutInProgress = false;
                });
    }

    private void removeTutorialView() {
        if (getParent() != null)
            ((ViewGroup) getParent()).removeView(this);
    }

    /**
     * locate info card view above/below the
     * circle. If circle's Y coordiante is bigger than
     * Y coordinate of root view, then locate cardview
     * above the circle. Otherwise locate below.
     */
    private void setInfoLayout() {
        handler.post(() -> {
            isLayoutCompleted = true;

            if (infoView.getParent() != null)
                ((ViewGroup) infoView.getParent()).removeView(infoView);

            LayoutParams infoDialogParams = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT);

            if (targetShape.getPoint().y < height / 2) {
                ((RelativeLayout) infoView).setGravity(Gravity.TOP);
                infoDialogParams.setMargins(
                        0,
                        targetShape.getPoint().y + targetShape.getHeight() / 2,
                        0,
                        0);
            } else {
                ((RelativeLayout) infoView).setGravity(Gravity.BOTTOM);
                infoDialogParams.setMargins(
                        0,
                        0,
                        0,
                        height - (targetShape.getPoint().y + targetShape.getHeight() / 2) + 2 * targetShape.getHeight() / 2);
            }

            infoView.setLayoutParams(infoDialogParams);
            infoView.postInvalidate();

            addView(infoView);

            if (!isImageViewEnabled) {
                imageViewIcon.setVisibility(GONE);
            }

            infoView.setVisibility(VISIBLE);
        });
    }

    private void updateInfoLayout() {
        handler.post(() -> {
            isLayoutCompleted = true;

            if (infoView.getParent() != null)
                ((ViewGroup) infoView.getParent()).removeView(infoView);

            LayoutParams infoDialogParams = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT);

            if (targetShape.getPoint().y < height / 2) {
                ((RelativeLayout) infoView).setGravity(Gravity.TOP);
                infoDialogParams.setMargins(
                        0,
                        targetShape.getPoint().y + targetShape.getHeight() / 2,
                        0,
                        0);
            } else {
                ((RelativeLayout) infoView).setGravity(Gravity.BOTTOM);
                infoDialogParams.setMargins(
                        0,
                        0,
                        0,
                        height - (targetShape.getPoint().y + targetShape.getHeight() / 2) + 2 * targetShape.getHeight() / 2);
            }

            infoView.setLayoutParams(infoDialogParams);
            infoView.postInvalidate();

            addView(infoView);

            if(isInfoEnabled) {
                if (!isImageViewEnabled) {
                    imageViewIcon.setVisibility(GONE);
                }

                infoView.setVisibility(VISIBLE);
            }
        });
    }

    private void setDotViewLayout() {
        handler.post(() -> {

            if (dotView.getParent() != null)
                ((ViewGroup) dotView.getParent()).removeView(dotView);

            LayoutParams dotViewLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dotViewLayoutParams.height = TutorialUtils.dpToPx(Constants.DEFAULT_DOT_SIZE);
            dotViewLayoutParams.width = TutorialUtils.dpToPx(Constants.DEFAULT_DOT_SIZE);
            dotViewLayoutParams.setMargins(
                    targetShape.getPoint().x - (dotViewLayoutParams.width / 2),
                    targetShape.getPoint().y - (dotViewLayoutParams.height / 2),
                    0,
                    0);
            dotView.setLayoutParams(dotViewLayoutParams);
            dotView.postInvalidate();
            addView(dotView);

            dotView.setVisibility(VISIBLE);
            AnimationFactory.performAnimation(dotView);
        });
    }

    private void updateDotViewLayout() {
        handler.post(() -> {

            if (dotView.getParent() != null)
                ((ViewGroup) dotView.getParent()).removeView(dotView);

            LayoutParams dotViewLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dotViewLayoutParams.height = TutorialUtils.dpToPx(Constants.DEFAULT_DOT_SIZE);
            dotViewLayoutParams.width = TutorialUtils.dpToPx(Constants.DEFAULT_DOT_SIZE);
            dotViewLayoutParams.setMargins(
                    targetShape.getPoint().x - (dotViewLayoutParams.width / 2),
                    targetShape.getPoint().y - (dotViewLayoutParams.height / 2),
                    0,
                    0);
            dotView.setLayoutParams(dotViewLayoutParams);
            dotView.postInvalidate();
            if (isDotViewEnabled) {
                addView(dotView);

                dotView.setVisibility(VISIBLE);
                AnimationFactory.performAnimation(dotView);
            }
        });
    }

    public void setSkipButtonLayout() {
        handler.post(() -> {

            if (skipButton.getParent() != null)
                ((ViewGroup) skipButton.getParent()).removeView(skipButton);

            LayoutParams skipButtonLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            float dp = getResources().getDisplayMetrics().scaledDensity;
            skipButtonLayoutParams.setMargins((int) (15 * dp), (int) (30 * dp), (int) (15 * dp), (int) (30 * dp));
            switch (skipButtonLocation) {
                case TOP_LEFT:
                    skipButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, TRUE);
                    skipButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, TRUE);
                    break;
                case TOP_CENTER:
                    skipButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, TRUE);
                    skipButtonLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, TRUE);
                    break;
                case TOP_RIGHT:
                    skipButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, TRUE);
                    skipButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, TRUE);
                    break;
                case BOTTOM_LEFT:
                    skipButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, TRUE);
                    skipButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, TRUE);
                    break;
                case BOTTOM_CENTER:
                    skipButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, TRUE);
                    skipButtonLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, TRUE);
                    break;
                case BOTTOM_RIGHT:
                    skipButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, TRUE);
                    skipButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, TRUE);
                    break;
            }

            skipButton.setLayoutParams(skipButtonLayoutParams);
            skipButton.postInvalidate();
            addView(skipButton);

            skipButton.setVisibility(VISIBLE);
            skipButton.setEnabled(true);
        });
    }

    public void setBackButtonLayout() {
        handler.post(() -> {

            if (backButton.getParent() != null)
                ((ViewGroup) backButton.getParent()).removeView(backButton);

            LayoutParams backButtonLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            float dp = getResources().getDisplayMetrics().scaledDensity;
            backButtonLayoutParams.setMargins((int) (15 * dp), (int) (30 * dp), (int) (15 * dp), (int) (30 * dp));
            switch (backButtonLocation) {
                case TOP_LEFT:
                    backButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, TRUE);
                    backButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, TRUE);
                    break;
                case TOP_CENTER:
                    backButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, TRUE);
                    backButtonLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, TRUE);
                    break;
                case TOP_RIGHT:
                    backButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, TRUE);
                    backButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, TRUE);
                    break;
                case BOTTOM_LEFT:
                    backButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, TRUE);
                    backButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, TRUE);
                    break;
                case BOTTOM_CENTER:
                    backButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, TRUE);
                    backButtonLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, TRUE);
                    break;
                case BOTTOM_RIGHT:
                    backButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, TRUE);
                    backButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, TRUE);
                    break;
            }

            backButton.setLayoutParams(backButtonLayoutParams);
            backButton.postInvalidate();
            addView(backButton);

            backButton.setVisibility(VISIBLE);
            backButton.setEnabled(true);
        });
    }

    public void setRestartButtonLayout() {
        handler.post(() -> {

            if (restartButton.getParent() != null)
                ((ViewGroup) restartButton.getParent()).removeView(restartButton);

            LayoutParams restartButtonLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            float dp = getResources().getDisplayMetrics().scaledDensity;
            restartButtonLayoutParams.setMargins((int) (15 * dp), (int) (30 * dp), (int) (15 * dp), (int) (30 * dp));
            switch (restartButtonLocation) {
                case TOP_LEFT:
                    restartButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, TRUE);
                    restartButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, TRUE);
                    break;
                case TOP_CENTER:
                    restartButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, TRUE);
                    restartButtonLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, TRUE);
                    break;
                case TOP_RIGHT:
                    restartButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, TRUE);
                    restartButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, TRUE);
                    break;
                case BOTTOM_LEFT:
                    restartButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, TRUE);
                    restartButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, TRUE);
                    break;
                case BOTTOM_CENTER:
                    restartButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, TRUE);
                    restartButtonLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, TRUE);
                    break;
                case BOTTOM_RIGHT:
                    restartButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, TRUE);
                    restartButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, TRUE);
                    break;
            }

            restartButton.setLayoutParams(restartButtonLayoutParams);
            restartButton.postInvalidate();
            addView(restartButton);

            restartButton.setVisibility(VISIBLE);
            restartButton.setEnabled(true);
        });
    }

    /**
     * SETTERS
     */

    private void setMaskColor(int maskColor) {
        this.maskColor = maskColor;
    }

    private void setDelay(int delayMillis) {
        this.delayMillis = delayMillis;
    }

    private void setBackOnlyDelayMillisAddition(int backOnlyDelayMillisAddition) {
        this.backOnlyDelayMillisAddition = backOnlyDelayMillisAddition;
    }

    private long getDelayMillis() {
        return delayMillis;
    }

    private long getBackOnlyDelayMillisAddition() {
        return backOnlyDelayMillisAddition;
    }

    private void enableFadeInAnimation(boolean isFadeInAnimationEnabled) {
        this.isFadeInAnimationEnabled = isFadeInAnimationEnabled;
    }

    public boolean isFadeInAnimationEnabled() {
        return isFadeInAnimationEnabled;
    }

    private void enableFadeOutAnimation(boolean isFadeOutAnimationEnabled) {
        this.isFadeOutAnimationEnabled = isFadeOutAnimationEnabled;
    }

    public boolean isFadeOutAnimationEnabled() {
        return isFadeOutAnimationEnabled;
    }

    private void setShapeType(ShapeType shape) {
        this.shapeType = shape;
    }

    private void setSkipButtonLocation(ButtonLocation skipButtonLocation) {
        this.skipButtonLocation = skipButtonLocation;
    }

    private void setBackButtonLocation(ButtonLocation backButtonLocation) {
        this.backButtonLocation = backButtonLocation;
    }

    private void setRestartButtonLocation(ButtonLocation restartButtonLocation) {
        this.restartButtonLocation = restartButtonLocation;
    }

    private void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    private void setTarget(Target target) {
        targetView = target;
    }

    private void setFocusType(Focus focusType) {
        this.focusType = focusType;
    }

    private void setShape(Shape shape) {
        this.targetShape = shape;
    }

    private void setPadding(int padding) {
        this.padding = padding;
    }

    private void setDismissOnTouch(boolean dismissOnTouch) {
        this.dismissOnTouch = dismissOnTouch;
    }

    private void setFocusGravity(FocusGravity focusGravity) {
        this.focusGravity = focusGravity;
    }

    private void setColorTextViewInfo(int colorTextViewInfo) {
        this.colorTextViewInfo = colorTextViewInfo;
        textViewInfo.setTextColor(this.colorTextViewInfo);
    }

    private void setTextViewInfo(String textViewInfo) {
        this.textViewInfo.setText(textViewInfo);
    }

    private void setTextViewInfoSize(int textViewInfoSize) {
        this.textViewInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, textViewInfoSize);
    }

    private void enableInfoDialog(boolean isInfoEnabled) {
        this.isInfoEnabled = isInfoEnabled;
    }

    private void enableImageViewIcon(boolean isImageViewEnabled) {
        this.isImageViewEnabled = isImageViewEnabled;
    }

    private void enableDotView(boolean isDotViewEnabled) {
        this.isDotViewEnabled = isDotViewEnabled;
    }

    private void enableSkipButton(boolean isSkipButtonEnabled) {
        this.isSkipButtonEnabled = isSkipButtonEnabled;
    }

    private void enableBackButton(boolean isBackButtonEnabled) {
        this.isBackButtonEnabled = isBackButtonEnabled;
    }

    private void enableRestartButton(boolean isRestartButtonEnabled) {
        this.isRestartButtonEnabled = isRestartButtonEnabled;
    }

    public void setConfiguration(TutorialConfiguration configuration) {

        if (configuration != null) {
            this.maskColor = configuration.getMaskColor();
            this.delayMillis = configuration.getDelayMillis();
            this.isFadeInAnimationEnabled = configuration.isFadeInAnimationEnabled();
            this.isFadeOutAnimationEnabled = configuration.isFadeOutAnimationEnabled();
            this.colorTextViewInfo = configuration.getColorTextViewInfo();
            this.isDotViewEnabled = configuration.isDotViewEnabled();
            this.dismissOnTouch = configuration.isDismissOnTouch();
            this.colorTextViewInfo = configuration.getColorTextViewInfo();
            this.focusType = configuration.getFocusType();
            this.focusGravity = configuration.getFocusGravity();
        }
    }

    private void setListener(TutorialViewListener tutorialListener) {
        this.tutorialListener = tutorialListener;
    }

    private void setPerformClick(boolean isPerformClick, boolean isPerformLongClick, boolean performClickAlsoInReverse) {
        this.isPerformClick = isPerformClick;
        this.isPerformLongClick = isPerformLongClick;
        this.performClickAlsoInReverse = performClickAlsoInReverse;
    }

    /**
     * Builder Class
     */
    public static class Builder {

        private TutorialView tutorialView;

        private Activity activity;

        private Focus focusType = Focus.MINIMUM;

        public Builder(Activity activity) {
            this.activity = activity;
            tutorialView = new TutorialView(activity);
        }

        public TutorialView.Builder setMaskColor(int maskColor) {
            tutorialView.setMaskColor(maskColor);
            return this;
        }

        public TutorialView.Builder setDelayMillis(int delayMillis) {
            tutorialView.setDelay(delayMillis);
            return this;
        }

        public TutorialView.Builder setBackOnlyDelayMillisAddition(int backOnlyDelayMillisAddition) {
            tutorialView.setBackOnlyDelayMillisAddition(backOnlyDelayMillisAddition);
            return this;
        }

        public long getDelayMillis() {
            return tutorialView.getDelayMillis();
        }

        public long getBackOnlyDelayMillisAddition() {
            return tutorialView.getBackOnlyDelayMillisAddition();
        }

        public TutorialView.Builder enableFadeInAnimation(boolean isFadeInAnimationEnabled) {
            tutorialView.enableFadeInAnimation(isFadeInAnimationEnabled);
            return this;
        }

        public TutorialView.Builder enableFadeOutAnimation(boolean isFadeOutAnimationEnabled) {
            tutorialView.enableFadeOutAnimation(isFadeOutAnimationEnabled);
            return this;
        }

        public TutorialView.Builder setShape(ShapeType shape) {
            tutorialView.setShapeType(shape);
            return this;
        }

        public TutorialView.Builder setBackButtonLocation(ButtonLocation backButtonLocation) {
            tutorialView.setBackButtonLocation(backButtonLocation);
            return this;
        }

        public TutorialView.Builder setRestartButtonLocation(ButtonLocation restartButtonLocation) {
            tutorialView.setRestartButtonLocation(restartButtonLocation);
            return this;
        }

        public TutorialView.Builder setSkipButtonLocation(ButtonLocation skipButtonLocation) {
            tutorialView.setSkipButtonLocation(skipButtonLocation);
            return this;
        }

        public TutorialView.Builder setButtonsLocation(ButtonsLocation buttonsLocation) {
            switch (buttonsLocation) {
                case TOP:
                    setBackButtonLocation(ButtonLocation.TOP_LEFT);
                    setRestartButtonLocation(ButtonLocation.TOP_CENTER);
                    setSkipButtonLocation(ButtonLocation.TOP_RIGHT);
                    break;
                case BOTTOM:
                default:
                    setBackButtonLocation(ButtonLocation.BOTTOM_LEFT);
                    setRestartButtonLocation(ButtonLocation.BOTTOM_CENTER);
                    setSkipButtonLocation(ButtonLocation.BOTTOM_RIGHT);
                    break;
            }
            return this;
        }

        public TutorialView.Builder setFocusType(Focus focusType) {
            tutorialView.setFocusType(focusType);
            return this;
        }

        public TutorialView.Builder setFocusGravity(FocusGravity focusGravity) {
            tutorialView.setFocusGravity(focusGravity);
            return this;
        }

        public TutorialView.Builder setTarget(View view) {
            tutorialView.setTarget(new ViewTarget(view));
            return this;
        }

        public TutorialView.Builder setTargetPadding(int padding) {
            tutorialView.setPadding(padding);
            return this;
        }

        public TutorialView.Builder setTextColor(int textColor) {
            tutorialView.setColorTextViewInfo(textColor);
            return this;
        }

        public TutorialView.Builder setInfoText(String infoText) {
            tutorialView.enableInfoDialog(true);
            tutorialView.setTextViewInfo(infoText);
            return this;
        }

        public TutorialView.Builder setInfoTextSize(int textSize) {
            tutorialView.setTextViewInfoSize(textSize);
            return this;
        }

        public TutorialView.Builder dismissOnTouch(boolean dismissOnTouch) {
            tutorialView.setDismissOnTouch(dismissOnTouch);
            return this;
        }

        public TutorialView.Builder enableDotAnimation(boolean isDotAnimationEnabled) {
            tutorialView.enableDotView(isDotAnimationEnabled);
            return this;
        }

        public TutorialView.Builder enableIcon(boolean isImageViewIconEnabled) {
            tutorialView.enableImageViewIcon(isImageViewIconEnabled);
            return this;
        }

        public TutorialView.Builder enableSkipButton(boolean isSkipButtonEnabled) {
            tutorialView.enableSkipButton(isSkipButtonEnabled);
            return this;
        }

        public TutorialView.Builder enableBackButton(boolean isBackButtonEnabled) {
            tutorialView.enableBackButton(isBackButtonEnabled);
            return this;
        }

        public TutorialView.Builder enableRestartButton(boolean isRestartButtonEnabled) {
            tutorialView.enableRestartButton(isRestartButtonEnabled);
            return this;
        }

        public TutorialView.Builder setConfiguration(TutorialConfiguration configuration) {
            tutorialView.setConfiguration(configuration);
            return this;
        }

        public TutorialView.Builder setListener(TutorialViewListener tutorialListener) {
            tutorialView.setListener(tutorialListener);
            return this;
        }

        public TutorialView.Builder setCustomShape(Shape shape) {
            tutorialView.usesCustomShape = true;
            tutorialView.setShape(shape);
            return this;
        }

        public TutorialView.Builder performClick(boolean isPerformClick, boolean isPerformLongClick, boolean performClickAlsoInReverse) {
            tutorialView.setPerformClick(isPerformClick, isPerformLongClick, performClickAlsoInReverse);
            return this;
        }

        public View getTargetView() {
            return tutorialView.targetView.getView();
        }

        public TutorialView build() {
            if (tutorialView.usesCustomShape) {
                return tutorialView;
            }

            // no custom shape supplied, build our own
            Shape shape;

            if (tutorialView.shapeType == ShapeType.CIRCLE) {
                shape = new Circle(
                        tutorialView.targetView,
                        tutorialView.focusType,
                        tutorialView.focusGravity,
                        tutorialView.padding);
            } else {
                shape = new Rect(
                        tutorialView.targetView,
                        tutorialView.focusType,
                        tutorialView.focusGravity,
                        tutorialView.padding);
            }

            tutorialView.setShape(shape);
            tutorialView.updateDotViewLayout();
            tutorialView.updateInfoLayout();
            return tutorialView;
        }

        public TutorialView show(boolean inReverse) {
            TutorialView tutorialView = build();
            tutorialView.show(activity, inReverse);
            tutorialView.tutorialListener.onShow();
            return tutorialView;
        }

    }


}
