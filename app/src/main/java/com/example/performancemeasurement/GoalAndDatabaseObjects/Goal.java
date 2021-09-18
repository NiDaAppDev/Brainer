package com.example.performancemeasurement.GoalAndDatabaseObjects;

public class Goal {
    private boolean achieved;

    private String description;

    private String name;

    private String parentGoal;

    private int timeCounted;

    private int timeEstimated;

    private boolean expanded;

    public Goal(String name, String description) {
        this.name = name;
        this.description = description;
        this.parentGoal = "";
        this.timeCounted = 0;
        this.timeEstimated = 100;
        this.achieved = false;
    }

    public Goal(String name, String description, String parentGoal, int timeCounted, int timeEstimated, boolean achieved) {
        this.name = name;
        this.description = description;
        this.parentGoal = parentGoal;
        this.timeCounted = timeCounted;
        this.timeEstimated = timeEstimated;
        this.achieved = achieved;
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