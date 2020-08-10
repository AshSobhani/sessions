package com.example.sessions;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class SessionsProvider extends ContentProvider {
	private static final String TAG = "SessionsProvider";
	private DBManager dbManager = null;

	//Create and assign URI matchers accordingly
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(SessionsProviderContract.AUTHORITY, "sessions", 1);
		uriMatcher.addURI(SessionsProviderContract.AUTHORITY, "sessions/#", 2);
		uriMatcher.addURI(SessionsProviderContract.AUTHORITY, "track_points", 3);
		uriMatcher.addURI(SessionsProviderContract.AUTHORITY, "track_points/#", 4);
		uriMatcher.addURI(SessionsProviderContract.AUTHORITY, "*", 5);
	}

	@Override
	public boolean onCreate() {
		//Initialise database after printing debug log
		Log.d(TAG, "onCreate: Initialising Database");
		this.dbManager = new DBManager(getContext());

		return true;
	}

	@Nullable
	@Override
	public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
		Log.d(TAG, "query: Running...");
		SQLiteDatabase db = dbManager.getWritableDatabase();

		//Creating cases to detect what type of query has been requested and using the correct case accordingly
		switch(uriMatcher.match(uri)) {
			case 2:
				selection = "_id = " + uri.getLastPathSegment();
			case 1:
				return db.query(SessionsProviderContract.SESSIONS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
			case 4:
				selection = "_id = " + uri.getLastPathSegment();
			case 3:
				return db.query(SessionsProviderContract.TRACK_POINTS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
			case 5:
				return db.rawQuery("SELECT * FROM " + SessionsProviderContract.SESSIONS_TABLE +
						" UNION SELECT * FROM " + SessionsProviderContract.TRACK_POINTS_TABLE, selectionArgs);
			default:
				return null;
		}
	}

	@Nullable
	@Override
	public String getType(@NonNull Uri uri) {
		return null;
	}

	@Nullable
	@Override
	public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
		Log.d(TAG, "insert: Adding Session");
		SQLiteDatabase db = dbManager.getWritableDatabase();

		//Creating cases to detect what type of query has been requested and using the correct case accordingly
		switch(uriMatcher.match(uri)) {
			case 1:
				//Run SQL command to input new Session values into selected fields
				db.insert(SessionsProviderContract.SESSIONS_TABLE, null, contentValues);
				break;
			case 3:
				//Run SQL command to input new Track Point values into selected fields
				db.insert(SessionsProviderContract.TRACK_POINTS_TABLE, null, contentValues);
				break;
			default:
				return null;
		}

		return null;
	}

	@Override
	public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
		Log.d(TAG, "delete: Deleting record");
		SQLiteDatabase db = dbManager.getWritableDatabase();

		//Deleted rows from the table as requested
		db.delete(SessionsProviderContract.SESSIONS_TABLE, SessionsProviderContract.SESSIONS_ID + "=?",  strings);

		return 0;
	}

	@Override
	public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
		Log.d(TAG, "update: Updating record");
		SQLiteDatabase db = dbManager.getWritableDatabase();
		
		//Update table requested at the requested row
		db.update(SessionsProviderContract.SESSIONS_TABLE, contentValues, s, strings);
		
		return 0;
	}
}
