package com.zoop.checkout.app;

import com.zoop.zoopandroidsdk.ZoopAPI;
import com.zoop.zoopandroidsdk.commons.ZLog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class ZCLActivity extends AppCompatActivity {
	
	@Override
	protected void onStart() {
		super.onStart();		
		//Preferences.initialize(getApplicationContext());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			Intent intent = new Intent(ZCLActivity.this, StartupActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
		}

//		getSupportActionBar().setTitle("ADS KDSAS");
/*
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
*/
		getSupportActionBar().setCustomView(R.layout.zce_action_bar_basic);
		ZoopAPI.resetApplicationContext(getApplicationContext());


		((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.imageViewZoopLogo)).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				ZLog.t(300034);
			}
		});


	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString("message", "This is my message to be reloaded");
		super.onSaveInstanceState(outState);
	}



}

