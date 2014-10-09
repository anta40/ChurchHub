package com.mrzon.churchhub;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mrzon.churchhub.adapter.*;
import com.mrzon.churchhub.model.Church;
import com.mrzon.churchhub.model.Country;
import com.mrzon.churchhub.model.Denomination;
import com.mrzon.churchhub.model.Helper;
import com.mrzon.churchhub.model.Province;
import com.mrzon.churchhub.model.Region;
import com.mrzon.churchhub.util.GPSTracker;
import com.mrzon.churchhub.util.Util;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import junit.framework.Assert;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;


/**
 * Nearest Church Activity to display list of nearest church
 * 
 * @author Emerson Chan Simbolon
 */
public class NearestChurchActivity extends RoboActivity {

	/**
	 * RADIUS_MAX of church location that can be found
	 */
    private static final double RADIUS_MAX = 10.0;
    
    /**
     * RADIUS_MIN of church location that can be found 
     */
    private static final double RADIUS_MIN = 2.0;
    
    /**
     * Current radius base that used for search
     */
    private double radius = RADIUS_MIN;
    
    /**
     * Key Event that used to inform of change radius
     */
    public static String RADIUS_CHANGED_EVENT = "RADIUS_CHANGED_EVENT";

    
    /**
     * PullToRefreshListView contains the list of Church that fits radius criteria
     */
    @InjectView(R.id.pull_refresh_list)
    private PullToRefreshListView pullToRefreshView;
    
    
    /**
     * SeekBar to set the search radius
     */
    @InjectView(R.id.radius_seekbar)
    private SeekBar radiusSeekbar;
    
    /**
     * TextView to display current radius
     */
    @InjectView(R.id.radius_field)
    private TextView radiusTextview;
    
    /**
     * List of Church that being found
     */
    private List<Church> churches = null;
    
    /**
     * Routine to fetch the search command
     */
    private GetDataTask task = null;
    
    /**
     * Denomination for search criteria
     */
    private Denomination denomination;

    private ParseGeoPoint currentLocation;

    /**
     * Region for search criteria
     */    
    private Region region;
    
    
    
    /**
     * Adapter to handle nearby church list
     */
    private ListOfNearbyChurchAdapter mAdapter;
    
    /**
     * 
     */
    private String[] mcontent = new String[]{"No church found, please pull to refresh"};
    private ArrayList<String> content = new ArrayList<String>();

    /**
     * Update radius and redo search
     */
    private void updateRadius() {
    	if(task != null)
        task.cancel(true);
        task = new GetDataTask();
        radius = RADIUS_MIN + ((double) radiusSeekbar.getProgress()) / radiusSeekbar.getMax() * 8;
        radiusTextview.setText(Util.getPreetyDistance(radius * 1000));
        task.execute();
    }

    /**
     * getAPIForCoordinate return Foursquare API end point
     * @param lat the latitude
     * @param lon the longitude
     * @return Foursquare API end point
     */
    private String getAPIForCoordinate(double lat, double lon) {
        return "https://api.foursquare.com/v2/venues/search?categoryId=4bf58dd8d48988d132941735&oauth_token=EDO2GW34NJ5QBUVL5YXQTDYK41X2QMFBHS1XCY1FZMJFWNVM&v=20111229&ll=" + lat + "," + lon;
    }

