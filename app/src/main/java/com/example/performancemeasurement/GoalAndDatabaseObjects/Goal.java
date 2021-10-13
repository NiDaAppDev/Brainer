package com.example.performancemeasurement.GoalAndDatabaseObjects;

public class Goal {
    private boolean achieved;

    private String description;

    private String name;

    private String parentGoal;

    private String tag;

    private String finishDate;

    private int timeCounted;

    private int timeEstimated;

    private int difficulty;

    private int evolving;

    private int satisfaction;

    private boolean expanded;

    public Goal(String name, String description) {
        this.name = name;
        this.description = description;
        this.parentGoal = "";
        this.timeCounted = 0;
        this.timeEstimated = 100;
        this.achieved = false;
        this.tag = "";
        this.finishDate = "";
    }

    public Goal(String name, String description, String parentGoal, int timeCounted, int timeEstimated, int difficulty, int evolving, int satisfaction, boolean achieved, String tag, String finishDate) {
        this.name = name;
        this.description = description;
        this.parentGoal = parentGoal;
        this.timeCounted = timeCounted;
        this.timeEstimated = timeEstimated;
        this.difficulty = difficulty;
        this.evolving = evolving;
        this.satisfaction = satisfaction;
        this.achieved = achieved;
        this.tag = tag;
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

    public int getTimeCounted() {
        return this.timeCounted;
    }

    public int getTimeEstimated() {
        return this.timeEstimated;
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

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setEvolving(int evolving) {
        this.evolving = evolving;
    }

    public void setSatisfaction(int satisfaction) {
        this.satisfaction = satisfaction;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public int getProgress(){
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