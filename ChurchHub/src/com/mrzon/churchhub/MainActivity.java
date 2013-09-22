package com.mrzon.churchhub;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import com.mrzon.churchhub.util.Util;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity  {
	@InjectView(R.id.main_nearby_button)            private Button nearest; 
	@InjectView(R.id.main_browse_button)            private Button browse; 
	@InjectView(R.id.main_stats_button)            private Button stats; 
	@InjectView(R.id.main_history_button)            private Button recent; 
	@InjectView(R.id.login)					private Button login;
	@InjectView(R.id.signup)				private Button signup;
	@InjectView(R.id.loginAsTV) 			private TextView loginAsLabel;
	@InjectView(R.id.usernameField) 			private TextView usernameField;

	public static final String CHURCH_EXTRA = "church";
	public void setStyle() {
		Typeface roboto_ti = Typeface.createFromAsset(
				this.getAssets(), 
				"fonts/Roboto-ThinItalic.ttf");
		Typeface roboto_l = Typeface.createFromAsset(
				this.getAssets(), 
				"fonts/Roboto-Light.ttf");
		nearest.setTypeface(roboto_l);
		browse.setTypeface(roboto_l);
		stats.setTypeface(roboto_l);
		recent.setTypeface(roboto_l);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setAction() {
		nearest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, NearestChurchActivity.class);
				startActivity(intent);
			}
		});
		browse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CHDialog.BrowseDialogFragment dialog = new CHDialog.BrowseDialogFragment();
				dialog.show(getFragmentManager(), "SHOW BROWSE DIALOG");
			}
		});
		signup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CHDialog.SignUpDialogFragment loginDialog = new CHDialog.SignUpDialogFragment();
				loginDialog.show(getFragmentManager(), "SHOW SIGNUP DIALOG");
			}

		});
		stats.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		recent.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, RecentAttendingActivity.class);
				startActivity(intent);
			}
		});

		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(ParseUser.getCurrentUser()==null) {
					CHDialog.LoginDialogFragment loginDialog = new CHDialog.LoginDialogFragment();
					loginDialog.setView(login);
					loginDialog.show(getFragmentManager(), "SHOW LOGIN DIALOG");
				} else {
					AlertDialog dialog = new AlertDialog.Builder (MainActivity.this).create();
					dialog.setTitle ("Logout");
					dialog.setMessage ("Are you sure want to logout");
					dialog.setCancelable (true);
					dialog.setButton (DialogInterface.BUTTON_POSITIVE, "Logout",
							new DialogInterface.OnClickListener () {
						public void onClick (DialogInterface dialog, int buttonId) {
							ParseUser.logOut();
							Intent intent = new Intent(Util.LOGOUT_EVENT);
							LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
						}
					});
					dialog.setButton (DialogInterface.BUTTON_NEGATIVE, "Cancel",
							new DialogInterface.OnClickListener () {
						public void onClick (DialogInterface dialog, int buttonId) {

						}
					});
					dialog.show();
				}
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Parse.initialize(this, "hJTeSGqVGmVU7vJHSnv2Zl2DbBbSEquKoThko16o", "wE3CXaYe77U6UAqYlFozN4n18omhVyfS1qPu9gkN");
		ParseAnalytics.trackAppOpened(getIntent());
		setContentView(R.layout.activity_main);
		if(ParseUser.getCurrentUser()==null) {
			loginAsLabel.setVisibility(View.INVISIBLE);
			usernameField.setVisibility(View.INVISIBLE);
			login.setText("Login");
			signup.setVisibility(View.VISIBLE);
		} else {
			loginAsLabel.setVisibility(View.VISIBLE);
			usernameField.setText(ParseUser.getCurrentUser().getUsername());
			usernameField.setVisibility(View.VISIBLE);
			login.setText("Logout");
			signup.setVisibility(View.INVISIBLE);
		}
		setStyle();
		setAction();
		LocalBroadcastManager.getInstance(this).registerReceiver(loginReceiver,
				new IntentFilter(Util.LOGIN_EVENT));
		LocalBroadcastManager.getInstance(this).registerReceiver(logoutReceiver,
				new IntentFilter(Util.LOGOUT_EVENT));
	}
	// Our handler for received Intents. This will be called whenever an Intent
	// with an action named "custom-event-name" is broadcasted.
	private BroadcastReceiver loginReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent
			String message = intent.getStringExtra("message");
			signup.setVisibility(View.INVISIBLE);
			login.setText("Logout");
			loginAsLabel.setVisibility(View.VISIBLE);
			usernameField.setText(ParseUser.getCurrentUser().getUsername());
			usernameField.setVisibility(View.VISIBLE);
			Log.d("receiver", "Got message: " + message);
		}
	};

	private BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent
			signup.setVisibility(View.VISIBLE);
			login.setText("Login");
			loginAsLabel.setVisibility(View.INVISIBLE);
			usernameField.setVisibility(View.INVISIBLE);
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.add_church:
			Intent intent = new Intent(getBaseContext(), AddNewChurch.class);
			startActivity(intent);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	@Override
	protected void onDestroy() {
		// Unregister since the activity is about to be closed.
		// This is somewhat like [[NSNotificationCenter defaultCenter] removeObserver:name:object:] 
		LocalBroadcastManager.getInstance(this).unregisterReceiver(loginReceiver);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(logoutReceiver);
		super.onDestroy();
	}
}
