package com.mrzon.churchhub.pulltorefresh;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class PullToRefreshListView extends PullToRefreshBase<ListView> {

    public PullToRefreshListView(Context context) {
        super(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected final ListView createAdapterView(Context context,
            AttributeSet attrs) {
        ListView lv = new ListView(context, attrs);
        // Set it to this so it can be used in ListActivity/ListFragment

        lv.setId(android.R.id.list);
        return lv;
    }

	@Override
	public com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation getPullToRefreshScrollDirection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ListView createRefreshableView(Context context, AttributeSet attrs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isReadyForPullEnd() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isReadyForPullStart() {
		// TODO Auto-generated method stub
		return false;
	}

}