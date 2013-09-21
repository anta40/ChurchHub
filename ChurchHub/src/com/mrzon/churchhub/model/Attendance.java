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
		return date;
	}

	public User getUser() {
		return user;
	}

	private Date date;
	private String message;
	private User user;
	private Church church;
	public Church getChurch() {
		return church;
	}

	public void setChurch(Church church) {
		this.church = church;
	}

	public void setWorshipWeek(WorshipWeek w) {
		// TODO Auto-generated method stub
		worship = w;
	}

	public void setDate(Date createdAt) {
		// TODO Auto-generated method stub
		date = createdAt;
	}

	public void setUser(User user) {
		// TODO Auto-generated method stub
		this.user = user;
	}
	public void setMessage(String m) {
		message = m;
	}
	public String getMessage() {
		// TODO Auto-generated method stub
		return message;
	}

}
