package com.example.sessions;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManager extends SQLiteOpenHelper {
    public DBManager(Context context) {
        super(context, "sessionsDB", null, 1);

        getWritableDatabase();
    }

    private static final String TAG = "DBManager";

    public void onCreate(SQLiteDatabase db) {
        //Create table for sessions
        Log.d(TAG, "onCreate: Creating Session Table...");
        db.execSQL("CREATE TABLE sessions (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "name VARCHAR(128) NOT NULL," +
                "description VARCHAR(512) NOT NULL," +
                "rating INTEGER," +
                "date_time DATETIME," +
                "session_type VARCHAR(128) NOT NULL," +
                "duration LONG," +
                "avg_speed DOUBLE," +
                //"altitude INTEGER," +
                //"calories_burned INTEGER," +
                "distance DOUBLE);");

        //Create table for all the track points used in a session
        Log.d(TAG, "onCreate: Creating Track Points Table...");
        db.execSQL("CREATE TABLE track_points (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "sessions_id INTEGER," +
                "longitude DOUBLE," +
                "latitude DOUBLE," +
                "altitude DOUBLE ," +
                "CONSTRAINT fk1 FOREIGN KEY (sessions_id) REFERENCES sessions (_id)" +
                "ON DELETE CASCADE);");

        Log.d(TAG, "onCreate: Tables Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
