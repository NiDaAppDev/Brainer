package com.nidaappdev.brainer.databaseObjects;

import android.provider.BaseColumns;

public class StatisticsContract {
    private StatisticsContract() {
    }
    public static final class StatisticsEntry implements BaseColumns {
        public static final String COLUMN_DATE = "date";

        public static final String COLUMN_HOUR = "hour";

        public static final String COLUMN_HOURLY_SECONDS_OF_WORK = "hourlySecondsOfWork";

        public static final String COLUMN_HOURLY_NEURONS = "hourlyNeurons";

        public static final String COLUMN_PAYED_MONTHLY_NEURONS = "payedMonthlyNeurons";

        public static final String TABLE_NAME = "statistics";
    }

}
