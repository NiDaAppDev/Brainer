package com.nidaappdev.brainer.customObjects;

import android.content.Context;

import com.nidaappdev.brainer.databaseObjects.GoalDBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Goal {
    private static GoalDBHelper db;

    private boolean achieved;

    private String description;

    private String name;

    private String parentGoal;

    private String startDate;

    private ArrayList<String> tags;

    private String finishDate;

    private int timeCounted;

    private int timeEstimated;

    private int pomodoroCounted;

    private int pomodoroCountedTime;

    private int difficulty;

    private int evolving;

    private int satisfaction;

    private boolean expanded;

    public Goal(String name, String description) {
        this.name = name;
        this.description = description;
        this.parentGoal = "";
        this.startDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        this.timeCounted = 0;
        this.timeEstimated = 100;
        this.pomodoroCounted = 0;
        this.pomodoroCountedTime = 0;
        this.achieved = false;
        this.tags = new ArrayList<>();
        this.finishDate = "";
    }

    public Goal(String name, String description, int timeEstimated) {
        this.name = name;
        this.description = description;
        this.parentGoal = "";
        this.startDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        this.timeCounted = 0;
        this.timeEstimated = timeEstimated;
        this.pomodoroCounted = 0;
        this.pomodoroCountedTime = 0;
        this.achieved = false;
        this.tags = new ArrayList<>();
        this.finishDate = "";
    }

    public Goal(String name, String description, String parentGoal, int timeCounted, int timeEstimated, int pomodoroCounted, int pomodoroCountedTime, int difficulty, int evolving, int satisfaction, boolean achieved, ArrayList<String> tags, String finishDate) {
        this.name = name;
        this.description = description;
        this.parentGoal = parentGoal;
        this.timeCounted = timeCounted;
        this.timeEstimated = timeEstimated;
        this.pomodoroCounted = pomodoroCounted;
        this.pomodoroCountedTime = pomodoroCountedTime;
        this.difficulty = difficulty;
        this.evolving = evolving;
        this.satisfaction = satisfaction;
        this.achieved = achieved;
        this.tags = tags;
        this.finishDate = finishDate;
    }

    public String getDescription() {
        return this.description;
    }

    public String getName() {
        return this.name;
    }

    public String getParentGoal() {
        return this.parentGoal;
    }

    public String getStartDate(Context context) {
        db = new GoalDBHelper(context);
        return db.getGoalStartDate(name);
    }

    public int getTimeCounted() {
        return this.timeCounted;
    }

    public int getTimeEstimated() {
        return this.timeEstimated;
    }

    public int getPomodoroCounted() {
        return pomodoroCounted;
    }

    public int getPomodoroCountedTime() {
        return pomodoroCountedTime;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getEvolving() {
        return evolving;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public ArrayList<String> getTagsAsArrayList() {
        return tags;
    }

    public String[] getTagsAsArray() {
        return tags.toArray(new String[0]);
    }

    public String getFinishDate() {
        return finishDate;
    }

    public boolean isAchieved() {
        return this.achieved;
    }

    public void setAchieved(boolean achieved) {
        this.achieved = achieved;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParentGoal(String parentGoal) {
        this.parentGoal = parentGoal;
    }

    public void setTimeCounted(int timeCounted) {
        this.timeCounted = timeCounted;
    }

    public void setTimeEstimated(int timeEstimated) {
        this.timeEstimated = timeEstimated;
    }

    public void setPomodoroCounted(int pomodoroCounted) {
        this.pomodoroCounted = pomodoroCounted;
    }

    public void setPomodoroCountedTime(int pomodoroCountedTime) {
        this.pomodoroCountedTime = pomodoroCountedTime;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setEvolving(int evolving) {
        this.evolving = evolving;
    }

    public void setSatisfaction(int satisfaction) {
        this.satisfaction = satisfaction;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public int getProgress() {
        return timeCounted * 100 / timeEstimated;
    }

    public String toString() {
        return name;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }
}