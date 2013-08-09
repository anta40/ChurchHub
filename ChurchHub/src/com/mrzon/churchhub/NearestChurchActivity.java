package com.mrzon.churchhub;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.mrzon.churchhub.model.Church;
import com.mrzon.churchhub.model.Country;
import com.mrzon.churchhub.model.Denomination;
import com.mrzon.churchhub.model.Helper;
import com.mrzon.churchhub.model.Province;
import com.mrzon.churchhub.model.Region;
import com.mrzon.churchhub.util.GPSTracker;
import com.parse.ParseException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;

public class NearestChurchActivity extends RoboActivity {

	@InjectView(R.id.pull_refresh_list)		 	private PullToRefreshListView pullToRefreshView;

	private List<Church> churches = null;

	/**
	 * 
	 */

	private Denomination denomination;
	private double lat;
	private double lon;
	private String getAPIForCoordinate(double lat, double lon) {
		return "https://api.foursquare.com/v2/venues/search?categoryId=4bf58dd8d48988d132941735&oauth_token=EDO2GW34NJ5QBUVL5YXQTDYK41X2QMFBHS1XCY1FZMJFWNVM&v=20111229&ll="+lat+","+lon;		
	}
	private Region region;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nearest_church);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		pullToRefreshView.setOnRefreshListener(new OnRefreshListener<ListView>() {
		    @Override
		    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		        // Do work to refresh the list here.
		        new GetDataTask().execute();
		    }
		});
		setStyle();
		setAction();
		setContent();
	}


	public void setStyle() {
		Typeface roboto_ti = Typeface.createFromAsset(
				this.getAssets(), 
				"fonts/Roboto-ThinItalic.ttf");
		Typeface roboto_t = Typeface.createFromAsset(
				this.getAssets(), 
				"fonts/Roboto-Thin.ttf");
		Typeface roboto_l = Typeface.createFromAsset(
				this.getAssets(), 
				"fonts/Roboto-Light.ttf");

	}

	
	private class ATask extends AsyncTask<Church, Integer, Integer>
	{

		public ATask()
		{
			
		}

		@Override
		protected Integer doInBackground(Church... params) 
		{
			Toast.makeText(getBaseContext(), "Start saving", Toast.LENGTH_LONG).show();
			for(int i = 0; i < params.length; i++) {
				try {
					params[i].submitIfNotExistedYet();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return 0;
		}   
	}
	
	public void asyncJson(String url){

		//perform a Google search in just a few lines of code

		final AQuery aq = new AQuery(this);
		aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) {
				if(json != null){
					try {
						JSONObject response = json.getJSONObject("response");
						JSONArray jChurches = response.getJSONArray("venues");
						churches = new ArrayList<Church>();
						for(int i = 0; i < jChurches.length(); i++) {
							Church c = new Church();
							JSONObject jChurch = jChurches.getJSONObject(i);
							c.setName(jChurch.getString("name"));
							c.setFoursquareId(jChurch.getString("id"));
							JSONObject location = jChurch.getJSONObject("location");
							c.setAddress(location.getString("address"));
							c.setLat(location.getDouble("lat"));
							c.setLon(location.getDouble("lng"));
							c.setDistance(location.getInt("distance"));
							Country country = new Country();
							country.setName(location.getString("country"));
							country.setIso(location.getString("cc"));
							Province province = new Province();
							province.setName(location.getString("state"));
							province.setCountry(country);
							Region region = new Region();
							region.setName(location.getString("city"));
							churches.add(c);
						}
						content.clear();
						for(Church d : churches) {
							content.add(d.getName());
						}
						mAdapter.notifyDataSetChanged();
						ATask task = new ATask();
						task.execute((Church[]) churches.toArray());

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					//successful ajax call, show status code and json content
					Toast.makeText(aq.getContext(), status.getCode() + ":" + json.toString(), Toast.LENGTH_LONG).show();

				}else{

					//ajax error, show error code
					Toast.makeText(aq.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	public void setContent() {
		//churches = Helper.getChurches(region, denomination,-1l,true,NearestChurchActivity.this);
		GPSTracker mGPS = new GPSTracker(this);


		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, content);
		ListView actualListView = this.pullToRefreshView.getRefreshableView();
		actualListView.setAdapter(mAdapter);
		registerForContextMenu(actualListView);
		if(mGPS.canGetLocation() ){
			double mLat=mGPS.getLatitude();
			double mLong=mGPS.getLongitude();
			String api = getAPIForCoordinate(mLat, mLong);
			asyncJson(api);
		}else{
			// can't get the location
		}

	}
	private void setAction() {
		// TODO Auto-generated method stub
		pullToRefreshView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Church d = churches.get(arg2-1);
				String str = ((TextView) arg1).getText().toString();
				Toast.makeText(getBaseContext(),"Opening church "+str, Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getBaseContext(), ChurchActivity.class);
				intent.putExtra("church", d);
				startActivity(intent);
			}
		});
	}
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		@Override
		protected void onPostExecute(String[] result) {
			// Call onRefreshComplete when the list has been refreshed.
			content.clear();
			content.addAll(Arrays.asList(result));
			mAdapter.notifyDataSetChanged();

			// Call onRefreshComplete when the list has been refreshed.
			pullToRefreshView.onRefreshComplete();
			super.onPostExecute(result);
		}

		@Override
		protected String[] doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			
			GPSTracker mGPS = new GPSTracker(NearestChurchActivity.this);

			if(mGPS.canGetLocation() ){
				double mLat=mGPS.getLatitude();
				double mLong=mGPS.getLongitude();
				String api = getAPIForCoordinate(mLat, mLong);
				asyncJson(api);
			}else{
				// can't get the location
			}
			String[] str = new String[churches.size()];
			for(int i = 0; i < str.length; i++) {
				Church d = churches.get(i);
				str[i] = d.getName();
			}
			return str;
		}
	}
	private ArrayAdapter<String> mAdapter;
	private String[] mcontent = new String[]{"No church found, please pull to refresh"};
	private ArrayList<String> content= new ArrayList<String>();


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_nearest_church, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
