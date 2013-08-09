package com.mrzon.churchhub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.mrzon.churchhub.model.Church;
import com.mrzon.churchhub.model.Denomination;
import com.mrzon.churchhub.model.Helper;
import com.mrzon.churchhub.model.Region;
import com.mrzon.churchhub.model.Worship;

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

public class BrowseWorshipActivity extends RoboActivity {

	@InjectView(R.id.pull_refresh_list)		 	private PullToRefreshListView pullToRefreshView;
	
	private List<Worship> worships = null;
	private Church church;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse_worship);
		pullToRefreshView.setOnRefreshListener(new OnRefreshListener<ListView>() {
		    @Override
		    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		        // Do work to refresh the list here.
		        new GetDataTask().execute();
		    }
		});

		setStyle();
		setAction();
		
		Intent intent = getIntent();
		if(intent!=null) {
			setChurch((Church) intent.getSerializableExtra("church"));
		}
		setContent();
	}

	private void setChurch(Church church2) {
		// TODO Auto-generated method stub
		this.church = church2;
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
		worships = Helper.getWorships(church,-1l,true,BrowseWorshipActivity.this);
		if(this.worships==null) {
			content.addAll(Arrays.asList(mcontent));
		} else {
			for(Worship d : worships) {
				content.add(d.getName()+" - "+d.getDayString()+", "+d.getStartString());
			}
		}
		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, content);
		ListView actualListView = this.pullToRefreshView.getRefreshableView();
		actualListView.setAdapter(mAdapter);
		registerForContextMenu(actualListView);
	}
	private void setAction() {
		// TODO Auto-generated method stub
		pullToRefreshView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Worship d = worships.get(arg2-1);
				String str = ((TextView) arg1).getText().toString();
	            Toast.makeText(getBaseContext(),"Unimplemented yet", Toast.LENGTH_SHORT).show();
	            /*Intent intent = new Intent(getBaseContext(), ChurchActivity.class);
	            //intent.putExtra("church", d);
	            startActivity(intent);*/
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
			
			SharedPreferences pref = getPreferences(MODE_MULTI_PROCESS);
			long updateTime = System.currentTimeMillis();
			long l = pref.getLong(Church.latestUpdate, -1l);
			pref.edit().putLong(Church.latestUpdate, updateTime).commit();
			List<Worship> list = Helper.getWorships(church, l, false,BrowseWorshipActivity.this);
			worships=list;
			String[] str = new String[worships.size()];
			for(int i = 0; i < str.length; i++) {
				Worship d = worships.get(i);
				str[i] = d.getName()+" - "+d.getDayString()+", "+d.getStartString();
			}
			return str;
		}
	}
	private ArrayAdapter<String> mAdapter;
	private String[] mcontent = new String[]{"No church found, please pull to refresh"};
	private ArrayList<String> content= new ArrayList<String>();
			
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_browse_worship, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}

