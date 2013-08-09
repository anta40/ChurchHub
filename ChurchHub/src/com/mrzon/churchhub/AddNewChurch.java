package com.mrzon.churchhub;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.mrzon.churchhub.model.Denomination;
import com.mrzon.churchhub.model.Helper;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class AddNewChurch extends RoboActivity {

	public static Map<String, String> getQueryMap(String query)  
	{  
	    String[] params = query.split("&");  
	    Map<String, String> map = new HashMap<String, String>();  
	    for (String param : params)  
	    {  
	        String name = param.split("=")[0];  
	        String value = param.split("=")[1];  
	        map.put(name, value);  
	    }  
	    return map;  
	}
	public static Double[] getLatLong(Map<String,String> map) throws Exception{
		Double []latlong = new Double[2];
		String ll = "";
		if(map.get("ll")!=null) {
			ll = map.get("ll");
		} else if(map.get("sll")!=null) {
			ll = map.get("sll");
		}
		latlong[0] = Double.parseDouble(ll.split(",")[0]);
		latlong[1] = Double.parseDouble(ll.split(",")[1]);
	
		return latlong;
	}
	
	private class ATask extends AsyncTask<String, Integer, Double[]>
	{

		public ATask()
		{
		}

		@Override
		protected Double[] doInBackground(String... params) 
		{
			URLConnection urlConn =  connectURL(params[0]);
			urlConn.getHeaderFields();	
			System.out.println("Original URL: "+ urlConn.getURL());
			String url = urlConn.getURL().toExternalForm();
			Map<String,String> urlQuery = getQueryMap(url);
			Double[] locs = null;
			try{
				locs = getLatLong(urlQuery);
			} catch (Exception e) {
				Toast.makeText(getBaseContext(), "No coordinate found", Toast.LENGTH_LONG).show();
			}
			return locs;
		}   

		@Override
		protected void onPostExecute(Double[] result) 
		{
			lat = result[0];
			lon = result[1]; 
			if(lat == 0 && lon == 0) {
				Toast.makeText(getBaseContext(), "No coordinate found", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getBaseContext(), "Location is ready", Toast.LENGTH_LONG).show();
			}
		}
	}

	@InjectView(R.id.pull_refresh_list)		 	private PullToRefreshListView pullToRefreshView;
	private List<Denomination> denominations = null;
	private String address;
	private double lat,lon;
	URLConnection connectURL(String strURL) {
		URLConnection conn =null;
		try {
			URL inputURL = new URL(strURL);
			conn = inputURL.openConnection();
		}catch(MalformedURLException e) {
			Toast.makeText(getBaseContext(), "URL is not valid", Toast.LENGTH_LONG).show();
		}catch(IOException ioe) {
			Toast.makeText(getBaseContext(), "Can not connect to the URL", Toast.LENGTH_LONG).show();
		}
		return conn;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_church);
		pullToRefreshView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				new GetDataTask().execute();
			}
		});
		setStyle();
		setAction();
		setContent();
		Intent intent = getIntent();
		if(intent!=null) {
			Bundle extras = intent.getExtras();
			String address = (String) extras.get(Intent.EXTRA_SUBJECT);
			String dAddress = (String) extras.get(Intent.EXTRA_TEXT);
			String shortURL = dAddress.split("http")[1];
			shortURL = "http"+shortURL;
			System.out.println("Short URL: "+ shortURL);
			final String url =shortURL;
			ATask task = new ATask();
			task.execute(url);
			this.address = address;
		}

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

	public void setContent() {
		this.getDenominationFromCache();
		if(this.denominations==null) {
			content.addAll(Arrays.asList(mcontent));
		} else {
			for(Denomination d : denominations) {
				content.add(d.getName());
			}
		}
		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, content);
		ListView actualListView = this.pullToRefreshView.getRefreshableView();
		actualListView.setAdapter(mAdapter);
		registerForContextMenu(actualListView);
	}

	private String[] getDenominationFromServer() {
		SharedPreferences pref = getPreferences(MODE_MULTI_PROCESS);
		long updateTime = System.currentTimeMillis();
		long l = pref.getLong(Denomination.latestUpdate, -1l);
		List<Denomination> list = Helper.getDenominations(l, false, AddNewChurch.this);
		denominations = list;

		pref.edit().putLong(Denomination.latestUpdate, updateTime).commit();
		String[] str = new String[denominations.size()];

		for(int i = 0; i < str.length; i++) {
			Denomination d = denominations.get(i);
			str[i] = d.getName();
		}		
		return str;
	}

	private void getDenominationFromCache() {
		SharedPreferences pref = getPreferences(MODE_MULTI_PROCESS);
		long l = pref.getLong(Denomination.latestUpdate, -1l);
		List<Denomination> list = Helper.getDenominations(l, true, AddNewChurch.this);
		denominations = list;
	}


	private void setAction() {
		pullToRefreshView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Denomination d = denominations.get(arg2-1);
				String str = ((TextView) arg1).getText().toString();
				Toast.makeText(getBaseContext(),"Opening "+str, Toast.LENGTH_SHORT).show();
				CHDialog.AddChurchDialogFragment addDialog = new CHDialog.AddChurchDialogFragment();
				addDialog.setDenomination(d);
				addDialog.setAddress(address);
				addDialog.setLatitude(lat);
				addDialog.setLongitude(lon);
				addDialog.show(getFragmentManager(), "ADD ACTIVITY DIALOG");

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

			return getDenominationFromServer();
		}
	}
	private ArrayAdapter<String> mAdapter;
	private String[] mcontent = new String[]{"No denomination found, please pull to refresh"};
	private ArrayList<String> content= new ArrayList<String>();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.browse_denomination, menu);
		//		getMenuInflater().inflate(R.id.add_item, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.add_item:
			CHDialog.AddDenominationDialogFragment addDialog = new CHDialog.AddDenominationDialogFragment();
			addDialog.show(getFragmentManager(), "ADD ACTIVITY DIALOG");
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}