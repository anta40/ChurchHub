package com.mrzon.churchhub.adapter;

import java.util.ArrayList;
import java.util.List;

import com.mrzon.churchhub.CHDialog;
import com.mrzon.churchhub.R;
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
import com.parse.ParseGeoPoint;
import com.mrzon.churchhub.model.*;
public class ListOfNearbyChurchAdapter extends BaseAdapter {
	private LayoutInflater mInflater = null;
	private ParseGeoPoint point;
	private List<Church> churches;
	private final class ViewHolder {
		TextView nameTextView;
		TextView addressTextView;
		TextView distanceTextView;
	}

	private ViewHolder mHolder = null;
	private Context mContext;

	public ListOfNearbyChurchAdapter(Context context, ParseGeoPoint p, List<Church> c) {
		mContext = context;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		point = p;
		churches = c;
	}

	@Override
	public int getCount() {
		return churches.size();
	}

	@Override
	public Object getItem(int position) {
		return churches.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			mHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_of_nearest_church_cell, null);           
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder)convertView.getTag(); 
		}

		mHolder.nameTextView = (TextView)convertView.findViewById(R.id.nearby_church_name);
		mHolder.addressTextView = (TextView)convertView.findViewById(R.id.nearby_church_address);
		mHolder.distanceTextView = (TextView)convertView.findViewById(R.id.nearby_church_distance);
		final Church c = churches.get(position);
		double distance = Util.distFrom(c.getLat(), c.getLon(), point.getLatitude(), point.getLongitude());
		mHolder.nameTextView.setText(c.getName());
		mHolder.addressTextView.setText(c.getAddress());
		mHolder.distanceTextView.setText(Util.getPreetyDistance(distance));
		return convertView;
	}
}
