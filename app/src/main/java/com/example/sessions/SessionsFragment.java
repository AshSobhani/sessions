package com.example.sessions;

import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SessionsFragment extends Fragment {
	private static final String TAG = "SessionsFragment";
	private View v;
	private int SELSESSION_ACTIVITY_REQUEST_CODE;
	private boolean LOCATION_PERMISSION_ON;

	private SimpleCursorAdapter dataAdapter;
	private Cursor c;

	private ListView sessionListView;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		//Assign the correct view to the fragment
		v = inflater.inflate(R.layout.fragment_sessions, container, false);


		//Make sure location setting is on before allowing the user to track
		ensureLocationSettingInOn();

		//Update the sessions list
		updateSessionsList();

		return v;
	}

	public void updateSessionsList() {
		//Get sessions list view
		sessionListView = (ListView) v.findViewById(R.id.sessionsList);

		String[] columns = new String[] {
				SessionsProviderContract.SESSIONS_ID,
				SessionsProviderContract.SESSIONS_NAME,
				SessionsProviderContract.SESSIONS_DATE_TIME
		};

		c = getContext().getContentResolver().query(SessionsProviderContract.SESSIONS_URI, columns, null, null, null);

		//Mogul creators...
		int[] to = new int[] {
				R.id.boxId,
				R.id.sessionName,
				R.id.sessionDateTime,
		};
		dataAdapter = new SimpleCursorAdapter(
				getContext(), R.layout.session_row,
				c,
				columns,
				to,
				0);

		sessionListView.setAdapter(dataAdapter);

		//Create intent to got to selected recipe
		final Intent intent = new Intent(getContext(), SelSessionActivity.class);

		sessionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				int id = Integer.parseInt(((TextView) view.findViewById(R.id.boxId)).getText().toString());
				intent.putExtra("boxId", id);
				startActivityForResult(intent, SELSESSION_ACTIVITY_REQUEST_CODE);
			}
		});
	}

	protected void ensureLocationSettingInOn() {
		Log.d(TAG, "ensureLocationSettingInOn: Checking location setting...");
		//Create a location request
		LocationRequest locationRequest = LocationRequest.create();
		locationRequest.setInterval(10000);
		locationRequest.setFastestInterval(5000);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		//Add the location request
		LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
				.addLocationRequest(locationRequest);

		//Check location settings
		SettingsClient client = LocationServices.getSettingsClient(getActivity());
		Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

		//If the location settings are on then send success log
		task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
			@Override
			public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
				LOCATION_PERMISSION_ON = true;
				Log.d(TAG, "onSuccess: Location setting success!");
			}
		});

		//If the location settings are off then request user to turn it on
		task.addOnFailureListener(getActivity(), new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Log.d(TAG, "onFailure: Location setting failure");
				if (e instanceof ResolvableApiException) {
					// Location settings are not turned on
					try {
						// Show pop up requesting location to be turned on and check the result in onActivityResult().
						ResolvableApiException resolvable = (ResolvableApiException) e;
						resolvable.startResolutionForResult(getActivity(), MainActivity.REQUEST_CHECK_SETTINGS);
					} catch (IntentSender.SendIntentException sendEx) {
						// Ignore the error.
					}
				}
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		updateSessionsList();
		super.onActivityResult(SELSESSION_ACTIVITY_REQUEST_CODE, resultCode, data);
	}
}
