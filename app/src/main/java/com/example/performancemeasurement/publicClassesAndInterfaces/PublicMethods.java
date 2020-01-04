package com.example.performancemeasurement.publicClassesAndInterfaces;

import android.content.ContentProvider;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.*;

import static android.content.Context.MODE_PRIVATE;

public class PublicMethods {

    public static void addNewGoalSHaredPreferences(Context context, String preferenceName, String goalName, Set<String> goalValue){

        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(goalName, goalValue);
        editor.apply();

    }


}
