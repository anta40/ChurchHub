package com.mrzon.churchhub;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mrzon.churchhub.model.Denomination;
import com.mrzon.churchhub.model.Helper;
import com.mrzon.churchhub.util.GPSTracker;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

/**
 * AddNewChurch is implementation of Activity to add new church
 * @author Emerson Chan Simbolon
 *
 */
public class AddNewChurch extends RoboActivity {
	
	/**
	 * Progress dialog that shown when the church info send to the server
	 */
    private ProgressDialog pd;
    @InjectView(R.id.pull_refresh_list)
    
    /**
     * Pull to refresh list that will fetch info of the Church
     */
    private PullToRefreshListView pullToRefreshView;
    
    /**
     * List of denomination available
     */
    private List<Denomination> denominations = null;
    
    /**
     * Address of the church
     */
    private String address;
    
    /**
     * Location latitude and longitude of the Church
     */
    private double lat, lon;
    
    /**
     * Provider of the network
     */
    private String provider;
    
    /**
     * Adapter for array that contained in the pulltorefreshview
     */
    private ArrayAdapter<String> mAdapter;
    
    /**
     * Content of the pulltorefreshview
     */
    private String[] mcontent = new String[]{"No denomination found, please pull to refresh"};
    
    /**
     * Content of the pulltorefreshview in arraylist representation
     */
    private ArrayList<String> content = new ArrayList<String>();
    
    
    private boolean needlocation = false;
    
    /**
     * getQueryMap fetch information from URL link parameter to a Map
     * 
     * @param query URL parameter
     * @return map 
     */
    public static Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

    /**
     * getLatLong will return value latitude and longitude in array of double
     * 
     * @param map contains info from URL parameter
     * @return latitude and longitude
     * @throws Exception map not containing latitude and longitude information
     */
    public static Double[] getLatLong(Map<String, String> map) throws Exception {
        Double[] latlong = new Double[2];
        String ll = "";
        if (map.get("ll") != null) {
            ll = map.get("ll");
        } else if (map.get("sll") != null) {
            ll = map.get("sll");
        }
        latlong[0] = Double.parseDouble(ll.split(",")[0]);
        latlong[1] = Double.parseDouble(ll.split(",")[1]);

        return latlong;
    }
    
