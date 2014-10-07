package com.mrzon.churchhub;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import com.mrzon.churchhub.model.Country;
import com.mrzon.churchhub.model.Helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
/**
 * Browse Country Activity, viewed when:
 * 1. Want to add new Country
 * 2. Want to add new Region
 * 3. Want to browse Region
 * 4. Want to browse Country
 * 
 * @author Emerson Chan Simbolon
 */
public class BrowseCountryActivity extends RoboActivity {

	
	/**
	 * pulltorefreshlistview will fetch information when being pulled
	 */
    @InjectView(R.id.pull_refresh_list)
    private PullToRefreshListView pullToRefreshView;
    
    
	/**
	 * List of countries that fetched
	 */
    private List<Country> countries = null;
    
    
    /**
     * Adapter to the pulltorefreshview of the country name string
     */
    private ArrayAdapter<String> mAdapter;
    
    
    /**
     * String Array of country name
     */
    private String[] mcontent = new String[]{"No country found, please pull to refresh"};
    
    
    /**
     * List of country name
     */
    private ArrayList<String> content = new ArrayList<String>();

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_country);
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

    /**
     * Set style of browse country layout
     */
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

    
    /**
     * Get list of country from server
     * @return country name 
     */
    private String[] getCountryFromServer() {
        SharedPreferences pref = getPreferences(MODE_MULTI_PROCESS);
        long updateTime = System.currentTimeMillis();
        long l = pref.getLong(Country.latestUpdate, -1l);
        List<Country> list = Helper.getCountries(l, false, this);
        this.countries = list;

        pref.edit().putLong(Country.latestUpdate, updateTime).commit();
        String[] str = new String[countries.size() + 1];

        for (int i = 0; i < str.length - 1; i++) {
            Country d = countries.get(i);
            str[i] = d.getName();
        }
        str[str.length - 1] = "Unspecified";
        return str;
    }
    
    /**
     * get country name from cache
     */
    private void getCountryFromCache() {
        SharedPreferences pref = getPreferences(MODE_MULTI_PROCESS);
        long l = pref.getLong(Country.latestUpdate, -1l);
        List<Country> list = Helper.getCountries(l, true, this);
        countries = list;
    }
    
    /**
     * set content to the layout
     */
    public void setContent() {
        getCountryFromCache();
        if (this.countries == null) {
            content.addAll(Arrays.asList(mcontent));
        } else {
            for (Country d : countries) {
                content.add(d.getName());
            }
            content.add("Unspecified");
        }
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, content);
        ListView actualListView = this.pullToRefreshView.getRefreshableView();
        actualListView.setAdapter(mAdapter);
        registerForContextMenu(actualListView);
        TextView tv = new TextView(getApplicationContext());
        tv.setText("No country is available, add one");
        pullToRefreshView.setEmptyView(tv);
    }
    
    
    /**
     * Set action to the layout
     */
    private void setAction() {
        // TODO Auto-generated method stub
        pullToRefreshView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Country d = countries.get(arg2 - 1);
                String str = ((TextView) arg1).getText().toString();
                Toast.makeText(getBaseContext(), "Opening " + str, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), BrowseChurchActivity.class);
                intent.putExtra("country", d);
                startActivity(intent);
            }
        });
    }

    
    /**
     * Set option menu to the layout
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_browse_country, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_country:
                CHDialog.AddCountryDialogFragment addDialog = new CHDialog.AddCountryDialogFragment();
                addDialog.show(getFragmentManager(), "ADD ACTIVITY DIALOG");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    
    /**
     * Fetch Country Information in background
     * @author Emerson Chan Simbolon
     */
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
            return getCountryFromServer();
        }
    }
}