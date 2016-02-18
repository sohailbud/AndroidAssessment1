package com.example.android.okcupidassessment.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.okcupidassessment.model.Photo;
import com.example.android.okcupidassessment.model.User;

import com.example.android.okcupidassessment.model.database.DataContract.UserEntry;
import com.example.android.okcupidassessment.util.Utils;

/**
 * Created by Sohail on 2/16/16.
 */
public class DataHandler {

    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase db;

    private final String TAG = this.getClass().getSimpleName();

    /**
     * creates an instance of the database
     * @param context
     */
    public DataHandler(Context context) {
        dataBaseHelper = new DataBaseHelper(context);
    }

    /**
     * open the database
     * @return
     */
    public DataHandler open() {
        try {
            if (dataBaseHelper != null)
                db = dataBaseHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
         return this;
    }

    /**
     * close the database
     */
    public void close() {
        dataBaseHelper.close();
    }

    /**
     * checks if user already exists, return row id if it does else insert user data in user_entry table
     */
    public long insertUserData(User user) {

        Cursor userCursor = returnUserData(user.getUserid());

        if (userCursor.moveToNext()) {
            long row_ID = userCursor.getLong(userCursor.getColumnIndexOrThrow(UserEntry._ID));
            userCursor.close();
            return row_ID;

        } else {
            ContentValues userValues = new ContentValues();
            userValues.put(UserEntry.COLUMN_NAME_USER_ID, user.getUserid());
            userValues.put(UserEntry.COLUMN_NAME_USERNAME, user.getUsername());
            userValues.put(UserEntry.COLUMN_NAME_AGE, user.getAge());
            userValues.put(UserEntry.COLUMN_NAME_CITY_NAME, user.getCity_name());
            userValues.put(UserEntry.COLUMN_NAME_STATE_CODE, user.getState_code());
            userValues.put(UserEntry.COLUMN_NAME_MATCH, user.getMatch());
            userValues.put(UserEntry.COLUMN_NAME_LARGE_IMAGE_PATH, user.getPhoto().getFull_paths().getLarge());

            return db.insert(UserEntry.TABLE_NAME, null, userValues);
        }
    }

    /**
     * FOLLOWING METHODS RUN QUERIES TO OBTAIN DATA FROM SQL DATABASE
     */

    public Cursor returnUserData(String userid) {
        final String SQL_RETURN_USER_DATA = "SELECT * FROM " + UserEntry.TABLE_NAME +
                " WHERE " + UserEntry.COLUMN_NAME_USER_ID + " = ?";
        return db.rawQuery(SQL_RETURN_USER_DATA, new String[] {userid});
    }

    public Cursor returnUserData() {
        final String SQL_RETURN_USER_DATA = "SELECT * FROM " + UserEntry.TABLE_NAME;
        return db.rawQuery(SQL_RETURN_USER_DATA, null);
    }

    /**
     * A SQLiteOpenHelper class to manage database creation and version management.
     */
    protected class DataBaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "okCupidDatabase";

        // If you change the database schema, you must increment the database version.
        private static final int DATABASE_VERSION = 1;

        public DataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         * Creates tables
         */
        @Override
        public void onCreate(SQLiteDatabase db) {

            final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                    UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    UserEntry.COLUMN_NAME_USER_ID + " TEXT UNIQUE NOT NULL, " +
                    UserEntry.COLUMN_NAME_USERNAME + " TEXT UNIQUE NOT NULL, " +
                    UserEntry.COLUMN_NAME_AGE + " INTEGER, " +
                    UserEntry.COLUMN_NAME_CITY_NAME + " TEXT, " +
                    UserEntry.COLUMN_NAME_STATE_CODE + " TEXT, " +
                    UserEntry.COLUMN_NAME_MATCH + " INTEGER, " +
                    UserEntry.COLUMN_NAME_LARGE_IMAGE_PATH + " TEXT UNIQUE " +
                    " );";

            try {
                db.execSQL(SQL_CREATE_USER_TABLE);
                if (Utils.DEBUG) Log.i(TAG, "user_entry table successfully created");
            } catch (SQLiteException e) {
                e.printStackTrace();
                if (Utils.DEBUG) Log.i(TAG, "user_entry table not created");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);

            onCreate(db);
        }
    }
}
