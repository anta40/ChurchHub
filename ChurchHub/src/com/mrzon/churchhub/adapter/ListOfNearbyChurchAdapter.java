package com.mrzon.churchhub.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mrzon.churchhub.R;
import com.mrzon.churchhub.R.id;
import com.mrzon.churchhub.R.layout;
import com.mrzon.churchhub.model.*;
import com.mrzon.churchhub.util.Util;
import com.parse.ParseGeoPoint;

import java.util.List;

import junit.framework.Assert;

public class ListOfNearbyChurchAdapter extends BaseAdapter {
    private LayoutInflater mInflater = null;
    private ParseGeoPoint point;
    private List<Church> churches;
    private ViewHolder mHolder = null;
    private Context mContext;
    
    /**
     * Initiate the adapter of nearby church 
     * @param context context of the current activity
     * @param p current parse geo point
     * @param c list of church that want to be displayed
     */
    public ListOfNearbyChurchAdapter(Context context, ParseGeoPoint p, List<Church> c) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        point = p;
        churches = c;
    }
    
    public void setCurrentPoint(ParseGeoPoint p) {
    	Assert.assertNotNull(p);
    	point = p;
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
        if (convertView == null) {
            mHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_of_nearest_church_cell, null);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.nameTextView = (TextView) convertView.findViewById(R.id.nearby_church_name);
        mHolder.addressTextView = (TextView) convertView.findViewById(R.id.nearby_church_address);
        mHolder.distanceTextView = (TextView) convertView.findViewById(R.id.nearby_church_distance);
        final Church c = churches.get(position);
        double distance = -1.0;
        if(point != null){
        	 distance = Util.distFrom(c.getLat(), c.getLon(), point.getLatitude(), point.getLongitude());
        }
        String distanceText = distance == -1 ? "Not measured":Util.getPreetyDistance(distance);
        mHolder.nameTextView.setText(c.getName());
        mHolder.addressTextView.setText(c.getAddress());
        mHolder.distanceTextView.setText(distanceText);
        return convertView;
    }

    private final class ViewHolder {
        TextView nameTextView;
        TextView addressTextView;
        TextView distanceTextView;
    }
}
