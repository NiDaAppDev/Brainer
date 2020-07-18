package com.example.performancemeasurement.GoalAndDatabaseObjects;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.performancemeasurement.GoalAndDatabaseObjects.GoalContract.*;
import com.example.performancemeasurement.publicClassesAndInterfaces.PublicMethods;

import java.util.ArrayList;

public class GoalDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "goalsList.db";

    public static final int DATABASE_VERSION = 1;

    private SQLiteDatabase sQLiteDatabase = getWritableDatabase();

    public GoalDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_GOALS_LIST_TABLE = "CREATE TABLE " +
                GoalEntry.TABLE_NAME + " (" +
                GoalEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GoalEntry.COLUMN_GOAL_NAME + " TEXT NOT NULL, " +
                GoalEntry.COLUMN_GOAL_DESCRIPTION + " TEXT NOT NULL, " +
                GoalEntry.COLUMN_GOAL_PARENT + " TEXT NOT NULL, " +
                GoalEntry.COLUMN_GOAL_COUNTED_TIME + " INTEGER NOT NULL, " +
                GoalEntry.COLUMN_GOAL_ESTIMATED_TIME + " INTEGER NOT NULL, " +
                GoalEntry.COLUMN_GOAL_ACHIEVED + " BOOLEAN NOT NULL, " +
                GoalEntry.COLUMN_GOAL_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
        db.execSQL(SQL_CREATE_GOALS_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GoalEntry.TABLE_NAME);
        onCreate(db);
    }

    public Cursor getGoalID(Goal goal) {
        String queryID =
                "SELECT " + GoalEntry._ID +
                        " FROM " + GoalEntry.TABLE_NAME +
                        " WHERE " +
                        GoalEntry.COLUMN_GOAL_NAME + " = '" + goal.getName() + "'";
        return sQLiteDatabase.rawQuery(queryID, null);
    }

    public boolean doesGoalExist(Goal goal) {
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + GoalEntry.COLUMN_GOAL_NAME + " = '" + goal.getName() + "'";
        Cursor cursor = sQLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public Cursor getAllGoalsCursor() {
        return getWritableDatabase().query(GoalEntry.TABLE_NAME, null, null, null, null, null, GoalEntry.COLUMN_GOAL_TIMESTAMP + " DESC");
    }

    public Cursor getActiveGoalsCursor() {
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + GoalEntry.COLUMN_GOAL_ACHIEVED + " = 0";
        return sQLiteDatabase.rawQuery(query, null);
    }

    public Cursor getAchievedGoalsCursor() {
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + GoalEntry.COLUMN_GOAL_ACHIEVED + " = 1";
        return sQLiteDatabase.rawQuery(query, null);
    }

    public Cursor getSubGoalsCursorOf(Goal goal) {
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + GoalEntry.COLUMN_GOAL_PARENT + " = '" + goal.getName() + "'";
        return sQLiteDatabase.rawQuery(query, null);
    }

    public ArrayList<Goal> getAllGoalsArrayList() {
        Cursor cursor = getWritableDatabase().query(GoalEntry.TABLE_NAME, null, null, null, null, null, GoalEntry.COLUMN_GOAL_TIMESTAMP + " DESC");
        ArrayList<Goal> goals = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(GoalEntry.COLUMN_GOAL_NAME));
            String description = cursor.getString(cursor.getColumnIndex(GoalEntry.COLUMN_GOAL_DESCRIPTION));
            String parent = cursor.getString(cursor.getColumnIndex(GoalEntry.COLUMN_GOAL_PARENT));
            int timeCounted = cursor.getInt(cursor.getColumnIndex(GoalEntry.COLUMN_GOAL_COUNTED_TIME));
            int timeEstimated = cursor.getInt(cursor.getColumnIndex(GoalEntry.COLUMN_GOAL_ESTIMATED_TIME));
            boolean achieved = cursor.getInt(cursor.getColumnIndex(GoalEntry.COLUMN_GOAL_ACHIEVED)) > 0;
            Goal goal = new Goal(name, description, parent, timeCounted, timeEstimated, achieved);
            goals.add(goal);
        }
        return goals;
    }

    public ArrayList<Goal> getActiveGoalsArrayList() {
        ArrayList<Goal> activeGoals = getAllGoalsArrayList();
        for (Goal goal : activeGoals) {
            if (goal.isAchieved())
                activeGoals.remove(goal);
        }
        return activeGoals;
    }

    public ArrayList<Goal> getAchievedGoalsArrayList() {
        ArrayList<Goal> achievedGoals = getAllGoalsArrayList();
        for (Goal goal : achievedGoals) {
            if (!goal.isAchieved())
                achievedGoals.remove(goal);
        }
        return achievedGoals;
    }

    public ArrayList<Goal> getSubGoalsArrayListOf(Goal goal) {
        ArrayList<Goal> subGoals = new ArrayList<>();
        for (Goal subGoal : getAllGoalsArrayList()) {
            if (subGoal.getParentGoal().equals(goal.getName())) {
                subGoals.add(subGoal);
            }
        }
        return subGoals;
    }

    public void addGoal(Goal goal) {
        if (!doesGoalExist(goal) && goal.getName().trim().length() != 0 && goal.getTimeEstimated() != 0) {
            String name = goal.getName();
            String description = PublicMethods.getValueOrDefault(goal.getDescription(), "");
            String parent = PublicMethods.getValueOrDefault(goal.getParentGoal(), "");
            int timeCounted = PublicMethods.getValueOrDefault(goal.getTimeCounted(), 0);
            int timeEstimated = goal.getTimeEstimated();
            boolean achieved = PublicMethods.getValueOrDefault(goal.isAchieved(), false);

            ContentValues contentValues = new ContentValues();
            contentValues.put(GoalEntry.COLUMN_GOAL_NAME, name);
            contentValues.put(GoalEntry.COLUMN_GOAL_DESCRIPTION, description);
            contentValues.put(GoalEntry.COLUMN_GOAL_PARENT, parent);
            contentValues.put(GoalEntry.COLUMN_GOAL_COUNTED_TIME, timeCounted);
            contentValues.put(GoalEntry.COLUMN_GOAL_ESTIMATED_TIME, timeEstimated);
            contentValues.put(GoalEntry.COLUMN_GOAL_ACHIEVED, achieved);

            sQLiteDatabase.insert(GoalEntry.TABLE_NAME, null, contentValues);
        }
    }

    public void removeGoal(Goal goal) {
        if (doesGoalExist(goal)) {
            sQLiteDatabase.delete(GoalEntry.TABLE_NAME,
                    GoalEntry.COLUMN_GOAL_NAME + "=" + goal.getName(),
                    null);
        }
    }

    public void clearDatabase() {
        String clearDBQuery = "DELETE FROM "+GoalEntry.TABLE_NAME;
        sQLiteDatabase.execSQL(clearDBQuery);
    }

    public void editGoal(Goal goal, String newName, String newDescription, ArrayList<Goal> removedSubGoals) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(GoalEntry.COLUMN_GOAL_NAME, newName.equals(goal.getName()) ? goal.getName() : newName);
        contentValues.put(GoalEntry.COLUMN_GOAL_DESCRIPTION, newDescription.equals(goal.getDescription()) ? goal.getDescription() : newDescription);

        sQLiteDatabase.update(GoalEntry.TABLE_NAME, contentValues, GoalEntry.COLUMN_GOAL_NAME + " = " + goal.getName(), null);

        if(!(removedSubGoals.isEmpty())){
            for(Goal removedSubGoal : removedSubGoals){
                String subGoalsQuery = "UPDATE " + GoalEntry.TABLE_NAME +
                        " SET " +
                        GoalEntry.COLUMN_GOAL_PARENT + " = ''" +
                        " WHERE " +
                        GoalEntry.COLUMN_GOAL_NAME + " = '" + removedSubGoal.getName() +
                        "' AND " +
                        GoalEntry.COLUMN_GOAL_PARENT + " = '" + goal.getName() + "'";
                sQLiteDatabase.execSQL(subGoalsQuery);
            }
        }

    }

}