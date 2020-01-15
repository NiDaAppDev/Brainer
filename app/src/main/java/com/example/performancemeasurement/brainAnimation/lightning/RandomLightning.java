package com.example.performancemeasurement.brainAnimation.lightning;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.performancemeasurement.R;
import com.example.performancemeasurement.publicClassesAndInterfaces.PublicMethods;

import java.util.ArrayList;
import java.util.Random;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class RandomLightning extends View {

    Point start = new Point(-1, -1), end;
    Paint paint = new Paint(), blur = new Paint();
    Path path = new Path();
    int minLength = 1000, maxAdd = 25, lightningColor = R.color.lightning_base, blurColor = R.color.lightning_blur, fromX = 0, toX = 1500, fromY = 0, toY = 1500, stop = 0;
    float lightningWidth = 7f;
    int distance;
    String[] directions = new String[]{"LTR", "RTL"};
    String direction;

    /**
     * setBoundOnRuntime method:
     * this method sets the bounds of the lightning on runtime based on the view-of-the-lightnings size.
     */
    public void setBoundsOnRuntime() {
        ArrayList<Double> sizes1 = PublicMethods.getSharedPreferences(PublicMethods.getAppContext().getString(R.string.brain_preferences_SharedPreferences_name), PublicMethods.getAppContext().getString(R.string.lightning_view_sizes_SharedPreferences_value_name));
        ArrayList<Integer> sizes = new ArrayList<>();
        if (sizes1 != null) {
            for (int i = 0; i < sizes1.size(); i++) {
                sizes.add((int) Math.round(sizes1.get(i)));
            }
            if (sizes.get(0) > 0) {
                toX = sizes.get(0);
                toY = sizes.get(1);
            }
        }
        this.setBackgroundColor(Color.TRANSPARENT);
        setLightningMinLengthAndProgressMaxSizeOnRuntime();
    }

    /**
     * setLightningMinLengthAndProgressMaxSizeOnRuntime method:
     * this method sets the lightnings min length and the max distance between its path points.
     */
    public void setLightningMinLengthAndProgressMaxSizeOnRuntime() {
        minLength = (int) ((toX - fromX) / 1.3);
        maxAdd = minLength / 40;
    }

    /**
     * RandomLightning constructors:
     * these constructors initialize the class when created, with different arguments, not affecting
     * the initialization (they all call the init1() and init2() methods).
     */
    public RandomLightning(Context context) {
        super(context);
        init1();
        init2();
    }

    public RandomLightning(Context context, AttributeSet attrs) {
        super(context, attrs);
        init1();
        init2();
    }

    public RandomLightning(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init1();
        init2();
    }


    /**
     * setBounds method:
     * this method sets the bounds which the lightning can be in (bounds are creating a rect).
     *
     * @param fromX is the left bound.
     * @param toX   is the right bound.
     * @param fromY is the top bound.
     * @param toY   is the bottom bound.
     */
    public void setBounds(int fromX, int toX, int fromY, int toY) {
        this.fromX = fromX;
        this.toX = toX;
        this.fromY = fromY;
        this.toY = toY;
    }

    /**
     * setMinLength method:
     * this method sets the min length of the lightning.
     *
     * @param minLength is the min length.
     */
    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    /**
     * setMaxAdd method:
     * this method sets the max distance between two points of the lightning (path).
     *
     * @param maxAdd is the max distance between two points of the lightning (path).
     */
    public void setMaxAdd(int maxAdd) {
        this.maxAdd = maxAdd;
    }

    /**
     * setLightningColor method:
     * this method sets the lightning's color.
     *
     * @param lightningColor is the lightning color int.
     */
    public void setLightningColor(int lightningColor) {
        this.lightningColor = lightningColor;
    }

    /**
     * setLightningWidth method:
     * this method sets the lightning's width.
     *
     * @param lightningWidth is the lightning's width.
     */
    public void setLightningWidth(float lightningWidth) {
        this.lightningWidth = lightningWidth;
    }

    /**
     * init1 method:
     * this method initializes everything that is set in the beginning and should not change, and
     * has nothing to do with the animations.
     */
    public void init1() {
        setBoundsOnRuntime();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        start = generateStartPoint();
        direction = setDirection(start);
        paint.setColor(getResources().getColor(lightningColor));
        paint.setStrokeWidth(lightningWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        blur.set(paint);
        blur.setColor(getResources().getColor(lightningColor));
        blur.setStrokeWidth((float) (lightningWidth * 1.5));
        blur.setMaskFilter(new BlurMaskFilter(30, BlurMaskFilter.Blur.NORMAL));
        blur.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    /**
     * init2 method:
     * this method initializes everything that is set in the beginning, and might change since then,
     * and also things that relate to the animations.
     */
    public void init2() {
        path.reset();
        path.moveTo(start.x, start.y);
        end = generateNextPoint(start);

        createPath();

    }

    /**
     * createPath method:
     * creates the lightning's path, calls the rotateLightning method and the animateLightning method,
     * in order to rotate it and then animate it's revealing and fade out.
     */
    public void createPath() {
        path.lineTo(end.x, end.y);
        PathMeasure measure = new PathMeasure(path, false);
        distance = (int) measure.getLength();
        while (distance < minLength) {
            end = generateNextPoint(end);
            path.lineTo(end.x, end.y);
            measure = new PathMeasure(path, false);
            distance = (int) measure.getLength();
        }

        rotateLightning();

        animateLightning();
    }

    /**
     * rotateLightning method:
     * rotates the lightning in the bounds that were set earlier.
     */
    public void rotateLightning() {
        int x = (start.x + end.x) / 2;
        int y = (start.y + end.y) / 2;
        Matrix rotate = new Matrix();
        RectF bounds = new RectF();
        int angle = new Random().nextInt(180) + 1;
        path.computeBounds(bounds, true);
        rotate.postRotate(angle, x, y);
        path.transform(rotate);
    }

    /**
     * animateLightning method:
     * animates lightning's revealing and fades it out.
     */
    public void animateLightning() {
        float[] intervals = new float[]{distance, distance};

        setAlpha(1.0f);
        final ObjectAnimator phase = ObjectAnimator.ofFloat(RandomLightning.this, "phase", 1.0f, 0.0f);
        phase.setDuration(250);
        final ObjectAnimator alpha = ObjectAnimator.ofFloat(RandomLightning.this, "alpha", 0);
        alpha.setDuration(500);
        final AnimatorSet animset = new AnimatorSet();
        animset.play(phase).before(alpha);
        animset.start();
    }

    /**
     * setPhase method:
     * this method is called in the animateLightning method as "phase" (as a string), it creates the
     * phase reveal effect and calls the onDraw method.
     */
    public void setPhase(float phase) {
        paint.setPathEffect(createPathEffect(distance, phase, 0.0f));
        blur.setPathEffect(createPathEffect(distance, phase, 0.0f));
        invalidate();//will call onDraw
    }

    /**
     * createPathEffect method:
     * this method creates path's effect.
     */
    private static PathEffect createPathEffect(float pathLength, float phase, float offset) {
        return new DashPathEffect(new float[]{pathLength, pathLength},
                Math.max(phase * pathLength, offset));
    }

    /**
     * onDraw method:
     * this method is like main method - it runs everything and shows the canvas and it's contents
     * (in this case, the lightning).
     *
     * @param canvas is the canvas where everything is drawn.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, blur);
        canvas.drawPath(path, paint);
    }

    /**
     * generateStartPoint method:
     * this method generates a start point for the lightning's path.
     *
     * @return generated start point.
     */
    private Point generateStartPoint() {
        Point out = new Point(new Random().nextInt(toX - fromX + 1) + fromX, new Random().nextInt(toY - fromY + 1) + fromY);
        while ((out.x + minLength >= toX && out.x - minLength <= fromX) || out.x <= fromX || out.y <= fromY || out.x >= toX || out.y >= toY || out.y - (distance / 2) < fromY || out.y + (distance / 2) > toY) {
            out = new Point(new Random().nextInt(toX - fromX + 1) + fromX, new Random().nextInt(toY - fromY + 1) + fromY);
        }
        return out;
    }

    /**
     * setDirection method:
     * this method determines which direction the lightning will evolve.
     * If it cannot evolve in any direction the method calls generateStartPoint, to generate a new
     * start point that can evolve in one of the directions.
     * If it can evolve both directions, the method raffles a direction
     * ("LTR" or "RTL").
     *
     * @param startPoint lightning's (path's) current (and maybe final, depends on whether it can
     *                   evolve in any direction or not) start point.
     * @return direction in String - "LTR" or "RTL".
     */
    public String setDirection(Point startPoint) {
        if (startPoint.x + minLength > toX && startPoint.x - minLength < fromX) {
            startPoint = generateStartPoint();
            return setDirection(startPoint);
        } else if (startPoint.x + minLength < toX && startPoint.x - minLength < fromX) {
            return "LTR";
        }
        if (startPoint.x + minLength > toX && startPoint.x - minLength > fromX) {
            return "RTL";
        }
        return directions[new Random().nextInt(2)];
    }

    /**
     * generateNextPoint method:
     * generates the next path's point based on the previous one and the bounds, in a raffled
     * distance and direction from the previous point.
     *
     * @param prev is the previous point.
     * @return generated next point.
     */
    public Point generateNextPoint(Point prev) {
        int rand = new Random().nextInt(2);
        if (prev.y <= fromY) {
            rand = 0;
        } else if (prev.y >= toY) {
            rand = 1;
        }
        Point out = new Point();
        switch (direction) {
            case "LTR":
                switch (rand) {
                    case 0:
                        out = new Point(prev.x + generateNextDistance(prev, 'x', '+'), prev.y + generateNextDistance(prev, 'y', '+'));
                        break;
                    case 1:
                        out = new Point(prev.x + generateNextDistance(prev, 'x', '-'), prev.y - generateNextDistance(prev, 'y', '-'));
                        break;
                }
                break;
            case "RTL":
                switch (rand) {
                    case 0:
                        out = new Point(prev.x - generateNextDistance(prev, 'x', '+'), prev.y + generateNextDistance(prev, 'y', '+'));
                        break;
                    case 1:
                        out = new Point(prev.x - generateNextDistance(prev, 'x', '-'), prev.y - generateNextDistance(prev, 'y', '-'));
                        break;
                }
                break;
        }
        if (out.x > fromX && out.x < toX && out.y > fromY && out.y < toY) {
            return out;
        } else {
            return generateNextPoint(prev);
        }
    }

    /**
     * generateNextDistance method:
     * the method raffles the next-point's distance from the current one. The distance is between 1
     * to the max step's size [currently maxAdd = 25 (so the length of the lightning must be between
     * minLightningLength to minLightningLength + 25, to avoid errors or longer runtime in order to
     * create the right-length lightning.
     *
     * @param point  is the current point.
     * @param axis   is the axis we're adding / subtracting the generated distance to.
     * @param action is the action, addition / subtraction.
     * @return generated distance.
     */
    public int generateNextDistance(Point point, char axis, char action) {
        try {
            int out = new Random().nextInt(maxAdd - 1) + 1;
            if (axis == 'x') {
                if (action == '+') {
                    while (point.x + out > toX) {
                        out = new Random().nextInt(maxAdd) + 1;
                    }
                } else {
                    while (point.x - out < fromX) {
                        out = new Random().nextInt(maxAdd) + 1;
                    }
                }
            } else {
                if (action == '+') {
                    while (point.y + out > toY) {
                        out = new Random().nextInt(maxAdd) + 1;
                    }
                } else {
                    while (point.y - out < fromY) {
                        out = new Random().nextInt(maxAdd) + 1;
                    }
                }
            }
            return out;
        } catch (Exception e) {
            return maxAdd;
        }

    }

    /**
     * startLightningInFrequencyOf method:
     * this recreates  the lightning in a custom frequency (inserted as "create a lightning ... times a minute").
     *
     * @param timesAMinute is the amount of lightning generated in a minute.
     */
    public void startLightningsInFrequencyOf(int timesAMinute) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                init1();
                init2();
                /*invalidate();*/
                if (stop == 0) {
                    startLightningsInFrequencyOf(timesAMinute);
                }
            }
        }, 60000 / timesAMinute);
    }

    /**
     * stopLightnings method:
     * this method stops the regenerating of the lightning (stops startLightningsInFrequencyOf).
     */
    public void stopLightnings() {
        stop = 1;
    }

    /**
     * old methods with no use, replaced by better ones.
     */
    /*public Point generateNextPoint(Point prev) {
        switch (direction) {
            case "LTR-UTD":
                return new Point(prev.x + generateNextDistance(), prev.y + generateNextDistance());
            case "RTL-UTD":
                return new Point(prev.x - generateNextDistance(), prev.y + generateNextDistance());
            case "LTR-DTU":
                return new Point(prev.x + generateNextDistance(), prev.y - generateNextDistance());
            case "RTL-DTU":
                return new Point(prev.x - generateNextDistance(), prev.y - generateNextDistance());
        }
        return new Point(prev.x + generateNextDistance(), prev.y + generateNextDistance());
    }*/

    /*public void getLightningIntoBounds(){
        Matrix translate = new Matrix();
        if(start.x < fromX || start.y < fromY){
            if(start.x < fromX && start.y < fromY){
                translate.setTranslate(fromX - start.x, fromY - start.y);
            }else if (start.x < fromX){
                translate.setTranslate(fromX - start.x, 0);
            }else {
                translate.setTranslate(0, fromY - start.y);
            }
        }else if(end.x > toX || end.y > toY){
            if(end.x > toY && end.y > toY){
                translate.setTranslate(toX - end.x, toY - end.y);
            }else if (end.x > toX){
                translate.setTranslate(toX - end.x, 0);
            }else {
                translate.setTranslate(0, toY - end.y);
            }
        }else{
            return;
        }
        path.transform(translate);
        getLightningIntoBounds();
    }*/

    /*

     * getBounds method:
     * this method logs the bounds of the lightning.

        public void getBounds() {
            Log.e("Point", "\nx: " + start.x + "\ny: " + start.y + "\nfromX: " + fromX + "\ntoX: " + toX + "\nfromY: " + fromY + "\ntoY: " + toY);
        }
     */

}
