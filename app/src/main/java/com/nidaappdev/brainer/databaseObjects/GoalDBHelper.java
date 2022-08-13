package com.nidaappdev.brainer.databaseObjects;


import static com.nidaappdev.brainer.App.goalsDBReference;
import static com.nidaappdev.brainer.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_ACHIEVED;
import static com.nidaappdev.brainer.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_COUNTED_POMODORO;
import static com.nidaappdev.brainer.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_COUNTED_POMODORO_TIME;
import static com.nidaappdev.brainer.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_COUNTED_TIME;
import static com.nidaappdev.brainer.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_DESCRIPTION;
import static com.nidaappdev.brainer.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_DIFFICULTY;
import static com.nidaappdev.brainer.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_ESTIMATED_TIME;
import static com.nidaappdev.brainer.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_EVOLVING;
import static com.nidaappdev.brainer.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_FINISH_DATE;
import static com.nidaappdev.brainer.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_NAME;
import static com.nidaappdev.brainer.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_PARENT;
import static com.nidaappdev.brainer.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_SATISFACTION;
import static com.nidaappdev.brainer.databaseObjects.GoalContract.GoalEntry.COLUMN_GOAL_TAGS;
import static com.nidaappdev.brainer.databaseObjects.GoalContract.GoalEntry.TABLE_NAME;
import static com.nidaappdev.brainer.util.Constants.achievementsDescriptions;
import static com.nidaappdev.brainer.util.Constants.achievementsIconsRes;
import static com.nidaappdev.brainer.util.Constants.achievementsNames;
import static com.nidaappdev.brainer.util.Constants.achievementsRequirements;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.nidaappdev.brainer.App;
import com.nidaappdev.brainer.customObjects.Achievement;
import com.nidaappdev.brainer.customObjects.Goal;
import com.nidaappdev.brainer.databaseObjects.GoalContract.GoalEntry;
import com.nidaappdev.brainer.publicClassesAndInterfaces.PublicMethods;
import com.nidaappdev.brainer.util.PrefUtil;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GoalDBHelper extends SQLiteOpenHelper {
    public static final String GOALS_DATABASE_NAME = "goalsList.db";

    public static final int DATABASE_VERSION = 1;

    private static SQLiteDatabase sQLiteDatabase;

    private static final StatisticsDBHelper statisticsDB = new StatisticsDBHelper(App.appContext);

    private static Cursor cursor;

    public GoalDBHelper(Context context) {
        super(context, GOALS_DATABASE_NAME, null, DATABASE_VERSION);
        sQLiteDatabase = getWritableDatabase();
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
                COLUMN_GOAL_COUNTED_POMODORO_TIME + " INTEGER NOT NULL, " +
                COLUMN_GOAL_DIFFICULTY + " INTEGER NOT NULL, " +
                GoalEntry.COLUMN_GOAL_EVOLVING + " INTEGER NOT NULL, " +
                GoalEntry.COLUMN_GOAL_SATISFACTION + " INTEGER NOT NULL, " +
                COLUMN_GOAL_ACHIEVED + " BOOLEAN NOT NULL, " +
                GoalEntry.COLUMN_GOAL_TAGS + " TEXT NOT NULL, " +
                GoalEntry.COLUMN_GOAL_FINISH_DATE + " VARCHAR(10) NOT NULL, " +
                GoalEntry.COLUMN_GOAL_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
        db.execSQL(SQL_CREATE_GOALS_LIST_TABLE);
        sQLiteDatabase = db;
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
        cursor = sQLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public String getGoalStartDate(String goalName) {
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + GoalEntry.COLUMN_GOAL_NAME + " = '" + goalName + "'";
        cursor = sQLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        String result = PublicMethods.formatDateTime(cursor.getString(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_TIMESTAMP)));
        cursor.close();
        return result;
    }

    public String getGoalFinishDate(String goalName) {
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + GoalEntry.COLUMN_GOAL_NAME + " = '" + goalName + "'";
        cursor = sQLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        String result = cursor.getString(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_FINISH_DATE));
        cursor.close();
        return result;
    }

    public boolean doesActiveGoalNameAlreadyExist(String name, String editedGoalName) {
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + GoalEntry.COLUMN_GOAL_NAME + " = '" + name + "' AND " + COLUMN_GOAL_ACHIEVED + " = 0";
        cursor = sQLiteDatabase.rawQuery(query, null);
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


        cursor = getWritableDatabase().query(GoalEntry.TABLE_NAME, null, null, null, null, null, GoalEntry.COLUMN_GOAL_NAME + " ASC");
        ArrayList<Goal> goals = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_NAME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_DESCRIPTION));
            String parent = cursor.getString(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_PARENT));
            int timeCounted = cursor.getInt(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_COUNTED_TIME));
            int timeEstimated = cursor.getInt(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_ESTIMATED_TIME));
            int pomodoroCounted = cursor.getInt(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_COUNTED_POMODORO));
            int pomodoroCountedTime = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GOAL_COUNTED_POMODORO_TIME));
            int difficulty = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GOAL_DIFFICULTY));
            int evolving = cursor.getInt(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_EVOLVING));
            int satisfaction = cursor.getInt(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_SATISFACTION));
            boolean achieved = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GOAL_ACHIEVED)) > 0;
            ArrayList<String> tags = new ArrayList<>(Arrays.asList(cursor.getString(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_TAGS)).split(",")));
            String finishDate = cursor.getString(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_FINISH_DATE));
            Goal goal = new Goal(name, description, parent, timeCounted, timeEstimated, pomodoroCounted, pomodoroCountedTime, difficulty, evolving, satisfaction, achieved, tags, finishDate);
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

    public boolean isAchieved(String goalName) {
        ArrayList<Goal> achievedGoals = getAchievedGoalsArrayList();
        for (Goal goal : achievedGoals) {
            if (goal.getName().equals(goalName)) {
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isWorkTimeDivisionChartUnlocked() {
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            float dailyMinutesOfWork = statisticsDB.getDailyMinutesOfWork(dayOfWeek);
            if (dailyMinutesOfWork > 0) {
                return true;
            }
        }
        for (Month month : Month.values()) {
            float monthlyMinutesOfWork = statisticsDB.getMonthlyMinutesOfWork(month);
            if (monthlyMinutesOfWork > 0) {
                return true;
            }
        }
        for (float i = 0f; i < 24f; i++) {
            float hourlyMinutesOfWork = statisticsDB.getHourlyMinutesOfWork((int) i);
            if (hourlyMinutesOfWork > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isTimerModeDivisionChartUnlocked() {
        return statisticsDB.getAllTimeMinutesOfWork() > 0;
    }

    public boolean isTimerModeResultsChartUnlocked() {
        return !(Float.isNaN(getPomodoroDifficultyAverage()) && Float.isNaN(getRegularDifficultyAverage()));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isNeuronsProgressChartUnlocked() {
        DateTime dt = new DateTime();
        int todayOfWeek = (dt.getDayOfWeek() % 7);
        int todayOfMonth = dt.getDayOfMonth();
        int nowMonth = dt.getMonthOfYear();
        float nowHour = dt.getHourOfDay();

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            if (dayOfWeek.plus(1).ordinal() <= todayOfWeek) {
                long todayNeurons = statisticsDB.getDayOfThisWeekNeurons(dayOfWeek);
                if (todayNeurons > 0) {
                    return true;
                }
            }
        }
        for (int dayOfMonth = 1; dayOfMonth <= todayOfMonth; dayOfMonth++) {
            long todayNeurons = statisticsDB.getDayOfThisMonthNeurons(dayOfMonth);
            if (todayNeurons > 0) {
                return true;
            }
        }
        for (Month month : Month.values()) {
            long thisMonthsNeurons = statisticsDB.getMonthOfThisYearNeurons(month);
            if (thisMonthsNeurons > 0) {
                return true;
            }
            if (month.equals(Month.of(nowMonth))) {
                break;
            }
        }
        for (float hour = 0f; hour <= nowHour; hour++) {
            long currentHourNeurons = statisticsDB.getHourOfTodayNeurons((int) hour);
            if (currentHourNeurons > 0) {
                return true;
            }
        }

        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isAchievementAchieved(int achievementIndex) {
        switch (achievementIndex) {
            case 0:
                return PrefUtil.finishedTutorial();
            case 1:
                return getAllGoalsArrayList().size() > 0;
            case 2:
                return getAllGoalsArrayList().size() >= 50;
            case 3:
                return getAllTimePomodoroCount() > 0;
            case 4:
                return getAllTimePomodoroCount() >= 10;
            case 5:
                return getAchievedGoalsArrayList().size() > 0;
            case 6:
                return getAchievedGoalsArrayList().size() >= 10;
            case 7:
                return isWorkTimeDivisionChartUnlocked();
            case 8:
                return isTimerModeDivisionChartUnlocked();
            case 9:
                return isTimerModeResultsChartUnlocked();
            case 10:
                return isNeuronsProgressChartUnlocked();
            case 11:
                return statisticsDB.getAllTimeHighestNeurons() >= 1;
            case 12:
                return statisticsDB.getAllTimeHighestNeurons() >= 50;
            case 13:
                return statisticsDB.getAllTimeHighestNeurons() >= 100;
            case 14:
                return statisticsDB.getAllTimeHighestNeurons() >= 500;
            case 15:
                return statisticsDB.getAllTimeHighestNeurons() >= 1000;
            default:
                return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<Achievement> getAchievementsArrayList() {
        ArrayList<Achievement> achievements = new ArrayList<>();
        for (int i = 0; i < achievementsNames.length; i++) {
            Achievement achievement = new Achievement(achievementsNames[i], achievementsDescriptions[i], achievementsRequirements[i], achievementsIconsRes[i], isAchievementAchieved(i));
            achievements.add(achievement);
        }
        return achievements;
    }

    public ArrayList<Goal> getMostlyPomodoroAchievedGoalsArrayList() {
        ArrayList<Goal> achievedGoals = getAchievedGoalsArrayList();
        ArrayList<Goal> mostlyPomodoroAchievedGoals = new ArrayList<>();
        for (Goal goal : achievedGoals) {
            if ((float) goal.getPomodoroCountedTime() / 60f > ((float) goal.getTimeCounted() / 60f) - ((float) goal.getPomodoroCountedTime() / 60f)) {
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

    public ArrayList<Goal> getSubGoalsArrayListOf(String goalName) {
        ArrayList<Goal> subGoals = new ArrayList<>();
        for (Goal subGoal : getAllGoalsArrayList()) {
            if (subGoal.getParentGoal().equals(goalName)) {
                subGoals.add(subGoal);
            }
        }
        return subGoals;
    }

    public boolean isGoalSubGoalOf(String goalName, String parentGoalName) {
        ArrayList<Goal> subgoals = getSubGoalsArrayListOf(parentGoalName);
        for (Goal subgoal : subgoals) {
            if (subgoal.getName().equals(goalName)) {
                return true;
            }
        }
        return false;
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
            goalsNames.append("'").append(goal.getName()).append("'");
        }
        goalsNames.append(")");
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + COLUMN_GOAL_ACHIEVED + " = 0" + " AND " + COLUMN_GOAL_NAME + " NOT IN " + goalsNames;
        return sQLiteDatabase.rawQuery(query, null);
    }

    public long getGoalCountedTime(String goalName) {
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + GoalEntry.COLUMN_GOAL_NAME + " = '" + goalName + "'";
        cursor = sQLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        long result = cursor.getLong(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_COUNTED_TIME));
        cursor.close();
        return result;
    }

    public long getGoalEstimatedTime(String goalName) {
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + GoalEntry.COLUMN_GOAL_NAME + " = '" + goalName + "'";
        cursor = sQLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        long result = cursor.getLong(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_ESTIMATED_TIME));
        cursor.close();
        return result;
    }

    public long getGoalCountedPomodoro(String goalName) {
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + GoalEntry.COLUMN_GOAL_NAME + " = '" + goalName + "'";
        cursor = sQLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        long result = cursor.getLong(cursor.getColumnIndexOrThrow(GoalEntry.COLUMN_GOAL_COUNTED_POMODORO));
        cursor.close();
        return result;
    }

    public long getGoalCountedPomodoroTime(String goalName) {
        String query = "SELECT * FROM " + GoalEntry.TABLE_NAME + " WHERE " + GoalEntry.COLUMN_GOAL_NAME + " = '" + goalName + "'";
        cursor = sQLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        long result = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_GOAL_COUNTED_POMODORO_TIME));
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

    public long getAllTimePomodoroTimeCount() {
        ArrayList<Goal> goals = getAllGoalsArrayList();
        long result = 0;
        for (Goal goal : goals) {
            result += goal.getPomodoroCountedTime();
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
            int pomodoroCountedTime = PublicMethods.getValueOrDefault(goal.getPomodoroCountedTime(), 0);
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
            contentValues.put(COLUMN_GOAL_COUNTED_POMODORO_TIME, pomodoroCountedTime);
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
            goalData.put(COLUMN_GOAL_COUNTED_POMODORO_TIME, pomodoroCountedTime);
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

    public void removeGoal(String goalName) {
        if (doesGoalExist(goalName)) {
            sQLiteDatabase.delete(TABLE_NAME,
                    COLUMN_GOAL_NAME + "= '" + goalName + "'",
                    null);
            goalsDBReference.document(goalName).delete();
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

        String updateGoalPomodoroCount = "UPDATE " + TABLE_NAME +
                " SET " + COLUMN_GOAL_COUNTED_POMODORO + " = '" + (getGoalCountedPomodoro(goalName) + 1) +
                "' WHERE " + COLUMN_GOAL_NAME + " = '" + goalName + "'";

        String updateGoalPomodoroTime = "UPDATE " + TABLE_NAME +
                " SET " + COLUMN_GOAL_COUNTED_POMODORO_TIME + " = '" + (getGoalCountedPomodoroTime(goalName) + (PrefUtil.getPomodoroLength() * 60)) +
                "' WHERE " + COLUMN_GOAL_NAME + " = '" + goalName + "'";

        sQLiteDatabase.execSQL(updateGoalPomodoroCount);
        sQLiteDatabase.execSQL(updateGoalPomodoroTime);

        HashMap<String, Object> pomodoroData = new HashMap<>();
        pomodoroData.put(COLUMN_GOAL_COUNTED_POMODORO, getGoalCountedPomodoro(goalName) + 1);
        pomodoroData.put(COLUMN_GOAL_COUNTED_POMODORO_TIME, getGoalCountedPomodoroTime(goalName) + (PrefUtil.getPomodoroLength() * 60));
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

        removeSubGoalsFromGoal(removedSubGoals, goal.getName());

    }

    public void removeSubGoalsFromGoal(ArrayList<Goal> subgoals, String goalName) {
        if (!(subgoals.isEmpty())) {
            for (Goal removedSubGoal : subgoals) {
                String subGoalsQuery = "UPDATE " + TABLE_NAME +
                        " SET " +
                        COLUMN_GOAL_PARENT + " = ''" +
                        " WHERE " +
                        COLUMN_GOAL_NAME + " = '" + removedSubGoal.getName() +
                        "' AND " +
                        COLUMN_GOAL_PARENT + " = '" + goalName + "'";
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

        cursor = sQLiteDatabase.rawQuery(query, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            tagsList.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GOAL_TAGS)).split(",")));
        }

        cursor.close();

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
                COLUMN_GOAL_FINISH_DATE + " = '" +  new SimpleDateFormat("dd/MM/yyyy").format(new Date()) +
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
        cursor = sQLiteDatabase.rawQuery("SELECT * FROM " + tableName, null);
        if (cursor.moveToFirst()) {
            String[] columnNames = cursor.getColumnNames();
            do {
                for (String name : columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            cursor.getString(cursor.getColumnIndexOrThrow(name)));
                }
                tableString += "\n";

            } while (cursor.moveToNext());
        }
        cursor.close();
        return tableString;
    }

    public void clearDB() {
        String query = "DELETE FROM " + TABLE_NAME;
        sQLiteDatabase.execSQL(query);
    }

    public void closeStatisticsDB() {
        statisticsDB.close();
    }

    public void deleteDB(Context context) {
        if (sQLiteDatabase.isOpen()) {
            sQLiteDatabase.close();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        this.close();
        statisticsDB.closeGoalDB();
        context.deleteDatabase(GOALS_DATABASE_NAME);
    }

    public GoalDBHelper recreateDB(Context context) {
        deleteDB(context);
        sQLiteDatabase = getWritableDatabase();
        return new GoalDBHelper(context);
    }
}