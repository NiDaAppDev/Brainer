package com.example.performancemeasurement.GoalAndDatabaseObjects;

import android.provider.BaseColumns;

public class StatisticsContract {
    private StatisticsContract() {
    }
    public static final class StatisticsEntry implements BaseColumns {
        public static final String COLUMN_MONTH_OF_YEAR = "monthOfYear";

        public static final String COLUMN_MONTHLY_SECONDS_OF_WORK = "monthlySecondsOfWork";

        public static final String TABLE_NAME = "statistics";
    }

}
