package com.nidaappdev.performancemeasurement.GoalAndDatabaseObjects;


import static android.content.ContentValues.TAG;
import static com.nidaappdev.performancemeasurement.App.goalsDBReference;
import static com.nidaappdev.performancemeasurement.GoalAndDatabaseObjects.GoalContract.GoalEntry.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.nidaappdev.performancemeasurement.App;
import com.nidaappdev.performancemeasurement.GoalAndDatabaseObjects.GoalContract.GoalEntry;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.PublicMethods;
import com.nidaappdev.performancemeasurement.util.PrefUtil;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GoalDBHelper extends SQLiteOpenHelper {
    public static final String GOALS_DATABASE_NAME = "goalsList.db";

    public static final int DATABASE_VERSION = 1;

    private final SQLiteDatabase sQLiteDatabase = getWritableDatabase();

    private static final StatisticsDBHelper statisticsDB = new StatisticsDBHelper(App.appContext);

    public GoalDBHelper(Context context) {
        super(context, GOALS_DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //TODO to edit db columns edit here
        String SQL_CREATE_GOALS_LIST_TABLE = "CREATE TABLE " +
                GoalEntry.TABLE_NAME + " (" +
                GoalEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GoalEntry.COLUMN_GOAL_NAME + " TEXT NOT NULL, " +
                GoalEntry.COLUMN_GOAL_DESCRIPTION + " TEXT NOT NULL, " +
                GoalEntry.COLUMN_GOAL_PARENT + " TEXT NOT NULL, " +
                GoalEntry.COLUMN_GOAL_COUNTED_TIME + " INTEGER NOT NULL, " +
                GoalEntry.COLUMN_GOAL_ESTIMATED_TIME + " INTEGER NOT NULL, " +
                GoalEntry.COLUMN_GOAL_COUNTED_POMODORO + " INTEGER NOT NULL, " +
                COLUMN_GOAL_DIFFICULTY + " INTEGER NOT NULL, " +
                GoalEntry.COLUMN_GOAL_EVOLVING + " INTEGER NOT NULL, " +
                GoalEntry.COLUMN_GOAL_SATISFACTION + " INTEGER NOT NULL, " +
                COLUMN_GOAL_ACHIEVED + " BOOLEAN NOT NULL, " +
                GoalEntry.COLUMN_GOAL_TAGS + " TEXT NOT NULL, " +
                GoalEntry.COLUMN_GOAL_FINISH_DATE + " VARCHAR(10) NOT NULL, " +
                GoalEntry.COLUMN_GOAL_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
        db.execSQL(SQL_CREATE_GOALS_LIST_TABLE);
        Cursor dbCursor = db.query(GoalEntry.TABLE_NAME, null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GoalEntry.TABLE_NAME);
        onCreate(db);
    }

    public Goal getGoalByName(String goalName) {
        ArrayList<Goal> goals = getAllGoalsArrayList();
        for (Goal goal : goals) {
            if (goal.getName().equals(goalName)) {
                return goal;
            }
        }
        return null;
    }

    public Cursor getGoalID(Goal goal) {
        String queryID =
                "SELECT " + GoalEntry._ID +
                        " FROM " + GoalEntry.TABLE_NAME +
                        " WHERE " +
                        GoalEntry.COLUMN_GOAL_NAME + " = '" + goal.getName() + "'";
        return sQLiteDatabase.rawQuery(queryID, null);
    }

    public boolean doesGoalExist(String goalName) {
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + GoalEntry.COLUMN_GOAL_NAME + " = '" + goalName + "'";
        Cursor cursor = sQLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public String getGoalStartDate(String goalName) {
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + GoalEntry.COLUMN_GOAL_NAME + " = '" + goalName + "'";
        Cursor cursor = sQLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        String result = PublicMethods.formatDateTime(cursor.getString(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_TIMESTAMP)));
        cursor.close();
        return result;
    }

    public String getGoalFinishDate(String goalName) {
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + GoalEntry.COLUMN_GOAL_NAME + " = '" + goalName + "'";
        Cursor cursor = sQLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        String result = cursor.getString(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_FINISH_DATE));
        cursor.close();
        return result;
    }

    public boolean doesActiveGoalNameAlreadyExist(String name, String editedGoalName) {
        String Query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + GoalEntry.COLUMN_GOAL_NAME + " = " + name + " AND " + COLUMN_GOAL_ACHIEVED + " = 0";
        Cursor cursor = sQLiteDatabase.rawQuery(Query, null);
        if (cursor.getCount() <= 0 || (cursor.getCount() <= 1 && name.equals(editedGoalName))) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public Cursor getAllGoalsCursor() {
        return getWritableDatabase().query(GoalEntry.TABLE_NAME, null, null, null, null, null, GoalEntry.COLUMN_GOAL_NAME + "ASC");
    }

    public Cursor getActiveGoalsCursor() {
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + COLUMN_GOAL_ACHIEVED + " = 0";
        return sQLiteDatabase.rawQuery(query, null);
    }

    public Cursor getAchievedGoalsCursor() {
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + COLUMN_GOAL_ACHIEVED + " = 1";
        return sQLiteDatabase.rawQuery(query, null);
    }

    public Cursor getSubGoalsCursorOf(Goal goal) {
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + GoalEntry.COLUMN_GOAL_PARENT + " = '" + goal.getName() + "'";
        return sQLiteDatabase.rawQuery(query, null);
    }

    public ArrayList<Goal> getAllGoalsArrayList() {

        //TODO to edit db columns edit here


        Cursor cursor = getWritableDatabase().query(GoalEntry.TABLE_NAME, null, null, null, null, null, GoalEntry.COLUMN_GOAL_NAME + " ASC");
        ArrayList<Goal> goals = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_NAME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_DESCRIPTION));
            String parent = cursor.getString(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_PARENT));
            int timeCounted = cursor.getInt(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_COUNTED_TIME));
            int timeEstimated = cursor.getInt(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_ESTIMATED_TIME));
            int pomodoroCounted = cursor.getInt(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_COUNTED_POMODORO));
            int difficulty = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GOAL_DIFFICULTY));
            int evolving = cursor.getInt(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_EVOLVING));
            int satisfaction = cursor.getInt(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_SATISFACTION));
            boolean achieved = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GOAL_ACHIEVED)) > 0;
            ArrayList<String> tags = new ArrayList<>(Arrays.asList(cursor.getString(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_TAGS)).split(",")));
            String finishDate = cursor.getString(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_FINISH_DATE));
            Goal goal = new Goal(name, description, parent, timeCounted, timeEstimated, pomodoroCounted, difficulty, evolving, satisfaction, achieved, tags, finishDate);
            goals.add(goal);
        }
        cursor.close();
        return goals;
    }

    public ArrayList<Goal> getActiveGoalsArrayList() {
        ArrayList<Goal> allGoals = getAllGoalsArrayList();
        ArrayList<Goal> activeGoals = new ArrayList<>();
        for (Goal goal : allGoals) {
            if (!goal.isAchieved()) {
                activeGoals.add(goal);
            }
        }
        return activeGoals;
    }

    public ArrayList<Goal> getAchievedGoalsArrayList() {
        ArrayList<Goal> allGoals = getAllGoalsArrayList();
        ArrayList<Goal> achievedGoals = new ArrayList<>();
        for (Goal goal : allGoals) {
            if (goal.isAchieved())
                achievedGoals.add(goal);
        }
        return achievedGoals;
    }

    public ArrayList<Goal> getMostlyPomodoroAchievedGoalsArrayList() {
        ArrayList<Goal> achievedGoals = getAchievedGoalsArrayList();
        ArrayList<Goal> mostlyPomodoroAchievedGoals = new ArrayList<>();
        for (Goal goal : achievedGoals) {
            if ((float) goal.getPomodoroCounted() * 25f > ((float) goal.getTimeCounted() / 60f) - ((float) goal.getPomodoroCounted() * 25f)) {
                mostlyPomodoroAchievedGoals.add(goal);
            }
        }
        return mostlyPomodoroAchievedGoals;
    }

    public ArrayList<Goal> getMostlyRegularAchievedGoalsArrayList() {
        ArrayList<Goal> achievedGoals = getAchievedGoalsArrayList();
        ArrayList<Goal> mostlyRegularAchievedGoals = new ArrayList<>();
        for (Goal goal : achievedGoals) {
            if ((float) goal.getPomodoroCounted() * 25f < ((float) goal.getTimeCounted() / 60f) - ((float) goal.getPomodoroCounted() * 25f)) {
                mostlyRegularAchievedGoals.add(goal);
            }
        }
        return mostlyRegularAchievedGoals;
    }

    public float getPomodoroDifficultyAverage() {
        ArrayList<Goal> pomodoroAchievedGoals = getMostlyPomodoroAchievedGoalsArrayList();
        float result = 0f;
        for (Goal goal : pomodoroAchievedGoals) {
            result += goal.getDifficulty();
        }
        result /= pomodoroAchievedGoals.size();
        return result;
    }

    public float getRegularDifficultyAverage() {
        ArrayList<Goal> regularAchievedGoals = getMostlyRegularAchievedGoalsArrayList();
        float result = 0f;
        for (Goal goal : regularAchievedGoals) {
            result += goal.getDifficulty();
        }
        result /= regularAchievedGoals.size();
        return result;
    }

    public float getPomodoroSatisfactionAverage() {
        ArrayList<Goal> pomodoroAchievedGoals = getMostlyPomodoroAchievedGoalsArrayList();
        float result = 0f;
        for (Goal goal : pomodoroAchievedGoals) {
            result += goal.getSatisfaction();
        }
        result /= pomodoroAchievedGoals.size();
        return result;
    }

    public float getRegularSatisfactionAverage() {
        ArrayList<Goal> regularAchievedGoals = getMostlyRegularAchievedGoalsArrayList();
        float result = 0f;
        for (Goal goal : regularAchievedGoals) {
            result += goal.getSatisfaction();
        }
        result /= regularAchievedGoals.size();
        return result;
    }

    public float getPomodoroEvolvingAverage() {
        ArrayList<Goal> pomodoroAchievedGoals = getMostlyPomodoroAchievedGoalsArrayList();
        float result = 0f;
        for (Goal goal : pomodoroAchievedGoals) {
            result += goal.getEvolving();
        }
        result /= pomodoroAchievedGoals.size();
        return result;
    }

    public float getRegularEvolvingAverage() {
        ArrayList<Goal> regularAchievedGoals = getMostlyRegularAchievedGoalsArrayList();
        float result = 0f;
        for (Goal goal : regularAchievedGoals) {
            result += goal.getEvolving();
        }
        result /= regularAchievedGoals.size();
        return result;
    }

    public float getPomodoroEvaluationAverage() {
        ArrayList<Goal> pomodoroAchievedGoals = getMostlyPomodoroAchievedGoalsArrayList();
        float result = 0f;
        for (Goal goal : pomodoroAchievedGoals) {
            result += (100 - Math.abs(goal.getProgress() - 100)) / 20f;
        }
        result /= pomodoroAchievedGoals.size();
        return result;
    }

    public float getRegularEvaluationAverage() {
        ArrayList<Goal> regularAchievedGoals = getMostlyRegularAchievedGoalsArrayList();
        float result = 0f;
        for (Goal goal : regularAchievedGoals) {
            result += (100 - Math.abs(goal.getProgress() - 100)) / 20f;
        }
        result /= regularAchievedGoals.size();
        return result;
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

    public ArrayList<Goal> getPossibleParentGoalsArrayListOf(ArrayList<Goal> goals) {
        ArrayList<Goal> possibleParentGoals = new ArrayList<>();
        boolean candidateIsValid = true;
        for (Goal candidate : getActiveGoalsArrayList()) {
            for (Goal goal : goals) {
                if (candidate.getName().equals(goal.getName())) {
                    candidateIsValid = false;
                    break;
                }
            }
            if (candidateIsValid) {
                possibleParentGoals.add(candidate);
            } else {
                candidateIsValid = true;
            }
        }
        return possibleParentGoals;
    }

    public Cursor getPossibleParentGoalsCursor(ArrayList<Goal> goals) {
        StringBuilder goalsNames = new StringBuilder("(");
        for (Goal goal : goals) {
            if (goals.indexOf(goal) >= 1) {
                goalsNames.append(", ");
            }
            goalsNames.append(goal.getName());
        }
        goalsNames.append(")");
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + COLUMN_GOAL_ACHIEVED + " = 0" + " AND " + GoalEntry.COLUMN_GOAL_NAME + " NOT IN " + goalsNames;
        return sQLiteDatabase.rawQuery(query, null);
    }

    public long getGoalCountedTime(String goalName) {
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + GoalEntry.COLUMN_GOAL_NAME + " = '" + goalName + "'";
        Cursor cursor = sQLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        long result = cursor.getLong(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_COUNTED_TIME));
        cursor.close();
        return result;
    }

    public long getGoalEstimatedTime(String goalName) {
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + GoalEntry.COLUMN_GOAL_NAME + " = '" + goalName + "'";
        Cursor cursor = sQLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        long result = cursor.getLong(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_ESTIMATED_TIME));
        cursor.close();
        return result;
    }

    public long getGoalCountedPomodoro(String goalName) {
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + GoalEntry.COLUMN_GOAL_NAME + " = '" + goalName + "'";
        Cursor cursor = sQLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        long result = cursor.getLong(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_COUNTED_POMODORO));
        cursor.close();
        return result;
    }

    public long getAllTimePomodoroCount() {
        ArrayList<Goal> goals = getAllGoalsArrayList();
        long result = 0;
        for (Goal goal : goals) {
            result += goal.getPomodoroCounted();
        }
        return result;
    }

    public void addGoal(Goal goal) {
        if (!doesGoalExist(goal.getName()) && goal.getName().trim().length() != 0 && goal.getTimeEstimated() != 0) {
            String name = goal.getName();
            String description = PublicMethods.getValueOrDefault(goal.getDescription(), "");
            String parent = PublicMethods.getValueOrDefault(goal.getParentGoal(), "");
            int timeCounted = PublicMethods.getValueOrDefault(goal.getTimeCounted(), 0);
            int timeEstimated = PublicMethods.getValueOrDefault(goal.getTimeEstimated(), 100);
            int pomodoroCounted = PublicMethods.getValueOrDefault(goal.getPomodoroCounted(), 0);
            int difficulty = PublicMethods.getValueOrDefault(goal.getDifficulty(), 0);
            int evolving = PublicMethods.getValueOrDefault(goal.getEvolving(), 0);
            int satisfaction = goal.getSatisfaction();
            boolean achieved = PublicMethods.getValueOrDefault(goal.isAchieved(), false);
            ArrayList<String> tagsArray = PublicMethods.getValueOrDefault(goal.getTagsAsArrayList(), new ArrayList<>());
            String finishDate = PublicMethods.getValueOrDefault(goal.getFinishDate(), "");

            StringBuilder tags = new StringBuilder();
            for (int i = 0; i < tagsArray.size(); i++) {
                if (i > 0) {
                    tags.append(",");
                }
                tags.append(tagsArray.get(i));
            }

            //TODO to edit db columns edit here


            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_GOAL_NAME, name);
            contentValues.put(COLUMN_GOAL_DESCRIPTION, description);
            contentValues.put(COLUMN_GOAL_PARENT, parent);
            contentValues.put(COLUMN_GOAL_COUNTED_TIME, timeCounted);
            contentValues.put(COLUMN_GOAL_ESTIMATED_TIME, timeEstimated);
            contentValues.put(COLUMN_GOAL_COUNTED_POMODORO, pomodoroCounted);
            contentValues.put(COLUMN_GOAL_DIFFICULTY, difficulty);
            contentValues.put(COLUMN_GOAL_EVOLVING, evolving);
            contentValues.put(COLUMN_GOAL_SATISFACTION, satisfaction);
            contentValues.put(COLUMN_GOAL_ACHIEVED, achieved);
            contentValues.put(COLUMN_GOAL_TAGS, tags.toString());
            contentValues.put(COLUMN_GOAL_FINISH_DATE, finishDate);
            sQLiteDatabase.insert(TABLE_NAME, null, contentValues);

            HashMap<String, Object> goalData = new HashMap<>();
            goalData.put(COLUMN_GOAL_NAME, name);
            goalData.put(COLUMN_GOAL_DESCRIPTION, description);
            goalData.put(COLUMN_GOAL_PARENT, parent);
            goalData.put(COLUMN_GOAL_COUNTED_TIME, timeCounted);
            goalData.put(COLUMN_GOAL_ESTIMATED_TIME, timeEstimated);
            goalData.put(COLUMN_GOAL_COUNTED_POMODORO, pomodoroCounted);
            goalData.put(COLUMN_GOAL_DIFFICULTY, difficulty);
            goalData.put(COLUMN_GOAL_EVOLVING, evolving);
            goalData.put(COLUMN_GOAL_SATISFACTION, satisfaction);
            goalData.put(COLUMN_GOAL_ACHIEVED, achieved);
            goalData.put(COLUMN_GOAL_TAGS, tags.toString());
            goalData.put(COLUMN_GOAL_FINISH_DATE, finishDate);
            goalsDBReference.document(name).set(goalData);

        }
    }

    public void setParentToGoals(Goal parent, ArrayList<Goal> goalsList) {
        StringBuilder goalsBuilder = new StringBuilder("(");
        for (Goal goal : goalsList) {
            if (goalsList.indexOf(goal) >= 1) {
                goalsBuilder.append(", ");
            }
            goalsBuilder.append(goal.getName());
        }
        goalsBuilder.append(")");
//        String query = "UPDATE " + GoalEntry.TABLE_NAME +
//                " SET " + GoalEntry.COLUMN_GOAL_PARENT + "= " + parent.getName() +
//                " WHERE " + GoalEntry.COLUMN_GOAL_NAME + " IN " + goals.toString();
        ArrayList<String> goalsNameList = new ArrayList<>();
        for (Goal goal : goalsList) {
            goalsNameList.add(goal.getName());

            HashMap<String, Object> parentData = new HashMap<>();
            parentData.put(COLUMN_GOAL_PARENT, parent.getName());
            goalsDBReference.document(goal.getName()).update(parentData);
        }
        String[] goals = goalsNameList.toArray(new String[0]);
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_GOAL_PARENT, parent.getName());
        StringBuilder inClause = new StringBuilder(" IN (?");
        for (int i = 0; i < goals.length - 1; i++) {
            inClause.append(", ?");
        }
        inClause.append(")");


        sQLiteDatabase.update(TABLE_NAME, cv, COLUMN_GOAL_NAME + inClause.toString(), goals);
    }

    public void removeGoal(Goal goal) {
        if (doesGoalExist(goal.getName())) {
            sQLiteDatabase.delete(TABLE_NAME,
                    COLUMN_GOAL_NAME + "= '" + goal.getName() + "'",
                    null);
            goalsDBReference.document(goal.getName()).delete();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void progressGoal(String goalName, long secondsProgressed) {
        String updateGoalProgress = "UPDATE " + TABLE_NAME +
                " SET " + COLUMN_GOAL_COUNTED_TIME + " = '" + (getGoalCountedTime(goalName) + secondsProgressed) +
                "' WHERE " + COLUMN_GOAL_NAME + " = '" + goalName + "'";
        sQLiteDatabase.execSQL(updateGoalProgress);
        statisticsDB.addHourlySecondsOfWork((int) secondsProgressed);
        statisticsDB.addCurrentHourNeurons();
        HashMap<String, Object> progressData = new HashMap<>();
        progressData.put(COLUMN_GOAL_COUNTED_TIME, getGoalCountedTime(goalName) + secondsProgressed);
        goalsDBReference.document(goalName).update(progressData);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void pomodoroProgressGoal(String goalName) {
        progressGoal(goalName, PrefUtil.getPomodoroLength() * 60);

        String updateGoalPomodoro = "UPDATE " + TABLE_NAME +
                " SET " + COLUMN_GOAL_COUNTED_POMODORO + " = '" + (getGoalCountedPomodoro(goalName) + 1) +
                "' WHERE " + COLUMN_GOAL_NAME + " = '" + goalName + "'";

        sQLiteDatabase.execSQL(updateGoalPomodoro);


        HashMap<String, Object> pomodoroData = new HashMap<>();
        pomodoroData.put(COLUMN_GOAL_COUNTED_POMODORO, getGoalCountedPomodoro(goalName) + 1);
        goalsDBReference.document(goalName).update(pomodoroData);
    }

    public void editGoal(Goal goal, String newName, String newDescription, ArrayList<Goal> removedSubGoals) {
        boolean wasNameChanged = !goal.getName().equals(newName);
        if (wasNameChanged) {

            String updateParentName = "UPDATE " + TABLE_NAME +
                    " SET " + COLUMN_GOAL_PARENT + " = '" + newName +
                    "' WHERE " + COLUMN_GOAL_PARENT + " = '" + goal.getName() + "'";

            String updateGoalName = "UPDATE " + TABLE_NAME +
                    " SET " + COLUMN_GOAL_NAME + " = '" + newName +
                    "' WHERE " + COLUMN_GOAL_NAME + " = '" + goal.getName() + "'";


            sQLiteDatabase.execSQL(updateParentName);
            sQLiteDatabase.execSQL(updateGoalName);

            goalsDBReference.document(goal.getName()).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Map<String, Object> data = documentSnapshot.getData();
                    goalsDBReference.document(newName).set(data).addOnSuccessListener(unused -> goalsDBReference.document(goal.getName()).delete());
                }
            });

            for (Goal currentGoal : getAllGoalsArrayList()) {
                goalsDBReference.document(currentGoal.getName()).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null && documentSnapshot.exists() && documentSnapshot.getString(COLUMN_GOAL_PARENT).equals(goal.getName())) {
                        HashMap<String, Object> parentData = new HashMap<>();
                        parentData.put(COLUMN_GOAL_PARENT, newName);
                        goalsDBReference.document(currentGoal.getName()).update(parentData);
                    }
                });
            }
        }
        String updateGoalDescription = "UPDATE " + TABLE_NAME +
                " SET " + COLUMN_GOAL_DESCRIPTION + " = '" + newDescription +
                "' WHERE " + COLUMN_GOAL_NAME + " = '" + newName + "'";

        sQLiteDatabase.execSQL(updateGoalDescription);

        HashMap<String, Object> descriptionData = new HashMap<>();
        descriptionData.put(COLUMN_GOAL_DESCRIPTION, newDescription);
        goalsDBReference.document(newName).update(descriptionData);


        if (!(removedSubGoals.isEmpty())) {
            for (Goal removedSubGoal : removedSubGoals) {
                String subGoalsQuery = "UPDATE " + TABLE_NAME +
                        " SET " +
                        COLUMN_GOAL_PARENT + " = ''" +
                        " WHERE " +
                        COLUMN_GOAL_NAME + " = '" + removedSubGoal.getName() +
                        "' AND " +
                        COLUMN_GOAL_PARENT + " = '" + goal.getName() + "'";
                sQLiteDatabase.execSQL(subGoalsQuery);

                HashMap<String, Object> removedSubgoalData = new HashMap<>();
                removedSubgoalData.put(COLUMN_GOAL_PARENT, "");
                goalsDBReference.document(removedSubGoal.getName()).update(removedSubgoalData);
            }
        }

    }

    public ArrayList<String> getAllTags() {
        ArrayList<String> tagsList = new ArrayList<>();

        String query = "SELECT DISTINCT " + COLUMN_GOAL_TAGS + " FROM " + TABLE_NAME +
                " WHERE " + COLUMN_GOAL_TAGS + " NOT IN ('')";

        Cursor allTagsCursor = sQLiteDatabase.rawQuery(query, null);

        for (allTagsCursor.moveToFirst(); !allTagsCursor.isAfterLast(); allTagsCursor.moveToNext()) {
            tagsList.addAll(Arrays.asList(allTagsCursor.getString(allTagsCursor.getColumnIndexOrThrow(COLUMN_GOAL_TAGS)).split(",")));
        }

        allTagsCursor.close();

        return tagsList;
    }

    /**
     * sets a goal to achieved (achieved = 1) with it's new arguments (difficulty, evolving and satisfaction).
     *
     * @param goal         is the goal that's finishing.
     * @param difficulty   is the level of difficulty chosen by the user to insert.
     * @param evolving     is the level of evolving chosen by the user to insert.
     * @param satisfaction is the level of satisfaction chosen by the user to insert.
     * @param tags         are the tags the finished goal is going to be under.
     */
    public void finishGoal(Goal goal, int difficulty, int evolving, int satisfaction, String tags) {
        String query = "UPDATE " + GoalEntry.TABLE_NAME +
                " SET " +
                COLUMN_GOAL_ACHIEVED + " = '" + 1 +
                "', " +
                COLUMN_GOAL_DIFFICULTY + " = '" + difficulty +
                "', " +
                COLUMN_GOAL_EVOLVING + " = '" + evolving +
                "', " +
                COLUMN_GOAL_SATISFACTION + " = '" + satisfaction +
                "', " +
                COLUMN_GOAL_FINISH_DATE + " = '" + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) +
                "', " +
                COLUMN_GOAL_TAGS + " = '" + tags +
                "' WHERE " +
                COLUMN_GOAL_NAME + " = '" + goal.getName() + "'";
        sQLiteDatabase.execSQL(query);

        HashMap<String, Object> finishData = new HashMap<>();
        finishData.put(COLUMN_GOAL_ACHIEVED, true);
        finishData.put(COLUMN_GOAL_DIFFICULTY, difficulty);
        finishData.put(COLUMN_GOAL_EVOLVING, evolving);
        finishData.put(COLUMN_GOAL_SATISFACTION, satisfaction);
        finishData.put(COLUMN_GOAL_FINISH_DATE, new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        finishData.put(COLUMN_GOAL_TAGS, tags);
        goalsDBReference.document(goal.getName()).update(finishData);
    }

    public int getActiveGoalsCount() {
        return getActiveGoalsArrayList().size();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<Goal> getMonthlyAchievedGoals() {
        ArrayList<Goal> achievedGoals = getAchievedGoalsArrayList();
        ArrayList<Goal> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
        int nowMonth = formatter.parseLocalDate(new SimpleDateFormat("dd/MM/yyyy").format(new Date())).getMonthOfYear();
        int nowYear = formatter.parseLocalDate(new SimpleDateFormat("dd/MM/yyyy").format(new Date())).getYear();
        for (Goal achievedGoal : achievedGoals) {
            int goalMonth = formatter.parseLocalDate(achievedGoal.getFinishDate()).getMonthOfYear();
            int goalYear = formatter.parseLocalDate(achievedGoal.getFinishDate()).getYear();
            if (nowMonth == goalMonth && nowYear == goalYear) {
                result.add(achievedGoal);
            }
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getMonthlyDifficultiesAverage() {
        ArrayList<Goal> monthlyAchievedGoals = getMonthlyAchievedGoals();
        if (monthlyAchievedGoals.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (Goal monthlyAchievedGoal : monthlyAchievedGoals) {
            result += monthlyAchievedGoal.getDifficulty();
        }
        return result / monthlyAchievedGoals.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public double getUserNeurons() {
        try {
            double monthlyAchievedGoals = getMonthlyAchievedGoals().size(),
                    sumOfGoals = (getMonthlyAchievedGoals().size() + getActiveGoalsCount()),
                    monthlyDifficultyAverage = getMonthlyDifficultiesAverage(),
                    monthlyMinutesOfWork = statisticsDB.getCurrentMonthMinutesOfWork();
            double result = ((monthlyAchievedGoals / sumOfGoals) * (monthlyDifficultyAverage / 3.0)) * (monthlyMinutesOfWork / sumOfGoals) * 10;
            statisticsDB.payMonthlyNeurons(LocalDate.now().getMonth(), Year.of(LocalDate.now().getYear()));
            Log.d(TAG, "getUserNeurons: Hi");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Helper function that parses a given table into a string
     * and returns it for easy printing. The string consists of
     * the table name and then each row is iterated through with
     * column_name: value pairs printed out.
     *
     * @param tableName the the name of the table to parse
     * @return the table tableName as a string
     */
    public String getTableAsString(String tableName) {
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows = sQLiteDatabase.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst()) {
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name : columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndexOrThrow(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }

        return tableString;
    }

    public void clearDB(){
        String query = "DELETE FROM " + TABLE_NAME;
        sQLiteDatabase.execSQL(query);
    }

}