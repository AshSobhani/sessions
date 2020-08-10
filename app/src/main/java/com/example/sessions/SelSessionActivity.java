package com.example.sessions;

import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SelSessionActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {
    private static final String TAG = "SelSessionActivity";
    private TextView name, dateTime, duration, distance, avgSpeed, description, rating, sessionType;

    int id;
    private Cursor c;

    private MapView mapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    //Variable for decimal accuracy & Set decimal accuracy
    DecimalFormat dp = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: creating selected session...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sel_session);

        //Create toolbar
        Toolbar toolbar = findViewById(R.id.include);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get ID of session from users previous click
        if(getIntent().getExtras()!=null) {
            //Unpack bundle from recipes activity
            Bundle bundle = getIntent().getExtras();
            id = bundle.getInt("boxId");
        }

        //Assign the views to variables
        assignViews();

        //Get saved instances for the map
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        //Find the map view in the xml
        mapView = (MapView) findViewById(R.id.googleMap);
        mapView.onCreate(mapViewBundle);

        updateSessionFields();
    }

    public void updateSessionFields() {
        //Create a projection of what I want from the database
        String[] projection = {
                "name",
                "date_Time",
                "duration",
                "distance",
                "avg_speed",
                "description",
                "rating",
                "session_type",
        };

        //Define the ID I want and query the table
        String [] sessionID = {"" + id};
        c = getContentResolver().query(SessionsProviderContract.SESSIONS_URI, projection, SessionsProviderContract.SESSIONS_ID + "=?", sessionID, null);

        //Set the variables to database values accordingly
        if(c.moveToFirst()) {
            do {
                name.setText(c.getString(0));
                dateTime.setText(c.getString(1));
                duration.setText("" + (c.getLong(2)/6000) + "m");
                distance.setText(dp.format(c.getDouble(3)) + " m");
                avgSpeed.setText(dp.format(c.getDouble(4)) + " m/s");
                description.setText(c.getString(5));
                rating.setText("" + c.getInt(6));
                sessionType.setText(c.getString(7));

            } while(c.moveToNext());
        }
        c.close();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        //Enable my location icon and activate find location button listener
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMyLocationClickListener(this);

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

    public void assignViews() {
        //Assign view texts to variables
        name = findViewById(R.id.sessionName);
        dateTime = findViewById(R.id.dateTimeValue);
        duration = findViewById(R.id.durationValue);
        distance = findViewById(R.id.distanceValue);
        avgSpeed = findViewById(R.id.avgSpeedValue);
        description = findViewById(R.id.descriptionValue);
        rating = findViewById(R.id.ratingValue);
        sessionType = findViewById(R.id.sessionTypeValue);
    }

    public void onDeleteRecipe(View v) {
        getContentResolver().delete(SessionsProviderContract.SESSIONS_URI, "_id=?", new String[]{"" + id});
        finish();
    }
}
