package com.mrzon.churchhub.adapter;

import java.util.ArrayList;
import java.util.List;

import com.mrzon.churchhub.CHDialog;
import com.mrzon.churchhub.R;
import com.mrzon.churchhub.CHDialog.AttendDialogFragment;
import com.mrzon.churchhub.R.id;
import com.mrzon.churchhub.R.layout;
import com.mrzon.churchhub.model.Worship;
import com.mrzon.churchhub.model.WorshipWeek;
import com.mrzon.churchhub.util.Util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ListOfDateWorshipAdapter extends BaseAdapter {
	private LayoutInflater mInflater = null;
	private List<Worship> worshipList;
	private List<Integer> weekList;
	private List<WorshipWeek> worshipWeeks;
	
	private Activity mActivity;
	private final class ViewHolder {
		TextView dateTextView;
	}

	private ViewHolder mHolder = null;
	private Context mContext;

	public ListOfDateWorshipAdapter(Context context, Activity activity, List<Worship> worships, List<Integer> weekList2, List<WorshipWeek> worshipWeeks) {
		mContext = context;
		mActivity = activity;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		worshipList = worships;
		weekList = weekList2;
		this.worshipWeeks = worshipWeeks;
	}

	@Override
	public int getCount() {
		return worshipList.size()*weekList.size();
	}

	@Override
	public Object getItem(int position) {
		return worshipWeeks.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			mHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.worship_date_cell, null);           
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder)convertView.getTag(); 
		}

		mHolder.dateTextView = (TextView)convertView.findViewById(R.id.date_label);
		final Worship w = worshipList.get(position/weekList.size());
		int week = weekList.get(position % weekList.size());
		WorshipWeek ww = new WorshipWeek();
		ww.setWorship(w);
		ww.setYear(Util.getCurrentYear());
		ww.setWorshipWeek(week);
		String date = Util.toDDMMMString(ww.getWorshipDate());
		mHolder.dateTextView.setText(date);
		if(position>=worshipWeeks.size()) {
			worshipWeeks.add(ww);
		} else {
			worshipWeeks.set(position, ww);			
		}
		return convertView;
	}
}
