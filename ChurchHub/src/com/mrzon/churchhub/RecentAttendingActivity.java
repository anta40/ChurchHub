package com.mrzon.churchhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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
import com.mrzon.churchhub.model.Attendance;
import com.mrzon.churchhub.model.Helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;


/**
 * RecentAttendingActivity to display recent attended church
 * @author Emerson Chan Simbolon
 *
 */
public class RecentAttendingActivity extends RoboActivity {

	/**
	 * PullToRefreshView to display recent attended Church
	 */
    @InjectView(R.id.pull_refresh_list)
    private PullToRefreshListView pullToRefreshView;

    /**
     * List of recent attendance of the user
     */
    private List<Attendance> attendances = null;
    
    /**
     * Adapter of the pullToRefreshView
     */
    private ArrayAdapter<String> mAdapter;
    
    
    /**
     * Content of the Recent Church
     */
    private String[] mcontent = new String[]{"No recent attending"};
    
    /**
     * Content of the Recent Church
     */
    private ArrayList<String> content = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_denomination);
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
     * Set style to the activity
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
     * Set content to the activity
     */
    public void setContent() {
        this.getAttendanceFromCache();
        if (this.attendances == null) {
            content.addAll(Arrays.asList(mcontent));
        } else {
            for (Attendance d : attendances) {
                content.add(d.getChurch().getName());
            }
        }
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, content);
        ListView actualListView = this.pullToRefreshView.getRefreshableView();
        actualListView.setAdapter(mAdapter);
        registerForContextMenu(actualListView);
        TextView tv = new TextView(getApplicationContext());
        tv.setText("No recent attending");
        pullToRefreshView.setEmptyView(tv);
    }

    /**
     * Get recent attendance from server
     * @return list of Church that attended
     */
    private String[] getAttendanceFromServer() {
        SharedPreferences pref = getPreferences(MODE_MULTI_PROCESS);
        long updateTime = System.currentTimeMillis();
        long l = pref.getLong("ATTENDANCE_HISTORY", -1l);
        List<Attendance> list = Helper.getAttendances(l, false, this);
        attendances = list;

        pref.edit().putLong("ATTENDANCE_HISTORY", updateTime).commit();
        String[] str = new String[attendances.size()];

        for (int i = 0; i < str.length; i++) {
            Attendance d = attendances.get(i);
            str[i] = d.getChurch().getName();
        }
        return str;
    }

    /**
     * Get attendance from cache
     */
    private void getAttendanceFromCache() {
        this.attendances = Helper.getAttendances(-1, true, this);
    }

    /**
     * Set action to activity
     */
    private void setAction() {
        // TODO Auto-generated method stub
        pullToRefreshView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Attendance d = attendances.get(arg2 - 1);

                String str = ((TextView) arg1).getText().toString();
                Toast.makeText(getBaseContext(), "Opening " + str, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), ChurchActivity.class);
                intent.putExtra(MainActivity.CHURCH_EXTRA, d.getChurch());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_recent_attending, menu);
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

            return getAttendanceFromServer();
        }
    }

}
