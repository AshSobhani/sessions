package com.example.sessions;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.RemoteCallbackList;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.core.app.NotificationCompat;
import static com.example.sessions.App.CHANNEL_ID;

public class TrackingService extends Service {
	private static final String TAG = "TrackingService";

	//Creating variables for location tracking
	private LocationCallback locationCallback;
	private LocationRequest locationRequest;
	private FusedLocationProviderClient fusedLocationClient;

	//Used to manage and use track point data
	TrackPointsManager trackPointManager = new TrackPointsManager();

	//Creating the binder
	private final IBinder binder = new TrackingServiceBinder();
	RemoteCallbackList<TrackingServiceBinder> remoteCallbackList = new RemoteCallbackList<TrackingServiceBinder>();

	//Bundle to pack all data to sent to activity
	Bundle trackPointData = new Bundle();

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate: Service Created");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand: Starting Notification");

		//Create foreground notification
		createForegroundNotification();

		//Start tracking location
		beginLocationTracking();

		return super.onStartCommand(intent, flags, startId);
	}

	public class TrackingServiceBinder extends Binder implements IInterface {
		void stopTracking(){
			//Stop tracking location
			stopLocationTracking();
		}

		Bundle getSessionData (Bundle bundle) {
			return trackPointData;
		}

		public void registerCallback(ICallback callback) {
			this.callback = callback;
			remoteCallbackList.register(TrackingServiceBinder.this);
		}

		public void unregisterCallback(ICallback callback) {
			remoteCallbackList.unregister(TrackingServiceBinder.this);
		}

		ICallback callback;

		@Override
		public IBinder asBinder() {
			return this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind: Service Binded");
		return binder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onBind: Service Unbinded");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy: Service Destroyed");

		//Stop tracking location
		stopLocationTracking();
	}

	private void createForegroundNotification() {
		Log.d(TAG, "createNotification: Notification being built");
		//Setting where the user is directed to when clicking the notification (Track Session Activity)
		Intent notificationIntent = new Intent(this, TrackSessionActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		//Building the notification channel and customising message and image
		Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
				.setContentTitle("Sessions")
				.setContentText("Tracking current run...")
				.setSmallIcon(R.drawable.sessions_logo)
				.setContentIntent(pendingIntent)
				.build();

		//Start the service in the foreground
		startForeground(1, notification);
	}

	private void beginLocationTracking() {
		//Create a location client and get the last know location
		fusedLocationClient  = new FusedLocationProviderClient(this);
		fusedLocationClient.getLastLocation()
				.addOnSuccessListener(new OnSuccessListener<Location>() {
					@Override
					public void onSuccess(Location location) {
						//If the last known location is not null do the following
						if (location != null){
							Log.d(TAG, "Location Test: " + location.toString());
						} else {
							Log.d(TAG, "onSuccess: Cannot retrieve location");
						}
					}
				});

		Log.d(TAG, "beginLocationTracking: Creating Location Request...");
		//Create a location request and configure settings
		locationRequest = LocationRequest.create();
		locationRequest.setInterval(10000);
		locationRequest.setFastestInterval(5000);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		//Use the location to...
		locationCallback = new LocationCallback() {
			@Override
			public void onLocationResult(LocationResult locationResult) {
				if (locationResult == null) {
					return;
				}
				for (Location location : locationResult.getLocations()) {
					//Toast.makeText(getBaseContext(), "Location " + location, Toast.LENGTH_SHORT).show();

					//Send location (track point) to track point manager
					trackPointManager.addTrackPoint(location);

					Log.d(TAG, "startDateTime: " + trackPointManager.getSessionStartDateTime());
					Log.d(TAG, "distance: " + trackPointManager.getCalculatedDistance() + " m");
					Log.d(TAG, "avgSpeed: " + trackPointManager.getCalculatedAvgSpeed() + " m/s");
					Log.d(TAG, "duration: " + trackPointManager.getCalculatedDuration() + " s");
					Log.d(TAG, "altitude: " + trackPointManager.getAltitude() + "m");

					//Call back data to activity
					doCallbacks(setTrackVariables(trackPointManager));
				}
			}
		};

		//Start requesting location updates
		fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
	}

	public void doCallbacks(Bundle bundle) {
		final int n = remoteCallbackList.beginBroadcast();
		for (int i=0; i<n; i++) {
			remoteCallbackList.getBroadcastItem(i).callback.bundleEvent(bundle);
		}
		remoteCallbackList.finishBroadcast();
	}

	private void stopLocationTracking() {
		//Stop requesting location updates
		Log.d(TAG, "stopLocationTracking: Stopping location updates...");
		fusedLocationClient.removeLocationUpdates(locationCallback);
	}

	public Bundle setTrackVariables (TrackPointsManager trackPointManager) {
		//Put all track point data into bundle
		//Data for sessions table
		trackPointData.putString("startDateTime", trackPointManager.getSessionStartDateTime()); //DATETIME
		trackPointData.putLong("startTime", trackPointManager.getSessionStartTime()); //Start time to figure out total duration
		trackPointData.putDouble("avgSpeed", trackPointManager.getCalculatedAvgSpeed()); //AVGSPEED
		trackPointData.putDouble("distance", trackPointManager.getCalculatedDistance()); //DISTANCE
		trackPointData.putLong("duration", trackPointManager.getCalculatedDuration()); //DURATION

		//Data for track point table
		trackPointData.putDouble("longitude", trackPointManager.getLongitude());
		trackPointData.putDouble("latitude", trackPointManager.getLatitude());
		trackPointData.putDouble("altitude", trackPointManager.getAltitude());

		return trackPointData;
	}
}
