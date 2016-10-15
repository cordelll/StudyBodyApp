package com.example.android.bluetoothlegatt;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.util.Log;
import android.support.v4.widget.SimpleCursorAdapter;


public class AnalyzesDBadapter {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_TYPE = "type";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_START_TIME = "starttime";
    public static final String KEY_END_TIME = "endtime";
    public static final String KEY_AVG_HR = "avg";
    public static final String KEY_MAX_HR = "max";
    public static final String KEY_MIN_HR = "min";
    public static final String KEY_LONG_HR_FLOW = "hrflow";
    public static final String KEY_PROFILE_MAXIMAL_HR = "maximal_hr";
    public static final String KEY_PROFILE_RESTING_HR = "resting_hr";
    public static final String KEY_PROFILE_GENDER = "gndr";

    private static final String TAG = "CountriesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "World";
    private static final String SQLITE_TABLE = "Country";
    private static final int DATABASE_VERSION = 15;

    private final Context mCtx;

    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
                    KEY_ROWID + " integer PRIMARY KEY autoincrement," +
                    KEY_TYPE + "," +
                    KEY_DESCRIPTION + "," +
                    KEY_START_TIME + "," +
                    KEY_END_TIME + "," +
                    KEY_LONG_HR_FLOW + "," +
                    KEY_PROFILE_GENDER + "," +
                    KEY_AVG_HR + " integer," +
                    KEY_MAX_HR + " integer," +
                    KEY_MIN_HR + " integer," +
                    KEY_PROFILE_MAXIMAL_HR + " integer," +
                    KEY_PROFILE_RESTING_HR + " integer" +
                    ");";

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
            onCreate(db);
        }
    }

    public AnalyzesDBadapter(Context ctx) {
        this.mCtx = ctx;
    }

    public AnalyzesDBadapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public void deleteRow(String desc){
        mDb.delete(SQLITE_TABLE, "WHERE description " + "=" + desc, null);
    }

    public long createAnalysis(Analysis analysis) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TYPE, analysis.get_type());
        initialValues.put(KEY_DESCRIPTION, analysis.get_description());
        initialValues.put(KEY_START_TIME, analysis.get_startTime());
        initialValues.put(KEY_END_TIME, analysis.get_endTime());
        initialValues.put(KEY_AVG_HR, analysis.get_avgHR());
        initialValues.put(KEY_MAX_HR, analysis.get_maxHR());
        initialValues.put(KEY_MIN_HR, analysis.get_minHR());
        initialValues.put(KEY_LONG_HR_FLOW, analysis.get_longHRflow());
        initialValues.put(KEY_PROFILE_MAXIMAL_HR, analysis.get_maximalHR());
        initialValues.put(KEY_PROFILE_RESTING_HR, analysis.get_restingHR());
        initialValues.put(KEY_PROFILE_GENDER, analysis.get_gender());

        return mDb.insert(SQLITE_TABLE, null, initialValues);
    }

    public boolean deleteAllAnalyzes() {

        int doneDelete = 0;
        doneDelete = mDb.delete(SQLITE_TABLE, null, null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;

    }

    public Cursor fetchAllAnalyzes() {

        Cursor mCursor = mDb.query(SQLITE_TABLE, new String[]{KEY_ROWID,
                        KEY_TYPE, KEY_DESCRIPTION, KEY_START_TIME, KEY_END_TIME,
                        KEY_LONG_HR_FLOW, KEY_AVG_HR, KEY_MAX_HR, KEY_MIN_HR,
                        KEY_PROFILE_MAXIMAL_HR, KEY_PROFILE_RESTING_HR, KEY_PROFILE_GENDER},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

}