package com.mrzon.churchhub.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.mrzon.churchhub.util.Util;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class WorshipWeek implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6971541929403658363L;
	public static final String latestUpdate = "WORSHIPWEEK_LATEST_UPDATE";
	public Worship getWorship() {
		return worship;
	}
	public void setWorship(Worship worship) {
		this.worship = worship;
	}
	public String getSpeaker() {
		return speaker;
	}
	public void setSpeaker(String speaker) {
		this.speaker = speaker;
	}
	public int getWorshipWeek() {
		return weekOfYear;
	}
	public void setWorshipWeek(int worshipWeek) {
		this.weekOfYear = worshipWeek;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isAttended() {
		return attended;
	}
	public void setAttended(boolean attended) {
		this.attended = attended;
	}
	public Date getWorshipDate() {
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.setTime(Util.getMinimumDateBasedOnWeekOfTheYear(weekOfYear));
		if(alternateDate == -1) {
			cal.add(Calendar.DAY_OF_MONTH, worship.getDay());			
		} else {
			cal.add(Calendar.DAY_OF_MONTH, alternateDate);
		}
		return cal.getTime();
	}
	public int getAlternateDate() {
		return alternateDate;
	}
	public void setAlternateDate(int alternateDate) {
		this.alternateDate = alternateDate;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}

	
	private int alternateDate = -1;

	private int year;
	private Worship worship;
	private String speaker, theme, nats;
	private int weekOfYear;
	private String id = null;
	private boolean attended;
	public void setNats(String nats) {
		// TODO Auto-generated method stub
		this.nats = nats;
	}
	public void setTheme(String theme) {
		// TODO Auto-generated method stub
		this.theme = theme;
	}
	public void submit() throws ParseException {
		
		ParseObject po = new ParseObject("WeeklyWorship");
		if(id != null) {
			po.setObjectId(id);
		}
		po.put("date", getWorshipDate());
		po.put("speaker", speaker);
		po.put("theme", theme);
		po.put("nats", nats);
		po.put("year", year);
		po.put("weekOfYear", weekOfYear);
		if(worship!=null) {
			po.put("worship", worship.getPObject());
		}
		po.save();
		if(id == null) {
			this.setId(po.getObjectId());
		}
	}
	public String getTimeString() {
		// TODO Auto-generated method stub
		return worship.getDayString()+", "+Util.toDDMMYYYYString(getWorshipDate());
	}
	public String getTheme() {
		// TODO Auto-generated method stub
		return theme;
	}
	public String getNats() {
		// TODO Auto-generated method stub
		return nats;
	}
	
	public void fetch() {
		ParseQuery<ParseObject> pq = new ParseQuery<ParseObject>("WeeklyWorship");
		pq.whereEqualTo("worship", worship.getPObject());
		pq.whereEqualTo("weekOfYear", weekOfYear);
		pq.whereEqualTo("year", year);
		try {
			ParseObject po = pq.getFirst();
			if(po!=null) {
				id = po.getObjectId();
				speaker = po.getString("speaker");
				nats = po.getString("nats");
				theme = po.getString("theme");
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
