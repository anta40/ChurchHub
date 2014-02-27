package com.mrzon.churchhub.model;

import java.io.Serializable;
import java.util.Date;

public class Attendance implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4391226524512440880L;
	private WorshipWeek worship;
	public WorshipWeek getWorship() {
		return worship;
	}

	public Date getDate() {
		return mDate;
	}

	public User getUser() {
		return mUser;
	}

	public Church getChurch() {
		return mChurch;
	}

	public void setChurch(Church church) {
		this.mChurch = church;
	}

	public void setWorshipWeek(WorshipWeek w) {
		worship = w;
	}

	public void setDate(Date createdAt) {
		mDate = createdAt;
	}

	public void setUser(User user) {
		this.mUser = user;
	}
	public void setMessage(String m) {
		mMessage = m;
	}
	public String getMessage() {
		// TODO Auto-generated method stub
		return mMessage;
	}

	private Date mDate;
	private String mMessage;
	private User mUser;
	private Church mChurch;
}
