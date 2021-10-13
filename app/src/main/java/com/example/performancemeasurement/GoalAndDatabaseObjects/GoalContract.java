package com.example.performancemeasurement.GoalAndDatabaseObjects;

import android.provider.BaseColumns;

public class GoalContract {
    private GoalContract() {};
    public static final class GoalEntry implements BaseColumns {
        public static final String COLUMN_GOAL_ACHIEVED = "achieved";

        public static final String COLUMN_GOAL_FINISH_DATE = "finishDate";

        public static final String COLUMN_GOAL_COUNTED_TIME = "countedTime";

        public static final String COLUMN_GOAL_DESCRIPTION = "description";

        public static final String COLUMN_GOAL_ESTIMATED_TIME = "estimatedTime";

        public static final String COLUMN_GOAL_DIFFICULTY = "difficulty";

        public static final String COLUMN_GOAL_EVOLVING = "evolving";

        public static final String COLUMN_GOAL_SATISFACTION = "satisfaction";

        public static final String COLUMN_GOAL_TAG = "tag";

        public static final String COLUMN_GOAL_NAME = "name";

        public static final String COLUMN_GOAL_PARENT = "parent";

        public static final String COLUMN_GOAL_TIMESTAMP = "timeStamp";

        public static final String TABLE_NAME = "goalsList";

        //TODO to edit db columns edit here

    }
}

