package com.mrzon.churchhub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.parse.ParseUser;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChurchActivity extends RoboActivity  {
	@InjectView(R.id.church_name)             	private TextView name; 
	@InjectView(R.id.church_address)          	private TextView address; 
	@InjectView(R.id.map_button)             	private ImageButton mapButton; 
	@InjectView(R.id.pull_refresh_list)		 	private PullToRefreshListView pullToRefreshView;
	@InjectView(R.id.listView1)		 			private ListView worshipListView;
	private ArrayAdapter<String> mAdapter;
	private String[] mcontent = new String[]{"No attendee"};
	private ArrayList<String> content= new ArrayList<String>();
	private ArrayList<WorshipWeek> worshipWeeks= new ArrayList<WorshipWeek>();
	private List<Worship> todaysWorships;
	private Church church;
	private ArrayList<Attendance> attendances;
	public Church getChurch() {
		return church;
	}

	public void setChurch(Church church) {
		this.church = church;
		//worship = this.church.getNextWorship();
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

		name.setTypeface(roboto_t);
		address.setTypeface(roboto_ti);
	}

	private void fetchInformation() {
//		Worship w1 = new Worship();
//		w1.setChurch(church);
//		w1.setName("Kebaktian Umum 1");
//		w1.setStart(9.0);
//		w1.setEnd(11.0);
//		w1.setDay(0);
//		Worship w2 = new Worship();
//		w2.setChurch(church);
//		w2.setName("Sekolah minggu");
//		w2.setStart(9.0);
//		w2.setEnd(11.0);
//		w2.setDay(0);
//		WorshipWeek ww1 = new WorshipWeek();
//		ww1.setWorship(w1);
//		ww1.setId("j3hmA9ScEA");
//		ww1.setAttended(false);
//		ww1.setSpeaker("Pdt. M. Hariandja");
//		WorshipWeek ww2 = new WorshipWeek();
//		ww2.setWorship(w2);
//		ww2.setId("qsKNKhijuR");
//		ww2.setAttended(false);
//		ww2.setSpeaker("Kak Amel");
//		worshipWeeks.add(ww1);
//		worshipWeeks.add(ww2);
		Helper.getTodaysActiveWorship(church, todaysWorships, worshipWeeks);
	}

	public void setContent() {
		name.setText(church.getName());
		address.setText(church.getAddress());
		worshipListView.setAdapter(new ActiveWorshipAdapter(getBaseContext(), this, worshipWeeks));
		todaysWorships = new ArrayList<Worship>();
		fetchInformation();
		fetchAttendanceFromCache();
		if(attendances!=null) {
			String[] str = new String[attendances.size()];
			for(int i = 0; i < str.length; i++) {
				Attendance a = attendances.get(i);
				str[i] = a.getUser().getUserName() +" attended at "+a.getDate().toLocaleString()+" \""+a.getMessage()+"\"";
			}
			content.addAll(Arrays.asList(str));
		} else {
			content.addAll(Arrays.asList(mcontent));
		}
		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, content);
		ListView actualListView = this.pullToRefreshView.getRefreshableView();
		actualListView.setAdapter(mAdapter);
		registerForContextMenu(actualListView);


	}
	private void fetchAttendanceFromCache() {
		attendances = Helper.getAttendance(church, true, 10, this);
	}
	private void setAction() {
		mapButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(church==null)
					return;
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
						Uri.parse("geo:0,0?q="+church.getLat()+","+church.getLon()+" (" + church.getName() + ")"));
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
			ArrayList<Attendance> arrs = Helper.getAttendance(church, false, 10, ChurchActivity.this);
			String[] str = new String[arrs.size()];
			for(int i = 0; i < str.length; i++) {
				Attendance a = arrs.get(i);
				str[i] = a.getUser().getUserName() +" attended at "+a.getDate().toLocaleString()+" \""+a.getMessage()+"\"";
			}
			return str;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.church_activity);
		Intent intent = getIntent();
		if(intent!=null) {
			setChurch((Church) intent.getSerializableExtra(MainActivity.CHURCH_EXTRA));
		}
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.church_activity, menu);
		User u = UserHelper.currentUser;
		if(u != null) {
			Intent i = getIntent();
			Church ch = null;
			String save = "Unsave";
			if(i!=null) {
				ch = ((Church) i.getSerializableExtra(MainActivity.CHURCH_EXTRA));
			}
			Drawable icon = getResources().getDrawable(R.drawable.saved);
			if(!u.savedChurchExist(ch)) {
				icon = getResources().getDrawable(R.drawable.unsaved);
				save = "Save";
			}
			menu.getItem(0).setIcon(icon);
			menu.getItem(0).setTitle(save);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.add_worship_item:
			CHDialog.AddWorshipDialogFragment addDialog = new CHDialog.AddWorshipDialogFragment();
			addDialog.setChurch(this.getChurch());
			addDialog.show(getFragmentManager(), "ADD WORSHIP DIALOG");
			break;
		case R.id.view_worships:
			Intent intent = new Intent(getBaseContext(), BrowseWorshipActivity.class);
			intent.putExtra("church", church);
			startActivity(intent);
			break;
		case R.id.save: 
			User u = UserHelper.currentUser;
			if(u!=null) {
				if(!u.savedChurchExist(this.getChurch())) {
					u.addSavedChurch(getChurch());
				} else {
					u.removeSavedChurch(getChurch());
				}
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
