package com.nidaappdev.brainer.publicClassesAndInterfaces;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.HashMap;

public class FilterCursorWrapper extends CursorWrapper {

    private static final String LOG_TAG = FilterCursorWrapper.class.getSimpleName();

    private Cursor cursor;
    private OnCursorFilterer filterer;

    // een collectie dat gefilterde rijnummer linkt aan oorspronkelijk rijnummer
    private HashMap<Integer,Integer> mRowToRowMap = new HashMap<Integer,Integer>();    //mapt filtered posities (key) naar originele posities
    private int mPos = -1;

    public FilterCursorWrapper(Cursor cursor, OnCursorFilterer filterer) {
        super(cursor);
        this.cursor = cursor;
        this.filterer = filterer;
    }

    public void filter() {
        mRowToRowMap.clear();
        if (filterer == null)
        {
            //Log.e(LOG_TAG, "filterer has no value, filtering not allowed, empty result");
            return;
        }

        cursor.moveToPosition(-1);
        int filteredi=0;
        int originelei=0;
        while (cursor.moveToNext()) {
            if (filterer.retainsCurrent(cursor)) {
                mRowToRowMap.put(filteredi, originelei);
                filteredi++;
            }
            originelei++;
        }

        mPos = -1; // fix: this line is necessary to reset position for next iteration
    }

    @Override
    public int getCount() {
        return mRowToRowMap.size();
    }

    @Override
    public boolean moveToPosition(int position) {
        // Make sure position isn't past the end of the cursor
        final int count = getCount();
        if (position >= count) {
            mPos = count;
            return false;
        }

        // Make sure position isn't before the beginning of the cursor
        if (position < 0) {
            mPos = -1;
            return false;
        }

        mPos = position; // fix: this line is necessary to remember current position for next iteration

        int originelei = (int) mRowToRowMap.get(position);
        return super.moveToPosition(originelei);
    }

    @Override
    public final boolean move(int offset) {
        return moveToPosition(mPos + offset);
    }

    @Override
    public final boolean moveToFirst() {
        return moveToPosition(0);
    }

    @Override
    public final boolean moveToLast() {
        return moveToPosition(getCount() - 1);
    }

    @Override
    public final boolean moveToNext() {
        return moveToPosition(mPos + 1);
    }

    @Override
    public final boolean moveToPrevious() {
        return moveToPosition(mPos - 1);
    }

    @Override
    public final boolean isFirst() {
        return mPos == 0 && getCount() != 0;
    }

    @Override
    public final boolean isLast() {
        int cnt = getCount();
        return mPos == (cnt - 1) && cnt != 0;
    }

    @Override
    public final boolean isBeforeFirst() {
        if (getCount() == 0) {
            return true;
        }
        return mPos == -1;
    }

    @Override
    public final boolean isAfterLast() {
        if (getCount() == 0) {
            return true;
        }
        return mPos == getCount();
    }

    @Override
    public int getPosition() {
        return mPos;
    }

    public interface OnCursorFilterer{
        boolean retainsCurrent(Cursor cursor);   //true indien de current rij voldoet aan de filter, false anders
    }
}