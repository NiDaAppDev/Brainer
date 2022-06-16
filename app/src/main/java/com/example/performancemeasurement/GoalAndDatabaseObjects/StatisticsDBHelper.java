package com.example.performancemeasurement.GoalAndDatabaseObjects;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.performancemeasurement.GoalAndDatabaseObjects.StatisticsContract.StatisticsEntry;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StatisticsDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "statistics.db";

    public static final int DATABASE_VERSION = 1;

    private final SQLiteDatabase sQLiteDatabase = getWritableDatabase();

    public StatisticsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_STATISTICS_TABLE = "CREATE TABLE " +
                StatisticsEntry.TABLE_NAME + " (" +
                StatisticsEntry.COLUMN_MONTH_OF_YEAR + " INTEGER PRIMARY KEY, " +
                StatisticsEntry.COLUMN_MONTHLY_SECONDS_OF_WORK + " INTEGER NOT NULL)";
        db.execSQL(SQL_CREATE_STATISTICS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GoalContract.GoalEntry.TABLE_NAME);
        onCreate(db);
    }

    private boolean doesCurrentMonthExist() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
        int nowMonth = formatter.parseLocalDate(new SimpleDateFormat("dd/MM/yyyy").format(new Date())).getMonthOfYear();
        int nowYear = formatter.parseLocalDate(new SimpleDateFormat("dd/MM/yyyy").format(new Date())).getYear();
        int nowMonthOfYear = (nowMonth * 10000) + nowYear;
        String query = "SELECT * FROM " + StatisticsEntry.TABLE_NAME
                + " WHERE " + StatisticsEntry.COLUMN_MONTH_OF_YEAR + " = " + nowMonthOfYear;
        Cursor cursor = sQLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    private void addNewCurrentMonth() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
        int nowMonth = formatter.parseLocalDate(new SimpleDateFormat("dd/MM/yyyy").format(new Date())).getMonthOfYear();
        int nowYear = formatter.parseLocalDate(new SimpleDateFormat("dd/MM/yyyy").format(new Date())).getYear();
        int nowMonthOfYear = (nowMonth * 10000) + nowYear;

        ContentValues contentValues = new ContentValues();
        contentValues.put(StatisticsEntry.COLUMN_MONTH_OF_YEAR, nowMonthOfYear);
        contentValues.put(StatisticsEntry.COLUMN_MONTHLY_SECONDS_OF_WORK, 0);

        sQLiteDatabase.insert(StatisticsEntry.TABLE_NAME, null, contentValues);
    }

    public long getMonthlyMinutesOfWork() {
        if(doesCurrentMonthExist()) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
            int nowMonth = formatter.parseLocalDate(new SimpleDateFormat("dd/MM/yyyy").format(new Date())).getMonthOfYear();
            int nowYear = formatter.parseLocalDate(new SimpleDateFormat("dd/MM/yyyy").format(new Date())).getYear();
            int nowMonthOfYear = (nowMonth * 10000) + nowYear;

            String query = "SELECT * FROM " + StatisticsEntry.TABLE_NAME
                    + " WHERE " + StatisticsEntry.COLUMN_MONTH_OF_YEAR + " = " + nowMonthOfYear;
            Cursor cursor = sQLiteDatabase.rawQuery(query, null);
            cursor.moveToFirst();
            long result = cursor.getInt(cursor.getColumnIndexOrThrow(StatisticsEntry.COLUMN_MONTHLY_SECONDS_OF_WORK));
            cursor.close();
            return result / 60;
        }
        return 0;
    }

    public void addMonthlySecondsOfWork(int secondsOfWork) {
        if (!doesCurrentMonthExist()) {
            addNewCurrentMonth();
        }
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
        int nowMonth = formatter.parseLocalDate(new SimpleDateFormat("dd/MM/yyyy").format(new Date())).getMonthOfYear();
        int nowYear = formatter.parseLocalDate(new SimpleDateFormat("dd/MM/yyyy").format(new Date())).getYear();
        int nowMonthOfYear = (nowMonth * 10000) + nowYear;

        String query = "UPDATE " + StatisticsEntry.TABLE_NAME
                + " SET " + StatisticsEntry.COLUMN_MONTHLY_SECONDS_OF_WORK + " = " + (getMonthlyMinutesOfWork() + secondsOfWork)
                + " WHERE " + StatisticsEntry.COLUMN_MONTH_OF_YEAR + " = " + nowMonthOfYear;

        sQLiteDatabase.execSQL(query);
    }
}
