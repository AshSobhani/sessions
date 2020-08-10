package com.example.sessions;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
	private View v;
	Cursor c;

	//Create variables for items in the view
	private TextView sessionCountValue;
	private TextView distanceTotalValue;
	private TextView durationTotalValue;

	private TextView distanceValueR;
	private TextView durationValueR;
	private TextView avgSpeedValueR;


	//Variable for decimal accuracy & Set decimal accuracy
	DecimalFormat dp = new DecimalFormat("0.00");

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		//Assign the correct view to the fragment
		v = inflater.inflate(R.layout.fragment_sessions, container, false);

		sessionCountValue = v.findViewById(R.id.sessionCountValue);
		distanceTotalValue = v.findViewById(R.id.distanceTotalValue);
		durationTotalValue = v.findViewById(R.id.durationTotalValue);
		distanceValueR = v.findViewById(R.id.distanceValueR);
		durationValueR = v.findViewById(R.id.durationValueR);
		avgSpeedValueR = v.findViewById(R.id.avgSpeedValueR);

		//upDateFields();

		return v;
	}

	public void upDateFields() {
		//Create a projection of what I want from the database
		String[] projection = {
				"MAX(" + "duration" + ")",
				"MAX(" + "distance" + ")",
				"MAX(" + "avg_speed" + ")",
		};

		//Define the ID I want and query the table
		c = getActivity().getContentResolver().query(SessionsProviderContract.SESSIONS_URI, projection, null, null, null);



		//Set the variables to database values accordingly
		if(c.moveToFirst()) {
			do {
//				name.setText(c.getString(0));
//				dateTime.setText(c.getString(1));
//				duration.setText("" + (c.getLong(2)/6000) + "m");
//				distance.setText(dp.format(c.getDouble(3)) + " m");
//				avgSpeed.setText(dp.format(c.getDouble(4)) + " m/s");
//				description.setText(c.getString(5));
//				rating.setText("" + c.getInt(6));
//				sessionType.setText(c.getString(7));

				Log.d("g54mdp", "" + (c.getLong(1)/6000));

				durationTotalValue.setText("" + (c.getLong(1)/6000) + "m");
				distanceTotalValue.setText(dp.format(c.getDouble(2)) + " m");
				avgSpeedValueR.setText("" + (c.getLong(3)/6000) + "m");
//				distanceValueR
//				durationValueR
//				avgSpeedValueR

			} while(c.moveToNext());
		}
		c.close();
	}


}
