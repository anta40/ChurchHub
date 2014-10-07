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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mrzon.churchhub.adapter.ListOfDateWorshipAdapter;
import com.mrzon.churchhub.adapter.ListOfWeekAdapter;
import com.mrzon.churchhub.adapter.ListOfWorshipAdapter;
import com.mrzon.churchhub.model.Church;
import com.mrzon.churchhub.model.Helper;
import com.mrzon.churchhub.model.Worship;
import com.mrzon.churchhub.model.WorshipWeek;
import com.mrzon.churchhub.util.Util;

import java.util.ArrayList;
import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
/**
 * Browse Worship Activity, viewed when:
 * 1. Want to add new worship
 * 2. Want to view worship info
 * 3. Want to set worship week info
 * 
 * @author Emerson Chan Simbolon
 */
public class BrowseWorshipActivity extends RoboActivity {

    @InjectView(R.id.pull_refresh_list)
    private PullToRefreshListView pullToRefreshView;
    @InjectView(R.id.week_of_year)
    private GridView weekOfYearList;
    @InjectView(R.id.date_of_worship)
    private GridView dateOfWorship;
    @InjectView(R.id.add_worship_button)
    private Button addWorshipButton;


    private List<Worship> worships = null;
    private Church church;
    private List<Integer> weekList;
    private List<WorshipWeek> worshipWeeks;
    private int year;
    private ListOfWorshipAdapter mAdapter;
    private ListOfWeekAdapter wAdapter;
    private ListOfDateWorshipAdapter lAdapter;
    private String[] mcontent = new String[]{"No church found, please pull to refresh"};
    private ArrayList<String> content = new ArrayList<String>();

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
        if (intent != null) {
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
        worships = Helper.getWorships(church, -1l, true, BrowseWorshipActivity.this);
        if (this.worships == null) {
            worships = new ArrayList<Worship>();
        }
        year = Util.getCurrentYear();
        int week = Util.getCurrentWeekOfTheYear();
        weekList = new ArrayList<Integer>();
        worshipWeeks = new ArrayList<WorshipWeek>();
        for (int i = 0; i < 3; i++) {
            weekList.add(week + i);
        }
        mAdapter = new ListOfWorshipAdapter(getBaseContext(), this, worships);
        wAdapter = new ListOfWeekAdapter(getBaseContext(), this, weekList);
        lAdapter = new ListOfDateWorshipAdapter(this, this, worships, weekList, worshipWeeks);
        ListView actualListView = this.pullToRefreshView.getRefreshableView();
        weekOfYearList.setAdapter(wAdapter);
        dateOfWorship.setAdapter(lAdapter);
        actualListView.setAdapter(mAdapter);
        TextView tv = new TextView(getApplicationContext());
        tv.setText("No worship");
        pullToRefreshView.setEmptyView(tv);
        registerForContextMenu(actualListView);
    }

    private void setAction() {
        // TODO Auto-generated method stub
        pullToRefreshView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Worship d = worships.get(arg2 - 1);
                String str = d.getName();
                Toast.makeText(getBaseContext(), "Unimplemented yet", Toast.LENGTH_SHORT).show();
                /*Intent intent = new Intent(getBaseContext(), ChurchActivity.class);
	            //intent.putExtra("church", d);
	            startActivity(intent);*/
            }
        });

        dateOfWorship.setLongClickable(true);
        dateOfWorship.setOnItemLongClickListener(new OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View arg1,
                                           int position, long arg3) {
                // TODO Auto-generated method stub
                final WorshipWeek ww = worshipWeeks.get(position);

                final ProgressDialog pd = ProgressDialog.show(BrowseWorshipActivity.this, "Working..", "Getting worship week information", true);

                final Handler handler = new Handler() {
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                    @Override
                    public void handleMessage(Message msg) {
                        pd.dismiss();
                        CHDialog.AddWorshipWeekInfoDialogFragment addDialog = new CHDialog.AddWorshipWeekInfoDialogFragment();
                        addDialog.setWorshipWeek(ww);
                        addDialog.show(getFragmentManager(), "ADD WORSHIP WEEK INFO DIALOG");
                    }
                };
                new Thread() {
                    public void run() {
                        try {
                            if (ww.getId() == null) {
                                ww.fetch();
                            }
                            handler.sendEmptyMessage(0);
                        } catch (Exception e) {
                            Log.e("threadmessage", e.getMessage());
                        }
                    }
                }.start();
                return true;
            }
        });

        dateOfWorship.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            }
        });

        addWorshipButton.setOnClickListener(new OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CHDialog.AddWorshipDialogFragment addDialog = new CHDialog.AddWorshipDialogFragment();
                addDialog.setChurch(church);
                addDialog.show(getFragmentManager(), "ADD WORSHIP DIALOG");
            }
        });
        ListView actualListView = this.pullToRefreshView.getRefreshableView();
        actualListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub

            }
        });
        actualListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                CHDialog.AddWorshipDialogFragment addDialog = new CHDialog.AddWorshipDialogFragment();
                addDialog.setChurch(church);
                addDialog.setWorship(worships.get(arg2 - 1));
                addDialog.show(getFragmentManager(), "ADD WORSHIP DIALOG");
                return true;
            }
        });
    }

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

    private class GetDataTask extends AsyncTask<Void, Void, List<Worship>> {
        @Override
        protected void onPostExecute(List<Worship> list) {
            worships.clear();
            worships.addAll(list);
            church.setWorships(worships);
            mAdapter.notifyDataSetChanged();
            lAdapter.notifyDataSetChanged();
            pullToRefreshView.onRefreshComplete();
            super.onPostExecute(list);
        }

        @Override
        protected List<Worship> doInBackground(Void... arg0) {
            SharedPreferences pref = getPreferences(MODE_MULTI_PROCESS);
            long updateTime = System.currentTimeMillis();
            long l = pref.getLong(Worship.latestUpdate + "_" + church.getId(), -1l);
            pref.edit().putLong(Worship.latestUpdate + "_" + church.getId(), updateTime).commit();
            List<Worship> list = Helper.getWorships(church, l, false, BrowseWorshipActivity.this);

            return list;
        }
    }
}

