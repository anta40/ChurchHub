package com.mrzon.churchhub;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

import com.mrzon.churchhub.model.Church;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity  {
	@InjectView(R.id.textView1)            private Button nearest; 
	@InjectView(R.id.textView2)            private Button browse; 
	@InjectView(R.id.textView3)            private Button stats; 
	@InjectView(R.id.textView4)            private Button recent; 
	@InjectView(R.id.login)					private Button login;
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
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				CHDialog.BrowseDialogFragment dialog = new CHDialog.BrowseDialogFragment();
				dialog.show(getFragmentManager(), "SHOW BROWSE DIALOG");
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
			
			
			}
		});
		
		login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(ParseUser.getCurrentUser()==null) {
					CHDialog.LoginDialogFragment loginDialog = new CHDialog.LoginDialogFragment();
					loginDialog.setView(login);
					loginDialog.show(getFragmentManager(), "SHOW LOGIN DIALOG");
					if(ParseUser.getCurrentUser()!=null) {
						login.setText("Logout");
					}
				} else {
					AlertDialog dialog = new AlertDialog.Builder (MainActivity.this).create();
					  dialog.setTitle ("Logout");
					  dialog.setMessage ("Are you sure want to logout");
					  dialog.setCancelable (true);
					  dialog.setButton (DialogInterface.BUTTON_POSITIVE, "Logout",
					  new DialogInterface.OnClickListener () {
					    public void onClick (DialogInterface dialog, int buttonId) {
					    	ParseUser.logOut();
					    	login.setText("Login");
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
			login.setText("Login");
		} else {
			login.setText("Logout");
		}
		setStyle();
		setAction();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
