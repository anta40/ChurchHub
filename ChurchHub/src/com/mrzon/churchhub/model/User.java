package com.mrzon.churchhub.model;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

public class User {
	private double lon;
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}
	public Church getChurch() {
		return church;
	}
	public void setChurch(Church church) {
		this.church = church;
	}
	public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	public String getlName() {
		return lName;
	}
	public void setlName(String lName) {
		this.lName = lName;
	}
	public String getBod() {
		return bod;
	}
	public void setBod(String bod) {
		this.bod = bod;
	}
	private String username;
	private double lat;
	private Region region;
	private Church church;
	private String fName;
	private String lName;
	private String bod;
	public void setUserName(String username) {
		this.username = username;
	}
	public String getUserName() {
		// TODO Auto-generated method stub
		return username;
	}
	
	public void addSavedChurch(Church ch) {
		ParseRelation<ParseObject> q = ParseUser.getCurrentUser().getRelation("savedchurch");
		q.add(ch.getPObject());
	}
	
	public void removeSavedChurch(Church ch) {
		ParseRelation<ParseObject> q = ParseUser.getCurrentUser().getRelation("savedchurch");
		q.remove(ch.getPObject());
	}
	
	public boolean savedChurchExist(Church ch) {
		ParseRelation<ParseObject> pr = ParseUser.getCurrentUser().getRelation("savedchurch");
		ParseQuery<ParseObject> pq = pr.getQuery();
		pq.whereEqualTo("church", ch.getPObject());
		int i=0;
		try {
			i = pq.count();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i!=0;
	}
}
