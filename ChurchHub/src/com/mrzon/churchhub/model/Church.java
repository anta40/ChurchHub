package com.mrzon.churchhub.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.droidpersistence.annotation.Column;
import org.droidpersistence.annotation.PrimaryKey;
import org.droidpersistence.annotation.Table;
import org.droidpersistence.annotation.ForeignKey;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;
import com.parse.ParseRelation;
@Table(name="CHURCH")
public class Church implements Serializable {

	private Long code;

	/**
	 * 
	 */
	public static Church createFromId(String id) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Church");
		Church church = null;
		try {
			ParseObject pob = query.get(id);
			church = Helper.createChurch(pob, null);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		return church;
	}
	public static final String latestUpdate = "CHURCH_LATEST_UPDATE";
	private static final long serialVersionUID = -39876803275824193L;
	private String name;
	private String foursquareId;
	private int distance;
	public String getFoursquareId() {
		return foursquareId;
	}
	public void setFoursquareId(String foursquareId) {
		this.foursquareId = foursquareId;
	}
	private ParseObject pob;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Denomination getDenomination() {
		return denomination;
	}
	public void setDenomination(Denomination denomination) {
		this.denomination = denomination;
	}
	public Date getEsDate() {
		return esDate;
	}
	public void setEsDate(Date esDate) {
		this.esDate = esDate;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setWorships(List<Worship> worships) {
		this.worships = worships;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String callNumber) {
		this.phoneNumber = callNumber;
	}
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
	private Denomination denomination;

	private Date esDate;

	private String address;
	private List<Worship> worships;

	private String website;

	private String phoneNumber;
	private double lon;
	private double lat;
	private String id;

	public ParseObject getPObject() {
		ParseObject pob = ParseObject.createWithoutData("Church", id);
		return pob;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	@SuppressWarnings("unused")
	public void submit() throws ParseException {
		// TODO Auto-generated method stub
		ParseObject po = new ParseObject("Church");
		if(id!=null) {
			po.get(id);
		}
		po.put("name", name);
		if(website!=null)
			po.put("website", website);
		if(address!=null)
			po.put("address", address);
		if(esDate!=null)
		po.put("established", esDate);
		po.put("location", new ParseGeoPoint(lat, lon));
		if(foursquareId!=null)
			po.put("foursquareid", foursquareId);
		Region region = null;
		if(region!=null) {
			po.put("region", region.getPObject());
		}
		if(denomination!=null) {
			po.put("denomination", denomination.getPObject());
		}
		po.save();
		this.setId(po.getObjectId());
	}

	public List<Worship> getWorships(){
		if(worships==null) {
			ArrayList<Worship> worships = new ArrayList<Worship>();
			ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Worship");
			try {
				List<ParseObject> pos = query.find();
				for(ParseObject po:pos) {
					worships.add(Helper.createWorship(po, this));
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return worships;
	}
	public void setDistance(int int1) {
		// TODO Auto-generated method stub
		distance = int1;
	}

	public int getDistance() {
		return distance;
	}
	public void submitIfNotExistedYet() throws ParseException {
		// TODO Auto-generated method stub
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Church");
		query.whereEqualTo("foursquareid", foursquareId);
		int count = query.count();
		if(0==count) {
			submit();
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Church) {
			Church c = (Church) o;
			return c.getId().equals(getId());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getId().hashCode();
	}
}
