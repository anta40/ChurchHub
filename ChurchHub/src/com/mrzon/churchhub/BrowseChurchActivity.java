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

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
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

public class BrowseChurchActivity extends RoboActivity {

	@InjectView(R.id.pull_refresh_list)		 	private PullToRefreshListView pullToRefreshView;
	
	private List<Church> churches = null;
	private Denomination denomination;
	private Region region;
	
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
		
		Intent intent = getIntent();
		if(intent!=null) {
			setDenomination((Denomination) intent.getSerializableExtra("denomination"));
		}
		setContent();
	}

	private void setDenomination(Denomination denomination2) {
		// TODO Auto-generated method stub
		this.denomination = denomination2;
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
		churches = Helper.getChurches(region, denomination,-1l,true,this);
		if(this.churches==null) {
			content.addAll(Arrays.asList(mcontent));
		} else {
			for(Church d : churches) {
				content.add(d.getName());
			}
		}
		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, content);
		ListView actualListView = this.pullToRefreshView.getRefreshableView();
		actualListView.setAdapter(mAdapter);
		registerForContextMenu(actualListView);
		TextView tv = new TextView(getApplicationContext());
		tv.setText("No church in this denomination, add one");
		pullToRefreshView.setEmptyView(tv);
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
			
			SharedPreferences pref = getPreferences(MODE_MULTI_PROCESS);
			long updateTime = System.currentTimeMillis();
			long l = pref.getLong(Church.latestUpdate+"_"+denomination.getId(), -1l);
			pref.edit().putLong(Church.latestUpdate+"_"+denomination.getId(), updateTime).commit();
			List<Church> list = Helper.getChurches(region, denomination,l,false,BrowseChurchActivity.this);
			churches=list;
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
		getMenuInflater().inflate(R.menu.browse_church, menu);
		return true;
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.add_church:
	            CHDialog.AddChurchDialogFragment addDialog = new CHDialog.AddChurchDialogFragment();
	            addDialog.setDenomination(this.denomination);
	            addDialog.show(getFragmentManager(), "ADD ACTIVITY DIALOG");
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}

