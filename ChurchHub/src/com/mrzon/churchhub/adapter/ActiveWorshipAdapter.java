package com.mrzon.churchhub.adapter;

import java.util.ArrayList;

import com.mrzon.churchhub.CHDialog;
import com.mrzon.churchhub.R;
import com.mrzon.churchhub.CHDialog.AttendDialogFragment;
import com.mrzon.churchhub.R.id;
import com.mrzon.churchhub.R.layout;
import com.mrzon.churchhub.model.Worship;
import com.mrzon.churchhub.model.WorshipWeek;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ActiveWorshipAdapter extends BaseAdapter {
	private LayoutInflater mInflater = null;
	private ArrayList<WorshipWeek> worshipList = new ArrayList<WorshipWeek>();
	private Activity mActivity;
	private final class ViewHolder {
		TextView nameTextView;
		TextView timeTextView;
		TextView speakerTextView;
		Button attendButton;
	}

	private ViewHolder mHolder = null;
	private Context mContext;

	public ActiveWorshipAdapter(Context context, Activity activity, ArrayList<WorshipWeek> worshipWeeks) {
		mContext = context;
		mActivity = activity;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		worshipList = worshipWeeks;
	}

	@Override
	public int getCount() {
		return worshipList.size();
	}

	@Override
	public Object getItem(int position) {
		return worshipList.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			mHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_of_active_worship_cell, null);           
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder)convertView.getTag(); 
		}

		mHolder.nameTextView = (TextView)convertView.findViewById(R.id.worship_label);
		mHolder.attendButton = (Button)convertView.findViewById(R.id.worship_attend);
		mHolder.speakerTextView = (TextView)convertView.findViewById(R.id.worship_speaker);
		mHolder.timeTextView = (TextView)convertView.findViewById(R.id.worship_time);
		
		final WorshipWeek w = worshipList.get(position);
		mHolder.nameTextView.setText(w.getWorship().getName());
		mHolder.speakerTextView.setText(w.getSpeaker());
		mHolder.timeTextView.setText(w.getWorship().getStartString());
		mHolder.attendButton.setText(w.isAttended()?"Unattend":"Attend");
		mHolder.attendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CHDialog.AttendDialogFragment attendDialog = new CHDialog.AttendDialogFragment();
				attendDialog.setWorship(w);
				attendDialog.show(mActivity.getFragmentManager(), "Post Message");
			}
		});
		if(ParseUser.getCurrentUser() == null) {
			mHolder.attendButton.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}
}
