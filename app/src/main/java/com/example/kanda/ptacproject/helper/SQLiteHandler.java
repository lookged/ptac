package com.example.kanda.ptacproject.helper;

/**
 * Created by Kanda on 8/24/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SQLiteHandler extends SQLiteOpenHelper {
    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_USER = "user";

    // friendlist table name
    private static final String TABLE_FRIENDLIST = "friendlist";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";

    // Login Table Columns names
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FID = "fid";
    private static final String KEY_DATE = "date";
    private static final String KEY_STATUS = "status";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";

        String CREATE_FRIENDLIST_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_FRIENDLIST + "("
                + KEY_USER_ID + " TEXT," + KEY_FID + " TEXT,"
                + KEY_DATE + " DATETIME," + KEY_STATUS + " INT,"
                + " PRIMARY KEY (" + KEY_USER_ID + ", " + KEY_FID + ")"
                + ")";

        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_FRIENDLIST_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDLIST);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUser(String email, String uid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // Email
        //values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }


    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * Add friend list to friendlist table
     */
    public void addFriendList(String userId, String fId, Date date, int status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, userId);
        values.put(KEY_FID, fId);
        values.put(KEY_DATE, getDateTime());
        values.put(KEY_STATUS, status);

        // Inserting Row
        long id = db.insert(TABLE_FRIENDLIST, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New friendlist inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     */
    public ArrayList<String> getFriendList(String loginId) {
        ArrayList<String> friendList = null;
        String selectQuery = "SELECT  * FROM "
                + TABLE_FRIENDLIST + " WHERE "
                + KEY_USER_ID + " = \"" + loginId + "\"";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            friendList = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    friendList.add(cursor.getString(2));
                    Log.d(TAG, "Fetching friendlist from Sqlite: " + cursor.getString(1));
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return friendList;
    }


    /**
     * Re crate database Delete friend naja
     */
    public void deleteAllFriend() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_FRIENDLIST, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }
    public void deleteFriend(String loginId, String fIDTeejadornLob) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_FRIENDLIST, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }
}
