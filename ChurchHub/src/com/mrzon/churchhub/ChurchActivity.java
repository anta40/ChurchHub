package com.mrzon.churchhub;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mrzon.churchhub.adapter.ActiveWorshipAdapter;
import com.mrzon.churchhub.model.Attendance;
import com.mrzon.churchhub.model.Church;
import com.mrzon.churchhub.model.Helper;
import com.mrzon.churchhub.model.User;
import com.mrzon.churchhub.model.UserHelper;
import com.mrzon.churchhub.model.Worship;
import com.mrzon.churchhub.model.WorshipWeek;
import com.mrzon.churchhub.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;


/**
 * Church Activity to display church information
 * 
 * @author Emerson Chan Simbolon
 */
public class ChurchActivity extends RoboActivity {
    
	/**
	 * TextView to display Church Name
	 */
	@InjectView(R.id.church_name)
    private TextView name;
    
	/**
	 * TextView to display Church Address
	 */
	@InjectView(R.id.church_address)
    private TextView address;
    
	/**
	 * Button to open church location on Map 
	 */
	@InjectView(R.id.map_button)
    private ImageButton mapButton;
    
	/**
	 * PullToRefreshListView view to display activity of user on the church page
	 */
	@InjectView(R.id.pull_refresh_list)
    private PullToRefreshListView pullToRefreshView;
    
	/**
	 * ListView to display list of active worship in a day
	 */
	@InjectView(R.id.list_of_active_worship)
    private ListView worshipListView;
    
	/**
	 * Adapter to display string of activity
	 */
	private ArrayAdapter<String> mAdapter;

	/**
	 * Adapter to display string of activity
	 */
	private ActiveWorshipAdapter mActiveWorshipAdapter;	
	
	/**
	 * Content of the activity list
	 */
	private String[] mcontent = new String[]{"No attendee"};
    
	/**
	 * Content of the activity list
	 */
	private ArrayList<String> content = new ArrayList<String>();
    
	/**
	 * ArrayList of list of today's worship week
	 */
	private ArrayList<WorshipWeek> worshipWeeks = new ArrayList<WorshipWeek>();
    
	/**
	 * List of today's worship
	 */
	private List<Worship> todaysWorships;
    
	/**
	 * The Church instance
	 */
	private Church church;
    
	/**
	 * List of Attendance instance
	 */
	private ArrayList<Attendance> attendances;

	/**
	 * 
	 * @return
	 */
    public Church getChurch() {
        return church;
    }

    public void setChurch(Church church) {
        this.church = church;
        //worship = this.church.getNextWorship();
    }

    
    /**
     * Set Style of Church Layout
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

        name.setTypeface(roboto_t);
        address.setTypeface(roboto_ti);
        TextView tv = new TextView(getApplicationContext());
        tv.setText("No activity");
        TextView wv = new TextView(getApplicationContext());
        wv.setText("No worship");
		worshipListView.setEmptyView(wv);
        pullToRefreshView.setEmptyView(tv);
    }

    private BroadcastReceiver updateWorshipReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
        	setContent();
        }
    };

    protected void onResume() {
    	super.onResume();
    	mActiveWorshipAdapter.notifyDataSetChanged();
    }
    /**
     * Fetch Information to the activity
     */
    private void fetchInformation() {
        Helper.getTodaysActiveWorship(church, todaysWorships, worshipWeeks);
    }

