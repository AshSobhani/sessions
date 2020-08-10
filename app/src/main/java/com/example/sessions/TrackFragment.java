package com.example.sessions;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TrackFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
		GoogleMap.OnMyLocationClickListener {
	private static final String TAG = "TrackFragment";

	static int TRACK_SESSION_ACTIVITY_REQUEST_CODE = 1;
	private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

	private Button startButton;

	MapView mapView;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		//Assign the correct view to the fragment
		View v = inflater.inflate(R.layout.fragment_track, container, false);
		//Find the button and assign to member variable
		startButton = v.findViewById(R.id.startSession);

		//Set an on click listener for the button
		startButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//Start Tracking activity and service
				startTrackingActivityService();
			}
		});

		//Get saved instances for the map
		Bundle mapViewBundle = null;
		if (savedInstanceState != null) {
			mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
		}

		//Find the map view in the xml
		mapView = (MapView) v.findViewById(R.id.googleMap);
		mapView.onCreate(mapViewBundle);

		//Get the map data
		mapView.getMapAsync(this); //NOTE::: BROKEN IF LOCATION PERMISSION IS OFF

		return v;
	}

	private void startTrackingActivityService() {
		Log.d(TAG, "onClick: Starting Track Session Activity");
		//Make intent and start activity
		Intent intent = new Intent(getContext(), TrackSessionActivity.class);
		startActivityForResult(intent, TRACK_SESSION_ACTIVITY_REQUEST_CODE);

		Log.d(TAG, "onClick: Starting Tracking Service");
		//Make Service intent and start it
		Intent serviceIntent = new Intent(getContext(), TrackingService.class);
		getContext().startService(serviceIntent);
	}

	@Override
	public void onStart() {
		super.onStart();
		mapView.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
		mapView.onStop();
	}

	@Override
	public void onPause() {
		mapView.onPause();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		mapView.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}

	@Override
	public void onMapReady(final GoogleMap googleMap) {
		//Enable my location icon and activate find location button listener
		googleMap.setMyLocationEnabled(true);
		googleMap.setOnMyLocationButtonClickListener(this);
		googleMap.setOnMyLocationClickListener(this);

		FusedLocationProviderClient fusedLocationClient = new FusedLocationProviderClient(getContext());
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
		Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
		// Return false so that we don't consume the event and the default behavior still occurs
		// (the camera animates to the user's current position).
		return false;
	}

	@Override
	public void onMyLocationClick(@NonNull Location location) {
		Toast.makeText(getContext(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
	}
}