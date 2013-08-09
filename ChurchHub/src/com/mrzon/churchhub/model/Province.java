package com.mrzon.churchhub.model;

import java.io.Serializable;

import org.droidpersistence.annotation.Column;

import com.parse.ParseObject;

public class Province implements Serializable{
	/**
	 * 
	 */
	@Column(name="uid")
	private static final long serialVersionUID = 7965725728692835240L;
	@Column(name="latest")
	public static final String latestUpdate = "PROVINCE_LATEST_UPDATE";
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	private Country country;
	private String name;
	private String id;
	private ParseObject po;
	public ParseObject getPObject() {
		// TODO Auto-generated method stub
		return po;
	}
}