    /**
     * Set Content of the activity layout
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB) 
    public void setContent() {
    	name.setText(church.getName());
        address.setText(church.getAddress());
        todaysWorships = new ArrayList<Worship>();
        mActiveWorshipAdapter = new ActiveWorshipAdapter(getBaseContext(), this, worshipWeeks);
        worshipListView.setAdapter(mActiveWorshipAdapter);
        worshipListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				WorshipWeek wk = worshipWeeks.get(arg2);
				if(!wk.isAttended()){
					CHDialog.AttendDialogFragment addDialog = new CHDialog.AttendDialogFragment();
	                addDialog.setWorship(wk);
	                addDialog.show(getFragmentManager(), "ATTEND WORSHIP DIALOG");
				} else {
					Toast.makeText(getApplicationContext(), "Attended", Toast.LENGTH_SHORT).show();
				}
			}
		});
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, content);
        ListView actualListView = this.pullToRefreshView.getRefreshableView();
        actualListView.setAdapter(mAdapter);
        registerForContextMenu(actualListView);
        
        
        
    	final ProgressDialog pd = ProgressDialog.show(this, "Working..", "Get church information", true);

        final Handler handler = new Handler() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void handleMessage(Message msg) {
            	mAdapter.notifyDataSetChanged();
            	mActiveWorshipAdapter.notifyDataSetChanged();
                pd.dismiss();
            }
        };
        new Thread() {
            public void run() {
                try {
                	fetchInformation();
                    fetchAttendanceFromCache();
                    if (attendances != null) {
                        String[] str = new String[attendances.size()];
                        for (int i = 0; i < str.length; i++) {
                            Attendance a = attendances.get(i);
                            str[i] = a.getUser().getUserName() + " attended at " + a.getDate().toLocaleString() + " \"" + a.getMessage() + "\"";
                        }
                        content.addAll(Arrays.asList(str));
                    } else {
                        content.addAll(Arrays.asList(mcontent));
                    }
                    
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    Log.e("threadmessage", e.getMessage());
                }
            }
        }.start();

    }
    
    
    /**
     * Fetch attendance info from cache
     */
    private void fetchAttendanceFromCache() {
        attendances = Helper.getAttendance(church, true, 10, this);
    }

    /**
     * Set action to the activity layout
     */
    private void setAction() {
        mapButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (church == null)
                    return;
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("geo:0,0?q=" + church.getLat() + "," + church.getLon() + " (" + church.getName() + ")"));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.church_activity);
        Intent intent = getIntent();
        if (intent != null) {
            setChurch((Church) intent.getSerializableExtra(MainActivity.CHURCH_EXTRA));
        }
        pullToRefreshView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                // Do work to refresh the list here.
                new GetDataActivityTask().execute();
            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(updateWorshipReceiver,
                new IntentFilter(Util.UPDATE_WORSHIP_EVENT));
        setStyle();
        setAction();
        setContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.church_activity, menu);
//        User u = UserHelper.currentUser;
//        if (u != null) {
//            Intent i = getIntent();
//            Church ch = null;
//            String save = "Unsave";
//            if (i != null) {
//                ch = ((Church) i.getSerializableExtra(MainActivity.CHURCH_EXTRA));
//            }
//            Drawable icon = getResources().getDrawable(R.drawable.saved);
//            if (!u.savedChurchExist(ch)) {
//                icon = getResources().getDrawable(R.drawable.unsaved);
//                save = "Save";
//            }
//            menu.getItem(0).setIcon(icon);
//            menu.getItem(0).setTitle(save);
//        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_worship_item:
                CHDialog.AddWorshipDialogFragment addDialog = new CHDialog.AddWorshipDialogFragment(null);
                addDialog.setChurch(this.getChurch());
                addDialog.show(getFragmentManager(), "ADD WORSHIP DIALOG");
                break;
            case R.id.view_worships:
                Intent intent = new Intent(getBaseContext(), BrowseWorshipActivity.class);
                intent.putExtra("church", church);
                startActivity(intent);
                break;
//            case R.id.save:
//                User u = UserHelper.currentUser;
//                if (u != null) {
//                    if (!u.savedChurchExist(this.getChurch())) {
//                        u.addSavedChurch(getChurch());
//                    } else {
//                        u.removeSavedChurch(getChurch());
//                    }
//                }
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Fetch attendance 
     * @author Emerson Chan Simbolon
     */
    private class GetDataActivityTask extends AsyncTask<Void, Void, String[]> {
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
            ArrayList<Attendance> arrs = Helper.getAttendance(church, false, 10, ChurchActivity.this);
            String[] str = new String[arrs.size()];
            for (int i = 0; i < str.length; i++) {
                Attendance a = arrs.get(i);
                str[i] = a.getUser().getUserName() + " attended at " + a.getDate().toLocaleString() + " \"" + a.getMessage() + "\"";
            }
            return str;
        }
    }
    
    protected void onDestroy(){
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(updateWorshipReceiver);
    	super.onDestroy();
    }
}
