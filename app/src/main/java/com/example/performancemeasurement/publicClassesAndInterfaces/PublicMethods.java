package com.example.performancemeasurement.publicClassesAndInterfaces;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.performancemeasurement.GoalAndDatabaseObjects.Goal;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;


public class PublicMethods<T> extends Application {

    public static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        PublicMethods.appContext = getApplicationContext();
    }

    public static <T> void addNewSharedPreferences(String preferenceName, String valuesName, ArrayList<T> values){

        SharedPreferences sharedPreferences = getAppContext().getSharedPreferences(preferenceName, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(values);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(valuesName, json);
        editor.apply();

    }

    public static <T> ArrayList<T> getSharedPreferences(String preferenceName, String valuesName){

        SharedPreferences sharedPreferences = getAppContext().getSharedPreferences(preferenceName, MODE_PRIVATE);
        Gson gson = new Gson();
        ArrayList<T> out = new ArrayList<>();
        String json = sharedPreferences.getString(valuesName, "");
        if (!json.isEmpty()) {
            Type type = new TypeToken<ArrayList<T>>() {}.getType();
            out = gson.fromJson(json, type);
        }else{
            out = null;
        }
        return out;

    }

    public static <T> ArrayList<T> arrayListWithout(ArrayList<T> arrayList, ArrayList<T> exceptions){
        ArrayList<T> output = new ArrayList<>(arrayList);

        for(T exception : exceptions){
            output.remove(exception);
        }

        return output;
    }

    public static <T> T getValueOrDefault(T value, T defaultValue) {
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    public static int positionOfGoalInGoalsArrayList(String goalName, ArrayList<Goal> goalsArrayList){
        int output = 0;
        for(Goal goal1 : goalsArrayList){
            if(goalName.equals(goal1.getName())){
                return goalsArrayList.indexOf(goal1);
            }
        }
        return -1;
    }

    public static Context getAppContext() {
        return PublicMethods.appContext;
    }



}
