package com.nidaappdev.performancemeasurement.brainAnimation.lightning;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.nidaappdev.performancemeasurement.R;

import java.util.ArrayList;
import java.util.Random;

public class LightningManager extends ViewGroup {

    ArrayList<RandomLightning> randomLightnings = new ArrayList<>();
    ImageView brainImage;
    int numOfLightnings, singleLightningRepeatFrequency;
    Context context;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public LightningManager(Context context) {
        super(context);
        this.context = context;
        init();
        brainImage.setBackgroundResource(R.drawable.brain_image);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public LightningManager(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
        brainImage.setBackgroundResource(R.drawable.brain_image);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public LightningManager(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
        brainImage.setBackgroundResource(R.drawable.brain_image);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void init(){
        numOfLightnings = 1;
        singleLightningRepeatFrequency = 60;
        generateLightnings();
        brainImage = new ImageView(context);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void generateLightnings(){
        randomLightnings.clear();
        for(int i = 0; i < numOfLightnings; i ++){
            randomLightnings.add(new RandomLightning(context));
            randomLightnings.get(i).setId(i);
            this.addView(randomLightnings.get(i), 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        }
        startLightningsRepetition();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setupManager(int numOfLightnings, int singleLightningRepeatFrequency){
        this.numOfLightnings = numOfLightnings;
        this.singleLightningRepeatFrequency = singleLightningRepeatFrequency;

        generateLightnings();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startLightningsRepetition(){

        int numOfLightnings = randomLightnings.size(),
                totalFrequencies = singleLightningRepeatFrequency * numOfLightnings,
                currentRand,
                indexOfLightning,
                currentFrequency;

        ArrayList<Integer> frequencies = new ArrayList<>();
        for(int i = 0; i < numOfLightnings - 1; i ++){
            currentRand = new Random().nextInt(10) + (totalFrequencies / (numOfLightnings - i)) - 5;
            frequencies.add(currentRand);
            totalFrequencies -= currentRand;
        }
        frequencies.add(totalFrequencies);
        for(RandomLightning lightning : randomLightnings){
            indexOfLightning = randomLightnings.indexOf(lightning);
            currentFrequency = frequencies.get(indexOfLightning);
            lightning.startLightningsInFrequencyOf(currentFrequency);
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        brainImage.draw(canvas);



    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        for(RandomLightning lightning : randomLightnings){
            lightning.draw(canvas);
        }
    }
}
