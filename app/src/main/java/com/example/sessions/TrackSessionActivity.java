package com.example.sessions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static java.lang.String.valueOf;

public class TrackSessionActivity extends AppCompatActivity {
	private static final String TAG = "TrackSessionActivity";
	static int END_SESSION_ACTIVITY_REQUEST_CODE = 1;

	//Initialise service binder
	private TrackingService.TrackingServiceBinder trackingServiceBinder = null;

	//Variable for decimal accuracy & Set decimal accuracy
	DecimalFormat dp = new DecimalFormat("0.00");
	String formattedDuration;

	//Create variables for items in the view
	private TextView distanceValue;
	private TextView altitudeValue;
	private TextView durationValue;
	private TextView avgSpeedValue;
	//private TextView caloriesBurnedValue;

	//Passing across variable to be inserted
	private ArrayList<Double> longitudeArray = new ArrayList<Double>();
	private ArrayList<Double> latitudeArray = new ArrayList<Double>();
	private ArrayList<Double> altitudeArray = new ArrayList<Double>();

	//Ready variable to be bun
	private double distanceDouble;
	private String distance;
	private String dateTime;
	private long duration;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate: Track Session Activity Created");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_session);

		//Assign variables to the text views
		distanceValue = findViewById(R.id.sessionDistanceValue);
		altitudeValue = findViewById(R.id.sessionAltitudeValue);
		durationValue = findViewById(R.id.sessionDurationValue);
		avgSpeedValue = findViewById(R.id.sessionSpeedValue);
		//caloriesBurnedValue = findViewById(R.id.sessionCaloriesValue);

		//Bind the tracking service
		Intent intent = new Intent(this, TrackingService.class);
		this.bindService(intent, serviceConnection, Context.BIND_ABOVE_CLIENT);
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "onServiceConnected: Connected");
			trackingServiceBinder = (TrackingService.TrackingServiceBinder) service;
			trackingServiceBinder.registerCallback(callback);

		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			Log.d(TAG, "onServiceDisconnected: Disconnected");
			trackingServiceBinder.unregisterCallback(callback);
			trackingServiceBinder = null;
		}
	};

	ICallback callback = new ICallback() {

		@Override
		public void bundleEvent(final Bundle trackData) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					//NOTE::: FIX FIRST 2 INSTANCES

					//Populate arrays so we can insert into table in activity
					longitudeArray.add(trackData.getDouble("longitude"));
					latitudeArray.add(trackData.getDouble("latitude"));
					altitudeArray.add(trackData.getDouble("altitude"));

					//Get the date time
					dateTime = trackData.getString("startDateTime");
					Log.d(TAG, "formatAndSetData: " + dateTime);

					//Format data (decimal places, data types, etc..)
					formatAndSetData(trackData);
				}
			});
		}
	};

	public void formatAndSetData(Bundle trackData) {
		distanceDouble = trackData.getDouble("distance");
		//Get the data and convert to string
		distance = dp.format(distanceDouble);
		String altitude = dp.format(trackData.getDouble("altitude"));
		duration = trackData.getLong("duration");
		String avgSpeed = dp.format(trackData.getDouble("avgSpeed"));
//		String caloriesBurned = valueOf(trackData.getDouble("distance"));

		Log.d(TAG, "formatAndSetData: " + avgSpeed);

		//Setting duration to seconds, minutes and hours
		formattedDuration = String.format("%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(duration),
				TimeUnit.MILLISECONDS.toMinutes(duration) -
						TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
				TimeUnit.MILLISECONDS.toSeconds(duration) -
						TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));

		//Set the time text
		durationValue.setText(formattedDuration);

		//Wait for two iterations of callback before setting other text due to calculations and track points
		if(longitudeArray.size() > 2){
			distanceValue.setText(distance);
			altitudeValue.setText(altitude);
			avgSpeedValue.setText(avgSpeed);
			//caloriesBurnedValue.setText("500");
		}
	}

	public void toEndSession(View v) {
		Log.d(TAG, "toEndSession: Ending Session");

		//Stop tracking users location
		trackingServiceBinder.stopTracking();

		//Convert array list to array
		double[] longitudeArr = new double[longitudeArray.size()];
		for (int i = 0; i < longitudeArr.length; i++) {
			longitudeArr[i] = longitudeArray.get(i);
		}

		double[] latitudeArr = new double[latitudeArray.size()];
		for (int i = 0; i < latitudeArr.length; i++) {
			latitudeArr[i] = latitudeArray.get(i);
		}

		double[] altitudeArr = new double[altitudeArray.size()];
		for (int i = 0; i < altitudeArr.length; i++) {
			altitudeArr[i] = altitudeArray.get(i);
		}

		//Bundle and send session data
		Bundle sessionData = new Bundle();
		sessionData.putDouble("distance", distanceDouble);
		sessionData.putString("dateTime", dateTime);
		sessionData.putLong("duration", duration);
		sessionData.putDoubleArray("longitudeArr", longitudeArr);
		sessionData.putDoubleArray("latitudeArr", latitudeArr);
		sessionData.putDoubleArray("altitudeArr", altitudeArr);

		//Create intent and start End Session Activity
		Intent intent = new Intent(this, EndSessionActivity.class);
		intent.putExtras(sessionData);
		startActivityForResult(intent, END_SESSION_ACTIVITY_REQUEST_CODE);
	}

	@Override
	public void onBackPressed() {
		//Do Nothing
		//super.onBackPressed();
	}

}

