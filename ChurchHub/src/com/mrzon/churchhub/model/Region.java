package com.mrzon.churchhub.model;

import java.io.Serializable;

import org.droidpersistence.annotation.Column;

import com.parse.ParseException;
import com.parse.ParseObject;

public class Region implements Serializable{
	
	/**
	 * 
	 */
	@Column(name="uid")
	private static final long serialVersionUID = 2584799363827999595L;
	@Column(name="latest")
	public static final String latestUpdate = "REGION_LATEST_UPDATE";
	public Province getProvince() {
		return province;
	}
	public void setProvince(Province province) {
		this.province = province;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private Province province;
	private String id;
	private String name;
	private ParseObject po;
	public ParseObject getPObject() {
		ParseObject po = ParseObject.createWithoutData("Region", this.getId());
		return po;
	}
	public void setPObject(ParseObject po) {
		this.po = po;
	}
	
	public void submit() throws ParseException {
		po = new ParseObject("Region");
		po.put("name", name);
		po.put("province", province.getPObject());
	}
}