    /**
     * connectURL open connection based on the given URL
     * @param strURL url that want to be opened
     * @return the established connection
     */
    public URLConnection connectURL(String strURL) {
        URLConnection conn = null;
        try {
            URL inputURL = new URL(strURL);
            conn = inputURL.openConnection();
        } catch (MalformedURLException e) {
            Toast.makeText(getBaseContext(), "URL is not valid", Toast.LENGTH_LONG).show();
        } catch (IOException ioe) {
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
        Intent intent = getIntent();
        needlocation = false;
        if (intent != null) { //handle intent
            Bundle extras = intent.getExtras();
            try {
                String address = (String) extras.get(Intent.EXTRA_SUBJECT);
                String dAddress = (String) extras.get(Intent.EXTRA_TEXT);
                String shortURL = dAddress.split("http")[1];
                shortURL = "http" + shortURL;
                final String url = shortURL;
                ATask task = new ATask();
                task.execute(url);
                this.address = address;
            } catch (Exception e) {
                needlocation = true;
            }
        } else {
            needlocation = true;
        }
       
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

    public void setContent() {
    	
    	
    	final ProgressDialog pd = ProgressDialog.show(this, "Working..", "Fetching denomination", true);

		final Handler handler = new Handler() {
		    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
		    @Override
		    public void handleMessage(Message msg) {
		    	if (denominations == null) {
		            content.addAll(Arrays.asList(mcontent));
		        } else {
		            for (Denomination d : denominations) {
		                content.add(d.getName());
		            }
		        }
		        mAdapter = new ArrayAdapter<String>(AddNewChurch.this, android.R.layout.simple_list_item_1, content);
		        ListView actualListView = pullToRefreshView.getRefreshableView();
		        actualListView.setAdapter(mAdapter);
		        registerForContextMenu(actualListView);		    
		        pd.dismiss();
		    }
		};
		new Thread() {
		    public void run() {
		        try {
		        	 if (needlocation) {
		             	GPSTracker mGPS = new GPSTracker(AddNewChurch.this);
		             	Looper.prepare();
		             	mGPS.fetchLocation();
		             	lat = mGPS.getLatitude();
		             	lon = mGPS.getLongitude();
		             }
		            getDenominationFromCache();
		        } catch (Exception e) {
		            Log.e("threadmessage", e.getMessage());
		        } finally {
		        	handler.sendEmptyMessage(0);
		        }
		    }
		}.start();

    }

    /**
     * getDenominationFromServer fetch list of denomination from server
     * @return array of denomination string
     */
    private String[] getDenominationFromServer() {
        SharedPreferences pref = getPreferences(MODE_MULTI_PROCESS);
        long updateTime = System.currentTimeMillis();
        long l = pref.getLong(Denomination.latestUpdate, -1l);
        List<Denomination> list = Helper.getDenominations(l, false, AddNewChurch.this);
        denominations = list;

        pref.edit().putLong(Denomination.latestUpdate, updateTime).commit();
        String[] str = new String[denominations.size()];

        for (int i = 0; i < str.length; i++) {
            Denomination d = denominations.get(i);
            str[i] = d.getName();
        }
        return str;
    }
    
    /**
     * getDenominationFromCache fetch list of denomination from cache
     */
    private void getDenominationFromCache() {
        SharedPreferences pref = getPreferences(MODE_MULTI_PROCESS);
        long l = pref.getLong(Denomination.latestUpdate, -1l);
        List<Denomination> list = Helper.getDenominations(l, true, AddNewChurch.this);
        denominations = list;
    }
	/**
	 * Set list of action
	 */
    private void setAction() {
        pullToRefreshView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Denomination d = denominations.get(arg2 - 1);
                String str = ((TextView) arg1).getText().toString();
                Toast.makeText(getBaseContext(), "Opening " + str, Toast.LENGTH_SHORT).show();
                CHDialog.AddChurchDialogFragment addDialog = new CHDialog.AddChurchDialogFragment();
                addDialog.setDenomination(d);
                addDialog.setAddress(address);
                addDialog.setLatitude(lat);
                addDialog.setLongitude(lon);
                addDialog.show(getFragmentManager(), "ADD ACTIVITY DIALOG");

            }
        });
    }
    
    /**
     * Set option menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.browse_denomination, menu);
        //		getMenuInflater().inflate(R.id.add_item, menu);
        return true;
    }

    /**
     * Set option item selected
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override
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

    /**
     * Handle app when the app resumed
     */
    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Handle app when the app paused
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

 
    /**
     * AsyncTask that handle fetch from server
     * @author Emerson Chan Simbolon
     *
     */
    private class ATask extends AsyncTask<String, Integer, Double[]> {

        public ATask() {
        }

        @Override
        protected Double[] doInBackground(String... params) {
        	Geocoder geocoder = new Geocoder(AddNewChurch.this);  
        	List<Address> addresses;
        	try {
				addresses = geocoder.getFromLocationName(address, 1);

	            Double[] locs = new Double[2];
				if(addresses.size() > 0) {
	        	    locs[0] = addresses.get(0).getLatitude();
	        	    locs[1] = addresses.get(0).getLongitude();
	        	}
				return locs;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	
            return null;
        }

        @Override
        protected void onPostExecute(Double[] result) {
            if (result != null) {
                lat = result[0];
                lon = result[1];
            }
            if (lat == 0 && lon == 0) {
                Toast.makeText(getBaseContext(), "No coordinate found", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getBaseContext(), "Location is ready", Toast.LENGTH_LONG).show();
            }
            if(pd!=null)
            pd.dismiss();

            super.onPostExecute(result);
        }
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
            if(pd!=null)
            pd.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected String[] doInBackground(Void... arg0) {
            return getDenominationFromServer();
        }
    }
}