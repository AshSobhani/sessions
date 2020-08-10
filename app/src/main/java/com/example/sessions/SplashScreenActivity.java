package com.example.sessions;

import androidx.appcompat.app.AppCompatActivity;
import gr.net.maroulis.library.EasySplashScreen;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class SplashScreenActivity extends AppCompatActivity {
	private static final String TAG = "SplashScreenActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate: Splash Screen Booting...");

		//Configure how the splash screen will look and what activity it will go to
		EasySplashScreen config = new EasySplashScreen(SplashScreenActivity.this)
				.withFullScreen()
				.withTargetActivity(MainActivity.class)
				.withSplashTimeOut(2500)
				.withBackgroundColor(Color.parseColor("#181818"))
				.withAfterLogoText("SESSIONS")
				.withLogo(R.drawable.sessions_logo);

		//Set the text colour and size
		config.getAfterLogoTextView().setTextColor(Color.parseColor("#FFEB3B"));
		config.getAfterLogoTextView().setTextSize(25);

		//Create and view the splash screen
		View easySplashScreen = config.create();
		setContentView(easySplashScreen);
	}
}
