package com.mrzon.churchhub.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.mrzon.churchhub.CHDialog;
import com.mrzon.churchhub.CHDialog.AttendDialogFragment;
import com.mrzon.churchhub.R;
import com.mrzon.churchhub.R.id;
import com.mrzon.churchhub.R.layout;
import com.mrzon.churchhub.model.WorshipWeek;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * ActiveWorshipAdapter
 * 
 * @author Emerson Chan Simbolon
 */
public class ActiveWorshipAdapter extends BaseAdapter {
	/**
     * LayoutInflater of the adapter
     */
	private LayoutInflater mInflater = null;
	
	/**
	 * List of worship week
	 */
    private ArrayList<WorshipWeek> worshipList = new ArrayList<WorshipWeek>();
    
    /**
     * Activity that held the adapter
     */
    private Activity mActivity;
    
    /**
     * Holder of the view
     */
    private ViewHolder mHolder = null;
    
    /**
     * Active context
     */
    private Context mContext;
    public ActiveWorshipAdapter(Context context, Activity activity, ArrayList<WorshipWeek> worshipWeeks) {
        mContext = context;
        mActivity = activity;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        worshipList = worshipWeeks;
    }

    /**
     * Return worship list count
     */
    @Override
    public int getCount() {
        return worshipList.size();
    }

    /**
     * Return object int the given position
     */
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
        if (convertView == null) {
            mHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_of_active_worship_cell, null);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        Typeface roboto_l = Typeface.createFromAsset(
                mActivity.getAssets(),
                "fonts/Roboto-Light.ttf");
        
        mHolder.nameTextView = (TextView) convertView.findViewById(R.id.worship_label);
        mHolder.speakerTextView = (TextView) convertView.findViewById(R.id.worship_speaker);
        mHolder.timeTextView = (TextView) convertView.findViewById(R.id.worship_time);
        mHolder.nameTextView.setTypeface(roboto_l);
        mHolder.timeTextView.setTypeface(roboto_l);
        final WorshipWeek w = worshipList.get(position);
        mHolder.nameTextView.setText(w.getWorship().getName());
        mHolder.speakerTextView.setText(w.getSpeaker());
        mHolder.timeTextView.setText(w.getWorship().getStartString());
        
//        mHolder.attendButton.setText(w.isAttended() ? "Unattend" : "Attend");
//        mHolder.attendButton.setOnClickListener(new View.OnClickListener() {
//            @TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override
//            public void onClick(View v) {
//                CHDialog.AttendDialogFragment attendDialog = new CHDialog.AttendDialogFragment();
//                attendDialog.setWorship(w);
//                attendDialog.show(mActivity.getFragmentManager(), "Post Message");
//            }
//        });
        if (ParseUser.getCurrentUser() == null) {
            mHolder.attendButton.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    private final class ViewHolder {
        TextView nameTextView;
        TextView timeTextView;
        TextView speakerTextView;
        Button attendButton;
    }
}
