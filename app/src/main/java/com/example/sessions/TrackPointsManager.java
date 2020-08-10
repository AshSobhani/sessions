package com.example.sessions;

import android.location.Location;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TrackPointsManager {
	private static final String TAG = "TrackPointsManager";

	//Array for storing track points
	ArrayList<Location> trackPointArray = new ArrayList<>();

	//Current and previous track points for calculations
	Location currentTrackPoint;
	Location previousTrackPoint;

	//Required data that can be extracted or calculated by track points
	String startDateTime;
	double distance = 0;
	double avgSpeed = 0;
	double altitude = 0;
	double longitude = 0;
	double latitude = 0;
	long duration = 0;
	long startTime = 0;
	long currentTime = 0;

	TrackPointsManager() {
		//Get the sessions date and time
		startDateTime = getCurrentDateTime();

		//Get the session start time in seconds
		startTime = (System.currentTimeMillis());
		// startTime = (double) (System.currentTimeMillis()/1000); NOTE::: DOUBLE IN SECS
	}

	public void addTrackPoint(Location location) {
		//Add a track point to the array
		trackPointArray.add(location);

		if (trackPointArray.size() >= 2) {
			//Take the newest track points to calculate useful info
			currentTrackPoint = trackPointArray.get(trackPointArray.size() - 1);
			previousTrackPoint = trackPointArray.get(trackPointArray.size() - 2);
		}
	}

	public double getCalculatedDistance() {
		if (currentTrackPoint != null && previousTrackPoint != null) {
			//Calculate distance and add to the total distance
			distance += currentTrackPoint.distanceTo(previousTrackPoint);
		}

		return distance;
	}

	public long getCalculatedDuration() {
		//Get the current session time in seconds
		currentTime = (System.currentTimeMillis());

		//Calculate the duration of the session so far
		duration = currentTime - startTime;

		return duration;
	}

	public double getCalculatedAvgSpeed() {
		//Calculate the average speed meters per second
		avgSpeed = distance / ((duration / 1000));

		return avgSpeed;
	}

	public String getSessionStartDateTime() {
		return startDateTime;
	}

	public Long getSessionStartTime() {
		return startTime;
	}

	public double getLongitude() {
		if (currentTrackPoint != null) {
			//Get the current track points longitude
			return currentTrackPoint.getLongitude();
		}

		return longitude;
	}

	public double getLatitude() {
		if (currentTrackPoint != null) {
			//Get the current track points latitude
			return currentTrackPoint.getLatitude();
		}

		return latitude;
	}

	public double getAltitude () {
		if (currentTrackPoint != null) {
			//Get the current track points altitude
			altitude = currentTrackPoint.getAltitude();
		}

		return altitude;
	}

	public String getCurrentDateTime() {
		//Create the correct Date and Time format
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

		//Create date and time instance with the defined format
		String currentDateTime = sdf.format(new Date());

		return currentDateTime;
	}
}