    /**
     * 
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setDisplayHome() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest_church);
        // Show the Up button in the action bar.
        setDisplayHome();
        pullToRefreshView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                // Do work to refresh the list here.
            	if(task != null)
            		task.cancel(true);
            	task = new GetDataTask();
            	task.execute();
            }
        });
        
        new Timer().scheduleAtFixedRate(new GPSTracker(this), 0, 10000);
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

    public void asyncJson(String url) {

        //perform a Google search in just a few lines of code

        final AQuery aq = new AQuery(this);
        aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (json != null) {
                    try {
                        JSONObject response = json.getJSONObject("response");
                        JSONArray jChurches = response.getJSONArray("venues");
                        churches = new ArrayList<Church>();
                        for (int i = 0; i < jChurches.length(); i++) {
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
                        for (Church d : churches) {
                            content.add(d.getName());
                        }
                        mAdapter.notifyDataSetChanged();
                        ATask task = new ATask();
                        task.execute((Church[]) churches.toArray());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //successful ajax call, show status code and json content
                    Toast.makeText(aq.getContext(), status.getCode() + ":" + json.toString(), Toast.LENGTH_LONG).show();

                } else {

                    //ajax error, show error code
                    Toast.makeText(aq.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void setContent() {
        //churches = Helper.getChurches(region, denomination,-1l,true,NearestChurchActivity.this);
        GPSTracker mGPS = new GPSTracker(this);

        mGPS.fetchLocation();
        if (mGPS.canGetLocation()) {
            double mLat = mGPS.getLatitude();
            double mLong = mGPS.getLongitude();
            currentLocation = new ParseGeoPoint(mLat, mLong);
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Church");
            query.whereWithinKilometers("location", new ParseGeoPoint(mLat, mLong), radius);
            final ProgressDialog pd = ProgressDialog.show(NearestChurchActivity.this, "Working..", "Fetch nearest church");
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    mAdapter.notifyDataSetChanged();
                    pd.dismiss();
                }
            };
            query.findInBackground(new FindCallback<ParseObject>() {

                @Override
                public void done(List<ParseObject> arg0, ParseException arg1) {
                	
                    churches.clear();
                    for (int i = 0; i < arg0.size(); i++) {
                        churches.add(Helper.createChurch(arg0.get(i), null));
                    }
                    handler.handleMessage(null);
                }
            });
            //AsyncTask<Params, Progress, Result>
            //String api = getAPIForCoordinate(mLat, mLong);
            //asyncJson(api);
        } else {
            // can't get the location
        }
        churches = new ArrayList<Church>();
        mAdapter = new ListOfNearbyChurchAdapter(getApplicationContext(), currentLocation, churches);
        ListView actualListView = this.pullToRefreshView.getRefreshableView();
        radiusTextview.setText((radius + "") + " km");
        actualListView.setAdapter(mAdapter);
        registerForContextMenu(actualListView);
    }

    private void setAction() {
        pullToRefreshView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Church d = churches.get(arg2 - 1);
                Toast.makeText(getBaseContext(), "Opening church " + d.getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), ChurchActivity.class);
                intent.putExtra("church", d);
                startActivity(intent);
            }
        });

        radiusSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                updateRadius();
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

            }
        });
    }

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

    private class ATask extends AsyncTask<Church, Integer, Integer> {

        public ATask() {

        }

        @Override
        protected Integer doInBackground(Church... params) {
            Toast.makeText(getBaseContext(), "Start saving", Toast.LENGTH_LONG).show();
            for (int i = 0; i < params.length; i++) {
                try {
                    params[i].submitIfNotExistedYet();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            return 0;
        }
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {
    	ProgressDialog pd = null;
    	@Override
        protected void onPreExecute() {
    		super.onPreExecute();
    		pd = ProgressDialog.show(NearestChurchActivity.this, "Working..", "Fetch nearest church");
    	}
    	
        @Override
        protected void onPostExecute(String[] result) {
            // Call onRefreshComplete when the list has been refreshed.
//			content.clear();
//			content.addAll(Arrays.asList(result));
            mAdapter.notifyDataSetChanged();

            // Call onRefreshComplete when the list has been refreshed.
            pullToRefreshView.onRefreshComplete();
            pd.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected String[] doInBackground(Void... arg0) {
            SharedPreferences pref = getPreferences(MODE_MULTI_PROCESS);
            String temp = pref.getString("LATEST_LONGITUDE", null);
            if(temp == null) {
            	return null;
            }
            double mLat = Double.parseDouble(pref.getString("LATEST_LATITUDE", "0.0"));
            double mLong = Double.parseDouble(pref.getString("LATEST_LONGITUDE", "0.0"));
            currentLocation = new ParseGeoPoint(mLat, mLong);
            Assert.assertNotNull(mAdapter);
            mAdapter.setCurrentPoint(currentLocation);

                //String api = getAPIForCoordinate(mLat, mLong);
//				asyncJson(api);
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Church");
                query.whereWithinKilometers("location", new ParseGeoPoint(mLat, mLong), radius);
                List<ParseObject> chs;
                try {
                    chs = query.find();
                    churches.clear();
                    for (int i = 0; i < chs.size(); i++) {
                        churches.add(Helper.createChurch(chs.get(i), null));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            return null;
        }
    }

}
