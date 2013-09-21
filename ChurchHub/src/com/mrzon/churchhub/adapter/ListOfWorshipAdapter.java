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

public class ListOfWorshipAdapter extends BaseAdapter {
	private LayoutInflater mInflater = null;
	private List<Worship> worshipList = new ArrayList<Worship>();
	
	private Activity mActivity;
	private final class ViewHolder {
		TextView nameTextView;
		TextView dayTextView;
	}

	private ViewHolder mHolder = null;
	private Context mContext;

	public ListOfWorshipAdapter(Context context, Activity activity, List<Worship> worships) {
		mContext = context;
		mActivity = activity;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		worshipList = worships;
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
			convertView = mInflater.inflate(R.layout.list_of_worship_cell, null);           
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder)convertView.getTag(); 
		}

		mHolder.nameTextView = (TextView)convertView.findViewById(R.id.name);
		mHolder.dayTextView = (TextView)convertView.findViewById(R.id.time);
		final Worship w = worshipList.get(position);
		mHolder.nameTextView.setText(w.getName());
		mHolder.dayTextView.setText(w.getDayString()+", "+w.getStartString()+"-"+w.getEndString());
		return convertView;
	}
}
