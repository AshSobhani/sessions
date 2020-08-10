package com.example.sessions;

import android.net.Uri;

public class SessionsProviderContract {
	public static final String AUTHORITY = "com.example.sessions.SessionsProvider";

	//Resource Identifiers
	public static final Uri SESSIONS_URI = Uri.parse("content://"+AUTHORITY+"/sessions");
	public static final Uri SESSIONS_SINGLE_URI = Uri.parse("content://"+AUTHORITY+"/sessions/#");
	public static final Uri TRACK_POINTS_URI = Uri.parse("content://"+AUTHORITY+"/track_points");
	public static final Uri TRACK_POINTS_SINGLE_URI = Uri.parse("content://"+AUTHORITY+"/track_points/#");
	public static final Uri ALL_URI = Uri.parse("content://"+AUTHORITY+"/");

	//Tables
	public static final String SESSIONS_TABLE = "sessions";
	public static final String TRACK_POINTS_TABLE = "track_points";

	//Sessions Columns
	public static final String SESSIONS_ID = "_id";
	public static final String SESSIONS_NAME = "name";
	public static final String SESSIONS_DESCRIPTION = "description";
	public static final String SESSIONS_RATING = "rating";
	public static final String SESSIONS_DATE_TIME = "date_time";
	public static final String SESSIONS_SESSION_TYPE = "session_type";
	public static final String SESSIONS_DURATION = "duration";
	public static final String SESSIONS_AVG_SPEED = "avg_speed";
	public static final String SESSIONS_ELEVATION = "elevation";
	public static final String SESSIONS_DISTANCE = "distance";
	public static final String SESSIONS_CALORIES_BURNED = "calories_burned";

	//Track Points Columns
	public static final String TRACK_POINTS_ID = "_id";
	public static final String TRACK_POINTS_SESSION_ID = "session_id";
	public static final String TRACK_POINTS_LONGITUDE = "longitude";
	public static final String TRACK_POINTS_LATITUDE = "latitude";
	public static final String TRACK_POINTS_DATE_TIME = "date_time";

	//Sorting
	public static final String SORT_BY_ID = "_id";
	public static final String SORT_BY_RATING_DESC = "rating desc";
}
