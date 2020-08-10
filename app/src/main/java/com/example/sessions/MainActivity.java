package com.example.sessions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity";
	private int LOCATION_PERMISSION_CODE = 1;
	static final int REQUEST_CHECK_SETTINGS = 1;

	BottomNavigationView bottomNav = null;
	int fragmentFlag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate: Main Activity Open");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Find my nav bar and assign to variable
		bottomNav = findViewById(R.id.bottom_navbar);
		bottomNav.setOnNavigationItemSelectedListener(navListener);


		//Set starting fragment to sessions
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SessionsFragment()).commit();

		//Check if permissions is already on, if not request location permission
		if (ContextCompat.checkSelfPermission(MainActivity.this,
				Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			Log.d(TAG, "onNavigationItemSelected: Permissions On: Access Granted");
		} else {
			Log.d(TAG, "onNavigationItemSelected: Permissions Off: Requesting Access");
			requestLocationPermission();
		}
	}


	//Create a listener to detect changes in selected fragments
	private BottomNavigationView.OnNavigationItemSelectedListener navListener =
			new BottomNavigationView.OnNavigationItemSelectedListener() {
				@Override
				public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
					Fragment selectedFragment = null;

					//Set selected fragment accordingly by whats been selected
					switch (menuItem.getItemId()) {
						case R.id.navSessions:
							fragmentFlag = 0;
							selectedFragment = new SessionsFragment();

							break;
						case R.id.navTrack:
							selectedFragment = new TrackFragment();

//							break;
//						case R.id.navProfile:
//							fragmentFlag = 2;
//							selectedFragment = new ProfileFragment();
//							break;
					}

					//Make sure a fragment has been selected
					if (selectedFragment != null) {
						//Execute set or switch respectfully to the selected fragment
						getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
					}
					return true;
				}
			};

	private void requestLocationPermission() {
		if (ActivityCompat.shouldShowRequestPermissionRationale(this,
				Manifest.permission.ACCESS_FINE_LOCATION)) {
			ActivityCompat.requestPermissions(MainActivity.this,
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
		} else {
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == LOCATION_PERMISSION_CODE) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Log.d(TAG, "onRequestPermissionsResult: Permission Granted");
			} else {
				Log.d(TAG, "onRequestPermissionsResult: Permission Denied");

				//Return to previous fragment
				//returnToFragment();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		//Check the setting request
		if (requestCode == REQUEST_CHECK_SETTINGS) {
			//If its been turned on then do nothing
			if(resultCode == RESULT_OK) {
				Log.d(TAG, "onActivityResult: Setting have been turned on");
			} else {
				//Return to previous fragment
				returnToFragment();
				}
			}

		}

	private void returnToFragment() {
		switch (fragmentFlag) {
			case 0:
				//Return to sessions fragment and check navigation icon
				getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SessionsFragment()).commit();
				bottomNav.getMenu().getItem(0).setChecked(true);
				break;
			case 2:
				//Return to profile fragment and check navigation icon
				getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
				bottomNav.getMenu().getItem(2).setChecked(true);
				break;
		}
	}

}
