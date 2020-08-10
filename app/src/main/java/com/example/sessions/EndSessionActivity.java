package com.example.sessions;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class EndSessionActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {
	private static final String TAG = "EndSessionActivity";
	static int MAIN_ACTIVITY_REQUEST_CODE = 1;

	//Create variables for items in the view
	private String name;
	private String description;
	private String rating;
	private String sessionType;

	//Variables to insert into table
	private double distance;
	private String dateTime;
	private long duration;
	private double avgSpeed;
	private double[] longitudeArr;
	private double[] latitudeArr;
	private double[] altitudeArr;
	private int sessionID;

	private MapView mapView;
	private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate: End Session Activity Created");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_end_session);

		//Get saved instances for the map
		Bundle mapViewBundle = null;
		if (savedInstanceState != null) {
			mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
		}

		//Find the map view in the xml
		mapView = (MapView) findViewById(R.id.googleMap);
		mapView.onCreate(mapViewBundle);

		unpackBundle();
	}

	public void toMainActivity(View v) {
		Log.d(TAG, "toEndSession: Ending Session");

		//Insert all the data into the database
		insertDataIntoDatabase();

		//Start the new activity and stop the service
		startActivityStopService();
	}

	public void toMainActivityDiscard(View v) {
		Log.d(TAG, "toEndSession: Ending Session");

		//Start the new activity and stop the service
		startActivityStopService();
	}

	public void startActivityStopService() {
		//Create intent and start End Session Activity
		Intent intent = new Intent(this, MainActivity.class);
		startActivityForResult(intent, MAIN_ACTIVITY_REQUEST_CODE);

		//Stop the current tracking service
		Intent serviceIntent = new Intent(this, TrackingService.class);
		stopService(serviceIntent);
	}

	public void insertDataIntoDatabase() {
		Log.d(TAG, "insertDataIntoDatabase: Inserting data into tables");

		//Take the users inserted values
		name = ((EditText) findViewById(R.id.sessionName)).getText().toString();
		description = ((EditText) findViewById(R.id.sessionDescription)).getText().toString();
		sessionType = ((EditText) findViewById(R.id.sessionType)).getText().toString();
		rating = ((EditText) findViewById(R.id.sessionRating)).getText().toString();

		//Insert into sessions table
		ContentValues sessionsData = new ContentValues();
		sessionsData.put(SessionsProviderContract.SESSIONS_NAME, name);
		sessionsData.put(SessionsProviderContract.SESSIONS_DESCRIPTION, description);
		sessionsData.put(SessionsProviderContract.SESSIONS_RATING, rating);
		sessionsData.put(SessionsProviderContract.SESSIONS_DATE_TIME, dateTime);
		sessionsData.put(SessionsProviderContract.SESSIONS_SESSION_TYPE, sessionType);
		sessionsData.put(SessionsProviderContract.SESSIONS_DURATION, duration);
		sessionsData.put(SessionsProviderContract.SESSIONS_AVG_SPEED, avgSpeed);
		sessionsData.put(SessionsProviderContract.SESSIONS_DISTANCE, distance);

		//Insert entry into the table
		getContentResolver().insert(SessionsProviderContract.SESSIONS_URI, sessionsData);

		//Insert into track point table
		Cursor cursor = getContentResolver().query(SessionsProviderContract.SESSIONS_SINGLE_URI,
				new String[]{SessionsProviderContract.SESSIONS_ID}, null, null ,null);

		if (cursor.moveToFirst()) {
			do {
				sessionID = cursor.getInt(0);
			} while (cursor.moveToNext());
			cursor.close();
		}

		//Log.d(TAG, "insertDataIntoDatabase: " + sessionID);

		ContentValues trackPointData = new ContentValues();

		//Insert all the track points from the array
		for(int i = 0; i < longitudeArr.length ; i++){
			trackPointData.put("sessions_id", sessionID);
			trackPointData.put("longitude", longitudeArr[i]);
			trackPointData.put("latitude", latitudeArr[i]);
			trackPointData.put("altitude", altitudeArr[i]);

			//Insert data
			getContentResolver().insert(SessionsProviderContract.TRACK_POINTS_URI, trackPointData);
		}
	}

	public void unpackBundle (){
		//Take the data out and assign to variables
		Bundle sessionData = getIntent().getExtras();
		distance = sessionData.getDouble("distance");
		dateTime = sessionData.getString("dateTime");
		duration = sessionData.getLong("duration");
		longitudeArr = sessionData.getDoubleArray("longitudeArr");
		latitudeArr = sessionData.getDoubleArray("latitudeArr");
		altitudeArr = sessionData.getDoubleArray("altitudeArr");

		//Calculate total average speed
		avgSpeed = (distance / (duration / 1000));

//		Log.d(TAG, "unpackBundle: " + "distance: " + distance + " dateTime: " + dateTime + " dur: " + duration + " avs: " + avgSpeed);
//
//		for (int i = 0; i < latitudeArr.length; i++) {
//			Log.d(TAG, "lat: " + latitudeArr[i]);
//		}
//
//		for (int i = 0; i < longitudeArr.length; i++) {
//			Log.d(TAG, "long: " + longitudeArr[i]);
//		}
//
//		for (int i = 0; i < altitudeArr.length; i++) {
//			Log.d(TAG, "alt " + i + ": " + altitudeArr[i]);
//		}
	}

	@Override
	public void onMapReady(final GoogleMap googleMap) {
		//Enable my location icon and activate find location button listener
		googleMap.setMyLocationEnabled(true);
		googleMap.setOnMyLocationButtonClickListener(this);
		googleMap.setOnMyLocationClickListener(this);

		PolylineOptions line = new PolylineOptions().clickable(false);

		FusedLocationProviderClient fusedLocationClient = new FusedLocationProviderClient(this);
		fusedLocationClient.getLastLocation()
				.addOnSuccessListener(new OnSuccessListener<Location>() {
					@Override
					public void onSuccess(Location location) {
						//If the last known location is not null do the following
						if (location != null){


							//If there is a location found move camera to user location
							LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
							CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);


							googleMap.animateCamera(cameraUpdate);
						} else {
							Log.d(TAG, "onSuccess: Cannot retrieve location");
						}
					}
				});

		for(int i = 0 ; i < longitudeArr.length ; i++){
			LatLng location = new  LatLng(latitudeArr[i], longitudeArr[i]);
			line.add(location);
		}

		googleMap.addPolyline(line);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
		if (mapViewBundle == null) {
			mapViewBundle = new Bundle();
			outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
		}

		mapView.onSaveInstanceState(mapViewBundle);
	}

	@Override
	public boolean onMyLocationButtonClick() {
		Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
		// Return false so that we don't consume the event and the default behavior still occurs
		// (the camera animates to the user's current position).
		return false;
	}

	@Override
	public void onMyLocationClick(@NonNull Location location) {
		Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onBackPressed() {
		//Do Nothing
		//super.onBackPressed();
	}
}

