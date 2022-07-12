package com.nidaappdev.performancemeasurement.databaseObjects;

import static com.nidaappdev.performancemeasurement.App.statisticsDBReference;
import static com.nidaappdev.performancemeasurement.databaseObjects.StatisticsContract.StatisticsEntry.COLUMN_DATE;
import static com.nidaappdev.performancemeasurement.databaseObjects.StatisticsContract.StatisticsEntry.COLUMN_HOUR;
import static com.nidaappdev.performancemeasurement.databaseObjects.StatisticsContract.StatisticsEntry.COLUMN_HOURLY_NEURONS;
import static com.nidaappdev.performancemeasurement.databaseObjects.StatisticsContract.StatisticsEntry.COLUMN_HOURLY_SECONDS_OF_WORK;
import static com.nidaappdev.performancemeasurement.databaseObjects.StatisticsContract.StatisticsEntry.COLUMN_PAYED_MONTHLY_NEURONS;
import static com.nidaappdev.performancemeasurement.databaseObjects.StatisticsContract.StatisticsEntry.TABLE_NAME;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.nidaappdev.performancemeasurement.App;
import com.nidaappdev.performancemeasurement.databaseObjects.StatisticsContract.StatisticsEntry;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class StatisticsDBHelper extends SQLiteOpenHelper {

    public static final String STATISTICS_DATABASE_NAME = "statistics.db";

    public static final int DATABASE_VERSION = 1;

    private final SQLiteDatabase sQLiteDatabase = getWritableDatabase();

    private static final GoalDBHelper goalDB = new GoalDBHelper(App.appContext);

    public StatisticsDBHelper(Context context) {
        super(context, STATISTICS_DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_STATISTICS_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                StatisticsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                StatisticsEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                StatisticsEntry.COLUMN_HOUR + " INTEGER NOT NULL, " +
                StatisticsEntry.COLUMN_HOURLY_SECONDS_OF_WORK + " INTEGER NOT NULL, " +
                StatisticsEntry.COLUMN_HOURLY_NEURONS + " REAL NOT NULL, " +
                StatisticsEntry.COLUMN_PAYED_MONTHLY_NEURONS + " BOOLEAN NOT NULL)";
        db.execSQL(SQL_CREATE_STATISTICS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GoalContract.GoalEntry.TABLE_NAME);
        onCreate(db);
    }

    private boolean doesNowExist() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateTime dateTime = new DateTime();
        int hour = dateTime.getHourOfDay();
        String query = "SELECT * FROM " + TABLE_NAME
                + " WHERE " + StatisticsEntry.COLUMN_DATE + " = '" + simpleDateFormat.format(new Date())
                + "' AND " + StatisticsEntry.COLUMN_HOUR + " = " + hour;
        Cursor cursor = sQLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    private boolean doesHourExist(int hour) {
        String query = "SELECT * FROM " + TABLE_NAME
                + " WHERE " + StatisticsEntry.COLUMN_HOUR + " = " + hour;
        Cursor cursor = sQLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean doesDayExist(DayOfWeek dayOfWeek) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String query = "SELECT * FROM " + TABLE_NAME;
        try (Cursor cursor = sQLiteDatabase.rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                LocalDate cursorDate = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_DATE)), dateTimeFormatter);
                if (DayOfWeek.from(cursorDate).equals(dayOfWeek)) {
                    return true;
                }
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean doesMonthExist(Month month) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String query = "SELECT * FROM " + TABLE_NAME;
        try (Cursor cursor = sQLiteDatabase.rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                LocalDate cursorDate = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_DATE)), dateTimeFormatter);
                if (Month.from(cursorDate).equals(month)) {
                    return true;
                }
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean doesCurrentMonthExist() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String query = "SELECT * FROM " + TABLE_NAME;
        try (Cursor cursor = sQLiteDatabase.rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                LocalDate cursorDate = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_DATE)), dateTimeFormatter);
                LocalDate nowDate = LocalDate.parse(simpleDateFormat.format(new Date()), dateTimeFormatter);
                if (YearMonth.from(cursorDate).equals(YearMonth.from(nowDate))) {
                    return true;
                }
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addRecord(String date, int hour) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(StatisticsEntry.COLUMN_DATE, date);
        contentValues.put(StatisticsEntry.COLUMN_HOUR, hour);
        contentValues.put(StatisticsEntry.COLUMN_HOURLY_SECONDS_OF_WORK, 0);
        contentValues.put(StatisticsEntry.COLUMN_HOURLY_NEURONS, 0);
        contentValues.put(StatisticsEntry.COLUMN_PAYED_MONTHLY_NEURONS, false);

        sQLiteDatabase.insert(TABLE_NAME, null, contentValues);

        HashMap<String, Object> statisticsData = new HashMap<>();
        statisticsData.put(StatisticsEntry.COLUMN_DATE, date);
        statisticsData.put(StatisticsEntry.COLUMN_HOUR, hour);
        statisticsData.put(StatisticsEntry.COLUMN_HOURLY_SECONDS_OF_WORK, 0);
        statisticsData.put(StatisticsEntry.COLUMN_HOURLY_NEURONS, 0);
        statisticsData.put(StatisticsEntry.COLUMN_PAYED_MONTHLY_NEURONS, false);
        statisticsDBReference.document(LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy")).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "," + hour).set(statisticsData);
    }

    public void addRecordToDBOnly(String date, int hour, int hourlySecondsOfWork, double hourlyNeurons, boolean payedMonthlyNeurons) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(StatisticsEntry.COLUMN_DATE, date);
        contentValues.put(StatisticsEntry.COLUMN_HOUR, hour);
        contentValues.put(StatisticsEntry.COLUMN_HOURLY_SECONDS_OF_WORK, hourlySecondsOfWork);
        contentValues.put(StatisticsEntry.COLUMN_HOURLY_NEURONS, hourlyNeurons);
        contentValues.put(StatisticsEntry.COLUMN_PAYED_MONTHLY_NEURONS, payedMonthlyNeurons);

        sQLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addNowRecord() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = simpleDateFormat.format(new Date());
        DateTime dateTime = new DateTime();
        int hour = dateTime.getHourOfDay();
        addRecord(date, hour);
    }

    public long getAllTimeMinutesOfWork() {
        String query = "SELECT * FROM " + TABLE_NAME;
        long result = 0;
        try (Cursor cursor = sQLiteDatabase.rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                result += cursor.getInt(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_HOURLY_SECONDS_OF_WORK));
            }
        }
        return result / 60;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public long getCurrentMonthMinutesOfWork() {
        if (doesCurrentMonthExist()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String query = "SELECT * FROM " + TABLE_NAME;
            long result = 0;
            try (Cursor cursor = sQLiteDatabase.rawQuery(query, null)) {
                while (cursor.moveToNext()) {
                    LocalDate cursorDate = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_DATE)), dateTimeFormatter);
                    LocalDate nowDate = LocalDate.parse(simpleDateFormat.format(new Date()), dateTimeFormatter);
                    if (YearMonth.from(cursorDate).equals(YearMonth.from(nowDate))) {
                        result += cursor.getInt(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_HOURLY_SECONDS_OF_WORK));
                    }
                }
            }
            return result / 60;
        }
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getCurrentAllTimeNeurons() {
        String query = "SELECT * FROM " + TABLE_NAME;
        int result = 0;
        try (Cursor cursor = sQLiteDatabase.rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                result += cursor.getInt(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_HOURLY_NEURONS));
            }
        }
        return result;
    }

    public int getAllTimeHighestNeurons() {
        String query = "SELECT * FROM " + TABLE_NAME;
        int sum = 0, highest = 0;
        try(Cursor cursor = sQLiteDatabase.rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                sum += cursor.getInt(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_HOURLY_NEURONS));
                if(sum > highest) {
                    highest = sum;
                }
            }
        }
        return highest;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getCurrentMonthNeurons() {
        if (doesCurrentMonthExist()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String query = "SELECT * FROM " + TABLE_NAME;
            int result = 0;
            try (Cursor cursor = sQLiteDatabase.rawQuery(query, null)) {
                while (cursor.moveToNext()) {
                    LocalDate cursorDate = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_DATE)), dateTimeFormatter);
                    LocalDate nowDate = LocalDate.parse(simpleDateFormat.format(new Date()), dateTimeFormatter);
                    if (YearMonth.from(cursorDate).equals(YearMonth.from(nowDate))) {
                        result += cursor.getInt(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_HOURLY_NEURONS));
                    }
                }
            }
            return result;
        }
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public float getMonthlyMinutesOfWork(Month month) {
        if (doesMonthExist(month)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String query = "SELECT * FROM " + TABLE_NAME;
            float result = 0f;
            try (Cursor cursor = sQLiteDatabase.rawQuery(query, null)) {
                while (cursor.moveToNext()) {
                    LocalDate cursorDate = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_DATE)), dateTimeFormatter);
                    if (Month.from(cursorDate).equals(month)) {
                        result += cursor.getInt(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_HOURLY_SECONDS_OF_WORK));
                    }
                }
            }
            return result / 60f;
        }
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public float getDailyMinutesOfWork(DayOfWeek dayOfWeek) {
        if (doesDayExist(dayOfWeek)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String query = "SELECT * FROM " + TABLE_NAME;
            float result = 0f;
            try (Cursor cursor = sQLiteDatabase.rawQuery(query, null)) {
                while (cursor.moveToNext()) {
                    LocalDate cursorDate = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_DATE)), dateTimeFormatter);
                    if (DayOfWeek.from(cursorDate).equals(dayOfWeek)) {
                        result += cursor.getInt(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_HOURLY_SECONDS_OF_WORK));
                    }
                }
            }
            return result / 60f;
        }
        return 0;
    }

    public float getHourlyMinutesOfWork(int hour) {
        if (doesHourExist(hour)) {
            String query = "SELECT * FROM " + TABLE_NAME
                    + " WHERE " + StatisticsEntry.COLUMN_HOUR + " = " + hour;
            float result = 0f;
            try (Cursor cursor = sQLiteDatabase.rawQuery(query, null)) {
                while (cursor.moveToNext()) {
                    result += cursor.getInt(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_HOURLY_SECONDS_OF_WORK));
                }
            }
            return result / 60f;
        }
        return 0;
    }

    public long getCurrentHourMinutesOfWork() {
        if (doesNowExist()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            DateTime dateTime = new DateTime();
            int hour = dateTime.getHourOfDay();
            String query = "SELECT * FROM " + TABLE_NAME
                    + " WHERE " + StatisticsEntry.COLUMN_DATE + " = '" + simpleDateFormat.format(new Date())
                    + "' AND " + StatisticsEntry.COLUMN_HOUR + " = " + hour;
            Cursor cursor = sQLiteDatabase.rawQuery(query, null);
            cursor.moveToFirst();
            long result = cursor.getInt(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_HOURLY_SECONDS_OF_WORK)) / 60;
            cursor.close();
            return result;
        }
        return 0;
    }

    private long getCurrentHourSecondsOfWork() {
        if (doesNowExist()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            DateTime dateTime = new DateTime();
            int hour = dateTime.getHourOfDay();
            String query = "SELECT * FROM " + TABLE_NAME
                    + " WHERE " + StatisticsEntry.COLUMN_DATE + " = '" + simpleDateFormat.format(new Date())
                    + "' AND " + StatisticsEntry.COLUMN_HOUR + " = " + hour;
            Cursor cursor = sQLiteDatabase.rawQuery(query, null);
            cursor.moveToFirst();
            long result = cursor.getInt(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_HOURLY_SECONDS_OF_WORK));
            cursor.close();
            return result;
        }
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addHourlySecondsOfWork(int secondsOfWork) {
        if (!doesNowExist()) {
            addNowRecord();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = simpleDateFormat.format(new Date());
        DateTime dateTime = new DateTime();
        int hour = dateTime.getHourOfDay();

        String query = "UPDATE " + TABLE_NAME
                + " SET " + COLUMN_HOURLY_SECONDS_OF_WORK + " = " + (getCurrentHourSecondsOfWork() + secondsOfWork)
                + " WHERE " + COLUMN_DATE + " = '" + date
                + "' AND " + COLUMN_HOUR + " = " + hour;

        sQLiteDatabase.execSQL(query);

        HashMap<String, Object> hourlySecondsOfWorkData = new HashMap<>();
        hourlySecondsOfWorkData.put(COLUMN_HOURLY_SECONDS_OF_WORK, getCurrentHourSecondsOfWork());
        statisticsDBReference.document(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "," + hour).update(hourlySecondsOfWorkData);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getMonthOfThisYearNeurons(Month month) {
        if (doesMonthExist(month)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String query = "SELECT * FROM " + TABLE_NAME;
            int result = 0;
            try (Cursor cursor = sQLiteDatabase.rawQuery(query, null)) {
                while (cursor.moveToNext()) {
                    LocalDate cursorDate = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_DATE)), dateTimeFormatter);
                    LocalDate nowDate = LocalDate.parse(simpleDateFormat.format(new Date()), dateTimeFormatter);
                    if (Year.from(cursorDate).equals(Year.from(nowDate)) && Month.from(cursorDate).equals(month)) {
                        result += cursor.getInt(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_HOURLY_NEURONS));
                    }
                }
            }
            return result;
        }
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getDayOfThisMonthNeurons(int dayOfMonth) {
        if (doesCurrentMonthExist()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String query = "SELECT * FROM " + TABLE_NAME;
            int result = 0;
            try (Cursor cursor = sQLiteDatabase.rawQuery(query, null)) {
                while (cursor.moveToNext()) {
                    LocalDate cursorDate = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_DATE)), dateTimeFormatter);
                    LocalDate nowDate = LocalDate.parse(simpleDateFormat.format(new Date()), dateTimeFormatter);
                    if (Year.from(cursorDate).equals(Year.from(nowDate)) && Month.from(cursorDate).equals(Month.from(nowDate)) && cursorDate.getDayOfMonth() == dayOfMonth) {
                        result += cursor.getInt(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_HOURLY_NEURONS));
                    }
                }
            }
            return result;
        }
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getDayOfThisWeekNeurons(DayOfWeek dayOfWeek) {
        if (doesDayExist(dayOfWeek)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String query = "SELECT * FROM " + TABLE_NAME;
            int result = 0;
            try (Cursor cursor = sQLiteDatabase.rawQuery(query, null)) {
                while (cursor.moveToNext()) {
                    LocalDate cursorDate = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_DATE)), dateTimeFormatter);
                    LocalDate nowDate = LocalDate.parse(simpleDateFormat.format(new Date()), dateTimeFormatter);
                    WeekFields weekFields = WeekFields.of(Locale.getDefault());
                    int nowWeek = nowDate.get(weekFields.weekOfWeekBasedYear());
                    int cursorWeek = cursorDate.get(weekFields.weekOfWeekBasedYear());
                    if (cursorWeek == nowWeek && dayOfWeek.equals(DayOfWeek.from(cursorDate))) {
                        result += cursor.getInt(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_HOURLY_NEURONS));
                    }
                }
            }
            return result;
        }
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getHourOfTodayNeurons(int hour) {
        if (doesHourExist(hour)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String query = "SELECT * FROM " + TABLE_NAME
                    + " WHERE " + StatisticsEntry.COLUMN_DATE + " = '" + simpleDateFormat.format(new Date())
                    + "' AND " + StatisticsEntry.COLUMN_HOUR + " = " + hour;
            int result = 0;
            try (Cursor cursor = sQLiteDatabase.rawQuery(query, null)) {
                while (cursor.moveToNext()) {
                    result += cursor.getInt(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_HOURLY_NEURONS));
                }
            }
            return result;
        }
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private double getFirstHourOfMonthNeurons(Month month, Year year) {
        if (doesHourExist(0)) {
            String query = "SELECT * FROM " + TABLE_NAME
                    + " WHERE " + StatisticsEntry.COLUMN_DATE + " = '" + LocalDate.of(year.getValue(), month.getValue(), 1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    + "' AND " + StatisticsEntry.COLUMN_HOUR + " = " + 0;
            try (Cursor cursor = sQLiteDatabase.rawQuery(query, null)) {
                cursor.moveToFirst();
                return cursor.getInt(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_HOURLY_NEURONS));
            }
        }
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private double getCurrentHourNeurons() {
        if (!doesNowExist()) {
            addNowRecord();
            return 0;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = simpleDateFormat.format(new Date());
        DateTime dateTime = new DateTime();
        int hour = dateTime.getHourOfDay();

        String query = "SELECT * FROM " + TABLE_NAME
                + " WHERE " + StatisticsEntry.COLUMN_DATE + " = '" + date
                + "' AND " + StatisticsEntry.COLUMN_HOUR + " = " + hour;

        Cursor cursor = sQLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        double result = cursor.getDouble(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_HOURLY_NEURONS));
        cursor.close();
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addCurrentHourNeurons() {
        if (!doesNowExist()) {
            addNowRecord();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = simpleDateFormat.format(new Date());
        DateTime dateTime = new DateTime();
        int hour = dateTime.getHourOfDay();

        double monthlyAchievedGoals = goalDB.getMonthlyAchievedGoals().size(),
                sumOfGoals = (goalDB.getMonthlyAchievedGoals().size() + goalDB.getActiveGoalsCount()),
                monthlyDifficultyAverage = goalDB.getMonthlyDifficultiesAverage(),
                hourlyMinutesOfWork = getCurrentHourMinutesOfWork(),
                hourlyNeurons = ((monthlyAchievedGoals / sumOfGoals) * (monthlyDifficultyAverage / 3.0)) * (hourlyMinutesOfWork / sumOfGoals) * 1000;

        String query = "UPDATE " + TABLE_NAME
                + " SET " + StatisticsEntry.COLUMN_HOURLY_NEURONS + " = " + (getCurrentHourNeurons() + hourlyNeurons)
                + " WHERE " + StatisticsEntry.COLUMN_DATE + " = '" + date
                + "' AND " + StatisticsEntry.COLUMN_HOUR + " = " + hour;

        sQLiteDatabase.execSQL(query);

        HashMap<String, Object> currentHourNeuronsData = new HashMap<>();
        currentHourNeuronsData.put(COLUMN_HOURLY_NEURONS, getCurrentHourNeurons());
        statisticsDBReference.document(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "," + hour).update(currentHourNeuronsData);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean didPayMonthlyNeurons(Month month, Year year) {
        String firstDayOfMonthDate = LocalDate.of(year.getValue(), month.getValue(), 1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String query = "SELECT * FROM " + TABLE_NAME
                + " WHERE " + StatisticsEntry.COLUMN_DATE + " = '" + firstDayOfMonthDate + "' AND "
                + StatisticsEntry.COLUMN_PAYED_MONTHLY_NEURONS + " = " + 1;
        Cursor cursor = sQLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setPayedMonthlyNeurons(Month month, Year year) {
        if (!doesNowExist()) {
            addNowRecord();
        }
        String date = LocalDate.of(year.getValue(), month.getValue(), 1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String query = "UPDATE " + TABLE_NAME
                + " SET " + StatisticsEntry.COLUMN_PAYED_MONTHLY_NEURONS + " = " + 1
                + " WHERE " + StatisticsEntry.COLUMN_DATE + " = '" + date
                + "' AND " + StatisticsEntry.COLUMN_HOUR + " = " + 0;

        sQLiteDatabase.execSQL(query);

        HashMap<String , Object> payedMonthlyNeuronsData = new HashMap<>();
        payedMonthlyNeuronsData.put(COLUMN_PAYED_MONTHLY_NEURONS, true);
        statisticsDBReference.document(LocalDate.of(year.getValue(), month.getValue(), 1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "," + 0).update(payedMonthlyNeuronsData);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean doesFirstOfMonthOfYearExist(Month month, Year year) {
        String firstDayOfMonthDate = LocalDate.of(year.getValue(), month.getValue(), 1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String query = "SELECT * FROM " + TABLE_NAME
                + " WHERE " + StatisticsEntry.COLUMN_DATE + " = '" + firstDayOfMonthDate + "' AND "
                + StatisticsEntry.COLUMN_HOUR + " = " + 0;
        Cursor cursor = sQLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean passedFirstMonth(Month month, Year year) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String query = "SELECT * FROM " + TABLE_NAME
                + " WHERE " + StatisticsEntry._ID + " = " + 1;
        try (Cursor cursor = sQLiteDatabase.rawQuery(query, null)) {
            if (cursor.getCount() <= 0) {
                cursor.close();
                return true;
            }
            cursor.moveToFirst();
            LocalDate cursorDate = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_DATE)), dateTimeFormatter);
            LocalDate checkDate = LocalDate.parse(LocalDate.of(year.getValue(), month.getValue(), 1).format(dateTimeFormatter), dateTimeFormatter);
            if (cursorDate.isAfter(checkDate) || cursorDate.isEqual(checkDate) || (cursorDate.getYear() == checkDate.getYear() && cursorDate.getMonth().equals(checkDate.getMonth()))) {
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void paySpecificMonthNeurons(Month month, Year year) {
        String date = LocalDate.of(year.getValue(), month.getValue(), 1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        double monthlyPayment = ((double) getCurrentAllTimeNeurons()) / 10;

        String query = "UPDATE " + TABLE_NAME
                + " SET " + StatisticsEntry.COLUMN_HOURLY_NEURONS + " = " + (getFirstHourOfMonthNeurons(month, year) - monthlyPayment)
                + " WHERE " + StatisticsEntry.COLUMN_DATE + " = '" + date
                + "' AND " + StatisticsEntry.COLUMN_HOUR + " = " + 0;

        sQLiteDatabase.execSQL(query);

        HashMap<String, Object> paySpecificMonthNeuronsData = new HashMap<>();
        paySpecificMonthNeuronsData.put(COLUMN_HOURLY_NEURONS, getFirstHourOfMonthNeurons(month, year));
        statisticsDBReference.document(LocalDate.of(year.getValue(), month.getValue(), 1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "," + 0).update(paySpecificMonthNeuronsData);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void payMonthlyNeurons(Month month, Year year) {
        if(!passedFirstMonth(month, year)) {
            if (!doesFirstOfMonthOfYearExist(month, year)) {
                String date = LocalDate.of(year.getValue(), month.getValue(), 1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                addRecord(date, 0);
                if (month.getValue() > 1 && !doesFirstOfMonthOfYearExist(Month.of(month.getValue() - 1), year) && !passedFirstMonth(Month.of(month.getValue() - 1), year)) {
                    String tempDate = LocalDate.of(year.getValue(), month.getValue() - 1, 1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    addRecord(tempDate, 0);
                } else if (month.getValue() == 1 && !doesFirstOfMonthOfYearExist(Month.of(12), Year.of(year.getValue() - 1)) && !passedFirstMonth(Month.of(12), Year.of(year.getValue() - 1))) {
                    String tempDate = LocalDate.of(year.getValue() - 1, 12, 1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    addRecord(tempDate, 0);
                }
            }
            if (!didPayMonthlyNeurons(month, year)) {
                if (month.getValue() > 1) {
                    payMonthlyNeurons(Month.of(month.getValue() - 1), year);
                } else if (month.getValue() == 1) {
                    payMonthlyNeurons(Month.of(12), Year.of(year.getValue() - 1));
                }
                paySpecificMonthNeurons(month, year);
                setPayedMonthlyNeurons(month, year);
            }
        }
    }

    public void clearDB(){
        String query = "DELETE FROM " + TABLE_NAME;
        sQLiteDatabase.execSQL(query);
    }
}
