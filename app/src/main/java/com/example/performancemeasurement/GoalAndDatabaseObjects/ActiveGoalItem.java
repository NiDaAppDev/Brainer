package com.example.performancemeasurement.GoalAndDatabaseObjects;


import java.util.ArrayList;

public class ActiveGoalItem {
    private Goal goal;

    public ActiveGoalItem(Goal paramGoal) {
        this.goal = paramGoal;
    }

    public Goal getGoal() {
        return this.goal;
    }

    public int indexOfGoalInItemList(ArrayList<ActiveGoalItem> paramArrayList, boolean paramBoolean) {
        for (ActiveGoalItem activeGoalNormalItem : paramArrayList) {
            if ((paramBoolean && activeGoalNormalItem.getGoal().getName().equals(this.goal.getParentGoal())) || activeGoalNormalItem.getGoal().getName().equals(this.goal.getName()))
                return paramArrayList.indexOf(activeGoalNormalItem);
        }
        return -1;
    }

    public void setGoal(Goal paramGoal) {
        this.goal = paramGoal;
    }
}