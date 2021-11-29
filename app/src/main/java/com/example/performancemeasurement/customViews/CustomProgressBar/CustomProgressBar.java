package com.example.performancemeasurement.customViews.CustomProgressBar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.Nullable;

import com.example.performancemeasurement.R;
import com.example.performancemeasurement.customViews.CustomProgressBarButton.ButtonController;
import com.example.performancemeasurement.customViews.CustomProgressBarButton.DefaultButtonController;

public class CustomProgressBar extends View {

    private Paint progressBarPaint;
    private TextPaint textPaint;
    private RectF baseRect, progressRect;
    private float width, height, radius, progress, progressPercent, progressWidth, textSize;
    private int baseColor, progressColor;
    private int[] mBackgroundColor, mOriginBackgroundColor;
    private String text;
    private LinearGradient progressTextGradient, progressBarGradient;
    private ValueAnimator barAnimator;
    private ButtonController mCustomerController, mDefaultController;
    private boolean enableGradient, enablePress;

    public CustomProgressBar(Context context) {
        this(context, null);
    }

    public CustomProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            initController();
            initAttrs(attrs);
        }
        initController();
    }

    public CustomProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }


    public void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.CustomProgressBar, 0, 0);
        try {
            setBaseColor(typedArray.getColor(R.styleable.CustomProgressBar_base_color, getResources().getColor(R.color.round_corner_progress_bar_background_default)));
            setProgressColor(typedArray.getColor(R.styleable.CustomProgressBar_progress_color, getResources().getColor(R.color.round_corner_progress_bar_progress_default)));
            setRadius(typedArray.getFloat(R.styleable.CustomProgressBar_radius, getMeasuredHeight() / 2));
            setTextSize(typedArray.getFloat(R.styleable.CustomProgressBar_customProgressBarTextSize, 30));
            setText(typedArray.getString(R.styleable.CustomProgressBar_text));
            setProgress(typedArray.getFloat(R.styleable.CustomProgressBar_progress_percentage, 0));
            initGradientColor(getProgressColor(), getProgressColor());
            enableGradient = typedArray.getBoolean(R.styleable.CustomProgressBar_enable_gradient, false);
            enablePress = typedArray.getBoolean(R.styleable.CustomProgressBar_enable_press, false);
            ((DefaultButtonController)mDefaultController).setEnableGradient(enableGradient).setEnablePress(enablePress);
            if (enableGradient) {
                initGradientColor(mBackgroundColor[0], mDefaultController.getLighterColor(mBackgroundColor[0]));
            }
        } finally {
            typedArray.recycle();
        }

        initPaints();
    }

    private void initPaints() {

        progressBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressBarPaint.setColor((int) getBaseColor());
        progressBarPaint.setStyle(Paint.Style.FILL);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor((int) getProgressColor());
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textSize);

        initBars();

    }

    private void initBars() {

        baseRect = new RectF(0, 0, getWidth(), getHeight());

        ButtonController buttonController = switchController();

        progressPercent = getProgress() / (100 + 0f);

        if (buttonController.enableGradient()) {
            int[] colorList = new int[]{mBackgroundColor[0], mBackgroundColor[1], getBaseColor()};
            progressBarGradient = new LinearGradient(0, 0, getMeasuredWidth(), 0,
                    new int[]{mBackgroundColor[0], mBackgroundColor[1], getBaseColor()},
                    new float[]{0, progressPercent, progressPercent + 0.001f},
                    Shader.TileMode.CLAMP
            );
            progressBarPaint.setShader(progressBarGradient);
        } else {
            progressBarGradient = new LinearGradient(0, 0, getMeasuredWidth(), 0,
                    new int[]{mBackgroundColor[0], getBaseColor()},
                    new float[]{progressPercent, progressPercent + 0.001f},
                    Shader.TileMode.CLAMP
            );
            progressBarPaint.setColor(mBackgroundColor[0]);
            progressBarPaint.setShader(progressBarGradient);
        }


    }

    private void initController() {
        mDefaultController = new DefaultButtonController();
    }

    private int[] initGradientColor(int leftColor, int rightColor) {
        mBackgroundColor = new int[2];
        mBackgroundColor[0] = leftColor;
        mBackgroundColor[1] = rightColor;
        return mBackgroundColor;
    }


    private void updateProgressBar(Canvas canvas) {
        updateBar(canvas);
        updateText(canvas);
        setOutlineProvider(new ZoftinoCustomOutlineProvider(180));
        setClipToOutline(true);
    }

    public void updateBar(Canvas canvas) {
        updateProgressWidth();
        initBars();

        canvas.drawRoundRect(baseRect, radius, radius, progressBarPaint);


    }

    public void updateText(Canvas canvas) {

        Rect textBounds = new Rect(), percentageBounds = new Rect();
        textPaint.getTextBounds(String.valueOf(progress), 0, String.valueOf(progress).length(), percentageBounds);
        textPaint.getTextBounds(getText(), 0, getText().length(), textBounds);
        setText(TextUtils.ellipsize(text, textPaint, canvas.getWidth() - percentageBounds.width() - 30, TextUtils.TruncateAt.END).toString());
        textSize = (int) (canvas.getHeight() * 0.4);
        textPaint.setTextSize(textSize);

        final float y = canvas.getHeight() / 2 - (textPaint.descent() / 2 + textPaint.ascent() / 2);

        if (getText() == null) {
            setText("");
        }
        final float textWidth = textPaint.measureText(getFullText());

        float textStart = (getMeasuredWidth() - textWidth) / 2;

        float textEnd = (getMeasuredWidth() + textWidth) / 2;

        float coveredByProgressTextLength = (textWidth - getMeasuredWidth()) / 2 + getProgressWidth();
        float textProgress = coveredByProgressTextLength / textWidth;
        if (getProgressWidth() <= textStart) {
            textPaint.setShader(null);
            textPaint.setColor((int) getProgressColor());
        } else if (textStart < getProgressWidth() && getProgressWidth() <= textEnd) {
            progressTextGradient = new LinearGradient(textStart, 0, textEnd, 0,
                    new int[]{Color.WHITE, (int) getProgressColor()},
                    new float[]{textProgress, textProgress + 0.001f},
                    Shader.TileMode.CLAMP);
            textPaint.setColor((int) getProgressColor());
            textPaint.setShader(progressTextGradient);
        } else {
            textPaint.setShader(null);
            textPaint.setColor(Color.WHITE);
        }

        canvas.drawText(getFullText(), (getMeasuredWidth() - textWidth) / 2, y, textPaint);
    }

    public void updateProgressWidth() {
        progressWidth = getWidth() * getProgress() / 100;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!this.isInEditMode()) {
            updateProgressBar(canvas);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // set width
        setWidth(MeasureSpec.getSize(widthMeasureSpec));

        // set height
        setHeight(MeasureSpec.getSize(heightMeasureSpec));
        float exactHeight;
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                // we must be exactly the given size
                exactHeight = height;
                break;
            case MeasureSpec.AT_MOST:
                // we can not be bigger than the specified height
                exactHeight = (int) Math.min(height, width);
                break;
            default:
                // we can be whatever height want
                exactHeight = (int) height;
                break;
        }

        setMeasuredDimension((int) width, (int) exactHeight);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        ButtonController buttonController = switchController();
        if (buttonController.enablePress()) {
            if (mOriginBackgroundColor == null) {
                mOriginBackgroundColor = new int[2];
                mOriginBackgroundColor[0] = mBackgroundColor[0];
                mOriginBackgroundColor[1] = mBackgroundColor[1];
            }
            if (this.isPressed()) {
                int pressColorleft = buttonController.getPressedColor(mBackgroundColor[0]);
                int pressColorright = buttonController.getPressedColor(mBackgroundColor[1]);
                if (buttonController.enableGradient()) {
                    initGradientColor(pressColorleft, pressColorright);
                } else {
                    initGradientColor(pressColorleft, pressColorleft);
                }
            } else {
                if (buttonController.enableGradient()) {
                    initGradientColor(mOriginBackgroundColor[0], mOriginBackgroundColor[1]);
                } else {
                    initGradientColor(mOriginBackgroundColor[0], mOriginBackgroundColor[0]);
                }
            }
            invalidate();
        }

    }


    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {

        Parcelable superState = super.onSaveInstanceState();
        return new CustomProgressBar.SavedState(superState, (int) getProgress(), text);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        CustomProgressBar.SavedState s = (CustomProgressBar.SavedState) state;
        super.onRestoreInstanceState(s.getSuperState());
        setProgress(s.progress);
        setText(s.currentText);
    }


    private ButtonController switchController() {
        if (mCustomerController != null) {
            return mCustomerController;
        } else {
            return mDefaultController;
        }
    }

    public void removeAllAnim() {
        barAnimator.cancel();
        barAnimator.removeAllListeners();
    }


    public void setWidth(float width) {
        this.width = width;
        invalidate();
    }

    public void setHeight(float height) {
        this.height = height;
        invalidate();
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
    }

    public Integer getBaseColor() {
        return baseColor;
    }

    public void setBaseColor(Integer baseColor) {
        this.baseColor = baseColor;
        invalidate();
    }

    public Integer getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(Integer progressColor) {
        this.progressColor = progressColor;
        initGradientColor(progressColor, progressColor);
        invalidate();
    }

    public String getText() {
        return text;
    }

    public String getFullText(){
        return text + " " + (int) progress + "%";
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        setProgress(progress, true);
    }

    public void setProgress(final float progress, boolean animate) {
        if (animate) {

//            barAnimator = ValueAnimator.ofFloat(getProgress() / 100, progress / 100);
//
//            barAnimator.setDuration(700);
//
//            barAnimator.setInterpolator(new DecelerateInterpolator());
//
//            barAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    float interpolation = (float) animation.getAnimatedValue();
//                    setProgress(((interpolation * (progress - getProgress()))) + getProgress(), false);
//                }
//            });
//
//            if (!barAnimator.isStarted()) {
//                barAnimator.start();
//            }

//            Animation animation = new Animation() {
//                @Override
//                protected void applyTransformation(float interpolatedTime, Transformation t) {
//                    super.applyTransformation(interpolatedTime, t);
//                    float value = getProgress() + (progress - getProgress()) * interpolatedTime;
//                    setProgress(value, false);
//                }
//            };
//            animation.setDuration(700);
//            this.startAnimation(animation);

            this.progress = progress;
            postInvalidate();

        } else {
            this.progress = progress;
            postInvalidate();
        }
    }

    public float getProgressWidth() {
        updateProgressWidth();
        return progressWidth;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }


    public void enableDefaultPress(boolean enable) {
        if (mDefaultController != null) {
            ((DefaultButtonController) mDefaultController).setEnablePress(enable);
        }
    }

    public void enableDefaultGradient(boolean enable) {
        if (mDefaultController != null) {
            ((DefaultButtonController) mDefaultController).setEnableGradient(enable);
            initGradientColor(mDefaultController.getLighterColor(mBackgroundColor[0]), mBackgroundColor[0]);
        }
    }

    public CustomProgressBar setCustomerController(ButtonController customerController) {
        mCustomerController = customerController;
        return this;
    }


    public static class SavedState extends BaseSavedState {

        private int progress;
        private String currentText;

        public SavedState(Parcelable parcel, int progress, String currentText) {
            super(parcel);
            this.progress = progress;
            this.currentText = currentText;
        }

        private SavedState(Parcel in) {
            super(in);
            progress = in.readInt();
            currentText = in.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(progress);
            out.writeString(currentText);
        }

        public static final Creator<CustomProgressBar.SavedState> CREATOR = new Creator<CustomProgressBar.SavedState>() {

            @Override
            public CustomProgressBar.SavedState createFromParcel(Parcel in) {
                return new CustomProgressBar.SavedState(in);
            }

            @Override
            public CustomProgressBar.SavedState[] newArray(int size) {
                return new CustomProgressBar.SavedState[size];
            }
        };

    }

    public class ZoftinoCustomOutlineProvider extends ViewOutlineProvider {

        int roundCorner;

        public ZoftinoCustomOutlineProvider(int round) {
            roundCorner = round;
        }

        @Override
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), roundCorner);
        }
    }

}
