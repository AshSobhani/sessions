package com.example.sessions;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
	public static final String CHANNEL_ID = "TrackingService";

	@Override
	public void onCreate() {
		super.onCreate();

		createNotificationChannel();
	}

	private void createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			//Make instance of the notification channel
			NotificationChannel serviceChannel = new NotificationChannel(
					CHANNEL_ID,
					"Example Service Channel",
					NotificationManager.IMPORTANCE_DEFAULT
			);

			//Create the channel
			NotificationManager manager = getSystemService(NotificationManager.class);
			manager.createNotificationChannel(serviceChannel);
		}
	}
}
