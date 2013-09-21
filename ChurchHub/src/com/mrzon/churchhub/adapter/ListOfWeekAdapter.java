package com.mrzon.churchhub.adapter;

import java.util.ArrayList;
import java.util.Date;
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

public class ListOfWeekAdapter extends BaseAdapter {
	private LayoutInflater mInflater = null;
	private List<Integer> weekList = new ArrayList<Integer>();
	
	private Activity mActivity;
	private final class ViewHolder {
		TextView weekTextView;
		TextView rangeTextView;
	}

	private ViewHolder mHolder = null;
	private Context mContext;

	public ListOfWeekAdapter(Context context, Activity activity, List<Integer> weekList2) {
		mContext = context;
		mActivity = activity;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		weekList = weekList2;
	}

	@Override
	public int getCount() {
		return weekList.size();
	}

	@Override
	public Object getItem(int position) {
		return weekList.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			mHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.week_of_year_cell, null);           
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder)convertView.getTag(); 
		}

		mHolder.weekTextView = (TextView)convertView.findViewById(R.id.date);
		mHolder.rangeTextView = (TextView)convertView.findViewById(R.id.range);
		
		Date start = Util.getMinimumDateBasedOnWeekOfTheYear(weekList.get(position));
		Date end = Util.getMaximumDateBasedOnWeekOfTheYear(weekList.get(position));
		
		String range = Util.toDDMMMString(start)+" - "+Util.toDDMMMString(end);
		mHolder.weekTextView.setText(weekList.get(position)+"");
		mHolder.rangeTextView.setText(range);
		return convertView;
	}
}
