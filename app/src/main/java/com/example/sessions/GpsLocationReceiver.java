package com.example.sessions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class GpsLocationReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {

			Toast.makeText(context, "Location Setting Changed",
					Toast.LENGTH_SHORT).show();
		}
		else{

			Toast.makeText(context, "Location Setting Changed",
					Toast.LENGTH_SHORT).show();

		}
	}
}
