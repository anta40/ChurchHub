package com.mrzon.churchhub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.mrzon.churchhub.model.Denomination;
import com.mrzon.churchhub.model.Helper;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BrowseDenominationActivity extends RoboActivity {

	@InjectView(R.id.pull_refresh_list)		 	private PullToRefreshListView pullToRefreshView;

	private List<Denomination> denominations = null;

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
			content.add("Unspecified");
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
		List<Denomination> list = Helper.getDenominations(l, false, BrowseDenominationActivity.this);
		denominations = list;

		pref.edit().putLong(Denomination.latestUpdate, updateTime).commit();
		String[] str = new String[denominations.size()+1];

		for(int i = 0; i < str.length-1; i++) {
			Denomination d = denominations.get(i);
			str[i] = d.getName();
		}
		str[str.length-1] = "Unspecified";
		return str;
	}

	private void getDenominationFromCache() {
		SharedPreferences pref = getPreferences(MODE_MULTI_PROCESS);
		long l = pref.getLong(Denomination.latestUpdate, -1l);
		List<Denomination> list = Helper.getDenominations(l, true, BrowseDenominationActivity.this);
		denominations = list;
	}
	

	private void setAction() {
		// TODO Auto-generated method stub
		pullToRefreshView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Denomination d = null;

				if(arg2 == denominations.size()+1) {
					
				} else {
					d = denominations.get(arg2-1);
				}
				String str = ((TextView) arg1).getText().toString();
				Toast.makeText(getBaseContext(),"Opening "+str, Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getBaseContext(),BrowseChurchActivity.class);
				intent.putExtra("denomination", d);
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
